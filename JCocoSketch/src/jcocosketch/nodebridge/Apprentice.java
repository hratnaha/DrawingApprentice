package jcocosketch.nodebridge;

import processing.core.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import com.mongodb.BasicDBObject;

import flexjson.JSONDeserializer;

import jcocosketch.*;

public class Apprentice {
	ArrayList<SketchPoint> allPoints = new ArrayList<SketchPoint>();
	ArrayList<Line> allLines = new ArrayList<Line>();
	long startTime;
	TrajectorMode currentMode = TrajectorMode.Local; // 0 = local, 1 = regional,
														// // 2 = global
	Boolean isGrouping = false;

	ArrayList<Group> allGroups = new ArrayList<Group>();
	ArrayList<Group> normalizedGroups = new ArrayList<Group>();
	QuadTree mainTree;
	Line line2;

	int width = 1400, height = 800;

	MongoControl mongo;
	JSONDeserializer deserializer;
	public Apprentice() {
		mainTree = new QuadTree(0, 0, width, height);
		mongo = new MongoControl();
		deserializer = new JSONDeserializer();
	}

	public void setMode(int mode_code) {
		System.out.println("change to mode: " + mode_code);
		switch (mode_code) {
		case 0:
			currentMode = TrajectorMode.Local;
			this.isGrouping = false;
			break;
		case 1:
			currentMode = TrajectorMode.Regional;
			break;
		case 2:
			currentMode = TrajectorMode.Global;
			break;
		default:
			currentMode = TrajectorMode.Local;
			break;
		}
	}

	public void vote(int isUp){
		if(isUp == 1)
			System.out.println("vote up");
		else
			System.out.println("vote down");
	}
	
	public void clear() {
		System.out.println("clear");
	}

	public void startGrouping(long strokeTimeStamp) {
		this.startTime = strokeTimeStamp;
		isGrouping = true;
	}

	public void addNewStroke(long strokeTimeStamp) {
		isGrouping = false;
		this.startTime = strokeTimeStamp;
	}

	public void addPoint(int x, int y, long timestamp, String id) {
		SketchPoint pt = new SketchPoint(x, y, timestamp, id);
		this.allPoints.add(pt);
	}

	public ArrayList<SketchPoint> decision() {
		ArrayList<SketchPoint> result = new ArrayList<SketchPoint>();
		try {
			if (!isGrouping) {

				if (currentMode == TrajectorMode.Local) {
					Line curline = new Line();

					for (SketchPoint pt : this.allPoints) {
						Point newpt = new Point(pt.x, pt.y, curline.getLineID());
						newpt.setTime(pt.timestamp);
						curline.addPoint(newpt);
						if (curline.getGroupID() != 1) {
							newpt.setGroupID(curline.getGroupID());
						}
						mainTree.set(pt.x, pt.y, newpt);
					}

					int offset = 3;
					if (allLines.size() > offset) {
						line2 = allLines.get(allLines.size() - offset);
					} else {
						line2 = curline; // can add other shapes to mutate with
					}

					Decision_Engine engine = new Decision_Engine(curline,
							line2, (float) Math.sqrt(Math.pow(500, 2)
									+ Math.pow(400, 2)));
					//add to all lines
					allLines.add(curline);
					//save lines here
					mongo.saveLine(allLines);
//					System.out.println(mongoPrint(true));
					//print out for debug
					//System.out.println(mongoPrint());
					
					Line newline = engine.decision();

					ArrayList<Point> pts = newline.getAllPoints();
					System.out.println(pts.size());
					// for(PVector pt : pts){
					for (int i = 0; i < pts.size(); i++) {
						SketchPoint newpt = new SketchPoint();
						newpt.x = pts.get(i).x;
						newpt.y = pts.get(i).y;
						result.add(newpt);
					}

					this.allPoints = new ArrayList<SketchPoint>();
					return result;
				} else if (currentMode == TrajectorMode.Regional) {

				} else if (currentMode == TrajectorMode.Global) {

				}
			}
		} catch (Exception e) {
			System.out.println(getStackTrace(e));
		}
		System.out.println("return nothing");
		this.allPoints = new ArrayList<SketchPoint>();
		return null;
	}

	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	public ArrayList<ArrayList<SketchPoint>> Grouping() {
		currentMode = TrajectorMode.Regional;
		LassoLine curLasso = new LassoLine();

		for (SketchPoint pt : this.allPoints) {
			Point newpt = new Point(pt.x, pt.y, curLasso.getLineID());
			newpt.setTime(pt.timestamp);
			curLasso.addPoint(newpt);

			mainTree.set(pt.x, pt.y, newpt);
		}

		curLasso.action(allGroups, allLines, normalizedGroups);

		this.allPoints = new ArrayList<SketchPoint>();
		return GetLinesFromLasso();
	}

	public ArrayList<ArrayList<SketchPoint>> GetLinesFromLasso() {

		try {
			if (this.allGroups.size() > 0) {

				ArrayList<ArrayList<SketchPoint>> results = new ArrayList<>();

				int size_main = this.allGroups.size() - 1;
				int size_lines = this.allGroups.get(size_main).getSize() - 1;

				for (int i = 0; i < this.allGroups.get(size_main).getSize(); ++i) {
					Line l1 = this.allGroups.get(size_main).lines.get(i);
					Decision_Engine engine = new Decision_Engine(l1, line2,
							(float) Math.sqrt(Math.pow(width, 2)
									+ Math.pow(height, 2)));

					Line l = engine.decision();
					l.compGenerated = true;

					//System.out.println("line generated");

					ArrayList<SketchPoint> result = new ArrayList<>();
					ArrayList<Point> pts = l.getAllPoints();
					System.out.println(pts.size());
					// for(PVector pt : pts){
					for (int j = 0; j < pts.size(); j++) {
						SketchPoint newpt = new SketchPoint();
						newpt.x = pts.get(j).x;
						newpt.y = pts.get(j).y;
						//newpt.id = this.allPoints.get(j).id;
						newpt.timestamp = System.nanoTime();//this.allPoints.get(j).timestamp;
						result.add(newpt);
					}
					results.add(result);

					System.out.println("Added a line from lasso");
				}
				System.out.println("num of lines: " + results.size());
				return results;
			} else {
				// engine = new Decision_Engine(line2, line2, (float)
				// Math.sqrt(Math
				// .pow(width, 2) + Math.pow(height, 2)));
			}
		} catch (Exception e) {
			System.out.println(getStackTrace(e));
		}
		return null;
	}

	public int PtCount() {
		return this.allPoints.size();
	}
	
	private String mongoPrint(boolean user){
		return mongo.linesString(user);
	}
	
	private void redrawLines(boolean u, boolean c){
		try{			
			BasicDBObject[] data = mongo.data();
			JSONDeserializer deserializer = new JSONDeserializer();
			Line line1 = (Line)deserializer.deserialize(data[1].toString());
			Line line2 = (Line)deserializer.deserialize(data[2].toString());
			if(u){
//				System.out.println(data[2].toString());
				if(line1.compGenerated){
					drawLine(line2);
				}else{
					drawLine(line1);
				}
//				buffer.addToBuffer(userLines);
			}
			if(c){
				if(!line1.compGenerated){
					drawLine(line2);
				}else{
					drawLine(line1);
				}
				//buffer.addToBuffer(compLines);
			}
			//buffer.update();
		}
		catch(Exception e){
			System.out.println("Error:" + e.getMessage());
		}
	}

	private ArrayList<Line> deserialize(String s){
		ArrayList<Line> lines = new ArrayList<Line>();
		try{			
			BasicDBObject[] data = mongo.data();
			JSONDeserializer deserializer = new JSONDeserializer();
			if(s.indexOf("allPoints", 5) > -1){//more than one line
				while(s.indexOf("allPoints")> -1){
					String d = "[{";
//					int end = (s.indexOf("allPoints",4) > -1) ? s.indexOf("allPoints",4) : 
					lines.add((Line)deserializer.deserialize(d));
				}
			}
		}
		catch(Exception e){
			System.out.println("Error:" + e.getMessage());
		}
		return lines;
	}
	private void drawLine(Line l){
		ArrayList<Point> points = l.allPoints;
		for(int ii = 0; ii < points.size()-1;){
			LineSegment ls = new LineSegment(new PVector(points.get(ii).x,points.get(ii).y),
					new PVector(points.get(++ii).x,points.get(ii).y));
			//buffer.addSegment(ls);
		}
			//buffer.update();
	}
	
	public void changeMongoUser(String id, String name){
		mongo = new MongoControl(id);
		mongo.createUser(id,name);
	}
	
}
