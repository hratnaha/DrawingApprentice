package jcocosketch;

import java.io.IOException;
import java.util.*;

import org.apache.commons.math3.*;
import org.apache.commons.math3.stat.Frequency;
public class Regional extends Decision_Engine {

	private HashMap<Integer, Integer> data = new HashMap<Integer,Integer>(); 
	// for future add Map like <Integer, Local> to keep track of each local decision with respect to the group
	
	private Local local;
	protected  Frequency freq = new Frequency();
	public Regional(Line line, Line line2, float screenDiag) {
		super(line, line2, screenDiag);
		this.local = new Local(line, line2, screenDiag);
		// TODO Auto-generated constructor stub
	}
	
	public void addRegion(int regionId) {
		
		Integer value;
		if (data.containsKey((Integer)regionId)) {
			value = data.get(regionId);
			data.put(regionId, ++value);
		} 
		else {
			data.put(regionId, 1);
			
		}
		freq.addValue(regionId);
		
	}
	public int regionDecision() {
		Integer[] arr = new Integer[data.size()];
		arr = data.keySet().toArray(arr);
		int num = arr[arr.length - 1];
		
		return generateNumberByFreq(freq, num);
		
	}
	
	public int generateNumberByFreq(Frequency f, int numAlgorithms) {
		double rand = random.nextDouble();
		long cumF = f.getCumFreq(numAlgorithms);
		
		for (int i=numAlgorithms; i>=0; --i){
			double thisBin = (double)f.getCumFreq(i)/cumF;
			if (thisBin <= rand) {
				return i;
			}
		}
		
		return 1;
	}
	
	public Line decision() {
		
			return local.decision();
		
		//return local.decisionLine(0);
	}
	

	
}
