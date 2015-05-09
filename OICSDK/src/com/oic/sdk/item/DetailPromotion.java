package com.oic.sdk.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.oic.sdk.R;

@SuppressLint("InflateParams")
public class DetailPromotion {
	
	
	public String text1,text2;
	public String desc;
	
	public ViewHolder holder;
	
	public DetailPromotion(Context context,OnDataChangeListener listener) {
		View parent = LayoutInflater.from(context).inflate(R.layout.detail_promotion, null);
		holder = new ViewHolder(parent, listener);
	}
	
	public ViewHolder getViewHolder(){
		return holder;
	}
	
	public View getView(){
		return holder.parent;
	}
	
	public static class ViewHolder{
		static String currentText1="";
		
		View parent;
		ImageView btnExpand;
		TextView text1,text2;
		View layoutDesc;
		TextView desc;
		
		OnDataChangeListener listener;
		
		public ViewHolder(View parent,OnDataChangeListener listener) {
			this.parent = parent;
			this.listener = listener;
			
			this.btnExpand = (ImageView)this.parent.findViewById(R.id.detail_prom_expand);
			this.text1 = (TextView)this.parent.findViewById(R.id.detail_prom_text1);
			this.text2 = (TextView)this.parent.findViewById(R.id.detail_prom_text2);
			this.layoutDesc = this.parent.findViewById(R.id.detail_prom_layout_desc);
			this.desc = (TextView)this.parent.findViewById(R.id.detail_prom_desc);
			
			this.parent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(currentText1.equalsIgnoreCase(text1.getText().toString())){
						setCurrentPromotion("");
					}else{
						setCurrentPromotion(text1.getText().toString());
					}
					
					if(ViewHolder.this.listener !=null){
						ViewHolder.this.listener.change();
					}
				}
			});
		}
		
		public static void setCurrentPromotion(String text1){
			currentText1 = text1;
		}
		
		public void setDataContext(DetailPromotion data){
			this.text1.setText(data.text1);
			this.text2.setText(data.text2);
			this.desc.setText(data.desc);
		}
		
		public void refresh(){
			if(currentText1.equals(text1.getText().toString())){
				layoutDesc.setVisibility(View.VISIBLE);
			}else{
				layoutDesc.setVisibility(View.GONE);
			}
		}
	}
	
	OnDataChangeListener listener;
	
	public DetailPromotion setOnDataChangeListener(OnDataChangeListener listener){
		this.listener = listener;
		return this;
	}
	public static interface OnDataChangeListener{
		public void change();
	}
}
