package it.polimi.stopit.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;

import it.polimi.stopit.R;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.database.DatabaseSeeder;
import it.polimi.stopit.model.Cigarette;
import it.polimi.stopit.services.ScheduleService;

public class ProfileFragment extends Fragment {

    private static final String ARG_ID = "ID";
    private static final String ARG_NAME = "name";
    private static final String ARG_SURNAME = "surname";
    private static final String ARG_POINTS = "points";
    private static final String ARG_IMAGE = "imageURL";

    private String ID;
    private String name;
    private String surname;
    private String points;
    private String imageURL;
    private BroadcastReceiver uiUpdated;
    private static int gain = 0;
    Controller controller;
    SharedPreferences settings;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String ID, String name, String surname, String points, String imageURL) {
        Fragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, ID);
        args.putString(ARG_NAME, name);
        args.putString(ARG_SURNAME, surname);
        args.putString(ARG_POINTS, points);
        args.putString(ARG_IMAGE, imageURL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new Controller(getActivity());
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (getArguments() != null) {
            ID = getArguments().getString(ARG_ID);
            name = getArguments().getString(ARG_NAME);
            surname = getArguments().getString(ARG_SURNAME);
            points = getArguments().getString(ARG_POINTS);
            imageURL = getArguments().getString(ARG_IMAGE);
        }
        DatabaseHandler db = new DatabaseHandler(getActivity());
        db.deleteAllContacts();

        DatabaseSeeder dbs = new DatabaseSeeder(getActivity());
        dbs.loadContacts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final Animation down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        final Animation up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView usernameText = (TextView) view.findViewById(R.id.username);
        usernameText.setText("" + name + " " + surname);

        final TextView level = (TextView) view.findViewById(R.id.level);

        level.setText(controller.getLevel(Long.parseLong(points)));

        final TextView showPoints = (TextView) view.findViewById(R.id.points);
        showPoints.setText(controller.getLevelPointsString(Long.parseLong(points)));

        final TextView losePoints = (TextView) view.findViewById(R.id.pointsSecret);

        final ProgressBar levelProgress = (ProgressBar) view.findViewById(R.id.level_progress);
        levelProgress.setMax(100);

        int progress=(int)(100 * controller.getPointsLevel(Long.parseLong(points)) / controller.getLevelPoints(Long.parseLong(points)));
        levelProgress.setProgress(progress);

        smokeOrDont();

        Firebase.setAndroidContext(getActivity());
        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/" + ID + "/points");
        fire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long delta = (long) snapshot.getValue() - Long.valueOf(points);
                if (delta < 0) {
                    losePoints.setText("-" + String.valueOf(delta));
                    losePoints.setVisibility(TextView.VISIBLE);
                    losePoints.setTextColor(Color.parseColor("#B71C1C"));
                    losePoints.startAnimation(down);
                } else if (delta > 0) {
                    losePoints.setText("+" + String.valueOf(delta));
                    losePoints.setVisibility(TextView.VISIBLE);
                    losePoints.setTextColor(Color.parseColor("#8BC34A"));
                    losePoints.startAnimation(up);
                }
                points = snapshot.getValue().toString();
                showPoints.setText(controller.getLevelPointsString(Long.parseLong(points)));
                int progress=(int)(100 * controller.getPointsLevel(Long.parseLong(points)) / controller.getLevelPoints(Long.parseLong(points)));
                levelProgress.setProgress(progress);
                level.setText(controller.getLevel(Long.parseLong(points)));
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        CircularImageView profilepic = (CircularImageView) view.findViewById(R.id.profilepic);
        Picasso.with(getActivity().getApplicationContext()).load(imageURL).into(profilepic);

        final TextView timerText = (TextView) view.findViewById(R.id.timer);

        final DecoView arcHours = (DecoView) view.findViewById(R.id.circle_hours);
        final DecoView arcMinutes = (DecoView) view.findViewById(R.id.circle_minutes);
        final DecoView arcSeconds = (DecoView) view.findViewById(R.id.circle_seconds);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float dpInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, dm);

        // Create background track
        arcHours.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setSpinDuration(500)
                .setLineWidth(dpInPx)
                .build());

        arcMinutes.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setSpinDuration(500)
                .setLineWidth(dpInPx)
                .build());

        arcSeconds.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setSpinDuration(500)
                .setLineWidth(dpInPx)
                .build());

        //Create data series track
        SeriesItem hourSeries = new SeriesItem.Builder(Color.parseColor("#039BE5"))
                .setRange(0, 100, 0)
                .setLineWidth(dpInPx)
                .setSpinDuration(1000)
                .build();

        SeriesItem minuteSeries = new SeriesItem.Builder(Color.parseColor("#00E676"))
                .setRange(0, 100, 0)
                .setLineWidth(dpInPx)
                .setSpinDuration(1000)
                .build();

        final SeriesItem secondSeries = new SeriesItem.Builder(Color.parseColor("#FFC107"))
                .setRange(0, 100, 0)
                .setLineWidth(dpInPx)
                .setSpinDuration(1000)
                .build();

        final int series1Index = arcHours.addSeries(hourSeries);
        final int series2Index = arcMinutes.addSeries(minuteSeries);
        final int series3Index = arcSeconds.addSeries(secondSeries);

        arcHours.addEvent(new DecoEvent.Builder(100).setIndex(series1Index).setDelay(0).build());
        arcMinutes.addEvent(new DecoEvent.Builder(100).setIndex(series2Index).setDelay(0).build());
        arcSeconds.addEvent(new DecoEvent.Builder(100).setIndex(series3Index).setDelay(0).build());

        uiUpdated = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //This is the part where I get the timer value from the service and I update it every second, because I send the data from the service every second. The coundtdownTimer is a MenuItem
                long millisUntilFinished = intent.getExtras().getLong("countdown");
                setTimer(timerText, millisUntilFinished);

                long hours = millisUntilFinished / 3600000;
                long minutes = (millisUntilFinished - (hours * 3600000)) / 60000;
                long seconds = (millisUntilFinished - (hours * 3600000) - (minutes * 60000)) / 1000;

                arcHours.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 24) * hours)).setIndex(series1Index).setDelay(0).build());
                arcMinutes.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 60) * minutes)).setIndex(series2Index).setDelay(0).build());
                arcSeconds.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 60) * seconds)).setIndex(series3Index).setDelay(0).build());

            }
        };

        getActivity().startService(new Intent(getActivity(), ScheduleService.class));
        getActivity().registerReceiver(uiUpdated, new IntentFilter("COUNTDOWN_UPDATED"));

        Button smoke = (Button) view.findViewById(R.id.smoke);
        smoke.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case DialogInterface.BUTTON_POSITIVE:
                                DatabaseHandler dbh = new DatabaseHandler(getActivity());
                                controller.updatePoints(-50);

                                settings.edit().putLong("points", Long.parseLong(points) - 50).apply();
                                settings.edit().putLong("weekPoints", settings.getLong("weekPoints", 0) - 50).apply();
                                settings.edit().putLong("dayPoints", settings.getLong("dayPoints", 0) - 50).apply();

                                MutableDateTime dt = new MutableDateTime(DateTimeZone.UTC);
                                DateTime date = new DateTime(new Instant());
                                dbh.addCigarette(new Cigarette(1, date, "smoke"));

                                Intent i = new Intent("SMOKE_OUTOFTIME");
                                i.putExtra("time", dt);
                                getActivity().sendBroadcast(i);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure? You will lose 50 points!!").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(uiUpdated);
    }

    public void setTimer(TextView timerText, long millis) {

        long hours = millis / 3600000;
        long minutes = (millis - (hours * 3600000)) / 60000;
        long seconds = (millis - (hours * 3600000) - (minutes * 60000)) / 1000;

        if (hours >= 10) {
            if (minutes >= 10) {
                if (seconds >= 10) {

                    timerText.setText(hours + ":" + minutes + ":" + seconds);
                } else {

                    timerText.setText(hours + ":" + minutes + ":0" + seconds);
                }
            } else {

                if (seconds >= 10) {

                    timerText.setText(hours + ":0" + minutes + ":" + seconds);
                } else {

                    timerText.setText(hours + ":0" + minutes + ":0" + seconds);
                }
            }
        } else {

            if (minutes >= 10) {
                if (seconds >= 10) {

                    timerText.setText("0" + hours + ":" + minutes + ":" + seconds);
                } else {

                    timerText.setText("0" + hours + ":" + minutes + ":0" + seconds);
                }
            } else {

                if (seconds >= 10) {

                    timerText.setText("0" + hours + ":0" + minutes + ":" + seconds);
                } else {

                    timerText.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                }
            }
        }
    }

    private void smokeOrDont() {

        if (gain != 0) {
            gain = 0;
        }
        if (getActivity().getIntent().getExtras() != null) {

            gain = getActivity().getIntent().getExtras().getInt("points", 0);
            getActivity().getIntent().removeExtra("points");
        }

        if(getActivity().getIntent().hasExtra("alternative")){
            getActivity().getIntent().removeExtra("alternative");

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    long points = settings.getLong("points", 0);
                    long daypoints = settings.getLong("dayPoints", 0);
                    long weekpoints = settings.getLong("weekPoints", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    DatabaseHandler dbh = new DatabaseHandler(getActivity());
                    DateTime date;

                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            int bonus = dbh.getAlternative(getActivity().getIntent().getExtras().getString("alternative")).getBonusPoints();
                            editor.putLong("dayPoints", daypoints + bonus);
                            editor.putLong("weekPoints", weekpoints + bonus);
                            editor.putLong("points", points + bonus);
                            editor.commit();

                            controller.updatePoints(bonus);

                            date = new DateTime(new Instant());
                            dbh.addCigarette(new Cigarette(1, date, "smoke"));
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            editor.putLong("points", points + gain);
                            editor.putLong("dayPoints", daypoints + gain);
                            editor.putLong("weekPoints", weekpoints + gain);
                            editor.commit();

                            controller.updatePoints(gain);

                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose")
                    .setCancelable(false)
                    .setMessage("Do the activity and you will get bonus points!")
                    .setPositiveButton("Alternative!", dialogClickListener)
                    .setNegativeButton("smoke", dialogClickListener)
                    .setIcon(R.drawable.stopitsymbol)
                    .show();
        }

        else if (gain != 0) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    long points = settings.getLong("points", 0);
                    long daypoints = settings.getLong("dayPoints", 0);
                    long weekpoints = settings.getLong("weekPoints", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    DatabaseHandler dbh = new DatabaseHandler(getActivity());
                    DateTime date;

                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            gain = gain * 2;
                            editor.putLong("dayPoints", daypoints + gain);
                            editor.putLong("weekPoints", weekpoints + gain);
                            editor.putLong("points", points + gain);
                            editor.commit();

                            controller.updatePoints(gain);

                            date = new DateTime(new Instant());
                            dbh.addCigarette(new Cigarette(1, date, "smoke"));
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            editor.putLong("points", points + gain);
                            editor.putLong("dayPoints", daypoints + gain);
                            editor.putLong("weekPoints", weekpoints + gain);
                            editor.commit();

                            controller.updatePoints(gain);

                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose")
                    .setCancelable(false)
                    .setMessage("Don't smoke and you will get double points!")
                    .setPositiveButton("Don't smoke", dialogClickListener)
                    .setNegativeButton("smoke", dialogClickListener)
                    .setIcon(R.drawable.stopitsymbol)
                    .show();
        }
    }

}
