package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.polimi.stopit.R;
import it.polimi.stopit.model.User;

public class ContactFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private ArrayList<User> mContacts;
    ArrayList<String> facebookFriends=new ArrayList<>();
    JSONArray friends;

    public ContactFragment() {
    }

    public static Fragment newInstance() {
        Fragment fragment = new ContactFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        getmContacts();

        mContacts.add(new User("1", "Paulo", "Dybala", "http://scontent.cdninstagram.com/hphotos-xta1/t51.2885-19/s150x150/12139892_453222071547866_1052697760_a.jpg", Long.parseLong("10")));
        mContacts.add(new User("2", "Lionel", "Messi", "https://e120c7a329d82deabb254b6d6abcaeb74cd6f833.googledrive.com/host/0B3-zO2AfoiQjWXRqUVVUX19mdFk/players/Argentina/Lionel_MESSI.png", Long.parseLong("10")));
        mContacts.add(new User("3", "Eden", "Hazard", "http://img.uefa.com/imgml/TP/players/9/2013/324x324/1902160.jpg", Long.parseLong("10")));
        mContacts.add(new User("4", "Scarlett", "Johansson", "http://coolspotters.com/files/photos/1109436/scarlett-johansson-profile.png?1381189248", Long.parseLong("10")));
        mContacts.add(new User("5", "Guido", "Meda", "http://www.motocorse.com/foto/22762/thumbs500/1.jpg", Long.parseLong("10")));
        mContacts.add(new User("6", "Federica", "Nargi", "https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/s320x320/e15/11252786_483822848436098_1537023381_n.jpg", Long.parseLong("10")));
        mContacts.add(new User("7", "Alessandro", "Del Piero", "http://2.bp.blogspot.com/-dCln92KA_VY/T_2LuG2m5CI/AAAAAAAAA1o/Hpc9K0P8Jxo/s1600/Alessandro+Del+Piero-3.jpg", Long.parseLong("10")));
        mContacts.add(new User("8", "Gianluigi", "Buffon", "http://img.uefa.com/imgml/TP/players/14/2014/324x324/21307.jpg", Long.parseLong("10")));
        mContacts.add(new User("9", "James", "LeBron", "http://l1.yimg.com/bt/api/res/1.2/a3msGgStarpOr9C2Gaygnw--/YXBwaWQ9eW5ld3NfbGVnbztpbD1wbGFuZTtxPTc1O3c9NjAw/http://media.zenfs.com/en/person/Ysports/lebron-james-basketball-headshot-photo.jpg", Long.parseLong("10")));

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

    public void getmContacts(){

        mContacts=new ArrayList<>();

        /* make the API call for facebook friends*/
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {
                            friends = response.getJSONObject().getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(friends!=null){

                            for(int i=0;i<friends.length();i++){

                                JSONObject friend= null;

                                try {

                                    friend = friends.getJSONObject(i);
                                    facebookFriends.add(friend.getString("id"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            Firebase.setAndroidContext(getActivity().getApplicationContext());
                            final Firebase firebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

                            firebaseRef.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    for(String friend:facebookFriends) {

                                        User user = snapshot.child(friend).getValue(User.class);

                                        if(!mContacts.contains(user)) {

                                            mContacts.add(user);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }else{
                            Toast.makeText(getActivity(),"No one has already installed StopIt",Toast.LENGTH_LONG);
                        }

                    }
                }
        );

        request.executeAsync();

    }
}
