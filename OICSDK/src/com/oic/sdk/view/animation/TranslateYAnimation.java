package com.oic.sdk.view.animation;

public class TranslateYAnimation extends Animation {

	public TranslateYAnimation() {
		super(Type.TranslateY);
		mDuration = 1000;
		mStartTime = System.currentTimeMillis();
		mEndTime = System.currentTimeMillis() + mDuration;
		mStartValue = 0;
	}
}
