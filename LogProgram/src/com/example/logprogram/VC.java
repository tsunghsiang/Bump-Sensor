package com.example.logprogram;

/**************  
 * 1 sensor input per unit Object
 ******************/

public class VC {
	private int seq;
	private int numOfSec;
	private long timeStamp;
	private double gX, gY, gZ, gV;
	private float speed;
	public VC() {
		
	}

	public VC(int seq, long timeStamp, int numOfSec, double gX, double gY,
			double gZ, float speed) {
		// TODO Auto-generated constructor stub
		this.seq = seq;
		this.timeStamp = timeStamp;
		this.numOfSec = numOfSec;
		this.gX = gX; this.gY = gY; this.gZ = gZ;
		this.speed = speed;
	}
	

	public void setGv(double gV) {
		// TODO Auto-generated method stub
		this.gV = gV;
	}

	public int getSeq() {
		// TODO Auto-generated method stub
		return this.seq;
	}

	public long getTime() {
		// TODO Auto-generated method stub
		return this.timeStamp;
	}

	public double getGX() {
		// TODO Auto-generated method stub
		return this.gX;
	}

	public double getGY() {
		// TODO Auto-generated method stub
		return this.gY;
	}
	
	public double getGZ() {
		// TODO Auto-generated method stub
		return this.gZ;
	}
	
	public double getGV() {
		// TODO Auto-generated method stub
		return this.gV;
	}

	public int getNumOfSec() {
		// TODO Auto-generated method stub
		return this.numOfSec;
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
}

