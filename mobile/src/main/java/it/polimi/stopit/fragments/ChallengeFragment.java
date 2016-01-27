package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.NewChallengeActivity;
import it.polimi.stopit.adapters.ChallengeRecyclerViewAdapter;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Challenge;

public class ChallengeFragment extends Fragment {

    public ChallengeFragment() {
    }

    public static Fragment newInstance() {

        return new ChallengeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_list, container, false);

        List<Challenge> mChallenges = new DatabaseHandler(getActivity()).getActiveChallenges();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_challenge);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), NewChallengeActivity.class);
                getActivity().startActivity(intent);

            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listChallenge);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.setAdapter(new ChallengeRecyclerViewAdapter(mChallenges, getActivity()));

        recyclerView.setHasFixedSize(true);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
