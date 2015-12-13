package it.polimi.stopit.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import it.polimi.stopit.R;
import it.polimi.stopit.adapters.AchievementRecyclerViewAdapter;
import it.polimi.stopit.model.Achievement;

public class AchievementFragment extends Fragment {

    private List<Achievement> mAchievements;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public AchievementFragment() {
    }

    public static Fragment newInstance() {
        Fragment fragment = new AchievementFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement_list, container, false);

        mAchievements=new ArrayList<>();

        String[] achievementsID=getResources().getStringArray(R.array.achievements_ids);
        String[] achievementsTit=getResources().getStringArray(R.array.achievements_title);
        String[] achievementsDesc=getResources().getStringArray(R.array.achievements_desc);
        String[] achievementsPoint=getResources().getStringArray(R.array.achievements_points);
        String[] achievementsObt=getResources().getStringArray(R.array.achievements_obtained);
        //String[] achievementsImg=getContext().getResources().getStringArray(R.array.achievements_images);

        for(int i=0;i<5;i++){

            Achievement tempAch=new Achievement();
            tempAch.setId(achievementsID[i]);
            tempAch.setTitle(achievementsTit[i]);
            tempAch.setDescription(achievementsDesc[i]);
            //tempAch.setPoints(Long.parseLong(achievementsPoint[i]));
            tempAch.setPoints(100);
            tempAch.setObtained(Boolean.parseBoolean(achievementsObt[i]));
            //tempAch.setImage(achievementsImg[i]);
            mAchievements.add(tempAch);
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new AchievementRecyclerViewAdapter(mAchievements, mListener));
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
