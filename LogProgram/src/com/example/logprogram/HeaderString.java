package com.example.logprogram;


/**************  
 * Store all Header String
 ******************/

public class HeaderString {
	
	public static String GpsHeaderString = 
		   "#######################################################################################\n"
		   +"# GPS Tracking Log File                                                               #\n"
           +"# Format: L.Timestamp,D.Latitude,D.Longtitude,D.Height,F.Speed,F.Bearing,F.HDOP,F.VDOP,F.PDOP<CR><LF> #\n"                                                   
           +"#######################################################################################\n"
           ;
	
	public static String AccHeaderString =
		   "#######################################################################################\n"
		   +"# Accelerometer Log File                                                              #\n"
		   +"# Format: L.Timestamp,D.Gx,D.Gy,D.Gz,D.Gv<CR><LF>                                     #\n"
		   +"#######################################################################################\n"
		   ;
	
	public static String MagHeaderString =
		   "#######################################################################################\n"
		   +"# Magnetic Field Sensor Log File                                                      #\n"
		   +"# Format: L.Timestamp,D.Mx,D.My,D.Mz<CR><LF>                                          #\n"
		   +"#######################################################################################\n"
		   ;
	
	public static String AuxHeaderString = 
		   "#######################################################################################\n"
		   +"# Auxiliary Log File                                                                  #\n"
		   +"# Format: L.Timestamp,F.Speed,D.Gx_avg,D.Gy_avg,D.Gz_avg,D.deltaGv[],D.BigSigma,D.SmallSigma[]<CR><LF> #\n"
		   +"#######################################################################################\n"
		   ;
	
	public static String AnomHeaderString = 
		  "#######################################################################################\n"
		  +"# Anomaly Event Log File                                                              #\n"
		  +"# Format: L.Timestamp,D.Latitude,D.Longitude,D.Height,F.Speed,F.Bearing,D.AI,D.EventSigma,D.SmallSigma #\n"
		  +"#######################################################################################\n"
		  ;
			

}
