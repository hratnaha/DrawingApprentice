class Decision_Engine {
 Line line;
 //int rotate = 0;
 int translate = 1;
 int reflection = 2;
 int scaling = 3;
 int drawback = 4;
 //int decide;
 
 
 public Decision_Engine(Line line){ 
   //constructor yeeeeaaaaahhhhhhh is this even needed
   this.line = line;
   
   //I evidently forgot how to program with objects. I feel bad.
 
 }
  
  public void decision(){
    int decision = (int)random(1,5);
    println(decision);
    if(decision == translate){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.translation();
      newLine.drawLine();
    } else if(decision == reflection){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.reflection();
      newLine.drawLine();
    } else if(decision == scaling){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.scaling();
      newLine.drawLine();
    } else if(decision == drawback){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.drawBack(this.line);
      newLine.drawLine();
    }
    
    
  }
  
  public void setLine(Line line){
    this.line = line;
  }
  
}
