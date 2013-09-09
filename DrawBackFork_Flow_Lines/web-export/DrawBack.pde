ArrayList <Line> allLines = new ArrayList<Line>(); 
Line curLine; 
ArrayList <Line> stack = new ArrayList<Line>(); //keep track of the drawBack stack
float probRandom; //the amount of time that the drawBack will interject randomness
float degreeRandom; //how much fluctuation is introduced in random interjections
int i; //iteration count for stack

void setup() 
{
  size(600, 600);
  background(255);
  fill(0); 
  strokeWeight(1); 
  smooth(); 
  i = 0;// initialize the count for the 
  frameRate(20); 
  probRandom = .15; 
  degreeRandom = 5;
}

void draw() 
{
  //could have a stack of lines that need to be processed
  checkStack();
}


//##### Event Handling
void mousePressed() 
{
  //line(pmouseX, pmouseY, mouseX, mouseY); 
  curLine = new Line(mouseX, mouseY); 
  allLines.add(curLine);
}
void mouseDragged() 
{
  line(pmouseX, pmouseY, mouseX, mouseY); 
  //check if the slope has not change by 90 degrees
  //if so set line end to previous point and begin new line with current point add previous line to stack
  curLine.curEnd(mouseX, mouseY);
}
void mouseReleased() 
{
  line(pmouseX, pmouseY, mouseX, mouseY); 
  curLine.setEnd(mouseX, mouseY); 
  //printAllLines();
  drawBack(curLine);
}

// Random drawback function
void drawBack(Line line)
{
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

      Point newPoint = new Point(x, y); 
      newLine.allPoints.add(newPoint);
    }
    else 
      //just add the point to the point array
    newLine.allPoints.add(line.allPoints.get(i));
  }
  //render the line
  stack.add(newLine);
}



void changeMode (String mode)
{
  //change the mode of the sketch 
  //this information is coming from the button
}


//######## Random DrawBack Mode
void checkStack()
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
class Line {
  Point startPoint; 
  Point curEnd; 
  Point endPoint; 
  ArrayList<Point> allPoints = new ArrayList<Point>(); 
  Point myPoint; 
  float startTime; 
  float endTime; 
  boolean isSelected = false; 
  float lineID; 
  float currentSlope; 

  Rectangle myBoundingBox;

  float xmin = -1; 
  float ymin = -1; 
  float xmax = -1; 
  float ymax = -1; 

  float octave; 

  public Line(float x, float y)
  {
    myPoint = new Point(x, y);
    startPoint = myPoint;
    allPoints.add(startPoint);
    startTime = millis();
  }

  public void curEnd(float x, float y) 
  {
    myPoint = new Point(x, y);
    //myPoint.printPoint(); 
    allPoints.add(myPoint);
  }

  public void setEnd(float x, float y) 
  {
    myPoint = new Point(x, y);
    endPoint = myPoint; 
    allPoints.add(endPoint);
    endTime = millis();
    makeBoundingBox();
  }

  public void printPoints() 
  {
    for (int i = 0; i < allPoints.size(); i++) {
      //println("i = " + i); 
      allPoints.get(i).printPoint(); 
      //curPoint.printPoint();
    }
  }

  public float getTotalDistance() 
  {
    float totalDistance = 0; 
    //println("In getTotalDistance()"); 
    for (int i = 0; i < allPoints.size(); i++) {
      if (i < allPoints.size() - 1) {
        Point p1 = allPoints.get(i); 
        Point p2 = allPoints.get(i+1); 
        float currentDistance = sqrt((sq(p2.x - p1.x)) + sq((p2.y - p1.y))); 
        totalDistance += currentDistance;
      } 
      else if (i == allPoints.size() - 1) {
        Point p1 = allPoints.get(i -1); 
        Point p2 = allPoints.get(i); 
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

  public void makeBoundingBox() {
    //creat the bounding box after the end of the line
    for (int i = 0; i < allPoints.size(); i++) 
    {
      Point p1 = allPoints.get(i); 
      if ( xmin == -1 && ymin== -1 && xmax == -1 && ymax == -1) {
        xmin = p1.x; 
        xmax = p1.x; 
        ymin = p1.y; 
        ymax = p1.y;
      }
      else 
      {
        if (p1.x < xmin) 
        {
          xmin = p1.x;
        }
        else if (p1.x > xmax) 
        {
          xmax = p1.x;
        }
        if ( p1.y < ymin) 
        {
          ymin = p1.y;
        }
        else if ( p1.y > ymax) 
        {
          ymax = p1.y;
        }
      }
    }
    Point origin = new Point(xmin, ymin); 
    float recWidth = xmax - xmin; 
    float recHeight = ymax - ymin; 
    myBoundingBox = new Rectangle((int)origin.x, (int)origin.y, (int)recWidth, (int)recHeight);
  }

  public Rectangle getBoundingBox() 
  {
    return myBoundingBox;
  }


  public void calculateOctave() 
  {
    println("in calculate octave"); 
    float cp = ymax - (getRectHeight() / 2); //center point y value
    float oh = height / 4; //octave height

    if ( cp < oh*4 && cp > oh*3) 
    {
      println("in 4"); 
      octave = 1.0;
    }
    else if ( cp < oh*3 && cp > oh*2) 
    {
      println("in 3"); 
      octave = 2.0;
    }
    else if ( cp < oh*2 && cp > oh) 
    {
      println("in 2"); 
      octave = 3.0;
    }
    else if (cp<oh) 
    {
      println("in 1"); 
      octave = 4.0;
    }
  }

  public float getOctave() 
  {
    calculateOctave(); 
    println("Octave = " + octave); 
    return octave;
  }
  public void drawBoundingBox() 
  {
    myBoundingBox.drawRect();
  }
  public ArrayList<Point> getAllPoints() 
  {
    return allPoints;
  }
  public Point getPoint(int i) 
  {
    return allPoints.get(i);
  }
  public int getSize() 
  {
    return allPoints.size();
  }

  public float getMaxY() 
  {
    return ymax;
  }
  public float getMaxX() 
  {
    return xmax;
  }

  public int getRectHeight() 
  {
    return myBoundingBox.getHeight();
  }

  public void setLineID(float lineID) 
  {
    this.lineID = lineID;
  }
  public float getLineID() 
  {
    return lineID;
  }
  public void setCurrentSleop()
  {
    Point prevPoint = allPoints.get(allPoints.size() - 1); 
    currentSlope = (curEnd.y - prevPoint.y)/(curEnd.x - prevPoint.x);
  }
  public float getCurrentSlope()
  {
    return currentSlope;
  }
  //public boolean testSlope()
  //{
  //}


  public void drawLine() 
  {
    //println("In drawLine()"); 
    //println("Allpoints.size()" + allPoints.size()); 
    strokeWeight(.5); 
    for (int i = 0; i < allPoints.size(); i++) 
    {
      if (i < allPoints.size() - 1) 
      {
        //println("Less than allPoints.size()"); 
        Point p1 = allPoints.get(i); 
        Point p2 = allPoints.get(i+1); 
        line(p1.x, p1.y, p2.x, p2.y);
      } 
      else if (i == allPoints.size() - 1) 
      {
        //println("Equal to allPoints.size()"); 
        Point p1 = allPoints.get(i -1); 
        Point p2 = allPoints.get(i); 
        line(p1.x, p1.y, p2.x, p2.y);
      }
    }
  }
}

class Point{
  float x; 
  float y; 
  
  public Point (float x, float y){
    this.x = x; 
    this.y = y; 
  }
  public void printPoint(){
    println( "( "+ x + " , " + y + " )");
  }
    public void drawEllipseAtPoint(){
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

