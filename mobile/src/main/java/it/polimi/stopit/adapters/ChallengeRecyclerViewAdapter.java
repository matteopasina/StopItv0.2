package it.polimi.stopit.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.joda.time.MutableInterval;

import java.net.URI;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.fragments.ChallengeFragment;
import it.polimi.stopit.fragments.ChallengeFragment.OnListFragmentInteractionListener;
import it.polimi.stopit.fragments.ContactFragment;
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
                System.out.println("accepted: "+challenge.isAccepted());
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
            view.setOnClickListener(this);
            view.setClickable(true);
        }

        @Override
        public String toString() {
            return super.toString() + " '";
        }

        @Override
        public void onClick(final View view) {

            final DatabaseHandler dbh = new DatabaseHandler(view.getContext());
            final Challenge challenge = dbh.getChallenge(mChallenges.get(getLayoutPosition()).getOpponentID());
            if (!challenge.isAccepted()) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                System.out.println("yes button: " + mChallenges.get(getLayoutPosition()).getOpponentID());
                                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

                                Firebase.setAndroidContext(view.getContext());
                                final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges");
                                Firebase newChallenge = fire.push();

                                newChallenge.child("ID1").setValue(challenge.getOpponentID());
                                newChallenge.child("ID2").setValue(p.getString("ID", null));
                                newChallenge.child("Points1").setValue(0);
                                newChallenge.child("Points2").setValue(0);

                                challenge.setID(newChallenge.getKey());
                                challenge.setAccepted(true);
                                MutableDateTime now = new MutableDateTime();
                                challenge.setStartTime(now.getMillis());
                                challenge.setEndTime(now.getMillis() + challenge.getEndTime());

                                System.out.println(challenge.getID());
                                System.out.println(challenge.getOpponentID());
                                System.out.println("endtime: " + challenge.getEndTime());
                                System.out.println("startTime: " + challenge.getStartTime());

                                dbh.updateChallenge(challenge);
                                mChallenges.set(getLayoutPosition(),challenge);
                                notifyDataSetChanged();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked

                                dbh.deleteChallenge(mChallenges.get(getLayoutPosition()).getOpponentID());
                                mChallenges.remove(getLayoutPosition());
                                notifyDataSetChanged();

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Accept the challenge of " + opponentName.getText() + "?")
                        .setTitle("Challenge!")
                        .setPositiveButton("Accept", dialogClickListener)
                        .setNegativeButton("Refuse", dialogClickListener)
                        .show();

            }
        }
    }
}
