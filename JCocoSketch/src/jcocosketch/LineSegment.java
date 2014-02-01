package jcocosketch;

import processing.core.*;
import java.util.*;

public class LineSegment{
  PVector start; 
  PVector end; 
  public LineSegment(PVector s, PVector e){
    start = s; 
    end = e; 
  }
  public void printPoints(){
    System.out.println("Start: " + start + " End: " + end); 
  }
  
  public void render(PGraphics buffer){
    buffer.line(start.x, start.y, end.x, end.y); 
  }
}
