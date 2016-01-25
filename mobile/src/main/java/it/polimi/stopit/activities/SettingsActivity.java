package it.polimi.stopit.activities;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.controller.Controller;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = String.valueOf(newValue);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                return true;

            } else if (preference instanceof EditTextPreference && !stringValue.contentEquals(((EditTextPreference) preference).getText())) {

                if (preference.getKey().equals("CPD")) {

                    int CPD = Integer.parseInt(stringValue);

                    if (CPD > 50 || CPD < 1) {

                        Toast.makeText(preference.getContext(), "Cigarettes per day must be in range 1 - 50", Toast.LENGTH_SHORT).show();

                    } else {

                        preference.setSummary(stringValue);
                        new Controller(preference.getContext()).buildStopProgram(Integer.parseInt(stringValue),0);

                        return true;
                    }


                } else if (preference.getKey().equals("cigcost")) {

                    int cigcost = Integer.parseInt(stringValue);

                    if (cigcost > 50 || cigcost < 1) {

                        Toast.makeText(preference.getContext(), "Error insert a realistic price!", Toast.LENGTH_SHORT).show();

                    } else {

                        preference.setSummary(stringValue);
                        return true;
                    }

                } else if (preference.getKey().equals("daysToRed")) {

                    int daysToRed = Integer.parseInt(stringValue);

                    if (daysToRed < 0) {

                        Toast.makeText(preference.getContext(), "You will need at least 50 days", Toast.LENGTH_SHORT).show();

                    } else if(daysToRed > 1000){

                        Toast.makeText(preference.getContext(), "Come on, don't be lazy!", Toast.LENGTH_SHORT).show();

                    }else {

                        preference.setSummary(stringValue);
                        new Controller(preference.getContext()).buildStopProgram(100,Integer.parseInt(stringValue));
                        return true;
                    }

                }


            } else {

                preference.setSummary(stringValue);
                return true;
            }
            return false;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getInt(preference.getKey(), 0));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            this.finish();
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || AltActivitiesPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("CPD"));
            bindPreferenceSummaryToValue(findPreference("cigcost"));
            bindPreferenceSummaryToValue(findPreference("daysToRed"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {

                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AltActivitiesPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_altactivities);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
