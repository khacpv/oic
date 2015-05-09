package com.oic.sdk.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.oic.sdk.R;
import com.oic.sdk.view.util.MapConfig;

public abstract class AbsMapView extends SurfaceView implements
		SurfaceHolder.Callback {
	protected int _width;
	protected int _height;
	
	protected MapConfig config;
	
	public MapConfig getConfig() {
		return config;
	}

	public void setConfig(MapConfig config) {
		this.config = config;
	}
	
	public void transform(Matrix matrix){
		config.transform(matrix);
	}
	
	public abstract AbsMapController getMapController();

	/**
	 * Surface Internal
	 * */
	private OicThread thread;

	public AbsMapView(Context context) {
		super(context);
		_init(null);
	}

	public AbsMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_init(attrs);
	}

	private void _init(AttributeSet attrs) {
		getHolder().addCallback(this);

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.OicSdk);
		getAttributes(a);
		a.recycle();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		_width = w;
		_height = h;
	}
	
	public int getW(){
		return _width;
	}
	
	public int getH(){
		return _height;
	}

	protected abstract void getAttributes(TypedArray a);

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			thread = new OicThread(this);
			thread.setRunning(true);
			thread.start();
		} catch (IllegalThreadStateException e) {
			thread = null;
			thread = new OicThread(this);
			thread.setRunning(true);
			thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				retry = false;
				thread.join();
			} catch (InterruptedException e) {

			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

	}

	@Override
	protected abstract void onDraw(Canvas canvas);
	
	public abstract float getScale();
	
	public abstract boolean contains(int x,int y);
	
	public abstract RectF getBound();
	
	public abstract PointF getPixelFromLatLong(float lat,float lon);
	public abstract PointF getLatLongFromPixel(float Axc,float Ayc);
	
	public abstract PointF constraintToMapBound(String lat,String lon);
	public abstract PointF constraintToCanvasBound(float x,float y);

	private class OicThread extends Thread {
		private SurfaceHolder holder;
		private AbsMapView view;
		private boolean canRun = false;

		public OicThread(AbsMapView view) {
			this.view = view;
			this.holder = view.getHolder();
		}

		public void setRunning(boolean b) {
			canRun = b;
		}

		@SuppressLint("WrongCall")
		@Override
		public void run() {
			while (canRun) {
				Canvas canvas = null;
				try {
					canvas = holder.lockCanvas();
					if (canvas != null) {
						synchronized (holder) {
							view.onDraw(canvas);
						}
					}
				} finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}

				}
			}
		}
	}
}
