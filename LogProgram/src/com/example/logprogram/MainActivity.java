package com.example.logprogram;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**************  
 * The introduction activity
 * It will jump to next page 10 seconds later or by pressing Next Button 
 ******************/

public class MainActivity extends FragmentActivity {

	private final int SPLASH_DISPLAY_LENGHT = 10000;
	private Button bt_skip ;
	private TextView tv_title;
	private TextView tv_mainDescription;
	private TextView tv_copyRight;
	
	private boolean isPressNext = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		if(!SystemParameters.isOpen){
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent mainIntent = new Intent(MainActivity.this,
						RecordActivity.class);
				if(!isPressNext)
					MainActivity.this.startActivity(mainIntent);
				MainActivity.this.finish();
			}

		}, SPLASH_DISPLAY_LENGHT);
		}
	}
	
	public void gotoNext(View v){
		isPressNext = true;
		if(!SystemParameters.isOpen){
			Intent mainIntent = new Intent(MainActivity.this,RecordActivity.class);
			MainActivity.this.startActivity(mainIntent);
		}
		else
			MainActivity.this.finish();
	}
	
	private void findView(){
		bt_skip = (Button) findViewById(R.id.bt_skip);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_mainDescription = (TextView) findViewById(R.id.tv_mainDescription);
		tv_copyRight = (TextView) findViewById(R.id.tv_copyRight);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.finish();
		return;
	}

	
}
