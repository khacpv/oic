package com.oic.sdk.abs;

import java.util.HashMap;

public abstract class OicDataObject{
	public HashMap<String, String> tags;
	
	public OicDataObject() {
		tags = new HashMap<String, String>();
	}
	
	public void addTag(String k,String v){
		tags.put(k, v);
	}
}
