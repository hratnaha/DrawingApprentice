class Point{
  float x; 
  float y; 
  
  public Point (float x, float y){
    this.x = x; 
    this.y = y; 
  }
  public void print(){
    println( "( "+ x + " , " + y + " )");
  }
    public void draw(){
      fill(30, 50); 
      color(0,0,255);
    ellipse(x,y,3,3); 
    color(0,0,0);
    fill(0); 
}

public float getX(){
  return x; 
}
public float getY(){
  return y; 
}

public Point calcMidPoint (Point p)
{
  return new Point(this.x + p.x / 2, this.y + p.y / 2);
}

public float calcDistance(Point p)
{
  return sqrt(sq(p.x - this.x) + sq(p.y - this.y));
}

}
