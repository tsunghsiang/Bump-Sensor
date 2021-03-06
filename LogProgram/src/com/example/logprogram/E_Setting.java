package com.example.logprogram;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import static com.example.logprogram.Constant.gps_flag;
import static com.example.logprogram.Constant.mobile_flag;
import static com.example.logprogram.Constant.wifi_flag;

public class E_Setting extends Activity {
	
	boolean GPS_FLAG, WIFI_FLAG, MOBILE_FLAG;
	ToggleButton wifiButton, mobileButton;
	Button gpsButton;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e_setting);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //initialize UI components
        initialize();
	}

    private void toggleInitialization(boolean gpsFlag, boolean wifiFlag, boolean mobileFlag) {
		if(wifiFlag)
			wifiButton.setChecked(true);
		else
			wifiButton.setChecked(false);
		
		if(mobileFlag)
			mobileButton.setChecked(true);
		else
			mobileButton.setChecked(false);
	}	
	
	public void initialize(){
		gpsButton    = (Button)findViewById(R.id.e_gps_button);
		wifiButton   = (ToggleButton)findViewById(R.id.e_wifi_button);
		mobileButton = (ToggleButton)findViewById(R.id.e_3g_button);
	}	
	
	public void gpsClick(View view){
	    // obtain the status of GPS
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    
        if(!provider.contains("gps")){ //if gps is disabled
        	GPS_FLAG = true;
        	gps_flag = true;
			Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		    startActivity(intent);
        }else{
        	GPS_FLAG = true;
        	gps_flag = true;
        	Toast.makeText(this, "GPS has been enabled", Toast.LENGTH_SHORT).show();
        }
	}

	public void wifiClick(View view){
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    // Is wifi on?
	    boolean wifiEnabled;
	    //obtain the status of wifi connection
	    WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
	    	    
	    if (on) {
	        // Enable vibrate
	    	if(!(wifiEnabled = wifiManager.isWifiEnabled()))
	    		wifiManager.setWifiEnabled(true);
	    	WIFI_FLAG = true;
	    	wifi_flag = true;
	    } else {
	        // Disable vibrate
	    	if((wifiEnabled = wifiManager.isWifiEnabled()))
	    		wifiManager.setWifiEnabled(false);
	    	WIFI_FLAG = false;
	    	wifi_flag = false;
	    }		
	}
	
	public void mobileClick(View view){
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	        // Enable vibrate
	    	setMobileDataEnabled(this, true);
	    	MOBILE_FLAG = true;
	    	mobile_flag = true;
	    } else {
	        // Disable vibrate
	    	setMobileDataEnabled(this, false);
	    	MOBILE_FLAG = false;
	    	mobile_flag = false;
	    }		
	}	
	
	private void setMobileDataEnabled(Context context, boolean enabled) {
	    ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class<?> conmanClass = null;
		try {
			conmanClass = Class.forName(conman.getClass().getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    Field iConnectivityManagerField = null;
		try {
			iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    iConnectivityManagerField.setAccessible(true);
	    Object iConnectivityManager = null;
		try {
			iConnectivityManager = iConnectivityManagerField.get(conman);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    Class<?> iConnectivityManagerClass = null;
		try {
			iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    Method setMobileDataEnabledMethod = null;
		try {
			setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    setMobileDataEnabledMethod.setAccessible(true);
	    try {
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void enterLogin(View view){
		//if GPS is connected and either wifi or mobile is connected
		if(GPS_FLAG && (WIFI_FLAG || MOBILE_FLAG)){
			//close network checking interface
			stopService(new Intent(this, NetworkChecking.class));
			//go to login page
			Intent intent = new Intent(this, E_Login.class);
			/*
			 * 評估是否有可能需要傳送 global state variable 儲存在 SQLite DB
			 * */
			startActivity(intent);			
		}else{
			
			Toast.makeText(
					this,  
					"\n\t\tPlease enable GPS\nOpen either Wiif or Mobile Network", 
					Toast.LENGTH_LONG).show();
		}
	}	
	
    //destroy the activity
    protected void onDestroy(){
        super.onDestroy();
    }
    
    protected void onStart(){
    	super.onStart();
        //receive bundle, with current network status, from the Network checking interface
        Bundle bundle = this.getIntent().getExtras();
        GPS_FLAG      = bundle.getBoolean("GPS");
        WIFI_FLAG     = bundle.getBoolean("WIFI"); 
        MOBILE_FLAG   = bundle.getBoolean("MOBILE");      	
    }
    
    protected void onResume(){
    	super.onResume();
    	toggleInitialization(GPS_FLAG, WIFI_FLAG, MOBILE_FLAG);
    }
    
    protected void onPause(){
    	super.onPause();
    }
    
    protected void onStop(){
    	super.onStop();
    }
}
