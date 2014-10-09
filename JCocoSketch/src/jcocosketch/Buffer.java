package jcocosketch;

import processing.core.*;
import processing.data.StringList;

import java.awt.Color;
import java.util.*;

public class Buffer {
	ArrayList<Line> allLines = new ArrayList<Line>();
	PImage img;
	PGraphics buffer;
	PGraphics buffer2; 
	PGraphics graphics; 
	boolean diff = true;
	boolean transparent = false;
	boolean showComp = false; 
	PApplet master; 
	private PApplet papp;
	
	private SandPainter sp;
	public Buffer(PApplet master, PGraphics graphics) {
		this.master = master; 
		this.papp = master;
		buffer = master.createGraphics(1200, 700, PApplet.JAVA2D);
		this.graphics = graphics; 
		sp = new SandPainter(master);
	}


	// Need to integrate this for color. Keep a record of all the lines
	//independent from the segments that have been printed. 
	public void update() { 
		System.out.println("Update Called"); 
		buffer.beginDraw(); 
		buffer.background(255);
		buffer.smooth();
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
				buffer.line(p1.x, p1.y, p2.x,p2.y); 
				
				
		}
		buffer.endDraw(); 
		img = buffer.get(0, 0, buffer.width, buffer.height);
		//diff=true; 
		diff = false;
	}
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
		//SKETCH FUNCTION TEST
		//add sketch
		/*float t =this.random(10f, 80f);
		float rx = 30f;
		float ry = 35f;
		//new SandPainter(this).regionColor(30f, 35f, 180);
		for(int k=0 ;k<50;++k){
			
			rx+=0.81*PApplet.sin(t*PApplet.PI/180);
		      ry-=0.81*PApplet.cos(t*PApplet.PI/180);
		      int cx = (int)(rx);
		      int cy = (int)(ry);
		      float x = rx;
		      float y = ry;
		      buffer.ellipse(p1.x, p1.y, 50f, 50f);
		      float ox = p1.x + k;
		      float oy = p1.y + master.random(0, 3) ;
		      float g = master.random(-0.050f,0.050f);
				float maxg = 1.0f;
				if (g<0) g=0;
				if (g>maxg) g=maxg;

				// calculate grains by distance
				//int grains = int(sqrt((ox-x)*(ox-x)+(oy-y)*(oy-y)));
				int grains = 64;

				// lay down grains of sand (transparent pixels)
				float w = g/(grains-1);
				for (int f=0;f<grains;f++) {
					float a = (float) (0.1-f/(grains*10.0f));
					buffer.stroke(master.red(master.color(200)),master.green(master.color(200)),master.blue(master.color(200)),a*256);
					buffer.point(ox+(x-ox)*sin(sin(f*w)),oy+(y-oy)*sin(sin(f*w)));
				}
		}
		//SKETCH FUNCTION END
		*/
		buffer2.endDraw();
		
		PImage tempImage = buffer2.get(0, 0, buffer2.width, buffer2.height); 
		
		buffer.beginDraw();
		buffer.image(tempImage, (float)(buffPos.x  + deltaW), (float)(buffPos.y  + deltaH)); 
		//sketch
		//for(int ih = 0; ih<30; ih++)
	    //	sketch(newP1.x  , newP1.y, newP2.x , newP2.y );
		//end sketch
		
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
	
	public void sketch(float x, float y, float ox, float oy) {
	 	int c = (int)random(100, 250);
	 
	    // modulate gain
	    float g=random(-0.050,0.050);
	    float maxg = (float) 1.0;
	    if (g<0) g=0;
	    if (g>maxg) g=maxg;
	    
	    // calculate grains by distance
	    //int grains = int(sqrt((ox-x)*(ox-x)+(oy-y)*(oy-y)));
	    int grains = 64;
	    
	    // lay down grains of sand (transparent pixels)
	    float w = g/(grains-1);
	    for (int i=0;i<grains;i++) {
	      float a = (float) (0.1-i/(grains*10.0));
	     // 	master.strokeWeight(1);
	     
			buffer.stroke(master.red(c),master.green(c),master.blue(c),a*256);
	      buffer.point(ox+(x-ox)*sin(sin(i*w)),oy+(y-oy)*sin(sin(i*w)));
	    }
	  
}


private float sin(float f) {
	// TODO Auto-generated method stub
	return PApplet.sin(f);
}

private float random(double d, double e) {
	// TODO Auto-generated method stub
	return master.random((float)d, (float)e);
}

}
