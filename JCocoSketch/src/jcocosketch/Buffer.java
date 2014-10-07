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
	Line lassoLine;


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
				
				buffer.line(p1.x, p1.y, p2.x,p2.y);
				mainTree.set(point1.getX(),point1.getY(),point1);
				mainTree.set(point2.getX(),point2.getY(),point2);
				//System.out.println("QuadTree: " + mainTree.getCount());
			}
		}
		Point[] keys = mainTree.getKeys();
		
		//Testing the Quad Tree
//		for(int i=0; i<keys.length;i++){
//			//System.out.println("Keys(Points): " + keys[i]);
//			//Getting the main nodes for root (the largest nodes)
//			Node temp = mainTree.getQuadrantForPoint(mainTree.getRootNode(), keys[i].getX(), keys[i].getY());
//			//Prints out these nodes with it's children depending on the point. There can be more children if necessary
//			System.out.println(" Point: (" + keys[i].getX() + "," + keys[i].getY() +") Subnodes: "
//					+ temp.toString() + " NE " + temp.getNe().toString()+ " NW " + temp.getNw().toString()
//					+ " SE " + temp.getSe().toString()+ " SW " + temp.getSw().toString());
//		}
		//Testing out Point Class and QuadTree interaction
//		ArrayList<PVector> linePoints = allLines.get(allLines.size()-1).allPoints;
//		for(int i = 0; i < linePoints.size(); i++){
//			Point testPoint = new Point(linePoints.get(i).x,linePoints.get(i).y,allLines.get(allLines.size()-1).lineID);
//		}
		//System.out.println(mainTree.getIndex(new Point(linePoints.get(0).x,linePoints.get(0).y,allLines.get(allLines.size()-1).lineID)));
		buffer.endDraw();
		img = buffer.get(0, 0, buffer.width, buffer.height);
		diff=true; 
		Random randy = new Random();
		int numLines = 0;
		
		//Lasso recognition and contains happens here
		if(lassoLine == null){
			System.out.println("No Lasso Line");
		}
		else{
			float groupID = randy.nextFloat();
			boolean isInLasso = false;
			ArrayList<Line> linesInGroup = new ArrayList<Line>();
			for (int i = 0; i < allLines.size(); i++) 
			{ 
				Line l = allLines.get(i);
				isInLasso = false;
				for (int j= 0; j < l.allPoints.size(); j++) {
					PVector tempPoint = l.allPoints.get(j);
					if(!lassoLine.contains(tempPoint.x, tempPoint.y)){
						isInLasso = false;
						j = l.allPoints.size();
					}
					else{
						isInLasso = true;
						//System.out.println(tempPoint.x + " " + tempPoint.y);
					}
				}
				if(isInLasso){
					l.setGroupID(groupID);
					numLines++;
					linesInGroup.add(l);
					isInLasso = false;
				}
			}
			//Adds new group of lines if there is at least one line 100% contained in the lasso
			if(linesInGroup.size() > 0){
				allGroups.add(linesInGroup);
			}
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
