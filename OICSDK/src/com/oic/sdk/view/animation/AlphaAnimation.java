package com.oic.sdk.view.animation;

public class AlphaAnimation extends Animation {

	public AlphaAnimation() {
		super(Type.Alpha);
		mDuration = 500;
		mStartTime = System.currentTimeMillis();
		mEndTime = System.currentTimeMillis() + mDuration;
		mStartValue = 0;
		mEndValue = 255;
	}
}
