class LineSegment{
  PVector start; 
  PVector end; 
  public LineSegment(PVector s, PVector e){
    start = s; 
    end = e; 
  }
  public void printPoints(){
    println("Start: " + start + " End: " + end); 
  }
  public void render(){
    line(start.x, start.y, end.x, end.y); 
  }
}
