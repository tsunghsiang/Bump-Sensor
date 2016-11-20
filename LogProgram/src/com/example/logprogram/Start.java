package com.example.logprogram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Start extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	//Chinese interface transition
	public void C_interfaceClick(View view){
		Intent intent = new Intent(getApplicationContext(), C_Welcome.class);
	    startActivity(intent); //start to run the specified activity		
	}
	//English interface transition
	public void E_interfaceClick(View view){
		Intent intent = new Intent(getApplicationContext(), E_Welcome.class);
	    startActivity(intent); //start to run the specified activity		
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
	
}
