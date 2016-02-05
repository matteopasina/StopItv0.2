package it.polimi.stopit.adapters;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    int i=0;

    public LeaderboardAdapter(Context context, ArrayList<User> leaderboard) {
        mLeaderboard = leaderboard;
        this.context=context;
        mInflater = LayoutInflater.from(context);
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView name,points,position;
        private ImageView image;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            name = (TextView) itemView.findViewById(R.id.name);
            image=(ImageView) itemView.findViewById(R.id.circle);
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

        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView name = itemHolder.name;
        TextView points = itemHolder.points;
        TextView myposition = itemHolder.position;
        ImageView circle=itemHolder.image;

        // replace text contents
        name.setText(mLeaderboard.get(position).getName());
        points.setText(mLeaderboard.get(position).getPoints().toString());
        myposition.setText(""+(position+1));
        //Bitmap bmp = BitmapFactory.decodeByteArray(mLeaderboard.get(position).getImg(), 0, mLeaderboard.get(position).getImg().length);
        //circle.setImageBitmap(bmp);

        // replace list item's metadata
        holder.itemView.setTag(position);

        holder.setIsRecyclable(false);

    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {

        try{
            return mLeaderboard.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
