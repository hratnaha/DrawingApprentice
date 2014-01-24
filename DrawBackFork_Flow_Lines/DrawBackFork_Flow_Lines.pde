import g4p_controls.*;
ArrayList <Line> allLines = new ArrayList<Line>(); //keeps track of all human lines
ArrayList <Line> compLines = new ArrayList<Line>(); //keeps track of all comp lines
ArrayList<Shape> allShapes = new ArrayList<Shape>(); 
String drawingMode = "draw";  //draw, teach, drawPos
StringList strings = new StringList(); // strings for file output
float probRandom; //the amount of time that the drawBack will interject randomness
float degreeRandom; //how much fluctuation is introduced in random interjections
int i; //iteration count for stack
int lineSpeed = 25; 
PImage catIcon; 
PImage img;
boolean shapeDrag = false; 
boolean intClick = false; 
boolean lineGroup = false; 
boolean bufferChanged = false; 
Shape myShape; 
Shape targetShape; 
Rectangle shapeBound; 
Line curLine; 
Decision_Engine engine;
color computerColor = color(253, 52, 91); 
color humanColor = color(0); 
Buffer buffer; 


void setup() 
{
  buffer = new Buffer(); 
  catIcon = loadImage("cat.png"); 
  myShape = new Shape(); 
  size(700, 700, JAVA2D);
  createGUI();
  customGUI();
  background(255);
  noFill(); 
  strokeWeight(1); 
  smooth(); 
  probRandom = .15; 
  degreeRandom = 5;
}

void draw() 
{
  if (buffer.getImage() != null) {
    image(buffer.getImage(), 0, 0);
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
    text("Click & drag where you want me to draw.", 235, 10, 220, 50);
  }
  if (shapeDrag) {
    shapeBound.drawRect();
  }
}

//##### Event Handling
void mousePressed() 
{
  checkInterface(new PVector(mouseX, mouseY)); 
  //println("intClick = " + intClick +" Drawing Mode: " + drawingMode); 
  if (drawingMode=="teach" || drawingMode=="draw" && intClick != true) {
    curLine = new Line(mouseX, mouseY); 
    curLine.col = humanColor; 
    allLines.add(curLine);
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
    line(pmouseX, pmouseY, mouseX, mouseY); 
    curLine.curEnd(mouseX, mouseY);
  }
  if (drawingMode=="drawPos" && !shapeDrag) {
    shapeDrag = true;
  }
  if (shapeDrag) {
    shapeBound.setPos2(new PVector(mouseX, mouseY));
  }
}
void mouseReleased() 
{ 
  if (!intClick) {
    line(pmouseX, pmouseY, mouseX, mouseY); 
    if (drawingMode == "draw") {
      buffer.addToBuffer(curLine); 
      curLine.setEnd(mouseX, mouseY); 
      engine = new Decision_Engine(curLine);
      curLine = null;  
      Line l = engine.decision();
      l.col = computerColor; 
      println("computerColor: "+ l.col); 
      SimpleThread compDraw = new SimpleThread(25, l, buffer);
      compDraw.start();
      compLines.add(l); 
    }
    else if (drawingMode=="drawPos") {
      createShape(shapeBound.origin);
      drawingMode = "draw";
    }
    else if (drawingMode == "teach")
    {
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

void clear() {
  allLines = new ArrayList<Line>(); 
  background(255);
  //clear the buffer
}
void lineDetection() {
  save("db.jpg");
  PImage src = loadImage("db.jpg");
}

public void customGUI() {
}

public void createShape(PVector pos) {
  Shape t = targetShape.createInstance(pos, shapeBound.w, shapeBound.h); 
  SimpleThread s = new SimpleThread(25, t, buffer); 
  s.start();
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

static public void main(String args[]) {
  PApplet.main("DrawBackFork_Flow_Lines");
}

