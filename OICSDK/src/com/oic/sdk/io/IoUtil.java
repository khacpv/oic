package com.oic.sdk.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;

public class IoUtil {
	public static final String folderApp = "OIC";
	
	public static final int TYPE_ASSETS = 0;
	public static final int TYPE_RAW = 1;
	public static final int TYPE_STORAGE = 2;
	public static final int TYPE_STORAGE_EXT = 3;
	public static final int TYPE_NETWORK = 4;
	
	public IoUtil(){
		
	}
	
	public static String getStoragePath(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		return path;
	}
	
	public static boolean isExistsFolderApp (){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File folder = new File(path, folderApp+File.separator+"map");
		return folder.exists();
	}
	
	public static String getOicPath(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File folder = new File(path, folderApp);
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		return folder.getAbsolutePath();
	}
	
	public static String getOicMapPath(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File folder = new File(path, folderApp+"/map");
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		return folder.getAbsolutePath();
	}
	
	public static String getOicLogoPath(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File folder = new File(path, folderApp+"/logo");
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		return folder.getAbsolutePath();
	}
	
	public static String getOicImagePath(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File folder = new File(path, folderApp+"/image");
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		return folder.getAbsolutePath();
	}
	
	public static InputStreamReader getInputStream(Context ctx, int type,String uri){
		InputStreamReader is = null;
		switch (type) {
		case TYPE_ASSETS:
			try {
				is = new InputStreamReader(ctx.getAssets().open(uri));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case TYPE_RAW:
			is = new InputStreamReader(ctx.getResources().openRawResource(Integer.parseInt(uri)));
			break;
		case TYPE_STORAGE:
			try {
				is = new InputStreamReader(new FileInputStream(new File(uri)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case TYPE_STORAGE_EXT:
			try {
				is = new InputStreamReader(new FileInputStream(new File(uri)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case TYPE_NETWORK:
			
			break;
		default:
			break;
		}
		return is;
	}
}
