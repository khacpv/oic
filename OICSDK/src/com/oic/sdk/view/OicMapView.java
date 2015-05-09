package com.oic.sdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.oic.sdk.R;
import com.oic.sdk.abs.AbsMapView;
import com.oic.sdk.config.OicConfig;
import com.oic.sdk.config.OicConstant;
import com.oic.sdk.config.OicUtil;
import com.oic.sdk.io.piclayer.Piclayer;
import com.oic.sdk.io.prefs.OicPreferences;
import com.oic.sdk.view.overlay.OverlayManager;
import com.oic.sdk.view.util.CanvasBounds;
import com.oic.sdk.view.util.MapBounds;
import com.oic.sdk.view.util.MapConfig;
import com.oic.sdk.view.util.OicResource;

public class OicMapView extends AbsMapView{
	public static final String TAG = "OicMapView";
	
	public static boolean MODE_DEV = false;
	
	public static final int MAP_VERSION = 2;
	
//	private static final String COPYRIGHT = "\u00a9";
	private static final String TRADEMARK = "\u2122";
	
	public static int STATE_ACTIVE = 0;
	public static int STATE_PAUSE = 1;
	
	public static final float MIN_SCALE = 0.5f;
	public static final float MIN_SCALE_TEXT = 0.75f;
	public static final float MIN_SCALE_BORDER = 0.55f;
	public static final float MIN_SCALE_STORE = 1.2f;
	public static final float MAX_SCALE = 24f;
	
	long startTime;
	
	private OverlayManager overlayMng;
	private OicMapController mapController;
	
	private float ratio;
	
	private Canvas mCanvas;
	private RectF mMapRect;
	
	float lastCurrX = 0;
	float lastCurrY = 0;
	
	private boolean loading = false;
	private int state=STATE_ACTIVE;
	
	private Paint tradeMark;
	private int textSize = 16;
	private float margin = 10;
	private String tradeMarkText = "OIC"+TRADEMARK;
	
	private OnMapViewListener listener;
	
	//background
	BitmapDrawable background;
	Paint fillPaint;
	
	private boolean disableTouch = false;
	
	private RectF rectBound = new RectF();
	
	public OicMapView(Context context) {
		super(context);
		init();
	}
	
	public OicMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressWarnings("deprecation")
	public void init(){
		MODE_DEV = OicPreferences.isDebugOicMap(getContext());
		
		overlayMng = new OverlayManager(this);
		mapController = new OicMapController(this);
		this.mMapRect = new RectF(0, 0, 0, 0);
		this.ratio = 1.0F;
		
		tradeMark = new Paint(Paint.ANTI_ALIAS_FLAG);
		tradeMark.setTextSize(OicResource.dipToPixels(getContext(), textSize));
		tradeMark.setStrokeWidth(1);
		tradeMark.setShadowLayer(10, 0, 0, Color.WHITE);
		tradeMark.setStyle(Style.FILL);
		tradeMark.setColor(0x8A676568);
		tradeMark.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
		margin = OicResource.dipToPixels(getContext(), margin);
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bacground);
		background = new BitmapDrawable(bmp);
		BitmapShader fillBMPshader = new BitmapShader(bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);  
		fillPaint = new Paint();
	    fillPaint.setStyle(Paint.Style.FILL);  
	    //Assign the 'fillBMPshader' to this paint  
	    fillPaint.setShader(fillBMPshader); 
	    if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
	    	setLayerType(View.LAYER_TYPE_NONE,new Paint(Paint.ANTI_ALIAS_FLAG));
	    }else{
	    	setLayerType(View.LAYER_TYPE_NONE,new Paint(Paint.ANTI_ALIAS_FLAG));
	    }
	}
	
	public Canvas getCanvas() {
		return mCanvas;
	}
	
	public float getRatio(){
		return ratio;
	}
	
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	
	public boolean contains(int x,int y){
		return rectBound.contains(x, y);
	}
	
	public RectF getBound(){
		return rectBound;
	}
	
	@SuppressLint("WrongCall")
	@Override
	protected void onDraw(Canvas canvas) {
		this.mCanvas = canvas;
		
//		if(state == STATE_PAUSE || isLoading()){
//			return;
//		}
		
//		startTime = System.currentTimeMillis();
		
		// draw background
//		canvas.drawColor(Color.DKGRAY);
		canvas.drawPaint(fillPaint);
		
		rectBound.left = rectBound.top = 0;
		rectBound.right = getW();
		rectBound.bottom=getH();
		
		// draw other overlays
		overlayMng.onDraw(canvas);
				
//		if (getMapController().getScroller().computeScrollOffset()) {
//			float currentX = getMapController().getScroller().getCurrX();
//			float currentY = getMapController().getScroller().getCurrY();
//			getMapController().scroll(-currentX+lastCurrX, -currentY+lastCurrY);
//			lastCurrX = currentX;
//			lastCurrY = currentY;
//			postInvalidate();
//		}
		
		if(OicConfig.DEBUG_ONDRAW){
			long endTime = System.currentTimeMillis();
			Log.d(TAG, String.format(OicConstant.TOTAL_RUNTIME,"onDraw",(endTime-startTime)+""));
		}
		
		// draw trademark
		canvas.drawText(tradeMarkText, margin, getHeight()-margin, tradeMark);
		
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(state == STATE_PAUSE || isLoading() || disableTouch){
			return false;
		}
		
		boolean overlayEventResult = mapController.onTouch(this, event);
		return overlayEventResult || super.onTouchEvent(event);
	}
	
	@Override
	protected void getAttributes(TypedArray a) {
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public OverlayManager getOverlayManager(){
		return overlayMng;
	}
	
	public OicMapController getMapController(){
		return mapController;
	}
	
	public RectF getBoundMap(){
		return mMapRect;
	}
	
	public void setBoundMap(RectF rectF){
		this.mMapRect = rectF;
	}
	
	public float getScale(){
		return getMapController().scale;
	}
	
	public PointF constraintToMapBound(String lat,String lon){
		PointF _pointF = OicUtil.convertToXY(lat, lon);
		_pointF.x = -_pointF.x;
		return _pointF;
	}
	
	public PointF constraintToCanvasBound(float x,float y){
		CanvasBounds canvasBound = getConfig().getCanvasBound();
		MapBounds mapBounds = getConfig().getMapBound();
		
		if (canvasBound.CURRENT_WRAPPER == CanvasBounds.WRAP_HEIGHT) {
			double COEFF = canvasBound.getCoeff();
			float yc = canvasBound.height - (float) (COEFF * (y - mapBounds.min.y));
			float xc = (float) ((canvasBound.width / mapBounds.dLongitude) * (x - mapBounds.min.x));
			return new PointF(xc, yc);
		}
		return new PointF(0, 0);
	}
	
	public PointF getPixelFromLatLong(float lat,float lon){
		PointF _pointF = OicUtil.convertToXY(lat+"", lon+"");
		
		double Ax = -_pointF.x;
		
		double maxX = getConfig().getMapBound().max.x;
		double minX = getConfig().getMapBound().min.x;
		double maxCX = getConfig().getMapBound().max.xc;
		double minCX = getConfig().getMapBound().min.xc;
		
		double Axc = maxCX - (maxCX-minCX)*(maxX-Ax)/(maxX-minX);
		
		double Ay = _pointF.y;
		
		double maxY = getConfig().getMapBound().max.y;
		double minY = getConfig().getMapBound().min.y;
		double maxCY = getConfig().getMapBound().max.yc;
		double minCY = getConfig().getMapBound().min.yc;
		
		double Ayc = maxCY - (maxCY-minCY)*(maxY-Ay)/(maxY-minY);
		
		PointF _p = new PointF((float)Axc, (float)Ayc);
		
//		MapConfig mapConfig = getConfig();
//		
//		PointF _pointF = OicUtil.convertToXY(lat+"", lon+"");
//		NodePoint _point = new NodePoint(-_pointF.x, _pointF.y);
//		
//		double COEFF = mapConfig.getCanvasBound().getCoeff();
//		float yc = mapConfig.getCanvasBound().height - (float) (COEFF * (_point.y - mapConfig.getMapBound().min.y));
//		float xc = (float) ((mapConfig.getCanvasBound().width / mapConfig.getMapBound().dLongitude) * (_point.x - mapConfig.getMapBound().min.x));
//		PointF _p = new PointF(xc, yc);
		Log.e(TAG, "getPixelFromLatLong:"+lat+"-"+lon+"=>"+_p.x+"-"+_p.y);
		return _p;
	}
	
	public PointF getLatLongFromPixel(float Axc,float Ayc){
		double maxX = getConfig().getMapBound().max.x;
		double minX = getConfig().getMapBound().min.x;
		double maxCX = getConfig().getMapBound().max.xc;
		double minCX = getConfig().getMapBound().min.xc;
		
		double Ax = maxX - (maxX-minX)*(maxCX-Axc)/(maxCX-minCX);
		
		double maxY = getConfig().getMapBound().max.y;
		double minY = getConfig().getMapBound().min.y;
		double maxCY = getConfig().getMapBound().max.yc;
		double minCY = getConfig().getMapBound().min.yc;
		
		double Ay = maxY - (maxY-minY)*(maxCY-Ayc)/(maxCY-minCY);
		
		PointF _p = OicUtil.convertToLatLon((float)-Ax, (float)Ay);
		return _p;
	}
	
	public MapConfig setPiclayer(Piclayer config){
		loading = true;
		
		float left = 0;
		float top = 0;
		float width = config.width;
		float height = config.height;
		
		RectF mBackgroundRect = new RectF(left, top, left+width, top+height);
		this.setBoundMap(mBackgroundRect);
		
		
		MapConfig result = constrain(config);
		if(listener!=null){
			listener.onLoadComplete();
		}
		loading = false;
		return result;
	}
	
	private MapConfig constrain(Piclayer config) {
		Float maxLat = -Float.parseFloat(config.minLat+"");
		Float maxLon = -Float.parseFloat(config.minLon+"");
		Float minLat = -Float.parseFloat(config.maxLat+"");
		Float minLon = -Float.parseFloat(config.maxLon+"");
		
		int canvasWidth = (int)((OicMapView)this).getBoundMap().width();
		int canvasHeight = (int)((OicMapView)this).getBoundMap().height();
		
		MapBounds mapBound = new MapBounds();
		CanvasBounds canvasBound = new CanvasBounds();
		
		mapBound.constrain(minLat, minLon, maxLat, maxLon);
		canvasBound.constraint(canvasWidth, canvasHeight, mapBound);
		super.config = new MapConfig(config, canvasBound, mapBound);
		return super.config;
	}
	
	public void onPause(){
		state = STATE_PAUSE;
		this.mapController.onPause();
	}
	
	public void onResume(){
		state = STATE_ACTIVE;
		this.mapController.onResume();
	}
	
	public PointF getCenter(){
		float centerX = getLeft() + getWidth()/2;
		float centerY = getTop() + getHeight()/2;
		return new PointF(centerX, centerY);
	}
	
	public boolean isLoading() {
		return loading;
	}
	
	public void setDisableTouch(boolean disable){
		this.disableTouch = disable;
	}
	
	public void setOnMapViewListener(OnMapViewListener listener){
		this.listener = listener;
	}
	
	public static interface OnMapViewListener{
		public void onLoadComplete();
	}
}
