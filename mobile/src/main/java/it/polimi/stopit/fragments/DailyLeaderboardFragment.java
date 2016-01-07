package it.polimi.stopit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.stopit.R;
import it.polimi.stopit.adapters.DailyLeaderboardAdapter;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.User;

/**
 * Created by alessiorossotti on 30/12/15.
 */
public class DailyLeaderboardFragment extends Fragment{

    private ArrayList<User> mLeaderboard;
    private DatabaseHandler db;

    public DailyLeaderboardFragment() {

    }

    public static Fragment newInstance() {
        Fragment fragment = new WeeklyLeaderboardFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard_list, container, false);

        mLeaderboard=new ArrayList<>();
        db=new DatabaseHandler(getActivity());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mLeaderboard=db.getAllContacts();
        User me = new User(settings.getString("ID",null),settings.getString("name", null),settings.getString("surname", null),settings.getString("image", null),settings.getLong("points", 0),settings.getLong("dayPoints", 0),settings.getLong("weekPoints", 0));
        mLeaderboard.add(me);
        mLeaderboard.add(new User("1", "Paulo", "Dybala", "http://scontent.cdninstagram.com/hphotos-xta1/t51.2885-19/s150x150/12139892_453222071547866_1052697760_a.jpg", Long.parseLong("2110"),Long.parseLong("780"),Long.parseLong("575")));
        mLeaderboard.add(new User("2", "Lionel", "Messi", "https://e120c7a329d82deabb254b6d6abcaeb74cd6f833.googledrive.com/host/0B3-zO2AfoiQjWXRqUVVUX19mdFk/players/Argentina/Lionel_MESSI.png", Long.parseLong("5670"),Long.parseLong("3200"),Long.parseLong("480")));
        mLeaderboard.add(new User("3", "Eden", "Hazard", "http://img.uefa.com/imgml/TP/players/9/2013/324x324/1902160.jpg", Long.parseLong("1920"),Long.parseLong("900"),Long.parseLong("200")));
        mLeaderboard.add(new User("4", "Scarlett", "Johansson", "http://coolspotters.com/files/photos/1109436/scarlett-johansson-profile.png?1381189248", Long.parseLong("4560"),Long.parseLong("1200"),Long.parseLong("-50")));
        mLeaderboard.add(new User("5", "Guido", "Meda", "http://www.motocorse.com/foto/22762/thumbs500/1.jpg", Long.parseLong("8800"),Long.parseLong("560"),Long.parseLong("340")));
        mLeaderboard.add(new User("6", "Federica", "Nargi", "https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/s320x320/e15/11252786_483822848436098_1537023381_n.jpg", Long.parseLong("1267"),Long.parseLong("650"),Long.parseLong("200")));
        mLeaderboard.add(new User("7", "Alessandro", "Del Piero", "http://2.bp.blogspot.com/-dCln92KA_VY/T_2LuG2m5CI/AAAAAAAAA1o/Hpc9K0P8Jxo/s1600/Alessandro+Del+Piero-3.jpg", Long.parseLong("880"),Long.parseLong("700"),Long.parseLong("180")));
        mLeaderboard.add(new User("8", "Gianluigi", "Buffon", "http://img.uefa.com/imgml/TP/players/14/2014/324x324/21307.jpg", Long.parseLong("3450"),Long.parseLong("1000"),Long.parseLong("80")));
        mLeaderboard.add(new User("9", "Stephen", "Curry", "http://www.sportsspeakers360.com/admin/img/stephen-curry.jpg", Long.parseLong("1200"),Long.parseLong("150"),Long.parseLong("-100")));
        // reorder the leaderboard
        Collections.sort(mLeaderboard, new leaderComparator());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new DailyLeaderboardAdapter(mLeaderboard));
        }

        return view;
    }

    public class leaderComparator implements Comparator<User> {

        @Override
        public int compare(User contact1, User contact2) {

            return contact2.getDayPoints().compareTo(contact1.getDayPoints());

        }
    }
}
