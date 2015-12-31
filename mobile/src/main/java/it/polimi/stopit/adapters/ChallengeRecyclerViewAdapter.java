package it.polimi.stopit.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.joda.time.MutableInterval;

import java.net.URI;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.fragments.ChallengeFragment.OnListFragmentInteractionListener;
import it.polimi.stopit.model.Challenge;

public class ChallengeRecyclerViewAdapter extends RecyclerView.Adapter<ChallengeRecyclerViewAdapter.ViewHolder> {

    private List<Challenge> mChallenges;
    private Context context;

    public ChallengeRecyclerViewAdapter(List<Challenge> challenges, Context context) {
        mChallenges = challenges;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_challenge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Challenge challenge=mChallenges.get(position);
        Firebase.setAndroidContext(context);
        final Firebase fireRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
        fireRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                holder.opponentName.setText(snapshot.child(challenge.getOpponentID()).child("name").getValue().toString());
                Picasso.with(context).load(snapshot.child(challenge.getOpponentID()).child("profilePic").getValue().toString())
                        .into(holder.opponentImg);

                if(!challenge.isAccepted()) {

                    holder.challengeDuration.setText("Pending");

                }else{

                    MutableInterval duration=new MutableInterval();
                    duration.setInterval(challenge.getStartTime(), challenge.getEndTime());
                    holder.challengeDuration.setText(String.valueOf(duration.toDuration().getStandardDays()));
                }
                holder.challengeProgress.setProgress(0);

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return mChallenges.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public final TextView opponentName;
        public final TextView challengeDuration;
        public final TextView daysRemaining;
        public final ImageView opponentImg;
        public final ProgressBar challengeProgress;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            opponentName = (TextView) view.findViewById(R.id.opponent_name);
            challengeDuration = (TextView) view.findViewById(R.id.challenge_duration);
            daysRemaining = (TextView) view.findViewById(R.id.challenge_days_remaining);
            opponentImg = (ImageView) view.findViewById(R.id.opponent_image);
            challengeProgress=(ProgressBar)view.findViewById(R.id.challenge_progress);
        }

        @Override
        public String toString() {
            return super.toString() + " '";
        }

        @Override
        public void onClick(final View view) {

        }
    }
}
