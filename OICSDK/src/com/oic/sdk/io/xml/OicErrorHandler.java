package com.oic.sdk.io.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class OicErrorHandler implements ErrorHandler{
    
	
	
	public Exceptions exceptions = new Exceptions();
    
	@Override
	public void error(SAXParseException exception) throws SAXException {
		exceptions.Error = exception.getMessage();
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		exceptions.FatalError = exception.getMessage();
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		exceptions.Warning = exception.getMessage();
	}

	class Exceptions {
	   public String Error = "";
	   public String FatalError = "";
	   public String Warning = "";
	}
}
