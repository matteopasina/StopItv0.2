package it.polimi.stopit.services;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;

import java.io.ByteArrayOutputStream;

import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Cigarette;


public class ListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences settings;
    private Controller controller;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        controller=new Controller(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // DataItem changed
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                Log.v("DATA CHANGE", dataMap.toString());

                if (item.getUri().getPath().compareTo("/stopit/askMobile") == 0) {

                    Log.v("ASKMOBILE",""+dataMap.getLong("timestamp"));
                    Intent intent = new Intent("ASK_MOBILE");
                    sendBroadcast(intent);

                }

                if (item.getUri().getPath().compareTo("/stopit/smoke") == 0) {

                    Log.v("SMOKE",""+dataMap.getLong("timestamp"));
                    DatabaseHandler dbh = new DatabaseHandler(this);
                    controller=new Controller(this);
                    settings= PreferenceManager.getDefaultSharedPreferences(this);

                    controller.updatePoints(-50);

                    settings.edit().putLong("points", settings.getLong("points",0) - 50).apply();
                    settings.edit().putLong("weekPoints", settings.getLong("weekPoints", 0) - 50).apply();
                    settings.edit().putLong("dayPoints", settings.getLong("dayPoints", 0) - 50).apply();

                    MutableDateTime dt = new MutableDateTime(DateTimeZone.UTC);
                    DateTime date = new DateTime(new Instant());
                    dbh.addCigarette(new Cigarette(1, date, "smoke"));

                    Intent i = new Intent("SMOKE_OUTOFTIME");
                    i.putExtra("time", dt);
                    sendBroadcast(i);
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    @Override
    public void onConnected (Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Log.v("LISTENER_MOBILE: ", "connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
