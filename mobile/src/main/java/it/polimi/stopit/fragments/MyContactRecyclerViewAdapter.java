package it.polimi.stopit.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.fragments.ContactFragment.OnListFragmentInteractionListener;
import it.polimi.stopit.model.User;

public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private final List<User> mContacts;

    public MyContactRecyclerViewAdapter(ArrayList<User> contacts, OnListFragmentInteractionListener listener) {
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

        holder.mUser = mContacts.get(position);

        Picasso.with(holder.mProfilePic.getContext()).load(mContacts.get(position).getProfilePic()).into(holder.mProfilePic);

        holder.mName.setText(mContacts.get(position).getName()+" "+mContacts.get(position).getSurname());

        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mUser);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CircularImageView mProfilePic;
        public final TextView mName;
        public User mUser;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProfilePic = (CircularImageView) view.findViewById(R.id.contact_profilepic);
            mName = (TextView) view.findViewById(R.id.contact_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
