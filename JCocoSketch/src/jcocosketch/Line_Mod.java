package jcocosketch;

import processing.core.*;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;

import org.apache.commons.math3.random.*;

public class Line_Mod {
	private static final float PROB_RANDOM = 0.15f;
	private static final float DEGREE_RANDOM = 5.0f;

	Line line;
	ArrayList<PVector> allPoints;
	Random random;

	public Line_Mod(Line line, Random random) {
		this.line = line;
		this.allPoints = line.getAllPoints();
		this.random = random;
	}

	public Line translation() {
		float lineSpacing = 80;
		Line translatedLine = new Line();
		for (int j = 0; j < allPoints.size(); j++) {// cycle through all points
			// of line, exclude
			// beginning and end point
			if (j < allPoints.size() - 2) {// special case for start and end of
				// line
				// generate the normal point and add to the newLine
				PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y);
				PVector normal = new PVector(-1, 1); // normal vector to the
				// drawn line (might
				// need to change signs
				// based on slope)
				normal.setMag(lineSpacing);
				PVector normalPoint = PVector.add(p0, normal); // determine the
				// location of
				// new point
				PVector newPoint = new PVector(normalPoint.x, normalPoint.y);
				translatedLine.addPoint(newPoint);
			}
		}
		return translatedLine;
	}

	public Line reflection() { // reflects along y = -x
		float lineSpacing = 20;
		Line reflectionLine = new Line();

		for (int j = 0; j < allPoints.size(); j++) {// cycle through all points
			// of line, exclude
			// beginning and end point
			if (j < allPoints.size() - 2) {// special case for start and end of
				// line
				// generate the normal point and add to the newLine
				PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y);
				PVector normal = new PVector(-1, 1); // the reflection axis
				normal.setMag(lineSpacing);
				PVector normalPoint = PVector.add(p0, normal); // determine the
				// location of
				// new point
				PVector newPoint = new PVector(normalPoint.y, normalPoint.x);
				reflectionLine.addPoint(newPoint);
			}
		}
		return reflectionLine;
	}

	public Line scaling() { // scales it, just doesn't put the line where I'd
		// like it to.
		float lineSpacing = 80;
		Line scaledLine = new Line();

		for (int j = 0; j < allPoints.size(); j++) {// cycle through all points
			// of line, exclude
			// beginning and end point
			if (j < allPoints.size() - 2) {// special case for start and end of
				// line
				// generate the normal point and add to the newLine
				PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y);
				PVector normal = new PVector(1, -1); // line to get a
				// translation point
				// from
				normal.setMag(lineSpacing);
				PVector normalPoint = PVector.add(p0, normal); // determine the
				// location of
				// new point
				PVector newPoint = new PVector(normalPoint.x / 2 + 100,
						normalPoint.y / 2 + 100);
				// hard-coded the +100 in an attempt to get the scaled line
				// closer to the origional
				// needs actual fixing.
				scaledLine.addPoint(newPoint);
			}
		}
		return scaledLine;
	}

	public Line rotation() { // requires some maths
		float lineSpacing = 20;
		Line rotatedLine = new Line();

		for (int j = 0; j < allPoints.size(); j++) {// cycle through all points
			// of line, exclude
			// beginning and end point
			if (j < allPoints.size() - 2) {// special case for start and end of
				// line
				// generate the normal point and add to the newLine
				PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y);
				PVector p1 = new PVector(allPoints.get(allPoints.size() / 2).x,
						allPoints.get(allPoints.size() / 2).y);// rotation axis

				// math to rotate a point:
				float m = p0.mag() - p1.mag(); // distance between two points
				// float z = acos(abs(

				PVector normal = new PVector(1, -1); // line to get a
				// translation point
				// from
				normal.setMag(lineSpacing);
				PVector normalPoint = PVector.add(p0, normal); // determine the
				// location of
				// new point
				PVector newPoint = new PVector(normalPoint.y, normalPoint.x);
				rotatedLine.addPoint(newPoint);
			}
		}
		return rotatedLine;
	}

	public Line drawBack(Line line) {
		// take the previous line and cycle through its components and add some
		// noise to it
		// here, I will just add a 1 * random 0-1 + point to each point, then
		// draw
		// create a new Line of the current line, then newLine.drawLine();
		Line newLine = new Line();
		for (int i = 0; i < line.allPoints.size(); i++) {
			// cycle through the points and add in a bit of randomness to each
			// points
			// first decide if we should interfere with this point, give it a P
			// of .5 for interfering
			if (random.nextFloat() > (1 - PROB_RANDOM)) {
				float x = line.allPoints.get(i).x;
				float y = line.allPoints.get(i).y;

				x = x - DEGREE_RANDOM + 2 * DEGREE_RANDOM * random.nextFloat();
				y = y - DEGREE_RANDOM + 2 * DEGREE_RANDOM * random.nextFloat();

				PVector newPoint = new PVector(x, y);
				newLine.allPoints.add(newPoint);
			} else
				// just add the point to the point array
				newLine.allPoints.add(line.allPoints.get(i));
		}
		return newLine;
	}
	public Line Trim(Line line, int width, int height) {
		
		Line newLine = new Line();
		for (int i = 0; i < line.allPoints.size(); i++) {
			// cycle through the points and add in a bit of randomness to each
			// points
			// first decide if we should interfere with this point, give it a P
			// of .5 for interfering
			
				float x = line.allPoints.get(i).x;
				float y = line.allPoints.get(i).y;
				if (x >= width || y >=height || x < 0 || y < 0) {
				
			} else
				// just add the point to the point array
				newLine.allPoints.add(line.allPoints.get(i));
		}
		return newLine;
	}
	

	public Line drawBackNoisy(Line line) {
		// take the previous line and cycle through its components and add some
		// noise to it
		// here, I will just add a 1 * random 0-1 + point to each point, then
		// draw
		// create a new Line of the current line, then newLine.drawLine();
		Line newLine = new Line();
		for (int i = 0; i < line.allPoints.size(); i++) {
			// cycle through the points and add in a bit of randomness to each
			// points
			// first decide if we should interfere with this point, give it a P
			// of .5 for interfering
			
				float x = line.allPoints.get(i).x;
				float y = line.allPoints.get(i).y;

				x = x - DEGREE_RANDOM + 2 * DEGREE_RANDOM * random.nextFloat() + (float)random.nextGaussian();
				y = y - DEGREE_RANDOM + 2 * DEGREE_RANDOM * random.nextFloat() + (float)random.nextGaussian();
				PVector newPoint = new PVector(x, y);
				newLine.allPoints.add(newPoint);
				x = x - DEGREE_RANDOM + 1.3f * (DEGREE_RANDOM +2) * random.nextFloat() + (float)random.nextGaussian();
				y = y - DEGREE_RANDOM + 1.9f * (DEGREE_RANDOM + 2) * random.nextFloat() + (float)random.nextGaussian();
				newPoint = new PVector(x, y);
				newLine.allPoints.add(newPoint);
			/*	x = x - DEGREE_RANDOM + 1.3f * (DEGREE_RANDOM +3) * random.nextFloat() + (float)random.nextGaussian();
				y = y - DEGREE_RANDOM + 1.9f * (DEGREE_RANDOM + 3) * random.nextFloat() + (float)random.nextGaussian();
				newPoint = new PVector(x, y);
				newLine.allPoints.add(newPoint);
			*/
				// just add the point to the point array
				//newLine.allPoints.add(line.allPoints.get(i));
		}
		return newLine;
	}
	
	public Line drawBackShade(Line line) {
		// take the previous line and cycle through its components and add some
		// noise to it
		// here, I will just add a 1 * random 0-1 + point to each point, then
		// draw
		// create a new Line of the current line, then newLine.drawLine();
		Line newLine = new Line();
		for (int i = 0; i < line.allPoints.size()-1; i++) {
			// cycle through the points and add in a bit of randomness to each
			// points
			// first decide if we should interfere with this point, give it a P
			// of .5 for interfering
			
				float x = line.allPoints.get(i).x;
				float y = line.allPoints.get(i).y;
				float nextY = line.allPoints.get(i+1).y;
				float nextX = line.allPoints.get(i+1).x;
				//x = x - DEGREE_RANDOM + 2 * DEGREE_RANDOM * random.nextFloat() + (float)random.nextGaussian();
				//y = y - DEGREE_RANDOM + 2 * DEGREE_RANDOM * random.nextFloat() + (float)random.nextGaussian();
				//PVector newPoint = new PVector(x, y);
				for(int i1 = 0; i1< (int) (nextY-y); ++i1) {
					float ynew = y - (random.nextInt(5)*random.nextFloat()); 
					x += 1;//random.nextInt(2)*random.nextFloat();
					PVector newPoint = new PVector(x, ynew);
					newLine.allPoints.add(newPoint);
				}
				
				
				
		}
		return newLine;
	}
	
	//************************************************************************
	//Actual Mutation Algorithm
	//Author: Kunwar Yashraj Singh
	//Added: Monday, September 8, 2014.
	//************************************************************************
	public Line drawMutation(Line line, Line line2) {
		if(line2==null){
			line2 = line;
		}
		boolean reverse = true;
		int reverse_offset = 50;
		float scale_factor = 1.2f;
		// create a new Line of the current line, then newLine.drawLine();
		Line newLine = new Line();
		int index_start = random.nextInt(line.allPoints.size());
		int index_end = index_start + random.nextInt(line.allPoints.size() - index_start);
		for (int i = 0; i < line.allPoints.size(); i++) {
			// cycle through the points and add in a bit of randomness to each
			// points
			// first decide if we should interfere with this point, give it a P
			// of .5 for interfering

			//Do mutation
			//by flipping bits -- its just one of several possible operations
			if (i >=index_start && i<=index_end) {
				
				if(!reverse) {
				float x1 = line2.allPoints.get(i).x;
				float y1 = line2.allPoints.get(i).y;
				PVector newPoint = new PVector(scale_factor*x1, y1);
				newLine.allPoints.add(newPoint);
				}
				else {
					float x1 = line2.allPoints.get(i).x - (reverse_offset)  ;
					float y1 =  line2.allPoints.get(i).y - (reverse_offset) ;
					PVector newPoint = new PVector(x1, y1);
					newLine.allPoints.add(newPoint);
				}

				//PVector newPoint = new PVector(scale_factor*x1, y1);
				//newLine.allPoints.add(newPoint);
			} else
				// just add the point to the point array
				newLine.allPoints.add(line.allPoints.get(i));
		}


		return newLine;
	}

	public Line drawOnlyMutation(Line line, Line line2) {
		if(line2==null){
			line2 = line;
		}
		boolean reverse = true;
		int reverse_offset = 50;
		float scale_factor = 1.2f;
		// create a new Line of the current line, then newLine.drawLine();
		Line newLine = new Line();
		
		int index_start = random.nextInt(line.allPoints.size());
		int index_end = index_start + random.nextInt(line.allPoints.size() - index_start);
		for (int i = 0; i < line.allPoints.size(); i++) {
			// cycle through the points and add in a bit of randomness to each
			// points
			// first decide if we should interfere with this point, give it a P
			// of .5 for interfering

			//Do mutation
			//by flipping bits -- its just one of several possible operations
			if (i >=index_start && i<=index_end) {
				
				if(!reverse) {
				float x1 = line2.allPoints.get(i).x;
				float y1 = line2.allPoints.get(i).y;
				PVector newPoint = new PVector(scale_factor*x1, y1);
				newLine.allPoints.add(newPoint);
				}
				else {
					float x1 = line2.allPoints.get(i).x - (reverse_offset)  ;
					float y1 =  line2.allPoints.get(i).y - (reverse_offset) ;
					PVector newPoint = new PVector(x1, y1);
					newLine.allPoints.add(newPoint);
				}

				//PVector newPoint = new PVector(scale_factor*x1, y1);
				//newLine.allPoints.add(newPoint);
			} else{}
				// just add the point to the point array
				//newLine.allPoints.add(line.allPoints.get(i));
		}


		return newLine;
	}


	//************************************************************************
	//Experimental Mutation Algorithm -- It acts really bizzare sometimes.
	//NOTE : Can remove this if not required.
	//Author: Kunwar Yashraj Singh
	//Added: Monday, September 8, 2014.
	//************************************************************************
	public Line drawMutation(Line line, Line line2, boolean useRandom) {
		if(line2==null){
			line2 = line;
		}
		java.util.Random rand = new java.util.Random();
		float scale_factor = 1f;
		int offset = 10;
		// create a new Line of the current line, then newLine.drawLine();
		Line newLine = new Line();
		int index_start = random.nextInt(line.allPoints.size());
		int index_end = index_start + random.nextInt(line.allPoints.size() - index_start);
		for (int i = 0; i < Math.min(line.allPoints.size(), line2.allPoints.size()); i++) {


			//Do mutation
			//by flipping bits -- its just one of several possible operations
			if (i >=index_start && i<=index_end) {

				float x1 = /*line.allPoints.get(i).x -*/ rand.nextInt((int)line2.allPoints.get(i).x);
				float y1 = /*line.allPoints.get(i).y -*/ rand.nextInt((int)line2.allPoints.get(i).y);

				PVector newPoint = new PVector(scale_factor*x1, scale_factor*y1);
				newLine.allPoints.add(newPoint);
			} else

				// just add the point to the point array
				newLine.allPoints.add(line2.allPoints.get(i));
		}

		newLine = this.scaling(newLine);
		return newLine;
	}

	public Line scaling(Line l) { // scales it, just doesn't put the line where I'd
		// like it to.
		float lineSpacing = 20;
		Line scaledLine = new Line();

		for (int j = 0; j < l.allPoints.size(); j++) {// cycle through all points
			// of line, exclude
			// beginning and end point
			if (j < l.allPoints.size() - 2) {// special case for start and end of
				// line
				// generate the normal point and add to the newLine
				PVector p0 = new PVector(l.allPoints.get(j).x, l.allPoints.get(j).y);
				PVector normal = new PVector(1, -1); // line to get a
				// translation point
				// from
				normal.setMag(lineSpacing);
				PVector normalPoint = PVector.add(p0, normal); // determine the
				// location of
				// new point
				PVector newPoint = new PVector(normalPoint.x / 5 + 100,
						normalPoint.y / 5 + 100);
				// hard-coded the +100 in an attempt to get the scaled line
				// closer to the origional
				// needs actual fixing.
				scaledLine.addPoint(newPoint);
			}
		}
		return scaledLine;
	}
	
	//TODO:
	// Remove any null pointer exception that occurs sometimes
	
	public Line drawApproximation(Line line, boolean sortY) {
		
		
		int offset = 10;
		
		double[] X = new double[line.allPoints.size()];
		double[] Y = new double[line.allPoints.size()];
	
		Line newLine = new Line();
		double medianX = (double)line.allPoints.get((int)line.allPoints.size()/2).x;
		double medianY = (double)line.allPoints.get((int)line.allPoints.size()/2).y;
		
		for (int i = 0; i < line.allPoints.size(); i++) {
			// cycle through the points and add in a bit of randomness to each
			// points
		X[i] = i;//line.allPoints.get(i).x;
		
		Y[i] = line.allPoints.get(i).y;
		
				
		}
		//remove duplicates and create a monotonic increasing sequence
		for(int i=0; i<X.length-1; i++) {
			
			for(int j=i+1; j<X.length; ++j){
			
			if((int)X[i] <= (int)X[j]) {
				X[j] = X[i]+1;
				
			}
			if((int)Y[i] <= (int)Y[j]) {
				Y[j] = Y[i]+1;
				
			}
			
			}
			//System.out.println(X[i] + " " + Y[i]);
			
		}
		Arrays.sort(X);
		if(sortY){
			Arrays.sort(Y);
		}
		
		double[][] zVal = new double[X.length][Y.length];
		SplineInterpolator si = new SplineInterpolator();
		PolynomialSplineFunction fs = si.interpolate(X, Y);
		//org.apache.commons.math3.analysis.interpolation.LoessInterpolator loess = new org.apache.commons.math3.analysis.interpolation.LoessInterpolator();
		//fs = loess.interpolate(X, Y);
		for(int i =0; i<line.allPoints.size();++i) {
			
			
			PVector newPoint = new PVector(line.allPoints.get(i).x + offset, (float)fs.value(i) + offset);
			newLine.allPoints.add(newPoint);
		}
		return newLine;
	}
	
	private float result(double[] best, float x) {
		float result1 =0.0f;
		
		for(int i=0;i<best.length;++i){
			result1 += Math.pow(x, i)*best[i];
		}
		
		return result1;
	}
	// Author: Kunwar Yashraj Singh
	//This is a good function to draw as user did but adding new features automatically
	//Somewhat during improvisation, I start with the users starting point, continue with the line but then decide
	//to slightly change something when moving along the line
	RandomGenerator rg = new JDKRandomGenerator();
	public Line drawPolynomial(boolean tryExperiment) {
		
		
		//rg.setSeed(173l);  // Fixed seed means same results every time

		// Create a GassianRandomGenerator using rg as its source of randomness
		//Old stuff gaussian
		GaussianRandomGenerator rawGenerator = new GaussianRandomGenerator(rg);
		int offset = 2;
		// new generator 
		/*org.apache.commons.math3.random.UniformRandomGenerator
		 rawGenerator = new UniformRandomGenerator(rg);*/
		//previous leaf values
		/*double[] mean = {1, 2};
		double c = 3;
		double[][] cov = {{9, c}, {c, 16}};*/
		// new leaf values 
		double[] mean = {1, 2};
		double c =4;
		double[][] cov = {{5, c}, {c, 10}};
		RealMatrix covariance = MatrixUtils.createRealMatrix(cov); 
		// Create a CorrelatedRandomVectorGenerator using rawGenerator for the components
		CorrelatedRandomVectorGenerator generator = 
		    new CorrelatedRandomVectorGenerator(mean, covariance, 1.0e-10 * covariance.getNorm(), rawGenerator);

		// Use the generator to generate correlated vectors
		double[] randomVector = generator.nextVector();
		
		PolynomialFunction pf = new PolynomialFunction(randomVector);
		
		Line newLine = new Line();
		//An experiment
		if(tryExperiment) {
		pf = pf.negate();
		}
		for(int i =0; i<line.allPoints.size();++i) {
			
			// (float)pf.value((double)i)
			if ((float)pf.value((double)i) > line.allPoints.get(/*0*/i).y + 20 ) {
				
			} else {
			PVector newPoint = new PVector(line.allPoints.get(i).x, (float)pf.value((double)i) + line.allPoints.get(/*0*/i).y - offset );
			newLine.allPoints.add(newPoint);
			}
		}
		return newLine;
		
	}
}
