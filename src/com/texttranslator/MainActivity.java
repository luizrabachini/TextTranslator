/*
 * TextTranslator Android App
 */
package com.texttranslator;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.texttranslator.R;
import com.texttranslator.controller.CameraController;
import com.texttranslator.core.Benchmark;
import com.texttranslator.core.DetectText;
import com.texttranslator.core.ExecutionLog;
import com.texttranslator.core.ExtractText;
import com.texttranslator.core.ProcessResult;
import com.texttranslator.core.TextTranslator;
import com.texttranslator.helper.ServerCom;

/**
 * Main Activity
 * 
 * @author luiz
 */
public class MainActivity extends Activity implements OnClickListener,
ShutterCallback, PictureCallback {
	private static final String TAG = "MainActivity";
	
	public static final String APP_PATH =
			Environment.getExternalStorageDirectory().toString() + "/TextTranslator/";	// Path of application
	
	public static final short OPT_TEXT_REGIONS = 1; // Option: find text regions 
	public static final short OPT_OCR = 2; // Option: OCR
	public static final short OPT_TRANSLATE = 3; // Option: Translate

	// UI elements
	
	private ImageButton btnDetect,
						 btnOcr,
						 btnTranslate,
						 btnConfig,
						 btnConnTest;
	private Button btnConfirmProc, // Confirm process button
					btnCancelProc; // Cancel process button
	private TextView txtLogSplashScreen, // Log to user feedback
					  txtLangView; // Language selected info
	private ToggleButton switchAmbientLight; // Switch between on and off camera flash light
	private LinearLayout processingMessage, // Processing feed back
						  confirmProcess; // Confirm process
	
	private PopupMenu popupMenuLang; // Configuration menu
	
	private ImageView imgConfirmProc; // Image to confirm process
	
	// Process
	
	private Bitmap imageToDetection; // Image captured by camera
	
	private Intent intentResult; // Intent to show result
					  
	private int option = -1; // Operation option selected
	
	private CameraController cameraController; // Controller of camera
	private ServerCom serverCom;
	
	private ProcessResult detectionResult; // Detection result to generate images	
	private DetectText detectText; // Text detection routine
	private ExtractText extractText; // Text extraction routine
	private TextTranslator textTranslator; // Translation routine
	
	private ExecutionLog execLog; // Collect statistic data of execution
	
	private Benchmark benchmark; // Benchmark for tests
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		serverCom = new ServerCom();
		execLog = new ExecutionLog();		
		benchmark = new Benchmark();
		
		init();
	}
	
	/**
	 * Create initial configuration (for splash screen components)
	 */
	private void init() {
		setContentView(R.layout.activity_splash);
		
		txtLogSplashScreen = (TextView)findViewById(R.id.txt_log_splash_screen);
		btnConnTest = (ImageButton)findViewById(R.id.btn_test_conn);
		
		btnConnTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startApp();
			}
		});
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startApp();
			}
		}, 200);
	}
	
	/**
	 * Start app and execute connection test to Internet
	 */
	private void startApp() {
		final MainActivity activity = this;
		txtLogSplashScreen.setText(getResources().getString(R.string.starting));
		
		if (serverCom.isTestingCom()) {
			return;
		}
		
		// Try connect. If ok, launch app.
		new Handler().postDelayed(new Runnable() {
			@Override
            public void run() {
				if (serverCom.executeComTest(activity)) {
					disableConnTest(); // Close test screen
					configResources(); // Configure resources of app
			    	startMainInterface(); // Start main interface
				} else {
					enableConnTest(); // Enable interface to repeat test connection
				}
            }
        }, 100);
	}
	
	/**
	 * Enable options in splash screen to execute connection test
	 */
	private void enableConnTest() {
		String text = getResources().getString(R.string.msg_offline);
		
		txtLogSplashScreen.setText(text);
		btnConnTest.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Disable options in splash screen to execute connection test
	 */
	private void disableConnTest() {
		String text = getResources().getString(R.string.msg_online);
		
		txtLogSplashScreen.setText(text);
		btnConnTest.setVisibility(View.GONE);
	}
	
	/**
	 * Configure application resources to launch
	 */
	private void configResources() {
		detectionResult = new ProcessResult();
		detectText = new DetectText();
		extractText = new ExtractText(getResources());
		textTranslator = new TextTranslator();
	}
	
	/**
	 * Launch main application interface
	 */
	private void startMainInterface() {
		setContentView(R.layout.activity_main);
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.sfv_camera);
		
		cameraController = new CameraController(surfaceView);
        		
		addButtonsActions();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onShutter() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	        case R.id.btn_detect:
	        	option = OPT_TEXT_REGIONS;
	        	prepareProcess();
	            break;
	        case R.id.btn_ocr:
	        	option = OPT_OCR;
	        	prepareProcess();
	            break;
	        case R.id.btn_translate:
	        	option = OPT_TRANSLATE;
	        	prepareProcess();
	            break;
	        case R.id.btn_config_app:
	        	showPopupMenu(v);
	            break;
	        case R.id.btn_ambient_light:
	        	cameraController.switchAmbientLight(this);
	        	break;
	        case R.id.btn_confirm_proc:
	        	startProcess();
	        	break;
	        case R.id.btn_cancel_proc:
	        	closeConfirmBar();
	        	prepareProcess();
	        	break;
	    }
	}
	
	/**
	 * Prepare process selected
	 */
	private void prepareProcess() {
		Log.i(TAG, "prepareProcess: Process started");
		
		detectionResult.prepare();
		
		if (cameraController.isViewing()) {
			cameraController.takePhoto(this, null, this);
        } else {
        	closeProcMessage();
            cameraController.startVisualization();
        }
	}
	
	/**
	 * Callback to capture picture
	 */
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.i(TAG, "onPictureTaken: Picture taken");
		imageToDetection = BitmapFactory.decodeByteArray(data, 0, data.length);
		cameraController.stopVisualization();
		previewProcess();
	}
	
	/**
	 * Show confirmation dialog to continue process
	 */
	private void previewProcess() {
		execLog.clearLog();
		execLog.appendLog("Detecting text...", true);
		detectText.processDetectTextImage(imageToDetection, detectionResult);
		execLog.appendLog("Done.", false);
		detectionResult.createTextRegionsImageBitmap();
				
		showConfirmBar();
	}
	
	/**
	 * Start main process
	 */
	private void startProcess() {
		closeConfirmBar();
		cameraController.disableFlashLight(this);
		showProcMessage();
		
		// Execute translation process in new thread
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				executeProcess();
			}
		}, 100);
	}
	
	/**
	 * Execute process
	 */
	private void executeProcess() {
		// Execute operations to generate result
		switch (option) {
	        case OPT_TEXT_REGIONS:
//	        	detectText.processDetectTextImage(imageToDetection, detectionResult);
	        	showResult();
	            break;
	        case OPT_OCR:
//	        	detectText.processDetectTextImage(imageToDetection, detectionResult);
	        	execLog.appendLog("Extracting text...", true);
	        	extractText.processOcr(textTranslator.getDictOri(), detectionResult);
	        	execLog.appendLog("Done.", false);
	        	showResult();
	            break;
	        case OPT_TRANSLATE:
//	        	detectText.processDetectTextImage(imageToDetection, detectionResult);
	        	execLog.appendLog("Extracting text...", true);
	        	extractText.processOcr(textTranslator.getDictOri(), detectionResult);
	        	execLog.appendLog("Done.", false);
	        	execLog.appendLog("Translating text...", true);
	        	textTranslator.processTransText(detectionResult);
	        	execLog.appendLog("Done.", false);
	        	execLog.appendLog("Original Text: " + detectionResult.getRecognizedText(), true);
	        	execLog.appendLog("Translated Text: " + detectionResult.getTranslatedText(), true);
	        	showResult();
	            break;
	    }
	}
	
	/**
	 * Scan folder application to benchmark
	 */
	public void executeBenchmark() {
		cameraController.stopVisualization();
    	showProcMessage();
		
		new Handler().postDelayed(new Runnable() {
			@Override
            public void run() {
		    	benchmark.scanFolder(detectText, extractText,
		    			textTranslator, detectionResult);
		    	closeProcMessage();
		    	cameraController.startVisualization();
            }
        }, 10);
	}
	
	/**
	 * Show result of process in new intent
	 * 
	 * @param option Option of process
	 */
	private void showResult() {
		Bitmap imageResult; // Image result to show in intent result
		
		Log.i(TAG, "showResult: Creating images");
		
		if (cameraController.getFlashLightStatus()) {
			cameraController.disableFlashLight(this);
		}
		
		execLog.appendLog("Editing original image...", true);
				
		// Switch option of process to generate results
		switch (option) {
			case OPT_TEXT_REGIONS:
				imageResult = detectionResult.getTextRegionsBitmap();
				break;
			case OPT_OCR:
				detectionResult.createOcrImageBitmap();
				imageResult = detectionResult.getOcrBitmap();
				break;
			case OPT_TRANSLATE:
				detectionResult.createTranslatedImageBitmap();
				imageResult = detectionResult.getTranslatedBitmap();
				break;
			default:
				Log.e(TAG, "showResult: Bad option " + option + " to create result");
				closeProcMessage();
				return;
		}
		
		execLog.appendLog("Done.", false);
		execLog.saveLogFile(detectionResult.getCurrentDirFolder());
		
		// Verify result image empty (no results)
		if (imageResult != null) {
			intentResult = new Intent(getBaseContext(), ResultActivity.class);
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			
			imageResult.compress(Bitmap.CompressFormat.JPEG, 100, bs);	// Convert image Bitmap to JPEG to show
			intentResult.putExtra("IMAGE_RESULT", bs.toByteArray());
			cameraController.startVisualization();
			
			closeProcMessage();			
			startActivity(intentResult);
		} else {
			Log.e(TAG, "showResult: No result to show!");
			closeProcMessage();
		}		
	}
	
	/**
	 * Find buttons in layout and add functions
	 */
	private void addButtonsActions() {
		btnDetect= (ImageButton)findViewById(R.id.btn_detect);
		btnOcr = (ImageButton)findViewById(R.id.btn_ocr);
		btnTranslate = (ImageButton)findViewById(R.id.btn_translate);
		
		btnConfig = (ImageButton)findViewById(R.id.btn_config_app);
		txtLangView = (TextView)findViewById(R.id.txt_lang_info);
		switchAmbientLight = (ToggleButton)findViewById(R.id.btn_ambient_light);
		
		confirmProcess = (LinearLayout)findViewById(R.id.pnl_confirm_proc);
		btnConfirmProc = (Button)findViewById(R.id.btn_confirm_proc);
		btnCancelProc = (Button)findViewById(R.id.btn_cancel_proc);
		imgConfirmProc = (ImageView)findViewById(R.id.img_confirm_proc);
		
		processingMessage = (LinearLayout)findViewById(R.id.pnl_processing);
				
		confirmProcess.setOnClickListener(this);
		processingMessage.setOnClickListener(this);
		
		btnDetect.setOnClickListener(this);
		btnOcr.setOnClickListener(this);
		btnTranslate.setOnClickListener(this);
		btnConfig.setOnClickListener(this);
		switchAmbientLight.setOnClickListener(this);
		btnConfirmProc.setOnClickListener(this);
		btnCancelProc.setOnClickListener(this);
	}
	
	/**
	 * Show popup menu to configure application language
	 * 
	 * @param view View to show popup
	 */
	private void showPopupMenu(View view) {
		popupMenuLang = new PopupMenu(MainActivity.this, view);
		popupMenuLang.getMenuInflater().inflate(R.menu.main, popupMenuLang.getMenu());

		popupMenuLang.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
            	// Switch language selected
            	switch (item.getItemId()) {
	    		    case R.id.lang_ptbr_enus:
	    		    	textTranslator.setTranslationOption(1);
	    		        txtLangView.setText(
	    		        		"Language: " + getResources().getString(R.string.lang_ptbr_enus));
	    		        break;
	    		    case R.id.lang_enus_ptbr:
	    		    	textTranslator.setTranslationOption(2);
	    		    	txtLangView.setText(
	    		    			"Language: " + getResources().getString(R.string.lang_enus_ptbr));
	    		    	break;
	    		    case R.id.scan_folder:
	    		    	executeBenchmark();
	    		    	break;
	    	    }
	    	    return true;
            }
        });

		popupMenuLang.show();
	}
	
	/**
	 * Show confirmation message
	 */
	private void showConfirmBar() {
		Bitmap confirmImageBitmap = detectionResult.getTextRegionsBitmap();
		
		imgConfirmProc.setImageBitmap(confirmImageBitmap);	
		confirmProcess.setVisibility(LinearLayout.VISIBLE);
	}
	
	/**
	 * Close confirmation message
	 */
	private void closeConfirmBar() {
		confirmProcess.setVisibility(LinearLayout.GONE);
	}
	
	/**
	 * Show processing message
	 */
	private void showProcMessage() {
		processingMessage.setVisibility(LinearLayout.VISIBLE);
	}
	
	/**
	 * Close processing message
	 */
	private void closeProcMessage() {
		processingMessage.setVisibility(LinearLayout.GONE);
	}
}
