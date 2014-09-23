package jcocosketch.nodebridge;
import processing.core.*;

public class SketchPoint extends PVector{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long timestamp;
	public String id;
	public SketchPoint(int x, int y, long timestamp, String id){
		this.x = x;
		this.y = y;
		this.timestamp = timestamp;
		this.id = id;
	}
	public SketchPoint(){
		
	}
}
