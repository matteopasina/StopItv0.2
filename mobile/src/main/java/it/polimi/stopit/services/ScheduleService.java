package it.polimi.stopit.services;

import android.app.Notification;
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


import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.MutableInterval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        long nextCiga=3000000;

        MutableDateTime start=new MutableDateTime();
        start.setHourOfDay(8);
        start.setMinuteOfHour(0);
        MutableDateTime end=new MutableDateTime();
        end.setHourOfDay(23);
        end.setMinuteOfHour(0);

        DateTime now = new DateTime();

        List<MutableInterval> list = splitDuration(start, end, (long) userdata.getInt("CPD",0));

        if(end.isBeforeNow()){
            nextCiga=start.getMillis()+86400000;
        }

        for(MutableInterval i : list){
            if(i.contains(now)){
                nextCiga=i.getEndMillis()-now.getMillis();
            }
        }

        return nextCiga;
    }

    static List<MutableInterval> splitDuration(MutableDateTime start, MutableDateTime end, long chunkAmount) {

        long millis = start.getMillis();
        long endMillis=end.getMillis();
        long chunkSize=(endMillis-millis)/chunkAmount;

        List<MutableInterval> list = new ArrayList<MutableInterval>();

        for(int i = 0; i < chunkAmount; ++i) {
            list.add(new MutableInterval(millis, millis += chunkSize));
        }

        list.add(new MutableInterval(millis, endMillis));
        return list;
    }

    public void sendNotification() {
        n+=1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.stopitsymbol)
                        .setContentTitle("You can smoke")
                        .setContentText("You earned it")
                        .setAutoCancel(true);
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
