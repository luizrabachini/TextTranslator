/*
 * TextTranslator Android App
 */
package com.texttranslator.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.StrictMode;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Communication with external servers
 * 
 * @author luiz
 */
public class ServerCom {
//	private static final String TAG = "ServerCom";
	
	private boolean testingCom = false; // Flag to indicate connection testing running
	
	public ServerCom() {
	}
	
	/**
	 * Send request to server using GET method
	 * 
	 * @param stringUrl URL to GET
	 * @return Response of request
	 * @throws IOException
	 */
	public String sendGETRequest(String stringUrl) throws IOException {
		// To do: develop method without interface obstruction
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	    // ---------------------------------------
		
	    StringBuilder response  = new StringBuilder();
	    URL url = new URL(stringUrl);
	    HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();
	    
	    if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK)
	    {
	        BufferedReader input = new BufferedReader(
	        		new InputStreamReader(httpconn.getInputStream(), "UTF-8"));
	        String strLine = null;
	        while ((strLine = input.readLine()) != null)
	        {
	            response.append(strLine);
	        }
	        input.close();
	    }
	    
	    String result = StringEscapeUtils.unescapeHtml4(response.toString());
	    
	    return result;
	}
	
	/**
	 * Test if Internet connection is available
	 * 
	 * @return True, if connection found, or False, if not
	 */
	public boolean executeComTest(Activity activity) {
		testingCom = true;
		ConnectivityManager cm =
				(ConnectivityManager)activity.
				getSystemService(Context.CONNECTIVITY_SERVICE);
		testingCom = false;
		return (cm.getActiveNetworkInfo() != null);
	}
	
	/**
	 * Testing connection running
	 * 
	 * @return True, if test is running, or false, if not
	 */
	public boolean isTestingCom() {
		return testingCom;
	}
}
