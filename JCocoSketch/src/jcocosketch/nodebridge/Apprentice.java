package jcocosketch.nodebridge;

import processing.core.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import jcocosketch.*;
import flexjson.*;

public class Apprentice {
	ArrayList<SketchPoint> allPoints = new ArrayList<SketchPoint>();
	ArrayList<Line> allLines = new ArrayList<Line>();
	ArrayList<Line> compLines = new ArrayList<Line>();
	Turn curTurn = null;
	boolean AgentOff = false;
	boolean HoldAlgo = false;
	long strokeStartTime;
	double systemStartTime = 0;
	TrajectorMode currentMode = TrajectorMode.Local; // 0 = local, 1 = regional,
														// 2 = global
	Boolean isGrouping = false;

	ArrayList<Group> allGroups = new ArrayList<Group>();
	ArrayList<Group> normalizedGroups = new ArrayList<Group>();
	QuadTree mainTree;
	Line line2;

	int width = 2100, height = 1080;

	public Apprentice() {
		mainTree = new QuadTree(0, 0, width, height);
		initializeNewTurn();
		try {
			DQNJS.isinit = false;
			DQNJS.init();
			DQNJS.setCreativity(0.5f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setCurrentTime(double now) {
		System.out.println("new apprentice start!! time: " + now);
		this.systemStartTime = now;
	}

	private void initializeNewTurn() {
		curTurn = new Turn();
		curTurn.startTurn();
	}

	public void setCanvasSize(int width, int height) {
		this.width = width;
		this.height = height;
		mainTree = new QuadTree(0, 0, width, height);
	}

	public void setCreativityLevel(int level) {
		float dlevel = (float) level / 100f;
		DQNJS.setCreativity(dlevel);
		System.out.println("Apprentice: set creativity level to " + dlevel);
	}

	public void setAgentOn(Boolean isOn) {
		this.AgentOff = !isOn;
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

	public void vote(int isUp) {
		if (isUp == 1) {
			DQNJS.learn(1.0f);
			System.out.println("vote up");
		} else {
			DQNJS.learn(-0.9f);
			System.out.println("vote down");
		}

	}

	public void clear() {
		System.out.println("clear");
	}

	public void startGrouping(long strokeTimeStamp) {
		this.strokeStartTime = strokeTimeStamp - (long) systemStartTime;
		isGrouping = true;
	}

	public void createStroke(long strokeTimeStamp) {
		isGrouping = false;
		this.strokeStartTime = strokeTimeStamp - (long) systemStartTime;
	}

	public void addPoint(int x, int y, double timestamp, String id) {
		// long timestampLong = Long.parseLong(timestamp);
		SketchPoint pt = new SketchPoint(x, y,
				(long) (timestamp - systemStartTime), id);
		//System.out.println("(" + pt.x + ", " + pt.y + ")");
		this.allPoints.add(pt);
	}

	public void endStroke(float r, float g, float b, float a, float thickness) {
		System.out.println("add new line");

		try {
			Line curline = createLine();
			curline.startTime = this.strokeStartTime;
			// System.out.println("set colors");
			curline.setColor(r, g, b, a);
			// System.out.println("set thickness");
			curline.setThickness(thickness);
			// System.out.println("add all lines");
			allLines.add(curline);
			// System.out.println("add cur lines");
			curTurn.addLine(curline);
			this.allPoints = new ArrayList<SketchPoint>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ArrayList<SketchPoint>> getDecision() {
		try {
			if (!isGrouping) {
				curTurn.endTurn();
				ArrayList<Line> turnLines = curTurn.getLines();
				ArrayList<Line> comLines = new ArrayList<Line>();

				System.out.println("turnlines: " + turnLines.size());
				if (!AgentOff) {
					System.out.println("agents is on!!");

					if (turnLines.size() > 0) {
						int preAlgo = -1;
						for (int i = 0; i < turnLines.size(); i++) {
							Line curLine = turnLines.get(i);

							int offset = 3;
							if (allLines.size() > offset)
								line2 = allLines.get(allLines.size() - offset);
							else
								line2 = curLine; // /can add other shapes to
													// mutate with
							System.out.println("used line length: " + line2.getAllPoints().size());
							Decision_Engine engine = new Decision_Engine(
									curLine, line2, (float) Math.sqrt(Math.pow(
											500, 2) + Math.pow(400, 2)));

							Line newline = engine.decision(preAlgo);
							// get the no. of decision and store it
							preAlgo = engine.curDecisionID;
							// Todo: color and thickness should be included in
							// the engine
							newline.setColor(curLine.colorR, curLine.colorG,
									curLine.colorB, curLine.colorA);
							newline.setThickness(curLine.getThickness());

							this.compLines.add(newline);
							comLines.add(newline);
						}

					}

					ArrayList<ArrayList<SketchPoint>> results = new ArrayList<ArrayList<SketchPoint>>();

					for (Line newline : comLines) {
						ArrayList<SketchPoint> result = new ArrayList<SketchPoint>();
						ArrayList<Point> pts = newline.getAllPoints();
						newline.startTime = System.currentTimeMillis()
								- (long) this.systemStartTime;
						System.out.println("computer lines generated: "
								+ newline.startTime);
						// for(PVector pt : pts){
						for (int i = 0; i < pts.size(); i++) {
							SketchPoint newpt = new SketchPoint();
							newpt.x = pts.get(i).x;
							newpt.y = pts.get(i).y;
							newpt.timestamp = System.currentTimeMillis()
									- (long) this.systemStartTime;// this.allPoints.get(j).timestamp;
							result.add(newpt);
						}
						results.add(result);
					}

					initializeNewTurn();
					return results;
				}
			}
		} catch (Exception e) {
			System.out.println(getStackTrace(e));
		}
		System.out.println("return nothing");
		initializeNewTurn();
		this.allPoints = new ArrayList<SketchPoint>();
		return null;
	}

	private Line createLine() {
		Line curline = new Line();
		try{
			for (SketchPoint pt : this.allPoints) {
				Point newpt = new Point(pt.x, pt.y, curline.getLineID());
				newpt.setTime(pt.timestamp);
				curline.addPoint(newpt);
				if (curline.getGroupID() != 1) {
					newpt.setGroupID(curline.getGroupID());
				}
				mainTree.set(pt.x, pt.y, newpt);
			}
		}catch(Exception e){
			System.out.println(e.getStackTrace());
		}
		return curline;
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
				//just return the user lines (ll below), then return that to the HTML 
				int preDecision = -1;
				for (int i = 0; i < this.allGroups.get(size_main).getSize(); ++i) {
					Line l = this.allGroups.get(size_main).lines.get(i);
					//need to make a new packetpoint including all the lines from the group
					
					
					//stuff below is comp generating lines for each line in the group
					/*Decision_Engine engine = new Decision_Engine(l1, line2,
							(float) Math.sqrt(Math.pow(width, 2)
									+ Math.pow(height, 2)));

					Line l = engine.decision(preDecision);
					preDecision = engine.curDecisionID;
					l.compGenerated = true;
*/
					// System.out.println("line generated");

					ArrayList<SketchPoint> result = new ArrayList<>();
					ArrayList<Point> pts = l.getAllPoints();
					System.out.println(pts.size());
					// for(PVector pt : pts){
					for (int j = 0; j < pts.size(); j++) {
						SketchPoint newpt = new SketchPoint();
						newpt.x = pts.get(j).x;
						newpt.y = pts.get(j).y;
						// newpt.id = this.allPoints.get(j).id;
						newpt.timestamp = System.nanoTime()
								- (long) systemStartTime;// this.allPoints.get(j).timestamp;
						System.out.println(newpt.timestamp);
						result.add(newpt);
					}
					results.add(result);

					System.out.println("Returned lines in the lasso");
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

	public String getUserLines() {
		JSONSerializer serializer = new JSONSerializer();

		String userLines = serializer.deepSerialize(this.allLines);
		// System.out.println(userLines);
		return userLines;
	}

	public String getComputerLines() {
		JSONSerializer serializer = new JSONSerializer();
		String computerLines = serializer.deepSerialize(this.compLines);
		return computerLines;
	}
}
