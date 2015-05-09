package com.oic.sdk.view.render;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.oic.sdk.data.Node;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.view.overlay.OverlayBase;
import com.oic.sdk.view.util.NodePoint;

public class PointContainer {
	Context context;
	OverlayBase layer;
	
	ArrayList<PointRender> points = new ArrayList<PointRender>();
	
	// temporary
	PointRender _pointRender;
	NodePoint _point = new NodePoint(0, 0);
	Paint _paint;
	PointF _pointF;
	
	public PointContainer(Context context,OverlayBase layer) {
		this.context = context;
		this.layer = layer;
	}
	
	public void init(){
		points = new ArrayList<PointRender>();
	}

	public void render(OverlayData overlayData){
		if(overlayData== null || overlayData.nodes == null){
			return;
		}
		
		for(Node _node: overlayData.nodes){
			_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			
			_pointF  = layer.getMapView().constraintToMapBound(_node.lat, _node.lon);
			_point = new NodePoint(_pointF.x, _pointF.y);
			
			_pointF = layer.getMapView().constraintToCanvasBound(_pointF.x, _pointF.y);
			_point.xc = _pointF.x;
			_point.yc = _pointF.y;
			
			_pointRender = new PointRender(context, _point, _paint);
			_pointRender.setNode(_node);
			points.add(_pointRender);
		}
	}
	
	public synchronized ArrayList<PointRender> getPoints() {
		return points;
	}
	
	public void transform(Matrix matrix){
		synchronized(points){
			for(PointRender _pointRender: points){
				_pointRender.transform(matrix);
			}
		}
	}
	
	public void offset(float deltaX, float deltaY){
		synchronized(points){
			for(PointRender _pointRender: points){
				_pointRender.offset(deltaX, deltaY);
			}
		}
	}
}
