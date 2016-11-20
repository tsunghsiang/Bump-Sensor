package com.example.logprogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

/**************  
 * All the file writer, include:
 * 1. Gps Log File Writer
 * 2. Accelerometer Log File Writer
 * 3. Magnetic Log File Writer
 * 4. Extraction Algorithm Log File Writer
 * 5. Anomaly Log File Writer
 * 6. Readme Writer
 * *****************/

public class LogFileWriter {
	
	private String fileName;
	private FileOutputStream outputStream;
	
	private String attributeGps  = "TimeStamp,Latitude,Longitude,Height,Speed,Bearing,HDOP,VDOP,PDOP\n";
	private String attributeAcc  = "Timestamp,Gx,Gy,Gz,Gv\n";
	private String attributeMag  = "TimeStamp,Mx,My,Mz\n";
	private String attributeAux  = "Timestamp,Speed,Gx_avg,Gy_avg,Gz_avg,deltaGv,BigSigma,SmallSigma[0],SmallSigma[1],SmallSigma[2],"
									+ "SmallSigma[3],SmallSigma[4],SmallSigma[5],SmallSigma[6],SmallSigma[7],\n";
	private String attributeAnom = "TimeStamp,Latitude,Longitude,Height,Speed,Bearing,AI,EventSigma,SmallSigma\n";	

	//private Context context ;
	
	public LogFileWriter(Context context,String filename,int type){
		super();
		//this.context = context;
		this.fileName=filename;
		initialize(type);
		
	}
	
	public void writeTesting(Vector<Double> bin) throws IOException{
		String outputString = null;
		for(int i=0;i<bin.size();i++){
			outputString = outputString + bin +",";
		}
		outputStream.write(outputString.getBytes());
	}
	
	public void writeGpsFile(long timestamp, double lat, double lng, float speed, double height, float bearing,
							float HDOP,float VDOP, float PDOP) throws IOException{
    	String outputString = timestamp+","+lat+","+lng+","+height+","+speed+","+bearing+","
    							+HDOP+","+VDOP+","+PDOP+"\n";
    	outputStream.write(outputString.getBytes());
	}
	
    public void writeAccFile(VC vc) throws IOException {
		String outputString = vc.getTime()+","+vc.getGX()+","+vc.getGY()+","+vc.getGZ()+","+vc.getGV()+"\n";
		outputStream.write(outputString.getBytes());

    }
    
	public void writeMagFile(long timestamp, double Mx, double My, double Mz) throws IOException{
    	String outputString =  timestamp+","+Mx+","+My+","+Mz+"\n";
    	outputStream.write(outputString.getBytes());
	}
	
	 public void writeAuxFile(long timeStamp,float bearing,double gxBar, double gyBar, double gzBar,
			 						double smallS[],double deltaGv,float speed) throws IOException {	
		 //Log.d("LogFileWriter", "writeSecond " + numOfSec);

		String outputString = timeStamp+","+speed+","+bearing+","+gxBar+","+gyBar+","+gzBar+","+deltaGv+","+
								//bigS[0]+","+bigS[1]+","+bigS[2]+","+bigS[3]+","+
								//bigS[4]+","+bigS[5]+","+bigS[6]+","+
								smallS[0]+","+smallS[1]+","+smallS[2]+","+smallS[3]+","+
								smallS[4]+","+smallS[5]+","+smallS[6]+"\n";
		outputStream.write(outputString.getBytes());
	  }
	    
	public void  writeAnomFile(long timeStamp,double lat, double lng,float speed, double sE, 
							double smallS,double ai,double height,float bearing
							//,double deltaGv, double small,double peakGv
							) throws IOException {
		
		Log.d("LogFileWriter", "writeEvent");
	    	String outputString = timeStamp+","+lat+","+lng+","+height+","+speed+","+bearing+","+ai+","+sE+","+smallS
	    							//+","+deltaGv+","+small+","+peakGv
	    							+"\n";
	    	outputStream.write(outputString.getBytes());
	}
	
	public void writeReadMeFile() throws IOException{
		int AccRate = SystemParameters.AccCount/SystemParameters.totalMinute;
		int MagRate = SystemParameters.MagCount/SystemParameters.totalMinute;
		
		
		String outputString = 
				"File Format Version: 1.0\r\n"
				+"File List:\r\n"
				+"\tReadMe.txt This file\r\n"
				+"\tGPSFile.csv GPS log file ("+SystemParameters.GpsCount+" records @ 1Hz)\r\n"
				+"\tAccFile.csv Accelerometer log file ("+SystemParameters.AccCount+" records @ "+AccRate+"Hz)\r\n"	
				+"\tMagFile.csv Magnetic field sensor log file ("+SystemParameters.MagCount+" records @ "+MagRate+"Hz)\r\n"
				+"\tAuxFile.csv Extraction Algorithm log file ("+SystemParameters.AuxCount+" records @ 1Hz)\r\n"
				+"\tAnomFile.csv Anomaly event log file ("+SystemParameters.AnomCount+" events records)\r\n"
				+"GPSFile.csv Record Format:\r\n"
				+"\tL.Timestamp,D.Latitude,D.Longtitude,D.Height,F.Speed,F.Bearing,F.HDOP,F.VDOP,F.PDOP<CR><LF>\r\n"	
				+"AccFile.csv Record Format:\r\n" 
				+"\tL.Timestamp,D.Gx,D.Gy,D.Gz,D.Gv<CR><LF>\r\n"
				+"MagFile.csv Record Format:\r\n" 
				+"\tL.Timestamp,D.Mx,D.My,D.Mz<CR><LF>\r\n"
				+"AuxFile.csv Record Format:\r\n"
				+"\tL.Timestamp,F.Speed,F.Bearing,D.Gx_avg,D.Gy_avg,D.Gz_avg,D.deltaGv,D.SmallSigma[]<CR><LF>\r\n"
				+"AnomFile.csv Record Format:\r\n"
				+"\tL.Timestamp,D.Latitude,D.Longitude,D.Height,F.Speed,F.Bearing,D.AI,D.EventSigma,D.SmallSigma<CR><LF>\r\n"
				+"Data Collector: "+SystemParameters.Collecter	+"\r\n"
				+"Vehicle Model: "+SystemParameters.VehicleModel +"\r\n"
				+"Smartphone Model: "+SystemParameters.PhoneModel +"\r\n"
				+"VID (Plate Number): "	+SystemParameters.VID	+"\r\n"
				+"IMEI: "	+SystemParameters.SID	+"\r\n"
				+"Mounting Method: "+SystemParameters.MountingMethod +"\r\n"
				+"Mounting Location: "+SystemParameters.MountingLocation+"\r\n"
				+"Start Date/Time: "+SystemParameters.StartDate	+"\r\n"	
				+"Duration: "+SystemParameters.Duration	+"\r\n"	
				+"Start Location: "	+SystemParameters.StartLat+", "+SystemParameters.StartLng	+"\r\n"
				+"End Location: " +SystemParameters.EndLat+", "+SystemParameters.EndLng	+"\r\n"	
				+"Mileage: " +SystemParameters.Mileage	+" KM\r\n"	
				+"Memo: "+SystemParameters.Memo+"\r\n"		
				+"C1: "+SystemParameters.C1 + "\r\n"
				+"C2: "+SystemParameters.C2 + "\r\n"
				;

		outputStream.write(outputString.getBytes());
	}
	

	public void closefile()
	{
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initialize(int type) {
		Log.d("LogFileWriter", "initialize begin");
		
		if(isExternalStorageWritable()){
			
			String path = Environment.getExternalStorageDirectory().getPath();

		    Time t=new Time();
		    t.setToNow();
		    String year = String.valueOf(t.year);
		    String month = String.valueOf(t.month+1);
		    String day = String.valueOf(t.monthDay);
		    String hour = String.valueOf(t.hour);
		    String minute = String.valueOf(t.minute);
		    String second = String.valueOf(t.second);
		    
		    month = "00".substring(0, 2 - month.length()) + month;
		    day = "00".substring(0, 2 - day.length()) + day;
		    hour = "00".substring(0, 2 - hour.length()) + hour;
		    minute = "00".substring(0, 2 - minute.length()) + minute;
		    second = "00".substring(0, 2 - second.length()) + second;
		    		  		    
		    //YYYYMMDD-HHMMSS
		    String date = year+month+day+"-"+hour+minute+second;
		    //String date = "234";
		    File dir;
		    File file;
		    
		    //統一folder的名字
		    if(type==1){
		    	SystemParameters.filename = date;
			
		    	dir = new File(path + "/NOL/BumpsOnRoads/"+date+"/");
		    	if (!dir.exists()){
		    		Log.d("LogFileWriter", year+month+day+"-"+hour+minute+second);
		    		dir.mkdirs();
		    	}

		    	String fileNametemp = date+"-"+fileName; 
			   
		    	file = new File(path + "/NOL/BumpsOnRoads/"+date+"/", fileName);
		    }
		    else{
		    	dir = new File(path + "/NOL/BumpsOnRoads/"+SystemParameters.filename+"/");
		    	if (!dir.exists()){
		    		Log.d("LogFileWriter", year+month+day+"-"+hour+minute+second);
		    		dir.mkdirs();
		    	}

		    	String fileNametemp = date+"-"+fileName; 
			   
		    	file = new File(path + "/NOL/BumpsOnRoads/"+SystemParameters.filename+"/", fileName);
		    }
			try {
				outputStream = new FileOutputStream(file);
				//initializeAttribute(type);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
			Log.d("LogFileWriter", "No Free Space to Create File");
	}
	
	private void initializeAttribute(int type) throws IOException{
		if(type == 1){
			outputStream.write(HeaderString.GpsHeaderString.getBytes());
			outputStream.write(attributeGps.getBytes());
		}
		else if(type == 2){
			outputStream.write(HeaderString.AccHeaderString.getBytes());
			outputStream.write(attributeAcc.getBytes());
		}
		else if(type == 3){
			outputStream.write(HeaderString.MagHeaderString.getBytes());
			outputStream.write(attributeMag.getBytes());
		}
		else if(type == 4){ 
			outputStream.write(HeaderString.AuxHeaderString.getBytes());
			outputStream.write(attributeAux.getBytes());
		}
		else if(type == 5){ 
			outputStream.write(HeaderString.AnomHeaderString.getBytes());
			outputStream.write(attributeAnom.getBytes());
		}
	}
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	


}
