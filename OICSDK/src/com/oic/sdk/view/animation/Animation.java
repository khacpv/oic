package com.oic.sdk.view.animation;

/**
 * Animation class.
 */
public abstract class Animation {
	public float mStartValue = 0;
	public float mEndValue = 0;
	public long mDuration = 500;
	public long mStartTime = System.currentTimeMillis();
	public long mEndTime = System.currentTimeMillis() + mDuration;
	public boolean mFinish = false;
	public Type mType;

	public enum Type {
		TranslateX, TranslateY, Alpha, Scale, ScaleX, ScaleY, Rotate,
	};

	public Animation(Type type) {
		mType = type;
	}

	public boolean isStarted() {
		if (System.currentTimeMillis() >= mStartTime) {
			return true;
		}
		return false;
	}

	public boolean isEnded() {
		if (System.currentTimeMillis() >= mEndTime) {
			return true;
		}
		return false;
	}

	public float getCurrentValue(long currentTime) {
		float currentValue = mStartValue + (currentTime)
				* (mEndValue - mStartValue) / mDuration;
		if (currentTime > mEndTime) {
			currentValue = mEndValue;
			mFinish = true;
		}
		return currentValue;
	}
}
