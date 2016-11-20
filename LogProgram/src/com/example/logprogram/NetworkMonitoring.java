package com.example.logprogram;

import static com.example.logprogram.Constant.CHINESE_FLAG;
import static com.example.logprogram.Constant.ENGLISH_FLAG;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

public class NetworkMonitoring extends Service{

	CountDownTimer waitTimer;
	boolean GPS_FLAG  	= false;
	boolean WIFI_FLAG 	= false;
	boolean MOBILE_FLAG = false;
	
	@Override
	public void onCreate(){
		super.onCreate();
		//Toast.makeText(this, "transitions", Toast.LENGTH_SHORT).show();
	
		waitTimer = new CountDownTimer(5000, 1000){
			@Override
			public void onFinish() {
				//obtain the status of 3G/WIFI/MOBILE NETWORK
				GPS_FLAG 	= isGPSconnected();
				WIFI_FLAG 	= isWificonnected();
				MOBILE_FLAG = isMobileconnected();
				NetworkMsg(GPS_FLAG, WIFI_FLAG, MOBILE_FLAG);
				this.start();
			}
			
			@Override
			public void onTick(long millisUntilFinished) {}
		}.start();	
	}		

	public void interfaceTransition(int language){
		switch(language)
		{
			case CHINESE_FLAG:
				Intent CtransIntent = new Intent(getApplication(), C_Drawer.class);
				CtransIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	   
				startActivity(CtransIntent);
				break;
			case ENGLISH_FLAG:
				Intent EtransIntent = new Intent(getApplication(), E_Drawer.class);
				EtransIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	   
				startActivity(EtransIntent);				
				break;
			default:
				break;
		}
	}
	
	public void NetworkMsg(boolean gpsFlag, boolean wifiFlag, boolean mobileFlag){
		if(gpsFlag && (wifiFlag || mobileFlag))
			Toast.makeText(getApplication(), "Network is stable", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getApplication(), "Network is unstable", Toast.LENGTH_SHORT).show();
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
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }
        stopSelf();
        Toast.makeText(getApplicationContext(), "stop network monitoring service", Toast.LENGTH_SHORT).show();
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override   
    public int onStartCommand (Intent intent, int flags, int startId) {   
        Bundle rev = intent.getExtras();
        int language = rev.getInt("interface");
        interfaceTransition(language);	 
        return START_NOT_STICKY;  
    }
	
}
