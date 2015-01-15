/*
 * TextTranslator Android App
 */
package com.texttranslator.controller;

import java.io.IOException;

import com.texttranslator.MainActivity;

import android.hardware.Camera.Parameters;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Controller of camera
 * 
 * @author luiz
 */
public class CameraController implements SurfaceHolder.Callback {
	private static final String TAG = "CameraController";
	
	private static final int CAM_RES_WIDTH = 800;
	private static final int CAM_RES_HEIGHT = 480;
		
	private Camera camera;
	private Parameters params;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean viewing = false, // Flag to indicates preview status
					  ambientLight;	// Flag to indicates flash status

	/**
	 * Constructor
	 * 
	 * @param surfaceView Area to draw image preview
	 */
	public CameraController(SurfaceView surfaceView) {
		this.surfaceView = surfaceView;
		surfaceHolder = this.surfaceView.getHolder();
		
		surfaceHolder.addCallback(this);
		
		ambientLight = false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = android.hardware.Camera.open();
		params = camera.getParameters();
		
		params.setFocusMode("continuous-picture");		
		params.setPictureSize(CAM_RES_WIDTH, CAM_RES_HEIGHT);
		camera.setParameters(params);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (viewing) {
			stopVisualization();
		}

		if (camera != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
				startVisualization();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopVisualization();
		camera.release();
		camera = null;
	}
	
	/**
	 * Switch camera flash light
	 * 
	 * @param mainActivity Activity of camera preview
	 */
	public void switchAmbientLight(MainActivity mainActivity) {
		if (ambientLight) {
			disableFlashLight(mainActivity);
		} else {
			enableFlashLight(mainActivity);
		}
	}
	
	/**
	 * Enable flash light of camera
	 * 
	 * @param mainActivity Activity of camera
	 */
	public void enableFlashLight(MainActivity mainActivity) {
		try {
	        if (mainActivity.getPackageManager().hasSystemFeature(
	                PackageManager.FEATURE_CAMERA_FLASH)) {
	        	params.setFlashMode(Parameters.FLASH_MODE_TORCH);
	            camera.setParameters(params);
	            ambientLight = true;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.e(TAG, "enableFlashLight: Error in enable flash light");
	    }
	}
	
	/**
	 * Disable flash light of camera
	 * 
	 * @param mainActivity Activity of camera
	 */
	public void disableFlashLight(MainActivity mainActivity) {
		try {
	        if (mainActivity.getPackageManager().hasSystemFeature(
	                PackageManager.FEATURE_CAMERA_FLASH)) {
	        	params.setFlashMode(Parameters.FLASH_MODE_OFF);
	            camera.setParameters(params);
	            ambientLight = false;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.e(TAG, "disableFlashLight: Error in disable flash light");
	    }
	}

	/**
	 * Capture image from camera
	 * 
	 * @param shutter
	 * @param raw
	 * @param jpeg
	 */
	public void takePhoto(Camera.ShutterCallback shutter,
			Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
		camera.takePicture(shutter, raw, jpeg);
	}

	/**
	 * Start camera preview
	 */
	public void startVisualization() {
		viewing = true;
		camera.startPreview();
	}

	/**
	 * Stop camera preview
	 */
	public void stopVisualization() {
		camera.stopPreview();
		viewing = false;
	}
	
	/**
	 * Camera light status
	 * 
	 * @return True, if flash is enabled, or False, if not
	 */
	public boolean getFlashLightStatus() {
		return ambientLight;
	}
	
	/**
	 * Indicates camera preview state
	 * 
	 * @return True, if viewing, or false, if not
	 */
	public boolean isViewing() {
		return viewing;
	}
}