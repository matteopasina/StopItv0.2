package it.polimi.stopit.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.stopit.adapters.LeaderboardAdapter;
import it.polimi.stopit.R;
import it.polimi.stopit.model.User;

public class LeaderboardActivity extends Activity implements WearableListView.ClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        final ArrayList<User> mLeaderboard = loadLeaderboard();

        Collections.sort(mLeaderboard, new LeaderComparator());

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.stub_leaderboard);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // Get the list component from the layout of the activity
                WearableListView listView =
                        (WearableListView) findViewById(R.id.leaderboard_list);

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
}
