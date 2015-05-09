package com.oic.sdk.config;

import android.graphics.PointF;

public class OicUtil {
	/**
	 * Return a new nodePoint according to equations of the Lambert projection
	 * 
	 * @param lat
	 *            latitude (in radians)
	 * @param lon
	 *            longitude (in radians);
	 * 
	 * @return new PointF(x,y)
	 * 
	 */
	public static PointF convertToXY(String lat, String lon) {
		return new GoogleMapsProjection2().fromLatLngToPoint(Double.parseDouble(lat), Double.parseDouble(lon));
//		float phi = (float) GPS.radians(Float.parseFloat(lat));
//		float lambda = (float) GPS.radians(Float.parseFloat(lon));
//		float q = (float) (2 * Math.sin(((Math.PI / 2) - phi) / 2));
//
//		float x = (float) (q * Math.sin(lambda));
//		float y = (float) (q * Math.cos(lambda));
//
//		// canvasBounds.width
//		return new PointF(x, y);
	}
	
	public static PointF convertToLatLon(float x,float y){
		return new GoogleMapsProjection2().fromPointToLatLng(new PointF(x, y));
	}
}
