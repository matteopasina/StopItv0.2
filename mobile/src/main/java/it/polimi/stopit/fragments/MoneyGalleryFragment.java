package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.adapters.MoneyRecyclerViewAdapter;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.MoneyTarget;

public class MoneyGalleryFragment extends Fragment {

    private List<MoneyTarget> mTargets;
    private OnListFragmentInteractionListener mListener;
    private DatabaseHandler db;

    public MoneyGalleryFragment() {
    }

    public static Fragment newInstance(int columnCount) {
        Fragment fragment = new MoneyGalleryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moneygallery_list, container, false);

        mTargets=new ArrayList<>();
        db=new DatabaseHandler(getActivity());
        mTargets=db.getAllCategories();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(new MoneyRecyclerViewAdapter(mTargets, mListener));
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
}
