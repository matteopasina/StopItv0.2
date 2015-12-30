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
import it.polimi.stopit.adapters.ContactRecyclerViewAdapter;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.User;

public class ContactFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private DatabaseHandler db;
    private ArrayList<User> mContacts;

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

        db=new DatabaseHandler(getActivity());
        mContacts=db.getAllContacts();

        mContacts.add(new User("1", "Paulo", "Dybala", "http://scontent.cdninstagram.com/hphotos-xta1/t51.2885-19/s150x150/12139892_453222071547866_1052697760_a.jpg", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));
        mContacts.add(new User("2", "Lionel", "Messi", "https://e120c7a329d82deabb254b6d6abcaeb74cd6f833.googledrive.com/host/0B3-zO2AfoiQjWXRqUVVUX19mdFk/players/Argentina/Lionel_MESSI.png", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));
        mContacts.add(new User("3", "Eden", "Hazard", "http://img.uefa.com/imgml/TP/players/9/2013/324x324/1902160.jpg", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));
        mContacts.add(new User("4", "Scarlett", "Johansson", "http://coolspotters.com/files/photos/1109436/scarlett-johansson-profile.png?1381189248", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));
        mContacts.add(new User("5", "Guido", "Meda", "http://www.motocorse.com/foto/22762/thumbs500/1.jpg", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));
        mContacts.add(new User("6", "Federica", "Nargi", "https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/s320x320/e15/11252786_483822848436098_1537023381_n.jpg", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));
        mContacts.add(new User("7", "Alessandro", "Del Piero", "http://2.bp.blogspot.com/-dCln92KA_VY/T_2LuG2m5CI/AAAAAAAAA1o/Hpc9K0P8Jxo/s1600/Alessandro+Del+Piero-3.jpg", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));
        mContacts.add(new User("8", "Gianluigi", "Buffon", "http://img.uefa.com/imgml/TP/players/14/2014/324x324/21307.jpg", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));
        mContacts.add(new User("9", "James", "LeBron", "http://l1.yimg.com/bt/api/res/1.2/a3msGgStarpOr9C2Gaygnw--/YXBwaWQ9eW5ld3NfbGVnbztpbD1wbGFuZTtxPTc1O3c9NjAw/http://media.zenfs.com/en/person/Ysports/lebron-james-basketball-headshot-photo.jpg", Long.parseLong("10"),Long.parseLong("100"),Long.parseLong("100")));

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new ContactRecyclerViewAdapter(mContacts, mListener));
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
