import processing.core.*; 
import processing.xml.*; 

import gab.opencv.*; 
import papaya.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class DrawBackFork_Flow_Lines extends PApplet {

class BezierFit {
  //ArrayList<Point> points;
  float[][] M = new float[][]{{-1,3,-3,1},{3,-6,3,0},{-3,3,0,0},{1,0,0,0}}; 
  public BezierFit() {
  }
  /**
   * Computes the best bezier fit of the supplied points using a simple RSS minimization.
   * Returns a list of 4 points, the control points
   * @param points
   * @return
   */
  public ArrayList<PVector> fit(ArrayList<PVector> points){
    //Matrix M = M();
    float[][] Minv = new float[4][4];
    Minv = Mat.inverse(M);
    float[][] U = U(points);
    float[][] UT = UT(points);
    float[][] X = X(points);
    float[][] Y = Y(points);
    
    float[][] A = Mat.multiply(UT, U);
    float[][] B = Mat.inverse(A);
    float[][] C = Mat.multiply(Minv, B);
    float[][] D = Mat.multiply(C, UT);
    float[][] E = Mat.multiply(D, X);
    float[][] F = Mat.multiply(D, Y);
    
    ArrayList<PVector> P = new ArrayList<PVector>();
    for(int i = 0; i < 4; i++){
      float x = E[i][0];
      float y = F[i][0];
      
      PVector p = new PVector(x, y);
      P.add(p);
    }
    
    return P;
  }
  
  private float[][] Y(ArrayList<PVector> points){
    float[][] Y = new float[points.size()][1];
    
    for(int i = 0; i < points.size(); i++)
      Y[i][0] = points.get(i).y;
    
    return Y;
  }
  
  private float[][] X(ArrayList<PVector> points){
    float[][] X = new float[points.size()][1];
    
    for(int i = 0; i < points.size(); i++)
      X[i][0] = points.get(i).x;
    
    return X;
  }
  
  private float[][] U(ArrayList<PVector> points){
    float[] npls = normalizedPathLengths(points);
    
    float[][] U = new float[npls.length][4];
    for(int i = 0; i < npls.length; i++){
      U[i][0] = pow(npls[i], 3);
      U[i][1] = pow(npls[i], 2);
      U[i][2] = pow(npls[i], 1);
      U[i][3] = pow(npls[i], 0);
    }

    return U;
  }
  
  private float[][] UT(ArrayList<PVector> points){
    float[] npls = normalizedPathLengths(points);
    
    float[][] UT = new float[4][npls.length];
    for(int i = 0; i < npls.length; i++){
      UT[0][i] = pow(npls[i], 3);
      UT[1][i] = pow(npls[i], 2);
      UT[2][i] = pow(npls[i], 1);
      UT[3][i] = pow(npls[i], 0);
    }

    return UT;
  }
  /**
   * Computes b(t).
   * @param t
   * @param v1
   * @param v2
   * @param v3
   * @param v4
   * @return
   */
  private PVector pointOnCurve(float t, PVector v1, PVector v2, PVector v3, PVector v4){
    PVector p;

    float x1 = v1.x;
    float x2 = v2.x;
    float x3 = v3.x;
    float x4 = v4.x;

    float y1 = v1.y;
    float y2 = v2.y;
    float y3 = v3.y;
    float y4 = v4.y;

    float xt, yt;

    xt = x1 * pow((1-t),3) 
        + 3 * x2 * t * pow((1-t), 2)
        + 3 * x3 * pow(t,2) * (1-t)
        + x4 * pow(t,3);

    yt = y1 * pow((1-t),3) 
        + 3 * y2 * t * pow((1-t), 2)
        + 3 * y3 * pow(t,2) * (1-t)
        + y4 * pow(t,3);

    p = new PVector(xt, yt);

    return p;
  }

  /** Computes the percentage of path length at each point. Can directly be used as t-indices into the bezier curve. */
  private float[] normalizedPathLengths(ArrayList<PVector> points){
    float pathLength[] = new float[points.size()];

    pathLength[0] = 0;

    for(int i = 1; i < points.size(); i++){
      PVector p1 = points.get(i);
      PVector p2 = points.get(i-1);
      float distance = sqrt(pow(p1.x - p2.x,2) + pow(p1.y - p2.y,2));
      pathLength[i] += pathLength[i-1] + distance;
    }

    float [] zpl = new float[pathLength.length];
    for(int i = 0; i < zpl.length; i++)
      zpl[i] = pathLength[i] / pathLength[pathLength.length-1];

    return zpl;
  }

}


class Button {
  int x; 
  int y; 
  int width; 
  int height; 
  String mode; 
  boolean mouseOver = false; 
  boolean active = false; 
  
  public Button(int x, int y, int width, int height)
  {
    this.x = x; 
    this.y = y; 
    this.width = width; 
    this.height = height; 
    render(); 
  }
  
  public void mousePressed()
  {
    //listens for if buttons are pressed
     if (mouseX > x && mouseX < x + width)
      if (mouseY > y && mouseY < y + height)
      {
        if (active = true)
          active = false; 
        else 
        {
          active = true; 
          
        }
      }
  }
  
  public void mouseMoved()
  {
    //check if the mouse if over the boundary of the button
    //if so change the color of the button 
    //set the condition of the mouseOver value to true
    //render
    if (mouseX > x && mouseX < x + width)
      if (mouseY > y && mouseY < y + height)
      {
        active = true; 
        render(); 
      }
  }
  
  public void render()
  {
    if (mouseOver || active)
      fill(100, 0, 0); 
    else 
      fill(0,100,0); 
    rect(x, y, width, height); 
  }
}

class Decision_Engine {
 Line line;
 //int rotate = 0;
 int translate = 1;
 int reflection = 2;
 int scaling = 3;
 int drawback = 4;
 //int decide;

 public Decision_Engine(Line line){ 
   //constructor yeeeeaaaaahhhhhhh is this even needed
   this.line = line;
   //I evidently forgot how to program with objects. I feel bad.
 }
  
  public void decision(){
    int decision = (int)random(1,5);
    println(decision);
    if(decision == translate){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.translation();
      newLine.draw();
    } else if(decision == reflection){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.reflection();
      newLine.draw();
    } else if(decision == scaling){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.scaling();
      newLine.draw();
    } else if(decision == drawback){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.drawBack(this.line);
      newLine.draw();
    }
  }
  
  public void setLine(Line line){
    this.line = line;
  }
}



OpenCV opencv;
ArrayList <Line> allLines = new ArrayList<Line>(); 
Line curLine; 
ArrayList <Line> stack = new ArrayList<Line>(); //keep track of the drawBack stack
float probRandom; //the amount of time that the drawBack will interject randomness
float degreeRandom; //how much fluctuation is introduced in random interjections
int i; //iteration count for stack
boolean drawBack = false; 
boolean drawBezier = false; 
ArrayList gPts;
int gMvCnt = 0;
LineGroup curLineGroup = new LineGroup();
ArrayList<LineGroup> lineGroups = new ArrayList<LineGroup>();

particle[] Z = new particle[200];
float colour = random(1);

public void setup() 
{
  size(600, 600);
  background(255);
  noFill(); 
  strokeWeight(1); 
  smooth(); 
  i = 0;// initialize the count for the 
  frameRate(60); 
  probRandom = .15f; 
  degreeRandom = 5;
  colorMode(HSB, 100, 100, 100);
  gPts = new ArrayList();
  //drawBezier = true;
  //PImage src = loadImage("PUI001.tif");
  //opencv = new OpenCV(this, src);
  lineGroups.add(curLineGroup);
  colorMode(RGB,255);
}

public void draw() 
{
  //could have a stack of lines that need to be processed
  checkStack();
  /*
  image(opencv.getOutput(), 0, 0);
  strokeWeight(3);
  for (Line line : lines) {
    stroke(0, 255, 0);
    line.drawLine();
  }
  */
  if(keyPressed == true && key == 's') {
    gravitateSwarm();
  }
}

public void redraw()
{
  background(255);
  println("allLines.size() = " + allLines.size());
  strokeWeight(1); 
  for(int i = 0; i < allLines.size(); i++)
  {
    allLines.get(i).draw(); 
  }
}

//##### Event Handling
public void mousePressed() 
{
  println(mouseX + " " + mouseY);
  //line(pmouseX, pmouseY, mouseX, mouseY); 
  curLine = new Line(mouseX, mouseY); 
  allLines.add(curLine);
  gPts = new ArrayList();
  gPts.add(new PVector(mouseX,mouseY));
  gMvCnt = 0;
}
public void mouseDragged() 
{
  println(mouseX + " " + mouseY);
  line(pmouseX, pmouseY, mouseX, mouseY); 
  //check if the slope has not change by 90 degrees
  //if so set line end to previous point and begin new line with current point add previous line to stack
  curLine.curEnd(mouseX, mouseY);
  if ( gMvCnt++ % 5 == 0 )
    gPts.add(new PVector(mouseX,mouseY));
}
public void mouseReleased() 
{
  //Actually the endPoint will be the same with its previous one.
  //line(pmouseX, pmouseY, mouseX, mouseY); 
  curLine.setEnd(mouseX, mouseY); 
  //curLine.printPoints();
  if(drawBezier)
  {
    drawBezier();
  }
  boolean in = false;
  if(curLineGroup.getSize() == 0 && lineGroups.size() == 1){
    curLineGroup.addLine(curLine);
    curLineGroup.setLineGroupID(0);
    
    //TODO: make every particle only attracted by one point at one time.
    for(int i = 0; i < Z.length; i++) {
      float radius = random(100);
      float angle = random(6.28f);
      Z[i] = new particle(curLineGroup.centerLine.getPoint(0).x + radius * sin(angle), curLineGroup.centerLine.getPoint(0).y + radius * cos(angle), 0, 0, 1);
      //Z[i] = new particle( random(width), random(height), 0, 0, 1 );
      //println("Particle " + Z[i].x + " " + Z[i].y);
    }
  }
  else {
    for(int i = 0; i < lineGroups.size(); i++) {
      curLineGroup = lineGroups.get(i);
      //println("c" + i);
      if(curLineGroup.inGroup(curLine)) {
        curLineGroup.addLine(curLine);
        in = true;
        break;
      }
    }
    if(in == false) {
      println("new group");
      curLineGroup = new LineGroup();
      lineGroups.add(curLineGroup);
      curLineGroup.addLine(curLine);
      curLineGroup.setLineGroupID(lineGroups.size() - 1);
      //
      for(int i = 0; i < Z.length; i++) {
        float radius = random(30);
        float angle = random(6.28f);
        Z[i] = new particle(curLineGroup.centerLine.getPoint(0).x + radius * sin(angle), curLineGroup.centerLine.getPoint(0).y + radius * cos(angle), 0, 0, 1);
        //Z[i] = new particle( random(width), random(height), 0, 0, 1 );
        //println("Particle " + Z[i].x + " " + Z[i].y);
      }
    }
  } 
  curLineGroup.printLineGroupID();
}

public void clear(){
    allLines = new ArrayList<Line>(); 
    //curLineGroup = new LineGroup();
    lineGroups = new ArrayList<LineGroup>();
    background(255);
}

public void keyPressed()
{
  if (key == 'l')
  {
    lineDetection();
  }
  if (key == 'd')
  {
    redraw();
    for(i = 0; i < lineGroups.size(); i++)
    {
      lineGroups.get(i).drawCenterLine();
    }
  }
  if(key == 'c'){
    clear();
  }
  if(key == 'r') {
    redraw();
  }
}
public void gravitateSwarm()
{
  //filter(INVERT);
 
  float r;
  redraw();
  stroke(0);
  rect(0,0,width,height);
  colorMode(HSB,1);
  for(int i = 0; i < Z.length; i++) {
    for(int j = 0; j < curLineGroup.getSize(); j++) {
      for(int k = 0; k < curLineGroup.getLine(j).getSize(); k++) {
      //for(int k = 0; k < 1; k++) {
        //println(i + " " + j + " " + k);
        Z[i].gravitate(new particle(curLineGroup.getLine(j).getPoint(k).x, curLineGroup.getLine(j).getPoint(k).y, 0, 0, 0.001f ) );
        //else {
          //Z[i].deteriorate();
        //}
        //if(sqrt(sq(Z[i].x - curLineGroup.centerLine.getPoint(curLineGroup.centerLine.getSize() - 1).x) + sq(Z[i].y - curLineGroup.centerLine.getPoint(curLineGroup.centerLine.getSize() - 1).y)) < 100)
          //Z[i].deteriorate();
        //else {
          Z[i].update();
          r = PApplet.parseFloat(i)/Z.length;
          stroke(colour, pow(r,0.1f), 1-r, 0.15f );
          Z[i].display();
        //}
      }
    }
  }
  colorMode(RGB,255);
  colour+=random(0.01f);
  if( colour > 1 ) {
    colour = colour%1;
  }
  stroke(0, 0, 0);
  //filter(INVERT);
}
public void generateFlowLines()
{
  //cycle through all lines to determine their flow lines
  for(int i = 0; i < allLines.size(); i++)
  {
    Line curLine = allLines.get(i); 
    curLine.generateFlowLines(); 
  }
}

public void translation(){
  for(int i = 0; i < allLines.size(); i++){
    Line curLine = allLines.get(i);
   // Line_Mod mod = new Line_Mod(curLine);
    Line newLine;
   // newLine = mod.translation();
    //newLine.drawLine();
  }
}

public void changeMode (String mode)
{
  //change the mode of the sketch 
  //this information is coming from the button
}

//######## Random DrawBack Mode
public void checkStack()
{
  if (stack.size() >= 1)
  {
    //println("i: " + i + "Size of allPoints: " + stack.get(0).allPoints.size()); 
    if (i < stack.get(0).allPoints.size())
    {
      //println("Size: " + stack.size() + "i: " + i); 
      //start the i at 0
      //look at the 
      float x1 = stack.get(0).allPoints.get(i).x;
      float y1 = stack.get(0).allPoints.get(i).y;

      float x2 = stack.get(0).allPoints.get(i + 1).x;
      float y2 = stack.get(0).allPoints.get(i + 1).y;

      //println("x1: " + x1 + "y1: " + y1 + "x2: " + x2 + "y2: " + y2); 
      line (x1, y1, x2, y2); 
      i++; 

      if (i == stack.get(0).allPoints.size() - 1)
      {
        println("Completed line response"); 
        stack.remove(0); 
        i = 0; //reset the counter
      }
    }
  }
}

public void drawBezier()
{
  int sz = gPts.size();
  if ( sz == 0)
    return;
  beginShape();
  stroke(0, 250, 150);
  float x1 = ((PVector)gPts.get(0)).x;
  float y1 = ((PVector)gPts.get(0)).y;
  float xc = 0.0f;
  float yc = 0.0f;
  float x2 = 0.0f;
  float y2 = 0.0f;
  vertex(x1,y1);
  for ( int i = 1; i< sz - 2; ++i)
  {
    xc = ((PVector)gPts.get(i)).x;
    yc = ((PVector)gPts.get(i)).y;
    x2 = (xc + ((PVector)gPts.get(i+1)).x)*0.5f;
    y2 = (yc + ((PVector)gPts.get(i+1)).y)*0.5f;
    bezierVertex((x1 + 2.0f*xc)/3.0f,(y1 + 2.0f*yc)/3.0f,
              (2.0f*xc + x2)/3.0f,(2.0f*yc + y2)/3.0f,x2,y2);
    x1 = x2;
    y1 = y2;
  }
  xc = ((PVector)gPts.get(sz-2)).x;
  yc = ((PVector)gPts.get(sz-2)).y;
  x2 = ((PVector)gPts.get(sz-1)).x;
  y2 = ((PVector)gPts.get(sz-1)).y;
  bezierVertex((x1 + 2.0f*xc)/3.0f,(y1 + 2.0f*yc)/3.0f,
         (2.0f*xc + x2)/3.0f,(2.0f*yc + y2)/3.0f,x2,y2);
  endShape();
  stroke(0, 0, 0);
}

public void lineDetection(){
  save("db.jpg");
  PImage src = loadImage("db.jpg");
  opencv = new OpenCV(this, src);
  opencv.findCannyEdges(20, 75);

  // Find lines with Hough line detection
  // Arguments are: threshold, minLengthLength, maxLineGap
  
  //lines = opencv.findLines(100, 30, 20);
}

class Line {
  /*Old using points
  Point startPoint; 
  Point curEnd; 
  Point endPoint; 
  ArrayList<Point> allPoints = new ArrayList<Point>(); 
  Point myPoint; 
*/
  //new using PVector
  PVector myPoint;
  PVector startPoint; 
  PVector curEnd; 
  PVector endPoint; 
  ArrayList<PVector> allPoints = new ArrayList<PVector>(); 

  float startTime; 
  float endTime; 
  boolean isSelected = false; 
  float lineID; 
  Rectangle myBoundingBox;
  float xmin = -1; 
  float ymin = -1; 
  float xmax = -1; 
  float ymax = -1; 

  //convert all reference of ponts to pvec

  public Line()
  {
    startTime = millis();
  }

  public Line(float x, float y)
  {
    //myPoint = new Point(x, y);
    myPoint = new PVector(x, y); 
    startPoint = myPoint;
    allPoints.add(startPoint);
    startTime = millis();
  }
  
  public Line(ArrayList<PVector> all)
  {
    //myPoint = new Point(x, y);
    startPoint = all.get(0);
    endPoint = all.get(all.size() - 1);
    allPoints = all;
  }
  
  public Line(PVector[] all)
  {
    //myPoint = new Point(x, y);
    startPoint = all[0];
    endPoint = all[all.length - 1];
    for(int i = 0; i < all.length; i++)
      allPoints.add(all[i]);
  }

  public void draw() 
  {
    //println("In drawLine()"); 
    //println("Allpoints.size()" + allPoints.size()); 
    strokeWeight(1); 
    for (int i = 0; i < allPoints.size(); i++) 
    {
      if (i < allPoints.size() - 1) 
      {
        //println("Less than allPoints.size()"); 
        PVector p1 = allPoints.get(i); 
        PVector p2 = allPoints.get(i+1); 
        line(p1.x, p1.y, p2.x, p2.y);
      } 
      /*else if (i == allPoints.size() - 1) 
      {
        println("allPoints.size()=" + allPoints.size()); 
        PVector p1 = allPoints.get(i -1); 
        PVector p2 = allPoints.get(i); 
        line(p1.x, p1.y, p2.x, p2.y);
      }
      */
    }
  }

  public void generateFlowLines()
  {
    float lineSpacing = 4; //distance between flow lines
    int numberOfLines = 10; //number of flow lines in each direction
    int increment = 100/numberOfLines; //for controlling color gradient HSB
    int curHue = 0; //tracking current hue
    for (int i = 0; i < numberOfLines; i++)
    {
      Line newLineAbove = new Line(); //create new Line objects for each of the flow lines
      Line newLineBelow = new Line(); 
      colorMode(HSB);
      stroke(curHue, 100, 50); 
      curHue += increment; 
      //flow lines below the line
      for (int j = 0; j< allPoints.size(); j++)
      {//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2)
        {//special case for start and end of line
          //generate the normal point and add to the newLine
          //println("Current i = " + i); 
          PVector p0 = allPoints.get(j); 
          PVector p1 = allPoints.get(j +1);
          PVector v = new PVector((p1.x - p0.x), (p1.y - p0.y)); //vector between two points in question
          PVector normal = new PVector(-v.y, v.x); //normal vector to the drawn line (might need to change signs based on slope)
          normal.setMag(i*lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          newLineBelow.addPoint(normalPoint);
        }
      }
      //flow lines abolve the line
      for (int j = 0; j< allPoints.size(); j++)
      {//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2)
        {//special case for start and end of line
          //generate the normal point and add to the newLine
          //println("Current i = " + i); 
          PVector p0 = allPoints.get(j); 
          PVector p1 = allPoints.get(j +1);
          PVector v = new PVector((p1.x - p0.x), (p1.y - p0.y)); //vector between two points in question
          PVector normal = new PVector(v.y, -v.x); //normal vector to the drawn line (might need to change signs based on slope)
          normal.setMag(i*lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          newLineAbove.addPoint(normalPoint);
        }
      }
      newLineAbove.draw();
      newLineBelow.draw(); 
      colorMode(RGB); 
      stroke(0);
    }
  }
  public void makeBoundingBox() {
    //creat the bounding box after the end of the line
    for (int i = 0; i < allPoints.size(); i++) {
      PVector p1 = allPoints.get(i); 
      if ( xmin == -1 && ymin== -1 && xmax == -1 && ymax == -1) {
        xmin = p1.x; 
        xmax = p1.x; 
        ymin = p1.y; 
        ymax = p1.y;
      }
      else 
      {
        if (p1.x < xmin) {
          xmin = p1.x;
        }
        else if (p1.x > xmax) {
          xmax = p1.x;
        }
        if ( p1.y < ymin) {
          ymin = p1.y;
        }
        else if ( p1.y > ymax) {
          ymax = p1.y;
        }
      }
    }
    PVector origin = new PVector(xmin, ymin); 
    float recWidth = xmax - xmin; 
    float recHeight = ymax - ymin; 
    myBoundingBox = new Rectangle((int)origin.x, (int)origin.y, (int)recWidth, (int)recHeight);
  }
  public void addPoint(PVector p)
  {//manually add a point to allPoints
    allPoints.add(p);
  }

  public void curEnd(float x, float y) 
  {
    myPoint = new PVector(x, y);
    if (startPoint == null)
    {
      startPoint = myPoint;
    }
    //myPoint.printPoint(); 
    allPoints.add(myPoint);
  }

  public void setEnd(float x, float y) 
  {
    //Actually the endPoint will be the same with its previous one.
    myPoint = new PVector(x, y);
    endPoint = myPoint; 
    //allPoints.add(endPoint);
    endTime = millis();
    makeBoundingBox();
  }

  public void printPoints() 
  {
    //println("The function is not implemented, please code me~!"); 
    for (int i = 0; i < allPoints.size(); i++) {
      println(allPoints.get(i).x + " " + allPoints.get(i).y); 
      //curPoint.printPoint();
    }
  }

  public float getTotalDistance() 
  {
    float totalDistance = 0; 
    //println("In getTotalDistance()"); 
    for (int i = 0; i < allPoints.size(); i++) {
      if (i < allPoints.size() - 1) {
        PVector p1 = allPoints.get(i); 
        PVector p2 = allPoints.get(i+1); 
        float currentDistance = sqrt((sq(p2.x - p1.x)) + sq((p2.y - p1.y))); 
        totalDistance += currentDistance;
      } 
      else if (i == allPoints.size() - 1) {
        PVector p1 = allPoints.get(i -1); 
        PVector p2 = allPoints.get(i); 
        float currentDistance = sqrt((sq(p2.x - p1.x)) + sq((p2.y - p1.y))); 
        totalDistance += currentDistance;
      }
    }
    return totalDistance;
  }

  public float getTotalTime() 
  {
    //println("In getTotalTime()" ); 
    float totalTime = endTime - startTime; 
    return totalTime;
  }

  public float getAverageVelocity() 
  {
    //println("In averageVelocity"); 
    float averageVelocity = getTotalDistance()/getTotalTime(); 
    //in pixels/millis
    return averageVelocity;
  }
  public Rectangle getBoundingBox() {
    return myBoundingBox;
  }
  public void drawBoundingBox() {
    myBoundingBox.drawRect();
  }
  public ArrayList<PVector> getAllPoints() {
    return allPoints;
  }
  public PVector getPoint(int i) {
    return allPoints.get(i);
  }
  public int getSize() {
    return allPoints.size();
  }
  public float getMaxY() {
    return ymax;
  }
  public float getMaxX() {
    return xmax;
  }
  public int getRectHeight() {
    return myBoundingBox.getHeight();
  }
  public void setLineID(float lineID) {
    this.lineID = lineID;
  }
  public float getLineID() {
    return lineID;
  }
  public void printLineID() {
    println(lineID);
  }
}


class LineGroup {
  Line centerLine;
  ArrayList<PVector> groupPoints = new ArrayList<PVector>();
  ArrayList<Line> groupLines = new ArrayList<Line>(); 
  PVector myPoint; 
  float startTime; 
  float endTime; 
  boolean isSelected = false; 
  float maxRange = 10; //arbitaryily set range to judge whether a point is in group
  int lineGroupID;
  BezierFit bezierFit;
  
  public LineGroup() {
     //startTime = millis();
     bezierFit = new BezierFit();
  }
  
  public LineGroup(Line line){
    groupLines.add(line);
    computeCenterLine();
    //drawCenterLine();
    //startTime = millis();
  }

  public void addLine(Line line)
  {//manually add a point to groupPoints
    groupLines.add(line);
    computeCenterLine();
    //drawCenterLine();
  }

  public boolean inGroup(Line curLine) 
  {
    boolean in = false;
    float num_centerLine_overlap = 0;
    float num_curLine_overlap = 0;
    int[] centerLine_overlap = new int[centerLine.allPoints.size()];
    int[] curLine_overlap = new int[curLine.allPoints.size()];
    float[] curLine_distance = new float[curLine.allPoints.size()];
    float centerLine_distance = 100000;
    boolean at_tail = false;
    boolean at_head = false;
    float curAvg = 0;
    float curStd = -1; // serve as an indicator
    float centerAvg = 0;
    float centerStd = 0;
    int curLine_overlap_start = -1;
    int curLine_overlap_end = -1;
    int centerLine_overlap_start = -1;
    int centerLine_overlap_end = -1;
    
    for(int j = 0; j < curLine.allPoints.size(); j++)
    {
      curLine_overlap[j] = -1;
      curLine_distance[j] = 100000;
    }
      
    for(int i = 0; i < centerLine.allPoints.size(); i++)
    {
      centerLine_distance = 100000;
      centerLine_overlap[i] = -1;
      for(int j = 0; j < curLine.allPoints.size(); j++)
      {
        float range = sqrt(sq(centerLine.getPoint(i).x - curLine.getPoint(j).x) + sq(centerLine.getPoint(i).y - curLine.getPoint(j).y)); 
        if(range < 100)
        {
          if(centerLine_distance > range){
            centerLine_distance = range;
            centerLine_overlap[i] = j;
          }
          if(curLine_distance[j] > range){
            curLine_distance[j] = range;
            curLine_overlap[j] = i;
          }
        }
      }
    }
    for(int i = 0; i < centerLine.allPoints.size(); i++)
    {
      //println("Center  " + centerLine_overlap[i]);
      if(centerLine_overlap[i] >= 0)
      {
        num_centerLine_overlap ++;
        if(centerLine_overlap_start == -1)
          centerLine_overlap_start = i;
        centerLine_overlap_end = i;
      }
    }
    for(int i = 0; i < curLine.allPoints.size(); i++)
    {
      //println("Cur  " + curLine_overlap[i]);
      if(curLine_overlap[i] >= 0)
      {
        num_curLine_overlap ++;
        if(curLine_overlap_start == -1)
          curLine_overlap_start = i;
        curLine_overlap_end = i;
      }
    }
    //println(num_centerLine_overlap + " " + centerLine.groupPoints.size());
    //println(num_curLine_overlap + " " + curLine.groupPoints.size());
    
    //Overlapping over 80% of either line is considered in one group
    if(num_centerLine_overlap / PApplet.parseFloat(centerLine.allPoints.size()) > 0.8f) { 
      in = true;
      println("before");
      curLine.printPoints();
      PVector[] tmp = new PVector[curLine.getSize()];
      curLine.getAllPoints().toArray(tmp);
      groupPoints = curLine.getAllPoints();
      if(centerLine_overlap[centerLine_overlap_start] > centerLine_overlap[centerLine_overlap_end]) {
        for(int i = centerLine_overlap_start; i <= centerLine_overlap_end; i ++)
          groupPoints.add(centerLine_overlap[i], centerLine.getPoint(i));
      }
      else {
        for(int i = centerLine_overlap_start; i <= centerLine_overlap_end; i ++)
          groupPoints.add(centerLine_overlap[i] + i - centerLine_overlap_start, centerLine.getPoint(i));
      } 
      curLine.allPoints = new ArrayList<PVector>();
      for(int i = 0; i < tmp.length; i++)
        curLine.allPoints.add(tmp[i]);
      println("after");
      curLine.printPoints();
    }
    else if(num_curLine_overlap / PApplet.parseFloat(curLine.allPoints.size()) > 0.8f) {
      in = true;
      groupPoints = centerLine.getAllPoints();
      if(curLine_overlap[curLine_overlap_start] > curLine_overlap[curLine_overlap_end]) {
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i], curLine.getPoint(i));
      }
      else {
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i] + i - curLine_overlap_start, curLine.getPoint(i));
      }  
    }
    //If ends of two lines parallel, which will result in 4 cases. Means and standard deviations computed from line segments are compared.
    else if(centerLine_overlap[centerLine.allPoints.size() - 1] >= 0 && curLine_overlap[0] >= 0)
    {
      ArrayList<Float> tmp = new ArrayList<Float>();
      for(int i = centerLine.allPoints.size() - 1; centerLine_overlap[i] >= 0; i--)
        tmp.add(new PVector(centerLine.getPoint(i).y - centerLine.getPoint(i - 1).y, centerLine.getPoint(i).x - centerLine.getPoint(i - 1).x).heading());
      centerAvg = average(tmp);
      centerStd = std(tmp);
      tmp = new ArrayList<Float>();
      for(int i = 0; curLine_overlap[i] >= 0; i++)
        tmp.add(new PVector(curLine.getPoint(i + 1).y - curLine.getPoint(i).y, curLine.getPoint(i + 1).x - curLine.getPoint(i).x).heading());
      curAvg = average(tmp);
      curStd = std(tmp);
      println(centerAvg + " " + centerStd);
      println(curAvg + " " + curStd);
      if(curStd > 0 && abs(centerAvg - curAvg) < 1 && abs(centerStd - curStd) < 1)
      {
        in = true;
        groupPoints = centerLine.getAllPoints();
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i] + i - curLine_overlap_start, curLine.getPoint(i));
        for(int i = curLine_overlap_end + 1; i < curLine.getSize(); i ++)
          groupPoints.add(groupPoints.size(), curLine.getPoint(i));
      }
    }
    else if(centerLine_overlap[0] >= 0 && curLine_overlap[curLine.allPoints.size() - 1] >= 0)
    {
      ArrayList<Float> tmp = new ArrayList<Float>();
      for(int i = curLine.allPoints.size() - 1; curLine_overlap[i] >= 0; i--)
        tmp.add(new PVector(curLine.getPoint(i).y - curLine.getPoint(i - 1).y, curLine.getPoint(i).x - curLine.getPoint(i - 1).x).heading());
      curAvg = average(tmp);
      curStd = std(tmp);
      tmp = new ArrayList<Float>();
      for(int i = 0; centerLine_overlap[i] >= 0; i++)
        tmp.add(new PVector(centerLine.getPoint(i + 1).y - centerLine.getPoint(i).y, centerLine.getPoint(i + 1).x - centerLine.getPoint(i).x).heading());
      centerAvg = average(tmp);
      centerStd = std(tmp);
      println(centerAvg + " " + centerStd);
      println(curAvg + " " + curStd);
      if(curStd > 0 && abs(centerAvg - curAvg) < 1 && abs(centerStd - curStd) < 1)
      {
        in = true;
        groupPoints = curLine.getAllPoints();
        for(int i = centerLine_overlap_start; i <= centerLine_overlap_end; i ++)
          groupPoints.add(centerLine_overlap[i] + i - centerLine_overlap_start, centerLine.getPoint(i));
        for(int i = centerLine_overlap_end + 1; i < centerLine.getSize(); i ++)
          groupPoints.add(groupPoints.size(), centerLine.getPoint(i));
      }
    }
    else if(centerLine_overlap[centerLine.allPoints.size() - 1] >= 0 && curLine_overlap[curLine.allPoints.size() - 1] >= 0)
    {
      ArrayList<Float> tmp = new ArrayList<Float>();
      for(int i = curLine.allPoints.size() - 1; curLine_overlap[i] >= 0; i--)
        tmp.add(new PVector(curLine.getPoint(i).y - curLine.getPoint(i - 1).y, curLine.getPoint(i).x - curLine.getPoint(i - 1).x).heading());
      curAvg = average(tmp);
      curStd = std(tmp);
      tmp = new ArrayList<Float>();
      for(int i = centerLine.allPoints.size() - 1; centerLine_overlap[i] >= 0; i--)
        tmp.add(new PVector(centerLine.getPoint(i - 1).y - centerLine.getPoint(i).y, centerLine.getPoint(i - 1).x - centerLine.getPoint(i).x).heading());
      centerAvg = average(tmp);
      centerStd = std(tmp);
      println(centerAvg + " " + centerStd);
      println(curAvg + " " + curStd);
      if(curStd > 0 && abs(centerAvg - curAvg) < 1 && abs(centerStd - curStd) < 1)
      {
        in = true;
        groupPoints = centerLine.getAllPoints();
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i], curLine.getPoint(i));
        for(int i = curLine_overlap_start - 1; i >= 0; i --)
          groupPoints.add(groupPoints.size(), curLine.getPoint(i));
      }
    }
    else if(centerLine_overlap[0] >= 0 && curLine_overlap[0] >= 0)
    {
      ArrayList<Float> tmp = new ArrayList<Float>();
      for(int i = 0; curLine_overlap[i] >= 0; i++)
        tmp.add(new PVector(curLine.getPoint(i).y - curLine.getPoint(i + 1).y, curLine.getPoint(i).x - curLine.getPoint(i + 1).x).heading());
      curAvg = average(tmp);
      curStd = std(tmp);
      tmp = new ArrayList<Float>();
      for(int i = 0; centerLine_overlap[i] >= 0; i++)
        tmp.add(new PVector(centerLine.getPoint(i + 1).y - centerLine.getPoint(i).y, centerLine.getPoint(i + 1).x - centerLine.getPoint(i).x).heading());
      centerAvg = average(tmp);
      centerStd = std(tmp);
      println(centerAvg + " " + centerStd);
      println(curAvg + " " + curStd);
      if(curStd > 0 && abs(centerAvg - curAvg) < 1 && abs(centerStd - curStd) < 1)
      {
        in = true;
        groupPoints = centerLine.getAllPoints();
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i], curLine.getPoint(i));
        for(int i = curLine_overlap_end + 1; i < curLine.getSize(); i ++)
          groupPoints.add(0, curLine.getPoint(i));
      }
    }
    
    return in; 
  }

  public void printLines() 
  {
    for (int i = 0; i < groupLines.size(); i++) {
      //println("i = " + i); 
      getLine(i).printLineID(); 
    }
  }

  public float getTotalDistance() 
  {
    float totalDistance = 0; 
    //println("In getTotalDistance()"); 
    for (int i = 0; i < groupLines.size(); i++) {
      totalDistance += groupLines.get(i).getTotalDistance();
    }
    return totalDistance;
  }


  public float getTotalTime() 
  {
    //println("In getTotalTime()" ); 
    float totalTime = endTime - startTime; 
    return totalTime;
  }

  public float getAverageVelocity() 
  {
    //println("In averageVelocity"); 
    float averageVelocity = getTotalDistance()/getTotalTime(); 
    //in pixels/millis
    return averageVelocity;
  }

  public ArrayList<Line> getGroupLines() 
  {
    return groupLines;
  }
  public Line getLine(int i) 
  {
    return groupLines.get(i);
  }
  public int getSize() 
  {
    return groupLines.size();
  }
  public void setLineGroupID(int lineGroupID) 
  {
    this.lineGroupID = lineGroupID;
  }
  public float getLineGroupID() 
  {
    return lineGroupID;
  }
  public void printLineGroupID()
  {
    println(lineGroupID);
  }
  public void computeCenterLine() 
  {
    //centerLine = getLine(getSize() - 1); // mock up
    
    //for(int i = 0; i < getSize(); i++)
    if(centerLine == null)
      groupPoints.addAll(getLine(getSize() - 1).getAllPoints());
    ArrayList<PVector> controlPoints = bezierFit.fit(groupPoints);
    //bezier(controlPoints.get(0).x, controlPoints.get(0).y, controlPoints.get(1).x, controlPoints.get(1).y, controlPoints.get(2).x, controlPoints.get(2).y, controlPoints.get(3).x, controlPoints.get(3).y);
    int steps = 10;
    ArrayList<PVector> points = new ArrayList<PVector>();
    for (int i = 0; i <= steps; i++) {
      float t = i / PApplet.parseFloat(steps);
      float x = bezierPoint(controlPoints.get(0).x, controlPoints.get(1).x, controlPoints.get(2).x, controlPoints.get(3).x, t);
      float y = bezierPoint(controlPoints.get(0).y, controlPoints.get(1).y, controlPoints.get(2).y, controlPoints.get(3).y, t);
      points.add(new PVector(x, y));
      //ellipse(x, y, 5, 5);
    }
    centerLine = new Line(points);
  }
  public void drawCenterLine()
  {
    stroke(0, 250, 150);
    centerLine.draw();
    stroke(0, 0, 0);
  }
  public void clear()
  {
    groupLines = new ArrayList<Line>(); 
  }
  public float average(ArrayList<Float> numbers)
  {
    float total = 0;
    for (int i = 0; i < numbers.size(); i++) 
     total += numbers.get(i);
    return total / numbers.size(); 
  }
  public float std(ArrayList<Float> numbers)
  {
    float total = 0;
    for (int i = 0; i < numbers.size(); i++) 
     total += numbers.get(i);
    float average = total / numbers.size(); 
    float totalSquares = 0;
    for (int i = 0; i < numbers.size(); i++) 
      totalSquares += (numbers.get(i) - average)*(numbers.get(i) - average);
    float averageSquare = totalSquares / numbers.size();
    return sqrt(averageSquare);
  } 
}

class Line_Mod{
  Line line;
  ArrayList<PVector> allPoints;
  
  public Line_Mod(Line line){
    this.line = line; //I don't know what am I doing ahhhhh
    this.allPoints = line.getAllPoints();
  }
  
   public Line translation(){
    float lineSpacing = 80;
    Line translatedLine = new Line();
    for (int j = 0; j< allPoints.size(); j++){//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2){//special case for start and end of line
          //generate the normal point and add to the newLine
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y); 
          PVector normal = new PVector(-1, 1); //normal vector to the drawn line (might need to change signs based on slope)
          normal.setMag(lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          PVector newPoint = new PVector(normalPoint.x, normalPoint.y); 
          translatedLine.addPoint(newPoint);
        }
      }
    //translatedLine.drawLine();
    return translatedLine;
  }
  
  public Line reflection(){ //reflects along y = -x
    float lineSpacing = 20;
    Line reflectionLine = new Line();
      
    for (int j = 0; j< allPoints.size(); j++){//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2){//special case for start and end of line
          //generate the normal point and add to the newLine
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y); 
          PVector normal = new PVector(-1,1); //the reflection axis
          normal.setMag(lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          PVector newPoint = new PVector(normalPoint.y, normalPoint.x); 
          reflectionLine.addPoint(newPoint);
        }
      }
   //reflectionLine.drawLine();
   return reflectionLine;
  }
  
  public Line scaling(){ //scales it, just doesn't put the line where I'd like it to.
    float lineSpacing = 80;
    Line scaledLine = new Line();
    
    for (int j = 0; j< allPoints.size(); j++){//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2){//special case for start and end of line
          //generate the normal point and add to the newLine
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y); 
          PVector normal = new PVector(1,-1); //line to get a translation point from
          normal.setMag(lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          PVector newPoint = new PVector(normalPoint.x/2+100, normalPoint.y/2+100); 
          //hard-coded the +100 in an attempt to get the scaled line closer to the origional
          //needs actual fixing.
          scaledLine.addPoint(newPoint);
        }
      }
  // scaledLine.drawLine();
     return scaledLine;
  }
  
  public Line rotation(){ //requires some maths
    float lineSpacing = 20;
    Line rotatedLine = new Line();
    
    for (int j = 0; j< allPoints.size(); j++){//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2){//special case for start and end of line
          //generate the normal point and add to the newLine
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y);
          PVector p1 = new PVector(allPoints.get(allPoints.size()/2).x, allPoints.get(allPoints.size()/2).y);//rotation axis
          
          //math to rotate a point:
          float m = p0.mag() - p1.mag(); //distance between two points
          //float z = acos(abs(
          
          PVector normal = new PVector(1,-1); //line to get a translation point from
          normal.setMag(lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          PVector newPoint = new PVector(normalPoint.y, normalPoint.x); 
          rotatedLine.addPoint(newPoint);
        }
      }
   //rotatedLine.drawLine();
   return rotatedLine;
  } 

  public Line drawBack(Line line){
      //take the previous line and cycle through its components and add some noise to it
      //here, I will just add a 1 * random 0-1 + point to each point, then draw
      //create a new Line of the current line, then newLine.drawLine(); 
      Line newLine = new Line(line.startPoint.x, line.startPoint.y); 
      for (int i = 0; i < line.allPoints.size(); i++)
      {
        //cycle through the points and add in a bit of randomness to each points
    
        //first decide if we should interfere with this point, give it a P of .5 for interfering
        if (random(0, 1) > (1 - probRandom))
        {
          float x = line.allPoints.get(i).x; 
          float y = line.allPoints.get(i).y; 
    
          x = x + random(-degreeRandom, degreeRandom); 
          y = y + random(-degreeRandom, degreeRandom); 
    
          PVector newPoint = new PVector(x, y); 
          newLine.allPoints.add(newPoint);
        }
        else 
          //just add the point to the point array
        newLine.allPoints.add(line.allPoints.get(i));
      }
      //render the line
      //stack.add(newLine);
      return newLine;
    }
}

class particle {
   
  float x;
  float y;
  float px;
  float py;
  float magnitude;
  float angle;
  float mass;
   
  particle( float dx, float dy, float V, float A, float M ) {
    x = dx;
    y = dy;
    px = dx;
    py = dy;
    magnitude = V;
    angle = A;
    mass = M;
  }
   
  public void reset( float dx, float dy, float V, float A, float M ) {
    x = dx;
    y = dy;
    px = dx;
    py = dy;
    magnitude = V;
    angle = A;
    mass = M;
  }
   
  public void gravitate( particle Z ) {
    float F, mX, mY, A;
    if( sq( x - Z.x ) + sq( y - Z.y ) != 0 ) {
      F = mass * Z.mass;
      mX = ( mass * x + Z.mass * Z.x ) / ( mass + Z.mass );
      mY = ( mass * y + Z.mass * Z.y ) / ( mass + Z.mass );
      A = findAngle( mX - x, mY - y );
       
      mX = F * cos(A);
      mY = F * sin(A);
       
      mX += magnitude * cos(angle);
      mY += magnitude * sin(angle);
       
      magnitude = sqrt( sq(mX) + sq(mY) );
      angle = findAngle( mX, mY );
    }
  }
 
  public void repel( particle Z ) {
    float F, mX, mY, A;
    if( sq( x - Z.x ) + sq( y - Z.y ) != 0 ) {
      F = mass * Z.mass;
      mX = ( mass * x + Z.mass * Z.x ) / ( mass + Z.mass );
      mY = ( mass * y + Z.mass * Z.y ) / ( mass + Z.mass );
      A = findAngle( x - mX, y - mY );
       
      mX = F * cos(A);
      mY = F * sin(A);
       
      mX += magnitude * cos(angle);
      mY += magnitude * sin(angle);
       
      magnitude = sqrt( sq(mX) + sq(mY) );
      angle = findAngle( mX, mY );
    }
  }
   
  public void deteriorate() {
    magnitude *= 0.925f;
  }
   
  public void update() {
     
    x += magnitude * cos(angle);
    y += magnitude * sin(angle);
     
  }
   
  public void display() {
    line(px,py,x,y);
    px = x;
    py = y;
  }
   
   
}
 
public float findAngle( float x, float y ) {
  float theta;
  if(x == 0) {
    if(y > 0) {
      theta = HALF_PI;
    }
    else if(y < 0) {
      theta = 3*HALF_PI;
    }
    else {
      theta = 0;
    }
  }
  else {
    theta = atan( y / x );
    if(( x < 0 ) && ( y >= 0 )) { theta += PI; }
    if(( x < 0 ) && ( y < 0 )) { theta -= PI; }
  }
  return theta;
}


class Point{
  float x; 
  float y; 
  
  public Point (float x, float y){
    this.x = x; 
    this.y = y; 
  }
  public void print(){
    println( "( "+ x + " , " + y + " )");
  }
    public void draw(){
      fill(30, 50); 
      color(0,0,255);
    ellipse(x,y,3,3); 
    color(0,0,0);
    fill(0); 
}

public float getX(){
  return x; 
}
public float getY(){
  return y; 
}

public Point calcMidPoint (Point p)
{
  return new Point(this.x + p.x / 2, this.y + p.y / 2);
}

public float calcDistance(Point p)
{
  return sqrt(sq(p.x - this.x) + sq(p.y - this.y));
}

}

class Rectangle{
  int x; 
  int y; 
  int recWidth; 
  int recHeight; 
  
  public Rectangle(int x, int y, int recWidth, int recHeight)
  {
    this.x = x; 
    this.y = y; 
    this.recWidth = recWidth; 
    this.recHeight = recHeight; 
  }
  
  public void drawRect()
  {
    noFill(); 
    stroke(255,0,0); 
    strokeWeight(2); 
    rect(x,y,recWidth,recHeight); 
    strokeWeight(3); 
  }
  public int getHeight()
  {
    return recHeight; 
  }
}

    static public void main(String args[]) {
        PApplet.main(new String[] { "--bgcolor=#ECE9D8", "DrawBackFork_Flow_Lines" });
    }
}
