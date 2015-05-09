package com.oic.sdk.io.network;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.oic.sdk.io.network.WifiScanner.StoreWifiItem.WifiItem;

public class WifiScanner {
	private static WifiScanner _INSTANCE = null;
	WifiManager wifiMng;
	Context context;
	OnScanResult listener;

	List<ScanResult> results;

	BroadcastReceiver wifiRecv = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent intent) {
			results = wifiMng.getScanResults();
			ArrayList<WifiItem> wifiData = new ArrayList<WifiItem>();
			for (ScanResult item : results) {
				WifiItem wifi = new WifiItem();
				wifi.bssid = item.BSSID;
				wifi.strength = item.level;
				wifiData.add(wifi);
			}
			if (listener != null) {
				listener.refresh(wifiData);
			}
		}
	};

	private WifiScanner(Context context) {
		this.context = context;
		wifiMng = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	}

	public static WifiScanner getInstance(Context context) {
		if (_INSTANCE == null) {
			_INSTANCE = new WifiScanner(context);
		}
		return _INSTANCE;
	}

	public StoreWifiItem getCurrentWifi() {
		try {
			StoreWifiItem wfItem = new StoreWifiItem();
			wfItem.storeId = "";
			results = wifiMng.getScanResults();
			ArrayList<WifiItem> wifiData = new ArrayList<WifiItem>();
			for (ScanResult item : results) {
				WifiItem wifi = new WifiItem();
				wifi.bssid = item.BSSID;
				wifi.strength = item.level;
				wifiData.add(wifi);
			}
			wfItem.wifiData = wifiData;
			return wfItem;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new StoreWifiItem();
	}

	public void scan() {
		context.registerReceiver(wifiRecv, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		if (wifiMng.isWifiEnabled() == false) {
			Toast.makeText(context, "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
			wifiMng.setWifiEnabled(true);
		} else {
			wifiMng.startScan();
			wifiRecv.onReceive(context, null);
		}
	}

	public void stop() {
		context.unregisterReceiver(wifiRecv);
	}

	public WifiScanner setOnScanResult(OnScanResult listener) {
		this.listener = listener;
		return this;
	}

	public static interface OnScanResult {
		public void refresh(ArrayList<WifiItem> result);
	}

	public static class StoreWifiItem {
		private ArrayList<WifiItem> wifiData;
		// _id tag in node
		private String storeId;
		
		public StoreWifiItem() {
			storeId = "";
			wifiData = new ArrayList<WifiItem>();
		}

		public ArrayList<WifiItem> getWifiData() {
			return wifiData;
		}
		
		public int getNumberWifi(){
			return wifiData.size();
		}

		public void setWifiData(ArrayList<WifiItem> wifiData) {
			this.wifiData = wifiData;
		}

		public String getStoreId() {
			return storeId;
		}

		public void setStoreId(String storeId) {
			this.storeId = storeId;
		}

		public static class WifiItem {
			public String bssid;
			public int strength;

			public String getBssid() {
				return bssid;
			}

			public void setBssid(String bssid) {
				this.bssid = bssid;
			}

			public int getStrength() {
				return strength;
			}

			public void setStrength(int strength) {
				this.strength = strength;
			}
		}
	}

}
