package it.polimi.stopit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.joda.time.MutableDateTime;

import it.polimi.stopit.R;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Challenge;

public class ChallengeDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Challenge Detail");

        final TextView opponentName=(TextView) findViewById(R.id.vsName);
        final TextView yourPoints=(TextView) findViewById(R.id.yourPoints);
        final TextView opponentPoints=(TextView) findViewById(R.id.opponentPoints);
        final TextView timeLeft=(TextView) findViewById(R.id.timeLeft);
        final Button giveUp=(Button) findViewById(R.id.give_up);
        giveUp.setText("Give up");

        final SharedPreferences p= PreferenceManager.getDefaultSharedPreferences(this);

        String opponentID=getIntent().getStringExtra("opponentID");
        final DatabaseHandler dbh=new DatabaseHandler(this);
        final Challenge challenge=dbh.getChallengeByOpponentID(opponentID);
        yourPoints.setText(String.valueOf(challenge.getOpponentPoints()));
        opponentPoints.setText(String.valueOf(challenge.getMyPoints()));

        MutableDateTime time=new MutableDateTime();
        time.setMillis(challenge.getEndTime() - time.getMillis());

        int days=(int)(time.getMillis())/(1000*60*60*24);
        time.setMillis(time.getMillis()-days*1000*60*60*24);

        int hours=(int)(time.getMillis())/(1000*60*60);
        time.setMillis(time.getMillis()-hours*1000*60*60);

        int minutes=(int)(time.getMillis())/(1000*60);
        timeLeft.setText(days+" days "+hours+" hours "+minutes+" minutes");


        Firebase.setAndroidContext(this);
        final Firebase fireOpponent = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/"+opponentID);
        final Firebase fireChallenge = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges/"+challenge.getID());

        fireChallenge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("id").getValue().toString().equals(p.getString("ID",null))){
                    yourPoints.setText(dataSnapshot.child("myPoints").getValue().toString());
                    opponentPoints.setText(dataSnapshot.child("opponentPoints").getValue().toString());
                }
                else if(dataSnapshot.child("opponentID").getValue().toString().equals(p.getString("ID",null))){
                    yourPoints.setText(dataSnapshot.child("opponentPoints").getValue().toString());
                    opponentPoints.setText(dataSnapshot.child("myPoints").getValue().toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        CircularImageView yourPic=(CircularImageView) findViewById(R.id.yourPic);
        Picasso.with(this.getApplicationContext()).load(p.getString("image",null)).into(yourPic);

        final CircularImageView opponentPic=(CircularImageView) findViewById(R.id.opponentPic);
        fireOpponent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                opponentName.setText(dataSnapshot.child("name").getValue().toString());
                Picasso.with(ChallengeDetail.this.getApplicationContext())
                        .load(dataSnapshot.child("profilePic").getValue().toString())
                        .into(opponentPic);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        giveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Firebase fireChallenge = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges/"+challenge.getID());
                fireChallenge.removeValue();
                dbh.deleteChallenge(challenge.getID());
                Intent intent=new Intent(ChallengeDetail.this,NavigationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}