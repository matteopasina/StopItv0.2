package it.polimi.stopit.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import it.polimi.stopit.R;

public class ClockActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.clock_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                final TextView timerText = (TextView) stub.findViewById(R.id.timer);
                timerText.setText("00:00:00");

                final DecoView arcHours = (DecoView) stub.findViewById(R.id.circle_hours);
                final DecoView arcMinutes = (DecoView) stub.findViewById(R.id.circle_minutes);
                final DecoView arcSeconds = (DecoView) stub.findViewById(R.id.circle_seconds);

                DisplayMetrics dm = getResources().getDisplayMetrics();
                float dpInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, dm);

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
            }
        });

        stub.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent launchCall = new Intent(ClockActivity.this, MenuActivity.class);
                startActivity(launchCall);
                return true;
            }
        });
    }
}
