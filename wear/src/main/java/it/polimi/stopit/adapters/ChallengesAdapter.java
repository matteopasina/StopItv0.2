package it.polimi.stopit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.database.DatabaseHandlerWear;
import it.polimi.stopit.model.Challenge;
import it.polimi.stopit.model.User;

/**
 * Created by matteo on 24/01/16.
 */
public class ChallengesAdapter extends WearableListView.Adapter {

    private final ArrayList<Challenge> mChallenges;
    private final Context context;
    private final LayoutInflater mInflater;
    private DatabaseHandlerWear db;

    public ChallengesAdapter(Context context, ArrayList<Challenge> challenges) {
        mChallenges = challenges;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView me, points, opponent;
        private CircularImageView mypic, opponentPic;
        private ImageView vs;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            me = (TextView) itemView.findViewById(R.id.me);
            mypic=(CircularImageView) itemView.findViewById(R.id.mypic);
            opponentPic=(CircularImageView) itemView.findViewById(R.id.opponentPic);
            points = (TextView) itemView.findViewById(R.id.mypoints);
            opponent = (TextView) itemView.findViewById(R.id.opponent);
            vs = (ImageView) itemView.findViewById(R.id.VS);
        }
    }

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // Inflate our custom layout for list items
        return new ItemViewHolder(mInflater.inflate(R.layout.challenge_item, null));
    }

    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {

        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        Challenge challenge = mChallenges.get(position);

        db=new DatabaseHandlerWear(context);
        User opponent=db.getContact(challenge.getOpponentID());

        int myImageResource = context.getResources().getIdentifier("io", "drawable", context.getPackageName());
        itemHolder.mypic.setImageResource(myImageResource);

        int opponentImageResource = context.getResources().getIdentifier(opponent.getProfilePic(), "drawable", context.getPackageName());
        itemHolder.opponentPic.setImageResource(opponentImageResource);

        // replace text contents
        itemHolder.opponent.setText(opponent.getName());

        long difference=challenge.getMyPoints()-challenge.getOpponentPoints();
        itemHolder.points.setText("" + difference);

        if(difference > 0){
            itemHolder.points.setTextColor(Color.GREEN);
        }else{
            itemHolder.points.setTextColor(Color.RED);
        }


        // replace list item's metadata
        holder.itemView.setTag(position);
    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {
        try {
            return mChallenges.size();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
