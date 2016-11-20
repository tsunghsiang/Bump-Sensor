package com.example.logprogram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class E_Register extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e_register);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void submitButton(View view){
		/*
		 * (1)send registration information to remote server
		 * (2)back to login page
		 * */
		startActivity(new Intent(this, E_Login.class));		
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
