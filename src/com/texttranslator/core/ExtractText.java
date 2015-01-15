/*
 * TextTranslator Android App
 */
package com.texttranslator.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.texttranslator.R;
import com.texttranslator.MainActivity;
import com.texttranslator.helper.Tools;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Recognize text from image using Tesseract OCR Engine
 * 
 * @author luiz
 */
public class ExtractText {
	private static final String TAG = "ExtractText";
	
	public static final String LANG_SUPORTED[] = {"en", "pt"}; // Languages supported
		
	private TessBaseAPI tessBaseApi; // Tesseract engine
	
	private File tessLangPath, // Tesseract language path
				 tessLangFile; // Tesseract language file
	
	/**
	 * Create text extract
	 * 
	 * @param res Resources from android to read files stored in raw folder
	 */
	public ExtractText(Resources res) {
		initOcr(res);
	}
	
	/**
	 * Process OCR to find text in image 
	 * 
	 * @param lang Language of origin (translate from)
	 * @param detectionResult Controller to store results
	 * @return True, if operation success, or False, if not
	 */
	public boolean processOcr(String lang, ProcessResult detectionResult) {
		ArrayList<ProcessStorage> detectionStorage = detectionResult.getDetectionStorage();
		Iterator<ProcessStorage> it = detectionStorage.iterator();
		
		Log.i(TAG, "processOcr: OCR started");
		
		while (it.hasNext()) {
			ProcessStorage detected = it.next();
			if (detected.getResultTextBWImage() == null) {
				Log.e(TAG, "processOcr: Image to OCR is null. Ignoring...");
				continue;
			}
			String text = processOcr(detected.getResultTextBWImage(), lang);
			detected.setTextOriginal(text);
		}
		
		Log.i(TAG, "processOcr: OCR complete");
		
		return true;
	}
	
	/**
	 * Process OCR to find text in image (use only bitmap)
	 * 
	 * @param lang Language of origin (translate from)
	 * @param detectionResult Controller to store results
	 * @return  True, if operation success, or False, if not
	 */
	public String processOcr(Bitmap imageToOcr, String lang) {
		String result;
		
		prepareOcr(lang.split("_")[0]);
		tessBaseApi.setImage(imageToOcr);
		result = tessBaseApi.getUTF8Text();
		tessBaseApi.end();
		
		return result;
	}
	
	// Lang install and configuration
	
	/**
	 * Create directories and install languages supported
	 * 
	 * @param res Resources from android to read files stored in raw folder
	 */
	public void initOcr(Resources res) {
		tessLangPath = new File(MainActivity.APP_PATH + "tessdata/");
		tessLangPath.mkdirs();
		installLanguageData(res);
	}
	
	/**
	 * Prepare OCR to language (before each query)
	 * 
	 * @param lang Language origin of text
	 */
	private void prepareOcr(String lang) {
		tessBaseApi = new TessBaseAPI();
		tessBaseApi.init(MainActivity.APP_PATH, lang + "data");
	}
	
	/**
	 * Install all languages supported
	 * 
	 * @param res Resources from android to read files stored in raw folder
	 */
	private void installLanguageData(Resources res) {
		for (String curLang : LANG_SUPORTED) {
			installLanguageData(res, curLang);
		}
	}
	
	/**
	 * Install language file in external storage
	 * 
	 * @param res Resources from android to read files stored in raw folder
	 * @param lang Language to install
	 */
	public boolean installLanguageData(Resources res, String lang) {
		InputStream in;
		tessLangFile = Tools.createFile(tessLangPath.getAbsolutePath(),
				lang + "data" + ".traineddata");
		
		Log.i(TAG, "installLanguageData: installing " + tessLangFile.getAbsolutePath());
		
		if(tessLangFile.exists() && tessLangFile.length() != 0) {
			Log.i(TAG, "installLanguageData: File already installed!");
			return true;
		}
		
		if (lang == LANG_SUPORTED[0]) {
			in = res.openRawResource(R.raw.tessendata);
		} else {
			in = res.openRawResource(R.raw.tessptdata);
		}
 		
	    FileOutputStream out;
	    byte[] buff = new byte[1024];
	    int read = 0;
	    
		try {
			out = new FileOutputStream(tessLangFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

	    try {
	    	while ((read = in.read(buff)) > 0) {
	    		out.write(buff, 0, read);
	    	}
	    	in.close();
	    	out.close();
	       
	    	Log.i(TAG, "installLanguageData: Language " + lang +
	    		   " data installed in " + tessLangFile.getAbsolutePath());
	    } catch (IOException e) {
			e.printStackTrace();
			
			Log.e(TAG, "installLanguageData: Language " + lang +
					" installation fail");
			
			return false;
		}
		
		return true;
	}
}
