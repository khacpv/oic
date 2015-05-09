package com.oic.sdk.io.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.oic.sdk.config.OicConfig;
import com.oic.sdk.config.OicConstant;
import com.oic.sdk.data.Node;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.data.Way;

public class OicHandler extends DefaultHandler {
	public static final String TAG = "OicHandler";
	
	long startTime;
	
	public boolean isLoaded = false;
	public OverlayData oicMapData;

	private String currentElement = "";
	private Way way;
	private Node node;
	private String k;
	private String v;

	public OicHandler() {
		super();

	}

	public void startDocument() {
		startTime = System.currentTimeMillis();
		oicMapData = new OverlayData();
	}

	public void endDocument() {
		oicMapData.setPathNodes();
		if(OicConfig.DEBUG){
			long endTime = System.currentTimeMillis();
			Log.d(TAG, String.format(OicConstant.TOTAL_RUNTIME, "startDocument",(endTime-startTime)+""));
		}
	}

	public void startElement(String uri, String name, String qName,
			Attributes atts) throws SAXException {
//		if(OicConfig.DEBUG){
//			Log.d(TAG, "Parsing: "+name+" tag.");
//		}
		if (name.equals("osm")) {
			oicMapData.generator = atts.getValue("generator");
			oicMapData.version = atts.getValue("version");
			return;
		} 
		else if (name.equals("bounds")) {
			oicMapData.minlat = atts.getValue("minlat");
			oicMapData.maxlat = atts.getValue("maxlat");
			oicMapData.minlon = atts.getValue("minlon");
			oicMapData.maxlon = atts.getValue("maxlon");
			return;
		} 
		else if (name.equals("node")) {
			// increment node counter
			node = new Node();
			node.lat = atts.getValue("lat");
			node.id = atts.getValue("id");
			node.lon = atts.getValue("lon");
			node.x = Float.parseFloat(node.lon);
			node.y = Float.parseFloat(node.lat);
			currentElement = "node";
			return;
		} 
		else if (name.equals("relation")) {
			oicMapData.addRelation();
			return;
		} 
		else if (name.equals("way")) {
			way = new Way();
			way.id = atts.getValue("id");
			way.action = atts.getValue("action");
			way.visible = atts.getValue("visible");
			way.nd = new ArrayList<String>();
			currentElement = "way";
			return;
		} 
		else if (name.equals("nd")) {
			way.nd.add(atts.getValue("ref"));
			return;
		} 
		else if (name.equals("tag")) {
			k = atts.getValue("k");
			v = atts.getValue("v");
			if (currentElement.equals("way"))
				way.addTag(k,v);
			else if(currentElement.equals("node"))
				node.addTag(k,v);
			return;
		}
	}

	public void endElement(String uri, String name, String qName) {
		if (name.equals("way")) {
			oicMapData.addWay(way);
			currentElement = "";
		} 
		else if (name.equals("node")) {
			oicMapData.addNode(node);
			currentElement = "";
		}
	}

	public void characters(char ch[], int start, int length) {

	}

}