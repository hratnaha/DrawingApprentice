//import gab.opencv.*;
import papaya.*;
//OpenCV opencv;
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
Decision_Engine engine;
//vehice's extra instance variables
Vehicle v;
int counter;
FlowField flowfield;

void setup() 
{
  size(600, 600);
  background(255);
  noFill(); 
  strokeWeight(1); 
  smooth(); 
  i = 0;// initialize the count for the 
  frameRate(20); 
  probRandom = .15; 
  degreeRandom = 5;
  colorMode(HSB, 100, 100, 100);
  gPts = new ArrayList();
  //drawBezier = true;
  //PImage src = loadImage("PUI001.tif");
  //opencv = new OpenCV(this, src);
  lineGroups.add(curLineGroup);
}

void draw() //veh code copied
{
  //could have a stack of lines that need to be processed
  checkStack();
//<<<<<<< HEAD
  //added to make vehicle work correctly
  colorMode(RGB);
  background(255,255,255);
  if(allLines.size() > 0){
   displayAllPrevLines();
 }
 if(curLine != null && curLine.getSize() > 1){
   curLine.draw();
 } 
 
 //this updates the vehicle's path:
 /*
 boolean nullFlag = false;
  if(v!= null){
    counter += 1;
    if(curLine.getSize() > counter){
      PVector target = new PVector(curLine.getPoint(counter).x, curLine.getPoint(counter).y);
       v.arrive(target);
    } else {
      PVector target = curLine.getEndPoint();
      v.arrive(target);
    }
    if(curLine.insideBufferZone(v.loc)){
      println("car now null");
      nullFlag = true;
      v = null;
      displayAllPrevLines();
    }
    if(!nullFlag){
      v.update();
     // v.display();
      if(counter%20 == 0){
       Line line = v.drawTrail(); 
       allLines.add(line);
      }
    }
  } */

//=======
  /*
  image(opencv.getOutput(), 0, 0);
  strokeWeight(3);
  for (Line line : lines) {
    stroke(0, 255, 0);
    line.drawLine();
  }
  */
//>>>>>>> f1e62ce3073dda09c1305dc9ceba5ef04896ad60
}


//##### Event Handling
void mousePressed() 
{
  //println(mouseX + " " + mouseY);
  //line(pmouseX, pmouseY, mouseX, mouseY); 
  curLine = new Line(mouseX, mouseY); 
  allLines.add(curLine);
  gPts = new ArrayList();
  gPts.add(new PVector(mouseX,mouseY));
  gMvCnt = 0;
}
void mouseDragged() 
{
  //println(mouseX + " " + mouseY);
  line(pmouseX, pmouseY, mouseX, mouseY); 
  //check if the slope has not change by 90 degrees
  //if so set line end to previous point and begin new line with current point add previous line to stack
  curLine.curEnd(mouseX, mouseY);
  if ( gMvCnt++ % 5 == 0 )
    gPts.add(new PVector(mouseX,mouseY));
}
void mouseReleased() 
{
  line(pmouseX, pmouseY, mouseX, mouseY); 
  curLine.setEnd(mouseX, mouseY); 
//<<<<<<< HEAD
  engine = new Decision_Engine(curLine);
  Line compLine = engine.decision();
  //allLines.add(compLine);
  stack.add(compLine); //not working QQ
  //displayAllPrevLines();
  }

void keyPressed(){
  if(key == 'c'){
    clear();
  }
  if(key == 'v'){
    v = new Vehicle(curLine.getPoint(0).x, curLine.getPoint(0).y);
    counter = 0;
    //create a new veh at the current line's start
    //will need a better solution for creating the car
  }
//=======
  //printAllLines();
  if(drawBezier)
  {
    drawBezier();
  }
  if(curLineGroup.getSize() > 0) {
    if(curLineGroup.inGroup(curLine))
      curLineGroup.addLine(curLine);
    else {
      println("new group");
      curLineGroup = new LineGroup();
      lineGroups.add(curLineGroup);
      curLineGroup.addLine(curLine);
      curLineGroup.setLineGroupID(lineGroups.size() - 1);
    }
  }
  else{
    curLineGroup.addLine(curLine);
    curLineGroup.setLineGroupID(0);
  }
  curLineGroup.printLineGroupID();
//>>>>>>> f1e62ce3073dda09c1305dc9ceba5ef04896ad60
}

void clear(){
    allLines = new ArrayList<Line>(); 
//<<<<<<< HEAD
    background(100);
}

/*void generateFlowLines()
=======
    //curLineGroup = new LineGroup();
    lineGroups = new ArrayList<LineGroup>();
    background(100);}

void keyPressed()
{
  if (key == 'l')
  {
    lineDetection();
  }
  if (key == 'd')
  {
    for(i = 0; i < lineGroups.size(); i++)
      lineGroups.get(i).drawCenterLine();
  }
  if(key == 'c'){
  clear();}
}

void generateFlowLines()
>>>>>>> f1e62ce3073dda09c1305dc9ceba5ef04896ad60
{
  //cycle through all lines to determine their flow lines
  for(int i = 0; i < allLines.size(); i++)
  {
    Line curLine = allLines.get(i); 
    curLine.generateFlowLines(); 
  }
}
*/

void changeMode (String mode)
{
  //change the mode of the sketch 
  //this information is coming from the button
}

//######## Random DrawBack Mode
void checkStack(){
  if (stack.size() >= 1){
    //println("i: " + i + "Size of allPoints: " + stack.get(0).allPoints.size()); 
    if (i < stack.get(0).allPoints.size()){
      //println("Size: " + stack.size() + "i: " + i); 
      //start the i at 0
      //look at the 
      float x1 = stack.get(0).allPoints.get(i).x;
      float y1 = stack.get(0).allPoints.get(i).y;

      float x2 = stack.get(0).allPoints.get(i + 1).x;
      float y2 = stack.get(0).allPoints.get(i + 1).y;

      //println("x1: " + x1 + "y1: " + y1 + "x2: " + x2 + "y2: " + y2); 
      //line (x1, y1, x2, y2); 
      PVector point1 = new PVector(x1,y1);
      PVector point2 = new PVector(x2,y2);
      stroke(240, 255, 255); 
      Line stackLine = new Line(x1,y1);
      stroke(255); 
      stackLine.addPoint(point2);
      allLines.add(stackLine);
      i++; 

      if (i == stack.get(0).allPoints.size() - 1){
        println("Completed line response"); 
        stack.remove(0); 
        i = 0; //reset the counter
      }
    }
  }
}

//<<<<<<< HEAD
void displayAllPrevLines(){
  for(int i = 0; i < allLines.size(); i++){
    if(allLines.get(i).getSize() > 1){
      Line l = allLines.get(i);
      l.draw();
    }
  } 
}

//=======
void drawBezier()
{
  int sz = gPts.size();
  if ( sz == 0)
    return;
  beginShape();
  stroke(0, 250, 150);
  float x1 = ((PVector)gPts.get(0)).x;
  float y1 = ((PVector)gPts.get(0)).y;
  float xc = 0.0;
  float yc = 0.0;
  float x2 = 0.0;
  float y2 = 0.0;
  vertex(x1,y1);
  for ( int i = 1; i< sz - 2; ++i)
  {
    xc = ((PVector)gPts.get(i)).x;
    yc = ((PVector)gPts.get(i)).y;
    x2 = (xc + ((PVector)gPts.get(i+1)).x)*0.5;
    y2 = (yc + ((PVector)gPts.get(i+1)).y)*0.5;
    bezierVertex((x1 + 2.0*xc)/3.0,(y1 + 2.0*yc)/3.0,
              (2.0*xc + x2)/3.0,(2.0*yc + y2)/3.0,x2,y2);
    x1 = x2;
    y1 = y2;
  }
  xc = ((PVector)gPts.get(sz-2)).x;
  yc = ((PVector)gPts.get(sz-2)).y;
  x2 = ((PVector)gPts.get(sz-1)).x;
  y2 = ((PVector)gPts.get(sz-1)).y;
  bezierVertex((x1 + 2.0*xc)/3.0,(y1 + 2.0*yc)/3.0,
         (2.0*xc + x2)/3.0,(2.0*yc + y2)/3.0,x2,y2);
  endShape();
  stroke(0, 0, 0);
}

void lineDetection(){
  save("db.jpg");
  PImage src = loadImage("db.jpg");
 // opencv = new OpenCV(this, src);
 // opencv.findCannyEdges(20, 75);

  // Find lines with Hough line detection
  // Arguments are: threshold, minLengthLength, maxLineGap
  
  //lines = opencv.findLines(100, 30, 20);
}
//>>>>>>> f1e62ce3073dda09c1305dc9ceba5ef04896ad60
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
  
  void mousePressed()
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
  
  void mouseMoved()
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
  
  void render()
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
 int veh = 5;
 //int decide;

 public Decision_Engine(Line line){ 
   //constructor yeeeeaaaaahhhhhhh is this even needed
   this.line = line;
   //I evidently forgot how to program with objects. I feel bad.
 }
  
  public Line decision(){
    int decision = (int)random(1,6);
    //int decision = 5;
    println(decision);
    if(decision == translate){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.translation();
      //newLine.draw();
      return newLine;
     
    } else if(decision == reflection){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.reflection();
      //newLine.draw();
      return newLine;
      
    } else if(decision == scaling){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.scaling();
      //newLine.draw();
      return newLine;
      
    } else if(decision == drawback){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.drawBack(this.line);
      //newLine.draw();
      return newLine;
      
    } else if(decision == veh){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.vehicleDraw(this.line);
      print("inside decision engine, vechile");
      //Line newLine  = new Line(0,0);
      return newLine;
      
    } else {
      Line newLine  = new Line(0,0);
      return newLine;
    }
  }
  
  public void setLine(Line line){
    this.line = line;
  }
}
class FlowField{
PVector[][] field;
int cols, rows, resolution;
ArrayList<Line> lineList;  //list of lines to base the flow field off of
//PVector[] allmins;  //array of all minimum points
float[] allmins;

FlowField(int r, ArrayList<Line> lineList){
  this.lineList = lineList;
  resolution = r;
  cols = 600/r;
  rows = 600/r;
  //allmins = new PVector[lineList.size()];
  allmins = new float[lineList.size()];
  field = new PVector[cols][rows];  //25x25 grid
  init();
}

void init(){
    float xoff = 0; //the x position of cells
    float yoff = 0; //the y position of cells
    float totaldistance;
    //ArrayList<PVector> allmins = new ArrayList<PVector>(); //all the minimum PVectors
    for (int i = 1; i <= cols; i++){
      for(int j = 1; j <= rows; j++){
        int counter =0;
        totaldistance=0;
        for(Line l: lineList){   //for each line in lineList, get all pvectors of the line and...
          ArrayList<PVector> allPoints = l.getAllPoints();
          xoff = resolution*i;
          yoff = resolution*j;
          HashMap<Float, PVector> mindist = new HashMap<Float, PVector>();
          for(PVector a : allPoints){
            mindist.put(sqrt(pow(a.x-xoff,2) + pow(a.y-yoff,2)), a);  //calculate minimum distance from cell to line and store it with the corresponding PVector
          }
          Object[] treesobject = mindist.keySet().toArray();  //calculates the minimum out of the whole list
          float[] treesfloat = new float[treesobject.length];
          for(int e = 0; e < treesfloat.length; e++)   //puts treesobject into treesfloat; had to do it this way because it wouldn't let me cast an Object[] to a float[]
            treesfloat[e] = (Float)treesobject[e];
          float[] absmin = getDistance(treesfloat);  //takes minimum value of treesfloat, uses the method getDistance() which returns float[] with minimum value and the second smallest value
          PVector min = mindist.get(absmin[0]);  //minimum value
          PVector min2 = mindist.get(absmin[1]);  //second smallest value
          float slope = (min2.y - min.y)/(min2.x - min.x);
          if(slope >= 10000) slope = 10000;
          else if(slope <= -10000) slope = -10000;
          else if(slope <= .00001 && slope >=0) slope = .00001;
          else if(slope >= -.00001 && slope <= 0) slope = -.00001;
          //println(slope);
          //allmins[counter] = PVector.sub(min, min2);
            //println(PVector.angleBetween(min, min2));
            //PVector.fromAngle(PVector.angleBetween(min, min2));
            //new PVector(sqrt(pow(min2.x-min.x,2)), sqrt(pow(min2.y-min.y,2)));
            //new PVector.sub(min, min2);
          //allmins[counter] = PVector.mult(allmins[counter], 1/absmin[0]);
          allmins[counter] = slope*(1/absmin[0]);
          //println(allmins[counter]);
          totaldistance += (1/(absmin[0]));
          counter++;
        }
        //PVector finalvalue = allmins[0];
        float finalvalue = allmins[0];
        //println(finalvalue);
        for(int m = 1; m < allmins.length; m++)
          //finalvalue = PVector.add(finalvalue, allmins[m]);
          finalvalue += allmins[m];
        //finalvalue = PVector.div(finalvalue, totaldistance);
        //finalvalue = PVector.div(finalvalue, allmins.length);
        finalvalue = (finalvalue/totaldistance)/allmins.length;
        //int finalval = Math.round(finalvalue);
            //finalvalue = PVector.fromAngle(PVector.angleBetween(finalvalue, allmins[m]));
        //println(finalvalue);
        PVector finalle = new PVector(1,finalvalue);
        finalle.normalize();
        field[i-1][j-1] = finalle;
      }
   }
}

  //takes a float[] and returns a float[] with minimum and second smallest value
  float[] getDistance(float[] list){
    float min = min(list);
    ArrayList<Float> minlist = new ArrayList<Float>();
    for(int i = 0; i<list.length; i++){
      if(list[i] == min)
        list[i] = Float.MAX_VALUE;
    }
    float[] minl = new float[2];
    minl[0] = min;
    minl[1] = min(list);
    //println("minimum distance value: "+ min);
    //println("2nd minimum distance value: "+ minl[1]);
    return minl;
  }
  

PVector lookup(PVector lookup){
  int col = int(constrain(lookup.x/resolution,0,cols-1));
  int row = int(constrain(lookup.y/resolution,0,rows-1));
  return field[col][row].get();
}

void display(){
  for(int i = 0; i < cols; i++){
    for(int j = 0; j < rows; j++)
        drawVector(field[i][j],i*resolution,j*resolution,resolution-2);
  }
}

  // Renders a vector object 'v' as an arrow and a location 'x,y'
  void drawVector(PVector v, float x, float y, float scayl) {
    pushMatrix(); //pushes current transformation matrix onto matrix stack aka puts it on top of other stuff
    float arrowsize = 4;
    // Translate to location to render vector
    translate(x,y);
    stroke(0,100);
    // Call vector heading function to get direction (note that pointing up is a heading of 0) and rotate
    float head = v.heading();
    //println("head " + head);
    if (head < 0)
    {
      translate(0, 18);
    }
    rotate(head);
    // Calculate length of vector & scale it to be bigger or smaller if necessary
    float len = v.mag()*scayl;
    
    line(0,0,len,0);
    popMatrix();
  }


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

  public void draw() 
  {
    //println("In drawLine()"); 
    //println("Allpoints.size()" + allPoints.size()); 
    strokeWeight(.5); 
    for (int i = 0; i < allPoints.size(); i++) 
    {
      if (i < allPoints.size() - 1) 
      {
        //println("Less than allPoints.size()"); 
        PVector p1 = allPoints.get(i); 
        PVector p2 = allPoints.get(i+1); 
        line(p1.x, p1.y, p2.x, p2.y);
      } 
      else if (i == allPoints.size() - 1) 
      {
       // println("allPoints.size()=" + allPoints.size()); 
        PVector p1 = allPoints.get(i -1); 
        PVector p2 = allPoints.get(i); 
        line(p1.x, p1.y, p2.x, p2.y);
      }
    }
  }

/*  public void generateFlowLines()
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
  */
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
  
  public boolean insideBufferZone(PVector loc){
    int radius = 10; //make a buffer zone raduis of 10 pixels
    float xDiff = loc.x - endPoint.x;
    float yDiff = loc.y - endPoint.y;
    
    float retInt = sqrt(xDiff*xDiff + yDiff*yDiff);
    boolean retBool;
    if(retInt <= radius){
      retBool = true;
    } else retBool = false;
    
    return retBool;
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
    myPoint = new PVector(x, y);
    endPoint = myPoint; 
    allPoints.add(endPoint);
    endTime = millis();
    makeBoundingBox();
  }

  public void printPoints() 
  {
    println("The function is not implemented, please code me~!"); 
    for (int i = 0; i < allPoints.size(); i++) {
      //println("i = " + i); 
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
  public PVector getEndPoint(){
    return endPoint;
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

//Press 'd' to group lines; press 'c' to clean
class LineGroup {
  Line centerLine;
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
  {//manually add a point to allPoints
    groupLines.add(line);
    computeCenterLine();
    //drawCenterLine();
  }

  public boolean inGroup(Line curLine) 
  {
    boolean in = false;
    float minDistance = 100000;
    int nearestPoint;
    for(int i = 0; i < centerLine.allPoints.size(); i++)
    {
      float range = abs(centerLine.getPoint(i).x - curLine.getPoint(0).x) + abs(centerLine.getPoint(i).y - curLine.getPoint(0).y); 
      //println(range);
      if(range < 100)
      {
        in = true;
        if(minDistance > range){
          minDistance = range;
          nearestPoint = i;
        }
      }
      else if(in == true && range >= 10)
        break;
    }
    float tangent;
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
    ArrayList<PVector> linePoints = new ArrayList<PVector>();
    for(int i = 0; i < getSize(); i++)
      linePoints.addAll(getLine(i).getAllPoints());
    ArrayList<PVector> controlPoints = bezierFit.fit(linePoints);
    //bezier(controlPoints.get(0).x, controlPoints.get(0).y, controlPoints.get(1).x, controlPoints.get(1).y, controlPoints.get(2).x, controlPoints.get(2).y, controlPoints.get(3).x, controlPoints.get(3).y);
    int steps = 10;
    ArrayList<PVector> points = new ArrayList<PVector>();
    for (int i = 0; i <= steps; i++) {
      float t = i / float(steps);
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
    
    //put a vehicle thing here?
  
public Line vehicleDraw(Line line){
  Vehicle v = new Vehicle (line.startPoint.x, line.startPoint.y);
  print("in vehicleDraw");
  Line newline = new Line(0,0);
  boolean nullFlag = false;
  while(!nullFlag){
  if(v!= null){
    counter += 1;
    if(line.getSize() > counter){
      PVector target = new PVector(line.getPoint(counter).x, line.getPoint(counter).y);
       v.arrive(target);
       print("following line");
    } else {
      PVector target = line.getEndPoint();
      v.arrive(target);
      print("reaching end");
    }
    if(curLine.insideBufferZone(v.loc)){
      newline = v.drawTrail();
      //println("car now null");
      nullFlag = true;
      v = null;
      print("car now null");
    }
    if(!nullFlag){
      v.update();
      }
    }
  }
      //displayAllPrevLines();
     // Line newline = v.drawTrail();
      print("at return");
      return newline;
} 
      
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
class Vehicle{
  PVector loc;
  PVector vel;
  PVector acc;
  
  ArrayList<PVector> locations = new ArrayList<PVector>();
  
  float r;
  float maxspeed;
  float maxforce;
  
  Vehicle(float x, float y){
    acc = new PVector(0,0);
    vel = new PVector(0,0);
    loc = new PVector(x,y);
    r = 3.0;
    
    maxspeed = 4; //arbitrary number
    maxforce = 0.1; //same here
  }
  
  void applyForce(PVector force){
    acc.add(force);
  }
  
  void display(){
    float theta = vel.heading() + PI/2;
    fill(175);
    stroke(0);
    pushMatrix();
    translate(loc.x, loc.y);
    rotate(theta);
    beginShape();
    vertex(0, -r*2);
    vertex(-r,r*2);
    vertex(r,r*2);
    endShape(CLOSE);
    popMatrix();
  }
  
  void update(){
   // print(loc);
    
    PVector m = new PVector(loc.x,loc.y);
    locations.add(m);
    //print(locations);
    
    vel.add(acc);
    vel.limit(maxspeed);
    loc.add(vel);
    acc.mult(0);
  }
  
  
  void seek(PVector target){
    PVector desire = PVector.sub(target, loc);
    desire.normalize();
    desire.mult(maxspeed);
    
    PVector steer = PVector.sub(desire,vel);
    steer.limit(maxforce);
    
    applyForce(steer);
  }
  
  void arrive(PVector target){
    PVector desire = PVector.sub(target, loc);
    
    float d = desire.mag();
    desire.normalize();
    
    if(d < 100){
      float m = map(d,0,100,0,maxspeed);
      desire.mult(m);
    } else {
      desire.mult(maxspeed);
    }
    
    PVector steer = PVector.sub(desire,vel);
    steer.limit(maxforce);
    applyForce(steer);
  }
  
  public Line drawTrail(){
    Line line = new Line();
   // print("In draw Trail");
    if(locations.size() >= 20){
      int start = (int)random(0,locations.size()-21);
      for(int i = start; i < start+20; i++){
        PVector point = locations.get(i);
       // PVector point2 = locations.get(i+1);
        //line(point.x, point.y, point2.x, point2.y);
        line.addPoint(point);
      }
      //line.draw();
    }
    return line;
  }
  
  public PVector getLoc(){
    return loc;
  }
    
}

