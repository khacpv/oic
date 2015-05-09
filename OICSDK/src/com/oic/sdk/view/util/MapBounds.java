package com.oic.sdk.view.util;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.oic.sdk.config.OicUtil;
import com.oic.sdk.view.OicMapView;

public class MapBounds {
	public NodePoint max, min;
	public float dLongitude, dLatitude;
	
	public void constrainToMapBounds(OicMapView mapView, CanvasBounds canvasBounds, float minLat, float minLon, float maxLat, float maxLon){
		PointF tmpPoint = OicUtil.convertToXY(minLat+"", minLon+"");
		this.min = new NodePoint(tmpPoint.x,tmpPoint.y);
		
		tmpPoint = OicUtil.convertToXY(maxLat+"", maxLon+"");
		this.max = new NodePoint(tmpPoint.x,tmpPoint.y);

		this.dLongitude = this.max.sub(this.min).x;
		this.dLatitude = this.max.sub(this.min).y;

		canvasBounds.width = (int)mapView.getBoundMap().width();
		canvasBounds.height = (int)mapView.getBoundMap().height();
		
		canvasBounds.autoWrap();
		canvasBounds.setCOEFF(canvasBounds.height / this.dLatitude);
	}
	
	public RectF getCanvasRect(){
		RectF rect = new RectF();
		rect.left = min.xc;
		rect.top = min.yc;
		rect.right = max.xc;
		rect.bottom = max.yc;
		return rect;
	}
	
	public void constrain(float minLat, float minLon, float maxLat, float maxLon){
		PointF tmpPoint = OicUtil.convertToXY(minLat+"", minLon+"");
		this.min = new NodePoint(tmpPoint.x,tmpPoint.y);
		
		tmpPoint = OicUtil.convertToXY(maxLat+"", maxLon+"");
		this.max = new NodePoint(tmpPoint.x,tmpPoint.y);

		this.dLongitude = this.max.sub(this.min).x;
		this.dLatitude = this.max.sub(this.min).y;
	}
	
	public void onScale(ScaleGestureDetector detector) {
		
	}
	
	public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY){
		
	}
	
	/**
	 * @return true if transformed, false is anything else.
	 * */
	public boolean transform(Matrix matrix){
		min.transform(matrix);
		max.transform(matrix);
		return true;
	}
}