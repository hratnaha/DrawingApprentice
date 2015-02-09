package jcocosketch;

import java.util.Random;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import processing.core.PVector;

public class SegmentPolyFunction {

	public double[] coeffs;
	public double x0;
	public double y0;
	
	public double x1;
	public double y1;
	Random rand = new Random();
	public double resultAt(double x) {
		double res = 0.0;
		int j = 0;
		for(int i = coeffs.length - 1; i >=0; --i) {
			res += coeffs[i]*Math.pow(x, j++ + 1);
			res += 20;
		}
		return res;
	
	}
	
	public Line getLine() {
		//coeffs[3] = 0.003;
		org.apache.commons.math3.analysis.polynomials.PolynomialFunction pf = new org.apache.commons.math3.analysis.polynomials.PolynomialFunction(coeffs);
		Line line = new Line();
		for (int i = (int) x0; i <= (int)x1; ++i) {
			line.allPoints.add(new Point((float) i, (float)pf.value((double)i)));
		}
		return line;
	}
	
	public Line getVariantLine() {
		/*for(int i = 0; i <coeffs.length;++i) {
			coeffs[i] = rand.nextInt(10) * Math.pow(8, -3);
			System.out.print(" Coeffs "+ i + ": " + coeffs[i]);
			
		}*/
		//coeffs[rand.nextInt(coeffs.length)] += Math.pow(Math.PI, -1*rand.nextInt(7) + rand.nextDouble());
		//coeffs = this.swap(coeffs);
		float offsetY = 190;//30;
		float offsetX = 0;
		System.out.println();
		 
		org.apache.commons.math3.analysis.polynomials.PolynomialFunction pf = new org.apache.commons.math3.analysis.polynomials.PolynomialFunction(coeffs);
		//coeffs[rand.nextInt(coeffs.length)] += Math.pow(Math.PI, -1*rand.nextInt(7) + rand.nextDouble());
		//coeffs[3] -= coeffs[1];
	//	x0 -= (double)rand.nextInt(5);
		pf = pf.add(new org.apache.commons.math3.analysis.polynomials.PolynomialFunction(coeffs));
		
		//pf = (PolynomialFunction) pf.derivative();
		//pf = pf.add(pf);
		Line line = new Line();
		for (int i = (int) x0; i <= (int)x1; ++i) {
			line.allPoints.add(new Point(((float) i)-offsetX, (float)pf.value((double)i)- offsetY));
		}
		return line;
	}
	
	private double[] swap(double[] coeffs) {
		double t = coeffs[0];
		coeffs[0] = coeffs[coeffs.length - 1];
		coeffs[coeffs.length - 2] = t;
		return coeffs;
		
	}
	
	public static double computeSimilarity(double[] coeff1, double[] coeff2) {
		double sim = 0.0;
		for (int i = 0; i < Math.min(coeff1.length, coeff2.length); ++i) {
			sim += Math.abs(coeff1[i]) - Math.abs(coeff2[i]);
		}
		return sim;
	}
}
