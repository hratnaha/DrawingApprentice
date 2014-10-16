package jcocosketch;

import java.util.ArrayList;
import java.util.Random;

import processing.core.PVector;

/**
 * Lasso Class defines a lasso object used to group lines and points.
 * 
 *
 */
public class LassoLine extends Line{
	int numLines = 0;
	
	public void action(Buffer currentBuffer){
		boolean isInLasso = false;
		ArrayList<Line> linesInGroup = new ArrayList<Line>();
		for (int i = 0; i < currentBuffer.allLines.size(); i++) 
		{ 
			Line l = currentBuffer.allLines.get(i);
			isInLasso = false;
			for (int j= 0; j < l.allPoints.size(); j++) {
				PVector tempPoint = l.allPoints.get(j);
				if(!currentBuffer.lassoLine.contains(tempPoint.x, tempPoint.y)){
					isInLasso = false;
					j = l.allPoints.size();
				}
				else{
					isInLasso = true;
					//System.out.println(tempPoint.x + " " + tempPoint.y);
				}
			}
			if(isInLasso){
				l.setGroupID(currentBuffer.allGroups.size());
				numLines++;
				linesInGroup.add(l);
				isInLasso = false;
			}
		}
		//Adds new group of lines if there is at least one line 100% contained in the lasso
		if(linesInGroup.size() > 0){
			currentBuffer.allGroups.add(linesInGroup);
		}
	}
}
