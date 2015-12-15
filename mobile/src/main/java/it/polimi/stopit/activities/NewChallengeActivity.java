package it.polimi.stopit.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.polimi.stopit.R;
import it.polimi.stopit.fragments.ContactFragment;

public class NewChallengeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_challenge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Contact");
        setSupportActionBar(toolbar);

        Fragment contactFragment = new ContactFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.rel_layout_content, contactFragment).commit();
    }

}
