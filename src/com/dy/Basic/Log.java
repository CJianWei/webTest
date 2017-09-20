package com.dy.Basic;

import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class Log {
	private static Logger logger = null;
	private static int Level = 1;

	public static void initLog(String name) throws SecurityException, IOException {
		logger = Logger.getLogger("kuxiao");
		FileHandler fileHandler = new FileHandler(name);
		fileHandler.setFormatter(new LogFormatter());
		logger.addHandler(fileHandler);
	}

	/**
	 * 
	 * 初始化日志模块
	 */
	public static void initLog() throws SecurityException, IOException {
		initLog(CommonTool.Now("yyyy-MM-dd-HH-mm") + ".log");
	}

	/**
	 * 
	 * 设置日志级别
	 */
	public static void setLogLevel(int level) {
		Log.Level = level;
	}

	public static void I(String str) {
		logger.info(str);
	}


	public static void E(String str) {
		W(str);
	}
	
	public static void W(String str) {
		logger.warning(str);
	}
}

class LogFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		return "[" + CommonTool.Now("") + "]" + "[" + record.getLevel() + "] --> " + record.getMessage() + "\n";
	}

}