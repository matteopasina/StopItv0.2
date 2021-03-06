package it.polimi.stopit.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import it.polimi.stopit.database.DatabaseHandlerWear;
import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.Challenge;
import it.polimi.stopit.model.User;

/**
 * Created by matteo on 19/01/16.
 */
public class WearListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private DatabaseHandlerWear db;
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
        db=new DatabaseHandlerWear(this);

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        System.out.println("DATA CHANGE");

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // DataItem changed
                DataItem item = event.getDataItem();
                dataMap = DataMapItem.fromDataItem(item).getDataMap();
                Log.v("PATHONWEAR", item.getUri().getPath());

                if (item.getUri().getPath().compareTo("/stopit/ID") == 0) {

                    Log.v("ID", "Ricevuto");
                    SharedPreferences s= PreferenceManager.getDefaultSharedPreferences(this);
                    s.edit().putString("ID", dataMap.getString("ID")).apply();

                }

                if (item.getUri().getPath().compareTo("/stopit/schedule") == 0) {

                    Log.v("SCHEDULE","Ricevuto");
                    Intent schedule = new Intent("SET_SCHEDULE");
                    schedule.putExtra("schedule",dataMap.getByteArray("schedule"));
                    sendBroadcast(schedule);

                }

                if (item.getUri().getPath().compareTo("/stopit/leaderboard") == 0) {
                    Log.v("LEADERBOARD", "Ricevuto");
                    try {
                        leaderboard=(ArrayList<User>)convertFromBytes(dataMap.getByteArray("leaderboard"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    saveLeaderboard(leaderboard);
                }

                if (item.getUri().getPath().compareTo("/stopit/achievements") == 0) {
                    Log.v("ACHIEVEMENTS", "Ricevuto");
                    try {
                        achievements=(ArrayList<Achievement>)convertFromBytes(dataMap.getByteArray("achievements"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    saveAchievements(achievements);
                }

                if (item.getUri().getPath().compareTo("/stopit/challenges") == 0) {
                    Log.v("CHALLENGES", "Ricevuto");
                    try {
                        challenges=(ArrayList<Challenge>)convertFromBytes(dataMap.getByteArray("challenges"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    saveChallenges(challenges);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
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

    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();

        }catch(Exception e){
            e.printStackTrace();

            return  null;
        }
    }

}
