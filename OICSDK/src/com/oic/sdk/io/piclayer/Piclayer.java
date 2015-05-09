package com.oic.sdk.io.piclayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;

import com.oic.sdk.config.OicConfig;
import com.oic.sdk.io.IoUtil;

/**
 * #JOSM PicLayer plugin calibration data #Sun Nov 27 22:21:58 CET 2011
 * POSITION_Y=6708383.375731533 
 * POSITION_X=602463.1049788792
 * INITIAL_SCALE=673.3630407396365 
 * M12=-714.691403694874 
 * M11=0.508636037014039
 * M10=6.263463862108147E-5 
 * M02=43.42632371753174 
 * M01=-2.1720970442278513E-4
 * M00=0.5060984647587284
 * */
public class Piclayer {
	private static final String POSITION_Y = "POSITION_Y=";
	private static final String POSITION_X = "POSITION_X=";
	private static final String INITIAL_SCALE = "INITIAL_SCALE=";
	private static final String M12 = "M12=";
	private static final String M11 = "M11=";
	private static final String M10 = "M10=";
	private static final String M02 = "M02=";
	private static final String M01 = "M01=";
	private static final String M00 = "M00=";
	private static final String TNX_X = "TNX_X=";
	private static final String TNX_Y = "TNX_Y=";
	private static final String AREA = "AREA=";
	private static final String WIDTH = "WIDTH=";
	private static final String HEIGHT = "HEIGHT=";
	private static final String MIN_LAT = "MIN_LAT=";
	private static final String MIN_LON = "MIN_LON=";
	private static final String MAX_LAT = "MAX_LAT=";
	private static final String MAX_LON = "MAX_LON=";

	public double positionX;
	public double positionY;
	public double initialScale;
	public double m00, m01, m02, m10, m11, m12;
	
	public float tranX = 0;
	public float tranY = 0;
	
	public float area = 1;
	public float width = 0;
	public float height = 0;
	
	public float minLat = 0;
	public float minLon = 0;
	public float maxLat = 0;
	public float maxLon = 0;

	@Override
	public String toString() {
		return "Piclayer [positionX=" + positionX + ", positionY=" + positionY
				+ ", initialScale=" + initialScale + ", m00=" + m00 + ", m01="
				+ m01 + ", m02=" + m02 + ", m10=" + m10 + ", m11=" + m11
				+ ", m12=" + m12 + "]";
	}
	
	public void parseFromAssets(Context ctx,String fileName){
		parse(ctx,IoUtil.getInputStream(ctx, IoUtil.TYPE_ASSETS, fileName));
	}
	
	public void parseFromStorage(Context ctx,String fileName){
		parse(ctx,IoUtil.getInputStream(ctx, IoUtil.TYPE_STORAGE_EXT, fileName));
	}

	public void parse(Context context, InputStreamReader input) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(input);

			// do reading, usually loop until end of file reading
			String mLine = reader.readLine();
			while (mLine != null) {
				// process line
				if(mLine.contains(POSITION_Y)){
					positionY = Double.parseDouble(mLine.substring(POSITION_Y.length()));
				}
				else if(mLine.contains(POSITION_X)){
					positionX = Double.parseDouble(mLine.substring(POSITION_X.length()));
				}
				else if(mLine.contains(INITIAL_SCALE)){
					initialScale = Double.parseDouble(mLine.substring(INITIAL_SCALE.length()));
				}
				else if(mLine.contains(M12)){
					m12 = Double.parseDouble(mLine.substring(M12.length()));
				}
				else if(mLine.contains(M11)){
					m11 = Double.parseDouble(mLine.substring(M11.length()));
				}
				else if(mLine.contains(M10)){
					m10 = Double.parseDouble(mLine.substring(M10.length()));
				}
				else if(mLine.contains(M02)){
					m02 = Double.parseDouble(mLine.substring(M02.length()));
				}
				else if(mLine.contains(M01)){
					m01 = Double.parseDouble(mLine.substring(M01.length()));
				}
				else if(mLine.contains(M00)){
					m00 = Double.parseDouble(mLine.substring(M00.length()));
				}
				else if(mLine.contains(TNX_X)){
					tranX = Float.parseFloat(mLine.substring(TNX_X.length()));
				}
				else if(mLine.contains(TNX_Y)){
					tranY = Float.parseFloat(mLine.substring(TNX_Y.length()));
				}
				else if(mLine.contains(AREA)){
					area = Float.parseFloat(mLine.substring(AREA.length()));
				}
				else if(mLine.contains(WIDTH)){
					width = Float.parseFloat(mLine.substring(WIDTH.length()));
				}
				else if(mLine.contains(HEIGHT)){
					height = Float.parseFloat(mLine.substring(HEIGHT.length()));
				}
				else if(mLine.contains(MIN_LAT)){
					minLat = Float.parseFloat(mLine.substring(MIN_LAT.length()));
				}
				else if(mLine.contains(MIN_LON)){
					minLon = Float.parseFloat(mLine.substring(MIN_LON.length()));
				}
				else if(mLine.contains(MAX_LAT)){
					maxLat = Float.parseFloat(mLine.substring(MAX_LAT.length()));
				}
				else if(mLine.contains(MAX_LON)){
					maxLon = Float.parseFloat(mLine.substring(MAX_LON.length()));
				}
				mLine = reader.readLine();
			}
		} catch (IOException e) {
			if(OicConfig.DEBUG){
				e.printStackTrace();
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					if(OicConfig.DEBUG){
						e.printStackTrace();
					}
				}
			}
		}
	}
}
