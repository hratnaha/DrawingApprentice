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
	double threshold = 1;
	
	public void action(ArrayList<Group> allGroups, ArrayList<Line> allLines, ArrayList<Group> normalizeGroups){
		boolean isInLasso = false;
		System.out.println("In Action method");
		Group newGroup = new Group();
		newGroup.setGroupID(allGroups.size());
		ArrayList<Line> linesInGroup = new ArrayList<Line>();
		for (int i = 0; i < allLines.size(); i++) 
		{ 
			Line l = allLines.get(i);
			double inLasso = 0;
			double outLasso = 0;
			for (int j= 0; j < l.allPoints.size(); j++) {
				Point tempPoint = l.allPoints.get(j);
				if(this.contains(tempPoint.x, tempPoint.y)){
					//j = l.allPoints.size();
					inLasso++;
				}
				else{
					//System.out.println(tempPoint.x + " " + tempPoint.y);
					outLasso++;
				}
			}
			double lassoRatio = (inLasso/(inLasso+outLasso));
			System.out.println("Lasso Point Ratio: " + lassoRatio);
			if(lassoRatio>=threshold){
				l.setGroupID(allGroups.size());
				numLines++;
				linesInGroup.add(l);
				newGroup.addLine(l);
				isInLasso = false;
			}
		}
		//Adds new group of lines if there is at least one line 100% contained in the lasso
		if(linesInGroup.size() > 0){
			System.out.println("Group added start");
			allGroups.add(newGroup);
			
			System.out.println(newGroup.getXmin() + "," + newGroup.getYmin());
			
			normalizeGroups.add(allGroups.get(allGroups.size()-1).normalizedGroup());
			Group newNormGroup = normalizeGroups.get(normalizeGroups.size()-1);
			System.out.println(newNormGroup.getXmin() + "," + newNormGroup.getYmin());
		}
	}
	
	public void action(Buffer currentBuffer){
		boolean isInLasso = false;
		System.out.println("In Action method");
		Group newGroup = new Group();
		this.makeBoundingBox();
		newGroup.setXmin(this.xmin);
		newGroup.setXmax(this.xmax);
		newGroup.setYmax(this.ymax);
		newGroup.setYmin(this.ymin);
		newGroup.setGroupID(currentBuffer.allGroups.size());
		ArrayList<Line> linesInGroup = new ArrayList<Line>();
		for (int i = 0; i < currentBuffer.allLines.size(); i++) 
		{ 
			Line l = currentBuffer.allLines.get(i);
			double inLasso = 0;
			double outLasso = 0;
			for (int j= 0; j < l.allPoints.size(); j++) {
				Point tempPoint = l.allPoints.get(j);
				if(currentBuffer.lassoLine.contains(tempPoint.x, tempPoint.y)){
					//j = l.allPoints.size();
					inLasso++;
				}
				else{
					//System.out.println(tempPoint.x + " " + tempPoint.y);
					outLasso++;
				}
			}
			double lassoRatio = (inLasso/(inLasso+outLasso));
			System.out.println("Lasso Point Ratio: " + lassoRatio);
			if(lassoRatio>=threshold){
				l.setGroupID(currentBuffer.allGroups.size());
				numLines++;
				linesInGroup.add(l);
				newGroup.addLine(l);
				isInLasso = false;
			}
		}
		//Adds new group of lines if there is at least one line 100% contained in the lasso
		if(linesInGroup.size() > 0){
			System.out.println("Group added start");
			currentBuffer.allGroups.add(newGroup);
			
			System.out.println(newGroup.getXmin() + "," + newGroup.getYmin());
			
			currentBuffer.normalizedGroups.add(currentBuffer.allGroups.get(currentBuffer.allGroups.size()-1).normalizedGroup());
			Group newNormGroup = currentBuffer.normalizedGroups.get(currentBuffer.normalizedGroups.size()-1);
			System.out.println(newNormGroup.getXmin() + "," + newNormGroup.getYmin());
		}
	}
}

