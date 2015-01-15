/*
 * TextTranslator Android App
 */
package com.texttranslator.core;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

import com.texttranslator.helper.ServerCom;

import android.util.Log;


/**
 * Translate text using MyMemory API
 * 
 * @author luiz
 * @see http://mymemory.translated.net/doc/spec.php
 */
public class TextTranslator {
	private static final String TAG = "TextCorrector";

	private final String URL_TRANSLATION =
			"http://api.mymemory.translated.net/get?q=%s&langpair=%s|%s";
	
	public static final int MAX_TRANS_LEN = 30; // Maximum string length submitted to translation
											   // (to prevent big text translations)
	
	private String langOri,
					langDes,
					textOriginal,
					textTranslated;
	
	private ServerCom serverCom;
	
	public TextTranslator() {
		serverCom = new ServerCom();
		setTranslationOption(1);
	}
	
	/**
	 * Set language option
	 * 
	 * @param option Option of translation
	 */
	public void setTranslationOption(int option) {
		switch(option) {
			case 1:
				langOri = ExtractText.LANG_SUPORTED[1];
				langDes = ExtractText.LANG_SUPORTED[0];
				break;
			case 2:
				langOri = ExtractText.LANG_SUPORTED[0];
				langDes = ExtractText.LANG_SUPORTED[1];
				break;
			default:
				langOri = "auto";
				langDes = "auto";
		}
		textTranslated = "";
		
		Log.i(TAG, "setTranslationOption: Translation redefined: " + langOri + " to " + langDes);
	}
	
	/**
	 * Call remote API to translate text and store result in controller
	 * 
	 * @param detectionController Controller to store result
	 * @return True, if success, or False, if not
	 */
	public boolean processTransText(ProcessResult detectionController) {
		ArrayList<ProcessStorage> detectionStorage =
				detectionController.getDetectionStorage();
		Iterator<ProcessStorage> it = detectionStorage.iterator();
		
		Log.i(TAG, "processTransText: Translation started");
		
		for (; it.hasNext();) {
			ProcessStorage detected = it.next();
			if (!processTransText(detected.getTextOriginal())) {
				Log.e(TAG, "processTransText: Translation fail. Ignoring text region.");
			}
			detected.setTextTranslated(this.textTranslated);
		}
		
		Log.i(TAG, "processTransText: Translation complete");
		
		return true;
	}
	
	/**
	 * Translate text based in language settings
	 * 
	 * @param text Text to translation
	 * @return True, if translation success, or False, if not
	 */
	public boolean processTransText(String text) {	
		if (translateText(text, langOri, langDes)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Translate text
	 * 
	 * @param text Text to translation
	 * @param langOri Language of origin in translation
	 * @param langDes Language of destine in translation
	 * @return True, if translation success, or False, if not
	 */
	public boolean translateText(String text, String langOri, String langDes) {
		if (text == null || text == null || text == null ||
				text.length() == 0 || text.length() > MAX_TRANS_LEN) {
			textOriginal = "";
			textTranslated = "";
			return true;
		}

		textOriginal = text;
		
		try {
			String url = String.format(URL_TRANSLATION,
					URLEncoder.encode(textOriginal, "UTF-8"),
					langOri,
					langDes);
			String response = serverCom.sendGETRequest(url);
			JSONObject jObjectResp = new JSONObject(response);
			int respStatus = jObjectResp.getInt("responseStatus");
			if (respStatus == 200) {
				JSONObject jObjectData = jObjectResp.getJSONObject("responseData");
				textOriginal = text;
				textTranslated = jObjectData.getString("translatedText");
			}

		} catch (Exception e) {
			Log.e(TAG, "translateText: Fail in translation API");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public String getDictOri() {
		return langOri;
	}

	public String getDictDes() {
		return langDes;
	}
}
