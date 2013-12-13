//import gab.opencv.*;
import papaya.*;
//OpenCV opencv;
import java.io.*;
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
int particleSize = 2000;
particle[] Z = new particle[particleSize];
int[] currentAttractor = new int[particleSize];
float colour = random(1);
ArrayList<FoodSeeker> foodSeekers = new ArrayList<FoodSeeker>();
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

  /*
  image(opencv.getOutput(), 0, 0);
  strokeWeight(3);
  for (Line line : lines) {
    stroke(0, 255, 0);
    line.drawLine();
  }
  */
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
}

void clear(){
    allLines = new ArrayList<Line>(); 
    background(100);
}

/*void generateFlowLines()
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
      Line stackLine = new Line(x1,y1);
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

void displayAllPrevLines(){
  for(int i = 0; i < allLines.size(); i++){
    if(allLines.get(i).getSize() > 1){
      Line l = allLines.get(i);
      l.draw();
    }
  } 
}

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
