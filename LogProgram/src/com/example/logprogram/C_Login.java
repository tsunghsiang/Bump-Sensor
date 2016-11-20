package com.example.logprogram;

import static com.example.logprogram.Constant.CHINESE_FLAG;
import static com.example.logprogram.Constant.ENGLISH_FLAG;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class C_Login extends Activity {

	private CheckBox remLoginInfo;
	private EditText mMail, mPassword;
	int chinese = CHINESE_FLAG;
	int english = ENGLISH_FLAG;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_login);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //initialize each reference
        remLoginInfo = (CheckBox)findViewById(R.id.c_check_logininfo);
        mMail        = (EditText)findViewById(R.id.c_login_mail);
        mPassword    = (EditText)findViewById(R.id.c_login_pwd);
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
    
    public void facebookButton(View view){
    	
    }
    
    public void gmailButton(View view){
    	
    }    
	
    public void linkedinButton(View view){
    	
    }
    
    public void registerButton(View view){
    	startActivity(new Intent(this, C_Register.class));
    }
    
    public void unregisterButton(View view){
    	if(mMail.getText().toString().equals("") || mPassword.getText().toString().equals("")){
    		Toast.makeText(this, "註銷帳號前，請先確認信箱密碼已填寫完畢", Toast.LENGTH_LONG).show();
    	}else{
    		/*
    		 * (1)send (mail,pwd) to remote server
    		 * (2)check if the user does exist
    		 * (3)unregister the user
    		 * */
    	}
    }
    
    public void enterNavigation(View view){
    	
    	/*
    	 * if(the id is registered)
    	 * 		go to navigation page
    	 * else
    	 * 		go to registration page
     	 * */
    	Intent intent = new Intent(C_Login.this, NetworkMonitoring.class);
    	Bundle bundle = new Bundle();
    	bundle.putInt("interface", chinese);
    	intent.putExtras(bundle);
    	startService(intent);
    }
    
    private CheckBox.OnCheckedChangeListener cbListener = 
            new CheckBox.OnCheckedChangeListener(){
 
    	public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
        {
    			
        }
    	
    };
}