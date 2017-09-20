package com.dy.Basic;

import java.util.ArrayList;

public class EleChain {
	private ArrayList<Ele> arys;
	public EleChain(){
		arys = new ArrayList<Ele>();
	}
	
	public EleChain add(Ele e){
		this.arys.add(e);
		return this;
	}
	
	public ArrayList<Ele> getEles(){
		return this.arys;
	}
}


