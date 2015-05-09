package com.oic.sdk.view.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.oic.sdk.io.OicLoader.OicMapDataLoad;

public class OicResource {
	private static OicResource INSTANCE;
	
	private Context context;
	private HashMap<String, Bitmap> icons;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, OicMapDataLoad> hashMap = new HashMap<Integer, OicMapDataLoad>();
	
	public static final String[] osmIcons = {
		// pre-load common icons
	};
	
	public OicResource(Context context) {
		this.context = context;
		
		initResource();
	}
	
	public static OicResource getInstance(Context context){
		if(INSTANCE == null){
			INSTANCE = new OicResource(context);
		}
		return INSTANCE;
	}
	
	public void initResource(){
		if(icons != null){
			for(Entry<String, Bitmap> set: icons.entrySet()){
				if(!set.getValue().isRecycled()){
					set.getValue().recycle();
					icons.remove(set.getKey());
				}
			}
			icons.clear();
			icons = null;
		}
		icons = new HashMap<String, Bitmap>();
		
		// osmicon
		for(String iconName: osmIcons){
			icons.put(iconName, initIcon(iconName));
		}
	}
	
	@SuppressLint("UseSparseArrays")
	public void addMapData(OicMapDataLoad data){
		if(hashMap == null){
			hashMap = new HashMap<Integer, OicMapDataLoad>();
		}
		hashMap.put(data.id, data);
	}
	
	public OicMapDataLoad getMapData(int id){
		return hashMap.get(id);
	}
	
	public BitmapDrawable getIcon(String iconName){
		Bitmap bmp = icons.get(iconName);
		if(bmp == null){
			bmp = initIcon(iconName);
		}
		@SuppressWarnings("deprecation")
		BitmapDrawable drawable = new BitmapDrawable(bmp);
		return drawable;
	}
	
	
	private Bitmap initIcon(String iconName){
		AssetManager assetManager = context.getAssets();
		InputStream istr = null;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open("icon/"+iconName);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (OutOfMemoryError e) {
			initResource();
			System.gc();
		}
		
		return bitmap;
	}
	
	public static float dipToPixels(Context context, float dipValue) {
	    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}
	
	public static String bmpToStr64(Bitmap bmp){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
		bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();
		String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
		return encoded;
	}
}
