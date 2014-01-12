class Shape {
  ArrayList<Line> allLines = new ArrayList<Line>(); 
  String ID = ""; 
  PVector pos = new PVector(0, 0); 
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

  public void setPos(PVector pos) {
    Line first = allLines.get(0); 
    this.pos = first.allPoints.get(0); 
    PVector p = this.pos; 
    PVector pt1 = new PVector(p.x,p.y);
    PVector pt2 = pos;
    PVector diff = PVector.sub(pt1,pt2);
    println("P: "+p+ " Pt1: "+pt1+" Pt2: " + pt2 + " Diff: " + diff);

    //PVector diff = this.pos; 
    for (int i=0; i < allLines.size(); i++) {
      Line line1 = allLines.get(i); 
        for (int j=0; j < line1.allPoints.size(); j++) {
          PVector pt = line1.allPoints.get(j); 
          pt.sub(diff);
        }
    }
    first = allLines.get(0); 
    this.pos = first.allPoints.get(0); 
  }
}

