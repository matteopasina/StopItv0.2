package it.polimi.stopit.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import it.polimi.stopit.OnPassingData;
import it.polimi.stopit.R;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.fragments.MoneyFragment;
import it.polimi.stopit.fragments.MoneyGalleryFragment;
import it.polimi.stopit.model.MoneyTarget;

public class AddMoneyTargetActivity extends AppCompatActivity implements OnPassingData{

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
        ((MoneyGalleryFragment)moneyGalleryFragment).registerActivity(this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.rel_layout_content, moneyGalleryFragment).commit();

        TextView catLabel= (TextView) findViewById(R.id.cat_label);
        TextView priceLabel= (TextView) findViewById(R.id.price_label);
        final EditText priceText = (EditText) findViewById(R.id.price_text);
        String[] priceValues = new String[100];

        TextView timeLabel=(TextView) findViewById(R.id.time_label);
        final NumberPicker timePicker = (NumberPicker) findViewById(R.id.time_picker);
        String[] timeValues = new String[52];

        for (int i = 0; i < timeValues.length; i++) {

            String number = Integer.toString(i+1);
            timeValues[i] = number + " Weeks";
        }
        timeValues[0]="1 Week";

        timePicker.setMinValue(1);
        timePicker.setMaxValue(52);
        timePicker.setWrapSelectorWheel(false);
        timePicker.setDisplayedValues(timeValues);

        Button addButton= (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int price;
                int duration=timePicker.getValue();
                duration=duration*7;
                int maxPrice;

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AddMoneyTargetActivity.this);

                int cigCost = Integer.parseInt(settings.getString("cigcost", null));
                int cigPerDay = Integer.parseInt(settings.getString("CPD", null));
                maxPrice=(cigCost*cigPerDay*duration)/100;

                if(name==null || imgRes==0){

                    System.out.println("Name: "+name+" imgRes = "+imgRes);
                    Toast.makeText(AddMoneyTargetActivity.this, "Select a category", Toast.LENGTH_SHORT).show();

                }else{

                    try{

                        price=Integer.parseInt(priceText.getText().toString());

                        if(price>0){

                            if(price<=maxPrice){

                                int cigToReduce=price*100/cigCost;
                                showDialog(name,price,duration,imgRes,cigToReduce);
                            }else{

                                Toast.makeText(AddMoneyTargetActivity.this, "You can save maximum "+maxPrice+" € in "+duration+" days ", Toast.LENGTH_SHORT).show();

                            }

                        }else{

                            Toast.makeText(AddMoneyTargetActivity.this, "Insert a valid price", Toast.LENGTH_SHORT).show();
                        }

                    }catch(Exception e){

                        Toast.makeText(AddMoneyTargetActivity.this, "Insert a vald price ", Toast.LENGTH_SHORT).show();
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

    public void showDialog(String nam,int pric, int duratio,int imageResourc,int cigToReduce){

        final String name=nam;
        final int price=pric;
        final int duration=duratio;
        final int imageResource=imageResourc;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        insertTarget(name,price,duration,imageResource);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddMoneyTargetActivity.this);
        builder.setMessage("Adding "+ name +", you will avoid to smoke "+(cigToReduce/duratio)+" cigarettes per day, confirm?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    public void insertTarget(String name,int price, int duration,int imageResource){

        DatabaseHandler db=new DatabaseHandler(getApplication());

        db.addMoneyTarget(new MoneyTarget(1,name,price,0,duration,imageResource));
        Toast.makeText(AddMoneyTargetActivity.this, ""+name+" inserted correctly!", Toast.LENGTH_SHORT).show();

        Fragment fragment = MoneyFragment.newInstance();

        FragmentManager fragmentManager=getFragmentManager();

        FragmentTransaction ft=fragmentManager.beginTransaction();

        ft.replace(R.id.content_addmoneytar, fragment);

        ft.commit();

        getSupportActionBar().setTitle("Money Target");
    }


    @Override
    public void callBack(String name,int imgResource) {
        System.out.println("Name: "+name+" imgRes = "+imgResource);
        this.name=name;
        this.imgRes=imgResource;
    }
}
