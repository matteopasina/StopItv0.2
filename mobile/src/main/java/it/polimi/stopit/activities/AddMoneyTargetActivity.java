package it.polimi.stopit.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

        TextView catLabel= (TextView) findViewById(R.id.cat_label);
        TextView priceLabel= (TextView) findViewById(R.id.price_label);
        final EditText priceText = (EditText) findViewById(R.id.price_text);
        String[] priceValues = new String[100];

        TextView timeLabel=(TextView) findViewById(R.id.time_label);
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
        String[] timeValues = new String[52];

        for (int i = 0; i < timeValues.length; i++) {

            String number = Integer.toString(i+1);
            timeValues[i] = number + " Weeks";
        }
        timeValues[0]="1 Week";

        //timePicker.setMinValue(1);
        //timePicker.setMaxValue(52);
        //timePicker.setWrapSelectorWheel(false);
        //timePicker.setDisplayedValues(timeValues);

        Button addButton= (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                System.out.println(""+priceText.getText().toString());

                if(!priceText.getText().toString().equals("")){

                    int price=Integer.parseInt(priceText.getText().toString());

                    if(price>0 && price<=1000){

                        Toast.makeText(AddMoneyTargetActivity.this, "The price is " + price, Toast.LENGTH_SHORT).show();
                        showDialog();
                    }else{

                        Toast.makeText(AddMoneyTargetActivity.this, "Insert a price between 1 and 1000 €", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    Toast.makeText(AddMoneyTargetActivity.this, "Price not valid", Toast.LENGTH_SHORT).show();
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

    public void showDialog(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddMoneyTargetActivity.this);
        builder.setMessage("You are adding a new money target, confirm?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}
