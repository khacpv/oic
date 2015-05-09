package com.oic.sdk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oic.sdk.R;

public class SplashFragment extends Fragment{
	ImageView imvSplash;
	Holder data;
	int id;
	
	public SplashFragment(int id,Holder data) {
		super();
		this.id = id;
		this.data = data;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_splash, container, false);
		imvSplash = (ImageView)rootView.findViewById(R.id.imv_splash);
		imvSplash.setImageResource(data.imageResource);
        return rootView;
	}
	
	public static class Holder{
		public int imageResource;
		
		public Holder(int imageResource) {
			this.imageResource = imageResource;
		}
	}
}
