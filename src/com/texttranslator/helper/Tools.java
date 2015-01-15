/*
 * TextTranslator Android App
 */
package com.texttranslator.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.texttranslator.MainActivity;

/**
 * Tools
 * 
 * @author luiz
 */
public class Tools {
	private static final String TAG = "TextTools";
	
	public Tools() {
	}
	
	public static void createDir(String dirPath) {
		File appPath = new File(dirPath);
		
		appPath.mkdirs();
	}
	
	/**
	 * Create random file name
	 * 
	 * @param prefix Name prefix of file
	 * @param suffix Name suffix of file (generally the extension)
	 * @return File name
	 */
	@SuppressLint("SimpleDateFormat")
	public static String createRandomFileName(String prefix, String suffix) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String fileName = prefix + dateFormat.format(date) + suffix;
		
		return fileName;
	}
	
	/**
	 * Create a temporary folder named at current datetime system
	 * 
	 * @return Path to dated directory created
	 */
	@SuppressLint("SimpleDateFormat")
	public static String createDatedDir() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String datedDirPath = MainActivity.APP_PATH + dateFormat.format(date) + "/";
		File datedDir = new File(datedDirPath);
		
		datedDir.mkdirs();
		
		return datedDirPath;
	}
	
	/**
	 * Create file using random name
	 * 
	 * @param prefix Name prefix of file
	 * @param suffix Name suffix of file (generally the extension)
	 * @return File created
	 */
	public static File createRandomFile(String prefix, String suffix) {
		String fileName = createRandomFileName(prefix, suffix);
		File file = new File(MainActivity.APP_PATH, fileName);
		
		return file;
	}
	
	/**
	 * Create file in app path directory
	 * 
	 * @param name Name of file
	 * @return File created
	 */
	public static File createFile(String name) {
		File file = new File(MainActivity.APP_PATH, name);
		
		return file;
	}
	
	/**
	 * Create file
	 * 
	 * @param path Path to store file
	 * @param name Name of file
	 * @return File created
	 */
	public static File createFile(String path, String name) {
		File file = new File(path, name);
		
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Save image in file
	 * 
	 * @param image Image to store
	 * @param imageFile Image file to store
	 * @return True, if operation success, or false, if not
	 */
	public static void saveImage(Bitmap image, File imageFile) {	
        FileOutputStream outStream;
        
        try {
            outStream = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.i(TAG, "saveImage: Image stored in " + imageFile.getAbsolutePath());
        } catch (Exception e) {
        	Log.e(TAG, "saveImage: Save image fail");
            imageFile = null;
        }
	}
	
	/**
	 * Save text in file
	 * 
	 * @param text Text to store
	 * @param textFile File to store
	 * @return True, if operation success, or false, if not
	 */
	public static void saveText(String text, File textFile) {	
        FileOutputStream outStream;
        
        try {
            outStream = new FileOutputStream(textFile);
            outStream.write(text.getBytes());
            outStream.flush();
            outStream.close();
        	Log.i(TAG, "saveText: File stored in " + textFile.getAbsolutePath());
        } catch (Exception e) {
        	Log.e(TAG, "saveText: Save file fail");
            textFile = null;
        }
	}
	
	/**
	 * Converts dp unit to pixels (based in device density) 
	 * 
	 * @param dp Value, in dp, to convert
	 * @param canvas Canvas of device
	 * @return Value converted to px
	 */
	public static float convertDpToPixel(float dp, Canvas canvas){
	    int density = canvas.getDensity();
	    float px = dp * (density / 160f);
	    
	    return px;
	}

	/**
	 * Converts pixels unit to dp (based in device density) 
	 * 
	 * @param px Value, in px, to convert
	 * @param canvas Canvas of device
	 * @return Value converted to dp
	 */
	public static float convertPixelsToDp(float px, Canvas canvas){
		int density = canvas.getDensity();
	    float dp = px / (density / 160f);
	    
	    return dp;
	}
}
