package com.dy.Basic;

import java.util.ArrayList;


public class StringChain {
	private ArrayList<String> sc = null;
	public StringChain(){
		sc = new ArrayList<String>();
	}
	
	public StringChain add(String str){
		sc.add(str);
		return this;
	}
	
	public ArrayList<String> getSC(){
		return this.sc;
	}
}
