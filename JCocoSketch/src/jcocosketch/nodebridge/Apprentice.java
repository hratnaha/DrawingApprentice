package jcocosketch.nodebridge;

import processing.core.*;

import java.util.*;

import jcocosketch.*;

public class Apprentice {
	ArrayList<SketchPoint> allPoints = new ArrayList<SketchPoint>();
	ArrayList<Line> allLines = new ArrayList<Line>();
	long startTime;
	int currentMode = 0;
	Boolean isGrouping = false;
	public Apprentice(){
		
	}
	public void setMode(int mode_code){
		System.out.println("change to mode: " + mode_code);
		currentMode = mode_code;
	}
	public void clear(){
		System.out.println("clear");
	}
	public void startGrouping(long strokeTimeStamp){
		this.startTime = strokeTimeStamp;
		isGrouping = true;
	}
	public void addNewStroke(long strokeTimeStamp){
		isGrouping = false;
		this.startTime = strokeTimeStamp;
	}
	public void addPoint(int x, int y, long timestamp, String id){
		SketchPoint pt = new SketchPoint(x, y, timestamp, id);
		this.allPoints.add(pt);
	}
	public ArrayList<SketchPoint> decision(){
		ArrayList<SketchPoint> result = new ArrayList<SketchPoint>();
		try{
			Line curline = new Line();

			for(SketchPoint pt : this.allPoints){
				Point newpt = new Point(pt.x, pt.y, curline.getLineID());
				newpt.setTime(pt.timestamp);
				curline.addPoint(newpt);
			}
			
			int offset = 3;
			Line line2;
			if(allLines.size()>offset){
				line2 = allLines.get(allLines.size()-offset);
			}
			else
			{
				line2 = curline; ///can add other shapes to mutate with
			}
			
			Decision_Engine engine = new Decision_Engine(curline, line2, (float)Math.sqrt(Math.pow(500, 2) + Math.pow(400, 2)));
			allLines.add(curline);
			
			Line newline = engine.decision();
			
			
			ArrayList<Point> pts = newline.getAllPoints();
			System.out.println(pts.size());
			//for(PVector pt : pts){
			for(int i=0;i<pts.size(); i++){
				SketchPoint newpt = new SketchPoint();
				newpt.x = pts.get(i).x;
				newpt.y = pts.get(i).y;
				newpt.id = this.allPoints.get(i).id;
				newpt.timestamp = this.allPoints.get(i).timestamp;
				result.add(newpt);
			}
			
			this.allPoints = new ArrayList<SketchPoint>();
			return result;
//			return this.allPoints;
		}catch(Exception e){
			System.out.println(e.toString());
		}
		System.out.println("return nothing");
		this.allPoints = new ArrayList<SketchPoint>();
		return null;
	}
	public int PtCount(){
		return this.allPoints.size();
	}
}
