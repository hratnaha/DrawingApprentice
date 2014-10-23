package jcocosketch;

import processing.core.PVector;

/**
 * Point class that represents the PVector objects, but has a line ID attached to it
 * 
 *
 */
public class Point extends PVector implements Comparable{
	private float lineID;
	private double x;
    private double y;
    private Object opt_value;

    private float groupID;

    /**
     * Creates a new point object.
     *
     * @param {double} x The x-coordinate of the point.
     * @param {double} y The y-coordinate of the point.
     * @param {Object} opt_value Optional value associated with the point.     
     */
    public Point(double x, double y, float lineID) {
    	this.x = x;
        this.y = y;
        this.lineID = lineID;
    }
    public Point(double x, double y, Object opt_value) {
        this.x = x;
        this.y = y;
        this.opt_value = opt_value;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
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
}
