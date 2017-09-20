package com.dy.Basic;

import java.util.ArrayList;


public class Path {
	
	public static String Dir = System.getProperty("user.dir");
	public static String loadPath(String pre,String platform,StringChain sc){
		if (pre.equals("") || pre == null || pre.equals(".")){
			pre= Dir;
		}
		String sp;
		switch (platform){
		case "window":
			sp = "\\";
			break;
		default:
			sp = "/";	
		}
		
		ArrayList<String> arrays = sc.getSC();
		for (int i = 0;i<arrays.size();i++){
			pre = pre + sp + arrays.get(i);
		}
		return pre;
	}
	
	public static String loadPathClient(StringChain sc){
		return loadPath(CommonTool.readConf("client_workspace"),CommonTool.readConf("client_platform"),sc);
	}
	
	public static String loadPathRemote(StringChain sc){
		return loadPath(CommonTool.readConf("dst_workspace"),CommonTool.readConf("dst_platform"),sc);
	}
}
