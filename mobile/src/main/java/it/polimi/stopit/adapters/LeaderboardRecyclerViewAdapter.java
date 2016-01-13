package it.polimi.stopit.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.model.User;

public class LeaderboardRecyclerViewAdapter extends RecyclerView.Adapter<LeaderboardRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<User> mLeaderboard;

    public LeaderboardRecyclerViewAdapter(ArrayList<User> leaderboard) {
        mLeaderboard = leaderboard;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mPosition.setText("" + (position + 1));

        Picasso.with(holder.mProfilePic.getContext()).load(mLeaderboard.get(position).getProfilePic()).into(holder.mProfilePic);

        holder.mName.setText(mLeaderboard.get(position).getName() + " " + mLeaderboard.get(position).getSurname());

        holder.mPoints.setText(""+mLeaderboard.get(position).getPoints());

        if(mLeaderboard.get(position).getID().equals(Profile.getCurrentProfile().getId())){

            holder.mView.findViewById(R.id.card_view).setBackgroundColor(Color.parseColor("#039BE5"));

        }else{

            holder.mView.findViewById(R.id.card_view).setBackgroundColor(Color.parseColor("#EEEEEE"));
        }

    }

    @Override
    public int getItemCount() {
        return mLeaderboard.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CircularImageView mProfilePic;
        public final TextView mName,mPoints,mPosition;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPosition = (TextView) view.findViewById(R.id.leaderboard_position);
            mProfilePic = (CircularImageView) view.findViewById(R.id.leaderboad_profilepic);
            mName = (TextView) view.findViewById(R.id.leaderboard_name);
            mPoints = (TextView) view.findViewById(R.id.leaderboard_points);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
