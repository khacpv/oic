package com.oic.sdk.data;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class UserData {
	@SerializedName("id")
	public int id;
	
	@SerializedName("name")
	public String name;
	
	@SerializedName("savedPlace")
	public ArrayList<SavedPlace> savedPlace = new ArrayList<UserData.SavedPlace>();
	
	public UserData() {
		id = -1;
		name = "new account";
		savedPlace = new ArrayList<UserData.SavedPlace>();
	}
	
	public int indexOf(String mapId, int floorId, int storeId){
		for(SavedPlace place: savedPlace){
			if(place.mapId.equals(mapId)){
				if(place.floorId == floorId){
					if(place.storeId == storeId){
						return savedPlace.indexOf(place);
					}
				}
			}
		}
		return -1;
	}
	
	public static class SavedPlace{
		@SerializedName("mapId")
		public String mapId;
		
		@SerializedName("floorId")
		public int floorId;
		
		@SerializedName("storeId")
		public int storeId;
		
		@SerializedName("timeSaved")
		public long timeSaved;
		
	}
}
