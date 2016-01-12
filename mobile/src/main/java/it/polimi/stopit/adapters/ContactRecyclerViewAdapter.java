package it.polimi.stopit.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.fragments.ContactFragment.OnListFragmentInteractionListener;
import it.polimi.stopit.model.Challenge;
import it.polimi.stopit.model.User;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private final List<User> mContacts;

    public ContactRecyclerViewAdapter(ArrayList<User> contacts, OnListFragmentInteractionListener listener) {
        mContacts = contacts;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Picasso.with(holder.mProfilePic.getContext()).load(mContacts.get(position).getProfilePic()).into(holder.mProfilePic);

        holder.mName.setText(mContacts.get(position).getName() + " " + mContacts.get(position).getSurname());

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public final CircularImageView mProfilePic;
        public final TextView mName;



        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProfilePic = (CircularImageView) view.findViewById(R.id.contact_profilepic);
            mName = (TextView) view.findViewById(R.id.contact_name);
            view.setOnClickListener(this);
            view.setClickable(true);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }

        @Override
        public void onClick(final View view) {
            DatabaseHandler dbh=new DatabaseHandler(mView.getContext());
            dbh.getAllChallengesNotOver();
            boolean singleChallenge=true;
            for(Challenge challenge : dbh.getAllChallengesNotOver()){
                if(challenge.getOpponentID().equals(mContacts.get(getLayoutPosition()).getID()))
                {
                    singleChallenge=false;
                    Toast.makeText(mView.getContext(), "You already have an active challenge with this contact", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if(singleChallenge) {
                final View dialogView = View.inflate(view.getContext(), R.layout.dialog_challenge, null);
                TextView messageDialog = (TextView) dialogView.findViewById(R.id.message_challenge);
                messageDialog.setText("You are challenging " + mName.getText() + "!" + "\nSet the days of the challenge:");

                CircularImageView opponent = (CircularImageView) dialogView.findViewById(R.id.opponent);
                Picasso.with(view.getContext()).load(mContacts.get(getLayoutPosition()).getProfilePic()).into(opponent);

                final SeekBar days = (SeekBar) dialogView.findViewById(R.id.days);
                days.setProgress(1);

                final TextView daysText = (TextView) dialogView.findViewById(R.id.days_challenge);
                daysText.setText("1");

                days.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        daysText.setText(String.valueOf(days.getProgress()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                DatabaseHandler dbh = new DatabaseHandler(view.getContext());

                                final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getContext());

                                Firebase.setAndroidContext(view.getContext());
                                final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Notifications/" + mContacts.get(getLayoutPosition()).getID());

                                Firebase challenge = fire.push();

                                dbh.addChallenge(new Challenge(mContacts.get(getLayoutPosition()).getID()
                                        , mContacts.get(getLayoutPosition()).getID(), 0, 0, 0,
                                        (long) days.getProgress() * 86400000, "false", "true", "false", "false"));

                                challenge.child("duration").setValue(days.getProgress());
                                challenge.child("opponent").setValue(settings.getString("ID", null));

                                Intent createChallenge = new Intent(view.getContext(), NavigationActivity.class);
                                createChallenge.putExtra("ID", mContacts.get(getLayoutPosition()).getID());
                                createChallenge.putExtra("length_days", days.getProgress());
                                view.getContext().startActivity(createChallenge);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(dialogView)
                        .setTitle("Challenge!")
                        .setPositiveButton("Challenge", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener)
                        .show();
            }
        }
    }
}
