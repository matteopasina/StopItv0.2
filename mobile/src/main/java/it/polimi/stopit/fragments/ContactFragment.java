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

    public ContactFragment() {
    }

    public static Fragment newInstance() {

        return new ContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        ArrayList<User> mContacts = new DatabaseHandler(getActivity()).getAllContacts();

        mContacts = new Controller(getActivity()).addTestContacts(mContacts);

        // Set the adapter
        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            recyclerView.setAdapter(new ContactRecyclerViewAdapter(mContacts));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
