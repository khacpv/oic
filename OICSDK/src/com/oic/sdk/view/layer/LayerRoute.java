package com.oic.sdk.view.layer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;

import com.oic.sdk.abs.AbsMapView;
import com.oic.sdk.algorithm.Pathfinder;
import com.oic.sdk.data.Node;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.view.overlay.OverlayBase;
import com.oic.sdk.view.render.PathRouteContainer;
import com.oic.sdk.view.render.PointContainer;
import com.oic.sdk.view.render.PointRender;
import com.oic.sdk.view.util.MapConfig;
import com.oic.sdk.view.util.OicResource;

public class LayerRoute extends OverlayBase {

	PathRouteContainer paths;
	PointContainer points;

	Paint paint_node = new Paint();
	Paint paint_route = new Paint();
	Paint paint_border = new Paint();

	ArrayList<String> drawedPointId = new ArrayList<String>();
	OicResource resource;

	Pathfinder algorithm = null;
	Path path = new Path();

	PointF start, end;
	
	float routeStrokeOut = 8; // dp
	float routeStrokeIn = 5; // dp
	
	private boolean drawPath = false;

	public LayerRoute(Context context, AbsMapView mapView) {
		super(context, mapView);
		
		routeStrokeIn = OicResource.dipToPixels(getContext(), routeStrokeIn);
		routeStrokeOut = OicResource.dipToPixels(getContext(), routeStrokeOut);


		paint_route.setStrokeWidth(routeStrokeOut);
		paint_route.setAntiAlias(true);
		paint_route.setStyle(Style.STROKE);
		paint_route.setColor(0xFF285BAC);
		paint_route.setStrokeJoin(Join.ROUND);
		paint_route.setStrokeCap(Cap.ROUND);
		// paint_route.setShadowLayer(3, 0, 0, 0xFF6D9EEB);

		paint_border.setStrokeWidth(routeStrokeIn);
		paint_border.setAntiAlias(true);
		paint_border.setStyle(Style.STROKE);
		paint_border.setColor(0xFF6D9EEB);
		paint_border.setStrokeJoin(Join.ROUND);
		paint_border.setStrokeCap(Cap.ROUND);
		// paint_border.setShadowLayer(3, 0, 0, 0xFF285BAC);

		init();
	}

	public void init() {
		super.init();
		points = new PointContainer(map.getContext(), this);
		paths = new PathRouteContainer(this, points);

		resource = OicResource.getInstance(getContext());
	}

	@Override
	public void onDraw(Canvas canvas, AbsMapView mapview) {
		super.onDraw(canvas, mapview);
		// draw ways
		// synchronized(paths){
		// for(PathRender pathRender: paths.getRoute()){
		// canvas.drawPath(pathRender.getPath(), paint_route);
		// }
		// }

		if (null != path && drawPath) {
			synchronized (path) {
				canvas.drawPath(path, paint_route);
				canvas.drawPath(path, paint_border);
			}
		}

		// draw nodes
		// synchronized (this) {
		// drawedPointId.clear();
		// for(PointRender pointRender: points.getPoints()){
		// if(PointRender.isCollection(pointRender, points.getPoints(),
		// drawedPointId)){
		// continue;
		// }
		//
		// drawedPointId.add(pointRender.getNode().id);
		// pointRender.onDrawNode(canvas, resource);
		// }
		// }
	}

	/**
	 * get route from nodes of routeData
	 * */
	public Path getRoute(Node start, Node end) {
		path = paths.calculate(start, end);
		return path;
	}

	/**
	 * get route from any point on screen
	 * */
	public Path getRoute(PointF start, PointF end) {
		drawPath = true;
		this.start = start;
		this.end = end;
		try {
			PointRender startNode = getNearby(start.x, start.y);
			PointRender endNode = getNearby(end.x, end.y);
			path = paths.calculate(end, start, startNode.getNode(), endNode.getNode());
		} catch (NullPointerException e) {
			e.printStackTrace();
			path = new Path();
			return path;
		}
		return path;
	}

	@Override
	public boolean transform(Matrix matrix) {
		synchronized (paths) {
			paths.transform(matrix);
		}

		synchronized (points) {
			points.transform(matrix);
		}

		synchronized (path) {
			path.transform(matrix);
		}
		return true;
	}

	@Override
	public void setDataOverlay(MapConfig config, OverlayData data) {
		super.setDataOverlay(config, data);
		synchronized (paths) {
			path = new Path();

			paths.init();
			points.init();

			paths.render(data, false);
			points.render(data);

			paths.render();

			algorithm = new Pathfinder(data.nodes);

			// Log.e(TAG, "scroll min: "+mapBound.min.xc+"-"+mapBound.min.yc);
			// Log.e(TAG, "scroll max: "+mapBound.max.xc+"-"+mapBound.max.yc);
		}
	}

	public PointRender getNearby(float x, float y) {
		if (points.getPoints().size() <= 0) {
			return null;
		}

		double minDis = Double.MAX_VALUE;
		PointRender result = points.getPoints().get(0);
		for (PointRender _point : points.getPoints()) {

			double newDis = _point.distance(x, y);
			if (newDis < minDis) {
				minDis = newDis;
				result = _point;
			}
		}
		return result;
	}

	public PointRender getNearby3nd(float x, float y) {
		if (points.getPoints().size() <= 0) {
			return null;
		}

		double minDis1 = Double.MAX_VALUE;
		double minDis2 = Double.MAX_VALUE;
		double minDis3 = Double.MAX_VALUE;
		PointRender result1 = points.getPoints().get(0);
		PointRender result2 = points.getPoints().get(0);
		PointRender result3 = points.getPoints().get(0);
		for (PointRender _point : points.getPoints()) {

			double newDis = _point.distance(x, y);
			if (newDis < minDis1 && newDis < minDis2 && newDis < minDis3) {
				minDis3 = minDis2;
				result3 = result2;
				
				minDis2 = minDis1;
				result2 = result1;

				minDis1 = newDis;
				result1 = _point;
			}
		}
		return result3;
	}
	
	public PointRender getNearby2nd(float x, float y) {
		if (points.getPoints().size() <= 0) {
			return null;
		}

		double minDis1 = Double.MAX_VALUE;
		double minDis2 = Double.MAX_VALUE;
		PointRender result1 = points.getPoints().get(0);
		PointRender result2 = points.getPoints().get(0);
		for (PointRender _point : points.getPoints()) {

			double newDis = _point.distance(x, y);
			if (newDis < minDis1 && newDis < minDis2) {
				
				minDis2 = minDis1;
				result2 = result1;

				minDis1 = newDis;
				result1 = _point;
			}
		}
		return result2;
	}

	public boolean back() {
		if(drawPath){
			drawPath = false;
			return true;
		}
		return false;
	}
}
