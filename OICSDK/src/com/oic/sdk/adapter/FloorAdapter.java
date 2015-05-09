package com.oic.sdk.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oic.sdk.R;
import com.oic.sdk.OicActivity;

public class FloorAdapter extends ArrayAdapter<String>{
	OicActivity activity;
	public FloorAdapter(OicActivity activity,List<String> objects) {
		super(activity, R.layout.view_item_floor, R.id.text, objects);
		this.activity = activity;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_item_floor, null);
		TextView tv = (TextView)convertView.findViewById(R.id.text);
		tv.setText(getItem(position)+"");
		if(position == activity.getCurrentFloor()){
			tv.setTextColor(Color.BLACK);
		}else{
			tv.setTextColor(Color.LTGRAY);
		}
		return convertView;
	}
}
