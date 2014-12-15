package jcocosketch;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.fitting.*;
public class CreativeTrajectoryMonitor {

	public static ArrayList<Integer> epochs = new ArrayList<Integer>();
//	private static WeightedObservedPoints obs = new WeightedObservedPoints();
	private static int i = 0;
	private static double[] coeff;
	protected static Frequency freq = new Frequency();
	protected static Random random = new Random();
	public static void AddMode(int mode) {
	//	epochs.add(mode);
		train(mode);
	}
	//private static PolynomialSplineFunction pf;
	private static void train(int mode) {
		// TODO Auto-generated method stub
		freq.incrementValue(mode, 1);
	}
	
	public static int PredictMode() {
		double rand = random.nextDouble();
		int numAlgorithms = 1;
		long cumF = freq.getCumFreq(numAlgorithms );
		
		for (int i=numAlgorithms; i>=0; --i){
			double thisBin = (double)freq.getCount(i)/cumF;
			if (rand <= thisBin) {
				System.out.println("Predicted Mode: " + i);
				return i;
			}
		}
		
		System.out.println("Predicted Mode: " + 0);
		return 0;
	}
}
