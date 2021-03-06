package it.polimi.stopit.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.adapters.AchievementsAdapter;
import it.polimi.stopit.database.DatabaseHandlerWear;
import it.polimi.stopit.model.Achievement;

public class AchievementsActivity extends Activity implements WearableListView.ClickListener {

    private DatabaseHandlerWear db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        db = new DatabaseHandlerWear(this);
        db.askMobile(this);

        checkAchievements();
        final ArrayList<Achievement> mAchievements = db.getAllAchievements();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // Get the list component from the layout of the activity
                WearableListView listView =
                        (WearableListView) findViewById(R.id.achievements_list);

                final View header=findViewById(R.id.header);
                final TextView title=(TextView) findViewById(R.id.titleA);


                // Assign an adapter to the list
                listView.setAdapter(new AchievementsAdapter(AchievementsActivity.this, mAchievements));

                // Set a click listener
                listView.setClickListener(AchievementsActivity.this);

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

    public ArrayList<Achievement> loadAchievements() {
        String filename = "achievements";
        ArrayList<Achievement> lista;

        try {
            FileInputStream fis = this.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lista = (ArrayList<Achievement>) ois.readObject();
            ois.close();
            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void checkAchievements() {
        ArrayList<Achievement> achievements = loadAchievements();
        ArrayList<Achievement> dbAch = db.getAllAchievements();
        for (Achievement ach : achievements) {
            for (Achievement dbA : dbAch) {
                if (ach.getId() == dbA.getId() && ach.isObtained() != dbA.isObtained()) {
                    dbA.setObtained(ach.isObtained());
                    db.updateAchievement(dbA);
                }
            }
        }
    }
}
