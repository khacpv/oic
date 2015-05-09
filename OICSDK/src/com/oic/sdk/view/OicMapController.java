package com.oic.sdk.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Scroller;

import com.oic.sdk.abs.AbsMapController;
import com.oic.sdk.config.OicConfig;
import com.oic.sdk.io.OicLoader;
import com.oic.sdk.io.OicLoader.OicLoaderListener;
import com.oic.sdk.io.OicLoader.OicMapDataLoad;
import com.oic.sdk.io.prefs.OicPreferences;
import com.oic.sdk.view.animation.ScaleAnimation;
import com.oic.sdk.view.animation.TranslateXAnimation;
import com.oic.sdk.view.animation.TranslateYAnimation;
import com.oic.sdk.view.gesture.RotationGestureDetector;
import com.oic.sdk.view.gesture.RotationGestureDetector.OnRotationGestureListener;
import com.oic.sdk.view.overlay.OverlayManager;

public class OicMapController extends AbsMapController implements
		OnTouchListener, OnScaleGestureListener, OnDoubleTapListener,
		OnGestureListener, OnRotationGestureListener, SensorEventListener {
	public static final String TAG = "OicMapController";

	public static final int COMPASS_NO = 1;
	public static final int COMPASS_YES = 0;
	public static final int COMPASS_DEF = -1;

	int stateCompass = COMPASS_DEF;

	OicMapView mapView;
	OverlayManager overlayMng;

	float angle = 0;
	float scale = 1F;
	float focusX = 0;
	float focusY = 0;
	float tnxX = 0;
	float tnxY = 0;

	protected RotationGestureDetector rotateGesture;
	protected GestureDetectorCompat gesture;
	protected ScaleGestureDetector gestureScale;
	protected Scroller mScroller;

	private OnActionMapListener listener;
	
	OicLoader thread;
	OicLoaderListener loaderListener;
	
	public int idFloor = 0;
	public ArrayList<OicMapDataLoad> lstMap = new ArrayList<OicLoader.OicMapDataLoad>();
	
	public OicMapController(OicMapView map) {
		this.mapView = map;
		this.overlayMng = this.mapView.getOverlayManager();

		gesture = new GestureDetectorCompat(getContext(), this);
		gesture.setOnDoubleTapListener(this);
		gestureScale = new ScaleGestureDetector(getContext(), this);
		mScroller = new Scroller(getContext());
		rotateGesture = new RotationGestureDetector(this);
		getContext();
		mSensorManager = (SensorManager) getContext().getSystemService(
				Context.SENSOR_SERVICE);

	}

	public void reset() {
		scale = 1F;
		focusX = 0;
		focusY = 0;
		tnxX = 0;
		tnxY = 0;
	}

	public void toggleCompass() {
		this.stateCompass++;
		if (stateCompass > COMPASS_NO) {
			stateCompass = COMPASS_DEF;
		}
		if (stateCompass == COMPASS_DEF) {
			deRotate();
		}
	}

	public int getStateCompass() {
		return stateCompass;
	}

	public Context getContext() {
		return mapView.getContext();
	}
	
	public void loadMap(int id, OicMapDataLoad data, Handler mHandler,final OicLoaderListener loadListener) {
		
		thread = new OicLoader(this.mapView.getContext(), mHandler, mapView, new OicLoaderListener() {
			
			@Override
			public void onLoadWorker(OicMapDataLoad data) {
				reset();
				overlayMng.init();
				if(loadListener!=null){
					loadListener.onLoadWorker(data);
				}
			}
			
			@Override
			public void onLoadStartUI() {
				mapView.setDisableTouch(true);
				
				if(loadListener!=null){
					loadListener.onLoadStartUI();
				}
			}
			
			@Override
			public void onLoadFailed(Exception e) {
				mapView.setDisableTouch(false);
				if(loadListener!=null){
					loadListener.onLoadFailed(e);
				}
			}
			
			@Override
			public void onLoadComplete(OicMapDataLoad data) {
				mapView.setDisableTouch(false);
				if(loadListener!=null){
					loadListener.onLoadComplete(data);
				}
			}
		}, data);
		thread.start();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean isOverlayTouch = true/* this.overlayMng.onTouch(mapView, event) */;

		boolean scaleVal = gestureScale.onTouchEvent(event);
		boolean gestureVal = gesture.onTouchEvent(event);
		if (rotateGesture.onTouchEvent(event)) {
			return true;
		}

		// Log.e(TAG, "scale: "+scale+" tnxX: "+tnxX+" tnxY:"+tnxY);
		return isOverlayTouch || scaleVal || gestureVal;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onDown");
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onShowPress");
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onSingleTapUp");
		}
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onScroll");
		}
		if (mapView.getCanvas() == null) {
			return false;
		}

		// RectF newBound = mapView.getConfig().getMapBound().getCanvasRect();
		// newBound.offset(-distanceX, -distanceY);
		//
		// RectF mapBound = mapView.getBound();

		tnxX += distanceX;
		tnxY += distanceY;

		Matrix matrix = new Matrix();
		matrix.setTranslate(-distanceX, -distanceY);

		return transform(matrix);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onLongPress");
		}
		
//		 float xc = e.getX();
//		 float yc = e.getY();
//		
//		 PointF myloc = mapView.getLatLongFromPixel(xc, yc);
//		 
//		 LayerLocation.myLocation = mapView.getPixelFromLatLong(myloc.x, myloc.y);
//		 Log.e(TAG, "onLongPress: "+myloc.x+"-"+myloc.y);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onFling");
		}
		final float velocityFactor = 2.5f;
		int minX = (int) (mapView.getW() - mapView.getBoundMap().width());
		int minY = (int) (mapView.getH() - mapView.getBoundMap().height());
		mScroller.fling((int) tnxX, (int) tnxY,
				(int) (velocityX / velocityFactor),
				(int) (velocityY / velocityFactor), minX, 0, minY, 0);
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onSingleTapConfirmed");
		}
		return overlayMng.onSingleTapConfirmed(e);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onDoubleTap");
		}

		float focusX = e.getX();
		float focusY = e.getY();

		zoom(getScale() * 1.7F, focusX, focusY, true);

		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onDoubleTapEvent");
		}
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onScale");
		}

		float scale = detector.getScaleFactor();
		float focusX = detector.getFocusX();
		float focusY = detector.getFocusY();

		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale, focusX, focusY);

		return transform(matrix);
	}

	private SensorManager mSensorManager;

	@SuppressWarnings("deprecation")
	public void onResume() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
		
		if(!OicPreferences.isRotateMap(getContext())){
			stateCompass = COMPASS_NO;
			deRotate();
		}

	}

	public void onPause() {
		mSensorManager.unregisterListener(this);
	}

	public boolean transform(Matrix matrix) {
		float[] transform = new float[9];
		matrix.getValues(transform);

		if (this.scale * transform[Matrix.MSCALE_X] >= OicMapView.MAX_SCALE
				&& transform[Matrix.MSCALE_X] > 1) {
			return false;
		}
		if (this.scale * transform[Matrix.MSCALE_X] <= OicMapView.MIN_SCALE
				&& transform[Matrix.MSCALE_X] < 1) {
			return false;
		}

		this.scale *= transform[Matrix.MSCALE_X];
		this.tnxX = transform[Matrix.MTRANS_X];
		this.tnxY = transform[Matrix.MTRANS_Y];

		if (listener != null) {
			listener.onTransform(matrix);
		}
		return overlayMng.transform(matrix);
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onScaleBegin");
		}
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		if (OicConfig.DEBUG_ONEVENT) {
			Log.e(TAG, "onScaleEnd");
		}
	}

	public Scroller getScroller() {
		return mScroller;
	}

	public float getScale() {
		return scale;
	}

	public PointF getTrans() {
		return new PointF(tnxX, tnxY);
	}

	public PointF getCenter() {
		return mapView.getCenter();
	}
	
	public float getAngle(){
		return this.angle;
	}

	public void move(float x, float y, boolean animate) {
		if (!animate) {
			setCenter(x, y);
			return;
		}
		TranslateXAnimation animX = new TranslateXAnimation();
		TranslateYAnimation animY = new TranslateYAnimation();

		animX.mDuration = animY.mDuration = 1000;

		animX.mStartValue = getCenter().x;
		animY.mStartValue = getCenter().y;

		animX.mEndValue = x;
		animY.mEndValue = y;

		float currX = getCenter().x;
		float currY = getCenter().y;

		while (!animX.isEnded() || !animY.isEnded()) {
			currX = animX.getCurrentValue(System.currentTimeMillis()
					- animX.mStartTime);
			currY = animY.getCurrentValue(System.currentTimeMillis()
					- animY.mStartTime);
			if (animX.isEnded()) {
				currX = x;
			}
			if (animY.isEnded()) {
				currY = y;
			}
			setCenter(currX, currY);
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
			}
		}
	}

	public void setCenter(float x, float y) {
		PointF currentCenter = getCenter();
		float tnxX = -x + currentCenter.x;
		float tnxY = -y + currentCenter.y;
		Matrix mtx = new Matrix();
		mtx.setTranslate(tnxX, tnxY);
		// transform(mtx);

		float[] transform = new float[9];
		mtx.getValues(transform);

		this.scale *= transform[Matrix.MSCALE_X];
		this.tnxX = transform[Matrix.MTRANS_X];
		this.tnxY = transform[Matrix.MTRANS_Y];

		if (listener != null) {
			listener.onTransform(mtx);
		}
		overlayMng.transform(mtx);
	}

	public void zoom(final float scale, final float x, final float y,
			boolean animate) {
		if (!animate) {
			zoom(scale, x, y);
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				ScaleAnimation anim = new ScaleAnimation();
				anim.mDuration = 300;
				anim.mStartTime = System.currentTimeMillis();
				anim.mEndTime = anim.mStartTime + anim.mDuration;
				anim.mStartValue = getScale();
				anim.mEndValue = scale;

				while (!anim.isEnded()) {
					zoom(anim.getCurrentValue(System.currentTimeMillis()
							- anim.mStartTime), x, y);
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	public void zoom(float scale, float x, float y) {
		float increaseScale = scale / this.scale;
		Matrix matrix = new Matrix();
		matrix.setScale(increaseScale, increaseScale, x, y);
		transform(matrix);
	}

	public void setOnActionMapListener(OnActionMapListener listener) {
		this.listener = listener;
	}

	public static interface OnActionMapListener {
		public void onTransform(Matrix matrix);
	}

	@Override
	public boolean OnRotation(RotationGestureDetector rotationDetector) {
		if(!OicPreferences.isRotateMap(getContext())){
			return false;
		}
		stateCompass = COMPASS_NO;
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
		}
		float angle = rotationDetector.getAngle();
		float cX = rotationDetector.getCenterX();
		float cY = rotationDetector.getCenterY();
		rotate(angle, cX, cY);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(final SensorEvent event) {
		if (stateCompass == COMPASS_YES) {
			if (this.angle == 0) {
				rotate(0.01F);
			} else {
				rotate(Math.round(event.values[0]));
			}

		}
	}

	public void deRotate() {
		stateCompass = COMPASS_DEF;
		rotate(0);
	}

	public void rotate(final float degree) {
		rotate(degree, getCenter().x, getCenter().y);
		Log.e(TAG, "rotate: " + degree);
	}

	public void rotate(final float degree, final float x, final float y) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Matrix matrix = new Matrix();
				matrix.setRotate(-degree - angle, x, y);
				overlayMng.transform(matrix);
				angle = -degree;
			}
		}).start();
		if(rotateListener!=null){
			rotateListener.onRotate(degree, x, y);
		}
	}

	@Override
	public void OnRotationBegin(RotationGestureDetector rotationDetector) {
		this.angle = 0;
	}

	@Override
	public void OnRotationEnd(RotationGestureDetector rotationDetector) {
		
	}
	
	OnRotateListener rotateListener;
	public void setOnRotationListener(OnRotateListener listener){
		this.rotateListener = listener;
	}
	
	public static interface OnRotateListener{
		public void onRotate(float angle,float x,float y);
	}
}
