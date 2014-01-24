class Line {
  PVector myPoint;
  PVector startPoint; 
  PVector curEnd; 
  PVector endPoint; 
  ArrayList<PVector> allPoints = new ArrayList<PVector>(); 
  float startTime; 
  float endTime; 
  boolean isSelected = false; 
  String lineID; 
  Rectangle myBoundingBox;
  color col; 

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
        //println("line.draw()");
        PVector p1 = allPoints.get(i); 
        PVector p2 = allPoints.get(i+1); 
        stroke(0); 
        //stroke(col); 
        line(p1.x, p1.y, p2.x, p2.y);
        //println("P1: " + p1 + " P2: " + p2); 
        stroke(0); 
      }
    }
  }
  /*
  public void makeBoundingBox() {
    println("Making Bounding Box. Alllines = " + allLines); 
    myBoundingBox = new Rectangle(allLines); 
    myBoundingBox.calculateBounds();
  }
  */
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
    //makeBoundingBox();
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
  public void setLineID(String lineID) {
    this.lineID = lineID;
  }
  public String getLineID() {
    return lineID;
  }
  public void printLineID() {
    println(lineID);
  }
}

