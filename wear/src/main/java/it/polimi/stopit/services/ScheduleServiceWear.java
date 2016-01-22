package it.polimi.stopit.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import org.joda.time.MutableDateTime;
import org.joda.time.MutableInterval;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScheduleServiceWear extends Service {
    public ScheduleServiceWear() {
    }

    private static List<MutableInterval> list;
    private static long nextCiga;
    private static MutableDateTime start;
    private static MutableDateTime end;
    private boolean beginOfDay = false;
    CountDownTimer Count;

    /*
    * Receives the broadcast from the button smoke on the main screen, the restarts the
    * timer with time shifted
    * */
    private BroadcastReceiver uiUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Count.cancel();

            start=new MutableDateTime();
            end=new MutableDateTime();

            SharedPreferences userdata = PreferenceManager.getDefaultSharedPreferences(ScheduleServiceWear.this);

            start.setMillis(intent.getLongExtra("start",0));
            end.setMillis(intent.getLongExtra("end", 0));
            userdata.edit().putLong("CPD", intent.getLongExtra("CPD", 0)).apply();

            start.setSecondOfMinute(0);
            end.setSecondOfMinute(0);

            list = splitDuration(start, end, intent.getLongExtra("CPD", 0));
            saveSchedule(list);
            nextCiga(list, start, end);

            Count = null;
            setCount(nextCiga);

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        deleteFile("schedule");

        //setta lo schedule per la prima esecuzione
        firstStart();

        nextCiga(list, start, end);
        setCount(nextCiga);
    }


    public void setCount(long next) {
        Count = new CountDownTimer(next, 1000) {
            public void onTick(long millisUntilFinished) {

                Intent i = new Intent("TIMER");
                i.putExtra("countdown", millisUntilFinished);

                sendBroadcast(i);

            }

            public void onFinish() {

                if (beginOfDay) {
                    deleteFile("schedule");
                    firstStart();
                    beginOfDay = false;
                }

                nextCiga(list, start, end);

                this.cancel();
                Count = null;
                setCount(nextCiga);
            }
        }.start();
    }

    public void firstStart() {
        list = loadSchedule();
        if (list == null) {

            start = new MutableDateTime();
            end = new MutableDateTime();

            start.setHourOfDay(9);
            start.setMinuteOfHour(0);
            end.setHourOfDay(23);
            end.setMinuteOfHour(0);

            list = splitDuration(start, end, Long.valueOf(25));
            saveSchedule(list);

        }
    }

    public void nextCiga(List<MutableInterval> list, MutableDateTime start, MutableDateTime end) {

        MutableDateTime now = new MutableDateTime();

        if (end.isBeforeNow()) {

            beginOfDay = true;
            nextCiga = (start.getMillis() + 86400000) - now.getMillis();
            System.out.println("endbefore" + end);

        } else if (start.isAfterNow()) {
            beginOfDay = true;
            nextCiga = start.getMillis() - now.getMillis();
            System.out.println("startafter");

        } else {

            for (MutableInterval i : list) {
                if (i.contains(now)) {

                    nextCiga = i.getEndMillis() - now.getMillis();
                    System.out.println("normal");
                    break;

                }
            }
        }
    }

    public void saveSchedule(List<MutableInterval> list) {
        String filename = "schedule";

        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<MutableInterval> loadSchedule() {
        String filename = "schedule";
        List<MutableInterval> lista;

        try {
            FileInputStream fis = this.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            lista = (List<MutableInterval>) ois.readObject();
            ois.close();
            return lista;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static List<MutableInterval> splitDuration(MutableDateTime start, MutableDateTime end, long chunkAmount) {

        long millis = start.getMillis();
        long endMillis = end.getMillis();
        long chunkSize = (endMillis - millis) / chunkAmount;

        List<MutableInterval> list = new ArrayList<MutableInterval>();

        for (int i = 0; i < chunkAmount; ++i) {
            list.add(new MutableInterval(millis, millis += chunkSize));
        }

        return list;
    }

    static List<MutableInterval> shiftIntervals(MutableDateTime time, List<MutableInterval> list) {
        long shift = 0;
        for (MutableInterval i : list) {
            if (i.contains(time)) {
                shift = time.getMillis() - i.getStartMillis();
                i.setInterval(time.getMillis(), i.getEndMillis() + shift);
                continue;
            }
            i.setInterval(i.getStartMillis() + shift, i.getEndMillis() + shift);
        }

        return list;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        registerReceiver(uiUpdated, new IntentFilter("SET_SCHEDULE"));

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
