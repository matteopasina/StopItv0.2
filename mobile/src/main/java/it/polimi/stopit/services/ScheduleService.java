package it.polimi.stopit.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.joda.time.MutableDateTime;
import org.joda.time.MutableInterval;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import it.polimi.stopit.NotificationID;
import it.polimi.stopit.R;
import it.polimi.stopit.Receivers.SmokeReceiver;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.AlternativeActivity;
import it.polimi.stopit.model.Challenge;
import it.polimi.stopit.model.User;

/**
 * Created by matteo on 13/12/15.
 */

public class ScheduleService extends Service {
    private NotificationManager mNM;
    private static List<MutableInterval> list;
    private static long nextCiga;
    private static MutableDateTime start;
    private static MutableDateTime end;
    private boolean beginOfDay = false;
    private GoogleApiClient mGoogleApiClient;
    CountDownTimer Count;
    int notificationID;
    int nID;
    Controller controller;
    private DatabaseHandler db;
    private SharedPreferences settings;

    /*
    * Receives the broadcast from the button smoke on the main screen, the restarts the
    * timer with time shifted
    * */
    private BroadcastReceiver uiUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Count.cancel();
            if (intent.getSerializableExtra("time") != null) {

                list = shiftIntervals((MutableDateTime) intent.getSerializableExtra("time"), list);

                nextCiga(list, start, end);
                saveSchedule(list);

            }

            Count = null;
            setCount(nextCiga);

        }
    };

    private BroadcastReceiver askLeaderboard = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            putLeaderboardInMap();

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        controller = new Controller(this);
        db = new DatabaseHandler(this);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        deleteFile("schedule");

        startService(new Intent(this, ListenerService.class));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        // Now you can use the Data Layer API
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

        //setta lo schedule per la prima esecuzione
        firstStart();

        nextCiga(list, start, end);
        setCount(nextCiga);

        controller.checkChallenges();
        controller.checkAccepted();
        controller.checkGiveUp();
    }


    public void setCount(long next) {
        Count = new CountDownTimer(next, 1000) {
            public void onTick(long millisUntilFinished) {

                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown", millisUntilFinished);

                sendBroadcast(i);

            }

            public void onFinish() {

                if (beginOfDay) {
                    deleteFile("schedule");
                    firstStart();
                    beginOfDay = false;
                }

                SharedPreferences userdata = PreferenceManager.getDefaultSharedPreferences(ScheduleService.this);
                if (new Random().nextInt(userdata.getInt("CPD", 0)) < userdata.getInt("CPD", 0) / 10) {

                    AlternativeActivity a = controller.chooseAlternative(calcPoints());
                    int p = calcPoints();

                    if (a == null) {

                        nID = sendNotification(p);
                        controller.cancelNotification(nID, 300000, p);

                    } else {

                        nID = controller.sendAlternativeNotification(a, p);
                        controller.cancelNotification(nID, 300000, p);

                    }

                } else {

                    try {

                        mNM.cancel(nID);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    nID = sendNotification(calcPoints());
                    controller.cancelNotification(nID, 300000, calcPoints());

                }

                nextCiga(list, start, end);

                this.cancel();
                Count = null;
                setCount(nextCiga);
            }
        }.start();
    }

    public void firstStart() {
        list = loadSchedule();
        if (list == null) {

            SharedPreferences userdata = PreferenceManager.getDefaultSharedPreferences(ScheduleService.this);

            start = new MutableDateTime();
            end = new MutableDateTime();

            start.setHourOfDay(9);
            start.setMinuteOfHour(0);
            start.setSecondOfMinute(0);
            end.setHourOfDay(23);
            end.setMinuteOfHour(0);
            end.setSecondOfMinute(0);

            list = splitDuration(start, end, userdata.getInt("CPD", 0));
            saveSchedule(list);
            putLeaderboardInMap();
            putAchievementsInMap();
            putChallengesInMap();
            putScheduleInMap(start.getMillis(), end.getMillis(), userdata.getInt("CPD", 0));
        }
    }

    public void nextCiga(List<MutableInterval> list, MutableDateTime start, MutableDateTime end) {

        MutableDateTime now = new MutableDateTime();

        if (end.isBeforeNow()) {

            beginOfDay = true;
            nextCiga = (start.getMillis() + 86400000) - now.getMillis();
            System.out.println("endbefore" + end);

        } else if (start.isAfterNow()) {
            beginOfDay = true;
            nextCiga = start.getMillis() - now.getMillis();
            System.out.println("startafter");

        } else {

            for (MutableInterval i : list) {
                if (i.contains(now)) {

                    nextCiga = i.getEndMillis() - now.getMillis();
                    System.out.println("normal");
                    break;

                }
            }
        }
    }

    public void putScheduleInMap(long start,long end, long CPD){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/schedule");
        putDataMapReq.getDataMap().putLong("start", start);
        putDataMapReq.getDataMap().putLong("end", end);
        putDataMapReq.getDataMap().putLong("CPD", CPD);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    public void putLeaderboardInMap() {

        ArrayList<User> mLeaderboard = db.getAllContacts();

        User me = new User(settings.getString("ID", null), settings.getString("name", null), settings.getString("surname", null), settings.getString("image", null), settings.getLong("points", 0), settings.getLong("dayPoints", 0), settings.getLong("weekPoints", 0), "", "");
        mLeaderboard.add(me);

        mLeaderboard = controller.addTestContacts(mLeaderboard);

        int i = 0;
        for(User contact: mLeaderboard) {

            //Bitmap img=controller.(contact.getProfilePic());
            //Asset asset=createAssetFromBitmap(img);

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/leaderboard/" + i);
            putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
           // putDataMapReq.getDataMap().putAsset("profileImage", asset);
            contact.putToDataMap(putDataMapReq.getDataMap());
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
            i++;
        }

    }

    public void putAchievementsInMap() {

        ArrayList<Achievement> mAchievements = (ArrayList<Achievement>) db.getAllAchievements();

        int i = 0;
        for(Achievement achievement: mAchievements) {

            /*Bitmap img = BitmapFactory.decodeResource(getResources(), achievement.getImage());
            Asset asset=createAssetFromBitmap(img);*/

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/achievements/" + i);
            putDataMapReq.getDataMap().putLong("timestamp",new MutableDateTime().getMillis());
            //putDataMapReq.getDataMap().putAsset("achievementImage", asset);
            achievement.putToDataMap(putDataMapReq.getDataMap());
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
            i++;
        }

    }

    public void putChallengesInMap() {

        ArrayList<Challenge> mChallenges = (ArrayList<Challenge>) db.getActiveChallenges();

        int i = 0;
        for(Challenge challenge: mChallenges) {

            //Bitmap img=controller.(contact.getProfilePic());
            //Asset asset=createAssetFromBitmap(img);

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/challenges/" + i);
            putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
            // putDataMapReq.getDataMap().putAsset("profileImage", asset);
            challenge.putToDataMap(putDataMapReq.getDataMap());
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
            i++;
        }

    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    public void saveSchedule(List<MutableInterval> list) {
        String filename = "schedule";

        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<MutableInterval> loadSchedule() {
        String filename = "schedule";
        List<MutableInterval> lista;

        try {
            FileInputStream fis = this.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lista = (List<MutableInterval>) ois.readObject();
            ois.close();
            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int calcPoints() {
        long points = 0;
        MutableDateTime now = new MutableDateTime();
        now.setSecondOfDay(now.getSecondOfDay() - 5);
        for (MutableInterval i : list) {
            if (i.contains(now)) {
                points = (i.getEndMillis() - i.getStartMillis()) / 60000;
                points += 0.5;
                break;
            }
        }
        return (int) points;
    }

    static List<MutableInterval> splitDuration(MutableDateTime start, MutableDateTime end, long chunkAmount) {

        long millis = start.getMillis();
        long endMillis = end.getMillis();

        if(chunkAmount==0) return null;
        long chunkSize = (endMillis - millis) / chunkAmount;

        List<MutableInterval> list = new ArrayList<>();

        for (int i = 0; i < chunkAmount; ++i) {
            list.add(new MutableInterval(millis, millis += chunkSize));
        }

        return list;
    }

    static List<MutableInterval> shiftIntervals(MutableDateTime time, List<MutableInterval> list) {
        long shift = 0;
        for (MutableInterval i : list) {
            if (i.contains(time)) {
                shift = time.getMillis() - i.getStartMillis();
                i.setInterval(time.getMillis(), i.getEndMillis() + shift);
                continue;
            }
            i.setInterval(i.getStartMillis() + shift, i.getEndMillis() + shift);
        }

        return list;
    }

    public int sendNotification(int points) {

        notificationID = NotificationID.getID();

        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.stopitsymbol);

        Intent smokeIntent = new Intent(this, SmokeReceiver.class);
        smokeIntent.putExtra("points", points);
        smokeIntent.putExtra("notificationID", notificationID);
        smokeIntent.putExtra("smoke", true);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, smokeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dontSmokeIntent = new Intent(this, SmokeReceiver.class);
        dontSmokeIntent.putExtra("points", points * 2);
        dontSmokeIntent.putExtra("notificationID", notificationID);
        PendingIntent piDS = PendingIntent.getBroadcast(this, 0, dontSmokeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setVibrate(new long[]{200, 500, 200, 500})
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.stopitsymbollollipop)
                        .setContentTitle("You can smoke")
                        .setContentText("You earned it")
                        .addAction(R.drawable.stopitsymbollollipop, "Don't smoke", piDS)
                        .addAction(R.drawable.stopitsymbollollipop, "Smoke", pi)
                        .setAutoCancel(true);
        // Sets an ID for the notification


        Intent resultIntent = new Intent(this, NavigationActivity.class);

        resultIntent.putExtra("points", points);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NavigationActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // Gets an instance of the NotificationManager service
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNM.notify(notificationID, mBuilder.build());
        return notificationID;
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result = mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);

        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {

            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    public static class LeaderComparator implements Comparator<User> {

        @Override
        public int compare(User contact1, User contact2) {

            return contact2.getPoints().compareTo(contact1.getPoints());

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        registerReceiver(uiUpdated, new IntentFilter("SMOKE_OUTOFTIME"));
        registerReceiver(askLeaderboard, new IntentFilter("ASK_LEADERBOARD"));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(uiUpdated);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
