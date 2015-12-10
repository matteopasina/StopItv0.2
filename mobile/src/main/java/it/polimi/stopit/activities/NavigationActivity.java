package it.polimi.stopit.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import it.polimi.stopit.fragments.MoneyFragment;
import it.polimi.stopit.fragments.ProfileFragment;
import it.polimi.stopit.model.User;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ProfileFragment.OnFragmentInteractionListener {

    User user=new User();
    private long points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user.setID(getIntent().getExtras().getString("userID"));
        user.setName(getIntent().getExtras().getString("name"));
        user.setSurname(getIntent().getExtras().getString("surname"));
        user.setProfilePic(getIntent().getExtras().getString("imageURL"));

        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.child(user.getID()).exists()) {
                    myFirebaseRef.child(user.getID()).setValue(user);
                }
                if (snapshot.child(user.getID()).child("points").getValue() == null) {
                    points = 0;
                } else {
                    points = (long) snapshot.child(user.getID()).child("points").getValue();

                }

                user.setPoints(points);

                Fragment fragment = ProfileFragment.newInstance(user.getName(), user.getSurname(), String.valueOf(points), user.getProfilePic());

                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction ft = fragmentManager.beginTransaction();

                ft.replace(R.id.content_frame, fragment);

                ft.commit();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        CircularImageView sidebarpic=(CircularImageView) findViewById(R.id.sidebarPic);
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

            Fragment fragment = ProfileFragment.newInstance(user.getName(),user.getSurname(),String.valueOf(user.getPoints()),user.getProfilePic());

            FragmentManager fragmentManager=getFragmentManager();

            FragmentTransaction ft=fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();

            getSupportActionBar().setTitle("Profile");
            

        } else if (id == R.id.leaderboard) {

            Fragment fragment = ProfileFragment.newInstance(user.getName(),user.getSurname(),String.valueOf(user.getPoints()),user.getProfilePic());

            FragmentManager fragmentManager=getFragmentManager();

            FragmentTransaction ft=fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();

            getSupportActionBar().setTitle("Leaderboard");

        } else if (id == R.id.achievements) {

            Fragment fragment = ProfileFragment.newInstance(user.getName(),user.getSurname(),String.valueOf(user.getPoints()),user.getProfilePic());

            FragmentManager fragmentManager=getFragmentManager();

            FragmentTransaction ft=fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();

            getSupportActionBar().setTitle("Achievements");

        } else if (id == R.id.challenge) {

            Fragment fragment = ProfileFragment.newInstance(user.getName(),user.getSurname(),String.valueOf(user.getPoints()),user.getProfilePic());

            FragmentManager fragmentManager=getFragmentManager();

            FragmentTransaction ft=fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();

            getSupportActionBar().setTitle("Challenges");

        } else if (id == R.id.stats) {

            Fragment fragment = ProfileFragment.newInstance(user.getName(),user.getSurname(),String.valueOf(user.getPoints()),user.getProfilePic());

            FragmentManager fragmentManager=getFragmentManager();

            FragmentTransaction ft=fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();

            getSupportActionBar().setTitle("Statistics");

        } else if (id == R.id.money) {

            Fragment fragment = MoneyFragment.newInstance();

            FragmentManager fragmentManager=getFragmentManager();

            FragmentTransaction ft=fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();

            getSupportActionBar().setTitle("Money Target");

        } else if (id == R.id.settings) {

            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);

            startActivity(intent);



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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
