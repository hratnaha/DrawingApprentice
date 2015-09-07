package jcocosketch;

import processing.core.PVector;

/**
 * Point class that represents the PVector objects, but has a line ID attached to it
 * 
 *
 */
public class Point extends PVector implements Comparable{
	public float lineID;
	//private double x;
    //private double y;
    public Object opt_value;
    public float timestamp; 

    public float groupID;

    public Point(float x, float y){
    	super(x, y);
    	this.lineID = 0;
    }
    /**
     * Creates a new point object.
     *
     * @param {double} x The x-coordinate of the point.
     * @param {double} y The y-coordinate of the point.
     * @param {Object} opt_value Optional value associated with the point.     
     */
    public Point(float x, float y, float lineID) {
    	super(x, y);
        this.lineID = lineID;
    }
    public Point(float x, float y, Object opt_value) {
        super(x, y);
        this.opt_value = opt_value;
        this.lineID = 0;
    }
    
    public void setTime(long time){
    	timestamp = time; 
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Object getValue() {
        return opt_value;
    }

    public void setValue(Object opt_value) {
        this.opt_value = opt_value;
    }
    
    public void setLineID(float lineID){
    	this.lineID = lineID;
    }
    
    public void setGroupID(float groupID){
    	this.groupID = groupID;
    }
    
    public float getGroupID(){
		return this.groupID;
	}

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public int compareTo(Object o) {
        Point tmp = (Point) o;
        if (this.x < tmp.x) {
            return -1;
        } else if (this.x > tmp.x) {
            return 1;
        } else {
            if (this.y < tmp.y) {
                return -1;
            } else if (this.y > tmp.y) {
                return 1;
            }
            return 0;
        }

    }
    
    public static Point LerpPoint(Point ptA, Point ptB, float fractionA){
    	PVector lerpPV = PVector.lerp(ptA, ptB, fractionA);
    	return new Point(lerpPV.x, lerpPV.y, ptA.lineID); 
    }

}
