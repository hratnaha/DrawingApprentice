class Decision_Engine {
 Line line;
 //int rotate = 0;
 int translate = 1;
 int reflection = 2;
 int scaling = 3;
 int drawback = 4;
 int veh = 5;
 //int decide;

 public Decision_Engine(Line line){ 
   //constructor yeeeeaaaaahhhhhhh is this even needed
   this.line = line;
   //I evidently forgot how to program with objects. I feel bad.
 }
  
  public Line decision(){
    int decision = (int)random(1,5);
    if(decision == translate){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.translation();
      //newLine.draw();
      return newLine;
     
    } else if(decision == reflection){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.reflection();
      //newLine.draw();
      return newLine;
      
    } else if(decision == scaling){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.scaling();
      //newLine.draw();
      return newLine;
      
    } else if(decision == drawback){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.drawBack(this.line);
      //newLine.draw();
      return newLine;
      
    } else if(decision == veh){
      Line_Mod m = new Line_Mod(this.line);
      Line newLine = m.vehicleDraw(this.line);
      //Line newLine  = new Line(0,0);
      return newLine;
      
    } else {
      Line newLine  = new Line(0,0);
      return newLine;
    }
  }
  
  public void setLine(Line line){
    this.line = line;
  }
}
