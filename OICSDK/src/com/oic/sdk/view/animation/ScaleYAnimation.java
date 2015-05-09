package com.oic.sdk.view.animation;

public class ScaleYAnimation extends Animation {

	public ScaleYAnimation() {
		super(Type.ScaleY);
		mDuration = 500;
		mStartTime = System.currentTimeMillis();
		mEndTime = System.currentTimeMillis() + mDuration;
		mStartValue = 1;
		mEndValue = 2;
	}
}
