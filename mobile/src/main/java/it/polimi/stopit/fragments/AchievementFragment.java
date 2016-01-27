package it.polimi.stopit.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.adapters.AchievementRecyclerViewAdapter;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Achievement;

public class AchievementFragment extends Fragment {

    public AchievementFragment() {
    }

    public static Fragment newInstance() {

        return new AchievementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement_list, container, false);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Achievement> mAchievements = db.getAllAchievements();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerViewHeader header = (RecyclerViewHeader) view.findViewById(R.id.header);

        recyclerView.setHasFixedSize(true);

        AchievementRecyclerViewAdapter adapter = new AchievementRecyclerViewAdapter(mAchievements);

        int itemCount=adapter.getItemCount();
        int numAchObt=db.getAchievementsObtCount();

        DecoView achProgress = (DecoView) view.findViewById(R.id.achievem_progress);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float dpInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);

        achProgress.addSeries(new SeriesItem.Builder(Color.argb(255, 240, 240, 240))
                .setRange(0, itemCount, itemCount)
                .setInitialVisibility(true)
                .setSpinDuration(3000)
                .setLineWidth(dpInPx)
                .build());

        SeriesItem progress = new SeriesItem.Builder(Color.parseColor("#039BE5"))
                .setRange(0, itemCount, 0)
                .setLineWidth(dpInPx)
                .setSpinDuration(3000)
                .build();

        int seriesIndex = achProgress.addSeries(progress);

        achProgress.addEvent(new DecoEvent.Builder(numAchObt).setIndex(seriesIndex).setDelay(500).build());

        TextView unlockedDesc = (TextView) view.findViewById(R.id.unlocked);

        unlockedDesc.setText(numAchObt + "/" + itemCount + "  unlocked");

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        header.attachTo(recyclerView, true);

        recyclerView.setAdapter(adapter);


        return view;
    }
}
