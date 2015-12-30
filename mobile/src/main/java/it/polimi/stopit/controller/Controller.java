package it.polimi.stopit.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import java.util.ArrayList;

import it.polimi.stopit.NotificationID;
import it.polimi.stopit.R;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Cigarette;
import it.polimi.stopit.model.MoneyTarget;

public class Controller {

    DatabaseHandler db;
    Context context;
    SharedPreferences settings;

    public Controller(Context context){

        this.context = context;
        db = new DatabaseHandler(context);
        settings = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void dailyMoneyControl(){

        Instant instant=new Instant();
        int year=instant.get(DateTimeFieldType.year());
        int month=instant.get(DateTimeFieldType.monthOfYear());
        int day=instant.get(DateTimeFieldType.dayOfMonth());

        int cigPD=Integer.parseInt(settings.getString("CPD", null));

        int numSmoked=0;

        ArrayList<Cigarette> todayCig=db.getDailyCigarettes(year,month,day);

        for(Cigarette cig:todayCig){

            if(cig.getType().equals("smoke")){

                numSmoked++;

            }
        }

        updateMoneyTarget((cigPD-numSmoked));

    }

    // updates money saved, saved is true if the user has smoked less or equal than his cpd

    public void updateMoneyTarget(int notsmoked){

        ArrayList<MoneyTarget> moneyTargets=db.getAllTargets();
        MoneyTarget currentTarget=new MoneyTarget();
        boolean first=false;
        int cigCost=Integer.parseInt(settings.getString("cigcost", null));

        for(MoneyTarget target:moneyTargets){

            if((target.getMoneySaved()!=target.getMoneyAmount()) && first==false){

                first=true;
                currentTarget=target;
            }

        }

        if(!first) return;

        int moneySaved=(currentTarget.getCigReduced()+notsmoked)*cigCost;

        long newMoney=currentTarget.getMoneySaved()+moneySaved;

        if(newMoney>=currentTarget.getMoneyAmount()){

            currentTarget.setMoneySaved(currentTarget.getMoneyAmount());
            currentTarget.setDuration(0);

            int newCPD=Integer.parseInt(settings.getString("CPD", null))+currentTarget.getCigReduced();

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("CPD", String.valueOf(newCPD));
        }
        else{
            currentTarget.setMoneySaved(newMoney);

            if(moneySaved>0){
                currentTarget.setDuration(currentTarget.getDuration()-1);
            }

        }


        db.updateMoneyTarget(currentTarget);
    }

    public void checkNotifications(){
        Firebase.setAndroidContext(context);
        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/");
        fire.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                DataSnapshot notification=snapshot.child("Notifications").child(settings.getString("ID",null));
                DataSnapshot users=snapshot.child("Users");
                if (notification.getChildrenCount() != 0) {
                    for (DataSnapshot children : notification.getChildren()) {
                        String opponent=users.child(children.getValue().toString()).child("name").toString()+" "+
                                users.child(children.getValue().toString()).child("surname").toString();
                        sendNotification(opponent);
                    }
                    fire.child("Notifications").child(settings.getString("ID",null)).removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void sendNotification(String opponent) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.stopitsymbol)
                        .setContentTitle("You have been challenged by "+opponent)
                        .setContentText("Smash his ass!")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, NavigationActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NavigationActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNM =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNM.notify(NotificationID.getID(), mBuilder.build());

    }
}
