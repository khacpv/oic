package com.oic.sdk.view.render;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.PointF;

import com.oic.sdk.data.Node;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.data.Way;
import com.oic.sdk.view.overlay.OverlayBase;
import com.oic.sdk.view.util.NodePoint;
import com.oic.sdk.view.util.OicColor;

public class PathContainer {
	OverlayBase layer;
	
	Path[] pathArray;
	Paint[] paintArray;
	ArrayList<PathRender> paths = new ArrayList<PathRender>();
		
	int borderColorDef = 0x88888888;
	int borderWidthDef = 1;
	int borderWidthBackground = 15;

	// temporary
	Node _node;
	PointF _pointF;
	NodePoint _nodePoint;
	int _nodeCount = 0;
	int _nodeSize = 0;
	Path _path;
	Paint _paint;
	PathRender _pathRender;
	
	public PathContainer(OverlayBase layer) {
		this.layer = layer;
	}
	
	public void init(){
		paths = new ArrayList<PathRender>();
	}
	
	public void render(OverlayData overlayData){
		render(overlayData, true);
	}
	
	public void render(OverlayData overlayData, boolean closePath){
		Collections.sort(overlayData.ways);
		for(Way way: overlayData.ways){
			_path = new Path();
			_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			
			int color = OicColor.getBackgroundColor(way.getBackgroundType());
			_paint.setColor(color);
			_paint.setStrokeCap(Cap.ROUND);
			_paint.setStrokeJoin(Join.ROUND);
			_paint.setStrokeWidth(borderWidthDef);
//			_paint.setShadowLayer(borderWidthDef, 0, 0, borderColorDef);
			
			_nodeCount = 0;
			_nodeSize = way.nd.size();
			
			for(String _nodeId: way.nd){
				_node = overlayData.getNode(_nodeId);
				
				_pointF  = layer.getMapView().constraintToMapBound(_node.lat, _node.lon);
				_nodePoint = new NodePoint(_pointF.x, _pointF.y);
				
				_pointF = layer.getMapView().constraintToCanvasBound(_pointF.x, _pointF.y);
				_nodePoint.xc = _pointF.x;
				_nodePoint.yc = _pointF.y;
				
				if (_nodeCount == 0) {
					/* first node */
					_path.moveTo(_nodePoint.xc, _nodePoint.yc);
				} else {
					_path.lineTo(_nodePoint.xc, _nodePoint.yc);
				}
				if(_nodeCount == _nodeSize && closePath){
					// final node
					_path.close();
				}
				_nodeCount++;
			}
			
			_pathRender = new PathRender(_path, _paint);
			_pathRender.setWay(way);
			// way background
			if(OicColor.LOCTYPE_BACKGROUND.equals(way.getBackgroundType())){
				_pathRender.getPaint().setStrokeWidth(borderWidthBackground);
				paths.add(0,_pathRender);
				continue;
			}
			paths.add(_pathRender);
		}
		pathArray = getPathArrayInternal();
		paintArray = getPaintArrayInternal();
	}
	
	public synchronized ArrayList<PathRender> getPaths() {
		return paths;
	}
	
	private synchronized Path[] getPathArrayInternal(){
		Path[] _paths = new Path[paths.size()];
		for(int i=0;i<paths.size();i++){
			_paths[i]=paths.get(i).getPath();
		}
		return _paths;
	}
	
	public synchronized Path[] getPathArray(){
		return pathArray;
	}
	
	private synchronized Paint[] getPaintArrayInternal(){
		Paint[] _paint = new Paint[paths.size()];
		for(int i=0;i<paths.size();i++){
			_paint[i]=paths.get(i).getPaint();
		}
		return _paint;
	}
	
	public synchronized Paint[] getPaintArray(){
		return paintArray;
	}
	
	public void transform(Matrix matrix){
		synchronized(paths){
			for(Path _pathRender: pathArray){
				_pathRender.transform(matrix);
			}
		}
	}
	
	public OverlayBase getLayer() {
		return layer;
	}
	
	public void offset(float deltaX, float deltaY){
		synchronized(paths){
			for(PathRender _pathRender: paths){
				_pathRender.offset(deltaX, deltaY);
			}
		}
	}
}
