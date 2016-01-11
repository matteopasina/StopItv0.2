package it.polimi.stopit.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.client.Firebase;

import it.polimi.stopit.controller.Controller;

public class ControllerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Firebase.setAndroidContext(context);
        Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Controller controller = new Controller(context);

        if(intent.getExtras().getString("type").equals("day")){

            myFirebaseRef.child(settings.getString("ID", null)).child("dayPoints").setValue(0);
            settings.edit().putLong("dayPoints", 0);
            controller.dailyMoneyControl();
            controller.setDailyAlarm();

        }else if(intent.getExtras().getString("type").equals("week")){

            myFirebaseRef.child(settings.getString("ID", null)).child("weekPoints").setValue(0);
            settings.edit().putLong("weekPoints", 0);
            controller.setWeeklyAlarm();

        }

    }
}