package jcocosketch.intersectionResponse;

import java.util.ArrayList;
import jcocosketch.*;
import processing.core.*;

public class BezierResponse {
	private ArrayList<BezierResponsePart> leftList =  new ArrayList<BezierResponsePart>();
	private ArrayList<BezierResponsePart> rightList = new ArrayList<BezierResponsePart>();

	public void addPart(BezierResponsePart part, int side) {
		if (1 == side) {
			rightList.add(part);
		} else {
			leftList.add(part);
		}
	}
	
	public void spliceLeftRight() {
		ArrayList<BezierResponsePart> fullList = new ArrayList<BezierResponsePart>(leftList.size() + rightList.size());
		for (int i = leftList.size() - 1; i >= 0; i--) {
			fullList.add(leftList.get(i).reversed());
		}
		fullList.addAll(rightList);
		rightList = fullList;
		leftList = null;
	}
	
	public void draw(PGraphics buffer) {
		for (BezierResponsePart part : rightList) {
			part.draw(buffer);
		}
	}
	
	public Line approximation(int kPerSegment) {
		Line theApproximation = new Line();
		for (BezierResponsePart part : rightList) {
			part.buildUpApproximation(kPerSegment, theApproximation);
		}
		return theApproximation;
	}
}