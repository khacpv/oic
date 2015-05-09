package com.oic.sdk.config;

import android.location.Location;
import android.os.Handler;
import android.os.Message;


public class GPS {
	public Location location = new Location("gps");
	public Location target = new Location("gps");
	public static int LOCATION_UPDATED = 1;
	public boolean isTarget = false;
	
	private int Radius = 6371000;
	
	private Thread mockLocationUpdate = null; 
	private Handler locationUpdater;
	
	private double mockLocations[][] = {
			{8.653986,49.472767},
			{9.670434,49.449615},
			{18.654018,49.472752},
			{38.653986,49.472767}
	};
	private int mockLocationsPointer = 0;
	
	public GPS(Handler _locationUpdater){
		location.setLatitude(45.4722635);
		location.setLongitude(9.1875991);
		
		
		locationUpdater = _locationUpdater;
		
		mockLocationUpdate = new Thread(new locationUpdating()); 
		 
	}
	public void startLocationUpdater(){
		mockLocationUpdate.start();
	}
	public void stopLocationUpdater(){
		mockLocationUpdate.interrupt();//();
	}
	public void setActualLocation(double latitude, double longitude){
		location.setLatitude(latitude);
		location.setLongitude(longitude);
	}
	
	public void setTargetLocation(double latitude, double longitude){
		isTarget = true;
		target.setLatitude(latitude);
		target.setLongitude(longitude);
	}
	public double getHaversineDistance(){
	    
		double dLat = radians(target.getLatitude() - location.getLatitude());
   	    double dLon = radians(target.getLongitude() - location.getLongitude()); 
   	    
   	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
   	        Math.cos(radians(location.getLatitude())) * Math.cos(radians(target.getLatitude())) * 
   	        Math.sin(dLon/2) * Math.sin(dLon/2); 
   	    
   	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
   	   
   	    return Radius * c;
   	
   }
	
   public String getSmartHaversineDistance(){
	   double hd = getHaversineDistance();
	   String shd = "";
	   if(hd > 1000){
		   shd = Math.round(hd/1000) + " km";
	   } else {
		   shd = Math.round(hd) + " m";
	   }
	   return shd;
   }
	
   public static double radians(double n) {
  	  return n * Math.PI / 180;
   }
   
   public static double degree(double radian){
	   return radian * 180 / Math.PI;
   }
   
   class locationUpdating implements Runnable{ 
       // @Override 
       public void run() { 
            while(!Thread.currentThread().isInterrupted()){
            	Message m = new Message();
            	m.what = LOCATION_UPDATED;
            	// update location with strings
            	mockLocationsPointer++;
            	if(mockLocationsPointer > mockLocations.length - 1){
            		mockLocationsPointer = 0;
            	}
            	setActualLocation(mockLocations[mockLocationsPointer][0],mockLocations[mockLocationsPointer][1] );
            	
            	locationUpdater.sendMessage(m);
                
            	try { 
                      Thread.sleep(5000); 
                 } catch (InterruptedException e) { 
                      Thread.currentThread().interrupt(); 
                 } 
            } 
       } 
 } 
}
