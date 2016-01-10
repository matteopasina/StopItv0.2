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
import java.util.Random;

import it.polimi.stopit.NotificationID;
import it.polimi.stopit.R;
import it.polimi.stopit.Receivers.ChallengeReceiver;
import it.polimi.stopit.Receivers.ControllerReceiver;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.AlternativeActivity;
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

        SharedPreferences.Editor editor = settings.edit();

        if(numSmoked==0){

            int noSmokeDays=0;
            Achievement achievement;

            try{

                noSmokeDays=settings.getInt("noSmokeDays",0);

            }catch (Exception e){

            }

            if(noSmokeDays>0){

                noSmokeDays+=1;
                editor.putInt("noSmokeDays",noSmokeDays).apply();

                if(noSmokeDays==3){

                    if(!db.getAchievement(2).isObtained()) {

                        achievement=new Achievement(2, "Three Days Stop", "Don't smoke for three days", 400, R.drawable.stop_three_days, true);
                        db.updateAchievement(achievement);
                        updatePoints(400);
                    }

                }else if(noSmokeDays==7){

                    if(!db.getAchievement(3).isObtained()) {

                        achievement=new Achievement(3, "One Week Stop", "Don't smoke for one week", 600, R.drawable.stop_one_week, true);
                        db.updateAchievement(achievement);
                        updatePoints(600);
                    }

                }else if(noSmokeDays==14){

                    if(!db.getAchievement(4).isObtained()) {

                        achievement=new Achievement(4, "Two Weeks Stop", "Don't smoke for two weeks", 800, R.drawable.stop_two_weeks, true);
                        db.updateAchievement(achievement);
                        updatePoints(800);
                    }

                }else if(noSmokeDays==30){

                    if(!db.getAchievement(5).isObtained()) {

                        achievement=new Achievement(5, "One Month Stop", "Don't smoke for one month", 1000, R.drawable.stop_one_month, true);
                        db.updateAchievement(achievement);
                        updatePoints(1000);
                    }
                }

            }else{

                editor.putInt("noSmokeDays",1).apply();

                if(!db.getAchievement(1).isObtained()) {

                    achievement=new Achievement(1, "One Day Stop", "Don't smoke for one day", 200, R.drawable.stop_one_day, true);
                    db.updateAchievement(achievement);
                    updatePoints(200);
                }

            }


        }else{

            editor.putInt("noSmokeDays",0).apply();

        }

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

    public void updateLeaderboardAchievement(String type){

        Achievement achievement;

        if(type.equals("first")){

            if(!db.getAchievement(18).isObtained()){

                achievement=new Achievement(18, "Winner", "First in all time leaderboard", 500, R.drawable.winner, true);
                db.updateAchievement(achievement);
                updatePoints(500);
            }

        }else if(type.equals("top10")){

            if(!db.getAchievement(16).isObtained()) {

                achievement = new Achievement(16, "Top10", "In all time leaderboard", 100, R.drawable.winner, true);
                db.updateAchievement(achievement);
                updatePoints(100);

            }

        }else if(type.equals("top3")){

            if(!db.getAchievement(17).isObtained()) {

                achievement = new Achievement(17, "Top3", "In all time leaderboard", 250, R.drawable.winner, true);
                db.updateAchievement(achievement);
                updatePoints(250);
            }
        }
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
        List<Challenge> challengeList=db.getAllChallenges();

        for(Challenge challenge : challengeList) {
            if(challenge.isAccepted()) {

                challenge.setMyPoints(challenge.getMyPoints() + points);
                challenge.setOpponentPoints(challenge.getOpponentPoints() + points);

                final Firebase VS = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges/" + challenge.getID());
                VS.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //se sei tu lo sfidante
                        if (dataSnapshot.child("id").getValue().toString().equals(settings.getString("ID", null))) {
                            VS.child("myPoints").setValue((long) dataSnapshot.child("myPoints").getValue() + points);

                        } else if (dataSnapshot.child("opponentID").getValue().toString().equals(settings.getString("ID", null))) {
                            VS.child("opponentPoints").setValue((long) dataSnapshot.child("opponentPoints").getValue() + points);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
    }

    public int getCigAvoided(){

        return db.getCigarettesAvoided();
    }

    public int getMoneySaved(){

        int cigCost=Integer.parseInt(settings.getString("cigcost", null));

        return cigCost*db.getCigarettesAvoided();
    }

    //check if there are challenges that you launched accepted
    public void checkAccepted(){
        Firebase.setAndroidContext(context);
        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Accepted");

        fire.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                final DataSnapshot accepted = snapshot.child(settings.getString("ID", null));

                //se l'avversario ha accettato prende la challenge da firebase e la mette nel database
                if(accepted.exists()) {
                    if (accepted.getValue().toString() != "0") {

                        final Firebase fireChallenge = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges");

                        fireChallenge.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                DataSnapshot C = snapshot.child(accepted.getValue().toString());
                                Challenge chall = new Challenge(accepted.getValue().toString(),
                                        C.child("id").getValue().toString(),
                                        (long) C.child("myPoints").getValue(),
                                        (long) C.child("opponentPoints").getValue(),
                                        (long) C.child("startTime").getValue(),
                                        (long) C.child("endTime").getValue(),
                                        C.child("accepted").getValue().toString(),
                                        C.child("challenger").getValue().toString());

                                db.updateChallenge(chall);

                                Controller controller=new Controller(context);
                                controller.setChallengeAlarm(chall.getStartTime(),
                                        chall.getEndTime()-chall.getStartTime(),
                                        chall.getID());
                                controller.sendCustomNotification("Challenge accepted","Don't smoke if you want to win!");
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }

                        });

                        fire.child(settings.getString("ID", null)).removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    //check if there are challenges for you
    public void checkChallenges(){

        Firebase.setAndroidContext(context);
        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Notifications");

        fire.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                final DataSnapshot notification = snapshot.child(settings.getString("ID", null));

                //scontrolla firebase su Notifications e se c'è qualche sfida manda la notifica all'utente e la salva nel db come non accettata
                if (notification.getChildrenCount() != 0) {

                    for(final DataSnapshot children : notification.getChildren()) {

                        final Firebase fireInner = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/"+children.child("opponent").getValue().toString());

                        fireInner.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                //costruisci testo notifica
                                String opponent = snapshot.child("name").getValue().toString() + " " +
                                        snapshot.child("surname").getValue().toString();


                                //manda notifica
                                sendNotificationChallenge(opponent, children.child("opponent").getValue().toString());


                                //aggiungi challenge al DB
                                db.addChallenge(new Challenge(children.child("opponent").getValue().toString()
                                        , children.child("opponent").getValue().toString(), 0, 0, 0,
                                        (long) children.child("duration").getValue() * 86400000, "false", "false"));
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                    fire.child(settings.getString("ID", null)).removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void sendNotificationChallenge(String opponent,String ID) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.stopitsymbol)
                        .setContentTitle(opponent+" challenged you!")
                        .setContentText("Smash his ass!")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, NavigationActivity.class);

        resultIntent.putExtra("IDopponent",ID);

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
        NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNM.notify(NotificationID.getID(), mBuilder.build());

    }

    public boolean sendAlternative(){

        List<AlternativeActivity> alternativeActivityList=db.getAllAlternative();
        AlternativeActivity alternativeChoosen;
        System.out.println(alternativeActivityList);
        for(AlternativeActivity activity: alternativeActivityList){
            System.out.println(activity);
            System.out.println(settings.getBoolean("food",false));
            if(!settings.getBoolean(activity.getCategory(),false)){
                alternativeActivityList.remove(activity);
            }
        }

        ArrayList listWeight=new ArrayList();
        for(AlternativeActivity activitySelected: alternativeActivityList){
            int j=0;
            for(int i=0;i < activitySelected.getFrequency(); i++)
            {
                listWeight.add(j);
            }
            j++;
        }

        if(alternativeActivityList.isEmpty()){
            return false;
        }
        alternativeChoosen=alternativeActivityList.get((int)listWeight.get(new Random().nextInt(listWeight.size())));

        sendAlternativeNotification(alternativeChoosen);
        return true;
    }

    public void sendAlternativeNotification(AlternativeActivity alternativeActivity){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(alternativeActivity.getImage())
                        .setContentTitle(alternativeActivity.getTitle())
                        .setContentText(alternativeActivity.getDescription())
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, NavigationActivity.class);
        resultIntent.putExtra("points", alternativeActivity.getBonusPoints());

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

    public String getLevel(long points){

        String levelText="";
        int level=(int)(points/1000)+1;

        if(level < 10){

            levelText="Level "+level+" - Beginner";

        }else if(level < 20){

            levelText="Level "+level+" - Novice";

        }else if(level < 30){

            levelText="Level "+level+" - Rookie";

        }else if(level < 40){

            levelText="Level "+level+" - Semi-Pro";

        }else if(level < 50){

            levelText="Level "+level+" - Pro";

        }else if(level < 60){

            levelText="Level "+level+" - Veteran";

        }else if(level < 70){

            levelText="Level "+level+" - Expert";

        }else if(level < 80){

            levelText="Level "+level+" - Magus";

        }else if(level < 90){

            levelText="Level "+level+" - Master";

        }else if(level < 100){

            levelText="Level "+level+" - Grandmaster";

        }else{

            levelText="Level 100 - Legend";
        }

        return levelText;
    }
}
