package it.polimi.stopit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.model.User;

/**
 * Created by matteo on 15/01/16.
 */
public class LeaderboardAdapter extends WearableListView.Adapter {

    private final ArrayList<User> mLeaderboard;
    private final Context context;
    private final LayoutInflater mInflater;
    int i = 0;

    public LeaderboardAdapter(Context context, ArrayList<User> leaderboard) {
        mLeaderboard = leaderboard;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView name, points, position;
        private CircularImageView image;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            name = (TextView) itemView.findViewById(R.id.name);
            image = (CircularImageView) itemView.findViewById(R.id.circle);
            points = (TextView) itemView.findViewById(R.id.points);
            position = (TextView) itemView.findViewById(R.id.position);
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

        User user = mLeaderboard.get(position);

        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView name = itemHolder.name;
        TextView points = itemHolder.points;
        TextView myposition = itemHolder.position;
        CircularImageView circle = itemHolder.image;

        // replace text contents
        name.setText(user.getName());
        points.setText(user.getPoints().toString());
        myposition.setText("" + (position + 1));

        int imageResource = context.getResources().getIdentifier(user.getProfilePic(), "drawable", context.getPackageName());
        circle.setImageResource(imageResource);


        // replace list item's metadata
        holder.itemView.setTag(position);

        holder.setIsRecyclable(false);

    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {

        try {
            return mLeaderboard.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
