package com.oic.sdk.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.oic.sdk.R;
import com.oic.sdk.io.prefs.OicPreferences;
import com.oic.sdk.view.OicMapView;

public class SettingActivity extends ActionBarActivity{
	public static final int ACTION_CODE_SETTING = 3;
	
	ActionBar actionBar;
	
	TextView deviceVersion;
	TextView appVersion;
	TextView mapVersion;
	TextView releaseDate;
	ToggleButton btnDebugOicMap;
	ToggleButton btnRotationMap;
	ToggleButton btnShowWelcome;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		setupActionBar();
		
		deviceVersion = (TextView)findViewById(R.id.device_version);
		appVersion = (TextView)findViewById(R.id.app_version);
		mapVersion = (TextView)findViewById(R.id.map_version);
		releaseDate = (TextView)findViewById(R.id.release_date);
		btnDebugOicMap = (ToggleButton)findViewById(R.id.btn_toggle_debug);
		btnRotationMap = (ToggleButton)findViewById(R.id.btn_toggle_rotate);
		btnShowWelcome = (ToggleButton)findViewById(R.id.btn_toggle_splash);
		
		btnDebugOicMap.setChecked(OicPreferences.isDebugOicMap(this));
		btnRotationMap.setChecked(OicPreferences.isRotateMap(this));
		btnShowWelcome.setChecked(OicPreferences.getValue(this, OicPreferences.KEY_SPLASH_SHOW, true));
		
		deviceVersion.setText(android.os.Build.VERSION.SDK_INT+"");
		
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			appVersion.setText(pInfo.versionCode+"");
			releaseDate.setText(pInfo.versionName+"");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		mapVersion.setText(OicMapView.MAP_VERSION+"");
		
		
	}
	
	private void setupActionBar(){
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.abs_def);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
	}
	
	public void btnClicked(View v){
		switch (v.getId()) {
		case R.id.abs_btn_back:
			this.finish();
			break;
		case R.id.btn_toggle_debug:
			ToggleButton btn = (ToggleButton)v;
			OicPreferences.setDebugOicMap(this, btn.isChecked());
			break;
		case R.id.btn_toggle_rotate:
			btn = (ToggleButton)v;
			OicPreferences.setRotateMap(this, btn.isChecked());
			break;
		case R.id.btn_toggle_splash:
			btn = (ToggleButton)v;
			OicPreferences.setValue(this, OicPreferences.KEY_SPLASH_SHOW, btn.isChecked());
			break;
		default:
		}
	}
	
	public void setTitle(String title){
		TextView tv = (TextView)findViewById(R.id.abs_title);
		tv.setText(title);
	}
}
