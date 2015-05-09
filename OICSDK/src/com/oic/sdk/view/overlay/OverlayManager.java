package com.oic.sdk.view.overlay;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;

import com.oic.sdk.abs.AbsMapView;
import com.oic.sdk.abs.AbsOverlay;
import com.oic.sdk.config.OicException;
import com.oic.sdk.io.OicLoader.OicMapDataLoad;
import com.oic.sdk.view.OicMapController;
import com.oic.sdk.view.OicMapView;
import com.oic.sdk.view.layer.LayerDepartment;
import com.oic.sdk.view.layer.LayerLocation;
import com.oic.sdk.view.layer.LayerRoute;
import com.oic.sdk.view.layer.LayerStore;
import com.oic.sdk.view.util.MapConfig;

/**
 * Overlay manager all overlay on mapView.
 * Default overlay at zero is background. 
 * */
public class OverlayManager implements OnTouchListener,
OnScaleGestureListener, OnDoubleTapListener, OnGestureListener{
	Context context;
	OicMapView mapView;
	OicMapController controller;
	
	ArrayList<AbsOverlay> overlays;
	
	LayerDepartment departmentLayer;
	LayerStore storeLayer;
	LayerRoute routeLayer;
	LayerLocation locationLayer;
	
	public OverlayManager(OicMapView mapView){
		this.context = mapView.getContext();
		this.mapView = mapView;
		this.controller = mapView.getMapController();
		
		overlays = new ArrayList<AbsOverlay>();
	}
	
	@SuppressLint("WrongCall")
	public void onDraw(Canvas canvas){
		try{
			synchronized (this) {
				// draw layers
				for(AbsOverlay overlay: overlays){
					if(overlay.isVisible()){
						overlay.onDraw(canvas, mapView);
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
	public void initDefaultOverlay(){
		departmentLayer = new LayerDepartment(this.context, mapView);
		storeLayer = new LayerStore(this.context, mapView);
		routeLayer = new LayerRoute(this.context, mapView);
		locationLayer = new LayerLocation(this.context, mapView);
		
		addOverlay(departmentLayer);
		addOverlay(routeLayer);
		addOverlay(storeLayer);
		addOverlay(locationLayer);
	}
	
	public void setDefaultDataOverLay(MapConfig mapConfig,OicMapDataLoad data){
		for(AbsOverlay overLay: overlays){
			if(overLay instanceof LayerDepartment){
				overLay.setDataOverlay(mapConfig, data.department);
			}
			if(overLay instanceof LayerStore){
				overLay.setMapConfig(mapConfig);
				overLay.setDataOverlay(mapConfig, data.store);
			}
			if(overLay instanceof LayerRoute){
				overLay.setMapConfig(mapConfig);
				overLay.setDataOverlay(mapConfig, data.route);
			}
			if(overLay instanceof LayerLocation){
				// ignore
			}
		}
	}
	
	public LayerDepartment getDefaultLayerDepartMent(){
		return departmentLayer;
	}
	
	public LayerStore getDefaultLayerStore(){
		return storeLayer;
	}
	
	public LayerRoute getDefaultLayerRoute(){
		return routeLayer;
	}
	
	public LayerLocation getDefaultLayerLocation(){
		return locationLayer;
	}
	
	public <T extends AbsOverlay> void addOverlay(T overlay){
		overlays.add(overlay);
	}
	
	public <T extends AbsOverlay> void addOverlay(int index,T overlay) throws OicException{
		try{
			overlays.add(index,overlay);
		}catch(IndexOutOfBoundsException e){
			throw new OicException(e.getMessage());
		}
	}
	
	public AbsOverlay getOverlay(int index){
		return overlays.get(index);
	}
	
	public void init(){
		for(AbsOverlay overLay: overlays){
			overLay.init();
		}
	}
	
	public int size(){
		return overlays.size();
	}
	
	public void removeOverlay(int index){
		overlays.remove(index);
	}
	
	public void removeOverlay(AbsOverlay overlay){
		overlays.remove(overlay);
	}
	
	public boolean transform(Matrix matrix){
		try{
			for(AbsOverlay overlay: overlays){
				overlay.transform(matrix);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean onTouch(AbsMapView mapView,MotionEvent event){
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onTouch(mapView, event);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onDown(e);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onShowPress(e);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onSingleTapUp(e);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onScroll(e1, e2, distanceX, distanceY);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onLongPress(e);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onFling(e1, e2, velocityX, velocityY);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onSingleTapConfirmed(e);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onDoubleTap(e);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onDoubleTapEvent(e);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onScale(detector);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onScaleBegin(detector);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onScaleEnd(detector);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		try{
			for(AbsOverlay overlay: overlays){
				overlay.onTouch(v, event);
			}
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		return true;
	}
}
