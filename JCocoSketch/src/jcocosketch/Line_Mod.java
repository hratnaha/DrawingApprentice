package jcocosketch;

import processing.core.*;
import java.util.*;

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
	//************************************************************************
	//Actual Mutation Algorithm
	//Author: Kunwar Yashraj Singh
	//Added: Monday, September 8, 2014.
	//************************************************************************
	public Line drawMutation(Line line, Line line2) {
		if(line2==null){
			line2 = line;
		}
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
		
				float x1 = line2.allPoints.get(i).x;
				float y1 = line2.allPoints.get(i).y;
				
				PVector newPoint = new PVector(scale_factor*x1, y1);
				newLine.allPoints.add(newPoint);
			} else
				// just add the point to the point array
				newLine.allPoints.add(line.allPoints.get(i));
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
		float scale_factor = 1.2f;
		float offset = 10;
		// create a new Line of the current line, then newLine.drawLine();
		Line newLine = new Line();
		int index_start = random.nextInt(line.allPoints.size());
		int index_end = index_start + random.nextInt(line.allPoints.size() - index_start);
		for (int i = 0; i < line.allPoints.size(); i++) {
			
			
			//Do mutation
			//by flipping bits -- its just one of several possible operations
			if (i >=index_start && i<=index_end) {
		
				float x1 = line.allPoints.get(i).x + rand.nextInt((int)line2.allPoints.get(i).x) - offset;
				float y1 = line.allPoints.get(i).y + rand.nextInt((int)line2.allPoints.get(i).y) - offset;
				
				PVector newPoint = new PVector(scale_factor*x1, y1);
				newLine.allPoints.add(newPoint);
			} else
				// just add the point to the point array
				newLine.allPoints.add(line2.allPoints.get(i));
		}
		
		
		return newLine;
	}
}
