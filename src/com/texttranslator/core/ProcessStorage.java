/*
 * TextTranslator Android App
 */
package com.texttranslator.core;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

/**
 * Storage to process results
 * 
 * @author luiz
 */
public class ProcessStorage {
//	private static final String TAG = "DetectionStorage";

	private String textOriginal, // Text original created by OCR Tesseract
		   			textTranslated; // Text translated by remote API
	
	private Bitmap resultTextImageOriginal, // Text image cropped from original image
					resultTextImageBW; // Text image cropped from bw image
	
	int resultAreaX, // Px of text box in original image (captured by camera)
		resultAreaY, // Py of text box in original image (captured by camera)
		resultAreaW, // Width of text box in original image (captured by camera)
		resultAreaH; // Height of text box in original image (captured by camera)
	
	/**
	 * Constructor
	 * 
	 * @param textOriginal Text OCR
	 * @param textTranslated Text translated
	 * @param resultTextImage Text cropped
	 * @param resultAreaX Px of box text
	 * @param resultAreaY Py of box text
	 * @param resultAreaW Width of box text
	 * @param resultAreaH Height of box text
	 */
	public ProcessStorage(String textOriginal,
			String textTranslated,
			Bitmap resultTextImageOriginal,
			Bitmap resultTextImageBW,
			int resultAreaX, int resultAreaY,
			int resultAreaW, int resultAreaH) {
		this.textOriginal = textOriginal;
		this.textTranslated = textTranslated;
		this.resultTextImageOriginal = resultTextImageOriginal;
		this.resultTextImageBW = resultTextImageBW;
		this.resultAreaX = resultAreaX;
		this.resultAreaY = resultAreaY;
		this.resultAreaW = resultAreaW;
		this.resultAreaH = resultAreaH;
	}

	public String getTextOriginal() {
		return textOriginal;
	}

	public String getTextTranslated() {
		return textTranslated;
	}

	public Bitmap getResultTextOriginalImage() {
		return resultTextImageOriginal;
	}
	
	public Bitmap getResultTextBWImage() {
		return resultTextImageBW;
	}
	
	public int getResultAreaX() {
		return resultAreaX;
	}

	public int getResultAreaY() {
		return resultAreaY;
	}

	public int getResultAreaW() {
		return resultAreaW;
	}

	public int getResultAreaH() {
		return resultAreaH;
	}
	
	@SuppressLint("DefaultLocale")
	public void setTextOriginal(String textOriginal) {
		if (textOriginal != null) {
			this.textOriginal = textOriginal.toLowerCase();
		}
	}

	@SuppressLint("DefaultLocale")
	public void setTextTranslated(String textTranslated) {
		if (textTranslated != null) {
			this.textTranslated = textTranslated.toLowerCase();
		}
	}
}
