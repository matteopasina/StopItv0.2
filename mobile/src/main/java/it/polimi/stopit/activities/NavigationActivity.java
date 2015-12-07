package it.polimi.stopit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import it.polimi.stopit.R;
import it.polimi.stopit.model.User;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    User user=new User();
    private long points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user.setID(getIntent().getExtras().getString("userID"));
        user.setName(getIntent().getExtras().getString("name"));
        user.setSurname(getIntent().getExtras().getString("surname"));
        user.setProfilePic(getIntent().getExtras().getString("imageURL"));

        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/");
        myFirebaseRef.child("User").child("ID").setValue(user.getID());
        myFirebaseRef.child("User").child("Name").setValue(user.getName());
        myFirebaseRef.child("User").child("Surname").setValue(user.getSurname());
        myFirebaseRef.child("User").child("Points").setValue(0);
        myFirebaseRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                TextView username = (TextView) findViewById(R.id.username);
                username.setText("" + snapshot.child("Name").getValue().toString() + " " + snapshot.child("Surname").getValue().toString());
                points=(long)snapshot.child("Points").getValue();
                TextView showPoints=(TextView) findViewById(R.id.points);
                showPoints.setText("Points:"+String.valueOf(points));
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        /*TextView username = (TextView) findViewById(R.id.username);

        username.setText("" + user.getName() + " " + user.getSurname());*/
        CircularImageView profilepic=(CircularImageView) findViewById(R.id.profilepic);
        Picasso.with(getApplicationContext()).load(user.getProfilePic()).into(profilepic);

        Button smoke=(Button)findViewById(R.id.smoke);
        smoke.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                points+=50;
                myFirebaseRef.child("User").child("Points").setValue(points);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        TextView sidename = (TextView) findViewById(R.id.sideName);
        TextView sidelevel = (TextView) findViewById(R.id.sideLevel);
        sidename.setText("" + user.getName() + " " + user.getSurname());
        sidelevel.setText("Beginner: level 1");
        ImageView sidebarpic = (ImageView) findViewById(R.id.sidebarPic);
        Picasso.with(getApplicationContext()).load(user.getProfilePic()).into(sidebarpic);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            // Handle the camera action
        } else if (id == R.id.leaderboard) {

        } else if (id == R.id.achievements) {

        } else if (id == R.id.challenge) {

        } else if (id == R.id.stats) {

        } else if (id == R.id.money) {

        } else if (id == R.id.settings) {

        }else if (id == R.id.logout) {
            Intent intent = new Intent(this,Login.class);
            AccessToken.setCurrentAccessToken(null);
            Profile.setCurrentProfile(null);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
