package it.polimi.stopit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import it.polimi.stopit.R;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseSeeder;

public class FirstLoginSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login_settings);

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        setTitle("");

        TextView welcomeUser = (TextView) findViewById(R.id.welcome);
        welcomeUser.setText("Welcome, " + settings.getString("name", null));

        final TextView progressText = (TextView) findViewById(R.id.progress);
        progressText.setText("25/50");

        final SeekBar cigaPerDay = (SeekBar) findViewById(R.id.seekBar);
        cigaPerDay.setProgress(50);
        cigaPerDay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressText.setText(seekBar.getProgress() / 2 + "/50");
            }
        });

        final EditText cigCostVal = (EditText) findViewById(R.id.cigcost_text);

        Button done = (Button) findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cigaPerDay.getProgress() != 0) {

                    int cost = 0;
                    try {

                        cost = Integer.parseInt(cigCostVal.getText().toString());

                    } catch (Exception e) {

                        Toast.makeText(FirstLoginSettingsActivity.this, "Please insert a valid cost", Toast.LENGTH_SHORT).show();
                    }

                    if (cost > 0) {

                        if (cost <= 50) {
                            Intent intent = new Intent(FirstLoginSettingsActivity.this, NavigationActivity.class);

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("CPD", cigaPerDay.getProgress() / 2);

                            editor.putInt("cigcost", Integer.parseInt(cigCostVal.getText().toString()));

                            editor.putInt("daysToRed", 365);

                            editor.commit();

                            DatabaseSeeder dbSeed = new DatabaseSeeder(getApplicationContext());
                            dbSeed.loadContacts();
                            dbSeed.seedMoneyCategories();
                            dbSeed.seedAlternatives();
                            dbSeed.seedAchievements();

                            new Controller(getBaseContext()).buildStopProgram(cigaPerDay.getProgress() / 2, 365);

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(FirstLoginSettingsActivity.this, "Are you sure? Insert a realistic price", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(FirstLoginSettingsActivity.this, "Please insert a valid cost", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(FirstLoginSettingsActivity.this, "So you don't smoke? :)", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
