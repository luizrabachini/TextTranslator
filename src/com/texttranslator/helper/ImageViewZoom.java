/*
 * TextTranslator Android App
 */
package com.texttranslator.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;

/**
 * Imageview able to zoom
 * 
 * @author luiz
 */
public class ImageViewZoom extends View {
	private float minScale = 0f; // Minimal zoom scale
	private float maxScale = 10f; // Maximum zoom scale
	
	private Bitmap imgResult;
	private int imgX = 0, // Position x to draw image on view
				imgY = 0, // Position y to draw image on view
				canvasWidth,
				canvasHeight,
				startMoveX, // x position of initial touch
				startMoveY, // y position of initial touch
				difMoveX, // Difference between start point x (touch) and current x (drag)
				difMoveY; // Difference between start point y (touch) and current y (drag)
	
	private ScaleGestureDetector gestureDetect; // Gesture detection
	private float scale = 1f; // Scale of image

	public ImageViewZoom(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		gestureDetect = new ScaleGestureDetector(context, new ScaleListener());
	}
	
	/**
	 * Set image result to show
	 * 
	 * @param imgResult Image to show
	 */
	public void setImageResult(Bitmap imgResult) {
		this.imgResult = imgResult;
	}
	
	/**
	 * Set initial values
	 */
	private void initParams() {
		int imgWidth = imgResult.getWidth(),
			imgHeight = imgResult.getHeight();
		minScale = canvasHeight / imgHeight;
		updateScale(minScale);
		imgX = (int)(canvasWidth / 2 - ((imgWidth * scale) / 2));
		imgY = (int)(canvasHeight / 2 - ((imgHeight * scale) / 2));
	}
	
	/**
	 * Update scale of image result
	 * 
	 * @param newScale New scale of image result
	 */
	private void updateScale(float newScale) {
		scale = Math.max(minScale, Math.min(newScale, maxScale));
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		int eventPx = (int)(event.getX()),
			eventPy = (int)(event.getY());
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startMoveX = eventPx;
			startMoveY = eventPy;
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			difMoveX = startMoveX - eventPx;
			difMoveY = startMoveY - eventPy;
			startMoveX = eventPx;
			startMoveY = eventPy;
			gestureDetect.onTouchEvent(event);
			invalidate();
			break;
		default:
			break;
		}
		
        return true;
    }
	
	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    
	    canvasWidth = canvas.getWidth();
	    canvasHeight = canvas.getHeight();
	    
	    if (imgResult != null) {
	    	if (minScale == 0) {
	    		initParams();
	    	}
	    	
	    	imgX -= difMoveX / scale;
	    	imgY -= difMoveY / scale;
	    	
	    	canvas.scale(scale, scale, canvasWidth / 2, canvasHeight / 2);	    	
	    	canvas.drawBitmap(imgResult, imgX, imgY, null);
	    }
	}
	
	/**
	 * Listener to get scale
	 */
	private class ScaleListener extends SimpleOnScaleGestureListener {		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float newScale = scale * detector.getScaleFactor();
			updateScale(newScale);
			return true;
		}
	}
}
