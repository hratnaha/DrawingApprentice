package jni;
import java.awt.*;

public class BasicPrimitive{

    public double length;
    public double startAngle;
    public double startCurvature;
    public double curvatureDerivative;
    public int bptype;
    public double startX;
    public double startY;
    
    public void setLength(double l){
    	this.length = l;
    }
    public void setStAngle(double a){
    	this.startAngle = a;
    }
    public void setStCurvature(double c){
    	this.startCurvature = c;
    }
    public void setCurvatureDerivative(double c){
    	this.curvatureDerivative = c;
    }
    public void setType(int t){
    	this.bptype = t;
    }
    public void setStartX(double x){
    	this.startX = x;
    }
    public void setStartY(double y){
    	this.startY = y;
    }
}
