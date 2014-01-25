import g4p_controls.*;
ArrayList <Line> allLines = new ArrayList<Line>(); 
ArrayList <Line> stack = new ArrayList<Line>(); //keep track of the drawBack stack
ArrayList gPts;//related to current drawing stroke
ArrayList<Shape> allShapes = new ArrayList<Shape>(); 
String drawingMode = "draw";  //draw, teach, drawPos
StringList strings = new StringList(); // strings for file output
float probRandom; //the amount of time that the drawBack will interject randomness
float degreeRandom; //how much fluctuation is introduced in random interjections
int i; //iteration count for stack
int gMvCnt = 0;
int particleSize = 2000;
int counter;
int stringsCount = 0; // count how many lines should be written to files
PImage catIcon; 
PImage dogIcon;
PImage ratIcon;
boolean shapeDrag = false; 
boolean intClick = false; 
boolean lineGroup = false; 
boolean drawBack = false; 
Shape myShape; 
Shape targetShape; 
Rectangle shapeBound; 
Line curLine; 
Decision_Engine engine;

void setup() 
{
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
  gPts = new ArrayList();
}

void draw() //veh code copied
{
  background(255);
  checkStack();
  if (allLines.size() > 0) {
    displayAllPrevLines();
  }
  if (curLine != null && curLine.getSize() > 1) {
    curLine.draw();
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
}
void clear() {
  allLines = new ArrayList<Line>(); 
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
//######## Random DrawBack Mode
void checkStack() {
  if (stack.size() >= 1) {
    if (i < stack.get(0).allPoints.size() - 1) {
      float x1 = stack.get(0).allPoints.get(i).x;
      float y1 = stack.get(0).allPoints.get(i).y;
      float x2 = stack.get(0).allPoints.get(i + 1).x;
      float y2 = stack.get(0).allPoints.get(i + 1).y;
      PVector point1 = new PVector(x1, y1);
      PVector point2 = new PVector(x2, y2);
      Line stackLine = new Line(x1, y1);
      stackLine.addPoint(point2);
      imageMode(CENTER);
      image(catIcon, x2, y2); 
      /*
      int rst = floor(random(2.99));
       if(rst == 2)
       image(catIcon, x2, y2); 
       else if(rst == 1)
       image(dogIcon, x2, y2); 
       else if(rst == 0)
       image(ratIcon, x2, y2); 
       */
      allLines.add(stackLine);
      i++; 
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

void lineDetection() {
  save("db.jpg");
  PImage src = loadImage("db.jpg");
}

public void customGUI() {
}

public void createShape(PVector pos) {
  //println("In createShape pos: " + pos); 
  Shape t = targetShape.createInstance(pos, shapeBound.w, shapeBound.h); 
  //this should return a shape
  for (int j = 0; j < t.allLines.size(); j++) {
    Line line = t.allLines.get(j); 
    stack.add(line);
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

class Decision_Engine {
  Line line;
  public Decision_Engine(Line line) { 
    this.line = line;
  }
  public Line decision() {
    int decision = (int)random(1, 5);
    Line_Mod m = new Line_Mod(this.line); 
    Line newLine = new Line(); 
    switch(decision) {
    case 1: 
      newLine = m.translation();
      break;
    case 2: 
      newLine = m.reflection();
      break; 
    case 3: 
      newLine = m.scaling();
      break;
    case 4: 
      newLine = m.drawBack(this.line);
      break; 
    }
    return newLine; 
  }
  public void setLine(Line line) {
    this.line = line;
  }
}

class Line {
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
  public Line()
  {
    startTime = millis();
  }
  public Line(float x, float y)
  {
    myPoint = new PVector(x, y); 
    startPoint = myPoint;
    allPoints.add(startPoint);
    startTime = millis();
  }
  public Line(ArrayList<PVector> all)
  {
    startPoint = all.get(0);
    endPoint = all.get(all.size() - 1);
    allPoints = all;
  }
  public Line(PVector[] all)
  {
    startPoint = all[0];
    endPoint = all[all.length - 1];
    for (int i = 0; i < all.length; i++)
      allPoints.add(all[i]);
  }
  public void draw() 
  {
    for (int i = 0; i < allPoints.size(); i++) 
    {
      if (i < allPoints.size() - 1) 
      {
        //println("Less than allPoints.size()"); 
        PVector p1 = allPoints.get(i); 
        PVector p2 = allPoints.get(i+1); 
        line(p1.x, p1.y, p2.x, p2.y);
      }
    }
  }
  public void makeBoundingBox() {
    myBoundingBox = new Rectangle(allLines); 
    myBoundingBox.calculateBounds();
  }
  public boolean insideBufferZone(PVector loc) {
    int radius = 10; //make a buffer zone raduis of 10 pixels
    float xDiff = loc.x - endPoint.x;
    float yDiff = loc.y - endPoint.y;
    float retInt = sqrt(xDiff*xDiff + yDiff*yDiff);
    boolean retBool;
    if (retInt <= radius) {
      retBool = true;
    } 
    else retBool = false;
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
    float averageVelocity = getTotalDistance()/getTotalTime(); 
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
  public PVector getEndPoint() {
    return endPoint;
  }
  public PVector getPoint(int i) {
    return allPoints.get(i);
  }
  public int getSize() {
    return allPoints.size();
  }
  public float getRectHeight() {
    float w = myBoundingBox.w; 
    return myBoundingBox.h;
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
  
class Rectangle {
  //Rectangle can take a array list of lines and create a bounding rectangle around those lines
  float w; 
  float h; 
  PVector pos2;// = pos;  
  ArrayList<Line> lines; 
  PVector min, max, origin, pos; 

  public Rectangle(PVector pos, float width, float height)
  {
    this.w = width; 
    this.h = height;
    this.pos = pos;
  }
  public Rectangle(ArrayList<Line> lines) {
    this.lines = lines;
    calculateBounds();
  }

  public void drawRect()
  {
    //need to draw the rect, but it is not added to stack, maybe it is about the placement before background.
    fill(255, 150); 
    stroke(0); 
    strokeWeight(1); 
    rectMode(CORNERS);
    rect(pos.x, pos.y, pos2.x, pos2.y); 
    strokeWeight(1);
    rectMode(CORNER);
  }


  public void setWidth(float width) {
    w = width;
  }
  public void calculateBounds() {
    //calculateBounds copy: 
    //get new origin point
    for (int i = 0; i < lines.size(); i++) {
      Line l = lines.get(i); 
      PVector s = l.allPoints.get(0); 
      min = new PVector(s.x, s.y); 
      max = new PVector(s.x, s.y);
      //println("Number 2 Min: " + min + " Max: " + max); 
      //println("Allpoints size: " +l.allPoints.size()); 
      for (int j =0; j<l.allPoints.size(); j++) {
        PVector p = l.allPoints.get(j); 
        //println("Current point p= " + p + "MinX = " + min.x + " maxX: " + max.x); 
        if (p.x < min.x) {
          min.x = p.x;
        }
        else if (p.x > max.x) {
          max.x = p.x;
        }
        if (p.y < min.y) {
          min.y = p.y;
        }
        else if (p.y > max.y) {
          max.y = p.y;
        }
      }
    }
    this.w = max.x - min.x; 
    this.h = max.y - min.y; 
    this.origin = new PVector(min.x, max.y); 
    println("Final Min-Mix. Min.x: " + min.x + "min.y: " + min.y + " max.x: " + max.x + "max.y: " + max.y);
  }

  public void setPos2(PVector pos2) {
    //println("Pos2 = " + pos2 + "Pos1: " + pos); 
    this.pos2 = pos2; 
    float x1 = max(pos.x, this.pos2.x); 
    float x2 = min(pos.x, this.pos2.x); 
    float y1 = max(pos.y, this.pos2.y); 
    float y2 = min(pos.y, this.pos2.y); 
    w = x1 - x2; 
    h = y1 - y2; 
    this.origin = new PVector(x2, y1); 
    println("In Rectangle class- new w: " + w + " h: " + h); 
    //here is the bug
  }
  public void setHeight(float height) {
    h = height;
  }
  public void setPos(PVector pos) {
    this.pos = pos;
  }

  public float getWidth() {
    return w;
  }
  public float getHeight() {
    return h;
  }
  public PVector getPos() {
    return pos;
  }
}

class Shape {
  //A Shape is a collection of lines the user draws and assigns a label to
  //Shapes are associated with the TeachMe/DrawMe features
  public ArrayList<Line> allLines = new ArrayList<Line>(); 
  public String ID = ""; 
  public PVector pos = new PVector(0, 0); 
  public float w = 0; 
  public float h = 0; 
  PVector min = null; 
  PVector max = null; 
  public Shape() {
  }
  public void addLine(Line line) {
    this.allLines.add(line);
  }

  public void setID(String ID) {
    this.ID = ID;
  }
  public String getID() {
    return ID;
  }
  public void completeShape() {
    calculateBounds(); 
    w = max.x - min.x; 
    h = max.y - min.y;
  }


  public Shape createInstance(PVector pos, float w, float h) {
    //create a new array list and width, etc. right here, don't keep it up
    //do everything, the draw and everything right form within this function, all the variables are local
    ArrayList<Line> editLines = new ArrayList<Line>(); 
    //when it scales the lines, it should recalc the origin
    //setDimensions 
    println("w: " + this.w + " h: " + this.h + " input W: " + w+ " input H: " + h); 
    float xScale = w/this.w; 
    float yScale = h/this.h;
    println("xScale: " + xScale + "yScale" + yScale); 
    for (int i= 0; i< allLines.size(); i++) {
      Line l = allLines.get(i); 
      Line newLine = new Line(); 
      for (int j = 0; j<l.allPoints.size(); j++) {
        PVector p = new PVector(l.allPoints.get(j).x, l.allPoints.get(j).y); 
        p.x = p.x * xScale; 
        p.y = p.y * yScale; 
        newLine.addPoint(p); 
      }
      editLines.add(newLine);
      println("Added a new Line: " + newLine.allPoints.size());
    }
    Rectangle testRec = new Rectangle(editLines); 
    //this is calculating the bounds of the original thing, rather than the new one, i have the bounds of the new one
    calculateBounds();
    //setPosition
    PVector pt1 = new PVector(pos.x, pos.y);
    //this.pos is not getting set.
    PVector pt2 = testRec.origin;
    PVector diff = PVector.sub(pt2, pt1);
    //println("Diff: " + diff); 
    println("Pt1: "+pt1+ " +  Pt2: " + pt2 + " Diff: " + diff);
    for (int i=0; i < editLines.size(); i++) {
      Line l = editLines.get(i); 
      for (int j=0; j < l.allPoints.size(); j++) {
        PVector pt = l.allPoints.get(j); 
        //println("Pt before transform: " + pt); 
        pt.sub(diff);
        //println("Pt after transform: " + pt);
      }
      //l.printPoints();
    }
    Shape myShape = new Shape(); 
    myShape.allLines = editLines; 
    myShape.pos = pos; 
    myShape.w = w; 
    myShape.h = h; 
    return myShape;
  }

  public void calculateBounds() {
    //calculateBounds copy: 
    //get new origin point
    for (int i = 0; i < allLines.size(); i++) {
      Line l = allLines.get(i); 
      PVector s = l.allPoints.get(0); 
      min = new PVector(s.x, s.y); 
      max = new PVector(s.x, s.y);
      //println("Number 2 Min: " + min + " Max: " + max); 
      //println("Allpoints size: " +l.allPoints.size()); 
      for (int j =0; j<l.allPoints.size(); j++) {
        PVector p = l.allPoints.get(j); 
        //println("Current point p= " + p + "MinX = " + min.x + " maxX: " + max.x); 
        if (p.x < min.x) {
          min.x = p.x;
        }
        else if (p.x > max.x) {
          max.x = p.x;
        }
        if (p.y < min.y) {
          min.y = p.y;
        }
        else if (p.y > max.y) {
          max.y = p.y;
        }
      }
    }

    this.pos = new PVector(min.x, max.y); 
    println("In shape, calc bounds. Final Min.x: " + min.x + "min.y: " + min.y + " max.x: " + max.x + "max.y: " + max.y + "Origin: " + new PVector(min.x, max.y));
  }
  public float getWidth() {
    return w;
  }
  public float getHeight() {
    return h;
  }
  public void setHeight(float height) {
    h= height;
  }
  public void setWidth(float width) {
    w = width;
  }
  public PVector getPos() {
    return pos;
  }
}

/* =========================================================
 * ====                   WARNING                        ===
 * =========================================================
 * The code in this tab has been generated from the GUI form
 * designer and care should be taken when editing this file.
 * Only add/edit code inside the event handlers i.e. only
 * use lines between the matching comment tags. e.g.
 
 void myBtnEvents(GButton button) { //_CODE_:button1:12356:
 // It is safe to enter your event code here  
 } //_CODE_:button1:12356:
 
 * Do not rename this tab!
 * =========================================================
 */

public void textfield1_change1(GTextField source, GEvent event) { //_CODE_:teachMeTF:397121:
  //println("teachMeTF - GTextField event occured " + System.currentTimeMillis()%10000000 );
  if (event == event.GETS_FOCUS) {
    drawingMode = "teach"; 
    println("Drawing mode = "+ drawingMode); 
    myShape = new Shape();
  }
  if (event==event.DRAGGING)
  {
    println("dragging");
  }
} //_CODE_:teachMeTF:397121:

public void drawMeTF_change1(GTextField source, GEvent event) { //_CODE_:drawMeTF:943118:
  //println("drawMeTF - GTextField event occured " + System.currentTimeMillis()%10000000 );
} //_CODE_:drawMeTF:943118:

public void teachMeButton_click1(GButton source, GEvent event) { //_CODE_:teachMeButton:665691:
  if (teachMeTF.getText() == null) {
    println("I don't know what you taught me! Please type a label first and then draw object");
  }
  else {
    myShape.setID(teachMeTF.getText()); 
    //println("My shape ID = "+  myShape.getID()); 
    teachMeTF.setText(""); 
    allShapes.add(myShape); 
    drawingMode = "draw";
  }
} //_CODE_:teachMeButton:665691:

public void drawMeButton_click1(GButton source, GEvent event) { //_CODE_:drawMeButton:574185:
  println("Draw me clicked, label = " + drawMeTF.getText()); 
  if (drawMeTF.getText() == null) {
    println("Nothing to draw");
  }
  else {
    //create shape here
    //then modify pos, and add to stack, after click
    for (int i = 0; i < allShapes.size() ; i++) {
      //println("All Shapes: " + allShapes); 
      //println("Shape #: " + i + " ID: " + allShapes.get(i).getID()); 
      String ID = allShapes.get(i).getID(); 
      if (ID.equals(drawMeTF.getText())) {
        //print("in If");
        Shape s = allShapes.get(i); 
        targetShape = new Shape(); 
        targetShape = s; 
        //println("Points in the shape: "); 
         for(int j = 0; j < targetShape.allLines.size(); j++){
          Line l = targetShape.allLines.get(j);
          //l.printPoints(); 
        }
        strokeWeight(1);
        //targetShape.shiftHorizontal(50); 
        //println("All Lines size: " + s.allLines.size());
      }
      else println("I don't know the shape");
    }
    drawingMode = "drawPos";
  }
  drawMeTF.setText("");
} //_CODE_:drawMeButton:574185:



// Create all the GUI controls. 
// autogenerated do not edit
public void createGUI() {
  G4P.messagesEnabled(false);
  G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
  G4P.setCursor(ARROW);
  if (frame != null)
    frame.setTitle("Apprentice AI Drawing Partner");
  teachMeTF = new GTextField(this, 15, 10, 120, 30, G4P.SCROLLBARS_NONE);
  teachMeTF.setDefaultText("Teach me!");
  teachMeTF.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  teachMeTF.setOpaque(true);
  teachMeTF.addEventHandler(this, "textfield1_change1");
  drawMeTF = new GTextField(this, 569, 10, 120, 30, G4P.SCROLLBARS_NONE);
  drawMeTF.setDefaultText("[tree]");
  drawMeTF.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  drawMeTF.setOpaque(true);
  drawMeTF.addEventHandler(this, "drawMeTF_change1");
  teachMeButton = new GButton(this, 147, 10, 80, 30);
  teachMeButton.setText("Done!");
  teachMeButton.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  teachMeButton.addEventHandler(this, "teachMeButton_click1");
  drawMeButton = new GButton(this, 473, 10, 80, 30);
  drawMeButton.setText("Draw me a...");
  drawMeButton.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  drawMeButton.addEventHandler(this, "drawMeButton_click1");
}

// Variable declarations 
// autogenerated do not edit
GTextField teachMeTF; 
GTextField drawMeTF; 
GButton teachMeButton; 
GButton drawMeButton; 


