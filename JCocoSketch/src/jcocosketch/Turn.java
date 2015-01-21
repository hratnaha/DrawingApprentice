package jcocosketch;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Turn class represents a user's turn. Consists of a queue of the lines a user draws in a turn.
 *
 */
public class Turn {
	private float startTime;
	private float endTime;
	private ArrayList<Line> lines;
	
	public Turn(){
		startTime = -1;
		lines = new ArrayList<Line>();
		setStartTime(-1);
		setEndTime(-1);
	}
	
	public Turn(Line firstLine){
		lines.add(firstLine);
		setStartTime(firstLine.startTime);
	}
	
	public ArrayList<Line> getLines(){
		return lines;
	}
	
	public void addLine(Line newLine){
		lines.add(newLine);
	}
	
	public void startTurn(){
		startTime = System.currentTimeMillis() / 1000.0f;
	}
	
	public void endTurn(){
		endTime = System.currentTimeMillis() / 1000.0f;
	}

	public float getStartTime() {
		return startTime;
	}

	public void setStartTime(float startTime) {
		this.startTime = startTime;
	}

	public float getEndTime() {
		return endTime;
	}

	public void setEndTime(float endTime) {
		this.endTime = endTime;
	}
}
