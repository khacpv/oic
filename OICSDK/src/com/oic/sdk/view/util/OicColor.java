package com.oic.sdk.view.util;


public class OicColor {
	public static final String LOCTYPE_DEFAULT = "";
	public static final String LOCTYPE_BACKGROUND = "floor_background";
	public static final String LOCTYPE_NA = "N/A";
	public static final String LOCTYPE_FOOD = "eat_drink";
	public static final String LOCTYPE_PLAY = "play";
	public static final String LOCTYPE_HEALTH = "healthcare";
	public static final String LOCTYPE_SHOPPING = "shop";
	
	// always suggest search this location
	public static final String[] COMMON_LOCATION_TYPE = {
		"wc","elevator","atm","exit","parking","info"
	};
	
	// search/filter locations
	public static final String[] NORMAL_LOCATION_TYPE = {
		LOCTYPE_FOOD,LOCTYPE_PLAY,LOCTYPE_HEALTH,LOCTYPE_SHOPPING
	};
	
	public static boolean isCommonLocationType(String locType){
		for(String item: COMMON_LOCATION_TYPE){
			if(item.equals(locType)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNormalLocationType(String locType){
		for(String item: NORMAL_LOCATION_TYPE){
			if(item.equals(locType)){
				return true;
			}
		}
		return false;
	}
	
	public static int getBackgroundColor(String locationType) {
		int color = 0xFFFFFFFF;
		if(locationType == null){
			locationType = LOCTYPE_DEFAULT;
		}
		
		if ("1".equals(locationType)) {
			color = 0x88FCE883;
		} else if ("3".equals(locationType)) {
			color = 0x6689CFF0;
		} else if ("4".equals(locationType)) {
			color = 0x88FFB6C1;
		} else if ("2".equals(locationType)) {
			color = 0x6644B984;
		} else if (LOCTYPE_BACKGROUND.equals(locationType)) {
			color = 0xFFFFFFFF;
		} else if ("5".equals(locationType)) {
			color = 0xFFE5E5E5;
		} else {
		}
		return color;
	}
	
	public static int getTextStrokeColor(String locationColor) {
		int color = 0xFFFFFFFF;
		if(locationColor == null){
			locationColor = LOCTYPE_DEFAULT;
		}
		
		if ("1".equals(locationColor)) {
			color = 0x55EAA041;
		} else if ("3".equals(locationColor)) {
			color = 0xFFFFFFFE;
		} else if ("4".equals(locationColor)) {
			color = 0xFFF7A7C0;
		} else if ("2".equals(locationColor)) {
			color = 0xFFC6F3DE;
		} else if ("6".equals(locationColor)) {
			color = 0xFFFFFFFF;
		} else if ("5".equals(locationColor)) {
			color = 0xFF999999;
		} else {
		}
		return color;
	}
	
	public static int getTextColor(String locationColor) {
		int color = 0xFF83334C;
		if(locationColor == null){
			locationColor = LOCTYPE_DEFAULT;
		}
		
		if ("1".equals(locationColor)) {
			color = 0xFF866031;
		} else if ("3".equals(locationColor)) {
			color = 0xFF434343;
		} else if ("4".equals(locationColor)) {
			color = 0xFFA3334C;
		} else if ("2".equals(locationColor)) {
			color = 0xFF1A764D;
		} else if ("6".equals(locationColor)) {
			color = 0xFF83334C;
		} else if ("5".equals(locationColor)) {
			color = 0xFF83334C;
		} else {
		}
		return color;
	}
}
