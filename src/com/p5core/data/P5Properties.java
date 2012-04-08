package com.p5core.data;

import java.io.IOException;
import java.util.Properties;

import processing.core.PApplet;

/**
 * simple convenience wrapper object for the standard
 * Properties class to return pre-typed data
 */
public class P5Properties extends Properties {
	protected PApplet p;
	public P5Properties(PApplet p) {
		super();
		try {
			load(p.createInput("run.properties"));
		} catch(IOException e) {
			p.println("couldn't read run.properties config file...");
		}
	}
 
	public String getString(String id, String defState) {
		return getProperty(id,defState);
	}
 
	public boolean getBoolean(String id, boolean defState) {
		return Boolean.parseBoolean(getProperty(id,""+defState));
	}
 
	public int getInt(String id, int defVal) {
		return Integer.parseInt(getProperty(id,""+defVal));
	}
 
	public float getFloat(String id, float defVal) {
		return new Float(getProperty(id,""+defVal)); 
  	}  
}