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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.fasterxml.jackson.databind.ser.SerializerFactory;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    public static Bitmap img = null;

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

                try {
                    putScheduleInMap();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            Count = null;
            setCount(nextCiga);

        }
    };

    private BroadcastReceiver sendWear = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                Log.v("SEND: ", "Received ask");
                putScheduleInMap();
                putLeaderboardInMap();
                putAchievementsInMap();
                putChallengesInMap();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        controller = new Controller(this);
        db = new DatabaseHandler(this);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        list = loadSchedule();

        //setta lo schedule per la prima esecuzione
        try {
            if (list == null) {
                firstStart();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // deleteFile("schedule");
        start = new MutableDateTime();
        end = new MutableDateTime();
        Log.v("SCHEDULE", "start:" + list.get(0).getStartMillis() +
                " end:" + list.get(list.size() - 1).getEndMillis());
        start.setMillis(list.get(0).getStartMillis());
        end.setMillis(list.get(list.size() - 1).getEndMillis());

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
                    try {
                        firstStart();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public void firstStart() throws InterruptedException, ExecutionException, TimeoutException {

        start = new MutableDateTime();
        end = new MutableDateTime();

        start.setHourOfDay(9);
        start.setMinuteOfHour(0);
        start.setSecondOfMinute(0);
        end.setHourOfDay(23);
        end.setMinuteOfHour(0);
        end.setSecondOfMinute(0);

        list = splitDuration(start, end, settings.getInt("CPD", 0));
        saveSchedule(list);

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

    public void putScheduleInMap() throws IOException {

        byte[] schedule;
        schedule=convertToBytes(list);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/schedule");
        putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
        putDataMapReq.getDataMap().putByteArray("schedule",schedule);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);


    }

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    public void putLeaderboardInMap() throws ExecutionException, InterruptedException, TimeoutException, IOException {

        ArrayList<User> mLeaderboard = db.getAllContacts();

        User me = new User(settings.getString("ID", null), settings.getString("name", null), settings.getString("surname", null), settings.getString("image", null), settings.getLong("points", 0), settings.getLong("dayPoints", 0), settings.getLong("weekPoints", 0), "", "");
        mLeaderboard.add(me);

        mLeaderboard = controller.addTestContacts(mLeaderboard);

        byte[] leaderboard=convertToBytes(mLeaderboard);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/leaderboard");
        putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
        putDataMapReq.getDataMap().putByteArray("leaderboard",leaderboard);
        //putDataMapReq.getDataMap().putAsset("profileImage", asset);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);


       /* int i = 0;
        for (User contact : mLeaderboard) {

           // Asset asset = createAssetFromBitmap(new DownloadImgTask().execute(contact.getProfilePic()).get(10000, TimeUnit.MILLISECONDS));

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/leaderboard/" + i);
            putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
            //putDataMapReq.getDataMap().putAsset("profileImage", asset);
            contact.putToDataMap(putDataMapReq.getDataMap());
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
            i++;

            Log.v("LEADERBOARD", "DONE: " + contact.getName());
        }*/

    }

    public void putAchievementsInMap() throws IOException {

        ArrayList<Achievement> mAchievements = (ArrayList<Achievement>) db.getAllAchievements();

        byte[] achievements=convertToBytes(mAchievements);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/achievements");
        putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
        putDataMapReq.getDataMap().putByteArray("achievements",achievements);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

    }

    public void putChallengesInMap() throws IOException {

        ArrayList<Challenge> mChallenges = (ArrayList<Challenge>) db.getActiveChallenges();

        byte[] challenges=convertToBytes(mChallenges);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/challenges");
        putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
        putDataMapReq.getDataMap().putByteArray("challenges",challenges);
        //putDataMapReq.getDataMap().putAsset("profileImage", asset);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
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

        if (chunkAmount == 0) chunkAmount = 1;
        long chunkSize = (endMillis - millis) / chunkAmount;

        List<MutableInterval> list = new ArrayList<>();

        for (int i = 0; i < chunkAmount; ++i) {
            list.add(new MutableInterval(millis, millis += chunkSize));
        }

        return list;
    }

    static List<MutableInterval> shiftIntervals(MutableDateTime time, List<MutableInterval> list) {
        MutableDateTime endtime = new MutableDateTime();
        endtime.setHourOfDay(23);
        endtime.setMinuteOfHour(0);
        endtime.setSecondOfMinute(0);
        long shift = 0;

        for (MutableInterval i : list) {
            if (i.contains(time)) {
                shift = time.getMillis() - i.getStartMillis();
                i.setInterval(time.getMillis(), i.getEndMillis() + shift);
                continue;
            }

            i.setInterval(i.getStartMillis() + shift, i.getEndMillis() + shift);
        }

        List<MutableInterval> lista = list;

        for (MutableInterval i : list) {
            if (i.getStart().isAfter(endtime)) {
                lista.remove(i);
            }
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
        smokeIntent.setAction("smoked");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, smokeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dontSmokeIntent = new Intent(this, SmokeReceiver.class);
        dontSmokeIntent.putExtra("points", points * 2);
        dontSmokeIntent.putExtra("notificationID", notificationID);
        dontSmokeIntent.setAction("dontSmoke");
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        registerReceiver(uiUpdated, new IntentFilter("SMOKE_OUTOFTIME"));
        registerReceiver(sendWear, new IntentFilter("ASK_MOBILE"));

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

    private class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            for (String url : urls) {
                return controller.getCircleBitmap(controller.getBitmapFromURL(url));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            img = result;
        }
    }

}
