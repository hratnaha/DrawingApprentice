class Shape {
  ArrayList<Line> allLines = new ArrayList<Line>(); 
  String ID = ""; 
  PVector pos; 
  public Shape() {
  }



  public void addLine(Line line) {
    this.allLines.add(line);
  }
  public void render() {
    //for (Line l: allLines) {
    //  l.draw();
    //}
  }
  public void setID(String ID) {
    this.ID = ID;
  }
  public String getID() {
    return ID;
  }
  public void shiftHorizontal(int shift) {
    for (int i = 0 ; i < allLines.size(); i++) {
      Line l = allLines.get(i); 
      for (int j = 0; j < l.allPoints.size(); j++) {
        PVector p = l.allPoints.get(j); 
        //println("Add stuff to points. X = " + p.x); 
        p.x = p.x + shift;
      }
    }
  }
}

