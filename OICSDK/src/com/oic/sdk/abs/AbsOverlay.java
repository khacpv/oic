package com.oic.sdk.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.OverScroller;

import com.oic.sdk.config.OicConfig;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.view.util.MapConfig;

public abstract class AbsOverlay implements OnTouchListener,
		OnScaleGestureListener, OnDoubleTapListener, OnGestureListener {
	public static String TAG = "AbsOverlay";
	
	Context context;
	public boolean _visible = true;
	
	protected AbsMapView map;
	
	protected GestureDetectorCompat gesture;
	protected ScaleGestureDetector gestureScale;
	protected OverScroller mScroller;
	
	public AbsOverlay(Context context,AbsMapView map) {
		this.map = map;
		this.context = context;
		gesture = new GestureDetectorCompat(getContext(),this);
		gesture.setOnDoubleTapListener(this);
		gestureScale = new ScaleGestureDetector(getContext(), this);
		mScroller = new OverScroller(getContext());
	}
	
	public abstract void init();
	public abstract void setMapConfig(MapConfig mapConfig);
	public abstract void setDataOverlay(MapConfig mapConfig, OverlayData data);
	
	public void setVisible(boolean isVisibile){
		this._visible = isVisibile;
	}
	
	public boolean isVisible(){
		return _visible;
	}
	
	public Context getContext(){
		return context;
	}
	
	public abstract void onDraw(Canvas canvas, AbsMapView mapview);
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean scaleVal = gestureScale.onTouchEvent(event);
		boolean gestureVal = gesture.onTouchEvent(event);
		
		return scaleVal || gestureVal;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onDown");
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onShowPress");
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onSingleTapUp");
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onScroll");
		}
		
		return scroll(distanceX, distanceY);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onLongPress");
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onFling");
		}
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onSingleTapConfirmed");
		}
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onDoubleTap");
		}
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onDoubleTapEvent");
		}
		return false;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onScale");
		}
		float scale = detector.getScaleFactor();
		float focusX = detector.getFocusX();
		float focusY = detector.getFocusY();
		
		return scale(scale, focusX, focusY);
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onScaleBegin");
		}
		return false;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		if(OicConfig.DEBUG_ONEVENT){
			Log.e(TAG, "onScaleEnd");
		}
	}
	
	public AbsMapView getMapView(){
		return this.map;
	}
	
	public boolean scroll(float distanceX, float distanceY){
		Matrix matrix = new Matrix();
		matrix.setTranslate(-distanceX, -distanceY);
		return transform(matrix);
	}
	
	public boolean scale(float scale,float focusX, float focusY){
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale, focusX, focusY);
		return transform(matrix);
	}
	
	public boolean transform(Matrix matrix){
		return false;
	}
}
