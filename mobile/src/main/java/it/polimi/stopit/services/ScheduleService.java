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
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.firebase.client.Firebase;

import org.joda.time.MutableDateTime;
import org.joda.time.MutableInterval;

import java.util.ArrayList;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.ChooseActivity;

/**
 * Created by matteo on 13/12/15.
 */

public class ScheduleService extends Service {
    private NotificationManager mNM;
    public static final String PREFS_NAME = "StopItPrefs";
    private List<MutableInterval> list;
    MutableDateTime start=new MutableDateTime();
    MutableDateTime end=new MutableDateTime();
    CountDownTimer Count;
    int n=0;
    private BroadcastReceiver uiUpdated=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Count.cancel();
            if(intent.getSerializableExtra("time")!=null){
                list = shiftIntervals((MutableDateTime) intent.getSerializableExtra("time"), list);
                System.out.println(intent.getSerializableExtra("time"));
                System.out.println(list);
            }
            Count=new CountDownTimer(nextCiga(list,start,end), 1000) {
                public void onTick(long millisUntilFinished) {

                    Intent i = new Intent("COUNTDOWN_UPDATED");
                    i.putExtra("countdown", millisUntilFinished);

                    sendBroadcast(i);
                }

                public void onFinish() {
                    sendNotification();
                    Handler h = new Handler();
                    long delayInMilliseconds = 300000;
                    h.postDelayed(new Runnable() {
                        public void run() {
                            mNM.cancel(n);
                            SharedPreferences p=getSharedPreferences(PREFS_NAME, 0);
                            Firebase.setAndroidContext(ScheduleService.this);
                            final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
                            long points=p.getLong("points",0);
                            fire.child(p.getString("ID", null)).child("points").setValue(points + 100);
                        }
                    }, delayInMilliseconds);
                    this.start();
                }
            }.start();
        }
    };;



    @Override
    public void onCreate(){
        super.onCreate();
        firstStart();
        Count=new CountDownTimer(nextCiga(list,start,end), 1000) {
            public void onTick(long millisUntilFinished) {

                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown", millisUntilFinished);

                sendBroadcast(i);
            }

            public void onFinish() {
                sendNotification();
                Handler h = new Handler();
                long delayInMilliseconds = 300000;
                h.postDelayed(new Runnable() {
                    public void run() {
                        mNM.cancel(n);
                        SharedPreferences p=getSharedPreferences(PREFS_NAME, 0);
                        Firebase.setAndroidContext(ScheduleService.this);
                        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");
                        long points=p.getLong("points",0);
                        fire.child(p.getString("ID", null)).child("points").setValue(points + 100);
                    }
                }, delayInMilliseconds);
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
        unregisterReceiver(uiUpdated);
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

    public void firstStart(){

        SharedPreferences userdata = getSharedPreferences(PREFS_NAME, 0);

        start.setHourOfDay(11);
        start.setMinuteOfHour(0);
        end.setHourOfDay(20);
        end.setMinuteOfHour(0);

        list = splitDuration(start, end, (long) userdata.getInt("CPD",0));
    }


    public long nextCiga(List<MutableInterval> list,MutableDateTime start,MutableDateTime end){

        long nextCiga=3000000;
        MutableDateTime now=new MutableDateTime();

        if(end.isBeforeNow()){
            nextCiga=(start.getMillis()+86400000)-now.getMillis();
        }

        if(start.isAfterNow()){
            nextCiga=start.getMillis()-now.getMillis();
        }

        for(MutableInterval i : list){
            if(i.contains(now)){
                nextCiga=i.getEndMillis()-now.getMillis();
                break;
            }
        }
        System.out.println(list);
        System.out.println(nextCiga);
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

        return list;
    }

    static List<MutableInterval> shiftIntervals(MutableDateTime time, List<MutableInterval> list) {
        long shift=0;
        for(MutableInterval i : list){
            if(i.contains(time)){
                shift=time.getMillis()-i.getStartMillis();
                i.setInterval(time.getMillis(),i.getEndMillis()+shift);
                continue;
            }
            i.setInterval(i.getStartMillis()+shift,i.getEndMillis()+shift);
        }

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


        Intent resultIntent = new Intent(this, ChooseActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ChooseActivity.class);
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
