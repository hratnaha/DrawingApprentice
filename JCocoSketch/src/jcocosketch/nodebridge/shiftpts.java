package jcocosketch.nodebridge;

import processing.core.*;
import java.util.*;
import jcocosketch.*;

public class shiftpts {
	ArrayList<SketchPoint> allPoints = new ArrayList<SketchPoint>();
	long startTime;
	public shiftpts(long strokeTime){
		this.startTime = strokeTime;
	}
	public void addPoint(int x, int y, long timestamp, String id){
		SketchPoint pt = new SketchPoint(x, y, timestamp, id);
		this.allPoints.add(pt);
	}
	public ArrayList<SketchPoint> shiftTen(){
		ArrayList<SketchPoint> result = new ArrayList<SketchPoint>();
		try{
			//System.out.println("here1!!");
			Line curline = new Line();
			//System.out.println("here2!!");
			for(SketchPoint pt : this.allPoints){
				PVector newpt = new PVector();
				newpt.x = pt.x + 10;
				newpt.y = pt.y + 10;
	//			System.out.println("java: " + pt.timestamp);
				
				curline.addPoint(newpt);
				
			}
			
//			System.out.println("here2!!");
			Decision_Engine engine = new Decision_Engine(curline, null, (float)Math.sqrt(Math.pow(500, 2) + Math.pow(400, 2)));
			Line newline = engine.decision();
			
			ArrayList<PVector> pts = newline.getAllPoints();
//			System.out.println("here3!!");
//			System.out.println(pts.size());
			int i=0;
			for(PVector pt : pts){
				SketchPoint newpt = new SketchPoint();
				newpt.x = pt.x;
				newpt.y = pt.y;
				newpt.id = this.allPoints.get(i).id;
				newpt.timestamp = this.allPoints.get(i).timestamp;
				result.add(newpt);
				i++;
			}
			
			
			return result;
//			return this.allPoints;
		}catch(Exception e){
		
		}
		return null;
	}
	public int PtCount(){
		return this.allPoints.size();
	}
}
