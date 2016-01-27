package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.polimi.stopit.OnPassingData;
import it.polimi.stopit.R;
import it.polimi.stopit.adapters.MoneyRecyclerViewAdapter;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.MoneyTarget;

public class MoneyGalleryFragment extends Fragment {

    private OnPassingData myListener;

    public MoneyGalleryFragment() {
    }

    public static Fragment newInstance() {

        return new MoneyGalleryFragment();
    }

    public void registerActivity(OnPassingData activity) {
        myListener = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moneygallery_list, container, false);

        List<MoneyTarget> mTargets = new DatabaseHandler(getActivity()).getAllCategories();

        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(new MoneyRecyclerViewAdapter(mTargets, myListener));

            recyclerView.setHasFixedSize(true);
        }
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
