package com.oic.sdk.activity;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oic.sdk.OicActivity;
import com.oic.sdk.R;
import com.oic.sdk.adapter.SplashAdapter;
import com.oic.sdk.fragment.transform.DepthPageTransformer;
import com.oic.sdk.io.IoUtil;
import com.oic.sdk.io.prefs.OicPreferences;
import com.oic.sdk.io.zip.ZipUtil;
import com.oic.sdk.io.zip.ZipUtil.OnZipListener;

public class SplashActivity extends FragmentActivity implements OnPageChangeListener {
	public static final String TAG = "SplashActivity";

	public static final int ACTION_CODE_SETTING = 4;
	
	private Button btnStart;
	private TextView tvNav;
	
	private ViewPager mPager;
	private ArrayList<Integer> images = new ArrayList<Integer>();
	private SplashAdapter imageAdapter;

	String assetsZipFile = "zip/OIC.zip";
	String folderToUnzip = IoUtil.getStoragePath();
	double zipFileSize = 0;
	int percent = 0;
	
	Handler mHandler = new Handler();
	
	boolean copyZipComplete = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		btnStart = (Button)findViewById(R.id.btn_start);
		tvNav = (TextView)findViewById(R.id.tv_nav);
		
		mPager = (ViewPager) findViewById(R.id.pager);
		imageAdapter = new SplashAdapter(getSupportFragmentManager(),mPager);
		mPager.setAdapter(imageAdapter);
		mPager.setPageTransformer(true, new DepthPageTransformer());
		
		mPager.setOnPageChangeListener(this);

		if (IoUtil.isExistsFolderApp() && !OicPreferences.getValue(this, OicPreferences.KEY_SPLASH_SHOW, true)) {
			copyZipComplete = true;
			loadFinish();
			return;
		}
		
		loadImages();

		copyData();
	}
	
	private void loadImages(){
		images.clear();
		images.add(R.drawable.splash_1);
		images.add(R.drawable.splash_2);
		images.add(R.drawable.splash_3);
		images.add(R.drawable.splash_4);
		images.add(R.drawable.splash_5);
		
		imageAdapter.setDataContext(images);
		refreshUI();
	}
	
	private void refreshUI(){
		tvNav.setText((mPager.getCurrentItem()+1)+"/"+images.size());
	}
	
	public void btnClicked(View v){
		loadFinish();
	}
	
	@Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

	private void copyData() {
		// copy file to sdcard
		try {
			copyZipComplete = false;
			zipFileSize = getAssets().open(assetsZipFile).available();
			ZipUtil.unzip(getAssets().open(assetsZipFile), folderToUnzip, new OnZipListener() {

				@Override
				public void onZipComplete(int total) {
					copyZipComplete = true;
				}

				@Override
				public void onProgress(final int unzipByte) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							int percent = unzipByte * 100 / (int) zipFileSize;
							SplashActivity.this.percent = percent;

							if (percent > 100) {
								SplashActivity.this.percent = 100;
								String dot = "";
								for (int i = 0; i < (percent % 5); i++) {
									dot += ".";
								}
								Log.e(TAG, "Copying"+dot);
							}
							refreshUI();
						}
					});
				}
			});
		} catch (IOException e1) {
			Toast.makeText(this, "Rất tiếc, tải dữ liệu không thành công.", Toast.LENGTH_SHORT).show();
			e1.printStackTrace();
			finish();
		}
	}

	private void loadFinish() {
		if(!copyZipComplete){
			copyData();
			return;
		}
		OicPreferences.setValue(this, OicPreferences.KEY_SPLASH_SHOW, false);
		startActivity(new Intent(SplashActivity.this, OicActivity.class));
		finish();
		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//		Log.e(TAG, "pos: "+position+" offset: "+positionOffset+" pixel: "+positionOffsetPixels);
	}
	boolean isShowAfterFinish = true;
	@Override
	public void onPageSelected(int position) {
		tvNav.setText((position+1)+"/"+images.size());
		AnimationListener listener = new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(isShowAfterFinish){
					btnStart.setVisibility(View.VISIBLE);
				}else{
					btnStart.setVisibility(View.INVISIBLE);
				}
			}
		};
		if(position == images.size()-1){
			isShowAfterFinish = true;
			btnStart.setVisibility(View.VISIBLE);
			Animation alphaAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
			alphaAnim.setAnimationListener(listener);
			btnStart.startAnimation(alphaAnim);
		}else{
			if(btnStart.getVisibility() != View.VISIBLE){
				return;
			}
			isShowAfterFinish = false;
			Animation alphaAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
			alphaAnim.setAnimationListener(listener);
			btnStart.startAnimation(alphaAnim);
		}
	}

}
