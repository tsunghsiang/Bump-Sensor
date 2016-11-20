package com.example.logprogram;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**************  
 * The activity that show the detected event on map 
 * (if the user press the Map Button on Record Activity)
 * 
 * it will show:
 * 1. event counter Text View
 * 2. go back Button
 * *****************/

public class MapActivity extends FragmentActivity{
	private GoogleMap googleMap;
	private LocationManager mgr;
	private String best;
	
	private Button gobackButton;
	private TextView eventCountResult_tv;
	
	public int eventCount=0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		
		if(status != ConnectionResult.SUCCESS){ // Google Play Services are not available
	        	
	            int requestCode = 10;
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	            dialog.show();
	            
	    }else{ // Google Play Services are available
	        	
	        	// Getting reference to the SupportMapFragment of activity_main.xml
	            SupportMapFragment fm 
	            = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Gmap);
	        	
	            // Getting GoogleMap object from the fragment
	            googleMap = fm.getMap();
	            
	            // Enabling MyLocation Layer of Google Map
	            googleMap.setMyLocationEnabled(true);

	    }
		
		/************************** startGPS ***********************************/
		
		mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		best = mgr.getBestProvider(criteria, true);
		Location location = mgr
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (best != null)
			location = mgr.getLastKnownLocation(best);
		mgr.requestLocationUpdates("gps", 0, 0, locationlistener); // 讓locationlistener處理資料有變化時的事情
		
		double lat,lng;
		lat = location.getLatitude();
		lng = location.getLongitude();
		LatLng current = new LatLng(lat,lng);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 14));
		
		/************************** Initial ***********************************/
		findview();
		drawEvent();
		eventCountResult_tv.setText(String.valueOf(SystemParameters.AnomCount));
		
		//change the button
		if(SystemParameters.isEnd){
			Resources res = this.getResources();
			Drawable drawable = res.getDrawable(R.drawable.exit);
			gobackButton.setBackgroundDrawable(drawable);
			gobackButton.setText("");

		}
		
		
	}
	
	private void findview(){
		gobackButton = (Button) findViewById(R.id.bt_back);
		eventCountResult_tv = (TextView) findViewById(R.id.tv_eventCount_result);
	}
	
	private final LocationListener locationlistener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location){
			double lat,lng;
			lat = location.getLatitude();
			lng = location.getLongitude();
			LatLng current = new LatLng(lat,lng);
			//googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16));
			//int i = SystemParameters.eventList.size();
			drawEvent();
			eventCountResult_tv.setText(String.valueOf(SystemParameters.AnomCount));

		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public void goBack(View v){
		//Intent mainIntent = new Intent(MapActivity.this,RecordActivity.class);
		MapActivity.this.finish();
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
	    drawEvent();
	    eventCountResult_tv.setText(String.valueOf(SystemParameters.AnomCount));

	}
	
	private void drawEvent(){
		for(int i=0;i<SystemParameters.eventList.size();i++){
			Event event = new Event();
			event = SystemParameters.eventList.get(i);
			double lat = event.getLatitude();
			double lng = event.getLongitude();
			LatLng eventLocation = new LatLng(lat,lng);
			Marker eventMarker = googleMap.addMarker(new MarkerOptions()
			.position(eventLocation)
			.title("Ai: "+event.getAI()+"\nPosition: "+lat+" , "+lng+"\nSpeed: "+event.getSpeed()+"\nTime: "+event.getTime()+"\n")
			.snippet("")
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.event)
					));  
		}

	}

}
