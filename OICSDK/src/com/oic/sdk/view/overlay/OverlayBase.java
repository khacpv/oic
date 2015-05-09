package com.oic.sdk.view.overlay;

import android.content.Context;
import android.graphics.Canvas;

import com.oic.sdk.abs.AbsMapView;
import com.oic.sdk.abs.AbsOverlay;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.view.util.CanvasBounds;
import com.oic.sdk.view.util.MapBounds;
import com.oic.sdk.view.util.MapConfig;

public class OverlayBase extends AbsOverlay{
	protected OverlayData overlayData;
	
	protected MapConfig mapConfig;
	
	public OverlayBase(Context context,AbsMapView mapView) {
		super(context,mapView);
	}
	public static final String TAG = "OverlayBase";
	
	public void setDataOverlay(MapConfig mapConfig, OverlayData data) {
		this.mapConfig = mapConfig;
		this.overlayData = data;
	}
	
	public void setMapConfig(MapConfig mapConfig){
		this.mapConfig = mapConfig;
	}
	
	public void init(){
		
	}
	
	@Override
	public void onDraw(Canvas canvas, AbsMapView mapview) {
		
	}
	
	public MapBounds getMapBound(){
		return mapConfig.getMapBound();
	}
	
	public CanvasBounds getCanvasBound(){
		return mapConfig.getCanvasBound();
	}
	
	public OverlayData getOverlayData(){
		return overlayData;
	}
}
