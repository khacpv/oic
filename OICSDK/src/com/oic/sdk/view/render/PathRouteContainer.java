package com.oic.sdk.view.render;

import java.util.ArrayList;

import android.graphics.Path;
import android.graphics.PointF;

import com.oic.sdk.algorithm.Connector;
import com.oic.sdk.algorithm.Pathfinder;
import com.oic.sdk.data.Node;
import com.oic.sdk.data.OverlayData;
import com.oic.sdk.data.Way;
import com.oic.sdk.view.overlay.OverlayBase;

public class PathRouteContainer extends PathContainer {

	PointRender start;
	PointRender end;
	Path path;
	Pathfinder algorithm = null;
	PointContainer points;

	public PathRouteContainer(OverlayBase layer,PointContainer points) {
		super(layer);
		this.points = points;
		
		//initLink();
	}
	
	public void render(){
		OverlayData dataRoute = getLayer().getOverlayData();
		render(dataRoute);
	}
	
	public void render(OverlayData dataRoute){
//		ArrayList<Node> nodes = dataRoute.nodes;
		ArrayList<Way> ways = dataRoute.ways;
		for(int i = 0;i<ways.size();i++){
			Way way = ways.get(i);
			for(int j=0;j<way.nd.size()-1;j++){
				String nodeId1 = way.nd.get(j);
				String nodeId2 = way.nd.get(j+1);
				Node node1 = dataRoute.getNode(nodeId1);
				Node node2 = dataRoute.getNode(nodeId2);
				node1.links.add(new Connector(node2, node1.dist(node2)));
				node2.links.add(new Connector(node1, node1.dist(node2)));
			}
		}
	}
	
	public Path calculate(PointF pStart,PointF pEnd,Node start,Node end){
		PointF _start = pStart;
		PointF _end = pEnd;
		//render();
		if(null == algorithm){
			algorithm = new Pathfinder(getLayer().getOverlayData().nodes);
		}
		
		algorithm.dijkstra(start);
		ArrayList<Node> routeNodes = algorithm.dijkstra(start, end);
		path = new Path();
		String nodeId;
		synchronized (path) {
			path.moveTo(pStart.x, pStart.y);
			for(int i=0;i<routeNodes.size();i++){
				nodeId = routeNodes.get(i).id;
				for(PointRender point: points.getPoints()){
					if(point.getNode().id.equals(nodeId)){
						if(i==0){
							if(_start == null){
								_start = new PointF(point.getNodePoint().xc,point.getNodePoint().yc);
							}
							path.moveTo(_start.x, _start.y);
							path.lineTo(point.getNodePoint().xc, point.getNodePoint().yc);
							continue;
						}
						
						
						if(i == routeNodes.size()-1 && _end != null){
							path.lineTo(point.getNodePoint().xc, point.getNodePoint().yc);
							path.lineTo(_end.x, _end.y);
							continue;
						}
						
						path.lineTo(point.getNodePoint().xc, point.getNodePoint().yc);
					}
				}
			}
			
		}
		return path;
	}

	public Path calculate(Node start, Node end){
		return calculate(null, null,start,end);
	}

	public Path getRoute() {
		return path;
	}
}
