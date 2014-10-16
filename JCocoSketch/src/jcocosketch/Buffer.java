package jcocosketch;

import processing.core.*;
import processing.data.StringList;

import java.awt.Color;
import java.util.*;

public class Buffer {
	ArrayList<Line> allLines = new ArrayList<Line>();
	ArrayList<ArrayList<Line>> allGroups = new ArrayList<ArrayList<Line>>();
	PImage img;
	PGraphics buffer;
	PGraphics buffer2; 
	PGraphics graphics; 
	boolean diff = true;
	boolean transparent = false;
	boolean showComp = false; 
	PApplet master;
	QuadTree mainTree;
	LassoLine lassoLine;


	public Buffer(PApplet master, PGraphics graphics, int width, int height) {
		this.master = master; 
		
		buffer = master.createGraphics(width, height, PApplet.JAVA2D);
		System.out.println("Width: " + width + " Height: " + height); 
		//buffer = master.createGraphics(2160, 1440, PApplet.JAVA2D);
		this.graphics = graphics;
		//mainTree = new QuadTree(0, 0, 2160+200, 1440+200);
		mainTree = new QuadTree(0, 0, width, height);

	}


	// Need to integrate this for color. Keep a record of all the lines
	//independent from the segments that have been printed. 
	public void update() { 
		System.out.println("Update Called");
		buffer.beginDraw(); 
		buffer.background(255);
		//buffer.smooth();
		buffer.noFill();
		for (int i = 0; i < allLines.size(); i++) 
		{ 
			Line l = allLines.get(i);
			buffer.strokeWeight(1);
			if(showComp && l.compGenerated)
				buffer.stroke(0,0,255);
			else buffer.stroke(0); 
			for (int j= 0; j < l.allPoints.size() - 1; j++) {
				PVector p1 = l.allPoints.get(j); 
				PVector p2 = l.allPoints.get(j+1);
				
				//Create points from PVector points
				Point point1 = new Point(p1.x,p1.y,l.lineID);
				Point point2 = new Point(p2.x,p2.y,l.lineID);
				if(l.getGroupID() != 1){
					point1.setGroupID(l.getGroupID());
					point2.setGroupID(l.getGroupID());
					//System.out.println(point1.getGroupID());
					//System.out.println(point2.getGroupID());
				}
				
				buffer.line(p1.x, p1.y, p2.x,p2.y);
				mainTree.set(point1.getX(),point1.getY(),point1);
				mainTree.set(point2.getX(),point2.getY(),point2);
				//System.out.println("QuadTree: " + mainTree.getCount());
			}
			diff=false;
		}
		Point[] keys = mainTree.getKeys();
		
		buffer.endDraw();
		img = buffer.get(0, 0, buffer.width, buffer.height);
		diff=true; 

		//Lasso recognition and contains happens here	
		if(lassoLine == null){
			System.out.println("No Lasso Line");
		}
		else{
			lassoLine.action(this);
			lassoLine = null;
		}
		//Number of groups with lines completely in it
		System.out.println(allGroups.size());
	}


	public void addToBuffer(Line l) {
		allLines.add(l);
		// update();
		diff = true;
	}

	public void addSegment(LineSegment l){
		int padding = 5; 
		PVector p1 = l.start; 
		PVector p2 = l.end; 
		PVector linePos = new PVector(Math.min(p1.x, p2.x), Math.min(p1.y,p2.y));
		
		PVector newP1 = new PVector(0,0); 
		PVector newP2 = new PVector(0,0); 
		double bWidth = Math.abs(p1.x - p2.x); 
		double bHeight = Math.abs(p1.y - p2.y); 
		double buffWidth = padding*2 + bWidth; 
		double buffHeight = padding*2 + bHeight; 
		PVector buffPos = new PVector(linePos.x - padding, linePos.y - padding); 
		double deltaW = Math.ceil(bWidth) - bWidth; 
		double deltaH = Math.ceil(bHeight) - bHeight; 
		bWidth = Math.ceil(bWidth); //creating a buffer around the lineSeg
		bHeight	= Math.ceil(bHeight); 
		if(bWidth < 1) bWidth = 1; 
		if(bHeight <1) bHeight = 1; 
		
		newP1.x = p1.x - linePos.x + padding;
		newP1.y = p1.y - linePos.y + padding; 
		newP2.x = p2.x - linePos.x + padding; 
		newP2.y = p2.y - linePos.y + padding; 
					
		buffer2 = master.createGraphics((int)buffWidth, (int)buffHeight, PApplet.JAVA2D); 
		//get the background image of the buffer to paste over
		PImage backgroundImage = buffer.get((int)buffPos.x, (int)buffPos.y,buffer2.width, buffer2.height); 
		buffer2.beginDraw(); 
		buffer2.smooth(); 
		buffer2.background(0,0); 
		buffer2.image(backgroundImage, buffer2.width, buffer2.height ); 
		buffer2.strokeWeight(1);
		buffer2.stroke(0);
		buffer2.smooth();
		buffer2.line(newP1.x , newP1.y, newP2.x, newP2.y);
		buffer2.endDraw();
		PImage tempImage = buffer2.get(0, 0, buffer2.width, buffer2.height); 
		
		buffer.beginDraw();
		buffer.image(tempImage, (float)(buffPos.x  + deltaW), (float)(buffPos.y  + deltaH)); 
		buffer.endDraw(); 
		img = buffer.get(0,0,buffer.width,buffer.height); 
		diff = true; 
	}
	/*
	public void addSegment(LineSegment l) {
		buffer.beginDraw();
		buffer.background(255);
		buffer.strokeWeight(1);
		buffer.stroke(0);
		buffer.smooth();
		if (img != null)
			buffer.image(img, 0, 0);
		buffer.stroke(0);
		buffer.line(l.start.x, l.start.y, l.end.x, l.end.y);
		img = buffer.get(0, 0, buffer.width, buffer.height);
		buffer.endDraw();
	}
	*/

	public PImage getImage() {
		return img;
	}

	public void clear() {
		allLines = new ArrayList<Line>();
		img = new PImage();
		update();
	}
}
