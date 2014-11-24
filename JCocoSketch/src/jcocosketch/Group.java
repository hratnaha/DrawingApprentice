package jcocosketch;

import java.util.ArrayList;

import processing.core.PVector;

public class Group {
	public ArrayList<Line> lines = new ArrayList<Line>();
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
		
	public void addLine(Line line){
		//line.makeBoundingBox();
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
		if (line.xmin < xmin && line.xmin > -1) {
			xmin = line.xmin;
		} if (line.xmax > xmax && line.xmax > -1) {
			xmax = line.xmax;
		}
		if (line.ymin < ymin && line.ymin > -1) {
			ymin = line.ymin;
		} if (line.ymax > ymax && line.ymax > -1) {
			ymax = line.ymax;
		}
	
		Line normalizedLine = new Line();
		for(int j = 0; j < line.allPoints.size(); j++){
			normalizedLine.addPoint(new Point((line.allPoints.get(j).x-xmin),(line.allPoints.get(j).y-ymin), normalizedLine.lineID));
		}
//		normalizedGroup.addLine(normalizedLine);
		
	}
	
	/**
	 * Shifts group by X and Y specified
	 * @param x
	 * @param y
	 * @return shiftedGroup
	 */
	public Group shiftGroup(double x, double y){
		Group shiftedGroup = new Group();
		
		for(int i = 0; i < lines.size(); i++){
			Line newLine = new Line();
			for(int j = 0; j < lines.get(i).allPoints.size(); j++){
				newLine.addPoint(new Point((lines.get(i).allPoints.get(j).x + (float)x), (lines.get(i).allPoints.get(j).y+ (float)y)));
			}
			shiftedGroup.addLine(newLine);
		}
		
		return shiftedGroup;	
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
