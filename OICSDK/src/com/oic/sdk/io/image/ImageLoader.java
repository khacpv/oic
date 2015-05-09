package com.oic.sdk.io.image;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.oic.sdk.data.Node;

public class ImageLoader {
	public static String[][] logo = {
		{"Vinperlland games","gamer1.png"},
		{"Caffenia","caffenia.png"},
		{"Cafe Deluxe","cafedeluxe.png"},
		{"Khu game R1","gamer1.png"},
		{"Nijyu Maru","nijyumaru.png"},
		{"Boss","boss.png"},
		{"Lotteria","lotteria.png"},
		{"Fresh Me Now","freshmenow.png"},
		{"Pizza+ Plus","pizzaplus.png"},
		{"Levi's","levis.png"},
		{"Lacoste","lacoste.png"},
		{"Hair lab","hairlab.png"},
		{"Trà sữa trân châu","trasuatranchau.png"}};
	public static Bitmap getLogoFromLocal(Context context, String name) {
		if(name.length() == 0){
			return null;
		}
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File folder = new File(path, "OIC/logo");
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		File imageFile = new File(folder.getAbsolutePath(), name);
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}
	
	public static Bitmap getImageFromLocal(Context context, String name) {
		if(name.length() == 0){
			return null;
		}
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File folder = new File(path, "OIC/image");
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		File imageFile = new File(folder.getAbsolutePath(), name);
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}
	
	public static String getLogoName(Node store){
		String name = store.toString();
		for(String[] item: logo){
			if(item[0].equalsIgnoreCase(name)){
				return item[1];
			}
		}
		return "";
	}
}
