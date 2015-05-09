package com.oic.sdk.io.prefs;

import com.oic.sdk.view.OicMapView;

import android.content.Context;
import android.content.SharedPreferences;

public class OicPreferences {
	public static final String MY_PREFS_NAME = "oic.prefs";
	
	public static final String KEY_SPLASH_SHOW = "oic.activity.splash";
	
	private static SharedPreferences.Editor getEditor(Context ctx){
		return ctx.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
	}
	
	private static SharedPreferences getPrefs(Context ctx){
		return ctx.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public static final String DEBUG_OIC_KEY = "oic.map.debug";
	public static void setDebugOicMap(Context ctx,boolean isDebugable){
		 getEditor(ctx).putBoolean(DEBUG_OIC_KEY, isDebugable).commit();
		 OicMapView.MODE_DEV = isDebugable;
	}
	public static boolean isDebugOicMap(Context ctx){
		return getPrefs(ctx).getBoolean(DEBUG_OIC_KEY, false);
	}
	
	public static final String ROTATE_ENABLE_KEY = "oic.map.rotate";
	public static void setRotateMap(Context ctx,boolean isRotationable){
		 getEditor(ctx).putBoolean(ROTATE_ENABLE_KEY, isRotationable).commit();
	}
	public static boolean isRotateMap(Context ctx){
		return getPrefs(ctx).getBoolean(ROTATE_ENABLE_KEY, true);
	}
	
	public static void setValue(Context ctx,String key,String value){
		getEditor(ctx).putString(key, value).commit();
	}
	
	public static void setValue(Context ctx,String key,int value){
		getEditor(ctx).putInt(key, value).commit();
	}
	
	public static void setValue(Context ctx,String key,boolean value){
		getEditor(ctx).putBoolean(key, value).commit();
	}
	
	public static String getValue(Context ctx,String key,String defValue){
		return getPrefs(ctx).getString(key, defValue);
	}
	
	public static boolean getValue(Context ctx,String key,boolean defValue){
		return getPrefs(ctx).getBoolean(key, defValue);
	}
	
	public static int getValue(Context ctx,String key,int defValue){
		return getPrefs(ctx).getInt(key, defValue);
	}
}
