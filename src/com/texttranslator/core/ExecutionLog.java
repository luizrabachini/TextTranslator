/*
 * TextTranslator Android App
 */
package com.texttranslator.core;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log of operations
 * 
 * @author luiz
 */
@SuppressLint("SimpleDateFormat")
public class ExecutionLog {
	private static final String LOG_FILE_NAME = "execution_log.txt";

	private String logContent;
	
	private long startTime;
	
	public ExecutionLog() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		
		startTime = System.currentTimeMillis();
		logContent = "";
		String logEntry = String.format(
				"\n---------- Init ----------\n" +
				"- %s" +
				"\n--------------------------\n\n",
				dateFormat.format(date));
		
		logContent += logEntry;
	}
	
	/**
	 * Add new entry in message log
	 * 
	 * @param message Message to entry
	 * @param modeInitial Flag to reset counter time
	 */
	@SuppressLint("DefaultLocale")
	public void appendLog(String message, boolean modeInitial) {
		String logEntry = "";
		
		if (modeInitial) {
			startTime = System.currentTimeMillis();
			logEntry = String.format("- %s\n", message);
		} else {
			long timeDif = System.currentTimeMillis() - startTime;
			logEntry = String.format("[%d]: %s\n", timeDif, message);
		}
		
		logContent += logEntry;
	}
	
	/**
	 * Save execution log in persistent file
	 * 
	 * @param path Path directory to save file
	 */
	public void saveLogFile(String path) {
		try {
			File logFile = new File(path + LOG_FILE_NAME);
			FileOutputStream fop = new FileOutputStream(logFile);
			
			if (!logFile.exists()) {
				logFile.createNewFile();
			}

			byte[] contentInBytes = logContent.getBytes();
			
			fop.write(contentInBytes);			
			fop.flush();
			fop.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clear log to new execution
	 */
	public void clearLog() {
		logContent = "";
	}
}
