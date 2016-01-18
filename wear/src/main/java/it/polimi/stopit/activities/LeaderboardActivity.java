package it.polimi.stopit.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.stopit.adapters.ListAdapter;
import it.polimi.stopit.R;
import it.polimi.stopit.model.User;

public class LeaderboardActivity extends Activity implements WearableListView.ClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        final ArrayList<User> mLeaderboard=new ArrayList<>();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.stub_leaderboard);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mLeaderboard.add(new User("1", "Paulo", "Dybala", "http://sortitoutsi.net/uploads/face/14044150.png", Long.parseLong("34110"), Long.parseLong("888"), Long.parseLong("17575")));
                mLeaderboard.add(new User("2", "Lionel", "Messi", "http://static2.blastingnews.com/media/photogallery/2015/10/8/290x290/b_290x290/lionel-messi-pode-terminar-a-carreira-dessa-forma_452055.jpg", Long.parseLong("89670"), Long.parseLong("3200"), Long.parseLong("18480")));
                mLeaderboard.add(new User("3", "Eden", "Hazard", "http://img.uefa.com/imgml/TP/players/9/2013/324x324/1902160.jpg", Long.parseLong("17923"), Long.parseLong("2280"), Long.parseLong("7920")));
                mLeaderboard.add(new User("4", "Scarlett", "Johansson", "https://pbs.twimg.com/profile_images/629741380957511680/cruVnLi2.jpg", Long.parseLong("24560"), Long.parseLong("1277"), Long.parseLong("6800")));
                mLeaderboard.add(new User("5", "Guido", "Meda", "http://www.motocorse.com/foto/22762/thumbs500/1.jpg", Long.parseLong("79800"), Long.parseLong("560"), Long.parseLong("18340")));
                mLeaderboard.add(new User("6", "Federica", "Nargi", "http://pbs.twimg.com/profile_images/665605062585155584/fhGO4ZY9_reasonably_small.jpg", Long.parseLong("48267"), Long.parseLong("650"), Long.parseLong("12700")));
                mLeaderboard.add(new User("7", "Alessandro", "Del Piero", "http://d1ktyob8e4hu6c.cloudfront.net/pub/avatar/RUNA_9839085/communitygazzetta/Il%20futuro%20di%20Del%20Piero.jpg", Long.parseLong("68880"), Long.parseLong("721"), Long.parseLong("8180")));
                mLeaderboard.add(new User("8", "Gianluigi", "Buffon", "http://i.imgur.com/9VYiq8e.png", Long.parseLong("11450"), Long.parseLong("1000"), Long.parseLong("3200")));
                mLeaderboard.add(new User("9", "Stephen", "Curry", "http://www.sportsspeakers360.com/admin/img/stephen-curry.jpg", Long.parseLong("43200"), Long.parseLong("100"), Long.parseLong("2950")));
                mLeaderboard.add(new User("10", "Joe", "Bastianich", "http://www.foodserviceconsultant.org/wp-content/uploads/Joe-Bastianich250.jpg", Long.parseLong("54277"), Long.parseLong("-250"), Long.parseLong("1200")));
                // Get the list component from the layout of the activity
                WearableListView listView =
                        (WearableListView) findViewById(R.id.leaderboard_list);

                // Assign an adapter to the list
                listView.setAdapter(new ListAdapter(LeaderboardActivity.this, mLeaderboard));

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
}
