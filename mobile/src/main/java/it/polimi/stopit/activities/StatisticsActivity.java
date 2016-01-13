package it.polimi.stopit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import it.polimi.stopit.R;
import it.polimi.stopit.controller.Controller;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Statistics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Controller control=new Controller(this);
        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(this);

        TextView totalPoints=(TextView) findViewById(R.id.total_points);
        totalPoints.setText(settings.getLong("points",0)+" total points ");

        TextView moneySaved=(TextView) findViewById(R.id.money_saved);
        int monSaved=control.getMoneySaved();
        moneySaved.setText(monSaved/100+"."+monSaved%100+" € saved");

        TextView cigAvoided=(TextView) findViewById(R.id.cig_avoided);
        cigAvoided.setText(control.getCigAvoided()+" cigarettes avoided");

        TextView healthSaved=(TextView) findViewById(R.id.health_saved);
        healthSaved.setText("-"+control.getCigAvoided()/100+"."+control.getCigAvoided()%100 + "% risk of cancer ");

        TextView challengeWon=(TextView) findViewById(R.id.challenge_won);
        challengeWon.setText(control.challengeWonLost()+" challenge won ");

        TextView moneyTargetCompleted=(TextView) findViewById(R.id.moneytarget_completed);
        moneyTargetCompleted.setText(settings.getInt("moneytargetcompleted",0)+" money target completed ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            this.finish();
            Intent intent=new Intent(this,NavigationActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
