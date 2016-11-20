package com.example.logprogram;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class GMap extends FragmentActivity{

	private GoogleMap googleMap;
	//update the location when moving over a specified distance
/*    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1;
    //update the location every 1000 millisecond
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000;
    //GPS location function
    private String locationProvider;
    private LocationManager locationManager;
    private Location mLocation;
    private Criteria criteria;*/
	
	protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
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
	}
	
    //destroy the activity
    protected void onDestroy(){
        super.onDestroy();
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    protected void onStart(){
    	super.onStart();
/*        //Getting LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Creating a criteria object to retrieve provider
        criteria = new Criteria();
        //set up a finer location accuracy requirement 
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //set up a low power requirement. 
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //Getting the name of the best provider
        locationProvider = locationManager.getBestProvider(criteria, true);
        //Getting Current Location
        mLocation = locationManager.getLastKnownLocation(locationProvider);
        //Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(
        		locationProvider, 
        		MINIMUM_TIME_BETWEEN_UPDATES, 
        		MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, 
        		myLocListener);*/
    }
    
    protected void onResume(){
    	super.onResume();
    }
    
    protected void onPause(){
    	super.onPause();
    }
    
    protected void onStop(){
    	super.onStop();
    }
	
    public void InfoClick(View v){
    	
    }
    
    
/*	// Define a listener that responds to location updates
	private final LocationListener myLocListener = new LocationListener() {
        @Override
        //Called when a new location is found by the network location provider.
        public void onLocationChanged(Location location) {
            mLocation = location;
            //obtain the last-known info of provider
            if (mLocation != null) {
            	*//** the variable should be used for real testing
            	 * 
            	 * lng     = String.format("%s", mLocation.getLongitude());
	             * lat     = String.format("%s", mLocation.getLatitude());
	             * speed   = String.format("%s", mLocation.getSpeed());
	             * curTime = String.format("%s", mLocation.getTime()); 
            	 * *//*

            }else{
            	//toast a message if no info is obtained
                Toast.makeText(GMap.this, "Cannot obtain location info", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        //Called when the provider status changes
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        //Called when the provider is enabled by the user
        public void onProviderEnabled(String s) {}

        @Override
        //Called when the provider is disabled by the user
        public void onProviderDisabled(String s) {}
	};*/
}
