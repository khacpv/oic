package com.oic.sdk.adapter;

import java.io.IOException;
import java.util.ArrayList;

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

import com.oic.sdk.OicActivity;
import com.oic.sdk.R;
import com.oic.sdk.activity.RouteActivity;
import com.oic.sdk.data.Node;
import com.oic.sdk.data.UserData;
import com.oic.sdk.data.UserData.SavedPlace;
import com.oic.sdk.io.image.ImageLoader;
import com.oic.sdk.io.json.UserLoader;
import com.oic.sdk.view.util.OicResource;

public class SavedPlaceAdapter extends ArrayAdapter<UserData.SavedPlace>{
	ArrayList<SavedPlace> savedPlace = new ArrayList<UserData.SavedPlace>();
	public SavedPlaceAdapter(Context context, ArrayList<SavedPlace> objects,String mapId,int floorId) {
		super(context, 0, 0, objects);
		setDataContext(objects, mapId, floorId);
	}
	
	public void setDataContext(ArrayList<SavedPlace> savedPlace,String mapId,int floorId){
		this.savedPlace.clear();
		for(SavedPlace place: savedPlace){
			if(place.mapId.equalsIgnoreCase(mapId) && place.floorId == floorId){
				this.savedPlace.add(place);
			}
		}
		notifyDataSetChanged();
	}
	
	@Override
	public SavedPlace getItem(int position) {
		return this.savedPlace.get(position);
	}
	
	@Override
	public int getCount() {
		return this.savedPlace.size();
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_item_saved_place, null);
			ViewHolder holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.desc = (TextView)convertView.findViewById(R.id.desc);
			holder.logo = (ImageView)convertView.findViewById(R.id.logo);
			holder.icon = (ImageView)convertView.findViewById(R.id.icon);
			holder.layout = (View)convertView.findViewById(R.id.layout_item_save_store);
			holder.delete = (ImageButton)convertView.findViewById(R.id.btn_delete);
			convertView.setTag(holder);
		}
		final SavedPlace place = getItem(position);
		
		ViewHolder holder = (ViewHolder)convertView.getTag();
		String idTag = place.storeId+"";
		Node node = OicActivity.storeLayer.getPointRenderByIdTag(idTag).getNode();
		holder.title.setText(node.toString());
		holder.desc.setText(node.getLocationType());
		
		Bitmap logo = ImageLoader.getLogoFromLocal(getContext(), ImageLoader.getLogoName(node));
		if(logo!=null){
			holder.logo.setImageBitmap(logo);
		}else{
			holder.logo.setImageResource(R.drawable.logo_empty);
		}
		
		holder.icon.setImageDrawable(OicResource.getInstance(getContext()).getIcon(node.getIconName()));
		
		holder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OicActivity.currentUser.savedPlace.remove(place);
				SavedPlaceAdapter.this.savedPlace.remove(place);
				try {
					UserLoader.saveUser(OicActivity.currentUser);
				} catch (IOException e) {
				}
				notifyDataSetChanged();
			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RouteActivity)getContext()).onSavedPlaceClick(place, v);
			}
		});
		return convertView;
	}
	
	public static class ViewHolder{
		public TextView title;
		public TextView desc;
		public ImageView logo;
		public ImageView icon;
		public ImageButton delete;
		public View layout;
	}
	
	public static interface OnSavedPlaceClickListener{
		public void onSavedPlaceClick(SavedPlace place,View v);
	}
}
