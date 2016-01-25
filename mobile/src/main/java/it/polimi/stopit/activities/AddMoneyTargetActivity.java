package it.polimi.stopit.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import it.polimi.stopit.OnPassingData;
import it.polimi.stopit.R;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.fragments.MoneyGalleryFragment;
import it.polimi.stopit.model.MoneyTarget;

public class AddMoneyTargetActivity extends AppCompatActivity implements OnPassingData {

    String name;
    int imgRes;

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
        ((MoneyGalleryFragment) moneyGalleryFragment).registerActivity(this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.rel_layout_content, moneyGalleryFragment).commit();

        final EditText priceText = (EditText) findViewById(R.id.price_text);

        final NumberPicker timePicker = (NumberPicker) findViewById(R.id.time_picker);
        String[] timeValues = new String[52];

        for (int i = 0; i < timeValues.length; i++) {

            String number = Integer.toString(i + 1);
            timeValues[i] = number + " Weeks";
        }
        timeValues[0] = "1 Week";

        timePicker.setMinValue(1);
        timePicker.setMaxValue(52);
        timePicker.setWrapSelectorWheel(false);
        timePicker.setDisplayedValues(timeValues);

        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int price;
                int duration = timePicker.getValue() * 7;
                int maxPrice;

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AddMoneyTargetActivity.this);

                int cigCost = settings.getInt("cigcost", 0);
                int cigPerDay = settings.getInt("CPD", 0);

                maxPrice = (cigCost * cigPerDay * duration);

                if (name == null || imgRes == 0) {

                    Toast.makeText(AddMoneyTargetActivity.this, "Select a category", Toast.LENGTH_SHORT).show();

                } else {

                    try {

                        price = Integer.parseInt(priceText.getText().toString()) * 100;

                        if (price > 0) {

                            if (price <= maxPrice) {

                                int cigToReduce = (price / (cigCost * duration));

                                if(cigToReduce==0) cigToReduce=1;

                                showDialog(name, price, duration, imgRes, cigPerDay - cigToReduce, cigToReduce);
                            } else {

                                Toast.makeText(AddMoneyTargetActivity.this, "You can save maximum " + maxPrice / 100 + " € in " + duration + " days ", Toast.LENGTH_SHORT).show();

                            }

                        } else {

                            Toast.makeText(AddMoneyTargetActivity.this, "Insert a valid price", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {

                        Toast.makeText(AddMoneyTargetActivity.this, "Insert a valid price ", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

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

    public void showDialog(final String name, final int price, final int duration, final int imageResource, final int CPD, final int cigToReduce) {


        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AddMoneyTargetActivity.this);
                        settings.edit().putInt("CPD", CPD).commit();

                        insertTarget(name, price, duration, imageResource, cigToReduce);

                        new Controller(getBaseContext()).buildStopProgram(CPD,0);

                        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                        intent.putExtra("redirect", "money");
                        startActivity(intent);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddMoneyTargetActivity.this);
        builder.setMessage("Adding " + name + ", you will avoid to smoke " + (cigToReduce) + " cigarettes per day, confirm?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    public void insertTarget(String name, int price, int duration, int imageResource, int cigToReduce) {

        DatabaseHandler db = new DatabaseHandler(getApplication());

        db.addMoneyTarget(new MoneyTarget(1, name, price, 0, duration, imageResource, cigToReduce));
        Toast.makeText(AddMoneyTargetActivity.this, "" + name + " inserted correctly!", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void callBack(String name, int imgResource) {

        this.name = name;
        this.imgRes = imgResource;
    }
}
