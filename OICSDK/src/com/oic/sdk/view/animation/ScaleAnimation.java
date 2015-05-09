package com.oic.sdk.view.animation;

public class ScaleAnimation extends Animation {

	public ScaleAnimation() {
		super(Type.Scale);
		mDuration = 500;
		mStartTime = System.currentTimeMillis();
		mEndTime = System.currentTimeMillis() + mDuration;
		mStartValue = 1;
		mEndValue = 2;
	}
}
