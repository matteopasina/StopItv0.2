package it.polimi.stopit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.polimi.stopit.services.ScheduleService;

import com.firebase.client.Firebase;

import it.polimi.stopit.R;

public class ChooseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "StopItPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        final int pointsFromN=getIntent().getExtras().getInt("points",0);
        System.out.println(pointsFromN);

        Button chooseSmoke=(Button) findViewById(R.id.chooseSmoke);

        chooseSmoke.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences p=getSharedPreferences(PREFS_NAME, 0);
                Firebase.setAndroidContext(ChooseActivity.this);
                final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
                long points=p.getLong("points",0);
                fire.child(p.getString("ID", null)).child("points").setValue(points + pointsFromN);

                Intent intent = new Intent(ChooseActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button chooseDont=(Button) findViewById(R.id.chooseDont);

        chooseDont.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences p=getSharedPreferences(PREFS_NAME, 0);
                Firebase.setAndroidContext(ChooseActivity.this);
                final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
                long points=p.getLong("points",0);
                fire.child(p.getString("ID", null)).child("points").setValue(points + pointsFromN*2);

                Intent intent = new Intent(ChooseActivity.this,NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
