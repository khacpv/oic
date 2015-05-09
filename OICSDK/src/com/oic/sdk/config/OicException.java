package com.oic.sdk.config;

@SuppressWarnings("serial")
public class OicException extends Exception{
	public OicException(String message){
		super(message);
	}
	
	public OicException(Throwable throwAble){
		super(throwAble);
	}
	
	public OicException(String message,Throwable throwAble){
		super(message,throwAble);
	}
}
