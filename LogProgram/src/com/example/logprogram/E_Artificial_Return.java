package com.example.logprogram;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class E_Artificial_Return extends Activity {
	
	private EditText userName;
	private EditText location;
	private EditText systime;
	private Button   snapShot, handin;
	private Spinner  event;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e_artificial_return);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
	
    public void e_hand_in(View v){
    	
    }
    
    public void e_snapshot(View v){
    	
    }
    
}
