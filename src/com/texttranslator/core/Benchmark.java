/*
 * TextTranslator Android App
 */
package com.texttranslator.core;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.texttranslator.MainActivity;

/**
 * Benchmark to save execution data
 * 
 * @author luiz
 */
public class Benchmark {
	private static final String TAG = "Benchmark";
	
	public static final String FILE_PATH_SCAN = MainActivity.APP_PATH + "benchmark/";

	private File folderFile;
	
	private ExecutionLog logger;	// Logger to collect statistic data of execution
	
	public Benchmark() {
		folderFile = new File(FILE_PATH_SCAN);
		logger = new ExecutionLog();
	}
	
	/**
	 * Scan folder of images to benchmark
	 * 
	 * @param detectText Text detector
	 * @param extractText Text extractor
	 * @param textTranslator Text Translator
	 * @param detectionResult Storage of detection
	 */
	public void scanFolder(DetectText detectText,
			ExtractText extractText,
			TextTranslator textTranslator,
			ProcessResult detectionResult) {
		Bitmap imageToDetection;
		
		Log.i(TAG, "scanFolder: Scaning...");
		
		for(File file : folderFile.listFiles()){
			logger.clearLog();
			detectionResult.prepare();
			imageToDetection = BitmapFactory.decodeFile(file.getAbsolutePath());
			
			if (imageToDetection != null) {
				logger.appendLog("Detecting text...", true);
				detectText.processDetectTextImage(imageToDetection, detectionResult);
				logger.appendLog("Done.", false);
	        	logger.appendLog("Extracting text...", true);
	        	extractText.processOcr(textTranslator.getDictOri(), detectionResult);
	        	logger.appendLog("Done.", false);
	        	logger.appendLog("Translating text...", true);
	        	textTranslator.processTransText(detectionResult);
	        	logger.appendLog("Done.", false);
	        	logger.appendLog("Original Text: " + detectionResult.getRecognizedText(), true);
	        	logger.appendLog("Translated Text: " + detectionResult.getTranslatedText(), true);
	        	
	        	// Create images
	        	
	        	detectionResult.createTextRegionsImageBitmap();
	        	detectionResult.createOcrImageBitmap();
	        	detectionResult.createTranslatedImageBitmap();
	        	
	        	logger.appendLog("Done.", false);
	    		logger.saveLogFile(detectionResult.getCurrentDirFolder());
			}
        }
		
		Log.i(TAG, "scanFolder: Done.");
	}
}
