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
import android.widget.Toast;

import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.AddMoneyTargetActivity;
import it.polimi.stopit.adapters.MoneyTargetsAdapter;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.MoneyTarget;

public class MoneyFragment extends Fragment {

    private ArrayList<MoneyTarget> mTargets;
    private DatabaseHandler db;
    private FloatingActionButton fab;

    public MoneyFragment() {

    }

    public static Fragment newInstance() {
        Fragment fragment = new MoneyFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_moneytarget_list, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DatabaseHandler db=new DatabaseHandler(getActivity());
                if(!db.targetAlreadyInProgress()){

                    Intent intent = new Intent(getActivity(),AddMoneyTargetActivity.class);
                    getActivity().startActivity(intent);

                }else{

                    Toast.makeText(getActivity(), "Complete your current targets before adding a new one", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mTargets=new ArrayList<>();
        db=new DatabaseHandler(getActivity().getApplicationContext());
        mTargets=db.getAllTargets();

        // Set the adapter

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new MoneyTargetsAdapter(mTargets,getActivity()));


        return view;
    }

}
