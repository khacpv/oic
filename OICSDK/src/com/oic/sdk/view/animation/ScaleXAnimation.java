package com.oic.sdk.view.animation;

public class ScaleXAnimation extends Animation {

	public ScaleXAnimation() {
		super(Type.ScaleX);
		mDuration = 1000;
		mStartTime = System.currentTimeMillis();
		mEndTime = System.currentTimeMillis() + mDuration;
		mStartValue = 1;
		mEndValue = 2;
	}
}
