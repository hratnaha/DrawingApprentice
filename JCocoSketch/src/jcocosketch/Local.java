package jcocosketch;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.math3.stat.*;
import org.apache.commons.math3.random.*;
//Neural NET import
/*import org.encog.*;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.*;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.ml.data.buffer.BufferedMLDataSet;*/

public class Local extends Decision_Engine{

	public int numAlgorithms = 14;
	private double[] learningArray = new double[numAlgorithms];
	public  Frequency freq = new Frequency();
	//Neural NEt
	//private BasicNetwork network;
	public Local(Line line, Line line2, float screenDiag) {
		super(line, line2, screenDiag);
		// TODO Auto-generated constructor stub
		for(int i = 1; i <= numAlgorithms; ++i) {
			this.freq.addValue(i);
		}
		//Init Neural Net
	/*	this.network = new BasicNetwork();
		this.network.addLayer(new BasicLayer(null, true, 10));
		this.network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 1));
		this.network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		this.network.getStructure().finalizeStructure();
		this.network.reset();*/
	}
	private int decisionNumber = 0;
	
	@Override
	public Line decision() {
		/*int decision = generateNumberByFreq(freq, numAlgorithms); //1 + random.nextInt(11);
		//freq.addValue(decision);
		decisionNumber = decision;
		for(int i = 1; i<=learningArray.length; ++i) {
			learningArray[i - 1] = (double)freq.getCount(i);
		}*/
		
		//Learn -- fix this propagation
	/*	MLDataSet trainingData = new BasicMLDataSet();
		BasicMLData m = new BasicMLData(learningArray);
		
		trainingData.add(m);
		 
	//	ResilientPropagation rprog = new ResilientPropagation(network, trainingData);
	//	rprog.iteration();
		//rprog.finishTraining();
		*/
		//return super.decisionLine(decision);
		return super.decision();
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
	
	public void upvote() {
	//	freq.addValue(decisionNumber);
		DQNJS.learn(1.0f);
		freq.incrementValue(decisionNumber, 1);
		
		System.out.println("Incremented " + decisionNumber);
	}
	
	public void downvote() {
		DQNJS.learn(-0.9f);
		freq.incrementValue(decisionNumber, -2);
		System.out.println("decremented " + decisionNumber);
	}
	
	public void print() {
		
	for (int i = 1; i <= numAlgorithms; ++i) {
			System.out.print("\t" + freq.getCount(i));
			
		}
		System.out.println();
	}
}
