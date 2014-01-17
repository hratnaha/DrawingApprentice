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


void changeMode (String mode)
{
  //change the mode of the sketch 
  //this information is coming from the button
}

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

