package jcocosketch;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.fitting.*;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import processing.core.PVector;

public class SegmentationAlgorithm {

	public static SegmentPolyFunction ChopIntoSegments(Line line) {
		
		float x0 = line.allPoints.get(0).x;
		
		float x1 = line.allPoints.get(line.allPoints.size() - 1).x;
		
		WeightedObservedPoints obs =  new WeightedObservedPoints();
		
		for (int index = 0; index < line.allPoints.size(); ++index) {
			PVector p = line.allPoints.get(index);
			obs.add(p.x, p.y);
		}
		
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);

		
		double[] coeff = fitter.fit(obs.toList());
		
		
		SegmentPolyFunction poly = new SegmentPolyFunction();
		poly.coeffs = coeff;
		poly.x0 = x0;
		poly.x1 = x1;
		return poly;
		
	}
	
	public static SegmentPolyFunction[] ChopIntoSegments(Line line, int samples) {
		SegmentPolyFunction[] poly = new SegmentPolyFunction[samples +1];
		int length = line.allPoints.size() / samples;
		Line temp = new Line();
		int j = 0;
		
		
		for (int i = 1; i < line.allPoints.size(); ++i) {
			if ( i % length != 0) {
				temp.allPoints.add(line.allPoints.get(i));
			}
			else {
				poly[j++] = ChopIntoSegments(temp);
				temp = new Line();
			}
		}
		System.out.println("Done Poly Segmenting");
		return poly;
	}
}
