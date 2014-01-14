import g4p_controls.*;

//import gab.opencv.*;
import papaya.*;
//OpenCV opencv;
import java.io.*;
ArrayList <Line> allLines = new ArrayList<Line>(); 
Line curLine; 
StringList strings = new StringList(); // strings for file output
int stringsCount = 0; // count how many lines should be written to files
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
PImage catIcon; 
PImage dogIcon;
PImage ratIcon;

String drawingMode = "draw";  //draw, teach, drawPos
Shape myShape; 
Shape targetShape; 
ArrayList<Shape> allShapes = new ArrayList<Shape>(); 
boolean intClick = false; 

Rectangle shapeBound; 
boolean shapeDrag = false; 

void setup() 
{
  //shapeBound = new Rectangle(mouseX, mouseY, 0, 0);
  catIcon = loadImage("cat.png"); 
  dogIcon = loadImage("dog.png"); 
  ratIcon = loadImage("rat.png"); 
  myShape = new Shape(); 
  size(700, 700, JAVA2D);
  createGUI();
  customGUI();
  background(255);
  noFill(); 
  strokeWeight(1); 
  smooth(); 
  i = 0;// initialize the count for the 
  frameRate(20); 
  probRandom = .15; 
  degreeRandom = 5;
  //colorMode(HSB, 100, 100, 100);
  gPts = new ArrayList();
  //drawBezier = true;
  //PImage src = loadImage("PUI001.tif");
  //opencv = new OpenCV(this, src);
  lineGroups.add(curLineGroup);
}

void draw() //veh code copied
{
  //could have a stack of lines that need to be processed
  //println("Mode: "+ drawingMode); 

  background(255);

  checkStack();
  //added to make vehicle work correctly
  //colorMode(RGB);
  //background(255,255,255);
  if (allLines.size() > 0) {
    displayAllPrevLines();
  }
  if (curLine != null && curLine.getSize() > 1) {
    curLine.draw();
  } 
  if (keyPressed == true && key == 'g') {
    gravitateSwarm();
  }
  for (int i = 0; i < foodSeekers.size(); i++) { // FoodSeeker will automatically run
    foodSeekers.get(i).run(allLines);
    //println("FoodSeeker " + i + ":");
  }
  textSize(16); 
  if (drawingMode=="teach") {
    fill(0);
    text("Type name of object, draw it, then click done!", 235, 10, 220, 50);
  }
  if (drawingMode=="drawPos") {
    fill(0); 
    text("Click where you want me to.", 235, 10, 220, 50);
  }
  if (shapeDrag) {
    shapeBound.drawRect();
  }
}


// Redraw all drawn lines
void redraw()
{
  background(255);
  println("allLines.size() = " + allLines.size());
  strokeWeight(1); 
  for (int i = 0; i < allLines.size(); i++)
  {
    //allLines.get(i).draw();
  }
}

//##### Event Handling
void mousePressed() 
{
  checkInterface(new PVector(mouseX, mouseY)); 
  //println("intClick = " + intClick +" Drawing Mode: " + drawingMode); 
  if (drawingMode=="teach" || drawingMode=="draw" && intClick != true) {
    //println("drawingLine"); 
    curLine = new Line(mouseX, mouseY); 
    allLines.add(curLine);
    gPts = new ArrayList();
    gPts.add(new PVector(mouseX, mouseY));
    gMvCnt = 0;
  }
  else if (drawingMode=="drawPos" && intClick!=true) {
    shapeBound = new Rectangle(new PVector(mouseX, mouseY), 0, 0);
    println(shapeBound.getPos()); 
    //println("shapeBound w: " + shapeBound.t);
  }
}
void mouseDragged() 
{
  if (drawingMode=="draw" || drawingMode=="teach" && intClick!=true) {
    //println("dragging drawing"); 
    line(pmouseX, pmouseY, mouseX, mouseY); 
    //check if the slope has not change by 90 degrees
    //if so set line end to previous point and begin new line with current point add previous line to stack
    curLine.curEnd(mouseX, mouseY);
    if ( gMvCnt++ % 5 == 0 )
      gPts.add(new PVector(mouseX, mouseY));
  }
  if (drawingMode=="drawPos" && !shapeDrag) {
    shapeDrag = true;
  }
  if (shapeDrag) {
    //println("w = " + shapeBound.w); 
    shapeBound.setPos2(new PVector(mouseX, mouseY));
  }
}
void mouseReleased() 
{ 
  if (!intClick) {
    //println("In mouse released"); 
    line(pmouseX, pmouseY, mouseX, mouseY); 
    curLine.setEnd(mouseX, mouseY); 
    if (drawBezier)
    {
      drawBezier();
    }
    boolean in = false;
    if (curLineGroup.getSize() == 0 && lineGroups.size() == 1) {
      curLineGroup.addLine(curLine);
      curLineGroup.setLineGroupID(0);

      for (int i = 0; i < Z.length; i++) {
        float radius = random(100);
        float angle = random(6.28);
        Z[i] = new particle(curLineGroup.centerLine.getPoint(0).x + radius * cos(angle), curLineGroup.centerLine.getPoint(0).y + radius * sin(angle), 0, 0, 1);
        //Z[i] = new particle( random(width), random(height), 0, 0, 1 );
        //println("Particle " + Z[i].x + " " + Z[i].y);
      }
    }
    else {
      for (int i = 0; i < lineGroups.size(); i++) {
        curLineGroup = lineGroups.get(i);
        //println("c" + i);
        if (curLineGroup.inGroup(curLine)) {
          curLineGroup.addLine(curLine);
          in = true;
          break;
        }
      }
      if (in == false) {
        //println("new group");
        curLineGroup = new LineGroup();
        lineGroups.add(curLineGroup);
        curLineGroup.addLine(curLine);
        curLineGroup.setLineGroupID(lineGroups.size() - 1);
        //
        for (int i = 0; i < Z.length; i++) {
          float radius = random(100);
          float angle = random(6.28);
          Z[i] = new particle(curLineGroup.centerLine.getPoint(0).x + radius * cos(angle), curLineGroup.centerLine.getPoint(0).y + radius * sin(angle), 0, 0, 1);
          //Z[i] = new particle( random(width), random(height), 0, 0, 1 );
          //println("Particle " + Z[i].x + " " + Z[i].y);
        }
      }
    } 
    //curLineGroup.printLineGroupID();
    if (drawingMode == "draw") {
      //println("In drawing mode mouseReleased"); 
      engine = new Decision_Engine(curLine);
      Line compLine = engine.decision();
      //allLines.add(compLine);
      stack.add(compLine); //not working QQ
      //displayAllPrevLines();
    }
    else if (drawingMode=="drawPos") {
      createShape(shapeBound.origin);
      drawingMode = "draw"; 
      //println("mouseReleased"); 
      //calculate scale
    }
    else if (drawingMode == "teach")
    {
      //println(myShape); 
      Line tempLine = new Line();  
      for (int i = 0; i < curLine.allPoints.size(); i++) {
        tempLine.addPoint(new PVector(curLine.allPoints.get(i).x, curLine.allPoints.get(i).y));
      }
      //need to iterate over the line and populate another one

      myShape.addLine(tempLine);
      myShape.completeShape();
    }
  }
  else intClick=false;
  shapeDrag = false;
}

void keyPressed()
{
  /*
  if (key == 'l')
   {
   lineDetection();
   }
   
   if (key == 'd') { // draw center line
   redraw();
   for (i = 0; i < lineGroups.size(); i++)
   {
   lineGroups.get(i).drawCenterLine();
   }
   }
   if (key == 'c') {
   clear();
   }
   if (key == 'r') {
   redraw();
   }
   if (key == 'f') { // generate a FoodSeeker and let it run
   for (int i = 0; i < lineGroups.size(); i++)
   {
   //int size = lineGroups.get(i).getCenterLine().getSize();
   //PVector position = lineGroups.get(i).getCenterLine().getPoint(round(random(size - 1)));
   for (int j = 0; j < lineGroups.get(i).getSize(); j++) {
   int size = lineGroups.get(i).getLine(j).getSize();
   PVector position = lineGroups.get(i).getLine(j).getPoint(round(random(size - 1)));
   
   //println(position);
   
   //PVector initialVector = new PVector(lineGroups.get(i).getCenterLine().getPoint(1).x - lineGroups.get(i).getCenterLine().getPoint(0).x, lineGroups.get(i).getCenterLine().getPoint(1).y - lineGroups.get(i).getCenterLine().getPoint(0).y);
   FoodSeeker foodSeeker = new FoodSeeker(position, 1, random(-3.14, 3.14), 30, 0.3);
   //foodSeeker.render();
   foodSeekers.add(foodSeeker);
   }
   }
   }
   if (key == 's') { // save drawn lines to file
   selectOutput("Select a file to write to:", "fileOutputSelected");
   }
   if (key == 'l') { // load drawn lines from file
   selectInput("Select a file to load from:", "fileInputSelected");
   }
   if (key == 'v') {
   v = new Vehicle(curLine.getPoint(0).x, curLine.getPoint(0).y);
   counter = 0;
   //create a new veh at the current line's start
   //will need a better solution for creating the car
   }
   */
}

void clear() {
  allLines = new ArrayList<Line>(); 
  //curLineGroup = new LineGroup();
  lineGroups = new ArrayList<LineGroup>();
  background(255);
}

void fileOutputSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } 
  else {
    println("User selected " + selection.getAbsolutePath());
    SaveStrings(selection.getAbsolutePath(), strings);
  }
}

void fileInputSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } 
  else {
    println("User selected " + selection.getAbsolutePath());
    strings = LoadStrings(selection.getAbsolutePath());
    allLines = StringsToLines(strings);
    redraw();
  }
}


void SaveStrings(String fileName, StringList list)
{
  String[] string = list.array();
  saveStrings(fileName, string);
}

StringList LoadStrings(String fileName)
{
  String string[] = loadStrings(fileName);
  StringList readVal = new StringList();
  for (int i = 0; i < string.length; i++)
    readVal.append(string[i]);
  return readVal;
} 

ArrayList<Line> StringsToLines(StringList stringList)
{
  ArrayList<Line> readVal = new ArrayList<Line>();
  for (int i = 0; i < stringList.size(); i++) {
    String s = stringList.get(i);
    if (s.equals("newLine")) {
      curLine = new Line();
      readVal.add(curLine);
    }
    else {
      float[] tmp = float(splitTokens(s));
      //println(tmp[0]+ " " + tmp[1]);
      curLine.curEnd(tmp[0], tmp[1]);
    }
  }
  return readVal;
} 
/*
void SaveObject(String fileName, Object toSave)
 {
 ObjectOutputStream out = null;
 try
 {
 FileOutputStream fos = new FileOutputStream(fileName);
 OutputStream os = fos;
 out = new ObjectOutputStream(os);
 out.writeObject(toSave);
 out.flush();
 }
 catch (IOException e)
 {
 e.printStackTrace();
 }
 finally
 {
 if (out != null)
 {
 try { out.close(); } catch (IOException e) {}
 }
 }
 }
 
 Object LoadObject(String fileName)
 {
 ObjectInputStream in = null;
 Object readVal = null;
 try
 {
 FileInputStream fis = new FileInputStream(fileName);
 InputStream is = fis;
 in = new ObjectInputStream(is);
 readVal = in.readObject();
 }
 catch (IOException e)
 {
 e.printStackTrace();
 }
 catch (ClassNotFoundException e)
 {
 e.printStackTrace();
 }
 finally
 {
 if (in != null)
 {
 try { in.close(); } catch (IOException e) {}
 }
 }
 return readVal;
 } 
 */
void gravitateSwarm()
{
  //filter(INVERT);

  float r;
  redraw();
  stroke(0);
  rect(0, 0, width, height);
  colorMode(HSB, 1);
  for (int i = 0; i < Z.length; i++) {
    /*
    for(int j = 0; j < curLineGroup.getSize(); j++) {
     for(int k = 0; k < curLineGroup.getLine(j).getSize(); k++) {
     //for(int k = 0; k < 1; k++) {
     //println(i + " " + j + " " + k);
     Z[i].gravitate(new particle(curLineGroup.getLine(j).getPoint(k).x, curLineGroup.getLine(j).getPoint(k).y, 0, 0, 0.001 ) );
     //else {
     //Z[i].deteriorate();
     //}
     //if(sqrt(sq(Z[i].x - curLineGroup.centerLine.getPoint(curLineGroup.centerLine.getSize() - 1).x) + sq(Z[i].y - curLineGroup.centerLine.getPoint(curLineGroup.centerLine.getSize() - 1).y)) < 100)
     //Z[i].deteriorate();
     //else {
     Z[i].update();
     r = float(i)/Z.length;
     stroke(colour, pow(r,0.1), 1-r, 0.15 );
     Z[i].display();
     //}
     }
     }*/
    if ((currentAttractor[i] + 1) < curLineGroup.getCenterLine().getSize() && dist(Z[i].x, Z[i].y, curLineGroup.getCenterLine().getPoint(currentAttractor[i]).x, curLineGroup.getCenterLine().getPoint(currentAttractor[i]).y) < 90)
      currentAttractor[i]++;
    //println(i + " attracted by " + currentAttractor[i] + " in " + curLineGroup.getCenterLine().getSize());
    Z[i].gravitate(new particle(curLineGroup.getCenterLine().getPoint(currentAttractor[i]).x, curLineGroup.getCenterLine().getPoint(currentAttractor[i]).y, 0, 0, 0.1 ) );
    Z[i].update();
    r = float(i)/Z.length;
    stroke(colour, pow(r, 0.1), 1-r, 0.15 );
    Z[i].display();
  }
  colorMode(RGB, 255);
  colour+=random(0.01);
  if ( colour > 1 ) {
    colour = colour%1;
  }
  stroke(0, 0, 0);
  //filter(INVERT);
}

void changeMode (String mode)
{
  //change the mode of the sketch 
  //this information is coming from the button
}

//######## Random DrawBack Mode
void checkStack() {
  //println("Stack Size: " + stack.size()); 
  if (stack.size() >= 1) {
    if (i < stack.get(0).allPoints.size() - 1) {
      //println("Size = " + stack.size() + "Size of allPoints: " + stack.get(0).allPoints.size()); 
      //pritln("Size: " + stack.size() + "i: " + i); 
      //start the i at 0
      //look at the 
      float x1 = stack.get(0).allPoints.get(i).x;
      float y1 = stack.get(0).allPoints.get(i).y;

      float x2 = stack.get(0).allPoints.get(i + 1).x;
      float y2 = stack.get(0).allPoints.get(i + 1).y;

      //println("x1: " + x1 + "y1: " + y1 + "x2: " + x2 + "y2: " + y2); 
      //line (x1, y1, x2, y2); 

      PVector point1 = new PVector(x1, y1);
      PVector point2 = new PVector(x2, y2);
      Line stackLine = new Line(x1, y1);

      stackLine.addPoint(point2);
      //fill(#7fff00);
      imageMode(CENTER);
      int rst = floor(random(2.99));
      if(rst == 2)
        image(catIcon, x2, y2); 
      else if(rst == 1)
        image(dogIcon, x2, y2); 
      else if(rst == 0)
        image(ratIcon, x2, y2); 

      //noFill(); 
      allLines.add(stackLine);
      i++; 
      //println("Stack Size: " + stack.size() + " i = " + i + " Size = " + (stack.get(0).allPoints.size()-1)); 
      if (i == stack.get(0).allPoints.size() - 1) {
        println("Completed line response"); 
        stack.remove(0); 
        i = 0; //reset the counter
      }
    }
    else stack.remove(0);
  }
}

void displayAllPrevLines() {
  for (int i = 0; i < allLines.size(); i++) {
    if (allLines.get(i).getSize() > 1) {
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
  vertex(x1, y1);
  for ( int i = 1; i< sz - 2; ++i)
  {
    xc = ((PVector)gPts.get(i)).x;
    yc = ((PVector)gPts.get(i)).y;
    x2 = (xc + ((PVector)gPts.get(i+1)).x)*0.5;
    y2 = (yc + ((PVector)gPts.get(i+1)).y)*0.5;
    bezierVertex((x1 + 2.0*xc)/3.0, (y1 + 2.0*yc)/3.0, 
    (2.0*xc + x2)/3.0, (2.0*yc + y2)/3.0, x2, y2);
    x1 = x2;
    y1 = y2;
  }
  xc = ((PVector)gPts.get(sz-2)).x;
  yc = ((PVector)gPts.get(sz-2)).y;
  x2 = ((PVector)gPts.get(sz-1)).x;
  y2 = ((PVector)gPts.get(sz-1)).y;
  bezierVertex((x1 + 2.0*xc)/3.0, (y1 + 2.0*yc)/3.0, 
  (2.0*xc + x2)/3.0, (2.0*yc + y2)/3.0, x2, y2);
  endShape();
  stroke(0, 0, 0);
}

void lineDetection() {
  save("db.jpg");
  PImage src = loadImage("db.jpg");
  // opencv = new OpenCV(this, src);
  // opencv.findCannyEdges(20, 75);

  // Find lines with Hough line detection
  // Arguments are: threshold, minLengthLength, maxLineGap

  //lines = opencv.findLines(100, 30, 20);
}

public void customGUI() {
}

public void createShape(PVector pos) {
  //println("In createShape pos: " + pos); 
  println("In createShape, about to setDimesion of w = " + shapeBound.w + shapeBound.h); 
  Shape t = targetShape.createInstance(pos, shapeBound.w, shapeBound.h); 
  println("T: " + t); 
  //this should return a shape

  for (int j = 0; j < t.allLines.size(); j++) {
    //println("in for loop"); 
    Line line = t.allLines.get(j); 
    //println("Points in this line: "); 
    //line.printPoints();
    //println("Adding line to stack. j= " + j + " size of line = " + line.allPoints.size()) ; 
    stack.add(line);
    //println("Stack size = " + stack.size());
  }
}

public void checkInterface(PVector pos) {
  float[][] elements = {
    {
      teachMeTF.getX(), teachMeTF.getY(), teachMeTF.getWidth(), teachMeTF.getHeight()
      }
      , 
    {
      drawMeTF.getX(), drawMeTF.getY(), drawMeTF.getWidth(), drawMeTF.getHeight()
      }
      , 
    {
      teachMeButton.getX(), teachMeButton.getY(), teachMeButton.getWidth(), teachMeButton.getHeight()
      }
      , 
    {
      drawMeButton.getX(), drawMeButton.getY(), drawMeButton.getWidth(), drawMeButton.getHeight()
      }
    };

    for (int i = 0; i < elements.length; i++) {
      float[] s = elements[i]; 
      if (pos.x > s[0] && pos.x < s[0] + s[2]) {
        //println"Within the bounds");
        if (pos.y> s[1] && pos.y < s[1] + s[3]) {
          //println("Within bounds"); 
          intClick = true; 
          //println("Int click detected"); 
          break;
        }
      }
      else intClick = false;
    }
}

