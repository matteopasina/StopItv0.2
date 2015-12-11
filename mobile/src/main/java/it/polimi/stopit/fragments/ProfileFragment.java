package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import it.polimi.stopit.R;

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


    // TODO: Rename and change types of parameters
    private String name;
    private String surname;
    private String points;
    private String imageURL;


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
        usernameText.setText(""+name+" "+surname);

        TextView showPoints = (TextView) view.findViewById(R.id.points);
        showPoints.setText("Points:  " + points);

        CircularImageView profilepic=(CircularImageView) view.findViewById(R.id.profilepic);
        Picasso.with(getActivity().getApplicationContext()).load(imageURL).into(profilepic);

        DecoView arcView = (DecoView) view.findViewById(R.id.dynamicArcView);

        // Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build());

        //Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        arcView.addEvent(new DecoEvent.Builder(25).setIndex(series1Index).setDelay(4000).build());
        arcView.addEvent(new DecoEvent.Builder(100).setIndex(series1Index).setDelay(8000).build());
        arcView.addEvent(new DecoEvent.Builder(10).setIndex(series1Index).setDelay(12000).build());

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
}
