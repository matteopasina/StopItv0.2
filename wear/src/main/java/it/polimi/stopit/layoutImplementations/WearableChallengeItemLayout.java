package it.polimi.stopit.layoutImplementations;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import it.polimi.stopit.R;

/**
 * Created by matteo on 24/01/16.
 */
public class WearableChallengeItemLayout extends RelativeLayout
        implements WearableListView.OnCenterProximityListener {

    private CircularImageView opponentpic;
    private TextView opponent,points;

    private final float mFadedTextAlpha;

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
    }

    // Get references to the icon and text in the item layout definition
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // These are defined in the layout file for list items
        // (see next section)
        opponentpic = (CircularImageView) findViewById(R.id.opponentPic);
        points = (TextView) findViewById(R.id.points);
        opponent = (TextView) findViewById(R.id.opponent);
    }

    @Override
    public void onCenterPosition(boolean animate) {
        points.setAlpha(1f);
        opponent.setAlpha(1f);
        opponentpic.setVisibility(CircularImageView.VISIBLE);
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        opponentpic.setVisibility(CircularImageView.INVISIBLE);
        points.setAlpha(mFadedTextAlpha);
        opponent.setAlpha(mFadedTextAlpha);
    }
}

