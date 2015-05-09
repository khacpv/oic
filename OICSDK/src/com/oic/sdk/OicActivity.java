package com.oic.sdk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oic.sdk.activity.FindBaby;
import com.oic.sdk.activity.RouteActivity;
import com.oic.sdk.activity.SettingActivity;
import com.oic.sdk.activity.StoreActivity;
import com.oic.sdk.adapter.FloorAdapter;
import com.oic.sdk.adapter.GiftAdapter;
import com.oic.sdk.adapter.GiftAdapter.GiftData;
import com.oic.sdk.adapter.MenuAdapter;
import com.oic.sdk.adapter.MenuAdapter.ItemMenu;
import com.oic.sdk.adapter.StoreAdapter;
import com.oic.sdk.adapter.StoreAdapter.OnStoreItemClickListener;
import com.oic.sdk.adapter.TwoLineDropdownAdapter;
import com.oic.sdk.algorithm.Pathfinder;
import com.oic.sdk.data.Node;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.data.UserData;
import com.oic.sdk.data.UserData.SavedPlace;
import com.oic.sdk.io.IoUtil;
import com.oic.sdk.io.OicLoader;
import com.oic.sdk.io.OicLoader.OicLoaderListener;
import com.oic.sdk.io.OicLoader.OicMapDataLoad;
import com.oic.sdk.io.image.ImageLoader;
import com.oic.sdk.io.json.UserLoader;
import com.oic.sdk.io.network.WifiScanner;
import com.oic.sdk.io.network.WifiScanner.OnScanResult;
import com.oic.sdk.io.network.WifiScanner.StoreWifiItem;
import com.oic.sdk.io.network.WifiScanner.StoreWifiItem.WifiItem;
import com.oic.sdk.io.prefs.OicPreferences;
import com.oic.sdk.view.OicMapController;
import com.oic.sdk.view.OicMapController.OnRotateListener;
import com.oic.sdk.view.OicMapView;
import com.oic.sdk.view.layer.LayerDepartment;
import com.oic.sdk.view.layer.LayerLocation;
import com.oic.sdk.view.layer.LayerRoute;
import com.oic.sdk.view.layer.LayerStore;
import com.oic.sdk.view.layer.LayerStore.OnActionStoreListener;
import com.oic.sdk.view.render.PointRender;
import com.oic.sdk.view.util.MapConfig;
import com.oic.sdk.view.util.NodePoint;
import com.oic.sdk.view.util.OicColor;
import com.oic.sdk.view.util.OicResource;
import com.readystatesoftware.viewbadger.BadgeView;

@SuppressLint("RtlHardcoded")
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class OicActivity extends ActionBarActivity {
	public static final String TAG = "OicActivity";

	public static final int TAB_LIST = 0;
	public static final int TAB_MAP = 1;
	public static final int TAB_GIFT = 2;

	public int currentTab = TAB_MAP;

	public static UserData currentUser;

	OverlayData departmentData;
	public static OverlayData storeData;
	OverlayData routeData;
	OicMapDataLoad data;
	OicMapView mapView;
	public static OicMapController mapController;
	LayerDepartment departmentLayer;
	public static LayerStore storeLayer;
	LayerRoute routeLayer;
	LayerLocation locationLayer;
	Pathfinder algorithm = null;

	PointF start, end;

	View layoutLoading;
	View storeDetail;
	ImageView detailLogo;
	TextView detailStoreTitle;
	ListView listFloors;
	ImageButton btnSaved;
	
	BadgeView badge;

	TextView tabStore;
	TextView tabMap;
	TextView tabGift;

	View layoutList;
	View layoutMap;
	View layoutGift;

	AutoCompleteTextView actv;

	FloorAdapter floorAdapter;
	ArrayList<String> lstFloorId;

	PointRender currentStore;

	OicLoader thread;
	OicLoaderListener loaderListener;
	private MediaPlayer mMediaPlayer;

	ActionBar actionBar;

	// private View mDrawerContent;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	ArrayList<ItemMenu> listMenu = new ArrayList<ItemMenu>();
	private MenuAdapter menuAdapter;

	StoreAdapter storeAdapter;

	ListView listStore;
	ListView listGift;

	private int lastMenuSelected = 0;

	Handler mHandler = new Handler();

	public static ArrayList<String> mapsData = new ArrayList<String>();
	public static ArrayList<String> mapsList = new ArrayList<String>();
	public static int currentMap = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.abs_home);
		// getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));

		actionBar = getSupportActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// mDrawerContent = findViewById(R.id.content_frame);

		layoutLoading = findViewById(R.id.layoutLoading);
		detailStoreTitle = (TextView) findViewById(R.id.detail_store_title);
		detailLogo = (ImageView) findViewById(R.id.detail_logo);
		storeDetail = findViewById(R.id.view_detail);
		listFloors = (ListView) findViewById(R.id.listFloors);
		actv = (AutoCompleteTextView) findViewById(R.id.abs_edt_search);
		btnSaved = (ImageButton) findViewById(R.id.btn_save_store);

		mapView = (OicMapView) findViewById(R.id.oic_mapview);

		tabStore = (TextView) findViewById(R.id.textStore);
		tabMap = (TextView) findViewById(R.id.textMap);
		tabGift = (TextView) findViewById(R.id.textGift);

		layoutList = findViewById(R.id.layoutList);
		layoutMap = findViewById(R.id.layoutMap);
		layoutGift = findViewById(R.id.layoutGift);

		listStore = (ListView) findViewById(R.id.list_store);
		listGift = (ListView) findViewById(R.id.list_gift);

		OnClickListener tabClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeTab(Integer.valueOf(v.getTag().toString()));
			}
		};

		tabStore.setOnClickListener(tabClick);
		tabMap.setOnClickListener(tabClick);
		tabGift.setOnClickListener(tabClick);
		
		badge = new BadgeView(this, tabGift);
		badge.setBackgroundResource(R.drawable.badge_ifaux);
		
		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
			// Set the adapter for the list view
			mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		}

		if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
			mDrawerLayout.setScrimColor(Color.TRANSPARENT);
		}

		menuAdapter = new MenuAdapter(this, listMenu);
		mDrawerList.setAdapter(menuAdapter);
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mapController = mapView.getMapController();
		departmentLayer = new LayerDepartment(this, mapView);
		storeLayer = new LayerStore(this, mapView);
		routeLayer = new LayerRoute(this, mapView);
		locationLayer = new LayerLocation(this, mapView);

		lstFloorId = new ArrayList<String>();
		floorAdapter = new FloorAdapter(this, lstFloorId);
		listFloors.setAdapter(floorAdapter);

		listFloors.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == mapController.idFloor) {
					return;
				}
				mapController.idFloor = position;
				loadMap(mapController.idFloor, mapController.lstMap.get(mapController.idFloor));
			}
		});

		mapView.getOverlayManager().addOverlay(departmentLayer);
		mapView.getOverlayManager().addOverlay(routeLayer);
		mapView.getOverlayManager().addOverlay(storeLayer);
		mapView.getOverlayManager().addOverlay(locationLayer);

		storeLayer.setOnActionStoreListener(new OnActionStoreListener() {

			@Override
			public void onStoreLongClick(PointRender store) {

			}

			@Override
			public void onStoreClick(PointRender store) {
				if (!locationLayer.isShowingAnnotation()) {
					mapController.setCenter(store.getNodePoint().xc, store.getNodePoint().yc);
				}
				if (OicMapView.MODE_DEV || LayerStore.isPointDraw(store)) {
					showAnnotationDetail(store, false, true);
				}

				// test convert data
				// PointF _p =
				// mapView.getPixelFromLatLong(Float.valueOf(store.getNode().lat),
				// Float.valueOf(store.getNode().lon));
				// Log.e(TAG,
				// "convert X_Y: "+store.getNode().lat+"-"+store.getNode().lon+" => "+_p.x+"-"+_p.y);
				//
				// PointF latLon = mapView.getLatLongFromPixel(_p.x, _p.y);
				// Log.e(TAG,
				// "convert LAT_LON: "+_p.x+"-"+_p.y+" => "+latLon.x+"-"+latLon.y);
			}

			@Override
			public void onNothingClick() {
				showAnnotationDetail(null, false, false);
			}

			@Override
			public void onStoreDetailClick(PointRender store) {
				startStoreActivity(store);
			}

			@Override
			public void onNothingLongClick() {

			}

			@Override
			public boolean onBtnLeftClick(final PointRender store) {
				Toast.makeText(getApplicationContext(), "Scanning wifies", Toast.LENGTH_SHORT).show();
				scanWifiAtPoint(store);
				return true;
			}

			@Override
			public boolean onBtnRightClick(PointRender store) {
				Intent pickDirectionIntent = new Intent(OicActivity.this, RouteActivity.class);
				pickDirectionIntent.putExtra(RouteActivity.ID_START, store.getNode().getIdTag());
				startActivityForResult(pickDirectionIntent, RouteActivity.GET_DIR_REQUEST);
				return true;
			}
		});

		loaderListener = new OicLoaderListener() {
			@Override
			public void onLoadStartUI() {
				layoutLoading.setVisibility(View.VISIBLE);
				listFloors.setEnabled(false);
				mapView.setDisableTouch(true);
			}

			@Override
			public void onLoadWorker(OicMapDataLoad data) {
				try {
					long time = System.currentTimeMillis();
					locationLayer.init(); // 309ms

					MapConfig mapConfig = mapView.setPiclayer(data.picLayer);
					departmentLayer.setDataOverlay(mapConfig, data.department);

					storeLayer.setMapConfig(mapConfig);
					storeLayer.setDataOverlay(mapConfig, data.store);

					routeLayer.setMapConfig(mapConfig);
					routeLayer.setDataOverlay(mapConfig, data.route);

					algorithm = new Pathfinder(data.route.nodes);
					departmentData = data.department;
					routeData = data.route;
					storeData = data.store;

					Log.e(TAG, "load time 6: " + (System.currentTimeMillis() - time) + " ms");

				} catch (Exception e) {

				}
			}

			@Override
			public void onLoadComplete(OicMapDataLoad data) {
				loadComponent();
				layoutLoading.setVisibility(View.GONE);
				storeDetail.setVisibility(View.GONE);
				listFloors.setEnabled(true);
				locationLayer.calcMyLocation();
				mapView.setDisableTouch(false);
				mDrawerList.bringToFront();
				mDrawerLayout.bringChildToFront(mDrawerList);
				mDrawerLayout.requestLayout();
				setTitle(mapsList.get(currentMap) + " (Tầng " + mapController.lstMap.get(mapController.idFloor).name + ")");
				mapController.setOnRotationListener(new OnRotateListener() {

					@Override
					public void onRotate(final float angle, final float x, final float y) {
						View compass = OicActivity.this.findViewById(R.id.btn_compass);
						RotateAnimation ra = new RotateAnimation(mapController.getAngle(), -angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
						ra.setDuration(210);
						ra.setFillAfter(true);
						compass.startAnimation(ra);

					}
				});

				// move to map after load
				changeTab(TAB_MAP);
				try {
					NodePoint center = storeLayer.getPointRenders().get(0).getNodePoint();
					mapController.move(center.xc, center.yc, false);
					mapController.zoom(1F, center.xc, center.yc, false);
					mapController.zoom(1.3F, center.xc, center.yc, true);
				} catch (Exception e) {

				}
				// test convert data
				// PointF _o = new PointF(-0.00667888896F, 0.02276906436F);
				// PointF p = mapView.getPixelFromLatLong(_o.x,_o.y);
				// PointF _p = mapView.getLatLongFromPixel(p.x, p.y);
				// Log.e(TAG, "from: "+_o.x+"-"+_o.y+"=>"+_p.x+"-"+_p.y);
			}

			@Override
			public void onLoadFailed(Exception e) {
				e.printStackTrace();
			}
		};
		
		mapsData = new ArrayList<String>();
		mapsList = new ArrayList<String>();
		
		mapsData.add("map.oic");
		mapsList.add("Royal City");
		mapsData.add("lotte.oic");
		mapsList.add("Lottle Tower");

		loadMap();

		loadGift();

		changeTab(TAB_MAP);
	}

	public void loadMap() {
		mapController.lstMap = new ArrayList<OicLoader.OicMapDataLoad>();
		mapController.lstMap = OicMapDataLoad.loadFromFile(IoUtil.getOicMapPath() + File.separator + mapsData.get(currentMap));

		loadMap(mapController.idFloor, mapController.lstMap.get(mapController.idFloor));
	}

	public void scanWifiAtPoint(final PointRender store) {
		Toast.makeText(getApplicationContext(), "Scanning wifies", Toast.LENGTH_SHORT).show();
		WifiScanner.getInstance(getApplicationContext()).setOnScanResult(new OnScanResult() {

			@Override
			public void refresh(ArrayList<WifiItem> result) {

				Toast.makeText(getApplicationContext(), "Found " + result.size() + " wifies", Toast.LENGTH_SHORT).show();

				StoreWifiItem item = new StoreWifiItem();
				item.setStoreId(store.getNode().getIdTag());
				item.setWifiData(result);

				locationLayer.addWifiData(item);
				locationLayer.saveWifiData();
			}
		}).scan();
	}

	public void startStoreActivity(PointRender store) {
		Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
		intent.putExtra(StoreActivity.STATE_STORE_ID, store.getNode().getIdTag() + "");
		startActivityForResult(intent, StoreActivity.GET_STORE);
	}

	public void loadGift() {
		ArrayList<GiftData> data = new ArrayList<GiftData>();
		GiftData dataTest = new GiftData();
		dataTest.bigImageName = "sample_promotion.png";
		dataTest.logoName = "nijyumaru.png";
		dataTest.text1 = "Nijyu Maru đặc biệt khuyến mại 50%";
		dataTest.text2 = "24/12/2014";
		dataTest.longDesc = "Áp dụng tại tất cả các cửa hàng trên toàn quốc";
		data.add(dataTest);

		dataTest = new GiftData();
		dataTest.bigImageName = "pizaplus.jpg";
		dataTest.logoName = "pizzaplus.png";
		dataTest.text1 = "Pizza+Plus thử món mới";
		dataTest.text2 = "15/12/2014";
		dataTest.longDesc = "Chỉ áp dụng các cửa hàng trong Royal City";
		data.add(dataTest);

		dataTest = new GiftData();
		dataTest.bigImageName = "skinfood.jpg";
		dataTest.logoName = "skinfood.jpg";
		dataTest.text1 = "Skin Food giảm giá 75%";
		dataTest.text2 = "24/12/2014";
		dataTest.longDesc = "Áp dụng từ 15 đến 30 tháng 12";
		data.add(dataTest);

		listGift.setAdapter(new GiftAdapter(this, listGift, data));
		
		badge.setText("3");
		tabGift.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				badge.show(true);
			}
		}, 1500);

		// animation hot promotion icon
		final Animation hotAnim = AnimationUtils.loadAnimation(this, R.anim.anim_hot_promotion);

		hotAnim.setAnimationListener(new AnimationListener() {
			long startTime = -1;

			@Override
			public void onAnimationStart(Animation animation) {
				if (startTime <= 0) {
					startTime = System.currentTimeMillis();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (System.currentTimeMillis() - startTime < 8000) {
					badge.postDelayed(new Runnable() {

						@Override
						public void run() {

							badge.startAnimation(hotAnim);

						}
					}, 1500);
				}else{
					badge.setVisibility(View.GONE);
				}
			}
		});
		badge.startAnimation(hotAnim);
	}

	public LayerStore getLayerStore() {
		return storeLayer;
	}

	public LayerRoute getLayerRoute() {
		return routeLayer;
	}

	public void changeTab(int index) {
		if (index < 0 && index > 3) {
			Log.e(TAG, "Index tab is not valid");
			return;
		}
		tabStore.setBackgroundColor(getResources().getColor(R.color.base_color));
		tabGift.setBackgroundColor(getResources().getColor(R.color.base_color));
		tabMap.setBackgroundColor(getResources().getColor(R.color.base_color));

		tabStore.setTextColor(getResources().getColor(R.color.home_tab_3_text_normal));
		tabGift.setTextColor(getResources().getColor(R.color.home_tab_3_text_normal));
		tabMap.setTextColor(getResources().getColor(R.color.home_tab_3_text_normal));

		layoutList.setVisibility(View.GONE);
		layoutMap.setVisibility(View.GONE);
		layoutGift.setVisibility(View.GONE);

		tabStore.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.tab_select_nor);
		tabGift.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.tab_select_nor);
		tabMap.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.tab_select_nor);

		currentTab = index;
		switch (currentTab) {
		case TAB_LIST:
			// tabStore.setBackgroundColor(getResources().getColor(R.color.home_tab_3_select));
			tabStore.setTextColor(getResources().getColor(R.color.home_tab_3_text_select));
			tabStore.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.tab_select);
			layoutList.setVisibility(View.VISIBLE);
			break;
		case TAB_MAP:
			// tabMap.setBackgroundColor(getResources().getColor(R.color.home_tab_3_select));
			tabMap.setTextColor(getResources().getColor(R.color.home_tab_3_text_select));
			layoutMap.setVisibility(View.VISIBLE);
			tabMap.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.tab_select);
			break;
		case TAB_GIFT:
			if(badge !=null){
				badge.hide(false);
			}
			// tabGift.setBackgroundColor(getResources().getColor(R.color.home_tab_3_select));
			tabGift.setTextColor(getResources().getColor(R.color.home_tab_3_text_select));
			layoutGift.setVisibility(View.VISIBLE);
			tabGift.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.tab_select);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void loadMap(int id, OicMapDataLoad data) {
		// if(tData.equals(royalB1Assets)){
		// data = new OicMapDataLoad(id,IoUtil.TYPE_ASSETS, tData[1], tData[0],
		// tData[3], tData[4]);
		// }else{
		// data = new OicMapDataLoad(id,IoUtil.TYPE_STORAGE, tData[1], tData[0],
		// tData[3], tData[4]);
		// }

		// ArrayList<OicMapDataLoad> lstMap = new
		// ArrayList<OicLoader.OicMapDataLoad>();
		// lstMap =
		// OicMapDataLoad.loadFromFile(IoUtil.getOicMapPath()+File.separator+"map.json");
		// Log.e(TAG, lstMap.get(0).departmentFile);

		// data = new OicMapDataLoad(1,"B1",IoUtil.TYPE_STORAGE,
		// royalB1Storage[1], royalB1Storage[0], royalB1Storage[3],
		// royalB1Storage[4],royalB1Storage[5]);
		// lstMap.add(data);
		//
		// data = new OicMapDataLoad(2,"B2",IoUtil.TYPE_STORAGE,
		// royalB2Storage[1], royalB2Storage[0], royalB2Storage[3],
		// royalB2Storage[4],royalB1Storage[5]);
		// lstMap.add(data);
		//
		// data = new OicMapDataLoad(3,"B3",IoUtil.TYPE_STORAGE,
		// royalB3Storage[1], royalB3Storage[0], royalB3Storage[3],
		// royalB3Storage[4],royalB3Storage[5]);
		// lstMap.add(data);
		//
		// data = new OicMapDataLoad(4,"B4",IoUtil.TYPE_STORAGE,
		// royalB4Storage[1], royalB4Storage[0], royalB4Storage[3],
		// royalB4Storage[4],royalB4Storage[5]);
		// lstMap.add(data);
		//
		// OicMapDataLoad.saveToFile(lstMap,
		// IoUtil.getOicMapPath()+File.separator+"map.json");

		mapController.loadMap(id, data, mHandler, loaderListener);
	}

	@SuppressLint("InflateParams")
	public void loadComponent() {
		lstFloorId.clear();
		for (OicMapDataLoad dataMap : mapController.lstMap) {
			lstFloorId.add(dataMap.name);
		}
		floorAdapter.notifyDataSetChanged();
		// set max height for listview floor
		LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) listFloors.getLayoutParams();
		if (floorAdapter.getCount() <= 4) {
			param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		} else {
			param.height = (int) OicResource.dipToPixels(this, 200F);
		}
		listFloors.setLayoutParams(param);
		// end of set maxheight
		try {
			loadSearchComponent();
		} catch (NullPointerException e) {

		}
		// menu component
		listMenu.clear();
		Resources res = getResources();
		listMenu.add(new ItemMenu(ItemMenu.MENU_SHOP, res.getString(R.string.shop), R.drawable.ic_menu_shop, R.drawable.ic_menu_shop_dark, res.getColor(R.color.menu_bg_shop), res.getColor(R.color.menu_bg_shop_select)));
		listMenu.add(new ItemMenu(ItemMenu.MENU_FOOD, res.getString(R.string.food), R.drawable.ic_menu_eatdrink, R.drawable.ic_menu_eatdrink_dark, res.getColor(R.color.menu_bg_food), res.getColor(R.color.menu_bg_food_select)));
		listMenu.add(new ItemMenu(ItemMenu.MENU_HEALTH, res.getString(R.string.healthcare), R.drawable.ic_menu_healthcare, R.drawable.ic_menu_healthcare_dark, res.getColor(R.color.menu_bg_healthcare), res.getColor(R.color.menu_bg_healthcare_select)));
		listMenu.add(new ItemMenu(ItemMenu.MENU_PLAY, res.getString(R.string.play), R.drawable.ic_menu_play, R.drawable.ic_menu_play_dark, res.getColor(R.color.menu_bg_play), res.getColor(R.color.menu_bg_play_select)));
		listMenu.add(new ItemMenu(ItemMenu.MENU_NOTIFY, res.getString(R.string.notify), R.drawable.ic_menu_baby, R.drawable.ic_menu_baby_dark, res.getColor(R.color.menu_bg_play), res.getColor(R.color.menu_bg_play_select)));
		listMenu.add(new ItemMenu(ItemMenu.MENU_SETTING, res.getString(R.string.setting), R.drawable.ic_menu_setting, R.drawable.ic_menu_setting_dark, res.getColor(R.color.menu_bg_setting), res.getColor(R.color.menu_bg_setting_select)));

		menuAdapter = new MenuAdapter(this, listMenu);
		mDrawerList.setAdapter(menuAdapter);

		// load store & gift screen
		storeAdapter = new StoreAdapter(this, storeLayer.getPointRenders()).setOnStoreItemClickListener(new OnStoreItemClickListener() {

			@Override
			public void onStoreClick(View v, int position, PointRender store) {
				switch (v.getId()) {
				case R.id.btn_map:
					changeTab(TAB_MAP);
					showAnnotationDetail(store, true, true);

					storeLayer.setFilterLocationType(store.getNode().getLocationType());
					break;
				default:
					startStoreActivity(store);
					break;
				}

			}
		});

		listStore.setAdapter(storeAdapter);

	}

	public void showAnnotationDetail(PointRender store, boolean animate, boolean isShow) {
		currentStore = store;

		if (isShow) {
			locationLayer.showAnnotation(currentStore);
			String detailStoreTxt = currentStore.getNode().toString();
			if (OicPreferences.isDebugOicMap(this)) {
				detailStoreTxt += "(" + store.getNode().getIdTag() + ")";
			}
			detailStoreTitle.setText(detailStoreTxt);
			Bitmap logo = ImageLoader.getLogoFromLocal(this, ImageLoader.getLogoName(currentStore.getNode()));
			if (logo != null) {
				detailLogo.setImageBitmap(logo);
			} else {
				detailLogo.setImageResource(R.drawable.logo_empty);
			}
			if (animate) {
				mapController.move(currentStore.getNodePoint().xc, currentStore.getNodePoint().yc, false);
				mapController.zoom(1F, currentStore.getNodePoint().xc, currentStore.getNodePoint().yc, false);
				mapController.zoom(6F, currentStore.getNodePoint().xc, currentStore.getNodePoint().yc, true);
				locationLayer.showAnnotation(currentStore);
			} else {
				locationLayer.showAnnotation(currentStore);
			}
			storeDetail.setVisibility(View.VISIBLE);
		} else {
			locationLayer.showAnnotation(currentStore);
			storeDetail.setVisibility(View.GONE);
		}
		int indexSavedPlace = -1;
		try {
			indexSavedPlace = currentUser.indexOf(mapsData.get(currentMap), mapController.idFloor, Integer.valueOf(currentStore.getNode().getIdTag()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		if (indexSavedPlace > -1) {
			btnSaved.setImageResource(R.drawable.star);
		} else {
			btnSaved.setImageResource(R.drawable.star_none);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		mapView.onResume();
		locationLayer.onResume();

		currentUser = UserLoader.loadUser();
	}

	public void showNotification(final PointRender store) {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					showNotification(0, store.getNode().toString(), "New food for your family", R.drawable.sample_promotion);
				} else {
					Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.sample_promotion);
					showHighNotification(0, store.getNode().toString(), "New food for your family", R.drawable.logo, bmp);
				}
			}
		}, 2000);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void showHighNotification(int id, String title, String message, int image, Bitmap bigIcon) {
		boolean autoCancel = true;
		int requestCode = 0;
		int notifyId = id;

		Intent intent = new Intent(this, OicActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, requestCode, intent, Intent.FLAG_ACTIVITY_SINGLE_TOP);

		Notification notify = new Notification.Builder(this).setContentTitle(title).setContentText(message).setSmallIcon(image).setLargeIcon(bigIcon).setAutoCancel(autoCancel).setStyle(new Notification.BigPictureStyle().bigPicture(bigIcon)).setContentIntent(pi).build();

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifyId, notify);

		stop();
		play(getApplicationContext(), R.raw.oic);
	}

	public void showNotification(int id, String title, String message, int image) {
		boolean autoCancel = true;
		int requestCode = 0;
		int notifyId = id;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(image) // notification
																										// icon
				.setContentTitle(title) // title for notification
				.setContentText(message) // message for notification
				.setAutoCancel(autoCancel); // clear notification after click
		Intent intent = new Intent(this, OicActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, requestCode, intent, Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mBuilder.setContentIntent(pi);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifyId, mBuilder.build());

		// play oic.sound
		stop();
		play(getApplicationContext(), R.raw.oic);
	}

	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public void play(Context c, int rid) {
		stop();

		mMediaPlayer = MediaPlayer.create(c, rid);
		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				stop();
			}
		});

		mMediaPlayer.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SettingActivity.ACTION_CODE_SETTING:
			menuAdapter.setCurrentSelection(lastMenuSelected);
			menuAdapter.notifyDataSetChanged();
			break;

		default:
			if (resultCode == RESULT_OK) {
				changeTab(TAB_MAP);

				String startNodeId = data.getStringExtra(RouteActivity.ID_START);
				String endNodeId = data.getStringExtra(RouteActivity.ID_END);
				PointRender start, end;
				PointF pStart, pEnd;

				if (startNodeId.equalsIgnoreCase(RouteActivity.ID_MY_LOCATION)) {
					pStart = new PointF(locationLayer.getMyLocation().x, locationLayer.getMyLocation().y);
				} else {
					start = storeLayer.getPointRenderByIdTag(startNodeId + "");
					if (start == null) {
						return;
					}
					pStart = start.getNodePoint().getScreenPoint();
				}

				if (endNodeId.equalsIgnoreCase(RouteActivity.ID_MY_LOCATION)) {
					pEnd = new PointF(locationLayer.getMyLocation().x, locationLayer.getMyLocation().y);
				} else {
					end = storeLayer.getPointRenderByIdTag(endNodeId + "");
					if (end == null) {
						return;
					}
					pEnd = end.getNodePoint().getScreenPoint();
				}

				routeLayer.getRoute(pStart, pEnd);

				mapController.setCenter(pStart.x, pStart.y);
				// mapController.zoom(1.1F, mapController.getCenter().x,
				// mapController.getCenter().y, true);
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
			break;
		}

	}

	public int getCurrentFloor() {
		return mapController.idFloor;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void loadSearchComponent() {
		final ArrayList<Node> stores = storeData.nodes;
		TwoLineDropdownAdapter adapter = new TwoLineDropdownAdapter(this, stores);
		actv.setAdapter(adapter);

		actv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				actv.setVisibility(View.GONE);
				storeLayer.setFilterLocationType(OicColor.LOCTYPE_DEFAULT);
				// close keyboard
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(OicActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				Node node = (Node) parent.getAdapter().getItem(position);
				String nodeId = node.id;
				PointRender store = storeLayer.getPointRender(nodeId + "");

				if (store != null) {
					changeTab(TAB_MAP);
					showAnnotationDetail(store, false, true);

					mapController.zoom(1F, store.getNodePoint().xc, store.getNodePoint().yc, false);
					mapController.move(store.getNodePoint().xc, store.getNodePoint().yc, false);
					mapController.zoom(6F, store.getNodePoint().xc, store.getNodePoint().yc, true);

					// layoutStoreDetail.setVisibility(View.VISIBLE);
					// storeName.setText(store.getNode().toString());
					// storeDesc.setText(store.getNode().toString());
					currentStore = store;

					if (currentStore.getNode().toString().contains("BBQ")) {
						showNotification(currentStore);
					}
				}
			}

		});
	}

	public void btnClicked(View v) {
		switch (v.getId()) {
		case R.id.btn_route_detail:
			if (currentStore == null) {
				return;
			}
			Intent pickDirectionIntent = new Intent(OicActivity.this, RouteActivity.class);
			pickDirectionIntent.putExtra(RouteActivity.ID_START, currentStore.getNode().id);
			pickDirectionIntent.putExtra(RouteActivity.IS_HAS_MYLOCATION, locationLayer.hasMyLocation());
			startActivityForResult(pickDirectionIntent, RouteActivity.GET_DIR_REQUEST);
			break;
		case R.id.btn_save_store:
			if (currentStore == null) {
				return;
			}
			int indexSavedPlace = -1;
			try {
				indexSavedPlace = currentUser.indexOf(mapsData.get(currentMap), mapController.idFloor, Integer.valueOf(currentStore.getNode().getIdTag()));
			} catch (NumberFormatException e) {
				return;
			}
			// if existed, remove
			if (indexSavedPlace > -1) {
				currentUser.savedPlace.remove(indexSavedPlace);
				btnSaved.setImageResource(R.drawable.star_none);
			}
			// else: add to favorite
			else {
				SavedPlace savePlace = new SavedPlace();
				savePlace.mapId = mapsData.get(currentMap);
				savePlace.floorId = mapController.idFloor;
				savePlace.storeId = Integer.valueOf(currentStore.getNode().getIdTag());
				savePlace.timeSaved = System.currentTimeMillis();
				currentUser.savedPlace.add(savePlace);
				btnSaved.setImageResource(R.drawable.star);
			}

			try {
				UserLoader.saveUser(currentUser);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.btnDirection:
			pickDirectionIntent = new Intent(this, RouteActivity.class);
			pickDirectionIntent.putExtra(RouteActivity.IS_HAS_MYLOCATION, locationLayer.hasMyLocation());
			startActivityForResult(pickDirectionIntent, RouteActivity.GET_DIR_REQUEST);
			break;
		case R.id.btnMyLocation:
			PointF myLocation = locationLayer.getMyLocation();
			mapController.setCenter(myLocation.x, myLocation.y);
			break;
		case R.id.abs_btn_search:
			AutoCompleteTextView edtSearch = (AutoCompleteTextView) findViewById(R.id.abs_edt_search);
			edtSearch.setVisibility(View.VISIBLE);
			break;
		case R.id.view_detail:
			if (currentStore == null) {
				return;
			}
			startStoreActivity(currentStore);
			break;
		case R.id.detail_logo:
			if (OicMapView.MODE_DEV) {
				scanWifiAtPoint(currentStore);
			}
			break;
		case R.id.btn_compass:
			mapController.toggleCompass();

			break;
		case R.id.mytext:
			showSelectMapDialog();
			break;
		case R.id.abs_btn_menu:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
				return;
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
				mDrawerLayout.bringChildToFront(mDrawerList);
				mDrawerLayout.requestLayout();
				supportInvalidateOptionsMenu();
			}

			break;
		default:
			break;
		}
	}

	private void showSelectMapDialog() {
		// custom dialog
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_select_map);
		dialog.setTitle(getString(R.string.select_map));

		// set the custom dialog components - text, image and button
		ListView listMap = (ListView) dialog.findViewById(R.id.list_map);

		listMap.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mapsList));
		listMap.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialog.dismiss();
				if (currentMap == position) {
					return;
				}

				currentMap = position;
				// Toast.makeText(OicActivity.this,
				// "select: "+mapsList.get(position),
				// Toast.LENGTH_SHORT).show();

				loadMap();
			}
		});

		dialog.show();
	}

	@Override
	protected void onPause() {
		super.onPause();

		mapView.onPause();
		locationLayer.onPause();

		try {
			UserLoader.saveUser(currentUser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView) findViewById(R.id.mytext)).setText(title);
		if (null != actionBar)
			actionBar.setTitle(title);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View view, int position, long id) {
			// Highlight the selected item, update the title, and close the
			// drawer
			mDrawerLayout.closeDrawer(mDrawerList);

			ItemMenu itemMn = (ItemMenu) view.getTag();

			if (ItemMenu.MENU_ALL.equals(itemMn.title)) {
				storeLayer.setFilterLocationType(OicColor.LOCTYPE_DEFAULT);

			} else if (ItemMenu.MENU_FOOD.equals(itemMn.title)) {
				storeLayer.setFilterLocationType(OicColor.LOCTYPE_FOOD);
			} else if (ItemMenu.MENU_HEALTH.equals(itemMn.title)) {
				storeLayer.setFilterLocationType(OicColor.LOCTYPE_HEALTH);
			} else if (ItemMenu.MENU_SHOP.equals(itemMn.title)) {
				storeLayer.setFilterLocationType(OicColor.LOCTYPE_SHOPPING);
			} else if (ItemMenu.MENU_PLAY.equals(itemMn.title)) {
				storeLayer.setFilterLocationType(OicColor.LOCTYPE_PLAY);
			} else if (ItemMenu.MENU_SETTING.equals(itemMn.title)) {
				Intent settingAct = new Intent(OicActivity.this, SettingActivity.class);
				startActivityForResult(settingAct, SettingActivity.ACTION_CODE_SETTING);
				return;
			} else if (ItemMenu.MENU_NOTIFY.equals(itemMn.title)) {
				startActivity(new Intent(OicActivity.this, FindBaby.class));
				return;
			}

			mDrawerList.setItemChecked(position, true);
			menuAdapter.setCurrentSelection(position);

			// update list store
			storeAdapter.setDataContext(storeLayer.getPointRenders(), storeLayer.getFilterLocationType());
			storeAdapter.notifyDataSetChanged();
			listStore.setAdapter(storeAdapter);

			if (!itemMn.title.equals(ItemMenu.MENU_SETTING)) {
				lastMenuSelected = position;
				mapController.setCenter(mapController.getCenter().x, mapController.getCenter().y);
				mapController.zoom(OicMapView.MIN_SCALE_STORE * 1.1F, mapController.getCenter().x, mapController.getCenter().y, true);

				showAnnotationDetail(currentStore, false, false);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (actv.getVisibility() == View.VISIBLE) {
			actv.setVisibility(View.GONE);
			return;
		}
		if (routeLayer.back()) {
			return;
		}
		if (storeDetail.getVisibility() == View.VISIBLE) {
			storeDetail.setVisibility(View.GONE);
			return;
		}
		super.onBackPressed();
	}
}