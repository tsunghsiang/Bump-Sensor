package com.example.logprogram;

import static com.example.logprogram.Constant.gps_flag;
import static com.example.logprogram.Constant.mobile_flag;
import static com.example.logprogram.Constant.wifi_flag;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class E_Drawer extends FragmentActivity {
	
    private DrawerLayout layDrawer;
    private ListView lstDrawer;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {	
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_drawer);
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
            = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.e_drawer_map);
        	
            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
            
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);

        }        
        
        //initialize drawer list
        initDrawerList();
    }     
    
    private void initDrawerList(){
    	layDrawer = (DrawerLayout) findViewById(R.id.e_drawer_layout);
        lstDrawer = (ListView) findViewById(R.id.e_left_drawer);
        String[] drawer_menu = this.getResources().getStringArray(R.array.e_drawer_menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.e_drawer_list_item, drawer_menu);
        lstDrawer.setAdapter(adapter);
        
        lstDrawer.setOnItemClickListener(new OnItemClickListener(){ 
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				//obtain the total number of subviews
				int numChild = parent.getChildCount();
				
				//mark the sub-view being selected
				for(int idx = 0; idx < numChild; idx++){
					if(idx != id)
						parent.getChildAt(idx).setBackgroundColor(getResources().getColor(R.color.black));
					else
						parent.getChildAt(idx).setBackgroundColor(getResources().getColor(R.color.hard_green));
				}
				
				/** do something here in the future*/
				switch((int)id)
				{
					case 0: 
						startActivity(new Intent(getApplicationContext(), RecordActivity.class)); 
						break;
					case 1: 
	    				Intent EtransIntent = new Intent(getApplication(), E_Setting.class);
	    				EtransIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	    				//put network status flag in the bundle
	    				Bundle bundle = new Bundle();
	    				bundle.putBoolean("GPS", gps_flag);
	    				bundle.putBoolean("WIFI", wifi_flag);
	    				bundle.putBoolean("MOBILE", mobile_flag);
	    				EtransIntent.putExtras(bundle);	    				
	    				startActivity(EtransIntent);     
						break;
					case 2: 
						startActivity(new Intent(getApplicationContext(), E_Artificial_Return.class));
						break;
					case 3: /*SPC*/																	  
						break;
					case 4: 
						startActivity(new Intent(getApplicationContext(), E_Menu_Welcome.class)); 	  
						break;
					default:
						break;
				}				
			}
        	
        });
    }   
    
    //destroy the activity
    protected void onDestroy(){
        super.onDestroy();
    }
    
    protected void onStart(){
    	super.onStart();
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}