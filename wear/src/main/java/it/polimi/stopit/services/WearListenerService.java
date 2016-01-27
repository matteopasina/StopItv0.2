package it.polimi.stopit.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.joda.time.MutableDateTime;
import org.joda.time.MutableInterval;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.ChallengesActivity;
import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.Challenge;
import it.polimi.stopit.model.User;

/**
 * Created by matteo on 19/01/16.
 */
public class WearListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private ArrayList<User> leaderboard = new ArrayList<>();
    private ArrayList<Achievement> achievements = new ArrayList<>();
    private ArrayList<Challenge> challenges = new ArrayList<>();
    private DataMap dataMap;

    @Override
    public void onCreate() {

        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        System.out.println("DATA CHANGE");
        int i = 0;

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().equals("/image")) {

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                Asset profileAsset = dataMapItem.getDataMap().getAsset("contactImage");
                Bitmap bitmap = loadBitmapFromAsset(profileAsset);

                String imagePath = Environment.getExternalStorageDirectory() + "/contactImage" + i + ".jpg";

                try {
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(imagePath));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (event.getType() == DataEvent.TYPE_CHANGED) {

                // DataItem changed
                DataItem item = event.getDataItem();
                dataMap = DataMapItem.fromDataItem(item).getDataMap();
                Log.v("PATHONWEAR", item.getUri().getPath());

                if (item.getUri().getPath().compareTo("/stopit/schedule") == 0) {

                    Intent schedule = new Intent("SET_SCHEDULE");
                    schedule.putExtra("start", dataMap.getLong("start"));
                    schedule.putExtra("end", dataMap.getLong("end"));
                    schedule.putExtra("CPD", dataMap.getLong("CPD"));
                    sendBroadcast(schedule);

                }

                if (item.getUri().getPath().matches("/stopit/leaderboard/.*")) {

                    Log.v("DENTRO;", "Dentro");
                    Asset profileAsset = dataMap.getAsset("profileImage");
                    Bitmap bitmap = loadBitmapFromAsset(profileAsset);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    User user = new User(dataMap);
                    user.setImg(byteArray);

                    if (!leaderboard.contains(user)) {
                        leaderboard.add(user);
                    }

                }

                if (item.getUri().getPath().matches("/stopit/achievements/.*")) {

                    Asset achievementAsset = dataMap.getAsset("achievementImage");
                    Bitmap bitmap = loadBitmapFromAsset(achievementAsset);

                    Achievement achievement = new Achievement(dataMap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    achievement.setImg(byteArray);

                    achievements.add(achievement);

                }

                for (int k = 0; k < 5; k++) {
                    if (item.getUri().getPath().compareTo("/stopit/challenges/" + k) == 0) {

                        /*Asset achievementAsset = dataMap.getAsset("achievementImage");
                        Bitmap bitmap = loadBitmapFromAsset(achievementAsset);*/

                        Challenge challenge = new Challenge(dataMap);

                        challenges.add(challenge);

                    }
                }


            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }

        saveLeaderboard(leaderboard);
        saveAchievements(achievements);
        saveChallenges(challenges);
    }

    private void saveChallenges(ArrayList<Challenge> challenges) {
        String filename = "challenges";

        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(challenges);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveLeaderboard(ArrayList<User> leaderboard) {
        String filename = "leaderboard";

        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(leaderboard);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveAchievements(ArrayList<Achievement> achievements) {
        String filename = "achievements";

        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(achievements);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {

        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        /*ConnectionResult result = mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);

        if (!result.isSuccess()) {
            Log.v("ASSETRESULT", "Requested an unknown Asset. Unsuccess " + result);
            return null;
        }*/
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();

        if (assetInputStream == null) {
            Log.v("ASSETINPUTSTREAM", "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

}
