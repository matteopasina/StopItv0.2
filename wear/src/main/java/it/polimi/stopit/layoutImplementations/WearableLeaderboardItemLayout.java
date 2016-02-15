package it.polimi.stopit.layoutImplementations;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import it.polimi.stopit.R;

/**
 * Created by matteo on 10/02/16.
 */
public class WearableLeaderboardItemLayout extends RelativeLayout
        implements WearableListView.OnCenterProximityListener {

    private CircularImageView mCircle;
    private TextView mName;
    private TextView mPoints;
    private TextView mPosition;

    private final float mFadedTextAlpha;

    public WearableLeaderboardItemLayout(Context context) {
        this(context, null);
    }

    public WearableLeaderboardItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableLeaderboardItemLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);

        mFadedTextAlpha = 40 / 100f;

    }

    // Get references to the icon and text in the item layout definition
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // These are defined in the layout file for list items
        // (see next section)
        mCircle = (CircularImageView) findViewById(R.id.circle);
        mName = (TextView) findViewById(R.id.name);
        mPoints = (TextView) findViewById(R.id.points);

        try{

            mPosition = (TextView) findViewById(R.id.position);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onCenterPosition(boolean animate) {

        mName.setAlpha(1f);
        mPoints.setAlpha(1f);
        if(mPosition!=null){
            mPosition.setAlpha(1f);
        }

    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        mCircle.setMaxWidth(50);
        mName.setAlpha(mFadedTextAlpha);

        if(mPosition!=null){
            mPosition.setAlpha(mFadedTextAlpha);
        }
        mPoints.setAlpha(mFadedTextAlpha);
    }
}
