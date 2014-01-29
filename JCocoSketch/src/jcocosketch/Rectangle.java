package jcocosketch;

import processing.core.*;
import java.util.*;

class Rectangle {
	// Rectangle can take a array list of lines and create a bounding rectangle
	// around those lines
	float w;
	float h;
	PVector pos2;// = pos;
	ArrayList<Line> lines;
	PVector min, max, origin, pos;

	public Rectangle(PVector pos, float width, float height) {
		this.w = width;
		this.h = height;
		this.pos = pos;
	}

	public Rectangle(ArrayList<Line> lines) {
		this.lines = lines;
		calculateBounds();
	}

	public void drawRect(PGraphics buffer) {
		// need to draw the rect, but it is not added to stack, maybe it is
		// about the placement before background.
		buffer.fill(255, 150);
		buffer.stroke(0);
		buffer.strokeWeight(1);
		buffer.rectMode(PApplet.CORNERS);
		buffer.rect(pos.x, pos.y, pos2.x, pos2.y);
		buffer.strokeWeight(1);
		buffer.rectMode(PApplet.CORNER);
	}

	public void setWidth(float width) {
		w = width;
	}

	public void calculateBounds() {
		// calculateBounds copy:
		// get new origin point
		for (int i = 0; i < lines.size(); i++) {
			Line l = lines.get(i);
			PVector s = l.allPoints.get(0);
			min = new PVector(s.x, s.y);
			max = new PVector(s.x, s.y);
			// println("Number 2 Min: " + min + " Max: " + max);
			// println("Allpoints size: " +l.allPoints.size());
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
		this.w = max.x - min.x;
		this.h = max.y - min.y;
		this.origin = new PVector(min.x, max.y);
		// println("Final Min-Mix. Min.x: " + min.x + "min.y: " + min.y +
		// " max.x: " + max.x + "max.y: " + max.y);
	}

	public void setPos2(PVector pos2) {
		// println("Pos2 = " + pos2 + "Pos1: " + pos);
		this.pos2 = pos2;
		float x1 = Math.max(pos.x, this.pos2.x);
		float x2 = Math.min(pos.x, this.pos2.x);
		float y1 = Math.max(pos.y, this.pos2.y);
		float y2 = Math.min(pos.y, this.pos2.y);
		w = x1 - x2;
		h = y1 - y2;
		this.origin = new PVector(x2, y1);
		// println("In Rectangle class- new w: " + w + " h: " + h);
		// here is the bug
	}

	public void setHeight(float height) {
		h = height;
	}

	public void setPos(PVector pos) {
		this.pos = pos;
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
		return h;
	}

	public PVector getPos() {
		return pos;
	}
}
