package com.oic.sdk.view.layer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;

import com.oic.sdk.abs.AbsMapView;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.view.OicMapView;
import com.oic.sdk.view.overlay.OverlayBase;
import com.oic.sdk.view.render.PointContainer;
import com.oic.sdk.view.render.PointRender;
import com.oic.sdk.view.util.MapConfig;
import com.oic.sdk.view.util.OicColor;
import com.oic.sdk.view.util.OicResource;

public class LayerStore extends OverlayBase {
	boolean useThread = false;
	// PathContainer paths;
	PointContainer points;
	ArrayList<PointRender> drawedPointId = new ArrayList<PointRender>();
	ArrayList<PointRender> drawedPointIdTmp = new ArrayList<PointRender>();
	ArrayList<PointRender> drawedPointIdDraw = new ArrayList<PointRender>();
	Queue<ArrayList<PointRender>> queue = new LinkedList<ArrayList<PointRender>>();

	Paint paint_node = new Paint();
	Paint paint_bounds = new Paint();
	Paint paint_ways = new Paint();
	Paint paint_bound = new Paint();

	StringBuilder nodeName = new StringBuilder();

	OicResource resource;

	OnActionStoreListener storeListener;

	String filterLocType = OicColor.LOCTYPE_DEFAULT;

	long lastupdateTime = System.currentTimeMillis();

	public LayerStore(Context context, AbsMapView mapView) {
		super(context, mapView);
		paint_node.setStrokeWidth(6);
		paint_node.setAntiAlias(true);
		paint_node.setStyle(Style.FILL_AND_STROKE);
		paint_node.setColor(Color.RED);

		paint_ways.setStrokeWidth(2);
		paint_ways.setStyle(Style.FILL_AND_STROKE);
		paint_ways.setAntiAlias(true);
		paint_ways.setColor(Color.BLUE);

		paint_bound.setStrokeWidth(6);
		paint_bound.setStyle(Style.FILL);
		paint_bound.setAntiAlias(true);
		paint_bound.setColor(Color.WHITE);

		init();
	}

	public void init() {
		super.init();
		// paths = new PathContainer(this);
		points = new PointContainer(map.getContext(), this);
		resource = OicResource.getInstance(getContext());
	}

	@Override
	public void onDraw(Canvas canvas, AbsMapView mapview) {
		super.onDraw(canvas, mapview);
		float scale = mapview.getScale();
		if (scale <= OicMapView.MIN_SCALE_TEXT) {
			return;
		}
		// draw ways
		// synchronized(paths){
		// for(PathRender pathRender: paths.getPaths()){
		// canvas.drawPath(pathRender.getPath(), pathRender.getPaint());
		// }
		// }
		synchronized (this) {
			// draw nodes
			if (!useThread) {
				invalidateList();
				for (PointRender nodeId : drawedPointId) {
					nodeId.onDrawNode(mapview.getContext(), canvas, mapview, resource);
				}

				for (PointRender point : drawedPointId) {
					point.onDrawText(canvas);
				}
			} else {
				// use thread
				drawedPointIdTmp = queue.poll();
				if (drawedPointIdTmp != null) {
					drawedPointIdDraw = drawedPointIdTmp;
				}
				for (PointRender nodeId : drawedPointIdDraw) {
					nodeId.onDrawNode(mapview.getContext(), canvas, mapview, resource);
				}

				for (PointRender point : drawedPointIdDraw) {
					point.onDrawText(canvas);
				}
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
				}
			}

		}

	}

	public void setFilterLocationType(String locationFilter) {
		if(OicColor.isCommonLocationType(locationFilter)){
			Log.e(TAG, "Invalid filter location TYPE");
			return;
		}

		filterLocType = locationFilter;
		Log.e(TAG, "set loc type: " + locationFilter);
	}
	
	public String getFilterLocationType(){
		return filterLocType;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		if (storeListener == null) {
			return false;
		}

		boolean processed = false;
		for (PointRender point : points.getPoints()) {
			// if (!point.getNode().hasText()) {
			// continue;
			// }
			if (!OicMapView.MODE_DEV) {
				// non touch on invisible node
				if (!point.getNode().isVisibleTag()) {
					continue;
				}
			}

			if (point.isVisible()) {
				if (point.isInside((int) e.getX(), (int) e.getY())) {
					if (storeListener != null) {
						storeListener.onStoreClick(point);
					}
					return processed;
				}
				if (point.isDetailInside((int) e.getX(), (int) e.getY())) {
					boolean isBtnClick = false;
					if (point.isRightBtnInside((int) e.getX(), (int) e.getY())) {
						if (storeListener.onBtnLeftClick(point)) {
							return true;
						}
					}

					if (point.isLeftBtnInside((int) e.getX(), (int) e.getY())) {
						if (storeListener.onBtnRightClick(point)) {
							return true;
						}
					}

					if (!isBtnClick) {
						storeListener.onStoreDetailClick(point);
					}
					return processed;
				}
			}

		}
		if (!processed) {
			storeListener.onNothingClick();
		}
		return processed;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		super.onLongPress(e);
		boolean processed = false;
		for (PointRender point : points.getPoints()) {
			if (point.isVisible() && point.isInside((int) e.getX(), (int) e.getY())) {
				if (storeListener != null) {
					storeListener.onStoreLongClick(point);
				}
				processed = true;
				return;
			}
		}
		if (!processed && storeListener != null) {
			storeListener.onNothingLongClick();
		}
	}
	
	public static boolean isPointDraw(PointRender point){
		return point.getNode().toString().length()>0 && point.getNode().getIconShortName().length()>0 && point.getNode().hasText() &&
				!"null".equals(point.getNode().getLocationType()) && !"null".equals(point.getNode().toString()) && point.getNode().isVisibleTag();
	}

	@Override
	public boolean transform(Matrix matrix) {
		synchronized (points) {
			points.transform(matrix);
		}
		if (useThread) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					synchronized (this) {
						invalidateList();
					}
					
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
					}
					queue.add(drawedPointId);
				}
			}).start();

		}
		return true;
	}

	private void invalidateList() {
		drawedPointId.clear();
		for (PointRender pointRender : points.getPoints()) {
			if (!pointRender.getNode().isVisibleTag()) {
				continue;
			}
			if (!pointRender.isAnnotationVisible()) {
				if (!getMapView().contains((int) pointRender.getNodePoint().xc, (int) pointRender.getNodePoint().yc)) {
					continue;
				}

				if (getMapView().getScale() <= OicMapView.MIN_SCALE_STORE && pointRender.getNode().hasText()) {
					continue;
				}

				// if collection with displaying node, ignore
//				if (PointRender.isCollection(pointRender, points.getPoints(), drawedPointId)) {
//					pointRender.setVisible(false);
//					continue;
//				}
				boolean hasCollection = false;
				for(PointRender _p: drawedPointId){
//					int id1 = Integer.valueOf(_p.getNode().id);
//					int id2 = Integer.valueOf(pointRender.getNode().id);
					if(PointRender.isCollectionDouble(pointRender, _p)){
						
						hasCollection = true;
						break;
					}
				}
				if(hasCollection){
					continue;
				}
			}
			String locType = pointRender.getNode().getLocationType() + "";
			// default hide unknown/NA locations
			pointRender.setVisible(false);
			
			// always draw wc/atm/elevator...
			if(OicColor.isCommonLocationType(locType)){
				pointRender.setVisible(true);
				drawedPointId.add(pointRender);
			}
			// draw all for not filter
			else if(filterLocType.equals(OicColor.LOCTYPE_DEFAULT)){
				pointRender.setVisible(true);
				drawedPointId.add(pointRender);
			}
			// filter for shop/food/...
			else if(OicColor.isNormalLocationType(locType)){
				if(locType.equals(filterLocType)){
					pointRender.setVisible(true);
					drawedPointId.add(pointRender);
				}
			}
		}
		
	}

	@Override
	public void setDataOverlay(MapConfig config, OverlayData data) {
		super.setDataOverlay(config, data);
		synchronized (this) {
			points.init();

			points.render(data);
		}
	}

	public ArrayList<PointRender> getPointRenders() {
		return points.getPoints();
	}

	public PointRender getPointRender(String id) {
		for (PointRender point : points.getPoints()) {
			if (point.getNode().id.equals(id)) {
				return point;
			}
		}
		return null;
	}
	
	public PointRender getPointRenderByIdTag(String idTag) {
		if(null == idTag){
			return null;
		}
		String _idTag = "";
		for (PointRender point : points.getPoints()) {
			_idTag = point.getNode().getIdTag();
			if (null != _idTag && _idTag.equals(idTag)) {
				return point;
			}
		}
		return null;
	}

	public void setOnActionStoreListener(OnActionStoreListener listener) {
		this.storeListener = listener;
	}

	public static interface OnActionStoreListener {
		public void onNothingClick();

		public void onStoreClick(PointRender store);

		public void onStoreDetailClick(PointRender store);

		public void onStoreLongClick(PointRender store);

		public void onNothingLongClick();

		public boolean onBtnLeftClick(PointRender store);

		public boolean onBtnRightClick(PointRender store);
	}
}
