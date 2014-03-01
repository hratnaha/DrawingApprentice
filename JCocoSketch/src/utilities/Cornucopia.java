package utilities;

import processing.core.*;

import java.util.*;

import jcocosketch.Line;

import utilities.PVecUtilities;

//javah -classpath bin -jni -d jni utilities.Cornucopia
// need to set native library path

public class Cornucopia {
	
	public Cornucopia() {
		
	}
	
	public Line getPrimitives(Line line, int parmtype) {
		float[] passin = new float[line.getSize() * 2];
		for(int i = 0; i < line.getSize(); i++) {
			passin[2*i] = line.getPoint(i).x;
			passin[2*i + 1] = line.getPoint(i).y;
		}
		//System.out.println(passin.length + " " + line.getSize());
		float[] gettin = getBasicPrimitives(passin, line.getSize(), parmtype);
		ArrayList<PVector> points = new ArrayList<PVector>();
		for(int i = 0; i < gettin.length / 2; i++) {
			points.add(new PVector(gettin[2*i], gettin[2*i+1]));
		}
		return new Line(points);
	}
	
    private native float[] getBasicPrimitives(float[] passin, int size, int parmtype);   

    static {
        System.load("/Users/AlanZhang/Documents/workspace/JCocoSketch/lib/libCornucopia.jnilib");
    }
}
