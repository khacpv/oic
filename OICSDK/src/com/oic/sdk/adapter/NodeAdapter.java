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
import com.oic.sdk.data.Node;
import com.oic.sdk.io.image.ImageLoader;
import com.oic.sdk.view.util.OicResource;

public class NodeAdapter extends ArrayAdapter<Node>{

	private ArrayList<Node> data = new ArrayList<Node>();
    
	private Context context;
	
	private OnStoreItemClickListener listener;
	
	public NodeAdapter(Context context, List<Node> items) {
		super(context, 0, items);
		setDataContext(items,"");
		this.context = context;
	}
	
	public void setDataContext(List<Node> items,String filter){
		data.clear();
		for(Node node: items){
			if("null".equals(node.toString())){
				continue;
			}
			data.add(node);
		}
	}
	
	public NodeAdapter setOnStoreItemClickListener(OnStoreItemClickListener listener){
		this.listener = listener;
		return this;
	}
	
	public int getCount() {
		return data.size();
    }
	
	public Node getItem(int position) {
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
		final Node data = (Node)getItem(position);
		holder = (ViewHolder)view.getTag();
		
		holder.title.setText(data.toString());
		holder.desc.setText(data.getLocationType());
		Bitmap logo = ImageLoader.getLogoFromLocal(getContext(), ImageLoader.getLogoName(data));
		if(logo!=null){
			holder.logo.setImageBitmap(logo);
		}else{
			holder.logo.setImageResource(R.drawable.logo_empty);
		}
		
		holder.icon.setImageDrawable(OicResource.getInstance(context).getIcon(data.getIconName()));
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
		public void onStoreClick(View v,int position,Node node);
	}
}
