class Line {
  PVector myPoint;
  PVector start; 
  PVector end; 
  ArrayList<PVector> allPoints = new ArrayList<PVector>(); 
  ArrayList<LineSegment> segments = new ArrayList<LineSegment>(); 
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
  public Line(ArrayList<PVector> all)
  {
    start = all.get(0);
    end = all.get(all.size() - 1);
    allPoints = all;
  }

  public Line(PVector[] all)
  {
    start = all[0];
    end = all[all.length - 1];
    for (int i = 0; i < all.length; i++)
      allPoints.add(all[i]);
  }
  
  public void addSegment(LineSegment l){
    segments.add(l); 
  }
  
  public void calculateSegments(){
    if(segments.size()==0){
      println("calc segs"); 
      for (int i = 0; i < allPoints.size() - 1; i++){
        LineSegment seg = new LineSegment(allPoints.get(i), allPoints.get(i+1)); 
        segments.add(seg); 
      }
    }
  }
    
  public void makeBoundingBox() {
    println("Making Bounding Box. Alllines = " + allLines); 
    myBoundingBox = new Rectangle(allLines); 
    myBoundingBox.calculateBounds();
  }
  
  public boolean insideBufferZone(PVector loc) {
    int radius = 10; //make a buffer zone raduis of 10 pixels
    float xDiff = loc.x - end.x;
    float yDiff = loc.y - end.y;
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
  
  public void setEnd(PVector p) 
  {
    end = p; 
    allPoints.add(end);
    endTime = millis();
    //makeBoundingBox();
  }

  public void setStart(PVector p){
    start = p; 
    allPoints.add(start); 
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
    return end;
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

