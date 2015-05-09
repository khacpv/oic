package com.oic.sdk.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.oic.sdk.OicActivity;
import com.oic.sdk.R;
import com.oic.sdk.adapter.SavedPlaceAdapter;
import com.oic.sdk.adapter.SavedPlaceAdapter.OnSavedPlaceClickListener;
import com.oic.sdk.adapter.TwoLineDropdownAdapter;
import com.oic.sdk.data.Node;
import com.oic.sdk.data.UserData.SavedPlace;
import com.oic.sdk.io.json.UserLoader;

public class RouteActivity extends Activity implements OnSavedPlaceClickListener{
	public static final String TAG = "RouteActivity";
	
	public static final int GET_DIR_REQUEST = 1;  // The request code
	public static final String ID_MY_LOCATION = "my_location_id";
	public static final String ID_START = "start_id";
	public static final String ID_END = "end_id";
	public static final String IS_HAS_MYLOCATION = "has_my_location";
	
	ListView lvSavedPlace;
	
	AutoCompleteTextView start,end;
	
	String startNodeId = "",endNodeId = "";
	
	SavedPlaceAdapter adapter;
	
	TwoLineDropdownAdapter startAdapter;
	TwoLineDropdownAdapter endAdapter;
	
	boolean hasMyLocation = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_route);
		
		lvSavedPlace = (ListView)findViewById(R.id.lv_saved_place);
		
		start = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewStart);
		end = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewEnd);
		
		final ArrayList<Node> stores = OicActivity.storeData.nodes;
		startAdapter = new TwoLineDropdownAdapter(this,stores);
		endAdapter = new TwoLineDropdownAdapter(this,stores);
		start.setAdapter(startAdapter);
		end.setAdapter(endAdapter);
		
		start.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Node node = (Node)parent.getAdapter().getItem(position);
				startNodeId = node.getIdTag();
				endAdapter.setStartNode(node);
				
				String title = node.toString();
				if(title.length() == 0 || "null".equals(title)){
					title = node.getIconShortName();
				}
				start.setText(title);
				
				// close keyboard
				InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
				inputManager.hideSoftInputFromWindow(RouteActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
		});
		
		end.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Node node = (Node)parent.getAdapter().getItem(position);
				endNodeId = node.getIdTag();
				startAdapter.setStartNode(node);
				
				String title = node.toString();
				if(title.length() == 0 || "null".equals(title)){
					title = node.getIconShortName();
				}
				end.setText(title);
				
				// close keyboard
				InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
				inputManager.hideSoftInputFromWindow(RouteActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
		});
		
		if(this.getIntent().hasExtra(ID_START)){
			String startNodeId = this.getIntent().getStringExtra(ID_START);
			Node node = OicActivity.storeData.getNode(startNodeId);
			this.startNodeId = node.getIdTag();
			start.setText(node.toString());
			end.requestFocus();
			endAdapter.setStartNode(node);
		}
		
		if(this.getIntent().hasExtra(IS_HAS_MYLOCATION)){
			hasMyLocation = this.getIntent().getBooleanExtra(IS_HAS_MYLOCATION, false);
		}
		findViewById(R.id.btnMyLocation).setEnabled(hasMyLocation);
		findViewById(R.id.btnMyLocation).setClickable(hasMyLocation);
		
		UserLoader.sortSavedPlace(OicActivity.currentUser);
		adapter = new SavedPlaceAdapter(this, OicActivity.currentUser.savedPlace,OicActivity.mapsData.get(OicActivity.currentMap),OicActivity.mapController.idFloor);
		lvSavedPlace.setAdapter(adapter);
	}
	
	public void btnClicked(View v){
		switch (v.getId()) {
		case R.id.btnGetDirection:
			if(null == startNodeId || null == endNodeId){
				Toast.makeText(this, "Hãy chọn điểm đầu và điểm cuối.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(startNodeId.isEmpty() || endNodeId.isEmpty()){
				Toast.makeText(this, "Hãy chọn điểm đầu và điểm cuối.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(startNodeId.equals(endNodeId)){
				Toast.makeText(this, "Hai điểm không thể giống nhau.", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent returnIntent = new Intent();
			returnIntent.putExtra(ID_START,startNodeId);
			returnIntent.putExtra(ID_END,endNodeId);
			setResult(RESULT_OK,returnIntent);
			finish();
			break;
		case R.id.btnMyLocation:
			if(start.isFocused()){
				startNodeId = ID_MY_LOCATION;
				start.setText("My Location");
			}else{
				endNodeId = ID_MY_LOCATION;
				end.setText("My Location");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onSavedPlaceClick(SavedPlace place, View view) {
		SavedPlaceAdapter.ViewHolder holder = (SavedPlaceAdapter.ViewHolder)view.getTag();
		
		if(start.isFocused()){
			startNodeId = place.storeId+"";
			start.setText(holder.title.getText());
			Node node = OicActivity.storeLayer.getPointRenderByIdTag(place.storeId+"").getNode();
			if(node!=null){
				endAdapter.setStartNode(node);
			}
		}else{
			endNodeId = place.storeId+"";
			end.setText(holder.title.getText());
		}
		Log.e(TAG, "startID: "+startNodeId +" endId: "+endNodeId);
	}
}
