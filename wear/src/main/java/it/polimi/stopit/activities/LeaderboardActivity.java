package it.polimi.stopit.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.joda.time.MutableDateTime;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.stopit.adapters.LeaderboardAdapter;
import it.polimi.stopit.R;
import it.polimi.stopit.database.DatabaseHandlerWear;
import it.polimi.stopit.model.User;
import it.polimi.stopit.services.ScheduleServiceWear;

public class LeaderboardActivity extends Activity implements WearableListView.ClickListener {

    private WearableListView listView;
    private DatabaseHandlerWear db;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        db=new DatabaseHandlerWear(this);
        db.askMobile(this);

        checkDBContacts();
        final ArrayList<User> mLeaderboard=db.getAllContacts();

        Collections.sort(mLeaderboard, new LeaderComparator());

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.stub_leaderboard);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // Get the list component from the layout of the activity
                listView = (WearableListView) findViewById(R.id.leaderboard_list);

                // Assign an adapter to the list
                listView.setAdapter(new LeaderboardAdapter(LeaderboardActivity.this, mLeaderboard));

                // Set a click listener
                listView.setClickListener(LeaderboardActivity.this);
            }
        });
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        // use this data to complete some action ...
    }

    @Override
    public void onTopEmptyRegionClick() {
    }

    public static class LeaderComparator implements Comparator<User> {

        @Override
        public int compare(User contact1, User contact2) {

            return contact2.getPoints().compareTo(contact1.getPoints());

        }
    }

    public ArrayList<User> loadLeaderboard() {
        String filename = "leaderboard";
        ArrayList<User> lista;

        try {
            FileInputStream fis = this.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lista = (ArrayList<User>) ois.readObject();
            ois.close();
            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void checkDBContacts() {
        ArrayList<User> users = loadLeaderboard();
        ArrayList<User> dbUsers = db.getAllContacts();
        for (User u : users) {
            for (User dbU : dbUsers) {
                if (u.getID().equals(dbU.getID())) {
                    dbU.setPoints(u.getPoints());
                    db.updateContact(dbU);
                }
            }
            if(!dbUsers.contains(u)){
                db.addContact(u);
            }
        }
    }
}
