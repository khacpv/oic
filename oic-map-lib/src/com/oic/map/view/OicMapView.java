package com.oic.map.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import com.oic.map.AbsMapView;

/**
 * Created by khacpham on 5/9/15.
 */
public class OicMapView extends AbsMapView {
    public static final String TAG = "OicMapView";

    public OicMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        Log.e(TAG, "init");
    }

    @Override
    protected void getAttributes(TypedArray a) {

    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    @Override
    public float getScale() {
        return 0;
    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    @Override
    public RectF getBound() {
        return null;
    }

    @Override
    public PointF getPixelFromLatLong(float lat, float lon) {
        return null;
    }

    @Override
    public PointF getLatLongFromPixel(float Axc, float Ayc) {
        return null;
    }

    @Override
    public PointF constraintToMapBound(String lat, String lon) {
        return null;
    }

    @Override
    public PointF constraintToCanvasBound(float x, float y) {
        return null;
    }
}
