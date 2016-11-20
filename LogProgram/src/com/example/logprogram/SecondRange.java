package com.example.logprogram;

import java.util.Vector;

/**************  
 * 1 second unit Object
 ******************/

public class SecondRange {
	private int numOfSec;
	private long timeStamp;
	private int from;
	private int to;
	private boolean isStable=false;
	private float speed;
	private double deltaGv;
	private double smallSigmaEvent;
	private double smallSigmaEvent2;
	private Vector<Double> gvList;
	
	public SecondRange(int numOfSec, long timeStamp) {
		this.numOfSec = numOfSec;
		this.timeStamp = timeStamp;
	}

	public void setFrom(int from) {
		this.from = from;
	}
	
	public void setTo(int to) {
		this.to = to;
	}
	
	public void setStable() {
		this.isStable = true;
	}
	
	public boolean getStableStatus() {
		return this.isStable;
	}
	
	public int getFrom() {
		return this.from;
	}
	
	public int getTo() {
		return this.to;
	}
	
	public int getNow() {
		return this.numOfSec;
	}
	
	public long getTime() {
		return this.timeStamp;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
	public void setdeltaGv(double deltaGv) {
		this.deltaGv = deltaGv;
	}
	
	public double getdeltaGv(){
		return this.deltaGv;
	}
	
	public void setSmallSigmaEvent(double smallSigmaEvent) {
		this.smallSigmaEvent = smallSigmaEvent;
	}
	
	public double getSmallSigmaEvent(){
		return this.smallSigmaEvent;
	}
	
	public void setSmallSigmaEvent2(double smallSigmaEvent2) {
		this.smallSigmaEvent2 = smallSigmaEvent2;
	}
	
	public double getSmallSigmaEvent2(){
		return this.smallSigmaEvent2;
	}
	
	public void addGvList(double gv){
		this.gvList.add(gv);
	}
	
	public Vector<Double> getGvList(){
		return this.gvList;
	}

}


