package com.oic.sdk.config;

import android.graphics.PointF;

public final class GoogleMapsProjection2 {
	private final int TILE_SIZE = 256;
	private PointF _pixelOrigin;
	private double _pixelsPerLonDegree;
	private double _pixelsPerLonRadian;

	public GoogleMapsProjection2() {
		this._pixelOrigin = new PointF(TILE_SIZE / 2.0F, TILE_SIZE / 2.0F);
		this._pixelsPerLonDegree = TILE_SIZE / 360.0;
		this._pixelsPerLonRadian = TILE_SIZE / (2 * Math.PI);
	}

	private double bound(double val, double valMin, double valMax) {
		double res;
		res = Math.max(val, valMin);
		res = Math.min(val, valMax);
		return res;
	}

	public double degreesToRadians(double deg) {
		return deg * (Math.PI / 180);
	}

	public double radiansToDegrees(double rad) {
		return rad / (Math.PI / 180);
	}
	
	/**
	 * default: zoom = 1
	 * */
	PointF fromLatLngToPoint(double lat, double lng){
		return fromLatLngToPoint(lat, lng, 1);
	}

	PointF fromLatLngToPoint(double lat, double lng, int zoom) {
		PointF point = new PointF(0, 0);

		point.x = (float) (_pixelOrigin.x + lng * _pixelsPerLonDegree);

		// Truncating to 0.9999 effectively limits latitude to 89.189. This is
		// about a third of a tile past the edge of the world tile.
		double siny = bound(Math.sin(degreesToRadians(lat)), -0.9999, 0.9999);
		point.y = (float) (_pixelOrigin.y + 0.5 * Math.log((1 + siny) / (1 - siny)) * -_pixelsPerLonRadian);

		int numTiles = 1 << zoom;
		point.x = point.x * numTiles;
		point.y = point.y * numTiles;
		return point;
	}
	
	/**
	 * default: zoom = 1
	 * */
	public PointF fromPointToLatLng(PointF point){
		return fromPointToLatLng(point,1);
	}

	public PointF fromPointToLatLng(PointF point, int zoom) {
		int numTiles = 1 << zoom;
		point.x = point.x / numTiles;
		point.y = point.y / numTiles;

		double lng = (point.x - _pixelOrigin.x) / _pixelsPerLonDegree;
		double latRadians = (point.y - _pixelOrigin.y) / -_pixelsPerLonRadian;
		double lat = radiansToDegrees(2 * Math.atan(Math.exp(latRadians)) - Math.PI / 2);
		return new PointF((float) lat, (float) lng);
	}

	/**
	 * @Test
	 * */
	public static void main(String[] args) {
		GoogleMapsProjection2 gmap2 = new GoogleMapsProjection2();

		PointF point1 = gmap2.fromLatLngToPoint(41.850033, -87.6500523, 15);
		System.out.println(point1.x + "   " + point1.y);
		PointF point2 = gmap2.fromPointToLatLng(point1, 15);
		System.out.println(point2.x + "   " + point2.y);
	}
}