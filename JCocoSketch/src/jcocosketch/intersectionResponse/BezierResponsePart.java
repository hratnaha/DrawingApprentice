package jcocosketch.intersectionResponse;

import java.util.*;
import processing.core.*;
import jcocosketch.*;

public class BezierResponsePart {
	private Line xLine[] = {null, null};
	private float xParam[] = {-1.0f, -1.0f};
	private float range[] = {0.0f, 1.0f};
	private PVector xpt[] = {null, null};
	private PVector cpt[] = {null, null};
	
	public BezierResponsePart(Line targetLine1, Line targetLine2) {
		Line newXline[] = {targetLine1, targetLine2};
		xLine = newXline;
	}
	
	public BezierResponsePart clone() {
		BezierResponsePart theClone = new BezierResponsePart(xLine[0], xLine[1]);
		float[] cloneXParam = {xParam[0], xParam[1]};
		PVector[] cloneXPt  = {xpt[0].get(), xpt[1].get()};
		PVector[] cloneCPt  = {cpt[0].get(), cpt[1].get()};
		theClone.xParam = cloneXParam;
		theClone.xpt = cloneXPt;
		theClone.cpt = cloneCPt;
		return theClone;
	}
	
	public void setXParam(int k, float param) {
		xParam[k] = param;
		xpt[k] = xLine[k].getPointAt(param);
	}
	
	public void setEndPoint(int k, PVector xPoint) {
		xParam[k] = -1.0f;
		xpt[k] = xPoint;
	}
	
	public void setControlPoint(int k, PVector controlPoint) {
		cpt[k] = controlPoint;
	}
	
	public PVector getControlPoint(int k) {
		return cpt[k];
	}
	
	public PVector getOwnTan(int k) {
		PVector dir = PVector.sub(cpt[k], getXPoint(k));
		if (1 == k) {dir.mult(-1.0f);}
		dir.normalize();
		return dir;
	}
	
	public Line getXLine(int k) {
		return xLine[k];
	}
	
	public float getXParam(int k) {
		return xParam[k];
	}
	
	public PVector getXPoint(int k) {
		return xpt[k];
	}
	
	public PVector getXTan(int k) {
		PVector tan = getXLine(k).getKDerivativeAt(getXParam(k), 1);
		tan.normalize();
		return tan;
	}
	
	public PVector getStraight(int k) {
		return PVector.sub(getXPoint(1 - k), getXPoint(k));
	}
	
	public void draw(PGraphics buffer) {
		PVector p1 = getXPoint(0);
		PVector p2 = getControlPoint(0);
		PVector p3 = getControlPoint(1);
		PVector p4 = getXPoint(1);
		buffer.bezier(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);
	}
	
	public void setRange(int k, float limit) {
		range[k] = limit;
	}
	
	public BezierResponsePart reversed() {
		BezierResponsePart theReversed = new BezierResponsePart(xLine[1], xLine[0]);
		float newXParam[] = {xParam[1], xParam[0]};
		float newRange[]  = {1.0f - range[1], 1.0f - range[0]};
		PVector newXpt[] = {xpt[1], xpt[0]};
		PVector newCpt[] = {cpt[1], cpt[0]};
		theReversed.xParam = newXParam;
		theReversed.xpt = newXpt;
		theReversed.cpt = newCpt;
		theReversed.range = newRange;
		return theReversed;
	}
	
	public Point atT(float t) {
		float tsq = t * t;
		float tcb = tsq * t;
		float f = 1 - t;
		float fsq = f * f;
		float fcb = fsq * f;
		PVector target = PVector.mult(xpt[0], fcb);
		target.add(PVector.mult(cpt[0], 3 * fsq * t));
		target.add(PVector.mult(cpt[1], 3 * f * tsq));
		target.add(PVector.mult(xpt[1], tcb));
		return new Point(target.x, target.y);
	}
	
	public void buildUpApproximation(int k, Line approximation) {
		if (0 == approximation.getTotalDistance()) {
			approximation.addPoint(atT(range[0]));
		}
		float delta = (range[1] - range[0]) / k;
		float t = range[0] + delta;
		for (int i = 0; i < k; i++, t += delta) {
			approximation.addPoint(atT(t));
		}
	}
	
	public float approximateLength(int k) {
		Line approximation = new Line();
		buildUpApproximation(k, approximation);
		return approximation.getTotalDistance();
	}
}
