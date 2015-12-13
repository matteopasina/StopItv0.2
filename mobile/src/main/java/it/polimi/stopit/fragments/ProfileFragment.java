package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import it.polimi.stopit.R;
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

    private static final String ARG_NAME = "name";
    private static final String ARG_SURNAME = "surname";
    private static final String ARG_POINTS = "points";
    private static final String ARG_IMAGE = "imageURL";
    private static final String PREFS_NAME = "StopItPrefs";


    // TODO: Rename and change types of parameters
    private String name;
    private String surname;
    private String points;
    private String imageURL;
    private int hFirst,hLast,mFirst,mLast,CPD;
    private BroadcastReceiver uiUpdated;


    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String name, String surname, String points,String imageURL) {
        Fragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
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
            name = getArguments().getString(ARG_NAME);
            surname = getArguments().getString(ARG_SURNAME);
            points = getArguments().getString(ARG_POINTS);
            imageURL = getArguments().getString(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView usernameText = (TextView) view.findViewById(R.id.username);
        usernameText.setText("" + name + " " + surname);

        TextView showPoints = (TextView) view.findViewById(R.id.points);
        showPoints.setText("Points:  " + points);

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
                .setLineWidth(28f)
                .build());

        arcMinutes.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setSpinDuration(500)
                .setLineWidth(28f)
                .build());

        arcSeconds.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setSpinDuration(500)
                .setLineWidth(28f)
                .build());

        //Create data series track
        SeriesItem hourSeries = new SeriesItem.Builder(Color.argb(255, 64, 196, 218))
                .setRange(0, 200, 0)
                .setLineWidth(28f)
                .build();

        SeriesItem minuteSeries = new SeriesItem.Builder(Color.argb(255, 41, 121, 255))
                .setRange(0, 100, 0)
                .setLineWidth(28f)
                .build();

        SeriesItem secondSeries = new SeriesItem.Builder(Color.argb(255, 232, 239, 42))
                .setRange(0, 100, 0)
                .setLineWidth(28f)
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

                if(minutes==0 && hours>0){
                    arcMinutes.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                            .setIndex(100)
                            .setDelay(0)
                            .setDuration(500)
                            .build());

                }
                else if(seconds==0 && minutes>0){
                    arcSeconds.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                            .setIndex(100)
                            .setDelay(0)
                            .setDuration(500)
                            .build());
                }
                else {

                    arcHours.addEvent(new DecoEvent.Builder((((float) 100 / 24) * hours)).setIndex(series1Index).setDelay(0).build());
                    arcMinutes.addEvent(new DecoEvent.Builder((((float) 100 / 60) * minutes)).setIndex(series2Index).setDelay(0).build());
                    arcSeconds.addEvent(new DecoEvent.Builder((((float) 100 / 60) * seconds)).setIndex(series3Index).setDelay(0).build());
                }
            }
        };

        getActivity().startService(new Intent(getActivity(), ScheduleService.class));
        getActivity().registerReceiver(uiUpdated, new IntentFilter("COUNTDOWN_UPDATED"));
        //Log.d("SERVICE", "STARTED!");

        /*new CountDownTimer(scheduleProgram(),1000){

            public void onTick(long millisUntilFinished) {

                setTimer(timerText,millisUntilFinished);

                long hours= millisUntilFinished/3600000;
                long minutes = (millisUntilFinished - (hours*3600000))/60000;
                long seconds = (millisUntilFinished - (hours*3600000)-(minutes*60000))/1000;

                arcHours.addEvent(new DecoEvent.Builder((((float) 100 / 24) * hours)).setIndex(series1Index).setDelay(0).build());
                arcMinutes.addEvent(new DecoEvent.Builder((((float) 100 / 60) * minutes)).setIndex(series2Index).setDelay(0).build());
                arcSeconds.addEvent(new DecoEvent.Builder((((float) 100 / 60) * seconds)).setIndex(series3Index).setDelay(0).build());
            }

            public void onFinish() {
                sendNotification();
                this.start();
            }

        }.start();

        /*Button smoke=(Button)findViewById(R.id.smoke);
        smoke.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                points -= 50;
                myFirebaseRef.child(user.getID()).child("points").setValue(points);
            }
        });*/

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
}
