package it.polimi.stopit.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import it.polimi.stopit.R;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.fragments.AchievementFragment;
import it.polimi.stopit.fragments.ChallengeFragment;
import it.polimi.stopit.fragments.MoneyFragment;
import it.polimi.stopit.fragments.ProfileFragment;
import it.polimi.stopit.fragments.StatisticsFragment;
import it.polimi.stopit.model.User;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    User user;
    private long points;
    private long daypoints;
    private long weekpoints;
    private String redirect="";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dialog = ProgressDialog.show(NavigationActivity.this, "", "Loading data...", true,false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Controller control=new Controller(this);

        control.weeklyUpdate();
        control.dailyUpdate();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {

            if(getIntent().hasExtra("redirect")) {
                redirect = getIntent().getExtras().getString("redirect");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!redirect.equals("")) {

            switch (redirect) {
                case "money": {

                    Fragment fragment = MoneyFragment.newInstance();

                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

                    getSupportActionBar().setTitle("Money Target");

                    dialog.dismiss();


                    break;
                }
                case "challenges": {

                    Fragment fragment = ChallengeFragment.newInstance();

                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

                    getSupportActionBar().setTitle("Challenges");

                    dialog.dismiss();

                    break;
                }
                case "stopped":

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:

                                    Intent intent = new Intent(getBaseContext(), NavigationActivity.class);
                                    startActivity(intent);

                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                    builder.setIcon(getResources().getDrawable(R.drawable.legend, null));
                    builder.setMessage("Congratulations, you have succesfully stopped smoking !!").setPositiveButton("Wonderful", dialogClickListener).show();

                    dialog.dismiss();
                    break;
            }

        }

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        user=new User();
        user.setID(settings.getString("ID", null));
        user.setName(settings.getString("name", null));
        user.setSurname(settings.getString("surname", null));
        user.setPoints(settings.getLong("points", 0));
        user.setProfilePic(settings.getString("image", null));
        user.setDayPoints(settings.getLong("dayPoints", 0));
        user.setWeekPoints(settings.getLong("weekPoints", 0));
        user.setLastDayCheck(settings.getString("lastDayCheck",null));
        user.setLastWeekCheck(settings.getString("lastWeekCheck",null));

        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

        if (isOnline()) {

            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (!snapshot.child(user.getID()).exists()) {

                        myFirebaseRef.child(user.getID()).setValue(user);

                    }
                    if (snapshot.child(user.getID()).child("points").getValue() == null) {

                        points = 0;
                        daypoints = 0;
                        weekpoints = 0;

                    } else {

                        points = (long) snapshot.child(user.getID()).child("points").getValue();
                        daypoints = (long) snapshot.child(user.getID()).child("dayPoints").getValue();
                        weekpoints = (long) snapshot.child(user.getID()).child("weekPoints").getValue();

                    }

                    user.setPoints(points);
                    user.setDayPoints(daypoints);
                    user.setWeekPoints(weekpoints);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("ID", user.getID());
                    editor.putString("name", user.getName());
                    editor.putString("surname", user.getSurname());
                    editor.putLong("points", user.getPoints());
                    editor.putString("image", user.getProfilePic());
                    editor.putLong("dayPoints", user.getDayPoints());
                    editor.putLong("weekPoints", user.getWeekPoints());
                    editor.putString("lastDayCheck", snapshot.child(user.getID()).child("lastDayCheck").getValue().toString());
                    editor.putString("lastWeekCheck", snapshot.child(user.getID()).child("lastWeekCheck").getValue().toString());

                    editor.apply();

                    if(redirect.equals("")){

                        try {
                            Fragment fragment = ProfileFragment.newInstance(user.getID(), user.getName(), user.getSurname(), String.valueOf(points), user.getProfilePic());

                            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

                            dialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onCancelled(FirebaseError error) {
                }
            });

        } else if(redirect.equals("")){

            Toast.makeText(NavigationActivity.this, "Offline", Toast.LENGTH_SHORT).show();
            Fragment fragment = ProfileFragment.newInstance(user.getID(), user.getName(), user.getSurname(), String.valueOf(points), user.getProfilePic());

            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            dialog.dismiss();

        }

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

        TextView sidename = (TextView) findViewById(R.id.sideName);
        TextView sidelevel = (TextView) findViewById(R.id.sideLevel);
        sidename.setText("" + user.getName() + " " + user.getSurname());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        long points = settings.getLong("points", 0);
        Controller control = new Controller(this);

        sidelevel.setText(control.getLevel(points));

        CircularImageView sidebarpic = (CircularImageView) findViewById(R.id.sidebarPic);
        Picasso.with(getApplicationContext()).load(user.getProfilePic()).into(sidebarpic);

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.profile) {

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            user.setPoints(settings.getLong("points", 0));

            Fragment fragment = ProfileFragment.newInstance(user.getID(), user.getName(), user.getSurname(), String.valueOf(user.getPoints()), user.getProfilePic());

            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            getSupportActionBar().setTitle("Profile");


        } else if (id == R.id.leaderboard) {

            Intent intent = new Intent(this, LeaderboardActivity.class);

            startActivity(intent);

        } else if (id == R.id.achievements) {

            Fragment fragment = AchievementFragment.newInstance();

            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            getSupportActionBar().setTitle("Achievements");

        } else if (id == R.id.challenge) {

            Fragment fragment = ChallengeFragment.newInstance();

            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            getSupportActionBar().setTitle("Challenges");

        } else if (id == R.id.stats) {

            Fragment fragment = StatisticsFragment.newInstance();

            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            getSupportActionBar().setTitle("Statistics");

        } else if (id == R.id.money) {

            Fragment fragment = MoneyFragment.newInstance();

            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            getSupportActionBar().setTitle("Money Target");

        } else if (id == R.id.settings) {

            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);


        } else if (id == R.id.logout) {

            LoginManager.getInstance().logOut();
            startActivity(new Intent(getBaseContext(), Login.class));
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

