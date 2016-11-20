package com.example.logprogram;

import static com.example.logprogram.Constant.CHINESE_FLAG;
import static com.example.logprogram.Constant.ENGLISH_FLAG;
import static com.example.logprogram.Constant.REGISTER_FLAG;
import static com.example.logprogram.Constant.UNREGISTER_FLAG;
import static com.example.logprogram.Constant.gps_flag;
import static com.example.logprogram.Constant.mobile_flag;
import static com.example.logprogram.Constant.wifi_flag;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class NetworkChecking extends Service{

	static final int CHINESE_INTERFACE = CHINESE_FLAG;
	static final int ENGLISH_INTERFACE = ENGLISH_FLAG;
	static final int REGISTER          = REGISTER_FLAG;
	static final int UNREGISTER        = UNREGISTER_FLAG;
	//mark if the corresponding wireless network is opened.
	boolean GPS_FLAG  	= false;
	boolean WIFI_FLAG 	= false;
	boolean MOBILE_FLAG = false;	
	// Keeps track of all current registered clients.
	ArrayList<Messenger> mClients = new ArrayList<Messenger>(); 
	// Target we publish for clients to send messages to IncomingHandler.
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mMessenger.getBinder();
	}

	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case NetworkChecking.REGISTER:
	        	mClients.add(msg.replyTo);
	        	//Toast.makeText(getApplicationContext(), "REGISTER", Toast.LENGTH_SHORT).show();
	        	break;
	        case NetworkChecking.UNREGISTER:
	        	mClients.remove(msg.replyTo);
	        	//Toast.makeText(getApplicationContext(), "UNREGISTER", Toast.LENGTH_SHORT).show();
	        	break;
	        case NetworkChecking.CHINESE_INTERFACE:
	        	Toast.makeText(getApplicationContext(), "CHINESE", Toast.LENGTH_SHORT).show();
	        	interfaceTransition(msg.what);
	            break;
	        case NetworkChecking.ENGLISH_INTERFACE:
	        	Toast.makeText(getApplicationContext(), "ENGLISH", Toast.LENGTH_SHORT).show();
	        	interfaceTransition(msg.what);
	        	break;
	        default:
	            super.handleMessage(msg);
	        }
	    }
	    
	    public void interfaceTransition(int Interface){
	    	switch(Interface)
	    	{
	    		case NetworkChecking.CHINESE_INTERFACE:
	    			//if GPS is connected and either wifi or mobile is connected
	    			if(GPS_FLAG && (WIFI_FLAG || MOBILE_FLAG)){
	    				//stop background network checking program	
	    				stopSelf();	    				
	    				//go to login page
	    				Intent CtransIntent = new Intent(getApplication(), C_Login.class);
	    				CtransIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    				startActivity(CtransIntent);
	    			}else{
	    				Toast.makeText(getApplicationContext(), "請設定網路連線選項", Toast.LENGTH_SHORT).show();
	    				//go to setting page
	    				Intent CtransIntent = new Intent(getApplication(), C_Setting.class);
	    				CtransIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    				//put network status flag in the bundle
	    				Bundle bundle = new Bundle();
	    				bundle.putBoolean("GPS", GPS_FLAG);
	    				bundle.putBoolean("WIFI", WIFI_FLAG);
	    				bundle.putBoolean("MOBILE", MOBILE_FLAG);
	    				CtransIntent.putExtras(bundle);
	    				startActivity(CtransIntent);
	    				//remember to close background network checking service when setting completed
	    			}    			
	    			break;
	    		case NetworkChecking.ENGLISH_INTERFACE:
	    			//if GPS is connected and either wifi or mobile is connected
	    			if(GPS_FLAG && (WIFI_FLAG || MOBILE_FLAG)){
	    				//stop background network checking program
	    				stopSelf();
	    				//go to login page
	    				Intent EtransIntent = new Intent(getApplication(), E_Login.class);
	    				EtransIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    				startActivity(EtransIntent);
	    			}else{
	    				Toast.makeText(getApplicationContext(), "Please set up network options", Toast.LENGTH_SHORT).show();
	    				//go to setting page
	    				Intent EtransIntent = new Intent(getApplication(), E_Setting.class);
	    				EtransIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	    				//put network status flag in the bundle
	    				Bundle bundle = new Bundle();
	    				bundle.putBoolean("GPS", GPS_FLAG);
	    				bundle.putBoolean("WIFI", WIFI_FLAG);
	    				bundle.putBoolean("MOBILE", MOBILE_FLAG);
	    				EtransIntent.putExtras(bundle);	    				
	    				startActivity(EtransIntent);
	    				//remember to close background network checking service when setting completed
	    			} 	    			
	    			break;
	    		default:
	    			break;
	    	} 
	    }
	}	
	
	@Override
	public void onCreate(){
		super.onCreate();
		/*
		 * implement wireless checking GPS/WIFI/3G
		 * determine whether it should jump to login page or setting pagE
		 */
		GPS_FLAG 	= isGPSconnected();
		gps_flag	= GPS_FLAG;
		WIFI_FLAG 	= isWificonnected();
		wifi_flag	= WIFI_FLAG;
		MOBILE_FLAG = isMobileconnected();
		mobile_flag = MOBILE_FLAG;
	}	

	private boolean isMobileconnected(){
		//check whether 3G is enabled in device or not using connectivity manager.
		ConnectivityManager connMgr
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo
		= connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
		boolean isMobileConn = networkInfo.isConnected();
		return isMobileConn;
	}	
	
	private boolean isWificonnected(){
		//check whether wifi is enabled in device or not using connectivity manager.
		ConnectivityManager connMgr
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo
		= connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		boolean isWifiConn = networkInfo.isConnected();
		return isWifiConn;
	}
	
    private boolean isGPSconnected() {
    	//check whether GPS is enabled in device or not using LocationManager.
    	LocationManager manager
    	= (LocationManager) getSystemService(Context.LOCATION_SERVICE );
    	boolean gpsStatus
    	= manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    	return gpsStatus;
    }	
	
    //destroy the activity
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "stop network checking service", Toast.LENGTH_SHORT).show();
    }		
	
}
