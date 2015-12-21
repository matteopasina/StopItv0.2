package it.polimi.stopit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import it.polimi.stopit.R;
import it.polimi.stopit.database.DatabaseSeeder;

public class FirstLoginSettingsActivity extends AppCompatActivity {

    // TODO mettere a posto i commenti ettutto
    public static final String PREFS_NAME = "StopItPrefs";
   // private static boolean first=false,last=false;
   // private static int hourOfDayFirst,hourOfDayLast=24,minuteFirst,minuteLast=60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login_settings);

        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        setTitle("Welcome, " + settings.getString("name", null));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView setCiga=(TextView) findViewById(R.id.SetCiga);

        DatabaseSeeder dbSeed=new DatabaseSeeder(getApplicationContext());
        dbSeed.loadContacts();
        dbSeed.seedMoneyCategories();

        final TextView progressText=(TextView) findViewById(R.id.progress);
        progressText.setText("25/50");

        final SeekBar cigaPerDay=(SeekBar) findViewById(R.id.seekBar);
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

        TextView cigCost=(TextView) findViewById(R.id.cig_cost);
        final EditText cigCostVal=(EditText) findViewById(R.id.cigcost_text);

        Button done=(Button) findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cigaPerDay.getProgress()!=0) {

                    int cost=0;
                    try{

                        cost=Integer.parseInt(cigCostVal.getText().toString());

                    }catch (Exception e){

                        Toast.makeText(FirstLoginSettingsActivity.this, "Please insert a valid cost", Toast.LENGTH_SHORT).show();
                    }

                    if(cost>0){

                        if(cost<=50){
                            Intent intent = new Intent(FirstLoginSettingsActivity.this, NavigationActivity.class);

                            SharedPreferences.Editor editor = settings.edit();
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FirstLoginSettingsActivity.this);
                            SharedPreferences.Editor defaultEditor = sharedPref.edit();
                            defaultEditor.putString("CPD", String.valueOf(cigaPerDay.getProgress() / 2));
                            editor.putInt("CPD", cigaPerDay.getProgress() / 2);

                            defaultEditor.putString("cigcost", String.valueOf(cigCostVal.getText()));
                            editor.putInt("cigcost", cost);
                            editor.commit();
                            defaultEditor.commit();

                            startActivity(intent);
                            finish();

                        }else{

                            Toast.makeText(FirstLoginSettingsActivity.this, "Are you sure? Insert a realistic price", Toast.LENGTH_SHORT).show();
                        }

                    }else{

                        Toast.makeText(FirstLoginSettingsActivity.this, "Please insert a valid cost", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(FirstLoginSettingsActivity.this, "So you don't smoke? :)", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
/*
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            if(first) {
                if(hourOfDay > hourOfDayLast) {

                    Toast.makeText(getActivity(), "Last cigarette must be after the first!", Toast.LENGTH_SHORT).show();

                }
                else if(hourOfDay == hourOfDayLast && minute >= minuteLast) {

                    Toast.makeText(getActivity(), "Last cigarette must be after the first!", Toast.LENGTH_SHORT).show();

                }
                else if((hourOfDayLast-hourOfDayFirst) < 6){
                    Toast.makeText(getActivity(), "Really?", Toast.LENGTH_SHORT).show();
                }
                else {
                    hourOfDayFirst = hourOfDay;
                    minuteFirst = minute;

                    Button lastC = (Button) getActivity().findViewById(R.id.button);
                    lastC.setEnabled(true);

                    TextView F = (TextView) getActivity().findViewById(R.id.firstSizza);
                    F.setText(hourOfDayFirst + ":" + minuteFirst);
                }
                first=false;

            }else if(last){

                if(hourOfDay < hourOfDayFirst){
                    Toast.makeText(getActivity(), "Last cigarette must be after the first!", Toast.LENGTH_SHORT).show();
                }
                else if(hourOfDay == hourOfDayFirst && minute <= minuteFirst) {
                    Toast.makeText(getActivity(), "Last cigarette must be after the first!", Toast.LENGTH_SHORT).show();
                }
                else if((hourOfDayLast-hourOfDayFirst) < 6){
                    Toast.makeText(getActivity(), "Really?", Toast.LENGTH_SHORT).show();
                }
                else {

                    hourOfDayLast=hourOfDay;
                    minuteLast=minute;

                    Button done = (Button) getActivity().findViewById(R.id.done);
                    done.setEnabled(true);

                    TextView L=(TextView)getActivity().findViewById(R.id.lastSizza);
                    L.setText(hourOfDayLast+":"+minuteLast);
                }
                last=false;
            }
        }
    }

    public void showTimePickerDialogFirst(View v) {
        first=true;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTimePickerDialogLast(View v) {
        last=true;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
*/

}
