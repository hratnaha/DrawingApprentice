package jcocosketch;

import java.util.ArrayList;

import processing.core.*; 

public class DrawBackStack {
	private ArrayList<Line> stack = new ArrayList<Line>();
	private float passedSegments = 0.0f;
	private int totalSegments = 0;
	private float redrawSpeed = 1.0f;
	private PImage icon;  

	
	public void push(Line line) {
		stack.add(line);
		totalSegments += line.segmentsTotal();
	}
	
	public void draw(PGraphics graphics, Buffer buffer) {
		int segsDrawn = 0; 
		if (stack.size() > 0) {
			Line l = stack.get(0);
			
			float suggestedSpeed = suggestedSpeed();
			if (redrawSpeed < suggestedSpeed || 0.0f == passedSegments) redrawSpeed = suggestedSpeed;
			
			int start = (int) passedSegments;
			passedSegments += redrawSpeed;
			int end = Math.min((int) passedSegments, l.segmentsTotal());

			LineSegment currentSegment = null; 
			for (int i = start; i < end; i++) {
				currentSegment = l.getSegment(i);
				buffer.addSegment(currentSegment); 
				segsDrawn = i; 
			} 
			graphics.fill(255,255,0); 
			graphics.imageMode(PConstants.CENTER); 
			if(currentSegment!= null)
					graphics.image(icon,currentSegment.end.x, currentSegment.end.y);
			graphics.imageMode(PConstants.CORNER); 
			
			graphics.fill(0); 
			if (l.segmentsTotal() == end) {
				buffer.addToBuffer(stack.get(0)); 
				stack.remove(0);
				totalSegments -= segsDrawn; 
				segsDrawn = 0; 
				passedSegments = 0.0f;
				//if (stack.size() == 0)
					//buffer.update(); 
			}
		}
	}
	
	public float suggestedSpeed() {
		return Math.max(totalSegments / 100.0f, 1.0f);
	}
	
	public void setIcon(PImage img){
		icon = img; 
	}
	public double getSize(){
		return stack.size(); 
	}
	
}
