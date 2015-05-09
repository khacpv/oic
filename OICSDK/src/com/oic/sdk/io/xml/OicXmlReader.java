package com.oic.sdk.io.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

import com.oic.sdk.data.OverlayData;

public class OicXmlReader {
	private static OicXmlReader _INSTANCE = null;

	private InputStream xmlInputStream = null;
	public OicHandler osmHandler;
	public OicErrorHandler osmErrorHandler;
	public String IOError = "";
	public String SAXError = "";
	public String GeneralError = "";
	public Context context;

	public OicXmlReader(Context context) {
		this.context = context;
		// instantiate our handler
		osmHandler = new OicHandler();
		// instantiate our error handler
		osmErrorHandler = new OicErrorHandler();
	}

	public static OicXmlReader getInstance(Context context) {
		if (_INSTANCE == null) {
			_INSTANCE = new OicXmlReader(context);
		}
		return _INSTANCE;
	}
	
	public boolean parseStorage(String xmlfile) throws ParserConfigurationException {
		try {
			osmHandler.isLoaded = false;
			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();
			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();
			// assign our handler
			xmlreader.setContentHandler(osmHandler);
			// assign our ErrorHandler
			xmlreader.setErrorHandler(osmErrorHandler);

			xmlInputStream = new FileInputStream(xmlfile);

			xmlreader.parse(new InputSource(xmlInputStream));
			osmHandler.isLoaded = true;
			return true;
		} catch (SAXException e) {
			SAXError = e.getMessage();
		} catch (IOException e) {
			IOError = e.getMessage();
		} catch (Exception e) {
			GeneralError = e.getMessage();
		}

		return false;
	}

	public boolean parse(String xmlfile) throws ParserConfigurationException {
		try {
			osmHandler.isLoaded = false;
			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();
			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();
			// assign our handler
			xmlreader.setContentHandler(osmHandler);
			// assign our ErrorHandler
			xmlreader.setErrorHandler(osmErrorHandler);

			xmlInputStream = context.getResources().getAssets().open(xmlfile);

			xmlreader.parse(new InputSource(xmlInputStream));
			osmHandler.isLoaded = true;
			return true;
		} catch (SAXException e) {
			SAXError = e.getMessage();
		} catch (IOException e) {
			IOError = e.getMessage();
		} catch (Exception e) {
			GeneralError = e.getMessage();
		}

		return false;
	}
	
	public OverlayData getOicMapData(){
		if(osmHandler.isLoaded){
			return osmHandler.oicMapData;
		}
		return new OverlayData();
	}
}
