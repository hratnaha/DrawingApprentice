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

  public Line()
  {
    startTime = millis();
  }
  
  public Line(float x, float y){
    myPoint = new Point(x, y);
    startPoint = myPoint;
    allPoints.add(startPoint);
    startTime = millis();
  }
  
  public Line(ArrayList<Point> points){
    allPoints = points;
    startPoint = points.get(0);
    endPoint = points.get(points.size() - 1);
  }
  
  public void addPoint(Point p)
  {//manually add a point to allPoints
    allPoints.add(p);
  }

  public void curEnd(float x, float y) 
  {

    myPoint = new Point(x, y);
    if (startPoint == null)
    {
      startPoint = myPoint;
    }
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
      allPoints.get(i).print(); 
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
  public void printLineID()
  {
    println(lineID);
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
        //println("allPoints.size()=" + allPoints.size()); 
        Point p1 = allPoints.get(i -1); 
        Point p2 = allPoints.get(i); 
        line(p1.x, p1.y, p2.x, p2.y);
      }
    }
  }

  public void generateFlowLines()
  {
    //array to control the distances, 
    //eventually needs to contain all the distances for the heatmapp including negatives..
    float lineSpacing = 10; 
    float m; 
    //create a new Line object to hold all the new points


    int numberOfLines = 2; //number of flow lines in each direction
    int increment = 100/numberOfLines;
    int curHue = 0;  

    for ( int i = 0; i < numberOfLines; i++)
    {
      //create multiple flow lines for each 
      Line newLineAbove = new Line(); 
      Line newLineBelow = new Line(); 
      //newLine.setColor(color); 
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
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y); 
          PVector p1 = new PVector(allPoints.get(j +1).x, allPoints.get(j+1).y);
          PVector v = new PVector((p1.x - p0.x), (p1.y - p0.y)); //vector between two points in question
          PVector normal = new PVector(-v.y, v.x); //normal vector to the drawn line (might need to change signs based on slope)
          normal.setMag(i*lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          Point newPoint = new Point(normalPoint.x, normalPoint.y); 
          //newPoint.draw(); 
          newLineBelow.addPoint(newPoint);
        }
      }
      
      //flow lines abolve the line
      for (int j = 0; j< allPoints.size(); j++)
      {//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2)
        {//special case for start and end of line
          //generate the normal point and add to the newLine
          //println("Current i = " + i); 
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y); 
          PVector p1 = new PVector(allPoints.get(j +1).x, allPoints.get(j+1).y);
          PVector v = new PVector((p1.x - p0.x), (p1.y - p0.y)); //vector between two points in question
          PVector normal = new PVector(v.y, -v.x); //normal vector to the drawn line (might need to change signs based on slope)
          normal.setMag(i*lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          Point newPoint = new Point(normalPoint.x, normalPoint.y); 
          //newPoint.draw(); 
          newLineAbove.addPoint(newPoint);
        }
      }
      newLineAbove.drawLine();
      newLineBelow.drawLine(); 
      colorMode(RGB); 
      stroke(0); 
    }
  }
}
