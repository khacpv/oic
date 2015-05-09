package com.oic.sdk.view.layer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.oic.sdk.R;
import com.oic.sdk.OicActivity;
import com.oic.sdk.abs.AbsMapView;
import com.oic.sdk.io.IoUtil;
import com.oic.sdk.io.network.WifiScanner;
import com.oic.sdk.io.network.WifiScanner.StoreWifiItem;
import com.oic.sdk.io.network.WifiScanner.StoreWifiItem.WifiItem;
import com.oic.sdk.view.OicMapController;
import com.oic.sdk.view.OicMapView;
import com.oic.sdk.view.overlay.OverlayBase;
import com.oic.sdk.view.render.PointRender;
import com.oic.sdk.view.util.OicResource;

public class LayerLocation extends OverlayBase {
	float myLocationRadius = 10; 	//dp

	public static final int locationWDef = 20; // dp

	private boolean isMyLocationVisible = false;

	OicResource resource;
	
	int animDuration = 100;
	int locationW = 20; // dp
	public static PointF myLocation = new PointF(0, 0);
	int currentImageIndex = 0;
	long lastUpdateTime = System.currentTimeMillis();

	protected Paint pCoodTouch = new Paint(Paint.ANTI_ALIAS_FLAG);
	protected Paint pCoodTouchStroke = new Paint(Paint.ANTI_ALIAS_FLAG);

	BitmapDrawable[] mylocationIcons = new BitmapDrawable[12];

	Rect myLocationRect = new Rect();

	ArrayList<PointRender> lstAnnotation = new ArrayList<PointRender>();
	
	private ArrayList<StoreWifiItem> listWifiData = new ArrayList<StoreWifiItem>();

	private int[] resourceIcons = { R.drawable.my_location_01, R.drawable.my_location_02, R.drawable.my_location_03, R.drawable.my_location_04, R.drawable.my_location_05, R.drawable.my_location_06, R.drawable.my_location_07, R.drawable.my_location_08, R.drawable.my_location_09,
			R.drawable.my_location_10, R.drawable.my_location_11, R.drawable.my_location_12 };

	BroadcastReceiver wifiRecv = new BroadcastReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			calcMyLocation();
		};
	};

	public LayerLocation(Context context, AbsMapView mapView) {
		super(context, mapView);
		init();
	}

	@SuppressWarnings("deprecation")
	public void init() {
		super.init();
		resource = OicResource.getInstance(getContext());

		locationW = locationWDef;
		
		locationW = (int) OicResource.dipToPixels(getContext(), locationW);
		myLocationRadius = OicResource.dipToPixels(getContext(), myLocationRadius);

		pCoodTouch.setStyle(Style.FILL);
		pCoodTouch.setColor(0x556D9EEB);

		pCoodTouchStroke.setStyle(Style.STROKE);
		pCoodTouchStroke.setStrokeWidth(3);
		pCoodTouchStroke.setColor(0x88285BAC);
		
		int i = 0;
		try {
			for (int iconResource : resourceIcons) {
				Bitmap myLocationIcon = BitmapFactory.decodeResource(getContext().getResources(), iconResource);
				mylocationIcons[i++] = new BitmapDrawable(myLocationIcon);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		lstAnnotation.clear();

		listWifiData = new ArrayList<StoreWifiItem>();
		loadWifiData();

	}

	public void calcMyLocation() {
		try {
			OicActivity activity = (OicActivity) getContext();
			LayerStore storeLayer = activity.getLayerStore();
			LayerRoute routeLayer = activity.getLayerRoute();

			StoreWifiItem currWifi = WifiScanner.getInstance(getContext()).getCurrentWifi();

			StoreWifiItem nearby = null;
			int nearbyMatching = 0;
			for (StoreWifiItem item : listWifiData) {
				int match = matchWifiList(currWifi, item);
				if (match > nearbyMatching) {
					nearbyMatching = match;
					nearby = item;
				}
			}
			if (nearbyMatching > 0) {
				PointRender store = storeLayer.getPointRenderByIdTag(nearby.getStoreId());
				PointF point = store.getNodePoint().getScreenPoint();
				myLocation = routeLayer.getNearby3nd(point.x, point.y).getNodePoint().getScreenPoint();
				isMyLocationVisible = true;
				int totalWifiInPoint = currWifi.getNumberWifi();
				float percentMatch = (float)nearbyMatching/(float)totalWifiInPoint*10;
				
				myLocationRadius = OicResource.dipToPixels(getContext(), 20F-percentMatch)*2;
				if(OicMapView.MODE_DEV){
					Log.e(TAG, "match percent "+nearbyMatching+ "/"+totalWifiInPoint+" : "+percentMatch+"% with Radius: "+myLocationRadius+"pixel");
				}
			} else {
				isMyLocationVisible = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isMyLocationVisible = false;
		}
	}

	public void onPause() {
		getContext().unregisterReceiver(wifiRecv);
	}

	public void onResume() {
		getContext().registerReceiver(wifiRecv, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	@Override
	public void onDraw(Canvas canvas, AbsMapView mapview) {
		super.onDraw(canvas, mapview);

		if (isMyLocationVisible) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastUpdateTime > animDuration) {
				lastUpdateTime = currentTime;
				currentImageIndex = (++currentImageIndex) % mylocationIcons.length;
			}
			myLocationRect.left = (int) myLocation.x - (locationW >> 1);
			myLocationRect.top = (int) myLocation.y - (locationW >> 1);
			myLocationRect.right = (int) myLocation.x + (locationW >> 1);
			myLocationRect.bottom = (int) myLocation.y + (locationW >> 1);
			
			canvas.drawCircle(myLocationRect.exactCenterX(), myLocationRect.exactCenterY(), myLocationRadius, pCoodTouch);
			canvas.drawCircle(myLocationRect.exactCenterX(), myLocationRect.exactCenterY(), myLocationRadius, pCoodTouchStroke);			
			
			mylocationIcons[currentImageIndex].setAntiAlias(true);
			mylocationIcons[currentImageIndex].setBounds(myLocationRect);
			mylocationIcons[currentImageIndex].draw(canvas);
		}

		synchronized (this) {
			for (PointRender point : lstAnnotation) {
				if (point.isAnnotationVisible()) {
					point.onDrawAnnotation(getContext(), canvas, resource);
				}
			}
		}

	}

	public void showAnnotation(PointRender point) {
		synchronized (this) {
			for (PointRender _point : lstAnnotation) {
				_point.setAnnotationVisible(false);
			}
			lstAnnotation.clear();
			if (point == null) {
				return;
			}
			point.setAnnotationVisible(true);
			lstAnnotation.add(point);
		}
	}

	public boolean isShowingAnnotation() {
		return lstAnnotation.size() > 0;
	}

	@Override
	public boolean transform(Matrix matrix) {
		if(!hasMyLocation()){
			return false;
		}
		float[] pts = new float[2];
		pts[0] = myLocation.x;
		pts[1] = myLocation.y;
		matrix.mapPoints(pts);
		myLocation = new PointF(pts[0], pts[1]);
		return true;
	}

	public PointF getMyLocation() {
		return myLocation;
	}
	
	public boolean hasMyLocation(){
		return myLocation.x != 0 && myLocation.y != 0;
	}

	public void setMyLocation(PointF newPoint) {
		myLocation = new PointF(newPoint.x, newPoint.y);
	}

	public void addWifiData(StoreWifiItem item) {
		if(null == item.getStoreId()){
			Log.e(TAG, "Invalid ID");
			return;
		}
		StoreWifiItem _item = getWifi(item.getStoreId());
		if (_item == null) {
			listWifiData.add(item);
		} else {
			_item.setWifiData(item.getWifiData());
		}

		calcMyLocation();
	}

	public StoreWifiItem getWifi(String storeId) {
		for (StoreWifiItem item : listWifiData) {
			if (item.getStoreId().equals(storeId)) {
				return item;
			}
		}
		return null;
	}

	public void saveWifiData() {
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(listWifiData);

		try {
			File wifiFile = getFileWifiData();
			if(wifiFile == null){
				return;
			}
			FileWriter out = new FileWriter(wifiFile);
			out.write(json);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadWifiData() {
		BufferedReader br = null;
		try {
			File wifiFile = getFileWifiData();
			if(wifiFile == null){
				return;
			}
			br = new BufferedReader(new FileReader(wifiFile));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			Gson gson = new GsonBuilder().create();
			listWifiData = gson.fromJson(sb.toString(), new TypeToken<ArrayList<StoreWifiItem>>() {
			}.getType());
			if (listWifiData == null) {
				listWifiData = new ArrayList<StoreWifiItem>();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public File getFileWifiData() {
		// Log.e(TAG,
		// "getFilesDir: "+getContext().getFilesDir().getAbsolutePath());
		try{
			OicMapController controller = (OicMapController)getMapView().getMapController();
			File folder = new File(IoUtil.getOicMapPath(), controller.lstMap.get(controller.idFloor).wifiFile);
			if (!folder.isDirectory()) {
				folder.mkdirs();
			}
			return new File(folder.getAbsolutePath(), "signal.oic");
		}catch(Exception e){
			return null;
		}
		
	}

	public int matchWifiList(StoreWifiItem item1, StoreWifiItem item2) {
		int countMatch = 0;
		for (WifiItem wifi1 : item1.getWifiData()) {
			for (WifiItem wifi2 : item2.getWifiData()) {
				if (wifi1.bssid.equals(wifi2.bssid)) {
					countMatch++;
					break;
				}
			}
		}
		return countMatch;
	}
}
