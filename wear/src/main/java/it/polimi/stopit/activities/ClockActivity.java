package it.polimi.stopit.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.joda.time.MutableDateTime;

import it.polimi.stopit.OnSwipeTouchListener;
import it.polimi.stopit.R;
import it.polimi.stopit.database.DatabaseSeederWear;
import it.polimi.stopit.services.ScheduleServiceWear;
import it.polimi.stopit.services.WearListenerService;

public class ClockActivity extends Activity {

    private GoogleApiClient mGoogleApiClient;
    private BroadcastReceiver uiUpdated;
    private DatabaseSeederWear db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = p.getBoolean("firstRun", true);
        if(firstRun){
            db=new DatabaseSeederWear(this);
            db.seedAchievements();
        }
        p.edit().putBoolean("firstRun", false).commit();


        startService(new Intent(this, WearListenerService.class));

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.clock_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                final Button smoke =(Button) stub.findViewById(R.id.smoke);

                final TextView timerText = (TextView) stub.findViewById(R.id.timer);
                timerText.setText("00:00:00");

                final DecoView arcHours = (DecoView) stub.findViewById(R.id.circle_hours);
                final DecoView arcMinutes = (DecoView) stub.findViewById(R.id.circle_minutes);
                final DecoView arcSeconds = (DecoView) stub.findViewById(R.id.circle_seconds);

                DisplayMetrics dm = getResources().getDisplayMetrics();
                float dpInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);

                // Create background track
                arcHours.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                        .setRange(0, 100, 100)
                        .setInitialVisibility(true)
                        .setSpinDuration(500)
                        .setLineWidth(dpInPx)
                        .build());

                arcMinutes.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                        .setRange(0, 100, 100)
                        .setInitialVisibility(true)
                        .setSpinDuration(500)
                        .setLineWidth(dpInPx)
                        .build());

                arcSeconds.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                        .setRange(0, 100, 100)
                        .setInitialVisibility(true)
                        .setSpinDuration(500)
                        .setLineWidth(dpInPx)
                        .build());

                //Create data series track
                SeriesItem hourSeries = new SeriesItem.Builder(Color.parseColor("#039BE5"))
                        .setRange(0, 100, 0)
                        .setLineWidth(dpInPx)
                        .setSpinDuration(1000)
                        .build();

                SeriesItem minuteSeries = new SeriesItem.Builder(Color.parseColor("#00E676"))
                        .setRange(0, 100, 0)
                        .setLineWidth(dpInPx)
                        .setSpinDuration(1000)
                        .build();

                final SeriesItem secondSeries = new SeriesItem.Builder(Color.parseColor("#FFC107"))
                        .setRange(0, 100, 0)
                        .setLineWidth(dpInPx)
                        .setSpinDuration(1000)
                        .build();

                final int series1Index = arcHours.addSeries(hourSeries);
                final int series2Index = arcMinutes.addSeries(minuteSeries);
                final int series3Index = arcSeconds.addSeries(secondSeries);

                arcHours.addEvent(new DecoEvent.Builder(100).setIndex(series1Index).setDelay(0).build());
                arcMinutes.addEvent(new DecoEvent.Builder(100).setIndex(series2Index).setDelay(0).build());
                arcSeconds.addEvent(new DecoEvent.Builder(100).setIndex(series3Index).setDelay(0).build());

                uiUpdated = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        //This is the part where I get the timer value from the service and I update it every second, because I send the data from the service every second. The coundtdownTimer is a MenuItem
                        long millisUntilFinished = intent.getExtras().getLong("countdown");
                        setTimer(timerText, millisUntilFinished);

                        long hours = millisUntilFinished / 3600000;
                        long minutes = (millisUntilFinished - (hours * 3600000)) / 60000;
                        long seconds = (millisUntilFinished - (hours * 3600000) - (minutes * 60000)) / 1000;

                        arcHours.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 24) * hours)).setIndex(series1Index).setDelay(0).build());
                        arcMinutes.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 60) * minutes)).setIndex(series2Index).setDelay(0).build());
                        arcSeconds.addEvent(new DecoEvent.Builder(100 - (((float) 100 / 60) * seconds)).setIndex(series3Index).setDelay(0).build());

                    }
                };

                startService(new Intent(ClockActivity.this, ScheduleServiceWear.class));
                try {
                    registerReceiver(uiUpdated, new IntentFilter("TIMER"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                smoke.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendSmoke();
                        Intent intent = new Intent(ClockActivity.this, ConfirmationActivity.class);
                        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                ConfirmationActivity.SUCCESS_ANIMATION);
                        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                                getString(R.string.msg_sent));
                        startActivity(intent);
                    }
                });

            }
        });

        askMobile();

        stub.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent launchMenu = new Intent(ClockActivity.this, MenuActivity.class);
                startActivity(launchMenu);
            }
        });

    }

    public void setTimer(TextView timerText, long millis) {

        long hours = millis / 3600000;
        long minutes = (millis - (hours * 3600000)) / 60000;
        long seconds = (millis - (hours * 3600000) - (minutes * 60000)) / 1000;

        if (hours >= 10) {
            if (minutes >= 10) {
                if (seconds >= 10) {

                    timerText.setText(hours + ":" + minutes + ":" + seconds);
                } else {

                    timerText.setText(hours + ":" + minutes + ":0" + seconds);
                }
            } else {

                if (seconds >= 10) {

                    timerText.setText(hours + ":0" + minutes + ":" + seconds);
                } else {

                    timerText.setText(hours + ":0" + minutes + ":0" + seconds);
                }
            }
        } else {

            if (minutes >= 10) {
                if (seconds >= 10) {

                    timerText.setText("0" + hours + ":" + minutes + ":" + seconds);
                } else {

                    timerText.setText("0" + hours + ":" + minutes + ":0" + seconds);
                }
            } else {

                if (seconds >= 10) {

                    timerText.setText("0" + hours + ":0" + minutes + ":" + seconds);

                } else {

                    timerText.setText("0" + hours + ":0" + minutes + ":0" + seconds);

                }
            }
        }
    }


    public void askMobile() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        // Now you can use the Data Layer API
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/askMobile");
        putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

    }

    public void sendSmoke(){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stopit/smoke");
        putDataMapReq.getDataMap().putLong("timestamp", new MutableDateTime().getMillis());
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

}
