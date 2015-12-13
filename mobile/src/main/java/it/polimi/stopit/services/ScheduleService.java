package it.polimi.stopit.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import it.polimi.stopit.R;

/**
 * Created by matteo on 13/12/15.
 */
public class ScheduleService extends Service {
    private NotificationManager mNM;
    public static final String PREFS_NAME = "StopItPrefs";
    public static final int NOTIFICATION = 1;
    CountDownTimer Count;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Count = new CountDownTimer(scheduleProgram(), 1000) {
            public void onTick(long millisUntilFinished) {

                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown",millisUntilFinished);

                sendBroadcast(i);
                //coundownTimer.setTitle(millisUntilFinished / 1000);

            }

            public void onFinish() {
                sendNotification();
                this.start();
            }
        };

        Count.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long scheduleProgram(){

        long wakefulness,interval;
        int hFirst,hLast,mFirst,mLast,CPD;

        SharedPreferences userdata = getSharedPreferences(PREFS_NAME, 0);

        hLast=userdata.getInt("hoursLast",0);
        mLast=userdata.getInt("minuteLast",0);
        hFirst=userdata.getInt("hoursFirst",0);
        mFirst=userdata.getInt("minuteFirst",0);
        CPD=userdata.getInt("CPD",0);

        Calendar c = Calendar.getInstance();
        int hourNow = c.get(Calendar.HOUR_OF_DAY);
        int minuteNow = c.get(Calendar.MINUTE);
        int secondNow = c.get(Calendar.SECOND);
        int milliNow=hourNow*60*60*1000+minuteNow*60*1000+secondNow*1000;


        wakefulness=(long)((hLast*60)+mLast)-((hFirst*60)+mFirst);
        interval=wakefulness/CPD;
        interval=interval*60*1000;

        int timer=milliNow;

        while(timer > interval) {
            timer -= interval;
        }

        long nextCiga=milliNow+(interval-timer);

        return interval-timer;
    }
    public void sendNotification() {
        int n=0;
        n+=1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.stopitsymbol)
                        .setContentTitle("You can smoke")
                        .setContentText("You earned it");
        // Sets an ID for the notification
        int mNotificationId = n;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
