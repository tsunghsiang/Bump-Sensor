package com.example.logprogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import android.util.Log;

public class DeltaGv {	
	private int numOfSec;
	private double deltaGv;
	private Vector<Double> gvList;
	private double biggestGv;
	private double smallestGv;
	private double cumulatedGv;
	private double cumulatedGvSquare;
	private int gvCount;
	private ArrayList<DeltaGv> bin ;
	
	DeltaGv(){
		this.biggestGv =0;
		this.smallestGv =100;
		this.cumulatedGv=0;
		this.bin = new ArrayList<DeltaGv>();
		
	}
	
	public void setnumOfSec(int numOfSec){
		this.numOfSec = numOfSec;
	}
	
	public double getnumOfSec(){
		return this.numOfSec;
	}
	
	public double getDeltaGv(){
		//Log.d("maxmin", String.valueOf(biggestGv) +" " +String.valueOf(smallestGv));
		deltaGv = biggestGv - smallestGv;
		return this.deltaGv;
	}
	
	public void addGvList(double gv){
		gvCount++;
		cumulatedGv += gv;
		cumulatedGvSquare += gv*gv;
		if(gv > biggestGv)
			biggestGv = gv;
		if(gv < smallestGv)
			smallestGv = gv;
		//this.gvList.add(gv);
	}
	
	public Vector<Double> getGvList(){
		return this.gvList;
	}
	
	public double getCumulatedGv(){
		return this.cumulatedGv;
	}
	
	public double getCumulatedGvSquare(){
		return this.cumulatedGvSquare;
	}
	
	public int getGvCount(){
		return this.gvCount;
	}
	
	public double getBigSigma(ArrayList<DeltaGv> bin){
		double bigSigma=0;
		DeltaGv deltaGvObject = new DeltaGv();
		
    	for(int i=0;i<bin.size();i++){
    		deltaGvObject = bin.get(i);
    		double cumulatedGvSquare = deltaGvObject.getCumulatedGvSquare();
    		double cumulatedGv = deltaGvObject.getCumulatedGv();
    		int size = deltaGvObject.getGvCount();
    		bigSigma = Math.sqrt((cumulatedGvSquare - (cumulatedGv*cumulatedGv)/size)/(size));
    	}

		return bigSigma;
		
	}
	
	public double getSmallSigma(ArrayList<DeltaGv> bin, int index){
		double smallSigma =0;
		double cumulatedSmallSigma =0;
		double cumulatedSmallSigmaSquare =0;
		int size=0;
		
		ArrayList<DeltaGv> tempbin = new ArrayList<DeltaGv>();
		
		for(int i=0;i<bin.size();i++){
			tempbin.add(bin.get(i));
			//Log.d("testing", String.valueOf(tempbin.get(i).deltaGv));
		}
		
		for(int i=0;i<tempbin.size();i++){
			//if(index == 2)
				//Log.d("sigmaset", String.valueOf(tempbin.get(i).deltaGv) );
		}
		//if(index == 2)
			//Log.d("sigmaset", "-----------------------------");

		Collections.sort(tempbin, new Comparator<DeltaGv>(){
			 @Override
			 public int compare(DeltaGv o1, DeltaGv o2) {
			        if (o1.deltaGv < o2.deltaGv) return -1;
			        if (o1.deltaGv > o2.deltaGv) return 1;
			        return 0;

			 }   
			});
		
		for(int i=0;i<tempbin.size()*0.5;i++){
			cumulatedSmallSigma += tempbin.get(i).getCumulatedGv();
			cumulatedSmallSigmaSquare += tempbin.get(i).getCumulatedGvSquare();
			size += tempbin.get(i).getGvCount();
			//if(index == 2)
				//Log.d("deltaGv", String.valueOf(tempbin.get(i).deltaGv) );
		}
		
		
		//if(index == 2){
			//Log.d("deltaGv", "--------");
			//Log.d("CUMULATED", String.valueOf(cumulatedSmallSigma) +" "+String.valueOf(cumulatedSmallSigmaSquare) +  " "+String.valueOf(size));
		//}
		//Log.d("SQUARE", String.valueOf(cumulatedSmallSigmaSquare) );
		//Log.d("SIZE", String.valueOf(size) );
		smallSigma = Math.sqrt((cumulatedSmallSigmaSquare - (cumulatedSmallSigma*cumulatedSmallSigma)/size)/(size));
		
		return smallSigma;
		
	}
	

}
