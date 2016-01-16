package it.polimi.stopit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.polimi.stopit.R;

public class MainActivity extends Activity {

    private ImageView leaderboard;
    private ImageView achievements;
    private ImageView challenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.main);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                leaderboard = (ImageView) stub.findViewById(R.id.leaderboard_img);
                achievements=(ImageView) stub.findViewById(R.id.achievements_img);
                challenges=(ImageView) stub.findViewById(R.id.challenge_img);

                leaderboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchLead = new Intent(MainActivity.this, LeaderboardActivity.class);
                        startActivity(launchLead);
                        finish();
                    }
                });

                achievements.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchLead = new Intent(MainActivity.this, LeaderboardActivity.class);
                        startActivity(launchLead);
                        finish();
                    }
                });

                challenges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchLead = new Intent(MainActivity.this, LeaderboardActivity.class);
                        startActivity(launchLead);
                        finish();
                    }
                });
            }
        });


    }
}
