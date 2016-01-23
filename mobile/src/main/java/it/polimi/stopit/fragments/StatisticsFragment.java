package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.stopit.R;
import it.polimi.stopit.controller.Controller;

public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {

    }

    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        Controller control=new Controller(getActivity());
        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(getActivity());

        TextView totalPoints=(TextView) view.findViewById(R.id.total_points);
        totalPoints.setText(settings.getLong("points",0)+" total points");

        TextView daysToRed=(TextView) view.findViewById(R.id.days_reduce);
        daysToRed.setText(settings.getInt("daysToRed",0)+" days to stop");

        TextView moneySaved=(TextView) view.findViewById(R.id.money_saved);
        int monSaved=control.getMoneySaved();
        moneySaved.setText(monSaved/100+"."+monSaved%100+" € saved");

        TextView cigAvoided=(TextView) view.findViewById(R.id.cig_avoided);
        cigAvoided.setText(control.getCigAvoided()+" cigarettes avoided");

        TextView challengeWon=(TextView) view.findViewById(R.id.challenge_won);
        challengeWon.setText(control.challengeWonLost()+" challenge won");

        TextView moneyTargetCompleted=(TextView) view.findViewById(R.id.moneytarget_completed);
        moneyTargetCompleted.setText(settings.getInt("moneytargetcompleted",0)+" money target completed");

        return view;

    }

}
