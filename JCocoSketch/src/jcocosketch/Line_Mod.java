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
	ArrayList<Point> allPoints;
	Random random;

	public Line_Mod(Line line, Random random) {
		this.line = line;
		this.allPoints = line.getAllPoints();
		this.random = random;
	}

	public Line MakeCompGenerated(Line line) {
		line.compGenerated = true;
		return line;
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
				Point p0 = new Point(allPoints.get(j).x, allPoints.get(j).y);
				PVector normal = new PVector(-1, 1); // normal vector to the
				// drawn line (might
				// need to change signs
				// based on slope)
				normal.setMag(lineSpacing);
				PVector normalPoint = PVector.add(p0, normal); // determine the
				// location of
				// new point
				Point newPoint = new Point(normalPoint.x, normalPoint.y, translatedLine.lineID);
				translatedLine.addPoint(newPoint);
			}
		}
		
		return MakeCompGenerated(translatedLine);
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
				Point newPoint = new Point(normalPoint.y, normalPoint.x, reflectionLine.lineID);
				reflectionLine.addPoint(newPoint);
			}
		}
		return MakeCompGenerated(reflectionLine);
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
				Point newPoint = new Point(normalPoint.x / 2 + 100,
						normalPoint.y / 2 + 100, scaledLine.lineID);
				// hard-coded the +100 in an attempt to get the scaled line
				// closer to the origional
				// needs actual fixing.
				scaledLine.addPoint(newPoint);
			}
		}
		return MakeCompGenerated(scaledLine);
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
				Point newPoint = new Point(normalPoint.y, normalPoint.x, rotatedLine.lineID);
				rotatedLine.addPoint(newPoint);
			}
		}
		return MakeCompGenerated(rotatedLine);
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

				Point newPoint = new Point(x, y, newLine.lineID);
				newLine.allPoints.add(newPoint);
			} else
				// just add the point to the point array
				newLine.allPoints.add(line.allPoints.get(i));
		}
		return MakeCompGenerated(newLine);
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
				if (x >= width || y >= height || x< 0 || y < 0) {
				
			} else
				// just add the point to the point array
				newLine.allPoints.add(line.allPoints.get(i));
		}
		return MakeCompGenerated(newLine);
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
				Point newPoint = new Point(x, y, newLine.lineID);
				newLine.allPoints.add(newPoint);
				x = x - DEGREE_RANDOM + 1.3f * (DEGREE_RANDOM +2) * random.nextFloat() + (float)random.nextGaussian();
				y = y - DEGREE_RANDOM + 1.9f * (DEGREE_RANDOM + 2) * random.nextFloat() + (float)random.nextGaussian();
				newPoint = new Point(x, y, newLine.lineID);
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
					Point newPoint = new Point(x, ynew);
					newLine.allPoints.add(newPoint);
				}
				
				
				
		}
		return MakeCompGenerated(newLine);
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
				Point newPoint = new Point(scale_factor*x1, y1);
				newLine.allPoints.add(newPoint);
				}
				else {
					float x1 = line2.allPoints.get(i).x - (reverse_offset)  ;
					float y1 =  line2.allPoints.get(i).y - (reverse_offset) ;
					Point newPoint = new Point(x1, y1,newLine.lineID);
					newLine.allPoints.add(newPoint);
				}

				//PVector newPoint = new PVector(scale_factor*x1, y1);
				//newLine.allPoints.add(newPoint);
			} else
				// just add the point to the point array
				newLine.allPoints.add(line.allPoints.get(i));
		}


		return MakeCompGenerated(newLine);
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
				Point newPoint = new Point(scale_factor*x1, y1, newLine.lineID);
				newLine.allPoints.add(newPoint);
				}
				else {
					float x1 = line2.allPoints.get(i).x - (reverse_offset)  ;
					float y1 =  line2.allPoints.get(i).y - (reverse_offset) ;
					Point newPoint = new Point(x1, y1, newLine.lineID);
					newLine.allPoints.add(newPoint);
				}

				//Point newPoint = new Point(scale_factor*x1, y1);
				//newLine.allPoints.add(newPoint);
			} else{}
				// just add the point to the point array
				//newLine.allPoints.add(line.allPoints.get(i));
		}


		return MakeCompGenerated(newLine);
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

				Point newPoint = new Point(scale_factor*x1, scale_factor*y1, newLine.lineID);
				newLine.allPoints.add(newPoint);
			} else

				// just add the point to the point array
				newLine.allPoints.add(line2.allPoints.get(i));
		}

		newLine = this.scaling(newLine);
		return MakeCompGenerated(newLine);
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
				Point p0 = new Point(l.allPoints.get(j).x, l.allPoints.get(j).y, scaledLine.lineID);
				PVector normal = new PVector(1, -1); // line to get a
				// translation point
				// from
				normal.setMag(lineSpacing);
				PVector normalPoint = PVector.add(p0, normal); // determine the
				// location of
				// new point
				Point newPoint = new Point(normalPoint.x / 5 + 100,
						normalPoint.y / 5 + 100, scaledLine.lineID);
				// hard-coded the +100 in an attempt to get the scaled line
				// closer to the origional
				// needs actual fixing.
				scaledLine.addPoint(newPoint);
			}
		}
		return MakeCompGenerated(scaledLine);
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
			
			
			Point newPoint = new Point(line.allPoints.get(i).x + offset, (float)fs.value(i) + offset, newLine.lineID);
			newLine.allPoints.add(newPoint);
		}
		return MakeCompGenerated(newLine);
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
				Point newPoint = new Point(line.allPoints.get(i).getX(), (float)pf.value((double)i) + line.allPoints.get(/*0*/i).y - offset, newLine.lineID);
				newLine.allPoints.add(newPoint);
			}
		}
		return MakeCompGenerated(newLine);
		
	}
	
	public Line Segment(Line line, boolean variant) {
		SegmentPolyFunction p = SegmentationAlgorithm.ChopIntoSegments(line);
		System.out.println("Called Segmentation Algorithm");
		for(int i =0 ;i <p.coeffs.length; ++i) {
			System.out.println("Coeffs " + i + ": " +p.coeffs[i]);
		}
		Line newLine = new Line();
	
		/*for (int i = 0; i < line.allPoints.size(); i++) {
			float x = line.allPoints.get(i).x;
			
			double resilt = p.coeffs[0]*Math.pow(x, 3) + p.coeffs[1]*Math.pow(x, 2) + p.coeffs[2]*Math.pow(x, 1) + p.coeffs[3];
			//System.out.println("Debug Point Result #" + i + " " + resilt);
			PVector point = new PVector(x, (float)pf.value((double)x));
			newLine.allPoints.add(point);
		}
		*/
	if(!variant) 
		newLine = p.getLine();
	else
		newLine = p.getVariantLine();
	
		return MakeCompGenerated(newLine);
	}
	
	public Line Segment(Line line, int samples, boolean variant) {
        SegmentPolyFunction[] p = SegmentationAlgorithm.ChopIntoSegments(line, samples);
    //    System.out.println("Called Segmentation Algorithm with sampling");
		/*for(int i =0 ;i <p.coeffs.length; ++i) {
			System.out.println("Coeffs " + i + ": " +p.coeffs[i]);
		}*/
        ArrayList<ArrayList<Double>> coeffsList = new ArrayList<ArrayList<Double>> ();
        ArrayList<ArrayList<Double>> inputList = new ArrayList<ArrayList<Double>> ();
        Line newLine = new Line();
        ArrayList<Line> pol = new ArrayList<Line>();
        for (int i = 0; i < line.allPoints.size(); i+=samples) {
            if (i + samples < line.allPoints.size()) {
                List<Point> points = line.allPoints.subList(i, i+samples);
                Line tempLine = new Line();
                tempLine.allPoints.addAll(points);
         //       System.out.println("Chopped Line: " + i);
                SegmentPolyFunction pGen = SegmentationAlgorithm.ChopIntoSegments(tempLine);

                //add coeffs to array
                ArrayList<Double> coeffs = new ArrayList<Double>();
                coeffs.add(pGen.coeffs[0]);
                coeffs.add(pGen.coeffs[1]);
                coeffs.add(pGen.coeffs[2]);
                coeffsList.add(coeffs);
                //Input fed to NEAT Net, only x,y pairs now
                ArrayList<Double> input = new ArrayList<Double>();
                input.add((double)line.allPoints.get(i).getX());
                input.add((double)line.allPoints.get(i).getY());
                inputList.add(input);
                if (!variant) {
                    pol.add(pGen.getLine());
                }
                else {
                    pol.add(pGen.getVariantLine());
                }
            }
        }
        //line.allPoints.subList(0, toIndex)

        //for (int  i=0;i<p.length; i++) {
        //newLine.allPoints.addAll(p[i].getLine().allPoints);
        //}
        //newLine = p[0].getLine();
        //System.out.println("Coeff 1 " + p[2].coeffs[0]);
        System.out.println("Segmentation Array Length: " + p.length);
        System.out.println("Line Array Length: " + pol.size());
        for (int i = 0; i< pol.size(); ++i) {
            newLine.allPoints.addAll(pol.get(i).allPoints);
        }

        // DO the LEARNING by COEFFICIENTS
        double[][] INPUT = new double[inputList.size()][2];
        for (int i =0; i<INPUT.length; ++i) {
            for (int j =0; j<2; ++j){
                INPUT[i][j] = inputList.get(i).get(j);
            }
        }
        double[][] OUTPUT = new double[coeffsList.size()][1];
        for (int i =0; i<OUTPUT.length; ++i) {
            for (int j =0; j<1; ++j){
                OUTPUT[i][j] = coeffsList.get(i).get(2);
            }
        }

        //LEARN -- remove this for not performing learning with segmentation
      //  NEATLearner.SimpleLearner(INPUT, OUTPUT);

        return MakeCompGenerated(newLine);
    }
    public Line generateBYNEATLEARNING(Line line, int samples) {
        SegmentPolyFunction pf = new SegmentPolyFunction();
        ArrayList<Line> listOfLines = new ArrayList<Line>();
        for (int i =0; i < line.allPoints.size(); i+=samples){
            pf.x0 = line.allPoints.get(i).x;
            pf.x1 = pf.x0 + samples;

            double[] data = {(double)line.allPoints.get(i).getX(), (double)line.allPoints.get(i).getY()};
            pf.coeffs[3] = 200;
            pf.coeffs[2] = NEATLearner.getCoeffecientsByNEAT(data);
            pf.coeffs[1] = NEATLearner.getCoeffecientsByNEAT(data);
            pf.coeffs[0] = NEATLearner.getCoeffecientsByNEAT(data);

            listOfLines.add(pf.getVariantLine(600));

        }
        Line toReturn  = new Line();
        for (int i =0; i<listOfLines.size(); ++i) {
            Line l = listOfLines.get(i);
            for (int j=0; j<l.allPoints.size(); ++j){
                toReturn.allPoints.add(l.allPoints.get(j));
            }
        }
        return toReturn;
    }

    //NEAT CO-GENERATION
    public Line SegmentNEAT(Line line, int samples, boolean variant) {
        SegmentPolyFunction[] p = SegmentationAlgorithm.ChopIntoSegments(line, samples);
        System.out.println("Called Segmentation Algorithm with sampling and NEAT");


        Line newLine = new Line();
        ArrayList<Line> pol = new ArrayList<Line>();
        for (int i = 0; i < line.allPoints.size(); i+=samples) {
            if (i + samples < line.allPoints.size()) {
                List<Point> points = line.allPoints.subList(i, i+samples);
                Line tempLine = new Line();
                tempLine.allPoints.addAll(points);
                System.out.println("Chopped Line: " + i);
                SegmentPolyFunction pGen = SegmentationAlgorithm.ChopIntoSegments(tempLine);
                //add coeffs to array

                //Input fed to NEAT Net, only x,y pairs now
                double[] data = {(double)line.allPoints.get(i).getX(), (double)line.allPoints.get(i).getY()};
                //		pGen.coeffs[3] = 200;
                pGen.coeffs[1] = NEATLearner.getCoeffecientsByNEAT(data);
                pGen.coeffs[2] = NEATLearner.getCoeffecientsByNEAT(data)*1.23;
                if (!variant) {
                    pol.add(pGen.getLine());
                }
                else {
                    pol.add(pGen.getVariantLine(500, pGen.coeffs, false));
                }
            }
        }
        //line.allPoints.subList(0, toIndex)

        //for (int  i=0;i<p.length; i++) {
        //newLine.allPoints.addAll(p[i].getLine().allPoints);
        //}
        //newLine = p[0].getLine();
        //System.out.println("Coeff 1 " + p[2].coeffs[0]);
        System.out.println("Segmentation Array Length: " + p.length);
        System.out.println("Line Array Length: " + pol.size());
        for (int i = 0; i< pol.size(); ++i) {
            newLine.allPoints.addAll(pol.get(i).allPoints);
        }

        newLine = this.scaling(newLine);
        return MakeCompGenerated(newLine);
    }

    public Line generateBYCTMExploration(Line line) {
        int[] allY = new int[line.allPoints.size()];
        for (int i =0; i <allY.length; ++i) {
            allY[i] = (int)line.allPoints.get(i).getY();

        }

        float[] newY = EMCCTMOPTIMIZE.OptimizePoints(allY, true, 20000);
        Line newLine = new Line();
        for (int i =0; i <newY.length; ++i) {
            newLine.allPoints.add(new Point(line.allPoints.get(i).getX(), newY[i], newLine.lineID));
        }
        newLine = Segment(newLine, 1, true); //how surprising the data has to be
        return newLine;
    }

    public Line SegmentAndCTM(Line line, int samples){
        Line newLine = new Line();
        ArrayList<Line> pol = new ArrayList<Line>();
        for (int i = 0; i < line.allPoints.size(); i+=samples) {
            if (i + samples <= line.allPoints.size()) {
                List<Point> points = line.allPoints.subList(i, i+samples);
                Line tempLine = new Line();
                tempLine.allPoints.addAll(points);
                SegmentPolyFunction pGen = SegmentationAlgorithm.ChopIntoSegments(tempLine);
                //DO CTM STUFF HERE
                int[] YS = new int[pGen.coeffs.length];
                for (int x =0; x < YS.length; ++x) {
                    YS[x] = (int)pGen.coeffs[x];
                    if (YS[x] < 0) {
                        YS[x] = -YS[x];
                    } else if (YS[x] ==0) {
                        YS[x] = 1;
                    }
                }

                float[] newYS = EMCCTMOPTIMIZE.OptimizePoints(YS);
                double[] newCoeffs = new double[newYS.length];

                for (int x =0; x < newYS.length; ++x) {
                    newCoeffs[x] = pGen.coeffs[x] + newYS[x]*1.2 ;

                }

                pol.add(pGen.getVariantLine(200, newCoeffs, true));

            }
        }

        for (int i = 0; i< pol.size(); ++i) {
            newLine.allPoints.addAll(pol.get(i).allPoints);
        }

        return MakeCompGenerated(newLine);
    }



}
