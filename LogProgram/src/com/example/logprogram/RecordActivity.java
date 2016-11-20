package com.example.logprogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**************  
 * The MAIN Activity that show many information:
 * 1. Gx, Gy, Gz
 * 2. Lat,Lng
 * 3. OnRackTest status
 * 4. Service status
 * 5. File records counter
 * 
 * Include 3 buttons:
 * 1. Start/Stop Log Service Button
 * 2. Map Button : will show the detected event on google map
 * 3. Setting Button :  can find in menu
 * *****************/

public class RecordActivity extends FragmentActivity  implements SensorEventListener{
	private LocationManager mgr;
	private String best;

	//Button
	private Button LogButton;
	
	//Text View
	private TextView tv_durationTime;
	
	public TextView tv_onracktestresult;
	public TextView tv_speedresult;	
	public TextView tv_timeresult;
	private TextView tv_latresult;
	private TextView tv_lngresult;
	
	private TextView tv_SmallS1;
	private TextView tv_SmallS2;
	private TextView tv_SmallS3;
	private TextView tv_SmallS4;	
	private TextView tv_SmallS5;
	private TextView tv_SmallS6;
	private TextView tv_SmallS7;
	private TextView tv_SmallS8;

	private TextView tv_numberofbumpingresult;
	private TextView tv_timeEventresult ;
	private TextView tv_latEventresult ;
	private TextView tv_lngEventresult ;
	private TextView tv_speedEventresult;
	private TextView tv_airesult  ;
	private TextView tv_speedunit;
	private TextView tv_speedunit2;
	private TextView tv_title_BumpingEvent;


	//count down timer
	private Long startTime;
	private Handler handler = new Handler();
	private int now=0;
	
	
	private VCE vce ;
	private SettingActivity settingActivity;

	//gps 
	private double lat;
	private double lng;
	private float speed;
	private long time;
	private double height;
	private float bearing;
	private float HDOP;
	private float VDOP;
	private float PDOP;
	private int numOfSec;
	private long numOfSeclong;

	//flag
	private boolean isInitialize = false;
	private boolean isClick = false;
	private boolean isFirstGps = true;
	private boolean isReadFile = false;// not read = false   , read = true
	private boolean isFirstRead = true;
	private boolean isGpsDataReady = false;
	private boolean isReadOldFormat = false; // new format = false , old format = true
	private boolean isReadFileFunction = false;//
	
	//file writer
	private LogFileWriter GpsWriter;
	private LogFileWriter MagWriter;
	private LogFileWriter ReadmeWriter;
	
	//for read input use
	private BufferedReader br;
	private BufferedReader brGPS;
	private int lastSecond = 0;
	private long lastSecondlong = 0;
	private long checkSec;
	private boolean isSecondRead = false;
	
	/* Menu */
	protected static final int MENU_INTRODUCTION = Menu.FIRST ;
	protected static final int MENU_SETTING = Menu.FIRST+1; 
	protected static final int MENU_EXIT = Menu.FIRST + 2;
	
	//for count sampling rate
	private int totalMinute = 0;
	private float totalDistance = 0;
	private double startLat =0;
	private double startLng =0;
	int tt=0;
	
    private double eventCountp =0;
    private int eventCountDown =3;
    
	private FileOutputStream outputStream;
	long previousTimestamp=0;
	long previousTimestampM=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		//initialize glocal variable
		initializeGlobal();
		makeCacheDir();
		
		try {
			loadInformation();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		findview();
		LogButton.setOnClickListener(LogStartClickListener);
		
		/************************** startGPS ***********************************/
	
		mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		best = mgr.getBestProvider(criteria, true);
		Location location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (best != null)
			location = mgr.getLastKnownLocation(best);
		mgr.requestLocationUpdates("gps", 0, 0, locationlistener); // 讓locationlistener處理資料有變化時的事情
		
		
		mgr.addGpsStatusListener(GPSstatusListener);//to get GPS status
		mgr.addNmeaListener(NmeaListener);
	
		/************************** start sensor ******************************/
	
		//accelerometer
		SensorManager sensorManager;
		sensorManager=(SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
	
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		
		//magnetic 
		SensorManager mSensorManager;
		Sensor mSensor;
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		mSensorManager.registerListener(this, 
				mSensor, SensorManager.SENSOR_DELAY_FASTEST);
		
		//get IMEI
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		SystemParameters.SID = telephonyManager.getDeviceId();
		
		/************************** File Writer **********************************/
		
		/*
		GpsWriter = new LogFileWriter(this,"GPSFile.csv",1);
		MagWriter = new LogFileWriter(this,"MagFile.csv",3);
		ReadmeWriter = new LogFileWriter(this,"ReadMe.txt",6);*/
		
		
		/************************** read input *********************************/
		if(isReadFile){
			File sdcard = Environment.getExternalStorageDirectory();

			//Get the text file
			//File file = new File(sdcard,"desire_ep_20120605_vc_in.csv");
			//File fileGPS = new File(sdcard,"desire_ep_20120605_gps_fix.csv");
			
			//File file = new File(sdcard,"NCTU-desire-R1_vc_in.csv");
			//File fileGPS = new File(sdcard,"NCTU-desire-R1_gps_fix.csv");
			
			File file = new File(sdcard,"AccFile.csv");
			File fileGPS = new File(sdcard,"GPSFile.csv");
		
			try {
				br = new BufferedReader(new FileReader(file));
				brGPS = new BufferedReader(new FileReader(fileGPS));

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/************************** VCE ****************************************/
		/*
		vce = new VCE(this);           
		vce.AccWriter = new LogFileWriter(this,"AccFile.csv",2);
		vce.AuxWriter = new LogFileWriter(this,"AuxFile.csv",4);
		vce.AnomWriter = new LogFileWriter(this,"AnomFile.csv",5);	*/
		
		

	}
	
	private void newFileWriter(){
		GpsWriter = new LogFileWriter(this,"GPSFile.csv",1);
		MagWriter = new LogFileWriter(this,"MagFile.csv",3);
		ReadmeWriter = new LogFileWriter(this,"ReadMe.txt",6);
		vce = new VCE(this);           
		vce.AccWriter = new LogFileWriter(this,"AccFile.csv",2);
		vce.AuxWriter = new LogFileWriter(this,"AuxFile.csv",4);
		vce.AnomWriter = new LogFileWriter(this,"AnomFile.csv",5);	
	}
	
	private void initializeGlobal(){
		SystemParameters.GpsCount  = 0;
		SystemParameters.AuxCount  = 0;
		SystemParameters.AccCount  = 0;
		SystemParameters.MagCount  = 0;
		SystemParameters.AnomCount = 0;
		
		SystemParameters.StartLat = "";
		SystemParameters.StartLng = "";
		SystemParameters.EndLat   = "";
		SystemParameters.EndLng   = "";
		
		SystemParameters.StartDate = " ";
		SystemParameters.Duration  = " ";
		SystemParameters.Mileage = "";
		
		SystemParameters.isServiceRunning = false;
		SystemParameters.isOnRack = 1;
		SystemParameters.isSetting = false;
	}
	
	private void makeCacheDir(){
		String path = Environment.getExternalStorageDirectory().getPath();
	    File dir;
	    File file;	    
	    String fileName = "cache.log";
		dir = new File(path + "/NOL/BumpsOnRoads/cache");
    	if (!dir.exists()){ 
    		
    		dir.mkdirs();
	    	file = new File(path + "/NOL/BumpsOnRoads/cache", fileName);

			String outputString = " / / / / / /5.0/2.5/ ";

			try {
				outputStream = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				outputStream.write(outputString.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} 	
		
	}
	
	private void loadInformation() throws IOException{
		String path = Environment.getExternalStorageDirectory().getPath();
	    File dir = new File(path + "/NOL/BumpsOnRoads/cache");

		//Get the text file
		File file = new File(dir,"cache.log");
		BufferedReader br = null;
	
		try {
			br = new BufferedReader(new FileReader(file));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String Line;
		Line = br.readLine();
		StringTokenizer token = new StringTokenizer(Line, "/");
		SystemParameters.Collecter = token.nextToken().toString();
		SystemParameters.VID = token.nextToken().toString();
		SystemParameters.PhoneModel = token.nextToken().toString();
		SystemParameters.VehicleModel = token.nextToken().toString();
		SystemParameters.MountingMethod = token.nextToken().toString();
		SystemParameters.MountingLocation = token.nextToken().toString();
		SystemParameters.C1= Double.valueOf(token.nextToken().toString()).doubleValue();
		SystemParameters.C2= Double.valueOf(token.nextToken().toString()).doubleValue();
		SystemParameters.Memo = token.nextToken().toString();
		
	}
	
	private void findview(){
		LogButton = (Button) findViewById(R.id.bt_log);
		tv_durationTime = (TextView) findViewById(R.id.tv_durationtimeresult);	
		
		tv_onracktestresult = (TextView) findViewById(R.id.tv_onracktestresult);
		tv_speedresult = (TextView) findViewById(R.id.tv_speedresult);
		tv_timeresult = (TextView) findViewById(R.id.tv_timeresult);
		tv_latresult = (TextView) findViewById(R.id.tv_latresult);
		tv_lngresult = (TextView) findViewById(R.id.tv_lngresult);
		tv_speedunit = (TextView) findViewById(R.id.tv_speedunit);
		tv_speedunit2 = (TextView) findViewById(R.id.tv_speedunit2);
		
		tv_SmallS1 = (TextView) findViewById(R.id.tv_smallS1);
		tv_SmallS2 = (TextView) findViewById(R.id.tv_smallS2);
		tv_SmallS3 = (TextView) findViewById(R.id.tv_smallS3);
		tv_SmallS4 = (TextView) findViewById(R.id.tv_smallS4);
		tv_SmallS5 = (TextView) findViewById(R.id.tv_smallS5);
		tv_SmallS6 = (TextView) findViewById(R.id.tv_smallS6);
		tv_SmallS7 = (TextView) findViewById(R.id.tv_smallS7);
		tv_SmallS8 = (TextView) findViewById(R.id.tv_smallS8);

		tv_numberofbumpingresult = (TextView) findViewById(R.id.tv_numberofbumpingresult);
		tv_timeEventresult = (TextView) findViewById(R.id.tv_timeEventresult);
		tv_latEventresult = (TextView) findViewById(R.id.tv_latEventresult);
		tv_lngEventresult = (TextView) findViewById(R.id.tv_lngEventresult);
		tv_speedEventresult = (TextView) findViewById(R.id.tv_speedEventresult);
		tv_airesult = (TextView) findViewById(R.id.tv_airesult);

		tv_title_BumpingEvent = (TextView) findViewById(R.id.tv_title_BumpingEvent);
	}

	private final LocationListener locationlistener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location){
			//Log.d("MapActivity", "onLocationChanged");
			lat = location.getLatitude();
			lng = location.getLongitude();	
			speed =(float) (location.getSpeed()*3.6);
			time = location.getTime();	
			
			time = location.getTime();
			//SystemParameters.offset = System.currentTimeMillis() - time;
			isGpsDataReady = true;

			if(isClick){
				if(isFirstGps){
					//time = location.getTime();
					//SystemParameters.offset = System.currentTimeMillis() - time;
					//Log.d("MapActivity", "time: " +time +" systen: " +System.currentTimeMillis() + " offset: " +SystemParameters.offset);
					SystemParameters.offset = System.currentTimeMillis() - time;
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMaximumFractionDigits( 6 ); 
					nf.setMinimumFractionDigits(6);	
					String latS = nf.format( lat);
					String lngS = nf.format( lng);
					tv_speedunit.setText("km/h");
					
					NumberFormat nf1 = NumberFormat.getInstance();
					nf1.setMaximumFractionDigits( 0 ); 
					nf1.setMinimumFractionDigits(0);	
					String speedS = nf1.format( speed);

					tv_latresult.setText(latS);
					tv_lngresult.setText(lngS);
					tv_speedresult.setText(speedS);
					tv_timeresult.setText(String.valueOf(time));
					SystemParameters.StartLat = latS;
					SystemParameters.StartLng = lngS;
					startLat = lat;
					startLng = lng;
					isFirstGps = false;
				}

				lat = location.getLatitude();
				lng = location.getLongitude();
				speed = (float) (location.getSpeed()*3.6);
				time = location.getTime();	
				height = location.getAltitude();
				bearing = location.getBearing();	
				tt++;
				/*
				if(tt>5){
					float results[] = new float[3];
					Location.distanceBetween(startLat,startLng, lat, lng, results);
					if(results[0]<9)
						;
					else
						totalDistance += results[0];
					tt=0;
					
				}
				 */
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits( 6 ); 
				nf.setMinimumFractionDigits(6);	
				
				String latS = nf.format( lat);
				String lngS = nf.format( lng);
				
				NumberFormat nf1 = NumberFormat.getInstance();
				nf1.setMaximumFractionDigits( 0 ); 
				nf1.setMinimumFractionDigits(0);	
				
				String speedS = nf1.format( speed);

				tv_latresult.setText(latS);
				tv_lngresult.setText(lngS);
				tv_speedresult.setText(speedS);
				tv_timeresult.setText(String.valueOf(time));


				try {
					GpsWriter.writeGpsFile(time, lat, lng, speed, height, bearing,HDOP,VDOP,PDOP);
					//GpsWriter.writeGpsFile(time, lat, lng, speed, height, bearing);
					SystemParameters.GpsCount++;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
	
	private final GpsStatus.Listener GPSstatusListener = new GpsStatus.Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			
		} 
		
	};
	
	private final GpsStatus.NmeaListener NmeaListener = new GpsStatus.NmeaListener(){

		@Override
		public void onNmeaReceived(long timestamp, String nmea) {
			// TODO Auto-generated method stub
			if (nmea == null)
				return;
			int searchGSA = nmea.indexOf("GPGSA");
			if (searchGSA != -1)
			{
				Log.d("nmea",nmea);
				String[] nmeaData = nmea.split(",");

				String PDOPtemp = nmeaData[15];
				String HDOPtemp = nmeaData[16];
				String VDOPtemp = nmeaData[17];
				
				if(PDOPtemp.equals(""))
					PDOP = 0;
				else
					PDOP = Float.parseFloat(PDOPtemp);
				
				if(HDOPtemp.equals(""))
					HDOP = 0;
				else
					HDOP = Float.parseFloat(HDOPtemp);
				
				String[] vdoptemp = VDOPtemp.split("\\*");
				
				if(vdoptemp[0].equals(""))
					VDOP = 0;
				else
					VDOP = Float.parseFloat(vdoptemp[0]);	
				
				Log.d("nmea",PDOP+" , "+HDOP+" , "+VDOP);
				// datum.setGSA(nmea.trim());
			}
			return;	
		}
		
	};
	
	private Runnable updateTimer = new Runnable() {
		public void run() {
			Long spentTime = System.currentTimeMillis() - startTime;
			//計算目前已過分鐘數
			Long minius = (spentTime/1000)/60;
			//計算目前已過秒數
			Long seconds = (spentTime/1000) % 60;
			
			Long hour = minius / 60 ;
			
			String hourS = "00".substring(0, 2 -String.valueOf(hour).length()) + String.valueOf(hour);
			String miniusS = "00".substring(0, 2 - String.valueOf(minius).length()) + String.valueOf(minius);
			String secondS  = "00".substring(0, 2 - String.valueOf(seconds).length()) + String.valueOf(seconds);
				
			tv_durationTime.setText( hourS+":"+miniusS+":"+secondS);
			SystemParameters.Duration = hourS+":"+miniusS+":"+secondS;
			handler.postDelayed(this, 1000);
			
			//something that one second do once
			now++;
			totalMinute++;
			SystemParameters.totalMinute = totalMinute;
			totalDistance += (speed/3.6);
			
			setSmallSigma();
			setBumpingEvent();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{		
		
		super.onCreateOptionsMenu(menu);	
		menu.add(Menu.NONE, MENU_SETTING, 0, R.string.action_settings);
		menu.add(Menu.NONE, MENU_INTRODUCTION, 0, R.string.action_introduction);
		menu.add(Menu.NONE, MENU_EXIT, 0, R.string.action_exit);	

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case MENU_SETTING:
			gotoSetting();			
			//MainActivity.this.finish();
			break;
		case MENU_EXIT:
			finish();
			break;
		case MENU_INTRODUCTION:
			gotoIntroduction();
			break;
		default:		
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void gotoSetting()
	{
		startActivity(new Intent().setClass(RecordActivity.this, SettingActivity.class));
		return;
	}
	
	private void gotoIntroduction()
	{
		SystemParameters.isOpen = true;
		startActivity(new Intent().setClass(RecordActivity.this, MainActivity.class));
		return;
	}
	
	public void mapClick(View v){
		Intent mainIntent = new Intent(RecordActivity.this,MapActivity.class);
		RecordActivity.this.startActivity(mainIntent);
	}
	
	public void showLogInformationDialog(){//and also write readme.txt
		
		try {
			ReadmeWriter.writeReadMeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecordActivity.this);
		alertDialogBuilder.setTitle("Log Information")
						.setMessage("Collector: " +SystemParameters.Collecter +"\n"
								+"Plate Number: " + SystemParameters.VID +"\n"
								+"Memo: " +SystemParameters.Memo+"\n\n"
								+"Duration: " + SystemParameters.Duration + "\n"
								+"Mileage: "+SystemParameters.Mileage+" (KM)\n\n"
								+"GpsFile: "+SystemParameters.GpsCount+" records\n"
								+"AccFile: "+SystemParameters.AccCount+" records\n"
								+"MagFile: "+SystemParameters.MagCount+" records\n"
								+"AuxFile: "+SystemParameters.AuxCount+" records\n"
								+"AnomFile: "+SystemParameters.AnomCount+" records\n\n"
								+"6 files are created.")
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, close
								// current activity
								//MainActivity.this.finish();
								RecordActivity.this.finish();
								
								//Intent intent = new Intent(RecordActivity.this, SettingActivity.class);
								//startActivity(intent);
							}
						  })
						  /*
						  .setNegativeButton("See the result on map",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, close
								// current activity
								//MainActivity.this.finish();

								
								Intent intent = new Intent(RecordActivity.this, MapActivity.class);
								startActivity(intent);
								RecordActivity.this.finish();
							}
						  })*/
						  
						.show();
		
		
	}
	
	private void readfile(){
		long timestamp=0;
		double gx=0,gy=0,gz=0;
		while(true){
			
		String strLine;
		StringTokenizer st = null;
	   	try {
	   		if(isFirstRead){
	   			
				String strGPSLine;
				StringTokenizer stGPS = null;

			    strGPSLine = brGPS.readLine();
			    stGPS = new StringTokenizer(strGPSLine, ",");
			    	
			    time=Long.parseLong(stGPS.nextToken());
			    lat = Double.parseDouble(stGPS.nextToken());
			    lng = Double.parseDouble(stGPS.nextToken());
			    height =Double.parseDouble(stGPS.nextToken());
			    speed = Float.parseFloat(stGPS.nextToken());
			    bearing = Float.parseFloat(stGPS.nextToken());
			    
			    numOfSeclong = time/1000;
			    lastSecondlong = numOfSeclong;
			    

	   		}
	   		
	    	strLine = br.readLine();
	    	st = new StringTokenizer(strLine, ",");

			timestamp = Long.parseLong(st.nextToken()); //timestamp
			gx = Double.parseDouble(st.nextToken());
			gy = Double.parseDouble(st.nextToken());
			gz = Double.parseDouble(st.nextToken());
			double gv = Double.parseDouble(st.nextToken());
			long tmpNumOfSeclong = timestamp/1000; //#sec   			
			
	    	if(timestamp/1000 == checkSec){
	    		now++;
	    		Log.d("MapActivity","now: "+now );

	    		if(tmpNumOfSeclong >=numOfSeclong){
	    			Log.d("MapActivity","here" );
	    			
	    			if(isSecondRead){
	    				String strGPSLine;
	    				StringTokenizer stGPS = null;
	    				strGPSLine = brGPS.readLine();
	    				stGPS = new StringTokenizer(strGPSLine, ",");
			    	
	    				time=Long.parseLong(stGPS.nextToken());
	    				lat = Double.parseDouble(stGPS.nextToken());
	    				lng = Double.parseDouble(stGPS.nextToken());
					    height = Double.parseDouble(stGPS.nextToken());
					    speed = Float.parseFloat(stGPS.nextToken());
					    bearing = Float.parseFloat(stGPS.nextToken());	    				
	    				numOfSeclong = time/1000;
	    				
		    			if(lastSecond == numOfSec){
		    				String strGPSLine1;
		    				StringTokenizer stGPS1 = null;
		    				strGPSLine1 = brGPS.readLine();
		    				stGPS1 = new StringTokenizer(strGPSLine1, ",");
				    	
		    				time=Long.parseLong(stGPS1.nextToken());
		    				lat = Double.parseDouble(stGPS1.nextToken());
		    				lng = Double.parseDouble(stGPS1.nextToken());
						    height = Double.parseDouble(stGPS1.nextToken());
						    speed = Float.parseFloat(stGPS1.nextToken());
						    bearing = Float.parseFloat(stGPS1.nextToken());	
		    				numOfSeclong = time/1000;

		    			}
		    			
		    			lastSecondlong = numOfSeclong;
	    			}

	    			isGpsDataReady = true;
			    }
			    
			    checkSec = timestamp/1000 + 1; 

	    	}
			
			if(isFirstRead){
				checkSec = timestamp/1000 + 1;
	   			isFirstRead = false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // skip first line
	   	
		try {
			if(isInitialize){
				//Log.d("MapActivity", "VCE start now");
				//Log.d("28",timestamp+"------------");
				if(previousTimestamp != timestamp)
					vce.VCExtractWithSpeed(gx,gy,gz,lat,lng,speed,timestamp,height,bearing); 
				previousTimestamp = timestamp;
			}
			else{
				//Log.d("MapActivity", "startInitial " + gx+" " +gy+" "+gz+" "+speed);
				//gX, gY, gZ, lat, lng, speed, timestamp
				//Toast.makeText(this, "initializing.....", Toast.LENGTH_SHORT).show();
				//Log.d("28",timestamp+"");
				if(previousTimestamp != timestamp)
					isInitialize = vce.initializationWithSpeed(gx,gy,gz,lat,lng,speed,timestamp,height,bearing);
				previousTimestamp = timestamp;
				isSecondRead = true;
				
				
				if(SystemParameters.isOnRack == 2){
					tv_onracktestresult.setText("True");
				}
				else if(SystemParameters.isOnRack == 3){
					tv_onracktestresult.setText("False");
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   	
		}

	}

	private Button.OnClickListener LogStartClickListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			//啟動服務
			// Intent intent = new Intent(MapActivity.this, ReadSensorService.class);
			// startService(intent);
			if(!SystemParameters.isServiceRunning){
				SystemParameters.isServiceRunning = true;
				newFileWriter();
				
				if(isReadFileFunction && isReadFile)
					readfile();
				
				Toast.makeText(getBaseContext(), "Log Service is Start", Toast.LENGTH_SHORT).show();			
				startTime = System.currentTimeMillis();
				//設定定時要執行的方法
				handler.removeCallbacks(updateTimer);
				//設定Delay的時間
				handler.postDelayed(updateTimer, 1000);
				isClick = true;
				LogButton.setText("Finish");
				LogButton.setBackgroundColor(Color.GREEN);

				tv_speedunit2.setText("km/h");
				
				//set time
			    Time t=new Time();
			    t.setToNow();
			    String year   = String.valueOf(t.year);
			    String month  = String.valueOf(t.month+1);
			    String day    = String.valueOf(t.monthDay);
			    String hour   = String.valueOf(t.hour);
			    String minute = String.valueOf(t.minute);
			    String second = String.valueOf(t.second);

			    //YYYYMMDD-HHMMSS			    
			    
			    month  = "00".substring(0, 2 - month.length()) + month;
			    day    = "00".substring(0, 2 - day.length()) + day;
			    hour   = "00".substring(0, 2 - hour.length()) + hour;
			    minute = "00".substring(0, 2 - minute.length()) + minute;
			    second = "00".substring(0, 2 - second.length()) + second;
			    
			    String date = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;

				SystemParameters.StartDate = date;
				
			}
			else{
				SystemParameters.isServiceRunning = false;
				SystemParameters.isEnd = true;
				
				Toast.makeText(getBaseContext(), "Log Service is Stop", Toast.LENGTH_SHORT).show();
				
				handler.removeCallbacks(updateTimer);
				isClick = false;	
				
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits( 6 ); 
				nf.setMinimumFractionDigits(6);	
				
				String latS = nf.format( lat);
				String lngS = nf.format( lng);

				SystemParameters.EndLat = latS;
				SystemParameters.EndLng = lngS;
				
				totalDistance = totalDistance/1000;
				
				NumberFormat nf3 = NumberFormat.getInstance();
				nf3.setMaximumFractionDigits( 3 ); 
				String totalDistanceS = nf.format( totalDistance);
				
				SystemParameters.Mileage = totalDistanceS;
				
				gotoSettingActivity();
				
				/*
				if(!SystemParameters.isSetting){
					AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(RecordActivity.this);
					alertDialogBuilder2.setTitle("Log Information")
								.setMessage("You haven't set the log information yet.\n"
										+ "Do you want to set the information?")
								.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {										
										Intent intent = new Intent(RecordActivity.this, SettingActivity.class);
										startActivity(intent);

										RecordActivity.this.finish();
									}
								  })
								 .setNegativeButton("No",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {	
										showLogInformationDialog();
									}
								  })
								.show();
				}
				else{*/
					
					//showLogInformationDialog();

				//}
			}
		}
	};
	
	private void gotoSettingActivity(){
		Intent intent = new Intent(RecordActivity.this, SettingEndActivity.class);
		startActivity(intent);

		RecordActivity.this.finish();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				//Log.d("SensorREader","now: "+now);
			double gx = 0,gy=0,gz=0;
			long timestamp=0;
			
			if(!isReadFile){
				//timestamp = System.currentTimeMillis() - SystemParameters.offset;
				//timestamp = System.currentTimeMillis();
				gx = event.values[0]; //sensor Gx
				gy = event.values[1]; //sensor Gy
				gz = event.values[2]; //sensor Gz	
				timestamp = (event.timestamp/1000000)-SystemParameters.offset;
				
			}
			
			if(isReadFile && isReadOldFormat && !isReadFileFunction){
				String strLine;
				StringTokenizer st = null;
			   	try {
			   		if(isFirstRead){
			   			strLine = br.readLine();//skip first line
			   			
						String strGPSLine;
						StringTokenizer stGPS = null;

					   	strGPSLine = brGPS.readLine();//skip first line

					    strGPSLine = brGPS.readLine();
					    stGPS = new StringTokenizer(strGPSLine, ",");
					    	
					    numOfSec=Integer.parseInt(stGPS.nextToken());
					    time=Long.parseLong(stGPS.nextToken());
					    lat = Double.parseDouble(stGPS.nextToken());
					    lng = Double.parseDouble(stGPS.nextToken());
					    speed = Float.parseFloat(stGPS.nextToken());
					    speed = (float)(speed*3.6);
					    
					    lastSecond = numOfSec;

			   		}
			   		
			    	strLine = br.readLine();
			    	st = new StringTokenizer(strLine, ",");

	    			int tempS = Integer.parseInt(st.nextToken()); //seq
	    			timestamp = Long.parseLong(st.nextToken()); //timestamp
	    			gx = Double.parseDouble(st.nextToken());
	    			gy = Double.parseDouble(st.nextToken());
	    			gz = Double.parseDouble(st.nextToken());
	    			int tmpNumOfSec = Integer.parseInt(st.nextToken()); //#sec
	    			
	    			
			    	if(timestamp/1000 == checkSec){
			    		now++;
			    		Log.d("MapActivity","now: "+now );

			    		if(tmpNumOfSec >=numOfSec){
			    			Log.d("MapActivity","here" );
			    			
			    			if(isSecondRead){
			    				String strGPSLine;
			    				StringTokenizer stGPS = null;
			    				strGPSLine = brGPS.readLine();
			    				stGPS = new StringTokenizer(strGPSLine, ",");
					    	
			    				numOfSec=Integer.parseInt(stGPS.nextToken());
			    				time=Long.parseLong(stGPS.nextToken());
			    				lat = Double.parseDouble(stGPS.nextToken());
			    				lng = Double.parseDouble(stGPS.nextToken());
			    				speed = Float.parseFloat(stGPS.nextToken());
			    				
			    				speed = (float)(speed*3.6);
			    				
				    			if(lastSecond == numOfSec){
				    				String strGPSLine1;
				    				StringTokenizer stGPS1 = null;
				    				strGPSLine1 = brGPS.readLine();
				    				stGPS1 = new StringTokenizer(strGPSLine1, ",");
						    	
				    				numOfSec=Integer.parseInt(stGPS1.nextToken());
				    				time=Long.parseLong(stGPS1.nextToken());
				    				lat = Double.parseDouble(stGPS1.nextToken());
				    				lng = Double.parseDouble(stGPS1.nextToken());
				    				speed = Float.parseFloat(stGPS1.nextToken());
				    				
				    				speed = (float)(speed*3.6);

				    			}
				    			
				    			lastSecond = numOfSec;
			    			}
	
			    			isGpsDataReady = true;
					    }
					    
					    checkSec = timestamp/1000 + 1; 
	
			    	}
	    			
	    			if(isFirstRead){
	    				checkSec = timestamp/1000 + 1;
			   			isFirstRead = false;
	    			}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // skip first line

			}
			
			else if(isReadFile && !isReadOldFormat && !isReadFileFunction){
				String strLine;
				StringTokenizer st = null;
			   	try {
			   		if(isFirstRead){
			   			
						String strGPSLine;
						StringTokenizer stGPS = null;

					    strGPSLine = brGPS.readLine();
					    stGPS = new StringTokenizer(strGPSLine, ",");
					    	
					    time=Long.parseLong(stGPS.nextToken());
					    lat = Double.parseDouble(stGPS.nextToken());
					    lng = Double.parseDouble(stGPS.nextToken());
					    height =Double.parseDouble(stGPS.nextToken());
					    speed = Float.parseFloat(stGPS.nextToken());
					    bearing = Float.parseFloat(stGPS.nextToken());
					    
					    numOfSeclong = time/1000;
					    lastSecondlong = numOfSeclong;
					    

			   		}
			   		
			    	strLine = br.readLine();
			    	st = new StringTokenizer(strLine, ",");

	    			timestamp = Long.parseLong(st.nextToken()); //timestamp
	    			gx = Double.parseDouble(st.nextToken());
	    			gy = Double.parseDouble(st.nextToken());
	    			gz = Double.parseDouble(st.nextToken());
	    			double gv = Double.parseDouble(st.nextToken());
	    			long tmpNumOfSeclong = timestamp/1000; //#sec   			
	    			
			    	if(timestamp/1000 == checkSec){
			    		now++;
			    		Log.d("MapActivity","now: "+now );

			    		if(tmpNumOfSeclong >=numOfSeclong){
			    			Log.d("MapActivity","here" );
			    			
			    			if(isSecondRead){
			    				String strGPSLine;
			    				StringTokenizer stGPS = null;
			    				strGPSLine = brGPS.readLine();
			    				stGPS = new StringTokenizer(strGPSLine, ",");
					    	
			    				time=Long.parseLong(stGPS.nextToken());
			    				lat = Double.parseDouble(stGPS.nextToken());
			    				lng = Double.parseDouble(stGPS.nextToken());
							    height = Double.parseDouble(stGPS.nextToken());
							    speed = Float.parseFloat(stGPS.nextToken());
							    bearing = Float.parseFloat(stGPS.nextToken());	    				
			    				numOfSeclong = time/1000;
			    				
				    			if(lastSecond == numOfSec){
				    				String strGPSLine1;
				    				StringTokenizer stGPS1 = null;
				    				strGPSLine1 = brGPS.readLine();
				    				stGPS1 = new StringTokenizer(strGPSLine1, ",");
						    	
				    				time=Long.parseLong(stGPS1.nextToken());
				    				lat = Double.parseDouble(stGPS1.nextToken());
				    				lng = Double.parseDouble(stGPS1.nextToken());
								    height = Double.parseDouble(stGPS1.nextToken());
								    speed = Float.parseFloat(stGPS1.nextToken());
								    bearing = Float.parseFloat(stGPS1.nextToken());	
				    				numOfSeclong = time/1000;

				    			}
				    			
				    			lastSecondlong = numOfSeclong;
			    			}
	
			    			isGpsDataReady = true;
					    }
					    
					    checkSec = timestamp/1000 + 1; 
	
			    	}
	    			
	    			if(isFirstRead){
	    				checkSec = timestamp/1000 + 1;
			   			isFirstRead = false;
	    			}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // skip first line

			}

			if(isClick && isGpsDataReady && !isReadFileFunction){
				try {
					if(isInitialize){
						//Log.d("MapActivity", "VCE start now");
						if(previousTimestamp != timestamp)
							vce.VCExtractWithSpeed(gx,gy,gz,lat,lng,speed,timestamp,height,bearing); 
						previousTimestamp = timestamp;
					}
					else{
						//Log.d("MapActivity", lat + " " +lng);
						//gX, gY, gZ, lat, lng, speed, timestamp
						//Toast.makeText(this, "initializing.....", Toast.LENGTH_SHORT).show();
						if(previousTimestamp != timestamp)
							isInitialize = vce.initializationWithSpeed(gx,gy,gz,lat,lng,speed,timestamp,height,bearing);
						previousTimestamp = timestamp;
						isSecondRead = true;
						
						
						if(SystemParameters.isOnRack == 2){
							tv_onracktestresult.setText("True");
						}
						else if(SystemParameters.isOnRack == 3){
							tv_onracktestresult.setText("False");
						}
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
			long timestamp = (event.timestamp/1000000) - SystemParameters.offset;
			float Mx = event.values[1];
			float My = event.values[2];
			float Mz = event.values[0];

			
			try {
				if(isClick){
					if(previousTimestampM != timestamp)
						MagWriter.writeMagFile(timestamp, Mx, My, Mz);
					previousTimestampM = timestamp;
					SystemParameters.MagCount++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void setBumpingEvent(){
		NumberFormat nf1 = NumberFormat.getInstance();
		nf1.setMaximumFractionDigits( 2 ); 
		nf1.setMinimumFractionDigits(2);	
		
		String AiS = nf1.format( SystemParameters.eventAI);
		String seS = nf1.format( SystemParameters.eventSigmaEvent);
		String ssS = nf1.format( SystemParameters.eventSmallSigma);
		
		NumberFormat nf2 = NumberFormat.getInstance();
		nf2.setMaximumFractionDigits( 0 ); 
		nf2.setMinimumFractionDigits(0);	
		
		String speedS = nf2.format( SystemParameters.eventSpeed);
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits( 6 ); 
		nf.setMinimumFractionDigits(6);	
		
		String latS = nf.format( SystemParameters.eventLat);
		String lngS = nf.format( SystemParameters.eventLng);
		
		
		tv_numberofbumpingresult.setText("# of bumping: "+String.valueOf(SystemParameters.AnomCount));
		tv_timeEventresult.setText(String.valueOf(SystemParameters.eventTime));
		tv_latEventresult.setText(latS);
		tv_lngEventresult.setText(lngS);
		tv_speedEventresult.setText(speedS);
		tv_airesult.setText(AiS+ "  ("+seS+"/"+ssS+")");
		
		checkBumpingEventChange();
		
	}
	

	
	private void setSmallSigma(){
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits( 4 ); 
		nf.setMinimumFractionDigits(4);	
		
		String SmallS1S = nf.format( SystemParameters.smallSArray[0]);
		String SmallS2S = nf.format( SystemParameters.smallSArray[1]);
		String SmallS3S = nf.format( SystemParameters.smallSArray[2]);
		String SmallS4S = nf.format( SystemParameters.smallSArray[3]);
		String SmallS5S = nf.format( SystemParameters.smallSArray[4]);
		String SmallS6S = nf.format( SystemParameters.smallSArray[5]);
		String SmallS7S = nf.format( SystemParameters.smallSArray[6]);
		//String SmallS8S = nf.format( SystemParameters.SmallS8);

		tv_SmallS1.setText(String.valueOf("[1]:"+SmallS1S));
		tv_SmallS2.setText(String.valueOf("[2]:"+SmallS2S));
		tv_SmallS3.setText(String.valueOf("[3]:"+SmallS3S));
		tv_SmallS4.setText(String.valueOf("[4]:"+SmallS4S));
		tv_SmallS5.setText(String.valueOf("[5]:"+SmallS5S));
		tv_SmallS6.setText(String.valueOf("[6]:"+SmallS6S));
		tv_SmallS7.setText(String.valueOf("[7]:"+SmallS7S));
		//tv_SmallS8.setText(String.valueOf("[8]:"+SmallS8S));
		
		checkSmallSigmaChange();
	}
	
	private void checkBumpingEventChange(){
		eventCountDown--;
		
		//initial
		if(eventCountDown == 0){
			tv_numberofbumpingresult.setBackgroundColor(Color.BLACK);
			tv_numberofbumpingresult.setTextColor(Color.WHITE);
			tv_title_BumpingEvent.setBackgroundColor(Color.BLACK);
			tv_title_BumpingEvent.setTextColor(Color.WHITE);
			eventCountDown =3;
		}
		
		//check
		if(eventCountp != SystemParameters.AnomCount){
			tv_numberofbumpingresult.setBackgroundColor(Color.CYAN);
			tv_numberofbumpingresult.setTextColor(Color.BLACK);
			tv_title_BumpingEvent.setBackgroundColor(Color.RED);
			tv_title_BumpingEvent.setTextColor(Color.WHITE);
		}
		
		//record
		eventCountp = SystemParameters.AnomCount;
	}
	
    private void checkSmallSigmaChange(){
    	//initial
    	
    	tv_SmallS1.setBackgroundColor(Color.BLACK); tv_SmallS1.setTextColor(Color.WHITE);
    	tv_SmallS2.setBackgroundColor(Color.BLACK); tv_SmallS2.setTextColor(Color.WHITE);
    	tv_SmallS3.setBackgroundColor(Color.BLACK); tv_SmallS3.setTextColor(Color.WHITE);
    	tv_SmallS4.setBackgroundColor(Color.BLACK); tv_SmallS4.setTextColor(Color.WHITE);
    	tv_SmallS5.setBackgroundColor(Color.BLACK); tv_SmallS5.setTextColor(Color.WHITE);
    	tv_SmallS6.setBackgroundColor(Color.BLACK); tv_SmallS6.setTextColor(Color.WHITE);
    	tv_SmallS7.setBackgroundColor(Color.BLACK); tv_SmallS7.setTextColor(Color.WHITE);
    	tv_SmallS8.setBackgroundColor(Color.BLACK); tv_SmallS8.setTextColor(Color.WHITE);    	
    	/*
	    	if (speed >=5 && speed < 15) {
	    		tv_SmallS1.setBackgroundColor(Color.CYAN);
	    		tv_SmallS1.setTextColor(Color.BLACK);
	    	} else if (speed >= 15 && speed < 30) {
	    		tv_SmallS2.setBackgroundColor(Color.CYAN);
	    		tv_SmallS2.setTextColor(Color.BLACK);
	    	} else if (speed >= 30 && speed < 45) {
	    		tv_SmallS3.setBackgroundColor(Color.CYAN);
	    		tv_SmallS3.setTextColor(Color.BLACK);
	    	} else if (speed >= 45 && speed < 60) {
	    		tv_SmallS4.setBackgroundColor(Color.CYAN);
	    		tv_SmallS4.setTextColor(Color.BLACK);
	    	} else if (speed >= 60 && speed < 75) {
	    		tv_SmallS5.setBackgroundColor(Color.CYAN);
	    		tv_SmallS5.setTextColor(Color.BLACK);
	    	} else if (speed >= 75 && speed < 90) {
	    		tv_SmallS6.setBackgroundColor(Color.CYAN);
	    		tv_SmallS6.setTextColor(Color.BLACK);
	    	} else if (speed >= 90 && speed < 105) {
	    		tv_SmallS7.setBackgroundColor(Color.CYAN);
	    		tv_SmallS7.setTextColor(Color.BLACK);
	    	} else if (speed >= 105 ) {
	    		tv_SmallS8.setBackgroundColor(Color.CYAN);
	    		tv_SmallS8.setTextColor(Color.BLACK);
	    	}	
	    	*/
	    	if (speed >=10 && speed < 30) {
	    		tv_SmallS1.setBackgroundColor(Color.CYAN);
	    		tv_SmallS1.setTextColor(Color.BLACK);
	    	} else if (speed >=30 && speed < 50) {
	    		tv_SmallS2.setBackgroundColor(Color.CYAN);
	    		tv_SmallS2.setTextColor(Color.BLACK);
	    	} else if (speed >=50 && speed < 70) {
	    		tv_SmallS3.setBackgroundColor(Color.CYAN);
	    		tv_SmallS3.setTextColor(Color.BLACK);
	    	} else if (speed >=70 && speed < 90) {
	    		tv_SmallS4.setBackgroundColor(Color.CYAN);
	    		tv_SmallS4.setTextColor(Color.BLACK);
	    	} else if (speed >=90 && speed < 110) {
	    		tv_SmallS5.setBackgroundColor(Color.CYAN);
	    		tv_SmallS5.setTextColor(Color.BLACK);
	    	} else if (speed >=110 && speed < 130) {
	    		tv_SmallS6.setBackgroundColor(Color.CYAN);
	    		tv_SmallS6.setTextColor(Color.BLACK);
	    	} else if (speed >= 130) {
	    		tv_SmallS7.setBackgroundColor(Color.CYAN);
	    		tv_SmallS7.setTextColor(Color.BLACK);
	    	}

    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		return;
	}

}
