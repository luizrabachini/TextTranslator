/*
 * TextTranslator Android App
 */
package com.texttranslator.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.texttranslator.MainActivity;
import com.texttranslator.helper.Tools;

/**
 * Create process result data
 * 
 * @author luiz
 */
public class ProcessResult {
	private static final String TAG = "DetectionResult";
	
	private final String ORIGINAL_IMAGE_NAME = "original.jpg";
	private final String BW_IMAGE_NAME = "bw.jpg";
	private final String TEXT_REGIONS_IMAGE_NAME = "text_regions.jpg";
	private final String BG_REGIONS_IMAGE_NAME = "bg_regions.jpg";
	private final String OCR_IMAGE_NAME = "ocr.jpg";
	private final String TRANSLATED_IMAGE_NAME = "translated.jpg";
	
	private final int FONT_SIZE = 60;
	private final int FONT_SPACE = 20;
	
	private File originalImageFile,
					bwImageFile,
					textRegionsImageFile,
					bgRegionsImageFile,
					ocrImageFile,
					translatedImageFile;

	private ArrayList<ProcessStorage> detectionStorage;
	
	private Bitmap originalImageBitmap,
					bwImageBitmap,
					textRegionsImageBitmap,
					ocrImageBitmap,
					translatedImageBitmap;
	private Paint paintBg,
				  paintFontFill,
				  paintBox;
				  //paintBgOverride;
	
	private Draw draw;
	
	private String dirFolder;
	
	public ProcessResult() {
		detectionStorage = new ArrayList<ProcessStorage>();
				
		paintBg = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
		paintBg.setStyle(Paint.Style.FILL);
		paintBg.setColor(Color.GRAY);
		
//		paintBgOverride = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
//		paintBgOverride.setStyle(Paint.Style.FILL);
//		paintBgOverride.setColor(Color.GRAY);
//		paintBgOverride.setAlpha(128);
		
		paintFontFill = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
		paintFontFill.setStyle(Paint.Style.FILL);
		paintFontFill.setColor(Color.WHITE);
		paintFontFill.setTextSize(FONT_SIZE);
		paintFontFill.setShadowLayer(3, 3, 3, Color.BLACK);
		
		paintBox = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintBox.setStyle(Paint.Style.STROKE);
		paintBox.setStrokeWidth(5);
		paintBox.setColor(Color.BLUE);
		
		draw = new Draw();
	}
	
	// ---------- Config ----------
	
	/**
	 * Prepare storage to new detection
	 */
	public void prepare() {
		dirFolder = Tools.createDatedDir();
		
		clear();
	}
	
	/**
	 * Clear storage to new detection
	 */
	private void clear() {
		detectionStorage.clear();
	}
	
	/**
	 * Get storage arraylist
	 * 
	 * @return Array list of storage
	 */
	public ArrayList<ProcessStorage> getDetectionStorage() {
		return detectionStorage;
	}
	
	// ---------- Files ----------
	
	/**
	 * Path to original image file (captured using camera device)
	 * 
	 * @return Path to image in local storage
	 */
	public String getOriginalImageFilePath() {
		return originalImageFile.getAbsolutePath();
	}
	
	// ---------- Text results ----------
	
	/**
	 * Process final text recognized of OCR
	 * 
	 * @return Text recognized
	 */
	public String getRecognizedText() {
		String result = "";
		Iterator<ProcessStorage> it = detectionStorage.iterator();
		
		for (; it.hasNext();) {
			ProcessStorage detected = it.next();
			result += detected.getTextOriginal() + "; ";
		}
		
		return result;
	}
	
	/**
	 * Process final text translated in remote service
	 * 
	 * @return Text translated
	 */
	public String getTranslatedText() {
		String result = "";
		Iterator<ProcessStorage> it = detectionStorage.iterator();
		
		for (; it.hasNext();) {
			ProcessStorage detected = it.next();
			result += detected.getTextTranslated() + "; ";
		}
		
		return result;
	}
	
	// ---------- Image getters ----------

	public Bitmap getOriginalImageBitmap() {
		return originalImageBitmap;
	}
	
	public Bitmap getBWImageBitmap() {
		return bwImageBitmap;
	}

	public Bitmap getTextRegionsBitmap() {
		return textRegionsImageBitmap;
	}
	
	public Bitmap getOcrBitmap() {
		return ocrImageBitmap;
	}
	
	public Bitmap getTranslatedBitmap() {
		return translatedImageBitmap;
	}	
	
	// ---------- Create ----------
		
	/**
	 * Process of all image results
	 */
	public void createAllImagesBitmap() {
		createTextRegionsImageBitmap();
		createOcrImageBitmap();
		createTranslatedImageBitmap();
	}
	
	/**
	 * Create original image file to store result
	 * 
	 * @param image Original image
	 */
	public void createOriginalImageBitmap(Bitmap image) {
		if (image != null) {
			originalImageFile = Tools.createFile(dirFolder, ORIGINAL_IMAGE_NAME);
			originalImageBitmap = image;				
			Tools.saveImage(originalImageBitmap, originalImageFile);
		} else {
			Log.e(TAG, "createOriginalImageBitmap: Null image");
		}
	}
	
	/**
	 * Create black/white image file to store result
	 * 
	 * @return Absolute path to file
	 */
	public String createBWImageFile() {
		bwImageFile = Tools.createFile(dirFolder, BW_IMAGE_NAME);		
		return bwImageFile.getAbsolutePath();
	}
	
	/**
	 * Load black/white image to show result
	 * 
	 * @return Absolute path to file
	 */
	public void createBWImageBitmap() {
		bwImageBitmap = BitmapFactory.decodeFile(bwImageFile.getAbsolutePath());
	}

	/**
	 * Create image containing text regions detached with rectangles
	 */
	public void createTextRegionsImageBitmap() {
		textRegionsImageFile = Tools.createFile(dirFolder, TEXT_REGIONS_IMAGE_NAME);
		textRegionsImageBitmap = createProcessedImageBitmap(MainActivity.OPT_TEXT_REGIONS, textRegionsImageFile);
	}
	
	/**
	 * Create image containing original text areas override with OCR result text
	 */
	public void createOcrImageBitmap() {
		ocrImageFile = Tools.createFile(dirFolder, OCR_IMAGE_NAME);
		ocrImageBitmap = createProcessedImageBitmap(MainActivity.OPT_OCR, ocrImageFile);
	}
	
	/**
	 * Create image containing original text areas override with translated result text
	 */
	public void createTranslatedImageBitmap() {
		translatedImageFile = Tools.createFile(dirFolder, TRANSLATED_IMAGE_NAME);
		translatedImageBitmap = createProcessedImageBitmap(MainActivity.OPT_TRANSLATE, translatedImageFile);
	}
	
	// ---------- Create image ----------
	
	/**
	 * Generate image result of operations using option flag
	 * 
	 * @param option Operation option
	 * @param fileToResult File to save image result (keep null to  save)
	 * @return Image result of operation
	 */
	@SuppressLint("DefaultLocale")
	private Bitmap createProcessedImageBitmap(int option, File fileToResult) {
		int fontSize = FONT_SIZE,
			fontSpace = FONT_SPACE;
		String textExtractedToDraw = "", // Text to draw in image result
			   textToDraw[];
		
		Log.i(TAG, "createProcessedImageBitmap: Reconstruction of image started");
		
		Bitmap bgBitmap = originalImageBitmap.copy(Bitmap.Config.ARGB_8888, true), // Copy original image to draw
			   textBitmap = Bitmap.createBitmap(originalImageBitmap.getWidth(), originalImageBitmap.getHeight(), Bitmap.Config.ARGB_8888);
				
		Canvas bgCanvas = new Canvas(bgBitmap),	// Canvas to draw background
			   textCanvas = new Canvas(textBitmap);	// Canvas to draw text
		
		Iterator<ProcessStorage> it = detectionStorage.iterator();
		
		textCanvas.drawColor(Color.TRANSPARENT);
		
		// Loop results containing image and coordinates
		for (; it.hasNext();) {
			ProcessStorage detected = it.next();
						
			int x = detected.getResultAreaX();		// Point x top|left pixel of box of text
			int y = detected.getResultAreaY();		// Point y top|left pixel of box of text
			int width = detected.getResultAreaW();	// Width of box
			int height = detected.getResultAreaH();	// Height of box of text
			
			// Option to create only boxs of text regions
			if (option == MainActivity.OPT_TEXT_REGIONS) {
				bgCanvas.drawRect(x, y, x + width, y + height, paintBox);
				continue;
			// Option to override text regions with OCR text
			} else if (option == MainActivity.OPT_OCR) {
				textExtractedToDraw = detected.getTextOriginal();
			// Option to override text regions with translated text
			} else if (option == MainActivity.OPT_TRANSLATE) {
				textExtractedToDraw = detected.getTextTranslated();
			// Bad option
			} else {
				Log.e(TAG, "createProcessedImageBitmap: Bad option " +
						option + " to create result");
				return null;
			}
			
			// Verify text null and text small
			if (textExtractedToDraw == null || textExtractedToDraw.length() <= 1) {
				continue;
			}
			
			textExtractedToDraw = textExtractedToDraw.toLowerCase();
			
			// Adapt font size to show text on region
			
			int textWidth = 0;
			int textHeight = 0;
			
			paintFontFill.setTextSize(fontSize);
			
			int fontSizeDif = Math.abs((int)(fontSize * 0.1));
			int fontSpaceDif = Math.abs((int)(fontSpace * 0.1));
			
			while (true) {
				textToDraw = splitTextToDraw(textExtractedToDraw, width, paintFontFill);
				textHeight = Math.abs((int)(paintFontFill.descent() + paintFontFill.ascent()));
				if ((textToDraw.length * (textHeight + fontSpace)) < height) {
					break;
				} else {
					fontSize -= fontSizeDif;
					fontSpace -= fontSpaceDif;
					paintFontFill.setTextSize(fontSize);
				}
			}			
						
			// Shape background with blur
			Bitmap bgBlurImage = draw.fastblur(detected.getResultTextOriginalImage(), 30);
			bgCanvas.drawBitmap(bgBlurImage, x, y, paintBg);
			
			int yPosText = y + textHeight + (int)(fontSpace * 0.3);			
					
			for (int i = 0; i < textToDraw.length; i++, yPosText += textHeight + FONT_SPACE) {
				// Update dimensions of text to draw
				textWidth = (int)paintFontFill.measureText(textToDraw[i]);
				
				// Calculate px to draw text result (centered)
				int xPosText = (int)((x + (width / 2)) - (textWidth / 2));
				// Draw text fill
				textCanvas.drawText(textToDraw[i], xPosText, yPosText, paintFontFill);
			}
		}
		
		if (fileToResult != null) {
			// Save image containing removed text regions
			bgRegionsImageFile = Tools.createFile(dirFolder, BG_REGIONS_IMAGE_NAME);
			Tools.saveImage(bgBitmap, bgRegionsImageFile);
			// Save image result of override text
			bgCanvas.drawBitmap(textBitmap, 0, 0, paintBg);
			Tools.saveImage(bgBitmap, fileToResult);
		}
		
		Log.i(TAG, "createProcessedImageBitmap: Reconstruction of image complete");
		
		return bgBitmap;
	}
	
	/**
	 * Find best position to split text (split using character ' ' in first position)
	 * 
	 * @param text Text to split
	 * @param result Vector to store result (two position is required)
	 */
	public String[] splitTextToDraw(String text, int boxWidth, Paint paint) {
		int textWidth = (int)paint.measureText(text);
		
		// Split big text line
		int textToDrawParts = 1;
		for (; (textWidth / textToDrawParts) > boxWidth; textToDrawParts++);
		
		int textSplitSize = text.length() / textToDrawParts;
		int textLastSplit = 0;
		ArrayList<String> resultTemp = new ArrayList<String>();
		
//		Log.i(TAG, "createProcessedImageBitmap: Text: " + text + ", Sizes: " + boxWidth + ", " + textWidth +
//				", Parts: " + textToDrawParts);
//		Log.i(TAG, "createProcessedImageBitmap: Splitting text in " + textToDrawParts + " parts");
		
		for (int i = textSplitSize; i < text.length(); i += textSplitSize) {
			i = findBestPositionToSplit(text, i, textLastSplit, ' ');
			resultTemp.add(text.substring(textLastSplit, i));
			textLastSplit = i;
		}
		
		if (textLastSplit < text.length()) {
			resultTemp.add(text.substring(textLastSplit, text.length()));
		}
		
		Iterator<String> iterator = resultTemp.iterator();
		String result[] = new String[resultTemp.size()];
		for (int i = 0; iterator.hasNext(); i++) {
			result[i] = iterator.next();
		}
		
		return result;		
	}
	
	/**
	 * Find best position to split string
	 * 
	 * @param text Text to split
	 * @param indexStart Index of start search best position
	 * @param limitBottom Bottom index limit to search
	 * @param split	Char separator to split
	 * @return Array of text split
	 */
	private int findBestPositionToSplit(String text, int indexStart, int limitBottom, char split) {
		
		int index1, index2, size1, size2;
		boolean success1, success2;
		
		index1 = index2 = indexStart;
		size1 = size2 = 0;
		success1 = success2 = false;
		
		if (text.charAt(indexStart) == split) {
			return indexStart;
		}
		
		for (int i = indexStart; i < text.length(); i++) {
			if (text.charAt(i) == split) {
				index1 = i;
				success1 = true;
				break;
			}
			size1++;
		}
		
		for (int i = indexStart; i > limitBottom; i--) {
			if (text.charAt(i) == split) {
				index2 = i;
				success2 = true;
				break;
			}
			size2++;
		}
		
		if (success1 && success2) {
			if (size1 < size2) {
				return index1;
			} else {
				return index2;
			}
		} else if (success1) {
			return index1;
		} else if (success2) {
			return index2;
		} else {
			return text.length();
		}
	}
	
	/**
	 * Return current directory to store result
	 * 
	 * @return Absolute path to folder
	 */
	public String getCurrentDirFolder() {
		return dirFolder;
	}
}
