package com.oic.sdk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oic.sdk.R;
import com.oic.sdk.io.image.ImageLoader;
import com.oic.sdk.view.layer.LayerStore;
import com.oic.sdk.view.render.PointRender;
import com.oic.sdk.view.util.OicColor;
import com.oic.sdk.view.util.OicResource;

public class StoreAdapter extends ArrayAdapter<PointRender>{

	private ArrayList<PointRender> data = new ArrayList<PointRender>();
    
	private Context context;
	
	private OnStoreItemClickListener listener;
	
	public StoreAdapter(Context context, List<PointRender> items) {
		super(context, 0, items);
		setDataContext(items,"");
		this.context = context;
	}
	
	public void setDataContext(List<PointRender> items,String filter){
		data.clear();
		for(PointRender point: items){
			
			if(LayerStore.isPointDraw(point) 
					&& (null == filter || "".equals(filter) || "null".equals(filter)
					|| filter.equals(point.getNode().getLocationType()))){
				if(OicColor.isCommonLocationType(point.getNode().getLocationType())){
					continue;
				}
				data.add(point);
			}
		}
		notifyDataSetChanged();
	}
	
	public StoreAdapter setOnStoreItemClickListener(OnStoreItemClickListener listener){
		this.listener = listener;
		return this;
	}
	
	public int getCount() {
		return data.size();
    }
	
	public PointRender getItem(int position) {
        return data.get(position);
    }

	public static class ViewHolder {
		public View layoutItemStore;
		public TextView title;
		public TextView desc;
		public ImageView logo;
		public ImageView icon;
		public ImageButton mapBtn;
		
		public void setOnClickListener(OnClickListener listener){
			layoutItemStore.setOnClickListener(listener);
			title.setOnClickListener(listener);
			icon.setOnClickListener(listener);
			mapBtn.setOnClickListener(listener);
			desc.setOnClickListener(listener);
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if(view == null){
			holder = new ViewHolder();
			
			view = LayoutInflater.from(context).inflate(R.layout.view_item_store, null);
			holder.layoutItemStore = view.findViewById(R.id.layout_item_store);
			holder.title = (TextView)view.findViewById(R.id.title);
			holder.desc = (TextView)view.findViewById(R.id.desc);
			holder.icon = (ImageView)view.findViewById(R.id.icon);
			holder.logo = (ImageView)view.findViewById(R.id.logo);
			holder.mapBtn = (ImageButton)view.findViewById(R.id.btn_map);
			view.setTag(holder);
		}
		final PointRender data = (PointRender)getItem(position);
		holder = (ViewHolder)view.getTag();
		
		holder.title.setText((position+1)+". "+data.getNode().toString());
		holder.desc.setText(data.getNode().getLocationType());
		Bitmap logo = ImageLoader.getLogoFromLocal(getContext(), ImageLoader.getLogoName(data.getNode()));
		if(logo!=null){
			holder.logo.setImageBitmap(logo);
		}else{
			holder.logo.setImageResource(R.drawable.logo_empty);
		}
		
		holder.icon.setImageDrawable(OicResource.getInstance(context).getIcon(data.getNode().getIconName()));
		if(listener!=null){
			OnClickListener clickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onStoreClick(v, position, data);
				}
			};
			holder.setOnClickListener(clickListener);
		}
		
		return view;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public static interface OnStoreItemClickListener{
		public void onStoreClick(View v,int position,PointRender store);
	}
}
