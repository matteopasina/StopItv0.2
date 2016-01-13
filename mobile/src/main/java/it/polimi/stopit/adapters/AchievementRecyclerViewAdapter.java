package it.polimi.stopit.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.model.Achievement;


public class AchievementRecyclerViewAdapter extends RecyclerView.Adapter<AchievementRecyclerViewAdapter.ViewHolder> {

    private final List<Achievement> mAchievements;

    public AchievementRecyclerViewAdapter(List<Achievement> items) {

        mAchievements = items;
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

        holder.setIsRecyclable(false);
        holder.achievPic.setImageResource(achievement.getImage());

        if(!achievement.isObtained()) {

            holder.achievPic.setImageAlpha(40);
            //holder.achievPic.setImageResource(R.drawable.locked);
            holder.achievDesc.setTextColor(Color.parseColor("#AAAAAA"));
            holder.achievTitle.setTextColor(Color.parseColor("#999999"));
            holder.achievPoints.setTextColor(Color.parseColor("#999999"));

        }

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
        public final ImageView achievPic;
        public final TextView achievTitle;
        public final TextView achievDesc;
        public final TextView achievPoints;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            achievPic=(ImageView) view.findViewById(R.id.achievement_pic);
            achievTitle= (TextView) view.findViewById(R.id.achiev_title);
            achievDesc= (TextView) view.findViewById(R.id.achiev_description);
            achievPoints= (TextView) view.findViewById(R.id.achiev_points);
        }
    }


}
