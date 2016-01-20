package it.polimi.stopit.Receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Cigarette;

public class SmokeReceiver extends BroadcastReceiver {

    private NotificationManager mNM;
    Controller controller;
    private SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {
        controller=new Controller(context);
        settings= PreferenceManager.getDefaultSharedPreferences(context);

        if (mNM == null)
            mNM = (NotificationManager)  context.getSystemService(context.NOTIFICATION_SERVICE);
        mNM.cancel(intent.getIntExtra("notificationID",0));

        int gain=intent.getIntExtra("points",0);
        long points = settings.getLong("points", 0);
        long daypoints = settings.getLong("dayPoints", 0);
        long weekpoints = settings.getLong("weekPoints", 0);
        SharedPreferences.Editor editor = settings.edit();
        DatabaseHandler dbh = new DatabaseHandler(context);
        DateTime date;

        editor.putLong("dayPoints", daypoints + gain);
        editor.putLong("weekPoints", weekpoints + gain);
        editor.putLong("points", points + gain);
        editor.commit();

        controller.updatePoints(gain);

        if(intent.getBooleanExtra("smoke",false)) {
            date = new DateTime(new Instant());
            dbh.addCigarette(new Cigarette(1, date, "smoke"));
        }
    }

}
