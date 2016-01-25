package it.polimi.stopit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.joda.time.MutableDateTime;

import it.polimi.stopit.R;

public class MenuActivity extends Activity {

    private ImageView leaderboard;
    private ImageView achievements;
    private ImageView challenges;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askLeaderboard();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.main);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                leaderboard = (ImageView) stub.findViewById(R.id.leaderboard_img);
                achievements = (ImageView) stub.findViewById(R.id.achievements_img);
                challenges = (ImageView) stub.findViewById(R.id.challenge_img);

                leaderboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchLead = new Intent(MenuActivity.this, LeaderboardActivity.class);
                        startActivity(launchLead);
                    }
                });

                achievements.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchAch = new Intent(MenuActivity.this, AchievementsActivity.class);
                        startActivity(launchAch);
                    }
                });

                challenges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchCall = new Intent(MenuActivity.this, ChallengesActivity.class);
                        startActivity(launchCall);
                    }
                });
            }
        });


    }

    public void askLeaderboard() {

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

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/askLeaderboard");
        putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

    }
}
