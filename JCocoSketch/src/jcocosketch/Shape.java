package jcocosketch;

import processing.core.*;
import java.util.*;

class Shape {
	// A Shape is a collection of lines the user draws and assigns a label to
	// Shapes are associated with the TeachMe/DrawMe features
	public ArrayList<Line> allLines = new ArrayList<Line>();
	public String ID = "";
	public PVector pos = new PVector(0, 0);
	public float w = 0;
	public float h = 0;
	PVector min = null;
	PVector max = null;

	public Shape() {
	}

	public void addLine(Line line) {
		this.allLines.add(line);
		// line.calculateSegments();
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}

	public void completeShape() {
		calculateBounds();
		w = max.x - min.x;
		h = max.y - min.y;
	}

	public Shape createInstance(PVector pos, float w, float h) {
		// create a new array list and width, etc. right here, don't keep it up
		ArrayList<Line> editLines = new ArrayList<Line>();
		// ###setDimensions
		System.out.println("w: " + this.w + " h: " + this.h + " input W: " + w + " input H: " + h);
		float xScale = w / this.w;
		float yScale = h / this.h;
		System.out.println("xScale: " + xScale + "yScale" + yScale);
		for (int i = 0; i < allLines.size(); i++) {
			Line l = allLines.get(i);
			Line newLine = new Line();
			for (int j = 0; j < l.allPoints.size(); j++) {
				PVector p = new PVector(l.allPoints.get(j).x,
						l.allPoints.get(j).y);
				p.x = p.x * xScale;
				p.y = p.y * yScale;
				newLine.addPoint(p);
			}
			editLines.add(newLine);
			System.out.println("Added a new Line: " + newLine.allPoints.size());
		}
		Rectangle testRec = new Rectangle(editLines);
		// this is calculating the bounds of the original thing, rather than the
		// new one, i have the bounds of the new one
		calculateBounds();
		// setPosition
		PVector pt1 = new PVector(pos.x, pos.y);
		PVector pt2 = testRec.origin;
		PVector diff = PVector.sub(pt2, pt1);
		System.out.println("Pt1: " + pt1 + " +  Pt2: " + pt2 + " Diff: " + diff);
		for (int i = 0; i < editLines.size(); i++) {
			Line l = editLines.get(i);
			for (int j = 0; j < l.allPoints.size(); j++) {
				PVector pt = l.allPoints.get(j);
				pt.sub(diff);
			}
		}
		Shape myShape = new Shape();
		myShape.allLines = editLines;
		myShape.pos = pos;
		myShape.w = w;
		myShape.h = h;
		return myShape;
	}

	public void calculateBounds() {
		for (int i = 0; i < allLines.size(); i++) {
			Line l = allLines.get(i);
			PVector s = l.allPoints.get(0);
			min = new PVector(s.x, s.y);
			max = new PVector(s.x, s.y);
			for (int j = 0; j < l.allPoints.size(); j++) {
				PVector p = l.allPoints.get(j);
				// println("Current point p= " + p + "MinX = " + min.x +
				// " maxX: " + max.x);
				if (p.x < min.x) {
					min.x = p.x;
				} else if (p.x > max.x) {
					max.x = p.x;
				}
				if (p.y < min.y) {
					min.y = p.y;
				} else if (p.y > max.y) {
					max.y = p.y;
				}
			}
		}
		this.pos = new PVector(min.x, max.y);
		System.out.println("In shape, calc bounds. Final Min.x: " + min.x + "min.y: "
				+ min.y + " max.x: " + max.x + "max.y: " + max.y + "Origin: "
				+ new PVector(min.x, max.y));
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
		return h;
	}

	public void setHeight(float height) {
		h = height;
	}

	public void setWidth(float width) {
		w = width;
	}

	public PVector getPos() {
		return pos;
	}
}
