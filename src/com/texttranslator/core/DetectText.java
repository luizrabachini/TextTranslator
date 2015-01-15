/*
 * TextTranslator Android App
 */
package com.texttranslator.core;

import java.util.ArrayList;


import android.graphics.Bitmap;
import android.util.Log;

/**
 * Detect text using SWT
 * 
 * @author luiz
 */
public class DetectText {
	private static final String TAG = "DetectText";
	
	// Load static library to detect text
	static {
		System.loadLibrary("exec_text_detect");
	}

	/**
	 * Constructor of DetectText class
	 */
	public DetectText() {
	}
	
	/**
	 * Native function to execute SWT process
	 * 
	 * @param fileNameOriginalStr Image path to detection
	 * @param filePathBWStr Image path to store black and white image
	 * @return Delimiters of text regions
	 */
	private native int[] swtBoundingBoxes(String fileNameOriginalStr, String filePathBWFile);
	
	/**
	 * Generate SWT image from original image
	 * 
	 * @param image Image Bitmap to detection
	 * @param detectionResult Controller to store results
	 * @return True, if operation success, or false, if operation fail
	 */
	public boolean processDetectTextImage(Bitmap image, ProcessResult detectionResult) {
		ArrayList<ProcessStorage> detectionStorage = detectionResult.getDetectionStorage();
		int resultPoints[];							// Operation result (points of rectangles content text)
		Bitmap imageOriginalToDetection,			// Image original
			   imageBWToDetection;					// Image BW to OCR (threshold)
		
		Log.i(TAG, "processDetectTextImage: Detection started");
		
		// Save the original image (captured from camera device)
		detectionResult.createOriginalImageBitmap(image);
		
		// Create file to store BW result
		String filePathBWImageFile = detectionResult.createBWImageFile();
		String filePathOriginalImageFile = detectionResult.getOriginalImageFilePath();
				
		// Find text areas using SWT
		resultPoints = swtBoundingBoxes(filePathOriginalImageFile, filePathBWImageFile);
		
		// Open BW file
		detectionResult.createBWImageBitmap();
		
		// Get images original and BW
		imageOriginalToDetection = detectionResult.getOriginalImageBitmap();
		imageBWToDetection = detectionResult.getBWImageBitmap();
		
		// Process SWT image
		if (resultPoints == null) {
	    	Log.i(TAG, "processDetectTextImage: Detection fail");
			return false;
		} else {
			Bitmap resultTextAreaOriginalImage = imageOriginalToDetection.copy(Bitmap.Config.ARGB_8888, true);
			Bitmap resultTextAreaBWImage = imageBWToDetection.copy(Bitmap.Config.ARGB_8888, true);
			
			for (int i = 0; (i + 3) < resultPoints.length; i += 4) {
				int x = resultPoints[i];
				int y = resultPoints[i+1];
				int width = resultPoints[i+2];
				int height = resultPoints[i+3];
				
				// No insert small regions
//				int resultTextAreaWidth = resultTextAreaOriginalImage.getWidth();
//				if (width < (resultTextAreaWidth / 3)) {
//					continue;
//				}
				
				// Crop original image
				Bitmap imageTextOriginal = Bitmap.createBitmap(resultTextAreaOriginalImage,
						x, y, width, height);
				
				// Crop BW image
				Bitmap imageTextBW = Bitmap.createBitmap(resultTextAreaBWImage,
						x, y, width, height);
				
				// Create storage result
				ProcessStorage detected = new ProcessStorage("", "",
						imageTextOriginal, imageTextBW, x, y, width, height);
				
				// Add result to array
				detectionStorage.add(detected);
			}
	    	
	    	Log.i(TAG, "processDetectTextImage: Detection complete");
	    	
	    	return true;
		}
	}
}
