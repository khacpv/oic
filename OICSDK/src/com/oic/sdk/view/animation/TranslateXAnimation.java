package com.oic.sdk.view.animation;

public class TranslateXAnimation extends Animation {

	public TranslateXAnimation() {
		super(Type.TranslateX);
		mDuration = 1000;
		mStartTime = System.currentTimeMillis();
		mEndTime = System.currentTimeMillis() + mDuration;
		mStartValue = 0;
	}
}
