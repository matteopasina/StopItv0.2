package it.polimi.stopit.layoutImplementations;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it.polimi.stopit.R;

/**
 * Created by matteo on 15/01/16.
 */
public class WearableListItemLayout extends RelativeLayout
        implements WearableListView.OnCenterProximityListener {

    private ImageView mCircle;
    private TextView mName;
    private TextView mPoints;
    private TextView mPosition;

    private final float mFadedTextAlpha;

    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs,
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
        mCircle = (ImageView) findViewById(R.id.circle);
        mName = (TextView) findViewById(R.id.name);
        mPoints = (TextView) findViewById(R.id.points);
        mPosition = (TextView) findViewById(R.id.position);
    }

    @Override
    public void onCenterPosition(boolean animate) {

        mName.setAlpha(1f);
        mPoints.setAlpha(1f);
        mPosition.setAlpha(1f);
        mCircle.getDrawable().setAlpha(255);

    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        mCircle.getDrawable().setAlpha(40);
        mName.setAlpha(mFadedTextAlpha);
        mPosition.setAlpha(mFadedTextAlpha);
        mPoints.setAlpha(mFadedTextAlpha);
    }
}
