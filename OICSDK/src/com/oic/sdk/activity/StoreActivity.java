package com.oic.sdk.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oic.sdk.R;
import com.oic.sdk.OicActivity;
import com.oic.sdk.data.Node;
import com.oic.sdk.io.image.ImageLoader;
import com.oic.sdk.item.DetailPromotion;
import com.oic.sdk.item.DetailPromotion.OnDataChangeListener;

public class StoreActivity extends ActionBarActivity{
	public static final String STATE_STORE_ID = "store_id";
	public static final int GET_STORE = 2;  // The request code
	
	String storeId = "";
	Node storeNode;
	
	ActionBar actionBar;
	
	LinearLayout layoutPromotion;
	
	ImageView bigImage;
	ImageView logo;
	TextView title,desc;
	
	ArrayList<DetailPromotion> promotions = new ArrayList<DetailPromotion>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.abs_detail);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_color_white));

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		setContentView(R.layout.activity_store);
		
		layoutPromotion = (LinearLayout)findViewById(R.id.detail_promotion);
		bigImage = (ImageView)findViewById(R.id.iv_big);
		logo = (ImageView)findViewById(R.id.detail_header_logo);
		title = (TextView)findViewById(R.id.title);
		desc = (TextView)findViewById(R.id.desc);
		
		if(this.getIntent().hasExtra(STATE_STORE_ID)){
			storeId = getIntent().getStringExtra(STATE_STORE_ID);
			storeNode = OicActivity.storeLayer.getPointRenderByIdTag(storeId).getNode();
			setTitle(storeNode.toString());
			Bitmap logoBmp = ImageLoader.getLogoFromLocal(this, ImageLoader.getLogoName(storeNode));
			if(logoBmp!=null){
				logo.setImageBitmap(logoBmp);
			}
			desc.setText(storeNode.tags.get("location"));
		}else{
			finish();
		}
		
		createDataSample();
		
	}
	
	private void createDataSample(){
		OnDataChangeListener listener = new OnDataChangeListener() {
			
			@Override
			public void change() {
				refresh();
			}
		};
		promotions.clear();
		
		DetailPromotion item = new DetailPromotion(this,listener);
		item.text1 = "Happy christmas!";
		item.text2 = "Chương trình tặng ly áp dụng từ ngày 21-11 đến khi hết quà tặng";
		item.desc = "Số lượng và màu sắc ly sẽ tùy thuộc tại từng cửa hàng.";
		item.getViewHolder().setDataContext(item);
		layoutPromotion.addView(item.getView());
		promotions.add(item);
		
		item = new DetailPromotion(this,listener);
		item.text1 = "85.000đ cho 02 Bulgogi Burger, 01 Khoai Tây Lắc";
		item.text2 = "02 Pepsi (M), 01 Christmas cup.";
		item.desc = "Happy Christmas!";
		item.getViewHolder().setDataContext(item);
		layoutPromotion.addView(item.getView());
		promotions.add(item);
		
		item = new DetailPromotion(this,listener);
		item.text1 = "Happy lunch chỉ với 35.000đ";
		item.text2 = "Từ 10am - 2pm, áp dụng cho các loại combo";
		item.desc = "Fish combo, Premium chicken combo, Cheese combo, Beef rice combo, Chicken ball rice combo.";
		item.getViewHolder().setDataContext(item);
		layoutPromotion.addView(item.getView());
		promotions.add(item);
	}
	
	private void refresh(){
		for(DetailPromotion item: promotions){
			item.holder.refresh();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String startNodeId = data.getStringExtra(RouteActivity.ID_START);
			String endNodeId = data.getStringExtra(RouteActivity.ID_END);
			
			Intent returnIntent = new Intent();
			returnIntent.putExtra(RouteActivity.ID_START,startNodeId);
			returnIntent.putExtra(RouteActivity.ID_END,endNodeId);
			setResult(RESULT_OK,returnIntent);
			finish();
		}
	}
	
	public void btnClicked(View v){
		switch (v.getId()) {
		case R.id.abs_btn_back:
			this.finish();
			break;
		case R.id.abs_btn_route:
			Intent pickDirectionIntent = new Intent(StoreActivity.this, RouteActivity.class);
			pickDirectionIntent.putExtra(RouteActivity.ID_START, storeNode.id);
			startActivityForResult(pickDirectionIntent, RouteActivity.GET_DIR_REQUEST);
			break;
		default:
		}
	}
	
	public void setTitle(String title){
		TextView tv = (TextView)findViewById(R.id.abs_title);
		tv.setText(title);
		this.title.setText(title);
	}
}
