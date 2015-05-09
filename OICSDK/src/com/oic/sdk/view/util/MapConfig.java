package com.oic.sdk.view.util;

import android.graphics.Matrix;

import com.oic.sdk.data.OverlayData;
import com.oic.sdk.io.piclayer.Piclayer;
import com.oic.sdk.view.OicMapView;

public class MapConfig {
	CanvasBounds canvasBound;
	MapBounds mapBound;
	Piclayer piclayer;
	
	public MapConfig(){
		canvasBound = new CanvasBounds();
		mapBound = new MapBounds();
	}

	public MapConfig(Piclayer piclayer, CanvasBounds canvasBound, MapBounds mapBound) {
		super();
		this.canvasBound = canvasBound;
		this.mapBound = mapBound;
		this.piclayer = piclayer;
	}
	
	public void setPicLayer(Piclayer piclayer){
		this.piclayer = piclayer;
	}
	
	public Piclayer getPiclayer(){
		return this.piclayer;
	}

	public CanvasBounds getCanvasBound() {
		return canvasBound;
	}

	public void setCanvasBound(CanvasBounds canvasBound) {
		this.canvasBound = canvasBound;
	}

	public MapBounds getMapBound() {
		return mapBound;
	}

	public void setMapBound(MapBounds mapBound) {
		this.mapBound = mapBound;
	}

	public void constrains(OverlayData department,OicMapView mapView){
		Float maxLat = -Float.parseFloat(department.minlat);
		Float maxLon = -Float.parseFloat(department.minlon);
		Float minLat = -Float.parseFloat(department.maxlat);
		Float minLon = -Float.parseFloat(department.maxlon);
		
		int canvasWidth = (int)mapView.getBoundMap().width();
		int canvasHeight = (int)mapView.getBoundMap().height();
		
		mapBound.constrain(minLat, minLon, maxLat, maxLon);
		canvasBound.constraint(canvasWidth, canvasHeight, mapBound);
	}
	
	public void transform(Matrix matrix){
		mapBound.min.transform(matrix);
		mapBound.max.transform(matrix);
	}
}
