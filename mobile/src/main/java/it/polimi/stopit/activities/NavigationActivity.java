package it.polimi.stopit.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import it.polimi.stopit.R;
import it.polimi.stopit.fragments.AchievementFragment;
import it.polimi.stopit.fragments.ChallengeFragment;
import it.polimi.stopit.fragments.LeaderboardFragment;
import it.polimi.stopit.fragments.MoneyFragment;
import it.polimi.stopit.fragments.ProfileFragment;
import it.polimi.stopit.model.User;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ProfileFragment.OnFragmentInteractionListener {

    User user=new User();
    private long points;
    public static final String PREFS_NAME = "StopItPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        user.setID(settings.getString("ID", null));
        user.setName(settings.getString("name", null));
        user.setSurname(settings.getString("surname", null));
        user.setPoints(settings.getLong("points", 0));
        user.setProfilePic(settings.getString("image", null));



        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
        if(isOnline()) {
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

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("ID", user.getID());
                    editor.putString("name", user.getName());
                    editor.putString("surname", user.getSurname());
                    editor.putLong("points", user.getPoints());
                    editor.putString("image", user.getProfilePic());
                    // Commit the edits!
                    editor.commit();
                    try {
                        Fragment fragment = ProfileFragment.newInstance(user.getName(), user.getSurname(), String.valueOf(points), user.getProfilePic());

                        FragmentManager fragmentManager = getFragmentManager();

                        FragmentTransaction ft = fragmentManager.beginTransaction();

                        ft.replace(R.id.content_frame, fragment);

                        ft.commit();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(FirebaseError error) {
                }
            });
        }
        else{
            Toast.makeText(NavigationActivity.this, "Offline", Toast.LENGTH_SHORT).show();
            Fragment fragment = ProfileFragment.newInstance(user.getName(), user.getSurname(), String.valueOf(points), user.getProfilePic());

            FragmentManager fragmentManager = getFragmentManager();

            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();
        }
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

            Fragment fragment = LeaderboardFragment.newInstance();

            FragmentManager fragmentManager=getFragmentManager();

            FragmentTransaction ft=fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();

            getSupportActionBar().setTitle("Leaderboard");

        } else if (id == R.id.achievements) {

            Fragment fragment = AchievementFragment.newInstance();

            FragmentManager fragmentManager=getFragmentManager();

            FragmentTransaction ft=fragmentManager.beginTransaction();

            ft.replace(R.id.content_frame, fragment);

            ft.commit();

            getSupportActionBar().setTitle("Achievements");

        } else if (id == R.id.challenge) {

            Fragment fragment = ChallengeFragment.newInstance();

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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
