class Rectangle{
  int x; 
  int y; 
  int recWidth; 
  int recHeight; 
  
  public Rectangle(int x, int y, int recWidth, int recHeight)
  {
    this.x = x; 
    this.y = y; 
    this.recWidth = recWidth; 
    this.recHeight = recHeight; 
  }
  
  public void drawRect()
  {
    noFill(); 
    stroke(255,0,0); 
    strokeWeight(2); 
    rect(x,y,recWidth,recHeight); 
    strokeWeight(3); 
  }
  public int getHeight()
  {
    return recHeight; 
  }
}
