package it.polimi.stopit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.stopit.R;
import it.polimi.stopit.adapters.WeeklyLeaderboardAdapter;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.User;

/**
 * Created by alessiorossotti on 30/12/15.
 */
public class WeeklyLeaderboardFragment extends Fragment{

    private ArrayList<User> mLeaderboard;
    private DatabaseHandler db;

    public WeeklyLeaderboardFragment() {

    }

    public static Fragment newInstance() {
        Fragment fragment = new WeeklyLeaderboardFragment();

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

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mLeaderboard=db.getAllContacts();
        User me = new User(settings.getString("ID",null),settings.getString("name", null),settings.getString("surname", null),settings.getString("image", null),settings.getLong("points", 0),settings.getLong("dayPoints", 0),settings.getLong("weekPoints", 0),"","");
        mLeaderboard.add(me);

        Controller controller=new Controller(getActivity());

        mLeaderboard=controller.addTestContacts(mLeaderboard);

        // reorder the leaderboard
        Collections.sort(mLeaderboard, new LeaderComparator());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new WeeklyLeaderboardAdapter(mLeaderboard));
        }

        return view;
    }

    public static class LeaderComparator implements Comparator<User> {

        @Override
        public int compare(User contact1, User contact2) {

            return contact2.getWeekPoints().compareTo(contact1.getWeekPoints());

        }
    }
}
