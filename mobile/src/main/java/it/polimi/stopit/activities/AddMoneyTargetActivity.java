package it.polimi.stopit.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import it.polimi.stopit.R;
import it.polimi.stopit.fragments.MoneyGalleryFragment;

public class AddMoneyTargetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_target);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add new money target");

        Fragment moneyGalleryFragment = new MoneyGalleryFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.rel_layout_content, moneyGalleryFragment).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
