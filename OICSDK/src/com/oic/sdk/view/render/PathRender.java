package com.oic.sdk.view.render;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.oic.sdk.data.Way;

public class PathRender {
	private Path path;
	private Paint paint;
	private Way way;
	
	public PathRender(Path path,Paint paint) {
		this.paint = paint;
		this.path = path;
	}
	
	public Path getPath() {
		return path;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	public Paint getPaint() {
		return paint;
	}
	
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public void transform(Matrix matrix){
		path.transform(matrix);
	}
	
	public void offset(float deltaX,float deltaY){
		path.offset(deltaX, deltaY);
	}

	public Way getWay() {
		return way;
	}

	public void setWay(Way way) {
		this.way = way;
	}
	
	
}
