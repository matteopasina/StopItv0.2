package it.polimi.stopit.services;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.User;

public class SendDataService extends WearableListenerService{

    private GoogleApiClient mGoogleApiClient;
    private static final String COUNT_KEY = "com.example.key.count";

    @Override
    public void onCreate() {
        super.onCreate();

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
        increaseCounter();
    }

    public void putScheduleInMap(long start,long end, long CPD){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/schedule");
        putDataMapReq.getDataMap().putLong("start", start);
        putDataMapReq.getDataMap().putLong("end", end);
        putDataMapReq.getDataMap().putLong("CPD", CPD);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    // Create a data map and put data in it
    private void increaseCounter() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count");
        putDataMapReq.getDataMap().putInt(COUNT_KEY, 100);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    public void sendLeaderboardImages(){

        DatabaseHandler db=new DatabaseHandler(getBaseContext());
        Controller controller=new Controller(getBaseContext());

        ArrayList<User> contacts=db.getAllContacts();

        for(User contact:contacts){

            Asset asset= Asset.createFromBytes(controller.convertImageToBytes(contact.getProfilePic()));
            PutDataMapRequest dataMap = PutDataMapRequest.create("/image");

            dataMap.getDataMap().putAsset("contactImage", asset);
            PutDataRequest request = dataMap.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                    .putDataItem(mGoogleApiClient, request);
        }
    }
}

