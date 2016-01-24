package it.polimi.stopit.layoutImplementations;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it.polimi.stopit.R;

/**
 * Created by matteo on 24/01/16.
 */
public class WearableChallengeItemLayout extends RelativeLayout
        implements WearableListView.OnCenterProximityListener {

    private ImageView mypic,opponentpic;
    private TextView me,opponent,mypoints,opponentpoints;

    private final float mFadedTextAlpha;
    private final int mFadedCircleColor;
    private final int mChosenCircleColor;

    public WearableChallengeItemLayout(Context context) {
        this(context, null);
    }

    public WearableChallengeItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableChallengeItemLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);

        mFadedTextAlpha = 20 / 100f;
        mFadedCircleColor = ContextCompat.getColor(context, R.color.shadows);
        mChosenCircleColor=ContextCompat.getColor(context, R.color.colorAccent);
    }

    // Get references to the icon and text in the item layout definition
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // These are defined in the layout file for list items
        // (see next section)
        //mypic = (ImageView) findViewById(R.id.mypic);
        //opponentpic = (ImageView) findViewById(R.id.opponentPic);
        me = (TextView) findViewById(R.id.me);
        mypoints = (TextView) findViewById(R.id.mypoints);
        opponent = (TextView) findViewById(R.id.opponent);
        opponentpoints = (TextView) findViewById(R.id.opponentPoints);
    }

    @Override
    public void onCenterPosition(boolean animate) {
        me.setAlpha(1f);
        mypoints.setAlpha(1f);
        opponent.setAlpha(1f);
        opponentpoints.setAlpha(1f);
        //((GradientDrawable) mCircle.getDrawable()).setColor(mChosenCircleColor);
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        //((GradientDrawable) mCircle.getDrawable()).setColor(mFadedCircleColor);
        me.setAlpha(1f);
        mypoints.setAlpha(1f);
        opponent.setAlpha(1f);
        opponentpoints.setAlpha(1f);
    }
}

