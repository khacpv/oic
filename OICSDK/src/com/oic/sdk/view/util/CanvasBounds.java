package com.oic.sdk.view.util;

import com.oic.sdk.view.OicMapView;

public class CanvasBounds {
	public int height = 0;
	public int width = 0;

	private double COEFF;

	public static final int WRAP_WIDTH = 0;
	public static final int WRAP_HEIGHT = 1;

	public int CURRENT_WRAPPER = -1;

	void setWrap(int W) {
		CURRENT_WRAPPER = W;
	}

	public void autoWrap() {
		if (height > width) {
			CURRENT_WRAPPER = WRAP_HEIGHT;

		} else {
			CURRENT_WRAPPER = WRAP_WIDTH;
		}

	}

	public void setCOEFF(double coeff) {
		this.COEFF = coeff;
	}
	
	public int getCurrentWrapper(){
		return this.CURRENT_WRAPPER;
	}

	/*
	 * coefficient equal CanvasBounds.height / MapBounds.dLatitude
	 * */
	public double getCoeff() {
		return this.COEFF;
	}
	
	public void constraint(int width, int height, MapBounds mapBound){
		this.width = width;
		this.height = height;
		
		autoWrap();
		setCOEFF(height / mapBound.dLatitude);
	}
	
	public void constrainToCanvasBound(OicMapView mapView, MapBounds mapBounds, NodePoint P){
		if (this.CURRENT_WRAPPER == CanvasBounds.WRAP_HEIGHT) {
			double COEFF = this.getCoeff();
			P.yc = this.height - (float) (COEFF * (P.y - mapBounds.min.y));
			P.xc = (float) ((this.width / mapBounds.dLongitude) * (P.x - mapBounds.min.x));
		}
	}
}