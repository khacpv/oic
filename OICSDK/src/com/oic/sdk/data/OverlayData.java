package com.oic.sdk.data;

import java.util.ArrayList;

import android.util.Log;

import com.oic.sdk.abs.OicDataObject;
import com.oic.sdk.algorithm.Pathfinder;
import com.oic.sdk.config.OicConfig;

public class OverlayData extends OicDataObject{
	public static final String TAG = "OicMapData";
	
	public String minlat, minlon, maxlat, maxlon;
	public String version, generator;
	public ArrayList<Node> nodes;
	public ArrayList<Way> ways;
	
	/* A star algorithm */
	Pathfinder Astar; 
	public boolean AstarStatus = false;
	
	public OverlayData() {
		super();
		nodes = new ArrayList<Node>();
		ways = new ArrayList<Way>();
	}
	
	public void setPathNodes(){
		Astar = new Pathfinder(nodes);
		AstarStatus = true;
	}
	
	public void addRelation(){
		if(OicConfig.DEBUG){
			Log.d(TAG, "addRelation is not implemented.");
		}
	}
	
	public Node getNode(String _id){
		for(Node node: nodes){
			if(node.id.equals(_id)){
				return node;
			}
		}
		return null;
	}
	
	public Way getWay(String wayId){
		for(Way way: ways){
			if(way.id.equals(wayId)){
				return way;
			}
		}
		return null;
	}
	
	public void addWay(Way way){
		ways.add(way);
	}
	
	public void addNode(Node node){
		nodes.add(node);
	}
}
