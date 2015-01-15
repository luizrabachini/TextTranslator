/*
 * TextTranslator Android App
 */
package com.texttranslator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.texttranslator.R;
import com.texttranslator.helper.ImageViewZoom;

/**
 * Activity to show result of translation
 * 
 * @author luiz
 */
public class ResultActivity extends Activity {
	private static final String TAG = "ResultActivity";
	
	private Bitmap imageResult; // Image result to show
	private ImageViewZoom imageViewResult; // Imageview to set image result
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
			
		imageViewResult = (ImageViewZoom)findViewById(R.id.img_result);
		prepareResult();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	/**
	 * Show result on activity
	 */
	private void prepareResult() {
		byte imageBytes[];
		
		Log.i(TAG, "showResult: Opening result");
		
		if(getIntent().hasExtra("IMAGE_RESULT")) {
			imageBytes = getIntent().getByteArrayExtra("IMAGE_RESULT");
		    imageResult = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		    imageViewResult.setImageResult(imageResult);
		}
	}
}
