package com.example.logprogram;

import android.text.format.Time;

/**************  
 * Event Object
 ******************/

public class Event
{
	private double latitude;
	private double longitude;
	private double UTCtime;
	private String time;
	private float speed;
	private double ai;

	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getUTCtime() {
		return UTCtime;
	}
	
	public void setUTCtime(double uTCtime) {
		UTCtime = uTCtime;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public double getAI() {
		return ai;
	}
	public void setAI(double ai) {
		this.ai = ai;
	}

	
}

