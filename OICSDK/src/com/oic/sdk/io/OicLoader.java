package com.oic.sdk.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.io.piclayer.Piclayer;
import com.oic.sdk.io.prefs.OicPreferences;
import com.oic.sdk.io.xml.OicXmlReader;
import com.oic.sdk.view.OicMapView;
import com.oic.sdk.view.util.OicResource;

public class OicLoader extends Thread{
	public static final String TAG = "OicLoader";
	
	Context activity;
	Handler activityHandler;
	OicMapView mapView;
	
	OicLoaderListener listener;
	OicMapDataLoad data;
	
	public OicLoader(Context activity, Handler handler,
			OicMapView mapView, OicLoaderListener listener,OicMapDataLoad data) {
		this.activity = activity;
		this.activityHandler = handler;
		this.mapView = mapView;
		this.listener = listener;
		this.data = data;
	}

	@Override
	public void run() {
		activityHandler.post(new Runnable() {

			@Override
			public void run() {
				if(listener!=null){
					listener.onLoadStartUI();
				}
			}
		});
		Log.e(TAG, "loading: "+data.toString());
		
		final long time1 = System.currentTimeMillis();
		switch (data.locale) {
		case IoUtil.TYPE_ASSETS:
			loadMapAssets();
			break;
		case IoUtil.TYPE_STORAGE:
		case IoUtil.TYPE_STORAGE_EXT:
			loadMapStorage();
			break;
		default:
			break;
		}
		
		Log.e(TAG, "load map "+data.id+" loadOSM duration: "+(System.currentTimeMillis()-time1)+"ms");
		
		if(listener!=null){
			final long time2 = System.currentTimeMillis();
			listener.onLoadWorker(data);
			Log.e(TAG, "load map "+data.id+" onLoadWorker duration: "+(System.currentTimeMillis()-time2)+"ms");
		}
		
		activityHandler.post(new Runnable() {

			@Override
			public void run() {
				if(listener!=null){
					final long time3 = System.currentTimeMillis();
					listener.onLoadComplete(data);
					Log.e(TAG, "load map "+data.id+" onLoadComplete duration: "+(System.currentTimeMillis()-time3)+"ms");
				}
			}
		});
	}
	
	public void loadMapStorage(){
		OicMapDataLoad _data = OicResource.getInstance(activity).getMapData(data.id);
		if(_data!=null && !OicPreferences.isDebugOicMap(activity)){
			data = _data;
			return;
		}
		try{
			// config
			data.picLayer = new Piclayer();
			data.picLayer.parseFromStorage(activity, IoUtil.getOicMapPath()+File.separator+data.picLayerFile);

			// department
			OicXmlReader.getInstance(activity).parseStorage(IoUtil.getOicMapPath()+File.separator+data.departmentFile);
			data.department = OicXmlReader.getInstance(activity).getOicMapData();

			// stores
			OicXmlReader.getInstance(activity).parseStorage(IoUtil.getOicMapPath()+File.separator+data.storeFile);
			data.store = OicXmlReader.getInstance(activity).getOicMapData();

			// route
			OicXmlReader.getInstance(activity).parseStorage(IoUtil.getOicMapPath()+File.separator+data.routeFile);
			data.route = OicXmlReader.getInstance(activity).getOicMapData();
			
			OicResource.getInstance(activity).addMapData(data);
		} catch (ParserConfigurationException e) {
			if(listener!= null){
				listener.onLoadFailed(e);
			}
		}
	}
	
	public void loadMapAssets() {
		OicMapDataLoad _data = OicResource.getInstance(activity).getMapData(data.id);
		if(_data!=null){
			data = _data;
			return;
		}
		
		try {
			// config
			data.picLayer = new Piclayer();
			data.picLayer.parseFromAssets(activity, data.picLayerFile);

			// department
			OicXmlReader.getInstance(activity).parse(data.departmentFile);
			data.department = OicXmlReader.getInstance(activity).getOicMapData();

			// stores
			OicXmlReader.getInstance(activity).parse(data.storeFile);
			data.store = OicXmlReader.getInstance(activity).getOicMapData();

			// route
			OicXmlReader.getInstance(activity).parse(data.routeFile);
			data.route = OicXmlReader.getInstance(activity).getOicMapData();
			
			OicResource.getInstance(activity).addMapData(data);
		} catch (ParserConfigurationException e) {
			if(listener!= null){
				listener.onLoadFailed(e);
			}
		}
	}
	
	public static class OicMapDataLoad {
		@SerializedName("id")
		public int id;
		
		@SerializedName("name")
		public String name;
		
		@SerializedName("locale")
		public int locale;
		
		@SerializedName("picLayerFile")
		public String picLayerFile;
		
		@SerializedName("departmentFile")
		public String departmentFile;
		
		@SerializedName("storeFile")
		public String storeFile;
		
		@SerializedName("routeFile")
		public String routeFile;
		
		@SerializedName("wifiFile")
		public String wifiFile;
		
		public transient Piclayer picLayer;
		public transient OverlayData department;
		public transient OverlayData store;
		public transient OverlayData route;
		
		public OicMapDataLoad(int id,String name,int locale,String picLayerFile, String departmentFile,
				String storeFile, String routeFile,String wifiFile) {
			this.id = id;
			this.name = name;
			this.locale = locale;
			this.picLayerFile = picLayerFile;
			this.departmentFile = departmentFile;
			this.storeFile = storeFile;
			this.routeFile = routeFile;
			this.wifiFile = wifiFile;
		}
		
		public boolean isEqual(OicMapDataLoad other){
			return id == other.id;
		}
		
		@Override
		public String toString() {
			return "OicMapDataLoad [id=" + id + ", name=" + name + ", locale=" + locale + ", picLayerFile=" + picLayerFile + ", departmentFile=" + departmentFile + ", storeFile=" + storeFile + ", routeFile=" + routeFile + ", wifiFile=" + wifiFile + "]";
		}

		public static ArrayList<OicMapDataLoad> loadFromFile(String mapFile){
			Gson gson = new Gson();
			ArrayList<OicMapDataLoad> loader = new ArrayList<OicLoader.OicMapDataLoad>();
			String content;
			try {
				content = new Scanner(new File(mapFile)).useDelimiter("\\Z").next();
				
				loader = gson.fromJson(content, new TypeToken<List<OicMapDataLoad>>(){}.getType());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return loader;
		}
		
		public static void saveToFile(ArrayList<OicMapDataLoad> data,String file){
			Gson gson = new Gson();
			PrintWriter out = null;
			String json = gson.toJson(data);
			try {
				out = new PrintWriter(file);
				out.print(json);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally{
				if(out!=null){
					out.close();
				}
			}
		}
	}
	
	public static interface OicLoaderListener{
		/**
		 * run on ui thread
		 * */
		public void onLoadStartUI();
		/**
		 * run on NON ui thread
		 * */
		public void onLoadWorker(OicMapDataLoad data);
		/**
		 * run on ui thread
		 * */
		public void onLoadComplete(OicMapDataLoad data);
		/**
		 * run on NON ui thread
		 * */
		public void onLoadFailed(Exception e);
	}
}
