class Vehicle{
  PVector loc;
  PVector vel;
  PVector acc;
  
  ArrayList<PVector> locations = new ArrayList<PVector>();
  
  float r;
  float maxspeed;
  float maxforce;
  
  Vehicle(float x, float y){
    acc = new PVector(0,0);
    vel = new PVector(0,0);
    loc = new PVector(x,y);
    r = 3.0;
    
    maxspeed = 4; //arbitrary number
    maxforce = 0.1; //same here
  }
  
  void applyForce(PVector force){
    acc.add(force);
  }
  
  void display(){
    float theta = vel.heading() + PI/2;
    fill(175);
    stroke(0);
    pushMatrix();
    translate(loc.x, loc.y);
    rotate(theta);
    beginShape();
    vertex(0, -r*2);
    vertex(-r,r*2);
    vertex(r,r*2);
    endShape(CLOSE);
    popMatrix();
  }
  
  void update(){
   // print(loc);
    
    PVector m = new PVector(loc.x,loc.y);
    locations.add(m);
    //print(locations);
    
    vel.add(acc);
    vel.limit(maxspeed);
    loc.add(vel);
    acc.mult(0);
  }
  
  
  void seek(PVector target){
    PVector desire = PVector.sub(target, loc);
    desire.normalize();
    desire.mult(maxspeed);
    
    PVector steer = PVector.sub(desire,vel);
    steer.limit(maxforce);
    
    applyForce(steer);
  }
  
  void arrive(PVector target){
    PVector desire = PVector.sub(target, loc);
    
    float d = desire.mag();
    desire.normalize();
    
    if(d < 100){
      float m = map(d,0,100,0,maxspeed);
      desire.mult(m);
    } else {
      desire.mult(maxspeed);
    }
    
    PVector steer = PVector.sub(desire,vel);
    steer.limit(maxforce);
    applyForce(steer);
  }
  
  public Line drawTrail(){
    Line line = new Line();
   // print("In draw Trail");
    if(locations.size() >= 20){
      int start = (int)random(0,locations.size()-21);
      for(int i = start; i < start+20; i++){
        PVector point = locations.get(i);
       // PVector point2 = locations.get(i+1);
        //line(point.x, point.y, point2.x, point2.y);
        line.addPoint(point);
      }
      //line.draw();
    }
    return line;
  }
  
  public PVector getLoc(){
    return loc;
  }
    
}
