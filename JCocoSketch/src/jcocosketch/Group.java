package jcocosketch;

import java.util.ArrayList;

import processing.core.PVector;

public class Group {
	ArrayList<Line> lines = new ArrayList<Line>();
	private float groupID;
	private float xmin = 0;
	private float ymin = 0;
	private float xmax = 0;
	private float ymax = 0;
	//private Group normalizedGroup;
	
	public Group(){
		groupID = -1;
	}
	
	public void addLine(Line line){
		this.lines.add(line);
		if(this.xmin == 0 && this.ymin == 0 && this.xmax == 0 && this.ymax == 0) {
			this.xmin = line.xmin;
			this.xmax = line.xmax;
			this.ymin = line.ymin;
			this.ymax = line.ymax;
		} else {
			if (line.xmin < xmin) {
				xmin = line.xmin;
			} else if (line.xmax > xmax) {
				xmax = line.xmax;
			}
			if (line.ymin < ymin) {
				ymin = line.ymin;
			} else if (line.ymax > ymax) {
				ymax = line.ymax;
			}
		}
		
		Line normalizedLine = new Line();
		for(int j = 0; j < line.allPoints.size(); j++){
			normalizedLine.addPoint(new PVector((line.allPoints.get(j).x-xmin),(line.allPoints.get(j).y-ymin)));
		}
//		normalizedGroup.addLine(normalizedLine);
		
	}
	
	public void removeLine(Line line){
		this.lines.remove(line);
	}
	
	public Group normalizedGroup(){
		Group normalizedGroup = new Group();
		
		for(int i = 0; i < lines.size(); i++){
			Line normalizedLine = new Line();
			for(int j = 0; j < lines.get(i).allPoints.size(); j++){
				normalizedLine.addPoint(new PVector((lines.get(i).allPoints.get(j).x-this.xmin),(lines.get(i).allPoints.get(j).y-this.ymin)));
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
