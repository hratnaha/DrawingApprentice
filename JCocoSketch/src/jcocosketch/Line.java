package jcocosketch;

import processing.core.*;

import java.util.*;

class Line {
	PVector myPoint;
	PVector start;
	PVector end;
	ArrayList<PVector> allPoints = new ArrayList<PVector>();
	ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
	float startTime;
	float endTime;
	boolean isSelected = false;
	String lineID;
	//Rectangle myBoundingBox;
	int col;

	public Line() {
		startTime = System.currentTimeMillis();
	}

	public Line(ArrayList<PVector> all) {
		start = all.get(0);
		end = all.get(all.size() - 1);
		allPoints = all;
	}

	public Line(PVector[] all) {
		start = all[0];
		end = all[all.length - 1];
		for (int i = 0; i < all.length; i++) {
			allPoints.add(all[i]);
		}
	}

	public void addSegment(LineSegment l) {
		segments.add(l);
	}

	public void calculateSegments() {
		if (segments.size() == 0) {
			System.out.println("calc segs");
			for (int i = 0; i < allPoints.size() - 1; i++) {
				LineSegment seg = new LineSegment(allPoints.get(i),
						allPoints.get(i + 1));
				segments.add(seg);
			}
		}
	}

	/*public void makeBoundingBox() {
		System.out.println("Making Bounding Box. Alllines = " + allLines);
		myBoundingBox = new Rectangle(allLines);
		myBoundingBox.calculateBounds();
	}*/

	public boolean insideBufferZone(PVector loc) {
		int radius = 10; // make a buffer zone raduis of 10 pixels
		float xDiff = loc.x - end.x;
		float yDiff = loc.y - end.y;
		return (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff) < radius;
	}

	public void addPoint(PVector p) {// manually add a point to allPoints
		allPoints.add(p);
	}

	public void setEnd(PVector p) {
		end = p;
		allPoints.add(end);
		endTime = System.currentTimeMillis();
		// makeBoundingBox();
	}

	public void setStart(PVector p) {
		start = p;
		allPoints.add(start);
	}

	public void printPoints() {
		// println("The function is not implemented, please code me~!");
		for (int i = 0; i < allPoints.size(); i++) {
			System.out.println(allPoints.get(i).x + " " + allPoints.get(i).y);
			// curPoint.printPoint();
		}
	}

	public float getTotalDistance() {
		float totalDistance = 0;
		for (int i = 0; i < allPoints.size(); i++) {
			if (i < allPoints.size() - 1) {
				PVector p1 = allPoints.get(i);
				PVector p2 = allPoints.get(i + 1);
				float currentDistance = (float)Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
				totalDistance += currentDistance;
			} else if (i == allPoints.size() - 1) {
				PVector p1 = allPoints.get(i - 1);
				PVector p2 = allPoints.get(i);
				float currentDistance = (float)Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
				totalDistance += currentDistance;
			}
		}
		return totalDistance;
	}

	public float getTotalTime() {
		float totalTime = endTime - startTime;
		return totalTime;
	}

	public float getAverageVelocity() {
		float averageVelocity = getTotalDistance() / getTotalTime();
		return averageVelocity;
	}

	/*public Rectangle getBoundingBox() {
		return myBoundingBox;
	}

	public void drawBoundingBox(PGraphics buffer) {
		myBoundingBox.drawRect(buffer);
	}*/

	public ArrayList<PVector> getAllPoints() {
		return allPoints;
	}

	public PVector getEndPoint() {
		return end;
	}

	public PVector getPoint(int i) {
		return allPoints.get(i);
	}

	public int getSize() {
		return allPoints.size();
	}

	/*public float getRectHeight() {
		float w = myBoundingBox.w;
		return myBoundingBox.h;
	}*/

	public void setLineID(String lineID) {
		this.lineID = lineID;
	}

	public String getLineID() {
		return lineID;
	}

	public void printLineID() {
		System.out.println(lineID);
	}
}
