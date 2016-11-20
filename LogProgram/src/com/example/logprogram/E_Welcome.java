package com.example.logprogram;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.WindowManager;
import android.widget.Toast;

public class E_Welcome extends Activity{

	private CountDownTimer waitTimer;
	boolean mIsBound;
	Messenger mService = null;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	//deal with messages from the service
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case NetworkChecking.REGISTER:
	        	
	        	break;
	        case NetworkChecking.UNREGISTER:
	        	
	        	break;
	        case NetworkChecking.CHINESE_INTERFACE:
	            
	            break;
	        case NetworkChecking.ENGLISH_INTERFACE:
	            
	            break;
	        default:
	            super.handleMessage(msg);
	        }
	    }
	}	
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, NetworkChecking.ENGLISH_INTERFACE);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
            Toast.makeText(getApplicationContext(), "Service Attached.", Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            Toast.makeText(getApplicationContext(), "Service Disconnected.", Toast.LENGTH_SHORT).show();
        }
    };    
    
    public void doBindService() {
    	Intent bindIntent = new Intent(getApplication(), NetworkChecking.class);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Toast.makeText(this, "Bind Service.", Toast.LENGTH_SHORT).show();
    }
    
    public  void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, NetworkChecking.UNREGISTER);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            Toast.makeText(getApplicationContext(), "Service Unbinding....", Toast.LENGTH_SHORT).show();
        }
    }        
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.e_welcome);
        //set screen "on" when running the program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

    //destroy the activity
    protected void onDestroy(){
        super.onDestroy();
        doUnbindService();
    }
    
    protected void onStart(){
    	super.onStart();
    }
    
    protected void onResume(){
    	super.onResume();
		waitTimer = new CountDownTimer(1000, 1000){

			@Override
			public void onFinish() {
				startService(new Intent(E_Welcome.this, NetworkChecking.class));
				doBindService();
			}
			
			@Override
			public void onTick(long millisUntilFinished) {}
		
		}.start();    	
    }
    
    protected void onPause(){
    	super.onPause();
    }
    
    protected void onStop(){
    	super.onStop();
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }	    	
    }	
	
}
