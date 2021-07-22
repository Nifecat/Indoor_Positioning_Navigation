package edu.xmu.inroomlocation.map;

import android.content.Context;
import android.util.AttributeSet;

import com.onlylemi.mapview.library.MapView;

public class LowRefreshRateMapView extends MapView {

    public LowRefreshRateMapView(Context context) {
        super(context);
    }

    public LowRefreshRateMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LowRefreshRateMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private long mLastRefreshTime = 0L;
    @Override
    public void refresh() {
        long t = System.currentTimeMillis();
        if (t - mLastRefreshTime > 32) {
            super.refresh();
            mLastRefreshTime = t;
        }
    }
}
