package com.oic.sdk.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oic.sdk.R;
import com.oic.sdk.adapter.GiftAdapter.GiftData;
import com.oic.sdk.io.image.ImageLoader;

public class GiftAdapter extends ArrayAdapter<GiftData> implements OnClickListener{
	int posSelected = -1;
	ListView lv;
	public GiftAdapter(Context context,ListView lv, ArrayList<GiftData> data) {
		super(context, R.layout.view_item_gift, data);
		this.lv = lv;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(v==null){
			v=LayoutInflater.from(getContext()).inflate(R.layout.view_item_gift, null);
			ViewHolder holder = new ViewHolder(position);
			holder.imvBig = (ImageView)v.findViewById(R.id.iv_big);
			holder.imvExpand = (ImageView)v.findViewById(R.id.iv_btn_expand);
			holder.layoutExpand = v.findViewById(R.id.layout_desc_expand);
			holder.layoutDesc = v.findViewById(R.id.layout_desc);
			holder.text1 = (TextView)v.findViewById(R.id.text1);
			holder.text2 = (TextView)v.findViewById(R.id.text2);
			holder.imvLogo =(ImageView)v.findViewById(R.id.iv_logo);
			holder.longDesc = (TextView)v.findViewById(R.id.long_desc);
			v.setTag(holder);
		}

		final GiftData data = getItem(position);
		final ViewHolder holder = (ViewHolder)v.getTag();
		
		holder.imvBig.setImageBitmap(ImageLoader.getImageFromLocal(getContext(), data.bigImageName));
		holder.text1.setText(data.text1+"");
		holder.text2.setText(data.text2+"");
		holder.longDesc.setText(data.longDesc+"");
		holder.imvLogo.setImageBitmap(ImageLoader.getLogoFromLocal(getContext(), data.logoName));
		
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(posSelected != position){
					posSelected = position;
					
					if(position == getCount()-1){
						scrollMyListViewToBottom();
					}
				}
				else {
					posSelected = -1;
				}
				notifyDataSetChanged();
				
			}
		};
		holder.imvExpand.setOnClickListener(listener);
		holder.layoutDesc.setOnClickListener(listener);
		
		if(position == posSelected){
			holder.layoutExpand.setVisibility(View.VISIBLE);
			holder.imvExpand.setImageResource(R.drawable.ic_store_arrow_up);
		}else{
			holder.layoutExpand.setVisibility(View.GONE);
			holder.imvExpand.setImageResource(R.drawable.ic_store_arrow_down);
		}
		
		return v;
	}
	
	private void scrollMyListViewToBottom() {
	    lv.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	            lv.setSelection(GiftAdapter.this.getCount() - 1);
	        }
	    },10);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_btn_expand:
			
			break;

		default:
			break;
		}
	}
	
	public static class ViewHolder{
		ImageView imvBig;
		ImageView imvExpand;
		View layoutExpand;
		View layoutDesc;
		TextView text1,text2;
		TextView longDesc;
		ImageView imvLogo;
		
		public ViewHolder(int position) {
		}
	}
	
	public static class GiftData{
		public String storeName;
		public String text1;
		public String text2;
		public String longDesc;
		public String bigImageName;
		public String logoName;
	}
}
