package com.oic.sdk.data;

import java.util.ArrayList;

import com.oic.sdk.abs.OicDataObject;
/**
 * <way id='-2604' action='modify' visible='true'>
    <nd ref='-2602' />
    <nd ref='-2603' />
    <nd ref='-2605' />
    <nd ref='-2607' />
    <nd ref='-2602' />
    <tag k='loc_color' v='1' />
  </way>
 * */
public class Way extends OicDataObject implements Comparable<Way>{
	public static final String LOC_TYPE = "loc_color";
	
	public String id;
	public String action;
	public String visible;
	public ArrayList<String> nd;
	
	public Way() {
		super();
		nd = new ArrayList<String>();
	}
	
	public void addNode(String nodeId){
		nd.add(id);
	}
	
	public String getBackgroundType(){
		return tags.get(LOC_TYPE);
	}
	
	public int getZindex(){
		String indexStr = tags.get("zindex");
		try{
			int zIndex = Integer.valueOf(indexStr);
			return zIndex;
		}catch(Exception e){
			return 0;
		}
	}

	@Override
	public int compareTo(Way another) {
		return getZindex() - another.getZindex();
	}
}
