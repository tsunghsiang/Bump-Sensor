package com.example.logprogram;

import java.util.ArrayList;
import java.util.Iterator;

/**************  
 * 1 sensor input per unit Object
 ******************/

public class SensorValues {
	private double xValue;
	private double yValue;
	private double zValue;
	private double norm;
	private boolean isNeeded = false;
	
	public SensorValues(double xValue, double yValue, double zValue) {
		this.xValue = xValue;
		this.yValue = yValue;
		this.zValue = zValue;
		this.norm = (double) Math.sqrt(this.xValue*this.xValue+this.yValue*this.yValue+this.zValue*this.zValue);
	}
	
	public double getNorm() {
		return this.norm;
	}
	
	public static SensorValues add(SensorValues vA, SensorValues vB) {
		SensorValues v = new SensorValues(vA.getXvalue()+vB.getXvalue(), vA.getYvalue()+vB.getYvalue(), vA.getZvalue()+vB.getZvalue());
		return v;
	}
	
	public static SensorValues scalarProduct(double scalar, SensorValues vA) {
		SensorValues v = new SensorValues(vA.xValue*scalar, vA.yValue*scalar, vA.zValue*scalar);
		return v;
	}
	
	public static double dotProduct(SensorValues vA, SensorValues vB) {
		return vA.getXvalue()*vB.getXvalue()+vA.getYvalue()*vB.getYvalue()+vA.getZvalue()*vB.getZvalue();		
	}
	
	public static double cosineTheta(SensorValues vA, SensorValues vB) {
		return SensorValues.dotProduct(vA, vB)/(vA.getNorm()*vB.getNorm());		
	}
	
	public static SensorValues projection(SensorValues vA, SensorValues vB) {
		SensorValues unitB = SensorValues.scalarProduct(1/vB.getNorm(), vB);
		return SensorValues.scalarProduct(vA.getNorm()*SensorValues.cosineTheta(vA, vB), unitB);
	}
	
	public static SensorValues crossProduct(SensorValues vA, SensorValues vB) {
		double vX = vB.getYvalue()*vA.getZvalue() - vB.getZvalue()* vA.getYvalue();
		double vY = vB.getXvalue()*vA.getZvalue() - vB.getZvalue()* vA.getXvalue();
		double vZ = vB.getXvalue()*vA.getYvalue() - vB.getYvalue()* vA.getXvalue();
		SensorValues v = new SensorValues(vX, vY, vZ);
		return v;
	}
	
	public static SensorValues inverse(SensorValues vA) {
		SensorValues v = new SensorValues(-vA.getXvalue(),-vA.getYvalue(),-vA.getZvalue());
		return v;
	}
	
	public static SensorValues average(ArrayList<SensorValues> vectors) {
		Iterator<SensorValues> i = vectors.iterator();
		double vX=0.0f, vY=0.0f, vZ=0.0f;
		SensorValues v;
		while (i.hasNext()) {
			SensorValues t = i.next();
			vX+=t.getXvalue();
			vY+=t.getYvalue();
			vZ+=t.getZvalue();
		}
		v = new SensorValues(vX/vectors.size(),vY/vectors.size(),vZ/vectors.size());
		return v;
	}
	
	public static SensorValues standardDeviation(ArrayList<SensorValues> vectors) {
		Iterator<SensorValues> i = vectors.iterator();
		double vX=0.0f, vY=0.0f, vZ=0.0f;
		double xX=0.0f, yY=0.0f, zZ=0.0f;
		SensorValues v;
		while (i.hasNext()) {
			SensorValues t = i.next();
			vX+=t.getXvalue();
			vY+=t.getYvalue();
			vZ+=t.getZvalue();			
			xX+=t.getXvalue()*t.getXvalue();
			yY+=t.getYvalue()*t.getYvalue();
			zZ+=t.getZvalue()*t.getZvalue();
		}
		vX = (double) Math.sqrt(xX/vectors.size() - (vX/vectors.size())*(vX/vectors.size()));
		vY = (double) Math.sqrt(yY/vectors.size() - (vY/vectors.size())*(vY/vectors.size()));
		vZ = (double) Math.sqrt(zZ/vectors.size() - (vZ/vectors.size())*(vZ/vectors.size()));
		v = new SensorValues(vX,vY,vZ);
		return v;
	}
	
	public double getXvalue() {
		return this.xValue;
	}
	
	public double getYvalue() {
		return this.yValue;
	}
	
	public double getZvalue() {
		return this.zValue;
	}
	
	public boolean isZero() {
		if (this.xValue ==0.0f && this.yValue == 0.0f && this.zValue ==0.0f) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getIsNeeded() {
		return this.isNeeded;
	}
	
	public void setIsNeeded(boolean isNeeded) {
		this.isNeeded = isNeeded;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("x value: ").append(xValue).append(", y value: ").append(yValue).append(", z value:").append(zValue).append(", isNeeded:")
				.append(isNeeded).toString();		
	}
	
	

}

