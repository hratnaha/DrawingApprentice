class Decision_Engine {
  Line line;
  public Decision_Engine(Line line) { 
    this.line = line;
  }
  public Line decision() {
    int decision = (int)random(1, 5);
    Line_Mod m = new Line_Mod(this.line); 
    Line newLine = new Line(); 
    switch(decision) {
    case 1: 
      newLine = m.translation();
      break;
    case 2: 
      newLine = m.reflection();
      break; 
    case 3: 
      newLine = m.scaling();
      break;
    case 4: 
      newLine = m.drawBack(this.line);
      break; 
    }
    return newLine; 
  }
  public void setLine(Line line) {
    this.line = line;
  }
}

