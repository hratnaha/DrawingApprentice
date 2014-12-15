package jcocosketch;

import processing.core.PVector;

public class SegmentPolyFunction {

	public double[] coeffs;
	public double x0;
	public double y0;
	
	public double x1;
	public double y1;
	
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
		org.apache.commons.math3.analysis.polynomials.PolynomialFunction pf = new org.apache.commons.math3.analysis.polynomials.PolynomialFunction(coeffs);
		Line line = new Line();
		for (int i = (int) x0; i <= (int)x1; ++i) {
			line.allPoints.add(new Point((float) i, (float)pf.value((double)i)));
		}
		return line;
	}
	
	public static double computeSimilarity(double[] coeff1, double[] coeff2) {
		double sim = 0.0;
		for (int i = 0; i < Math.min(coeff1.length, coeff2.length); ++i) {
			sim += Math.abs(coeff1[i]) - Math.abs(coeff2[i]);
		}
		return sim;
	}
}
