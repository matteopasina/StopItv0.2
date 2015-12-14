package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.model.User;

public class ContactFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private ArrayList<User> mContacts;

    public ContactFragment() {
    }

    public static ContactFragment newInstance(int columnCount) {
        ContactFragment fragment = new ContactFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    /*
        ArrayList<String> facebookFriends=new ArrayList<>();
        final JSONArray friends;
        /* make the API call for facebook friends
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/{friend-list-id}",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        friends = response.getJSONArray();

                        for(int i =0;i< friends.length();i++){

                            JSONObject friend;
                            try {
                                friend = friends.getJSONObject(i);

                            }catch (JSONException e){}

                            facebookFriends.add(friend.getString("id"));
                        }
                    }
                }
        ).executeAsync();

        Firebase.setAndroidContext(getActivity().getApplicationContext());
        final Firebase firebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //snapshot.child()
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new MyContactRecyclerViewAdapter(mContacts, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {

    }
}
