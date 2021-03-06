package it.polimi.stopit.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.joda.time.MutableDateTime;
import org.joda.time.MutableInterval;

import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.ChallengeDetail;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
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
                Picasso.with(context).load(snapshot.child(challenge.getOpponentID()).child("profilePic").getValue().toString()).into(holder.opponentImg);
                if(!challenge.isAccepted()) {

                    holder.challengeDuration.setText("Pending");
                    holder.daysRemaining.setText("");
                    holder.challengeProgress.setProgress(0);

                }else{

                    MutableInterval duration=new MutableInterval();
                    duration.setInterval(challenge.getStartTime(), challenge.getEndTime());
                    holder.challengeDuration.setText(String.valueOf(duration.toDuration().getStandardDays()));
                    MutableDateTime time=new MutableDateTime();
                    holder.challengeProgress.setProgress((int)(100 * (Float.valueOf(time.getMillis()-challenge.getStartTime()) / (challenge.getEndTime()-challenge.getStartTime()))));
                    holder.daysRemaining.setText("days left");
                }
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        public final View mView;
        public final TextView opponentName;
        public final TextView challengeDuration;
        public final TextView daysRemaining;
        public final CircularImageView opponentImg;
        public final ProgressBar challengeProgress;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            opponentName = (TextView) view.findViewById(R.id.opponent_name);
            challengeDuration = (TextView) view.findViewById(R.id.challenge_duration);
            daysRemaining = (TextView) view.findViewById(R.id.challenge_days_remaining);
            opponentImg = (CircularImageView) view.findViewById(R.id.opponent_image);
            challengeProgress=(ProgressBar)view.findViewById(R.id.challenge_progress);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setClickable(true);
        }

        @Override
        public String toString() {
            return super.toString() + " '";
        }

        @Override
        public void onClick(final View view) {

            final DatabaseHandler dbh = new DatabaseHandler(view.getContext());
            final Challenge challenge = dbh.getActiveChallengeByOpponentID(mChallenges.get(getLayoutPosition()).getOpponentID());
            final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

            if (!challenge.isAccepted()) {
                if (challenge.isChallenger()) {
                    Toast.makeText(context, "Wait for your opponent to respond", Toast.LENGTH_SHORT).show();
                } else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked

                                    Firebase.setAndroidContext(view.getContext());
                                    final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges");
                                    Firebase newChallenge = fire.push();

                                    MutableDateTime now = new MutableDateTime();

                                    challenge.setID(newChallenge.getKey());
                                    challenge.setAccepted(true);
                                    challenge.setStartTime(now.getMillis());
                                    challenge.setEndTime(now.getMillis() + challenge.getEndTime());

                                    newChallenge.setValue(challenge);
                                    newChallenge.child("ID").setValue(p.getString("ID", null));

                                    dbh.updateChallenge(challenge);
                                    mChallenges.set(getLayoutPosition(), challenge);
                                    notifyDataSetChanged();

                                    final Firebase accept = new Firebase("https://blazing-heat-3084.firebaseio.com/Accepted/" + challenge.getOpponentID());
                                    accept.child("accepted").setValue(newChallenge.getKey());

                                    Controller controller = new Controller(context);
                                    controller.setChallengeAlarm(challenge.getEndTime(),
                                            newChallenge.getKey());

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked

                                    dbh.deleteChallenge(mChallenges.get(getLayoutPosition()).getOpponentID());
                                    mChallenges.remove(getLayoutPosition());
                                    notifyDataSetChanged();

                                    final Firebase decline = new Firebase("https://blazing-heat-3084.firebaseio.com/Accepted/" + challenge.getOpponentID() );
                                    decline.child("declined").setValue(p.getString("ID",null));

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
            } else {

                Intent startDetail = new Intent(context, ChallengeDetail.class);
                startDetail.putExtra("opponentID", mChallenges.get(getLayoutPosition()).getOpponentID());
                context.startActivity(startDetail);

            }
        }

        @Override
        public boolean onLongClick(final View view){

            final DatabaseHandler dbh = new DatabaseHandler(view.getContext());
            final Challenge challenge = dbh.getChallengeByOpponentID(mChallenges.get(getLayoutPosition()).getOpponentID());

            if(challenge.isChallenger() && !challenge.isAccepted()) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                Firebase.setAndroidContext(view.getContext());
                                final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Notifications/"+challenge.getOpponentID()
                                        +"/"+challenge.getID());
                                fire.removeValue();
                                dbh.deleteChallenge(challenge.getID());
                                mChallenges.remove(getLayoutPosition());
                                notifyDataSetChanged();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Remove the invite of challenge?")
                        .setTitle("Cancel?")
                        .setPositiveButton("yes", dialogClickListener)
                        .setNegativeButton("no", dialogClickListener)
                        .show();
            }
            return true;
        }
    }
}
