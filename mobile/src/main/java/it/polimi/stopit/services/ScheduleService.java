package it.polimi.stopit.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


import java.util.Calendar;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.NavigationActivity;

/**
 * Created by matteo on 13/12/15.
 */

public class ScheduleService extends Service {
    private NotificationManager mNM;
    public static final String PREFS_NAME = "StopItPrefs";
    CountDownTimer Count;
    private BroadcastReceiver uiUpdated=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Count.cancel();
            Count.start();
        }
    };;
    private long nextCigarette;
    int n=0;


    @Override
    public void onCreate(){
        super.onCreate();
        Count=new CountDownTimer(nextCiga(), 1000) {
            public void onTick(long millisUntilFinished) {

                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown", millisUntilFinished);

                sendBroadcast(i);
            }

            public void onFinish() {
                sendNotification();
                this.start();
            }
        }.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, flags, startId);

            registerReceiver(uiUpdated, new IntentFilter("SMOKE_OUTOFTIME"));

            return START_STICKY;
    }

    @Override
    public void onDestroy() {
    }

    IBinder mBinder = new LocalBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ScheduleService getServerInstance() {
            return ScheduleService.this;
        }
    }


    public long nextCiga(){

            SharedPreferences userdata = getSharedPreferences(PREFS_NAME, 0);
            Calendar c = Calendar.getInstance();
            int hourNow = c.get(Calendar.HOUR_OF_DAY);
            int minuteNow = c.get(Calendar.MINUTE);
            int secondNow = c.get(Calendar.SECOND);

            long hLast=userdata.getInt("hoursLast", 0);
            long mLast=userdata.getInt("minuteLast", 0);
            long hFirst=userdata.getInt("hoursFirst", 0);
            long mFirst = userdata.getInt("minuteFirst", 0);

            int milliNow = hourNow * 60 * 60 * 1000 + minuteNow * 60 * 1000 + secondNow * 1000;
            long nextCiga = userdata.getLong("interval", 0);

            long timeLast=((hLast*60)+mLast)*60*1000;
            long timeFirst=((hFirst*60)+mFirst)*60*1000;

            if ( (milliNow+nextCiga) > timeLast){
                nextCiga=timeFirst;
            }
            System.out.println(nextCiga);
            return nextCiga;
    }

    public void sendNotification() {
        n+=1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.stopitsymbol)
                        .setContentTitle("You can smoke")
                        .setContentText("You earned it");
        // Sets an ID for the notification
        int mNotificationId = n;


        Intent resultIntent = new Intent(this, NavigationActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
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
        mNM =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNM.notify(mNotificationId, mBuilder.build());
    }
}
