package it.polimi.stopit.controller;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polimi.stopit.NotificationID;
import it.polimi.stopit.R;
import it.polimi.stopit.Receivers.ChallengeReceiver;
import it.polimi.stopit.Receivers.ControllerReceiver;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Challenge;
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

        updateMoneyTarget((cigPD - numSmoked));

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

    public void setDailyAlarm(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ControllerReceiver.class);
        intent.putExtra("type","day");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,0);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    public void setWeeklyAlarm(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ControllerReceiver.class);
        intent.putExtra("type","week");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,0);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, pi);
    }

    public void setChallengeAlarm(long startTime,long duration, String challengeKey){

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ChallengeReceiver.class);
        intent.putExtra("challengekey",challengeKey);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,0);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime,
                duration , pi);
    }

    public void sendCustomNotification(String title,String text){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.stopitsymbol)
                        .setContentTitle(title)
                        .setContentText(text)
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
        int notificationID=NotificationID.getID();
        mNM.notify(notificationID, mBuilder.build());
    }

    public void updatePoints(final long points){

        Firebase.setAndroidContext(context);
        final Firebase user = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/"+settings.getString("ID",null));
        //setta tutti i punti giornalieri, settimanali e totali prendendo da firebase quelli vecchi e sommandoli
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user.child("points").setValue((long) snapshot.child("points").getValue() + points);
                user.child("weekPoints").setValue((long)snapshot.child("weekPoints").getValue() + points);
                user.child("dayPoints").setValue((long)snapshot.child("dayPoints").getValue() + points);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        //per ogni challenge aggiorna i punti su firebase e in locale
        DatabaseHandler dbh=new DatabaseHandler(context);
        List<Challenge> challengeList=dbh.getAllChallenges();

        for(Challenge challenge : challengeList) {

            challenge.setMyPoints(challenge.getMyPoints()+points);
            challenge.setOpponentPoints(challenge.getOpponentPoints()+points);

            final Firebase VS = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges/"+challenge.getID());
            VS.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //se sei tu lo sfidante
                    if(dataSnapshot.child("id").getValue().toString().equals(settings.getString("ID",null))){
                        VS.child("myPoints").setValue( (long)dataSnapshot.child("myPoints").getValue() + points);

                    }
                    else if(dataSnapshot.child("opponentID").getValue().toString().equals(settings.getString("ID",null))){
                        VS.child("opponentPoints").setValue( (long)dataSnapshot.child("opponentPoints").getValue() + points);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    public int getCigAvoided(){

        return db.getCigarettesAvoided();
    }

    public int getMoneySaved(){

        int cigCost=Integer.parseInt(settings.getString("cigcost", null));

        return cigCost*db.getCigarettesAvoided();
    }
}
