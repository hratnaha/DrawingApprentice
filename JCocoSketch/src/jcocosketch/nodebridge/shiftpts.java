package jcocosketch.nodebridge;

import processing.core.*;

import java.util.*;

import jcocosketch.*;

public class shiftpts {
	ArrayList<SketchPoint> allPoints = new ArrayList<SketchPoint>();
	ArrayList<Line> allLines = new ArrayList<Line>();
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
			Line curline = new Line();

			for(SketchPoint pt : this.allPoints){
				PVector newpt = new PVector();
				newpt.x = pt.x + 10;
				newpt.y = pt.y + 10;
				
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
			
			
			ArrayList<PVector> pts = newline.getAllPoints();
			System.out.println(pts.size());
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
			System.out.println(e.toString());
		}
		System.out.println("return nothing");
		return null;
	}
	public int PtCount(){
		return this.allPoints.size();
	}
}
