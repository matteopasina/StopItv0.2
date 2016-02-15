package it.polimi.stopit.activities;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.adapters.ChallengesAdapter;
import it.polimi.stopit.database.DatabaseHandlerWear;
import it.polimi.stopit.model.Challenge;

public class ChallengesActivity extends Activity implements WearableListView.ClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);
        DatabaseHandlerWear db=new DatabaseHandlerWear(this);
        db.askMobile(this);

        final ArrayList<Challenge> mChallenges = loadChallenges();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                // Get the list component from the layout of the activity
                WearableListView listView =
                        (WearableListView) findViewById(R.id.challenge_list);

                final View header=findViewById(R.id.header);
                final TextView title=(TextView) findViewById(R.id.titleC);


                // Assign an adapter to the list
                listView.setAdapter(new ChallengesAdapter(ChallengesActivity.this, mChallenges));

                // Set a click listener
                listView.setClickListener(ChallengesActivity.this);

                listView.addOnScrollListener(new WearableListView.OnScrollListener() {
                    @Override
                    public void onScroll(int i) {
                        header.setY(header.getY() - i);
                        title.setY(title.getY() - i);
                    }

                    @Override
                    public void onAbsoluteScrollChange(int i) {
                    }

                    @Override
                    public void onScrollStateChanged(int i) {
                    }

                    @Override
                    public void onCentralPositionChanged(int i) {
                    }
                });
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

    public ArrayList<Challenge> loadChallenges() {
        String filename = "challenges";
        ArrayList<Challenge> lista;

        try {
            FileInputStream fis = this.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lista = (ArrayList<Challenge>) ois.readObject();
            ois.close();
            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
