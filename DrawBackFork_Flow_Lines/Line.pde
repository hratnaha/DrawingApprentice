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
        println("allPoints.size()=" + allPoints.size()); 
        PVector p1 = allPoints.get(i -1); 
        PVector p2 = allPoints.get(i); 
        line(p1.x, p1.y, p2.x, p2.y);
      }
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
    myPoint = new PVector(x, y);
    endPoint = myPoint; 
    allPoints.add(endPoint);
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

