package it.polimi.stopit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import it.polimi.stopit.R;
import it.polimi.stopit.fragments.AchievementFragment.OnListFragmentInteractionListener;
import it.polimi.stopit.model.Achievement;

import java.util.List;


public class AchievementRecyclerViewAdapter extends RecyclerView.Adapter<AchievementRecyclerViewAdapter.ViewHolder> {

    private final List<Achievement> mAchievements;
    private final OnListFragmentInteractionListener mListener;

    public AchievementRecyclerViewAdapter(List<Achievement> items, OnListFragmentInteractionListener listener) {
        mAchievements = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_achievement, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Achievement achievement=mAchievements.get(position);

        holder.achievTitle.setText(achievement.getTitle());
        holder.achievDesc.setText(achievement.getDescription());
        holder.achievPoints.setText(""+achievement.getPoints());
    }

    @Override
    public int getItemCount() {
        return mAchievements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final CircularImageView achievPic;
        public final TextView achievTitle;
        public final TextView achievDesc;
        public final TextView achievPoints;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            achievPic=(CircularImageView) view.findViewById(R.id.achievement_pic);
            achievTitle= (TextView) view.findViewById(R.id.achiev_title);
            achievDesc= (TextView) view.findViewById(R.id.achiev_description);
            achievPoints= (TextView) view.findViewById(R.id.achiev_points);
        }
    }
}
