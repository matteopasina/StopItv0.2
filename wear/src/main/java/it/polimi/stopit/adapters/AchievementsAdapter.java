package it.polimi.stopit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.model.Achievement;

/**
 * Created by matteo on 24/01/16.
 */
public class AchievementsAdapter extends WearableListView.Adapter {

    private final ArrayList<Achievement> mAchievements;
    private final Context context;
    private final LayoutInflater mInflater;

    public AchievementsAdapter(Context context, ArrayList<Achievement> achievements) {
        mAchievements = achievements;
        this.context=context;
        mInflater = LayoutInflater.from(context);
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView title,points;
        private ImageView image;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            title = (TextView) itemView.findViewById(R.id.name);
            image=(ImageView) itemView.findViewById(R.id.circle);
            points=(TextView) itemView.findViewById(R.id.points);
        }
    }

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // Inflate our custom layout for list items
        return new ItemViewHolder(mInflater.inflate(R.layout.list_item, null));
    }

    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {

        holder.setIsRecyclable(false);

        Achievement achievement=mAchievements.get(position);

        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView title = itemHolder.title;
        TextView points=itemHolder.points;
        ImageView circle=itemHolder.image;

        circle.setImageResource(achievement.getImage());

        if(!achievement.isObtained()) {
            circle.setImageResource(R.drawable.locked);
        }

        title.setText(achievement.getTitle());
        points.setText("" + achievement.getPoints());

        // replace list item's metadata
        holder.itemView.setTag(position);
    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {
        return mAchievements.size();
    }
}
