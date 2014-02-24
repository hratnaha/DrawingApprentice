package jcocosketch;

import java.util.ArrayList;

import processing.core.PGraphics;

public class DrawBackStack {
	private ArrayList<Line> stack = new ArrayList<Line>();
	private float passedSegments = 0.0f;
	private int totalSegments = 0;
	private float redrawSpeed = 1.0f;
	
	public void push(Line line) {
		stack.add(line);
		totalSegments += line.segmentsTotal();
	}
	
	public void draw(PGraphics graphics, Buffer buffer) {
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
				currentSegment.render(graphics);
				buffer.addSegment(currentSegment);
			} 
			graphics.fill(255,255,0); 
			graphics.ellipse(currentSegment.end.x, currentSegment.end.y, 15, 15); 
			graphics.fill(0); 
			if (l.segmentsTotal() == end) {
				stack.remove(0);
				passedSegments = 0.0f;
				if (stack.size() == 0)
					System.out.println("Stack emptied");
			}
		}
	}
	
	public float suggestedSpeed() {
		return Math.max(totalSegments / 100.0f, 1.0f);
	}
}
