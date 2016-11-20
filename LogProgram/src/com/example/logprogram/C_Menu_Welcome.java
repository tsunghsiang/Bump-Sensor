package com.example.logprogram;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class C_Menu_Welcome extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_welcome);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
    
    protected void onResume(){
    	super.onResume();  	
    }    
    
    //destroy the activity
    protected void onDestroy(){
        super.onDestroy();
    }
    
    protected void onStart(){
    	super.onStart();
    }    
    
    protected void onPause(){
    	super.onPause();
    }
    
    protected void onStop(){
    	super.onStop();	    	
    }	
}
