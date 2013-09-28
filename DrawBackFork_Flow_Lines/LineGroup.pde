class LineGroup {
  Line centerLine;
  ArrayList<Line> groupLines = new ArrayList<Line>(); 
  Point myPoint; 
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

  public boolean inGroup(float x, float y) 
  {
    boolean in = false;
    float minDistance = 100000;
    int nearestPoint;
    for(int i = 0; i < centerLine.allPoints.size(); i++)
    {
      float range = abs(centerLine.getPoint(i).x - x) + abs(centerLine.getPoint(i).y - y); 
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
    ArrayList<Point> linePoints = new ArrayList<Point>();
    for(int i = 0; i < getSize(); i++)
      linePoints.addAll(getLine(i).getAllPoints());
    ArrayList<Point> controlPoints = bezierFit.fit(linePoints);
    //bezier(controlPoints.get(0).x, controlPoints.get(0).y, controlPoints.get(1).x, controlPoints.get(1).y, controlPoints.get(2).x, controlPoints.get(2).y, controlPoints.get(3).x, controlPoints.get(3).y);
    int steps = 10;
    ArrayList<Point> points = new ArrayList<Point>();
    for (int i = 0; i <= steps; i++) {
      float t = i / float(steps);
      float x = bezierPoint(controlPoints.get(0).x, controlPoints.get(1).x, controlPoints.get(2).x, controlPoints.get(3).x, t);
      float y = bezierPoint(controlPoints.get(0).y, controlPoints.get(1).y, controlPoints.get(2).y, controlPoints.get(3).y, t);
      points.add(new Point(x, y));
      //ellipse(x, y, 5, 5);
    }
    centerLine = new Line(points);
  }
  public void drawCenterLine()
  {
    stroke(0, 250, 150);
    centerLine.drawLine();
    stroke(0, 0, 0);
  }
}
