package com.oic.sdk.adapter;

import java.util.ArrayList;

import com.oic.sdk.fragment.SplashFragment;
import com.oic.sdk.fragment.SplashFragment.Holder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class SplashAdapter extends FragmentStatePagerAdapter {
	ViewPager pager;
	ArrayList<Integer> images = new ArrayList<Integer>();
	
	public SplashAdapter(FragmentManager fm,ViewPager pager) {
		super(fm);
		this.pager = pager;
		this.images = new ArrayList<Integer>();
	}
	
	public void setDataContext(ArrayList<Integer> images){
		this.images.clear();
		this.images.addAll(images);
		this.notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		return new SplashFragment(position,new Holder(images.get(position)));
	}

	@Override
	public int getCount() {
		return images.size();
	}
	
//	@Override
//	public float getPageWidth(int position) {
//		if(position == 0){
//			return 1.0F;
//		}
//		else if(position == getCount()-1){
//			return 1.0F;
//		}
//		return 0.95F;
//	}
}