package it.polimi.stopit.fragments;

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
import it.polimi.stopit.adapters.LeaderboardRecyclerViewAdapter;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.User;

public class AllTimeLeaderboardFragment extends Fragment{

    User me;

    public AllTimeLeaderboardFragment() {

    }

    public static Fragment newInstance() {

        return new AllTimeLeaderboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard_list, container, false);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ArrayList<User> mLeaderboard = new DatabaseHandler(getActivity()).getAllContacts();
        me = new User(settings.getString("ID", null),settings.getString("name", null),settings.getString("surname", null),settings.getString("image", null),settings.getLong("points", 0),settings.getLong("dayPoints", 0),settings.getLong("weekPoints", 0),"","");
        mLeaderboard.add(me);

        mLeaderboard =new Controller(getActivity()).addTestContacts(mLeaderboard);

        // reorder the leaderboard
        Collections.sort(mLeaderboard, new LeaderComparator());

        for(User user: mLeaderboard){

            if(user.getID().equals(me.getID()) && mLeaderboard.indexOf(user)<10){

                Controller controller=new Controller(getActivity());

                controller.updateLeaderboardAchievement("top10");

                if(mLeaderboard.indexOf(user)<3){

                    controller.updateLeaderboardAchievement("top3");

                    if(mLeaderboard.indexOf(user)==0){

                        controller.updateLeaderboardAchievement("first");
                    }
                }
            }
        }

        // Set the adapter
        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            recyclerView.setAdapter(new LeaderboardRecyclerViewAdapter(mLeaderboard));
        }

        return view;
    }

    public static class LeaderComparator implements Comparator<User>{

        @Override
        public int compare(User contact1, User contact2) {

            return contact2.getPoints().compareTo(contact1.getPoints());

        }
    }
}
