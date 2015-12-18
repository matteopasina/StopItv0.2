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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private static List<MutableInterval> list;
    private static long nextCiga;
    private static MutableDateTime start=new MutableDateTime();
    private static MutableDateTime end=new MutableDateTime();
    CountDownTimer Count,Count2;
    int n=0;
    /*
    * Receives the boroadcast from the button smoke on the main screen, the restarts the
    * timer with time shifted
    * */
    private BroadcastReceiver uiUpdated=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Count.cancel();
            if(intent.getSerializableExtra("time")!=null){
                list = shiftIntervals((MutableDateTime) intent.getSerializableExtra("time"), list);
                nextCiga(list,start,end);
                saveSchedule(list);
                System.out.println(intent.getSerializableExtra("time"));
                System.out.println(list);
            }
            Count=null;
            setCount(nextCiga);
        }
    };;



    @Override
    public void onCreate(){
        super.onCreate();
        firstStart();
        nextCiga(list, start, end);
        setCount(nextCiga);
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

    public void setCount(long next){
        Count=new CountDownTimer(next, 1000) {
            public void onTick(long millisUntilFinished) {

                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown", millisUntilFinished);

                sendBroadcast(i);

            }

            public void onFinish() {

                sendNotification(calcPoints());
                nextCiga(list, start, end);

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
                this.cancel();
                Count=null;
                setCount(nextCiga);
            }
        }.start();
    }

    public void firstStart(){
        list=loadSchedule();
        if(list==null) {

            SharedPreferences userdata = getSharedPreferences(PREFS_NAME, 0);

            start.setHourOfDay(9);
            start.setMinuteOfHour(0);
            end.setHourOfDay(23);
            end.setMinuteOfHour(0);

            list = splitDuration(start, end, (long) userdata.getInt("CPD", 0));
            System.out.println("first start");
            saveSchedule(list);
        }
    }


    public void nextCiga(List<MutableInterval> list,MutableDateTime start,MutableDateTime end){

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
        System.out.println("nextciga list:"+list);
        System.out.println(nextCiga);
    }

    public void saveSchedule(List<MutableInterval> list){
        String filename = "schedule";

        try {
            FileOutputStream fos = this.openFileOutput (filename, Context.MODE_PRIVATE );
            ObjectOutputStream oos = new ObjectOutputStream( fos );
            oos.writeObject ( list );
            oos.close ();
        } catch ( Exception ex ) {
            ex.printStackTrace ();
        }
    }

    public List<MutableInterval> loadSchedule(){
        String filename = "schedule";
        List<MutableInterval> lista;

        try {
            FileInputStream fis = this.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lista=(List<MutableInterval>) ois.readObject();
            ois.close ();
            return lista;
        } catch ( Exception ex ) {
            ex.printStackTrace ();
        }
        return null;
    }

    public int calcPoints(){
        long points=0;
        MutableDateTime now=new MutableDateTime();
        now.setSecondOfDay(now.getSecondOfDay()-5);
        for(MutableInterval i : list){
            if(i.contains(now)){
                points=(i.getEndMillis()-i.getStartMillis())/60000;
                points+=0.5;
                break;
            }
        }
        return (int)points;
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

    public void sendNotification(int points) {
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

        resultIntent.putExtra("points",points);

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
