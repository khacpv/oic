package com.oic.sdk.view.render;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;

import com.oic.sdk.abs.AbsMapView;
import com.oic.sdk.data.Node;
import com.oic.sdk.view.OicMapView;
import com.oic.sdk.view.util.NodePoint;
import com.oic.sdk.view.util.OicColor;
import com.oic.sdk.view.util.OicResource;

public class PointRender {
	NodePoint nodePoint;
	Paint paint;
	TextPaint textPaint;
	float radius = 10;
	Node node;
	
	TextPaint annotTextPaint;
	BitmapDrawable locationAnnotation;
	Rect annotRect = new Rect();
	int annotationW = 32;	// dp
	int annotationH = 32;	// dp
	
	int iconDrawableW = 25;
	int iconDrawableH = 25;
	int iconDrawableMarginVer = 10;
	int iconDrawableMarginHor = 15;
	
	Rect iconRectRight;
	Rect iconRectLeft;
	
	Rect rect;
	
	BitmapDrawable iconRight;
	BitmapDrawable iconLeft;
	
	private boolean isVisible = false;
	private boolean isAnnotationVisible = false;
	
	private int iconW = 20;	// dp
	private int iconH = 22;	// dp
	
	private int textSize = 14;	// dp
	private int textStrokeWidth = 2;
	
	Rect rectSquare = new Rect();
	Rect doubleRectSquare = new Rect();
	Paint textNodePaint;
	StringBuilder nodeName;
	
	BitmapDrawable bmpIcon = null;
	float textWidthHalf;
	
	public PointRender(Context context,NodePoint nodePoint, Paint paint) {
		this.nodePoint = nodePoint;
		this.textPaint = new TextPaint(paint);
		this.annotTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		this.paint = paint;
		
		this.paint.setAntiAlias(true);
		this.paint.setStyle(Style.FILL);
		this.paint.setColor(Color.RED);
		
		textPaint.setTextSize(OicResource.dipToPixels(context, textSize));
		textPaint.setStrokeWidth(OicResource.dipToPixels(context, 5));
		textPaint.setStyle(Style.STROKE);
		textPaint.setColor(Color.WHITE);
		textPaint.setStrokeJoin(Join.ROUND);
		textPaint.setStrokeCap(Cap.ROUND);
		textPaint.setAntiAlias(true);
		
		annotTextPaint.setTextSize(OicResource.dipToPixels(context, textSize));
		annotTextPaint.setStrokeWidth(OicResource.dipToPixels(context, 5));
		annotTextPaint.setStyle(Style.STROKE);
		annotTextPaint.setColor(Color.WHITE);
		annotTextPaint.setStrokeJoin(Join.ROUND);
		annotTextPaint.setStrokeCap(Cap.ROUND);
		
		textNodePaint = this.getTextPaint();
		textNodePaint.setTextSize(OicResource.dipToPixels(context, textSize));
		textNodePaint.setStrokeWidth(textStrokeWidth);
		textNodePaint.setStrokeJoin(Join.ROUND);
		textNodePaint.setStrokeCap(Cap.ROUND);
		
		iconW = (int)OicResource.dipToPixels(context, iconW);
		iconH = (int)OicResource.dipToPixels(context, iconH);
		
		locationAnnotation = OicResource.getInstance(context).getIcon("markpoint.png");
		locationAnnotation.setAntiAlias(true);
		annotationW = (int)OicResource.dipToPixels(context, annotationW);
		annotationH = (int)OicResource.dipToPixels(context, annotationH);
		
		iconRight = OicResource.getInstance(context).getIcon("route.png");
		iconLeft = OicResource.getInstance(context).getIcon("logo.png");
		
		iconDrawableW = (int)OicResource.dipToPixels(context, iconDrawableW);
		iconDrawableH = (int)OicResource.dipToPixels(context, iconDrawableH);
		iconDrawableMarginHor = (int)OicResource.dipToPixels(context, iconDrawableMarginHor);
		iconDrawableMarginVer = (int)OicResource.dipToPixels(context, iconDrawableMarginVer);
		
		iconRectRight = new Rect();
		iconRectLeft = new Rect();
		
		rect = new Rect();
		
		
	}
	
	public NodePoint getNodePoint() {
		return nodePoint;
	}
	
	public void setNodePoint(NodePoint nodePoint) {
		this.nodePoint = nodePoint;
	}
	
	public Paint getPaint() {
		return paint;
	}
	
	public void setAnnotationVisible(boolean isVisible){
		this.isAnnotationVisible = isVisible;
	}
	
	public boolean isAnnotationVisible(){
		return this.isAnnotationVisible;
	}
	
	public Rect getBound(){
		int cx = (int)getNodePoint().xc;
		int cy = (int)getNodePoint().yc;
		rect.left = cx-(iconW>>1);
		rect.top = cy-(iconH>>1);
		rect.right = cx+(iconW>>1);
		rect.bottom = cy+(iconH>>1);
		return rect;
	}
	
	public Rect getSquareBound(){
		int cx = (int)getNodePoint().xc;
		int cy = (int)getNodePoint().yc;
		rectSquare.left = (cx-(iconW));
		rectSquare.top = (cy-(iconH));
		rectSquare.right = (cx+(iconW));
		rectSquare.bottom = (cy+(iconH));
		return rectSquare;
	}
	
	public Rect getDoubleSquareBound(){
		int cx = (int)getNodePoint().xc;
		int cy = (int)getNodePoint().yc;
		doubleRectSquare.left = (cx-(iconW*2));
		doubleRectSquare.top = (cy-(iconH*2));
		doubleRectSquare.right = (cx+(iconW*2));
		doubleRectSquare.bottom = (cy+(iconH*2));
		return doubleRectSquare;
	}
	
	public Rect getDetailBound(){
		return annotRect;
	}
	
	public TextPaint getTextPaint(){
		return textPaint;
	}
	
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	public void transform(Matrix matrix){
		nodePoint.transform(matrix);
	}
	
	public void offset(float deltaX,float deltaY){
		nodePoint.offset(deltaX, deltaY);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
		// draw text
		nodeName = new StringBuilder(getNode().toString());
		if(nodeName.length() == 0){
			nodeName = new StringBuilder(getNode().getIconShortName());
		}
		textWidthHalf = textNodePaint.measureText(nodeName.toString())/2;
	}
	public void onDrawNode(Context context,Canvas canvas, AbsMapView mapView, OicResource resource){
		if(getNode().hasIcon() && !isAnnotationVisible){
			// draw icon
			if(bmpIcon==null){
				bmpIcon = OicResource.getInstance(context).getIcon(getNode().getIconName());
			}else{
				bmpIcon.setBounds(this.getBound());
				bmpIcon.draw(canvas);
			}
		}
		
//		onDrawText(canvas);
	}
	
	public void onDrawText(Canvas canvas){
		if(getNode().hasText() && !isAnnotationVisible){
			// draw text node
			textNodePaint.setStyle(Style.STROKE);
			textNodePaint.setColor(OicColor.getTextStrokeColor(getNode().getLocationColor()));
			canvas.drawText(nodeName.toString(), 
					this.getNodePoint().xc-textWidthHalf, 
					this.getBound().top,
					textNodePaint);
			
			textNodePaint.setStyle(Style.FILL);
			textNodePaint.setColor(OicColor.getTextColor(getNode().getLocationColor()));
			
			canvas.drawText(nodeName.toString(), 
					this.getNodePoint().xc-textWidthHalf, 
					this.getBound().top,
					textNodePaint);
		}
	}
	
	public void onDrawAnnotation(Context context,Canvas canvas, OicResource resource){
		// draw annotation
		if(isAnnotationVisible || OicMapView.MODE_DEV){
			// draw background
			annotRect = new Rect(getBound());
			
			annotRect.left -= annotationW>>1;
			annotRect.left += getBound().width()>>1;
			annotRect.top = getBound().top;
			
			annotRect.right = annotRect.left + annotationW;
			annotRect.bottom = annotRect.top + annotationH;
			locationAnnotation.setBounds(annotRect);
			locationAnnotation.draw(canvas);
			
			annotTextPaint.setStyle(Style.STROKE);
			annotTextPaint.setColor(Color.WHITE);
			
			canvas.drawText(nodeName.toString(), 
					annotRect.centerX()-textWidthHalf, 
					annotRect.centerY()-annotationH/2,
					annotTextPaint);
			
			annotTextPaint.setStyle(Style.FILL);
			annotTextPaint.setColor(Color.RED);
			
			canvas.drawText(nodeName.toString(), 
					annotRect.centerX()-textWidthHalf, 
					annotRect.centerY()-annotationH/2,
					annotTextPaint);
			
//			// draw icon right
//			iconRectRight.left = annotRect.right-iconDrawableW-iconDrawableMarginHor; 
//			iconRectRight.top = annotRect.top+iconDrawableMarginVer;
//			iconRectRight.right = iconRectRight.left+iconDrawableW;
//			iconRectRight.bottom = iconRectRight.top+iconDrawableH;
//			iconRight.setBounds(iconRectRight);
//			iconRight.draw(canvas);
			
//			// draw icon left
//			iconRectLeft.left = annotRect.left+iconDrawableMarginHor;
//			iconRectLeft.top = annotRect.top+iconDrawableMarginVer;
//			iconRectLeft.right = iconRectLeft.left+iconDrawableW;
//			iconRectLeft.bottom = iconRectLeft.top+iconDrawableH;
//			
//			iconLeft.setBounds(iconRectLeft);
//			iconLeft.draw(canvas);
		}
	}
	
	public static boolean isCollection(PointRender p1, PointRender p2){
		return p1.getSquareBound().intersect(p2.getSquareBound());
	}
	
	public static boolean isCollectionDouble(PointRender p1, PointRender p2){
		return p1.getDoubleSquareBound().intersect(p2.getDoubleSquareBound());
	}
	
	public boolean isInside(int x,int y){
		return getSquareBound().contains(x, y);
	}
	
	public boolean isLeftBtnInside(int x,int y){
		return isAnnotationVisible && iconRectRight.contains(x, y);
	}
	
	public boolean isRightBtnInside(int x,int y){
		return isAnnotationVisible && iconRectLeft.contains(x, y);
	}
	
	public boolean isDetailInside(int x,int y){
		return isAnnotationVisible && getDetailBound().contains(x,y);
	}
	
	public static boolean isCollection(PointRender p, ArrayList<PointRender> list, ArrayList<PointRender> drawedIds){
		for(PointRender _p: drawedIds){
			if(PointRender.isCollectionDouble(p, _p)){
				return true;
			}
		}
		return false;
	}
	
	public double distance(float x, float y){
		return Math.pow(nodePoint.xc-x, 2)+Math.pow(nodePoint.yc - y, 2);
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}
