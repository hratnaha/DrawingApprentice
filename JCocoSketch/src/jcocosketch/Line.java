package jcocosketch;

import processing.core.*;

import java.util.*;

import utilities.PVecUtilities;

public class Line {
	public ArrayList<Point> allPoints = new ArrayList<Point>();
	public ArrayList<Float> parameter = new ArrayList<Float>();
	public Rectangle myBoundingBox;
	public float startTime;
	public float endTime;
	public float lineID;
	public float groupID = 1;
	public float xmin = -1;
	public float ymin = -1;
	public float xmax = -1;
	public float ymax = -1;
	public float colorR;
	public float colorG;
	public float colorB;
	public float colorA;
	public boolean compGenerated = false; 
	boolean isSelected = false;
	Random rand = new Random();
	public float lineWidth;


	// convert all reference of ponts to pvec

	public Line() {
		startTime = System.currentTimeMillis() / 1000.0f;
		this.lineID = rand.nextFloat();
	}

	public Line(float x, float y) {
		allPoints.add(new Point(x, y));
		if(this.xmin == -1){
			this.xmin = x;
		}
		
		if(this.ymin == -1){
			this.ymin = y;
		}
			
		if(this.xmax == -1){
			this.xmax = x;
		}
			
		if(this.ymax == -1) {
			this.ymax = y;
		} 
		if(xmin > -1 || ymin > -1 || xmax > -1 || ymax > -1)  {
			if (x < xmin) {
				xmin = x;
			} else if (x > xmax) {
				xmax = x;
			}
			if (y < ymin) {
				ymin = y;
			} else if (y > ymax) {
				ymax = y;
			}
		}
		startTime = System.currentTimeMillis() / 1000.0f;
		this.lineID = rand.nextFloat();
	}

	public Line(ArrayList<Point> all) {
		initFromPoints(all);
		this.lineID = rand.nextFloat();
	}

	public Line(Point[] all) {
		ArrayList<Point> allVec = new ArrayList<Point>();
		for (int i = 0; i < all.length; i++) {
			allVec.add(all[i]);
		}
		initFromPoints(allVec);
		this.lineID = rand.nextFloat();
	}
	
	public Line normalizeLine(){
		this.makeBoundingBox();
		Line normalizedLine = new Line();
		for(int j = 0; j < this.allPoints.size(); j++){
			normalizedLine.addPoint(new Point((this.allPoints.get(j).x-xmin),(this.allPoints.get(j).y-ymin), normalizedLine.lineID));
		}
		return normalizedLine;
	}
	
	public Line shiftLine(double x, double y){
		Line shiftedLine = new Line();
		
		for(int j = 0; j < this.allPoints.size(); j++){
			shiftedLine.addPoint(new Point((this.allPoints.get(j).x + (float)x), (this.allPoints.get(j).y+ (float)y)));
		}
		
		return shiftedLine;
	}
	
	private void initFromPoints(ArrayList<Point> all) {
		allPoints = all;
		initParam();
	}

	private void initParam() {
		parameter.clear();
		parameter.add(0.0f);
		for (int i = 1; i < allPoints.size(); i++) {
			if(this.xmin == -1){
				this.xmin = allPoints.get(i).x;
			}
			
			if(this.ymin == -1){
				this.ymin = allPoints.get(i).y;
			}
				
			if(this.xmax == -1){
				this.xmax = allPoints.get(i).x;
			}
				
			if(this.ymax == -1) {
				this.ymax = allPoints.get(i).y;
			} 
			if(xmin > -1 || ymin > -1 || xmax > -1 || ymax > -1) {
				if (allPoints.get(i).x < xmin) {
					xmin = allPoints.get(i).x;
				} else if (allPoints.get(i).x > xmax) {
					xmax = allPoints.get(i).x;
				}
				if (allPoints.get(i).y < ymin) {
					ymin = allPoints.get(i).y;
				} else if (allPoints.get(i).y > ymax) {
					ymax = allPoints.get(i).y;
				}
			}
			PVector d = PVector.sub(allPoints.get(i), allPoints.get(i - 1));
			parameter.add(parameter.get(parameter.size() - 1) + d.mag());
		}
	}

	public void draw(PGraphics buffer) {
		for (int i = 0; i < allPoints.size(); i++) {
			if (i < allPoints.size() - 1) {
				PVector p1 = allPoints.get(i);
				PVector p2 = allPoints.get(i + 1);
				buffer.line(p1.x, p1.y, p2.x, p2.y);
			}
		}
	}

	public void makeBoundingBox() {
		// create the bounding box after the end of the line
		for (int i = 0; i < allPoints.size(); i++) {
			PVector p1 = allPoints.get(i);
			if (xmin == -1 && ymin == -1 && xmax == -1 && ymax == -1) {
				xmin = p1.x;
				xmax = p1.x;
				ymin = p1.y;
				ymax = p1.y;
			} else {
				if (p1.x < xmin) {
					xmin = p1.x;
				} else if (p1.x > xmax) {
					xmax = p1.x;
				}
				if (p1.y < ymin) {
					ymin = p1.y;
				} else if (p1.y > ymax) {
					ymax = p1.y;
				}
			}
		}
		PVector origin = new PVector(xmin, ymin);
		float recWidth = xmax - xmin;
		float recHeight = ymax - ymin;
		myBoundingBox = new Rectangle((int) origin.x, (int) origin.y, (int) recWidth, (int) recHeight);
	}
	
	public boolean contains(double x, double y){
		if(x >= xmin && x <= xmax && y >= ymin && y <= ymax){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean insideBufferZone(PVector loc) {
		if (allPoints.isEmpty())
			return false;
		PVector endPoint = getPoint(-1);
		float xDiff = loc.x - endPoint.x;
		float yDiff = loc.y - endPoint.y;
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff) <= 10; // make a buffer zone raduis of 10 pixels
	}

	public void addPoint(Point p) {// manually add a point to allPoints
		Point lastP = new Point(p.x, p.y, lineID); 
		lastP.setTime(System.currentTimeMillis()); 
		float lastParam = 0;
		if (0 < allPoints.size()) {
			lastP = getPoint(-1);
			lastParam = getParameter(-1);
		} else {
			lastP = p;
		}
		if (xmin == -1 && ymin == -1 && xmax == -1 && ymax == -1) {
			xmin = p.x;
			xmax = p.x;
			ymin = p.y;
			ymax = p.y;
		} else {
			if (p.x < xmin) {
				xmin = p.x;
			} else if (p.x > xmax) {
				xmax = p.x;
			}
			if (p.y < ymin) {
				ymin = p.y;
			} else if (p.y > ymax) {
				ymax = p.y;
			}
		}
		allPoints.add(p);
		parameter.add(lastParam + Point.sub(lastP, p).mag());
	}
	
	public void setGroupID(float groupID){
		this.groupID = groupID;
	}
	
	public float getGroupID(){
		return this.groupID;
	}

	public void printPoints() {
		// println("The function is not implemented, please code me~!");
		for (int i = 0; i < allPoints.size(); i++) {
			System.out.println(allPoints.get(i).x + " " + allPoints.get(i).y);
		}
	}

	public float getTotalDistance() {
		if (0 < parameter.size()) {
			return getParameter(-1);
		} else {
			return 0;
		}
	}

	public float getTotalTime() {
		float totalTime = endTime - startTime;
		return totalTime;
	}

	public float getAverageVelocity() {
		// println("In averageVelocity");
		float averageVelocity = getTotalDistance() / getTotalTime();
		// in pixels/millis
		return averageVelocity;
	}

	public Rectangle getBoundingBox() {
		return myBoundingBox;
	}

	public void drawBoundingBox(PGraphics buffer) {
		myBoundingBox.drawRect(buffer);
	}

	public ArrayList<Point> getAllPoints() {
		return allPoints;
	}

	public Point getPoint(int i) {
		return allPoints.get((allPoints.size() + i) % allPoints.size());
	}

	public Point getPointAt(float target) {
		if (target <= 0)
			return allPoints.get(0);
		if (target >= getParameter(-1))
			return getPoint(-1);
		int pointIndex = Collections.binarySearch(parameter, target);

		if (pointIndex < 0) {
			pointIndex = -(pointIndex + 1);
		}

		float paramA = parameter.get(pointIndex - 1);
		float paramB = parameter.get(pointIndex);
		float fractionA = (target - paramA) / (paramB - paramA);
		Point vectorA = allPoints.get(pointIndex - 1);
		Point vectorB = allPoints.get(pointIndex);
		return Point.LerpPoint(vectorA, vectorB, fractionA);
	}

	public PVector getKDerivativeAt(float target, int k) {
		if (0 == k) {
			return getPointAt(target);
		} else {
			float halfstep = getTotalDistance() / 20;
			float paramA = Math.max(0.0f, target - halfstep);
			float paramB = Math.min(target + halfstep, getTotalDistance());
			if (paramA == paramB) {
				return new PVector();
			}
			PVector vecA = getKDerivativeAt(paramA, k - 1);
			PVector vecB = getKDerivativeAt(paramB, k - 1);
			PVector d = PVector.sub(vecB, vecA);
			d.mult(1.0f / (paramB - paramA));
			return d;
		}
	}

	public Float getRoundingRadius(float target) {
		float curvature = getKDerivativeAt(target, 2).mag();
		if (0 == curvature) {
			return Float.MAX_VALUE;
		} else {
			return 1.0f / curvature;
		}
	}

	public Float getParameter(int i) {
		return parameter.get((parameter.size() + i) % parameter.size());
	}

	public float intersectRay(PVector rayStart, PVector rayDir) {
		float closestIntersection = Float.MAX_VALUE;
		float intersectionParameter = -1.0f;
		for (int i = 1; i < allPoints.size(); i++) {
			PVector segment = PVector.sub(allPoints.get(i),
					allPoints.get(i - 1));
			PVector segnorm = segment.get();
			segnorm.normalize();
			PVector dvec = PVector.sub(rayStart, allPoints.get(i - 1));
			PVector ortD = PVecUtilities.ortogonalization(dvec, rayDir);
			PVector ortDir = PVecUtilities.ortogonalization(segnorm, rayDir);
			float t = ortD.magSq() / ortD.dot(ortDir);
			if (t < 0 || t > (parameter.get(i) - parameter.get(i - 1))) {
				continue;
			}
			PVector xpt = PVector.add(allPoints.get(i - 1),
					PVector.mult(segnorm, t));
			PVector rayD = PVector.sub(xpt, rayStart);
			float raydDotRaydir = rayD.dot(rayDir);
			if (raydDotRaydir < 1.0f) {
				continue;
			}
			float tray = rayD.magSq() / raydDotRaydir;
			if (tray < 1.0f || tray > closestIntersection) {
				continue;
			}
			closestIntersection = tray;
			intersectionParameter = parameter.get(i - 1) + t;
		}
		return intersectionParameter;
	}

	public int getSize() {
		return allPoints.size();
	}

	public float getMaxY() {
		return ymax;
	}

	public float getMaxX() {
		return xmax;
	}

	public float getRectHeight() {
		if (myBoundingBox != null) {
		
		return myBoundingBox.getHeight();
		}
		else {
			return 0;
		}
	
		
	}

	public void setLineID(float lineID) {
		this.lineID = lineID;
	}

	public float getLineID() {
		return lineID;
	}

	public void printLineID() {
		System.out.println(lineID);
	}
	
	public void setColor(float r, float g, float b, float a) {		
		try{
			this.colorA = a;
			this.colorG = g;
			this.colorB = b;
			this.colorR = r;
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	
	public void setThickness(float thickness){
		this.lineWidth = thickness;
	}

	public float getThickness(){
		return this.lineWidth;
	}
	
	public int segmentsTotal() {
		return allPoints.size() - 1;
	}
	
	public LineSegment getSegment(int i) {
		return new LineSegment(allPoints.get(i), allPoints.get(i+1));
	}
}