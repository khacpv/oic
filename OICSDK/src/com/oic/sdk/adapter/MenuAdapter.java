package com.oic.sdk.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oic.sdk.R;
import com.oic.sdk.adapter.MenuAdapter.ItemMenu;

public class MenuAdapter extends ArrayAdapter<ItemMenu>{
	int currentSelection = 0;
	
	public MenuAdapter(Context context, List<ItemMenu> objects) {
		super(context, R.layout.view_item_menu, android.R.id.text1, objects);
	}
	
	public void setCurrentSelection(int index){
		this.currentSelection=index;
	}
	
	public int getCurrentItemSelected(){
		return currentSelection;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(v == null){
			v= LayoutInflater.from(getContext()).inflate(R.layout.view_item_menu, null);
		}
		ItemMenu item = getItem(position);
		TextView text = (TextView)v.findViewById(R.id.text);
		ImageView icon = (ImageView)v.findViewById(R.id.icon);
		text.setText(item.text);
		v.setTag(item);
		
		if(currentSelection == position){
			icon.setImageResource(item.icon);
			v.setBackgroundColor(item.bgColorSelect);
			text.setTextColor(getContext().getResources().getColor(R.color.menu_text_select));
		}else{
			v.setBackgroundResource(R.drawable.item_selector_gray);
			icon.setImageResource(item.iconDrawableDef);
			text.setTextColor(getContext().getResources().getColor(R.color.menu_text));
		}
		return v;
	}
	
	public static class ItemMenu{
		public static final String MENU_ALL = "all";
		public static final String MENU_FOOD = "food";
		public static final String MENU_PLAY = "play";
		public static final String MENU_SHOP = "shop";
		public static final String MENU_HEALTH = "health";
		public static final String MENU_NOTIFY = "notify";
		public static final String MENU_SETTING = "setting";
		
		public String text;
		public String title;
		public int icon;
		public int bgColor;
		public int bgColorSelect;
		public int iconDrawableDef;
		
		public ItemMenu(String title,String text,int iconDrawable,int iconDrawableDef,int bgColor,int bgColorSelect) {
			this.text = text;
			this.title = title;
			this.icon = iconDrawable;
			this.bgColor = bgColor;
			this.bgColorSelect = bgColorSelect;
			this.iconDrawableDef = iconDrawableDef;
		}
	}
}
