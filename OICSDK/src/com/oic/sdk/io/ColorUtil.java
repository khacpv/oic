package com.oic.sdk.io;

import android.graphics.Color;

public class ColorUtil {
	public static final int COLOR_GRAY = Color.GRAY;
	public static final int COLOR_WHITE = Color.WHITE;
	public static final int COLOR_BLUE = Color.BLUE;
	public static final int COLOR_GREEN = Color.GREEN;
	public static final int COLOR_RED = Color.RED;
	public static final int COLOR_YELLOW = Color.YELLOW;
	public static final int COLOR_CYAN = Color.CYAN;
	public static final int COLOR_MAGENTA = Color.MAGENTA;
	public static final int COLOR_TRANS = Color.TRANSPARENT;
	
	public static int getColor(String color){
		if(color == null){
			return COLOR_TRANS;
		}
		if(color.equals("gray")){
			return COLOR_GRAY;
		}
		else if(color.equals("orange")){
			return COLOR_RED;
		}
		return COLOR_YELLOW;
	}
	
	public static int getColorHex(String hexColor){
		if(hexColor == null){
			return COLOR_TRANS;
		}
		return Color.parseColor(hexColor);
	}
}
