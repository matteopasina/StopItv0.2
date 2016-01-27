package it.polimi.stopit.controller;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import it.polimi.stopit.NotificationID;
import it.polimi.stopit.R;
import it.polimi.stopit.Receivers.ChallengeAcceptReceiver;
import it.polimi.stopit.Receivers.ChallengeReceiver;
import it.polimi.stopit.Receivers.SmokeReceiver;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.AlternativeActivity;
import it.polimi.stopit.model.Challenge;
import it.polimi.stopit.model.Cigarette;
import it.polimi.stopit.model.MoneyTarget;
import it.polimi.stopit.model.User;

public class Controller {

    DatabaseHandler db;
    Context context;
    SharedPreferences settings;
    Bitmap largeicon;

    public Controller(Context context) {

        this.context = context;
        db = new DatabaseHandler(context);
        settings = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void dailyMoneyControl() {

        Instant instant = new Instant();
        int year = instant.get(DateTimeFieldType.year());
        int month = instant.get(DateTimeFieldType.monthOfYear());
        int day = instant.get(DateTimeFieldType.dayOfMonth());

        int cigPD = settings.getInt("CPD", 0);

        int numSmoked = 0;

        ArrayList<Cigarette> todayCig = db.getDailyCigarettes(year, month, day);

        for (Cigarette cig : todayCig) {

            if (cig.getType().equals("smoke")) {

                numSmoked++;

            }
        }

        updateMoneyTarget((cigPD - numSmoked));

        SharedPreferences.Editor editor = settings.edit();

        if (numSmoked == 0) {

            int noSmokeDays = 0;
            Achievement achievement;

            try {

                noSmokeDays = settings.getInt("noSmokeDays", 0);

            } catch (Exception e) {

                e.printStackTrace();
            }

            if (noSmokeDays > 0) {

                noSmokeDays += 1;
                editor.putInt("noSmokeDays", noSmokeDays).apply();

                if (noSmokeDays == 3) {

                    if (!db.getAchievement(2).isObtained()) {

                        achievement = new Achievement(2, "Three Days Stop", "Don't smoke for three days", 400, R.drawable.stop_three_days, true);
                        db.updateAchievement(achievement);
                        updatePoints(400);
                    }

                } else if (noSmokeDays == 7) {

                    if (!db.getAchievement(3).isObtained()) {

                        achievement = new Achievement(3, "One Week Stop", "Don't smoke for one week", 600, R.drawable.stop_one_week, true);
                        db.updateAchievement(achievement);
                        updatePoints(600);
                    }

                } else if (noSmokeDays == 14) {

                    if (!db.getAchievement(4).isObtained()) {

                        achievement = new Achievement(4, "Two Weeks Stop", "Don't smoke for two weeks", 800, R.drawable.stop_two_weeks, true);
                        db.updateAchievement(achievement);
                        updatePoints(800);
                    }

                } else if (noSmokeDays == 30) {

                    if (!db.getAchievement(5).isObtained()) {

                        achievement = new Achievement(5, "One Month Stop", "Don't smoke for one month", 1000, R.drawable.stop_one_month, true);
                        db.updateAchievement(achievement);
                        updatePoints(1000);
                    }
                }

            } else {

                editor.putInt("noSmokeDays", 1).apply();

                if (!db.getAchievement(1).isObtained()) {

                    achievement = new Achievement(1, "One Day Stop", "Don't smoke for one day", 200, R.drawable.stop_one_day, true);
                    db.updateAchievement(achievement);
                    updatePoints(200);
                }

            }


        } else {

            editor.putInt("noSmokeDays", 0).apply();

        }

    }

    // updates money saved, saved is true if the user has smoked less or equal than his cpd

    public void updateMoneyTarget(int notsmoked) {

        ArrayList<MoneyTarget> moneyTargets = db.getAllTargets();
        MoneyTarget currentTarget = new MoneyTarget();
        boolean first = false;
        int cigCost = settings.getInt("cigcost", 0);

        for (MoneyTarget target : moneyTargets) {

            if ((target.getMoneySaved() != target.getMoneyAmount()) && !first) {

                first = true;
                currentTarget = target;
            }

        }

        if (!first) {

            if (notsmoked > 0) {

                for (int i = 0; i < notsmoked; i++) {

                    db.addCigarette(new Cigarette(1, new DateTime(new Instant()), "notsmoke"));
                }
            }

            return;
        }

        int newNotSmoked = currentTarget.getCigReduced() + notsmoked;

        if ((newNotSmoked) > 0) {

            for (int i = 0; i < newNotSmoked; i++) {

                db.addCigarette(new Cigarette(1, new DateTime(new Instant()), "notsmoke"));
            }
        }

        int moneySaved = (newNotSmoked) * cigCost;

        long newMoney = currentTarget.getMoneySaved() + moneySaved;

        if (newMoney >= currentTarget.getMoneyAmount()) {

            currentTarget.setMoneySaved(currentTarget.getMoneyAmount());
            currentTarget.setDuration(0);

            int newCPD = settings.getInt("CPD", 0) + currentTarget.getCigReduced();

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("CPD", newCPD).apply();

            int numCompleted = settings.getInt("moneyTargetCompleted", 0);
            numCompleted++;
            editor.putInt("moneyTargetCompleted", numCompleted).apply();

            buildStopProgram(newCPD, 0);

            if (numCompleted > 2) {

                updateMoneyTargetAchievement(3);

            } else {

                updateMoneyTargetAchievement(1);
            }


        } else {
            currentTarget.setMoneySaved(newMoney);

            int cigcost = settings.getInt("cigcost", 0);

            int duration = (int) (currentTarget.getMoneyAmount() - currentTarget.getMoneySaved()) / (currentTarget.getCigReduced() * cigcost);

            currentTarget.setDuration(duration);

        }


        db.updateMoneyTarget(currentTarget);
    }

    public void updateLeaderboardAchievement(String type) {

        Achievement achievement;

        switch (type) {
            case "first":

                if (!db.getAchievement(10).isObtained()) {

                    achievement = new Achievement(10, "Winner", "First in all time leaderboard", 500, R.drawable.winner, true);
                    db.updateAchievement(achievement);
                    updatePoints(500);
                }

                break;
            case "top10":

                if (!db.getAchievement(8).isObtained()) {

                    achievement = new Achievement(8, "Top10", "In all time leaderboard", 100, R.drawable.top10, true);
                    db.updateAchievement(achievement);
                    updatePoints(100);

                }

                break;
            case "top3":

                if (!db.getAchievement(9).isObtained()) {

                    achievement = new Achievement(9, "Top3", "In all time leaderboard", 250, R.drawable.top3, true);
                    db.updateAchievement(achievement);
                    updatePoints(250);
                }
                break;
        }
    }

    public void updateMoneyTargetAchievement(int num) {

        Achievement achievement;

        if (num == 1 && !db.getAchievement(6).isObtained()) {

            achievement = new Achievement(6, "Saver", "Complete a money target", 300, R.drawable.saver, true);
            db.updateAchievement(achievement);
            updatePoints(300);

        } else if (num == 3 && !db.getAchievement(7).isObtained()) {

            achievement = new Achievement(7, "Super Saver", "Complete 3 money targets", 800, R.drawable.super_saver, true);
            db.updateAchievement(achievement);
            updatePoints(800);
        }
    }

    public void updateLevelAchievement(int level) {

        Achievement achievement;
        if (level == 10) {

            if (!db.getAchievement(19).isObtained()) {
                achievement = new Achievement(19, "Novice", "Reach level 10", 200, R.drawable.novice, true);
                db.updateAchievement(achievement);
                updatePoints(200);
            }

        } else if (level == 25) {

            if (!db.getAchievement(20).isObtained()) {
                achievement = new Achievement(20, "Apprendice", "Reach level 25", 500, R.drawable.apprendice, true);
                db.updateAchievement(achievement);
                updatePoints(500);
            }

        } else if (level == 50) {

            if (!db.getAchievement(21).isObtained()) {
                achievement = new Achievement(21, "Master", "Reach level 50", 2000, R.drawable.master, true);
                db.updateAchievement(achievement);
                updatePoints(2000);
            }

        } else if (level == 100) {

            if (!db.getAchievement(22).isObtained()) {
                achievement = new Achievement(22, "Legend", "Reach level 100", 5000, R.drawable.shield, true);
                db.updateAchievement(achievement);
                updatePoints(5000);
            }
        }

    }

    public void updateChallengeAchievement() {

        Achievement achievement;

        if (!db.getAchievement(23).isObtained() && db.getAllWonChallenges().size() >= 1) {
            achievement = new Achievement(23, "Challenger", "Win a challenge", 250, R.drawable.challenge, true);
            db.updateAchievement(achievement);
            updatePoints(250);
        }

        if (!db.getAchievement(24).isObtained() && db.getAllWonChallenges().size() >= 3) {
            achievement = new Achievement(24, "Super Challenger", "Win 5 challenges", 1300, R.drawable.five_challenge, true);
            db.updateAchievement(achievement);
            updatePoints(1300);
        }
    }

    public void updateAlternativeAchievement() {

        if (!db.getAchievement(11).isObtained() && settings.getInt("numAlternative", 0) >= 1) {
            db.updateAchievement(new Achievement(11, "Beginner", "Do an alternative activity", 100, R.drawable.alternative, true));
            updatePoints(100);
        }

        if (!db.getAchievement(12).isObtained() && settings.getInt("numAlternative", 0) >= 10) {
            db.updateAchievement(new Achievement(12, "10 Activities", "Do 10 alternative activities", 200, R.drawable.alternative10, true));
            updatePoints(200);
        }

        if (!db.getAchievement(13).isObtained() && settings.getInt("numAlternative", 0) >= 20) {
            db.updateAchievement(new Achievement(13, "20 Activities", "Do 20 alternative activities", 400, R.drawable.alternative20, true));
            updatePoints(400);
        }

        if (!db.getAchievement(14).isObtained() && settings.getInt("numAlternative", 0) >= 50) {
            db.updateAchievement(new Achievement(14, "50 Activities", "Do 50 alternative activities", 600, R.drawable.alternative50, true));
            updatePoints(600);
        }

        if (!db.getAchievement(15).isObtained() && settings.getInt("numsport", 0) >= 10) {
            db.updateAchievement(new Achievement(15, "Sportsman", "Do 10 sport activities", 500, R.drawable.sportsman, true));
            updatePoints(500);
        }

        if (!db.getAchievement(16).isObtained() && settings.getInt("numsocial", 0) >= 10) {
            db.updateAchievement(new Achievement(16, "Social", "Do 10 social activities", 500, R.drawable.social, true));
            updatePoints(500);
        }

        if (!db.getAchievement(17).isObtained() && settings.getInt("numart", 0) >= 10) {
            db.updateAchievement(new Achievement(17, "Artist", "Do 10 art activities", 500, R.drawable.artist, true));
            updatePoints(500);
        }

        if (!db.getAchievement(17).isObtained() && settings.getInt("numfood", 0) >= 10) {
            db.updateAchievement(new Achievement(18, "Food", "Do 10 food activities", 500, R.drawable.food, true));
            updatePoints(500);
        }
    }

    public void setChallengeAlarm(long endTime, String challengeKey) {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ChallengeReceiver.class);
        intent.putExtra("challengekey", challengeKey);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        am.setExact(AlarmManager.RTC_WAKEUP, endTime, pi);
    }

    public void updatePoints(final long points) {

        Firebase.setAndroidContext(context);
        final Firebase user = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/" + settings.getString("ID", null));
        //setta tutti i punti giornalieri, settimanali e totali prendendo da firebase quelli vecchi e sommandoli
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user.child("points").setValue((long) snapshot.child("points").getValue() + points);
                user.child("weekPoints").setValue((long) snapshot.child("weekPoints").getValue() + points);
                user.child("dayPoints").setValue((long) snapshot.child("dayPoints").getValue() + points);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        //per ogni challenge aggiorna i punti su firebase e in locale
        List<Challenge> challengeList = db.getActiveChallenges();

        for (Challenge challenge : challengeList) {
            if (challenge.isAccepted()) {

                challenge.setMyPoints(challenge.getMyPoints() + points);
                challenge.setOpponentPoints(challenge.getOpponentPoints() + points);
                db.updateChallenge(challenge);

                final Firebase VS = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges/" + challenge.getID());
                VS.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            //se sei tu lo sfidante
                            if (dataSnapshot.child("id").getValue().toString().equals(settings.getString("ID", null))) {
                                VS.child("myPoints").setValue((long) dataSnapshot.child("myPoints").getValue() + points);

                            } else if (dataSnapshot.child("opponentID").getValue().toString().equals(settings.getString("ID", null))) {
                                VS.child("opponentPoints").setValue((long) dataSnapshot.child("opponentPoints").getValue() + points);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
    }

    public int getCigAvoided() {

        return db.getCigarettesAvoided();
    }

    public int getMoneySaved() {

        int cigCost = settings.getInt("cigcost", 0);

        return cigCost * db.getCigarettesAvoided();
    }

    //check if there are challenges that you launched accepted
    public void checkAccepted() {
        Firebase.setAndroidContext(context);
        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Accepted");

        fire.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                final DataSnapshot accepted = snapshot.child(settings.getString("ID", null));

                //se l'avversario ha accettato prende la challenge da firebase e la mette nel database
                if (accepted.getChildrenCount() == 1) {
                    try {
                        if (accepted.child("accepted").exists()) {

                            final Firebase fireChallenge = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges/" + accepted.child("accepted").getValue().toString());

                            fireChallenge.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    Challenge chall = new Challenge(accepted.child("accepted").getValue().toString(),
                                            snapshot.child("id").getValue().toString(),
                                            (long) snapshot.child("myPoints").getValue(),
                                            (long) snapshot.child("opponentPoints").getValue(),
                                            (long) snapshot.child("startTime").getValue(),
                                            (long) snapshot.child("endTime").getValue(),
                                            snapshot.child("accepted").getValue().toString(),
                                            "true",
                                            snapshot.child("over").getValue().toString(),
                                            snapshot.child("won").getValue().toString());

                                    db.updateChallenge(chall);

                                    Controller controller = new Controller(context);
                                    controller.setChallengeAlarm(chall.getEndTime(),
                                            chall.getID());
                                    controller.sendCustomNotification("Challenge accepted", "Don't smoke if you want to win!");
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }

                            });

                            fire.child(settings.getString("ID", null)).removeValue();
                        } else if (accepted.child("declined").exists()) {

                            db.deleteChallengeByOpponentId(accepted.child("declined").getValue().toString());
                            fire.child(settings.getString("ID", null)).removeValue();
                            Controller controller = new Controller(context);
                            controller.sendCustomNotification("Challenge declined", ":(");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    //check if there are challenges for you
    public void checkChallenges() {

        Firebase.setAndroidContext(context);
        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Notifications/" + settings.getString("ID", null));

        fire.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //scontrolla firebase su Notifications e se c'è qualche sfida manda la notifica all'utente e la salva nel db come non accettata
                if (dataSnapshot.getChildrenCount() != 0) {

                    for (final DataSnapshot children : dataSnapshot.getChildren()) {

                        if (children.getChildrenCount() == 2) {
                            final Firebase fireInner = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/" + children.child("opponent").getValue().toString());


                            fireInner.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    //costruisci testo notifica
                                    String opponent = snapshot.child("name").getValue().toString() + " " +
                                            snapshot.child("surname").getValue().toString();


                                    //manda notifica
                                    sendNotificationChallenge(opponent, snapshot.child("ID").getValue().toString(),snapshot.child("profilePic").getValue().toString());


                                    //aggiungi challenge al DB dello sfidato
                                    db.addChallenge(new Challenge(children.child("opponent").getValue().toString()
                                            , children.child("opponent").getValue().toString(), 0, 0, 0,
                                            (long) children.child("duration").getValue() * 86400000, "false", "false", "false", "false"));
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                            fire.child(children.getKey()).removeValue();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void checkGiveUp() {
        Firebase.setAndroidContext(context);
        final Firebase fireChallenge = new Firebase("https://blazing-heat-3084.firebaseio.com/GiveUp/" + settings.getString("ID", null));
        fireChallenge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 2) {
                    Challenge challenge = db.getChallenge(dataSnapshot.child("challenge").getValue().toString());
                    challenge.setOver(true);
                    challenge.setWon(true);
                    db.updateChallenge(challenge);
                    System.out.println(db.getChallenge(challenge.getID()).isOver());
                    System.out.println("opponentID: " + db.getChallengeByOpponentID(challenge.getOpponentID()).isOver());
                    sendCustomNotification(dataSnapshot.child("name").getValue().toString() + " resigned!", "You won the challenge");
                    fireChallenge.removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void sendNotificationChallenge(String opponent, String ID, String urlImage) {

        new DownloadImgTask().execute(urlImage);

        Intent acceptIntent = new Intent(context, ChallengeAcceptReceiver.class);
        acceptIntent.putExtra("accept", true);
        acceptIntent.putExtra("opponent",ID);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent refuseIntent = new Intent(context, ChallengeAcceptReceiver.class);
        refuseIntent.putExtra("accept", false);
        refuseIntent.putExtra("opponent",ID);
        PendingIntent piDS = PendingIntent.getBroadcast(context, 0, refuseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(largeicon)
                        .setSmallIcon(R.drawable.stopitsymbollollipop)
                        .setContentTitle(opponent + " challenged you!")
                        .setContentText("Begin the challenge!")
                        .addAction(R.drawable.stopitsymbollollipop, "Refuse", piDS)
                        .addAction(R.drawable.stopitsymbollollipop, "Accept", pi)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, NavigationActivity.class);
        resultIntent.putExtra("redirect", "challenges");

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

    public void sendCustomNotification(String title, String text) {

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.stopitsymbol);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.stopitsymbollollipop)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, NavigationActivity.class);
        resultIntent.putExtra("redirect", "challenges");

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
        int notificationID = NotificationID.getID();
        mNM.notify(notificationID, mBuilder.build());
    }

    public int sendAlternativeNotification(AlternativeActivity alternativeActivity, int points) {

        int notificationID = NotificationID.getID();

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), alternativeActivity.getImage());

        Intent smokeIntent = new Intent(context, SmokeReceiver.class);
        smokeIntent.putExtra("points", points);
        smokeIntent.putExtra("notificationID", notificationID);
        smokeIntent.putExtra("smoke", true);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, smokeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent alternativeIntent = new Intent(context, SmokeReceiver.class);
        alternativeIntent.putExtra("points", alternativeActivity.getBonusPoints());
        alternativeIntent.putExtra("notificationID", notificationID);
        alternativeIntent.putExtra("alternative", alternativeActivity.getTitle());
        alternativeIntent.putExtra("alternativeCategory", alternativeActivity.getCategory());
        PendingIntent piDS = PendingIntent.getBroadcast(context, 0, alternativeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.stopitsymbollollipop)
                        .setContentTitle(alternativeActivity.getTitle())
                        .setContentText(alternativeActivity.getDescription())
                        .addAction(R.drawable.stopitsymbollollipop, "Alternative!", piDS)
                        .addAction(R.drawable.stopitsymbollollipop, "Smoke", pi)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, NavigationActivity.class);
        resultIntent.putExtra("alternative", alternativeActivity.getTitle());
        resultIntent.putExtra("alternativeCategory", alternativeActivity.getCategory());
        resultIntent.putExtra("points", points);

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
        mNM.notify(notificationID, mBuilder.build());
        return notificationID;
    }

    public AlternativeActivity chooseAlternative(int points) {

        List<AlternativeActivity> alternativeActivityList = db.getAllAlternative();
        List<AlternativeActivity> alternativeActivityListCandidate = new ArrayList<>();

        AlternativeActivity alternativeChoosen;
        int j = 0;

        for (AlternativeActivity activity : alternativeActivityList) {

            if (!settings.getBoolean(activity.getCategory(), false)) {
                alternativeActivityListCandidate.add(activity);
            }

        }

        ArrayList listWeight = new ArrayList();
        for (AlternativeActivity activitySelected : alternativeActivityList) {
            for (int i = 0; i < activitySelected.getFrequency(); i++) {
                listWeight.add(j);
            }
            j++;
        }

        if (alternativeActivityList.isEmpty()) {
            return null;
        }

        alternativeChoosen = alternativeActivityList.get((int) listWeight.get(new Random().nextInt(listWeight.size())));

        return alternativeChoosen;
    }

    public long[] getLevelXPs() {

        long[] levelsXP = new long[99];

        long difference = 1000;
        long xp = 1000;

        for (int i = 0; i < 99; i++) {

            levelsXP[i] = xp;
            xp += difference;
            difference += 100;
        }

        return levelsXP;
    }

    public String getLevel(long points) {

        String levelText;

        long[] levelsXP = getLevelXPs();

        int level = 1;

        for (int i = 0; i < 98; i++) {

            if (levelsXP[i] < points && levelsXP[i + 1] > points) {

                level = i + 2;

            } else if (levelsXP[i] == points) {

                level = i + 2;

            } else if (levelsXP[98] < points) {

                level = 100;
            }
        }

        if (level >= 10) {

            updateLevelAchievement(10);

            if (level >= 25) updateLevelAchievement(25);
            if (level >= 50) updateLevelAchievement(50);
            if (level >= 100) updateLevelAchievement(100);
        }

        if (level < 10) {

            levelText = "Level " + level + " - Beginner";

        } else if (level < 20) {

            levelText = "Level " + level + " - Novice";

        } else if (level < 30) {

            levelText = "Level " + level + " - Rookie";

        } else if (level < 40) {

            levelText = "Level " + level + " - Semi-Pro";

        } else if (level < 50) {

            levelText = "Level " + level + " - Pro";

        } else if (level < 60) {

            levelText = "Level " + level + " - Veteran";

        } else if (level < 70) {

            levelText = "Level " + level + " - Expert";

        } else if (level < 80) {

            levelText = "Level " + level + " - Magus";

        } else if (level < 90) {

            levelText = "Level " + level + " - Master";

        } else if (level < 100) {

            levelText = "Level " + level + " - Grandmaster";

        } else {

            levelText = "Level 100 - Legend";
        }

        return levelText;
    }

    public String getLevelPointsString(long points) {

        long[] levelsXP = getLevelXPs();
        String pointsLevelPoints = "";

        for (int i = 0; i < 98; i++) {

            if (levelsXP[i] < points && levelsXP[i + 1] > points) {

                return ((points - levelsXP[i]) + " / " + (levelsXP[i + 1] - levelsXP[i]) + "  points");

            } else if (levelsXP[i] == points) {

                return ("0 / " + (levelsXP[i + 1] - levelsXP[i]) + "  points");

            } else if (levelsXP[98] < points) {

                return points + " points";

            } else if (levelsXP[0] > points) {

                return points + "/" + levelsXP[0] + "  points";
            }

        }

        return pointsLevelPoints;

    }

    public long getLevelPoints(long points) {

        long[] levelsXP = getLevelXPs();
        long levelpoints = 1000;

        for (int i = 0; i < 98; i++) {

            if (levelsXP[i] < points && levelsXP[i + 1] > points) {

                return levelsXP[i + 1] - levelsXP[i];

            } else if (levelsXP[i] == points) {

                return levelsXP[i] - levelsXP[i + 1];

            } else if (levelsXP[98] < points) {

                levelpoints = levelsXP[98] - levelsXP[97];

            } else if (levelsXP[0] > points) {

                levelpoints = levelsXP[0];
            }
        }

        return levelpoints;
    }

    public long getPointsLevel(long points) {

        long[] levelsXP = getLevelXPs();
        long pointslevel = 0;

        for (int i = 0; i < 98; i++) {

            if (levelsXP[i] < points && levelsXP[i + 1] > points) {

                return points - levelsXP[i];

            } else if (levelsXP[i] == points) {

                return 0;

            } else if (levelsXP[98] < points) {

                pointslevel = points;

            } else if (levelsXP[0] > points) {

                pointslevel = points;
            }
        }

        return pointslevel;
    }

    public ArrayList<User> addTestContacts(ArrayList<User> contacts) {

        final ArrayList<User> testContacts = new ArrayList<>();

        testContacts.add(new User("1", "Paulo", "Dybala", "http://sortitoutsi.net/uploads/face/14044150.png", Long.parseLong("34110"), Long.parseLong("888"), Long.parseLong("17575"), "", ""));
        testContacts.add(new User("2", "Lionel", "Messi", "http://static2.blastingnews.com/media/photogallery/2015/10/8/290x290/b_290x290/lionel-messi-pode-terminar-a-carreira-dessa-forma_452055.jpg", Long.parseLong("89670"), Long.parseLong("3200"), Long.parseLong("18480"), "", ""));
        testContacts.add(new User("3", "Eden", "Hazard", "http://img.uefa.com/imgml/TP/players/9/2013/324x324/1902160.jpg", Long.parseLong("17923"), Long.parseLong("2280"), Long.parseLong("7920"), "", ""));
        testContacts.add(new User("4", "Scarlett", "Johansson", "https://pbs.twimg.com/profile_images/629741380957511680/cruVnLi2.jpg", Long.parseLong("24560"), Long.parseLong("1277"), Long.parseLong("6800"), "", ""));
        testContacts.add(new User("5", "Guido", "Meda", "http://www.motocorse.com/foto/22762/thumbs500/1.jpg", Long.parseLong("79800"), Long.parseLong("560"), Long.parseLong("18340"), "", ""));
        testContacts.add(new User("6", "Federica", "Nargi", "http://pbs.twimg.com/profile_images/665605062585155584/fhGO4ZY9_reasonably_small.jpg", Long.parseLong("48267"), Long.parseLong("650"), Long.parseLong("12700"), "", ""));
        testContacts.add(new User("7", "Alessandro", "Del Piero", "http://d1ktyob8e4hu6c.cloudfront.net/pub/avatar/RUNA_9839085/communitygazzetta/Il%20futuro%20di%20Del%20Piero.jpg", Long.parseLong("68880"), Long.parseLong("721"), Long.parseLong("8180"), "", ""));
        testContacts.add(new User("8", "Gianluigi", "Buffon", "http://i.imgur.com/9VYiq8e.png", Long.parseLong("11450"), Long.parseLong("1000"), Long.parseLong("3200"), "", ""));
        testContacts.add(new User("9", "Stephen", "Curry", "http://www.sportsspeakers360.com/admin/img/stephen-curry.jpg", Long.parseLong("43200"), Long.parseLong("100"), Long.parseLong("2950"), "", ""));
        testContacts.add(new User("10", "Joe", "Bastianich", "http://www.foodserviceconsultant.org/wp-content/uploads/Joe-Bastianich250.jpg", Long.parseLong("54277"), Long.parseLong("-250"), Long.parseLong("1200"), "", ""));

        Firebase.setAndroidContext(context);
        final Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

        for (int i = 0; i < testContacts.size(); i++) {

            final User contact = testContacts.get(i);

            if (!contacts.contains(contact)) {

                contacts.add(contact);
            }

            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (!snapshot.child(contact.getID()).exists()) {

                        myFirebaseRef.child(contact.getID()).setValue(contact);

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
        return contacts;
    }

    public String challengeWonLost() {
        return (db.getAllWonChallenges().size() + "/" + db.getAllChallenges().size());
    }

    private class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            for (String url : urls) {
                return getCircleBitmap(getBitmapFromURL(url));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            largeicon = result;
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public void dailyUpdate() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        MutableDateTime now = new MutableDateTime();
        MutableDateTime lastDayCheck = getConvertedTime(settings.getString("lastDayCheck", null));

        Calendar calendar = Calendar.getInstance();
        now.setMonthOfYear(calendar.get(Calendar.MONTH) + 1);

        if (lastDayCheck.getYear() == now.getYear()) {

            if (lastDayCheck.getDayOfYear() < now.getDayOfYear()) {

                Firebase.setAndroidContext(context);
                Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

                myFirebaseRef.child(settings.getString("ID", null)).child("lastDayCheck").setValue(getStringTime(now));
                myFirebaseRef.child(settings.getString("ID", null)).child("dayPoints").setValue(0);

                settings.edit().putLong("dayPoints", 0).apply();
                settings.edit().putString("lastDayCheck", getStringTime(now)).apply();

                dailyMoneyControl();
                stopProgramControl();

            }

        } else if (lastDayCheck.getYear() < now.getYear()) {

            Firebase.setAndroidContext(context);
            Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

            myFirebaseRef.child(settings.getString("ID", null)).child("lastDayCheck").setValue(getStringTime(now));
            myFirebaseRef.child(settings.getString("ID", null)).child("dayPoints").setValue(0);

            settings.edit().putLong("dayPoints", 0).apply();
            settings.edit().putString("lastDayCheck", getStringTime(now)).apply();

            dailyMoneyControl();
            stopProgramControl();
        }
    }

    public void weeklyUpdate() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        MutableDateTime now = new MutableDateTime();
        MutableDateTime lastWeekCheck = getConvertedTime(settings.getString("lastWeekCheck", null));

        Calendar calendar = Calendar.getInstance();
        now.setMonthOfYear(calendar.get(Calendar.MONTH) + 1);

        if (lastWeekCheck.getYear() == now.getYear()) {

            if (lastWeekCheck.getDayOfYear() + 7 <= now.getDayOfYear()) {

                Firebase.setAndroidContext(context);
                Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

                myFirebaseRef.child(settings.getString("ID", null)).child("lastWeekCheck").setValue(getStringTime(now));
                myFirebaseRef.child(settings.getString("ID", null)).child("weekPoints").setValue(0);

                settings.edit().putString("lastWeekCheck", getStringTime(now)).apply();
                settings.edit().putLong("weekPoints", 0).apply();

            }


        } else if (lastWeekCheck.getYear() < now.getYear()) {

            if ((365 - lastWeekCheck.getDayOfYear()) + now.getDayOfYear() >= 7) {

                Firebase.setAndroidContext(context);
                Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

                myFirebaseRef.child(settings.getString("ID", null)).child("lastWeekCheck").setValue(getStringTime(now));
                myFirebaseRef.child(settings.getString("ID", null)).child("weekPoints").setValue(0);

                settings.edit().putString("lastWeekCheck", getStringTime(now)).apply();
                settings.edit().putLong("weekPoints", 0).apply();

            }

        }
    }

    public MutableDateTime getConvertedTime(String dateString) {

        MutableDateTime date = new MutableDateTime();

        try {

            String[] columns = dateString.split("/");

            date.setDate(Integer.parseInt(columns[2]), Integer.parseInt(columns[1]), Integer.parseInt(columns[0]));
            return date;

        } catch (Exception e) {

            System.out.println("date cannot be splitted !");
        }

        return null;
    }

    public String getStringTime(MutableDateTime date) {


        return date.getDayOfMonth() + "/" + date.getMonthOfYear() + "/" + date.getYear();
    }

    public String getSundayStringTime(MutableDateTime date) {

        if (date.getDayOfWeek() == 7) {

            return date.getDayOfMonth() + "/" + date.getMonthOfYear() + "/" + date.getYear();

        } else if (date.getDayOfMonth() - date.getDayOfWeek() > 0) {

            date.set(DateTimeFieldType.dayOfMonth(), date.getDayOfMonth() - date.getDayOfWeek());

            return date.getDayOfMonth() + "/" + date.getMonthOfYear() + "/" + date.getYear();

        } else {

            date.set(DateTimeFieldType.monthOfYear(), date.getMonthOfYear() - 1);
            date.set(DateTimeFieldType.dayOfMonth(), date.dayOfMonth().getMaximumValue() + date.getDayOfMonth() - date.getDayOfWeek());
            return date.getDayOfMonth() + "/" + date.getMonthOfYear() + "/" + date.getYear();
        }

    }

    public byte[] convertImageToBytes(String urlString) {

        URL url = null;
        try {

            url = new URL(urlString);

        } catch (MalformedURLException e) {

            e.printStackTrace();
        }

        InputStream is = null;
        byte[] imageBytes = null;

        try {
            if (url != null) {
                is = url.openStream();
            }
            imageBytes = getBytes(is);
        } catch (IOException e) {

            e.printStackTrace();

        }

        return imageBytes;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void cancelNotification(final int nID, long delay, final int points) {

        final NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Handler h = new Handler();
        long delayInMilliseconds = delay;
        h.postDelayed(new Runnable() {

            public void run() {

                mNM.cancel(nID);
                updatePoints(points);

            }

        }, delayInMilliseconds);
    }

    public void buildStopProgram(int numCig, int newDays) {

        if (numCig == 100) {

            numCig = settings.getInt("CPD", 0);
        }

        if (numCig > 0) {

            if (newDays == 0) {

                newDays = settings.getInt("daysToRed", 0);
            }

            int interval = newDays / numCig;

            if (interval == 0) interval++;
            settings.edit().putInt("redInterval", interval).commit();

            System.out.println("Stop schedule for " + newDays + " days from now has been setted");
            System.out.println("Reduction every " + interval + " days");

        } else {

            notifyStopCompleted();
        }
    }

    public void stopProgramControl() {

        int remainDays = settings.getInt("daysToRed", 0);

        if (remainDays > 1) {

            remainDays--;
            settings.edit().putInt("daysToRed", remainDays).commit();

            int interval = settings.getInt("redInterval", 0);

            if (interval == 1) {

                int CPD = settings.getInt("CPD", 0);

                if (CPD > 1) {

                    CPD--;
                    settings.edit().putInt("CPD", CPD).commit();
                    buildStopProgram(CPD, 0);

                } else {

                    settings.edit().putInt("daysToRed", 0).commit();
                    settings.edit().putInt("CPD", 0).commit();
                    notifyStopCompleted();
                }

            } else {

                interval--;
                settings.edit().putInt("redInterval", interval).commit();
            }

        } else {

            settings.edit().putInt("daysToRed", 0).commit();
            settings.edit().putInt("CPD", 0).commit();
            notifyStopCompleted();
        }

    }

    public void notifyStopCompleted() {

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.legend);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.stopitsymbollollipop)
                        .setContentTitle("YOU DID!")
                        .setContentText("Congratulations you have stopped smoking!")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, NavigationActivity.class);
        resultIntent.putExtra("redirect", "stopped");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NavigationActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        int notificationID = NotificationID.getID();
        mNM.notify(notificationID, mBuilder.build());


    }

}
