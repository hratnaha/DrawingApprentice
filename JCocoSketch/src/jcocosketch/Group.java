package jcocosketch;

import java.util.ArrayList;

import processing.core.PVector;

public class Group {
	ArrayList<Line> lines = new ArrayList<Line>();
	private float groupID;
	private float xmin = -1;
	private float ymin = -1;
	private float xmax = -1;
	private float ymax = -1;
	//private Group normalizedGroup;
	
	public Group(){
		groupID = -1;
	}
	
	public Group(ArrayList<Line> linesInGroup){
		this.lines = linesInGroup;
	}
	
	public Group(ArrayList<Line> linesInGroup, float groupID){
		this.lines = linesInGroup;
		this.groupID = groupID;
	}
	
	//THE PROBLEM IS THE XMIN AND YMIN FOR GROUPS IS SET TO -1 and for some reason never updated
	
	public void addLine(Line line){
		line.makeBoundingBox();
		System.out.println(line.xmin + " " + line.ymin);
		this.lines.add(line);
		if(this.xmin == -1){
			this.xmin = line.xmin;
		}
		
		if(this.ymin == -1){
			this.ymin = line.ymin;
		}
			
		if(this.xmax == -1){
			this.xmax = line.xmax;
		}
			
		if(this.ymax == -1) {
			this.ymax = line.ymax;
		} 
		if(line.xmin > -1 || line.ymin > -1 || line.xmax > -1 || line.ymax > -1) {
			if (line.xmin < xmin && line.xmin > -1) {
				xmin = line.xmin;
			} if (line.xmax > xmax && line.xmax > -1) {
				xmax = line.xmax;
			}
			if (line.ymin < ymin || line.ymin > -1) {
				ymin = line.ymin;
			} if (line.ymax > ymax || line.ymax > -1) {
				ymax = line.ymax;
			}
		}
		
		Line normalizedLine = new Line();
		for(int j = 0; j < line.allPoints.size(); j++){
			normalizedLine.addPoint(new Point((line.allPoints.get(j).x-xmin),(line.allPoints.get(j).y-ymin), normalizedLine.lineID));
		}
//		normalizedGroup.addLine(normalizedLine);
		
	}
	
	public void removeLine(Line line){
		this.lines.remove(line);
	}
	
	public Group normalizedGroup(){
		Group normalizedGroup = new Group();
		
		for(int i = 0; i < this.lines.size(); i++){
			Line normalizedLine = new Line();
			for(int j = 0; j < this.lines.get(i).allPoints.size(); j++){
				normalizedLine.addPoint(new Point((lines.get(i).allPoints.get(j).x-this.xmin),(lines.get(i).allPoints.get(j).y-this.ymin), normalizedLine.lineID));
				//normalizedLine.addPoint(new PVector(12,12));
			}
			normalizedGroup.addLine(normalizedLine);
		}

		return normalizedGroup;
	}
	
	//Setters and Getters
	public void setGroupID(float groupID){
		this.groupID = groupID;
//		normalizedGroup.setGroupID(groupID);
	}
	
	public float getGroupID(){
		return this.groupID;
	}
	
	public int getSize(){
		return lines.size();
	}

	public float getXmin() {
		return xmin;
	}

	public void setXmin(float xmin) {
		this.xmin = xmin;
	}

	public float getYmin() {
		return ymin;
	}

	public void setYmin(float ymin) {
		this.ymin = ymin;
	}

	public float getXmax() {
		return xmax;
	}

	public void setXmax(float xmax) {
		this.xmax = xmax;
	}

	public float getYmax() {
		return ymax;
	}

	public void setYmax(float ymax) {
		this.ymax = ymax;
	}
	

}
