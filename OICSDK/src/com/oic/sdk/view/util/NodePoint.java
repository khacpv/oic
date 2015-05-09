package com.oic.sdk.view.util;

import android.graphics.Matrix;
import android.graphics.PointF;

public class NodePoint {
	public float x;
	public float y;
	public float xc; // constrained on Canvas
	public float yc; // constrained on Canvas

	public NodePoint(float _x, float _y) {
		x = _x;
		y = _y;
	}

	public NodePoint sub(float _x, float _y) {
		return new NodePoint(x - _x, y - _y);
	}

	public NodePoint sub(NodePoint n) {
		return new NodePoint(x - n.x, y - n.y);
	}
	
	/**
	 * return PointF of xc & yc
	 * */
	public PointF getScreenPoint(){
		return new PointF(xc, yc);
	}
	
	/**
	 * @return true if transformed, false is anything else.
	 * */
	public boolean transform(Matrix matrix){
		float[] data = {xc,yc};
		matrix.mapPoints(data);
		xc = data[0];
		yc = data[1];
		return true;
	}
	
	/**
	 * @return true if translated, false is anything else.
	 * */
	public boolean offset(float deltaX,float deltaY){
		xc += deltaX;
		yc += deltaY;
		return true;
	}
	
	/**
	 * convert Lambert projection coordination to canvas coordination.
	 * @param initPos: start position of view
	 * @param mapBounds: bound of map osm
	 * @param canvasBound: bound of canvas
	 * */
	protected void constraintToCanvasBound(MapBounds mapBounds,CanvasBounds canvasBound){
		if (canvasBound.CURRENT_WRAPPER == CanvasBounds.WRAP_HEIGHT) {
			double COEFF = canvasBound.getCoeff();
			yc = canvasBound.height - (float) (COEFF * (y - mapBounds.min.y));
			xc = (float) ((canvasBound.width / mapBounds.dLongitude) * (x - mapBounds.min.x));
		}
	}
}