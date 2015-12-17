package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.AddMoneyTargetActivity;

public class MoneyFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public MoneyFragment() {
        // Required empty public constructor
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

        View view = inflater.inflate(R.layout.fragment_money, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),AddMoneyTargetActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

    }
}
