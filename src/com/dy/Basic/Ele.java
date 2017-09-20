package com.dy.Basic;

public class Ele {
	private String type;
	private String locatorStr;
	private int index;

	public Ele(String type, String locatorStr, int index) {
		this.type = type;
		this.locatorStr = locatorStr;
		this.index = index;
	}
	
	public Ele(String locatorStr){
		this("css",locatorStr,0);
	}
	
	public Ele(String type, String locatorStr) {
		this(type,locatorStr,0);
	}
	
	public Ele( String locatorStr, int index) {
		this("css",locatorStr,index);
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getLocatorStr(){
		return this.locatorStr;
	}
	
	public int getIndex(){
		return this.index;
	}
}

