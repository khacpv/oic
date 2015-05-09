package com.oic.sdk.view.layer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.oic.sdk.abs.AbsMapView;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.view.overlay.OverlayBase;
import com.oic.sdk.view.render.PathContainer;
import com.oic.sdk.view.util.MapBounds;
import com.oic.sdk.view.util.MapConfig;

public class LayerDepartment extends OverlayBase{
	
	PathContainer paths;
//	PointContainer points;

	Paint paint_node = new Paint();
	Paint paint_bounds = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint paint_ways = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint paint_bound = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint borderBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private boolean isDrawBorder = true;
	
	public LayerDepartment(Context context, AbsMapView mapView) {
		super(context, mapView);
		paint_node.setStrokeWidth(6);
		paint_node.setAntiAlias(true);
		paint_node.setStyle(Style.FILL_AND_STROKE);
		paint_node.setColor(Color.RED);

		paint_ways.setStrokeWidth(2);
		paint_ways.setStyle(Style.FILL_AND_STROKE);
		paint_ways.setAntiAlias(false);
		paint_ways.setColor(Color.BLUE);
		
		paint_bound.setStrokeWidth(3);
		paint_bound.setStyle(Style.FILL);
		paint_bound.setAntiAlias(false);
		paint_bound.setColor(Color.WHITE);
		
		borderPaint.setColor(0x88888888);
		borderPaint.setStrokeWidth(3);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeJoin(Join.ROUND);
		
		borderBgPaint.setColor(0xFF999999);
		borderBgPaint.setStrokeWidth(7);
		borderBgPaint.setStyle(Style.STROKE);
		borderBgPaint.setStrokeJoin(Join.ROUND);
		
		init();
	}
	
	public void init(){
		super.init();
		paths = new PathContainer(this);
//		points = new PointContainer(map.getContext(),this);
	}

	@Override
	public void onDraw(Canvas canvas, AbsMapView mapview) {
		super.onDraw(canvas, mapview);
		
		// draw background
//		MapBounds mapBound = mapConfig.getMapBound();
//		canvas.drawRect(new RectF(mapBound.min.xc, mapBound.max.yc, mapBound.max.xc, mapBound.min.yc), paint_bound);
		
		// draw ways
		synchronized(paths){
			Path[] _paths = paths.getPathArray();
			Paint[] _paints = paths.getPaintArray();
			RectF boundPath = new RectF();
			for(int i=0;i<_paths.length;i++){
				_paths[i].computeBounds(boundPath, true);
				if(!mapview.getBound().intersects(boundPath.left, boundPath.top, boundPath.right, boundPath.bottom)){
					continue;
				}
				canvas.drawPath(_paths[i], _paints[i]);
				
				if(isDrawBorder){
					// draw border
					if(i==0){
						canvas.drawPath(_paths[i], borderBgPaint);
					}else{
						canvas.drawPath(_paths[i], borderPaint);
					}
				}
			}
		}
		
//		// draw nodes
//		synchronized (points) {
//			for(PointRender pointRender: points.getPoints()){
//				canvas.drawCircle(pointRender.getNodePoint().xc, pointRender.getNodePoint().yc, pointRender.getRadius(), pointRender.getPaint());
//			}
//		}
	}

	@Override
	public boolean transform(Matrix matrix) {
		getMapView().transform(matrix);
		synchronized(paths){
			paths.transform(matrix);
		}
		
//		synchronized (points) {
//			points.transform(matrix);
//		}
		return true;
	}
	
	@Override
	public void setDataOverlay(MapConfig mapConfig, OverlayData data) {
		super.setDataOverlay(mapConfig, data);
		synchronized (paths) {
			paths.init();
//			points.init();
			
//			Log.e(TAG, data.nodes.size()+" nodes, "+data.ways.size()+" ways");
			
			paths.render(data);
//			points.render(data);
			
			MapBounds mapBound = mapConfig.getMapBound();
			
			PointF _p = map.constraintToCanvasBound(mapBound.min.x, mapBound.min.y);
			mapBound.min.xc = _p.x;
			mapBound.min.yc = _p.y;
			
			_p = map.constraintToCanvasBound(mapBound.max.x, mapBound.max.y);
			mapBound.max.xc = _p.x;
			mapBound.max.yc = _p.y;
		}
	}
	
	public boolean isDrawBorder() {
		return isDrawBorder;
	}
	
	public void setDrawBorder(boolean isDrawBorder) {
		this.isDrawBorder = isDrawBorder;
	}
	
	public void toggleBorder(){
		this.isDrawBorder = !this.isDrawBorder;
	}
}
