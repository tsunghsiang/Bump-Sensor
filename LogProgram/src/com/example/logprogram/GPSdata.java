package com.example.logprogram;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import android.util.Log;

public class GPSdata {
	private long timestamp;
	private double lat;
	private double lng;
	private double height;
	
	GPSdata(){
		
	}
	
	public void setTimestamp(long timestamp){
		this.timestamp = timestamp;
	}
	
	public long getTimestamp(){
		return this.timestamp;
	}
	
	public void setLat(double lat){
		this.lat = lat;
	}
	
	public double getLat(){
		return this.lat;
	}
	
	public void setLng(double lng){
		this.lng = lng;
	}
	
	public double getLng(){
		return this.lng;
	}
	
	public void setHeight(double height){
		this.height = height;
	}
	
	public double getHeight(){
		return this.height;
	}
	
	public double getExactLat(LinkedList<GPSdata> gpsdata, long timestamp,double ratio){
		double exactLat =0;
		for(int i=0;i<gpsdata.size()-1;i++){
			Log.d("GPSdata2",gpsdata.get(i).getTimestamp()+" "+gpsdata.get(i).getLat());
			if((gpsdata.get(i).getTimestamp() <= timestamp) && (gpsdata.get(i+1).getTimestamp() >= timestamp))
			{
				exactLat = (ratio* (gpsdata.get(i+1).getLat() - gpsdata.get(i).getLat())) + gpsdata.get(i).getLat();
				Log.d("GPSdata",gpsdata.get(i+1).getLat()+" " +gpsdata.get(i).getLat());
			}
		}
		Log.d("GPSdata2","--------------------");
		return exactLat;
	}
	
	public double getExactLng(LinkedList<GPSdata> gpsdata, long timestamp,double ratio){
		double exactLng =0;
		for(int i=0;i<gpsdata.size()-1;i++){
			if((gpsdata.get(i).getTimestamp() <= timestamp) && (gpsdata.get(i+1).getTimestamp() >= timestamp))
			{
				exactLng = (ratio* (gpsdata.get(i+1).getLng() - gpsdata.get(i).getLng())) + gpsdata.get(i).getLng();
			}
		}
		return exactLng;
	}
	
	public double getExactHeight(LinkedList<GPSdata> gpsdata, long timestamp,double ratio){
		double exactHeight =0;
		for(int i=0;i<gpsdata.size()-1;i++){
			if((gpsdata.get(i).getTimestamp() <= timestamp) && (gpsdata.get(i+1).getTimestamp() >= timestamp))
			{
				exactHeight = (ratio* (gpsdata.get(i+1).getHeight() - gpsdata.get(i).getHeight())) + gpsdata.get(i).getHeight();
			}
		}
		return exactHeight;
	}


	
}
