package com.dy.Basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import net.sf.json.JSONObject;

public class CommonTool {
	/**
	 * 获取系统当前时间
	 */
	public static String Now(String str) {
		String tmp = "yyyy-MM-dd HH:mm:ss";
		if (!str.equals("")) {
			tmp = str;
		}
		Date time = new Date();
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat(tmp);
		String ctime = formatter.format(time);
		return ctime;
	}

	/**
	 * 通过key,读取conf.properties里面的信息
	 */
	public static String readConf(String key) {
		return readConf("local",key);
	}

	
	public static String readConf(String sec,String key){
		String v = "";
		try{
			v = ConfUtil.getStr(sec, key);
			if (v == null){
				v= "";
			}
		}catch(Exception e){
			v = "";
		}
		return v;
	}
	
	/**
	 * 初始化配置文件
	 */
	public static void initConf(String addr) throws IOException{
		ConfUtil.initConf(addr);
	}
	
	
	/**
	 * 读取文件
	 */
	public static String txt2String(File file) {

		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/**
	 * 生成数字随机数
	 * 
	 * @param len
	 *            通过len控制生成的随机数的长度
	 */
	public static String getRandom(int len) {
		Random random = new Random();
		String result = "";
		for (int i = 0; i < len; i++) {
			result += random.nextInt(10);
		}

		return result;
	}


}
