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

import java.util.ArrayList;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.NewChallengeActivity;
import it.polimi.stopit.adapters.ChallengeRecyclerViewAdapter;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Challenge;

public class ChallengeFragment extends Fragment {

    private List<Challenge> mChallenges;
    private OnListFragmentInteractionListener mListener;
    private DatabaseHandler db;
    private FloatingActionButton fab;

    public ChallengeFragment() {
    }

    public static Fragment newInstance() {
        Fragment fragment = new ChallengeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_list, container, false);

        mChallenges=new ArrayList<>();
        db=new DatabaseHandler(getActivity());
        mChallenges=db.getActiveChallenges();

        fab = (FloatingActionButton) view.findViewById(R.id.add_challenge);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), NewChallengeActivity.class);
                getActivity().startActivity(intent);

            }
        });

        // Set the adapter

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listChallenge);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(new ChallengeRecyclerViewAdapter(mChallenges, getActivity()));

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
}
