class Buffer {
  ArrayList<Line> allLines = new ArrayList<Line>(); 
  PImage img;
  PGraphics buffer; 
  boolean diff = false; 
  boolean transparent = false; 

  public Buffer() {
    buffer = createGraphics(700, 700, JAVA2D);
  }
/* Need to integrate this for color. Keep a record of all the lines independent from the segments that have been printed. 
  public void update() {
    buffer.beginDraw();
    if (!transparent) 
      buffer.background(255);
    if (transparent)
      buffer.background(0, 0); 
    buffer.smooth();
    buffer.noFill();
    println("Preparing to update the buffer");
    for (int i = 0; i < allLines.size(); i++) {
      println("In all lines, size is: " + allLines.size());
      Line l = allLines.get(i); 
      //l.printPoints();
      for (int j = 0; j < l.allPoints.size() - 1; j++) 
      {
        //println("Less than allPoints.size()"); 
        //println("j = " + j); 
        PVector p1 = l.allPoints.get(j); 
        PVector p2 = l.allPoints.get(j+1); 
        //println("Color: " + l.col);
        stroke(0);
        buffer.line(p1.x, p1.y, p2.x, p2.y);
        println("Drawing lines to buffer. p1.x = " + p1.x + " p1y = " + p1.y + " p2x =" + p2.x + " p2y = " + p2.y);
        stroke(0);
      }
    }
    buffer.endDraw();
    img = buffer.get(0, 0, buffer.width, buffer.height);
    diff=true;
  }
  */

  public void addToBuffer(Line l) {
    allLines.add(l); 
    //update();
    diff = true;
  }

  public void addSegment(LineSegment l) {
    buffer.beginDraw();
    buffer.background(255); 
    buffer.strokeWeight(1); 
    buffer.stroke(0); 
    buffer.smooth(); 
    if (img != null)
      buffer.image(img, 0, 0); 
    stroke(0);
    buffer.line(l.start.x, l.start.y, l.end.x, l.end.y); 
    img = buffer.get(0, 0, buffer.width, buffer.height);
    buffer.endDraw(); 
  }

  public PImage getImage() {
    return img;
  }

  public void clear() {
    allLines = new ArrayList<Line>(); 
    img = new PImage(); 
    //update();
  }
}

