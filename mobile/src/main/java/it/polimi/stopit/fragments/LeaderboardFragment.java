package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.stopit.R;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.User;

public class LeaderboardFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private ArrayList<User> mLeaderboard;
    private DatabaseHandler db;
    public static final String PREFS_NAME = "StopItPrefs";

    public LeaderboardFragment() {
    }

    public static Fragment newInstance() {
        Fragment fragment = new LeaderboardFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard_list, container, false);

        mLeaderboard=new ArrayList<>();
        db=new DatabaseHandler(getActivity());
        mLeaderboard=db.getAllContacts();

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        User me = new User();
        me.setID(settings.getString("ID", null));
        me.setName(settings.getString("name", null));
        me.setSurname(settings.getString("surname", null));
        me.setPoints(settings.getLong("points", 0));
        me.setProfilePic(settings.getString("image", null));
        mLeaderboard.add(me);

        // reorder the leaderboard
        Collections.sort(mLeaderboard,new leaderComparator());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new LeaderboardRecyclerViewAdapter(mLeaderboard, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {

    }

    public class leaderComparator implements Comparator<User>{

        @Override
        public int compare(User contact1, User contact2) {

            return contact2.getPoints().compareTo(contact1.getPoints());

        }
    }
}
