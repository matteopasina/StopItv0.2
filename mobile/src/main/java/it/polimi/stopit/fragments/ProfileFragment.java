package it.polimi.stopit.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Cigarette;
import it.polimi.stopit.services.ScheduleService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final String ARG_ID = "ID";
    private static final String ARG_NAME = "name";
    private static final String ARG_SURNAME = "surname";
    private static final String ARG_POINTS = "points";
    private static final String ARG_IMAGE = "imageURL";


    // TODO: Rename and change types of parameters
    private String ID;
    private String name;
    private String surname;
    private String points;
    private String imageURL;
    private BroadcastReceiver uiUpdated;
    private static int gain=0;


    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String ID,String name, String surname, String points,String imageURL) {
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
        if (getArguments() != null) {
            ID = getArguments().getString(ARG_ID);
            name = getArguments().getString(ARG_NAME);
            surname = getArguments().getString(ARG_SURNAME);
            points = getArguments().getString(ARG_POINTS);
            imageURL = getArguments().getString(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final Animation down=AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        final Animation up= AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView usernameText = (TextView) view.findViewById(R.id.username);
        usernameText.setText("" + name + " " + surname);

        final TextView showPoints = (TextView) view.findViewById(R.id.points);
        showPoints.setText("Points:  " + points);

        final TextView losePoints = (TextView) view.findViewById(R.id.pointsSecret);

        smokeOrDont();

        Firebase.setAndroidContext(getActivity());
        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/"+ID+"/points");
        fire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long delta = (long) snapshot.getValue() - Long.valueOf(points);
                if (delta < 0) {
                    losePoints.setText("-"+String.valueOf(delta));
                    losePoints.setVisibility(TextView.VISIBLE);
                    losePoints.setTextColor(Color.parseColor("#B71C1C"));
                    losePoints.startAnimation(down);
                } else if (delta > 0) {
                    losePoints.setText("+"+String.valueOf(delta));
                    losePoints.setVisibility(TextView.VISIBLE);
                    losePoints.setTextColor(Color.parseColor("#8BC34A"));
                    losePoints.startAnimation(up);
                }
                points = snapshot.getValue().toString();
                showPoints.setText("Points:  " + points);
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        CircularImageView profilepic=(CircularImageView) view.findViewById(R.id.profilepic);
        Picasso.with(getActivity().getApplicationContext()).load(imageURL).into(profilepic);

        final TextView timerText= (TextView) view.findViewById(R.id.timer);

        final DecoView arcHours = (DecoView) view.findViewById(R.id.circle_hours);
        final DecoView arcMinutes = (DecoView) view.findViewById(R.id.circle_minutes);
        final DecoView arcSeconds = (DecoView) view.findViewById(R.id.circle_seconds);

        // Create background track
        arcHours.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setSpinDuration(500)
                .setLineWidth(28)
                .build());

        arcMinutes.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setSpinDuration(500)
                .setLineWidth(28)
                .build());

        arcSeconds.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setSpinDuration(500)
                .setLineWidth(28)
                .build());

        //Create data series track
        SeriesItem hourSeries = new SeriesItem.Builder(Color.parseColor("#039BE5"))
                .setRange(0, 100, 0)
                .setLineWidth(28)
                .setSpinDuration(1000)
                .build();

        SeriesItem minuteSeries = new SeriesItem.Builder(Color.parseColor("#00E676"))
                .setRange(0, 100, 0)
                .setLineWidth(28)
                .setSpinDuration(1000)
                .build();

        final SeriesItem secondSeries = new SeriesItem.Builder(Color.parseColor("#FFC107"))
                .setRange(0, 100, 0)
                .setLineWidth(28)
                .setSpinDuration(1000)
                .build();

        final int series1Index = arcHours.addSeries(hourSeries);
        final int series2Index = arcMinutes.addSeries(minuteSeries);
        final int series3Index = arcSeconds.addSeries(secondSeries);

        arcHours.addEvent(new DecoEvent.Builder(100).setIndex(series1Index).setDelay(0).build());
        arcMinutes.addEvent(new DecoEvent.Builder(100).setIndex(series2Index).setDelay(0).build());
        arcSeconds.addEvent(new DecoEvent.Builder(100).setIndex(series3Index).setDelay(0).build());

        uiUpdated= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //This is the part where I get the timer value from the service and I update it every second, because I send the data from the service every second. The coundtdownTimer is a MenuItem
                long millisUntilFinished=intent.getExtras().getLong("countdown");
                setTimer(timerText, millisUntilFinished);

                long hours= millisUntilFinished/3600000;
                long minutes = (millisUntilFinished - (hours*3600000))/60000;
                long seconds = (millisUntilFinished - (hours*3600000)-(minutes*60000))/1000;

                arcHours.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 24) * hours)).setIndex(series1Index).setDelay(0).build());
                arcMinutes.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 60) * minutes)).setIndex(series2Index).setDelay(0).build());
                arcSeconds.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 60) * seconds)).setIndex(series3Index).setDelay(0).build());

            }
        };

        getActivity().startService(new Intent(getActivity(), ScheduleService.class));
        getActivity().registerReceiver(uiUpdated, new IntentFilter("COUNTDOWN_UPDATED"));

        Button smoke=(Button) view.findViewById(R.id.smoke);
        smoke.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                SharedPreferences p= PreferenceManager.getDefaultSharedPreferences(getActivity());
                                Firebase.setAndroidContext(getActivity());
                                final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
                                fire.child(p.getString("ID", null)).child("points").setValue(Long.parseLong(points) - 50);
                                p.edit().putLong("points", Long.parseLong(points) - 50);
                                SharedPreferences.Editor editor = p.edit();
                                editor.putLong("points", Long.parseLong(points) - 50);
                                // Commit the edits!
                                editor.commit();

                                DatabaseHandler dbh=new DatabaseHandler(getActivity());
                                MutableDateTime dt = new MutableDateTime(DateTimeZone.UTC);
                                dbh.addCigarette(new Cigarette(1, dt.toDate(), "PORCONE"));

                                Intent i = new Intent("SMOKE_OUTOFTIME");
                                i.putExtra("time", dt);
                                getActivity().sendBroadcast(i);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(uiUpdated);
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setTimer(TextView timerText,long millis){

        long hours= millis/3600000;
        long minutes = (millis - (hours*3600000))/60000;
        long seconds = (millis - (hours*3600000)-(minutes*60000))/1000;

        if(hours>=10){
            if(minutes>=10){
                if(seconds>=10){

                    timerText.setText(hours+":"+minutes+":"+seconds);
                }
                else{

                    timerText.setText(hours+":"+minutes+":0"+seconds);
                }
            }
            else{

                if(seconds>=10){

                    timerText.setText(hours+":0"+minutes+":"+seconds);
                }
                else{

                    timerText.setText(hours+":0"+minutes+":0"+seconds);
                }
            }
        }
        else{

            if(minutes>=10){
                if(seconds>=10){

                    timerText.setText("0"+hours+":"+minutes+":"+seconds);
                }
                else{

                    timerText.setText("0"+hours+":"+minutes+":0"+seconds);
                }
            }
            else{

                if(seconds>=10){

                    timerText.setText("0"+hours+":0"+minutes+":"+seconds);
                }
                else{

                    timerText.setText("0"+hours+":0"+minutes+":0"+seconds);
                }
            }
        }
    }

    private void smokeOrDont(){
        if(gain!=0) {
            gain=0;
        }
        if(getActivity().getIntent().getExtras()!=null) {
            gain = getActivity().getIntent().getExtras().getInt("points", 0);
            getActivity().getIntent().removeExtra("points");
        }

        if(gain!=0) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(getActivity());
                    Firebase.setAndroidContext(getActivity());
                    final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
                    long points=p.getLong("points",0);
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            gain=gain*2;
                            fire.child(p.getString("ID", null)).child("points").setValue(points + gain);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            fire.child(p.getString("ID", null)).child("points").setValue(points + gain);
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose")
                    .setMessage("You take the blue pill—the story ends, you wake up in your " +
                    "bed and believe whatever you want to believe. You take the red pill—you stay in " +
                    "Wonderland, and I show you how deep the rabbit hole goes")
                    .setPositiveButton("Don't smoke", dialogClickListener)
                    .setNegativeButton("smoke", dialogClickListener)
                    .setIcon(R.drawable.stopitsymbol)
                    .show();
        }
    }
}
