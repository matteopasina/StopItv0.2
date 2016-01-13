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
import it.polimi.stopit.controller.Controller;
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

        Controller controller=new Controller(getActivity());

        mContacts=controller.addTestContacts(mContacts);

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
