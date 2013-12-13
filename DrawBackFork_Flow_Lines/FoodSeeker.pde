class FoodSeeker {
  ArrayList <Line> lines;
  int[] food;
  PVector position;
  float velocity;
  float angle;
  int maxFood;
  int curFood;
  float freedom; // range from 0 to pi
  int bestPoint = -1;
  int bestLine = -1; // the line that the best point is on
  float bestGain = 0;
  int count = 0;
  int savedIndex = 0;
  boolean firstTime = true;
    
  FoodSeeker(PVector pos, float vel, float ang, int fd, float fr) {
    position = new PVector();
    position.x = pos.x;
    position.y = pos.y;
    velocity = vel;
    angle = ang;
    maxFood = fd;
    curFood = fd;
    freedom = fr;
  }
  boolean isOnLine(Line line) {
    for(int i = 0; i < line.getAllPoints().size(); i++) {
      if(dist(position.x, position.y, line.getPoint(i).x, line.getPoint(i).y) < 10)
        return true;
      else if(i < line.getAllPoints().size() - 1) {
        PVector d1 = new PVector(position.x - line.getPoint(i).x, position.y - line.getPoint(i).y);
        PVector d2 = new PVector(line.getPoint(i + 1).x - position.x, line.getPoint(i + 1).y - position.y);
        if(abs(d1.heading() - d2.heading()) < 1) {
          return true;
        }
      }
    }
    return false;
  }
  boolean isOnPoint(PVector point) {
    if(dist(position.x, position.y, point.x, point.y) < 10)
      return true;
    return false;
  }
  void refillFood(int index) {
    curFood = curFood + food[index];
    food[index] = 0;
    if(curFood > maxFood)
      curFood = maxFood;
  }
  void updateFood() {
    
  }
  boolean starve() {
    if(curFood == 0)
      return true;
    else
      return false;
  }
  PVector getVelocity() {
    PVector v = new PVector(velocity * cos(angle), velocity * sin(angle));
    return v;
  }
  void step() {
    auto();
    curFood--;
    //food will grow
    
    for(int i = 0; i < food.length; i++)
      if(food[i] < maxFood)
        food[i]++;
    
    /*
    if(curFood > maxFood / 2) {
      wander();
      curFood--;
    }
    else {
      seekFood();
      curFood--;
    }
    */
  }
  void auto() {
    if(firstTime) {
      findNextPoint();
      firstTime = false;
      //println("First Time");
    }
    println("bestPoint: " + bestPoint + " savedIndex: " + savedIndex);
    if(bestPoint != -1){
      PVector direction = new PVector(lines.get(bestLine).getPoint(bestPoint).x - position.x, lines.get(bestLine).getPoint(bestPoint).y - position.y);
      //angle = direction.heading();
      angle = direction.heading() + random(-freedom, freedom);
      //println("    Attracted by Line: " + bestLine + " Point: " + bestPoint + "( " + lines.get(bestLine).getPoint(bestPoint).x + ", " + lines.get(bestLine).getPoint(bestPoint).y + ") curFood: " + curFood + " current pos: (" + position.x + ", " + position.y + ") heading: " + angle +" velocity " + velocity);
      if(isOnPoint(lines.get(bestLine).getPoint(bestPoint)) && food[savedIndex] != 0) {
        refillFood(savedIndex);
        println("refill " + savedIndex);
        findNextPoint();
      }  
    }
    else
      wander();
  }
  void wander() {
    position.add(getVelocity());
    angle = angle + random(-freedom, freedom);
  }
  /*
  void seekFood() {
    position.add(getVelocity());
    float distance = 99999999;
    int nearestLine = -1;
    int nearest = -1;
    for(int i = 0; i < lines.size(); i++){
      for(int j = 0; j < lines.get(i).getAllPoints().size(); j++) {
        float temp = dist(position.x, position.y, lines.get(i).getPoint(j).x, lines.get(i).getPoint(j).y);
        if(temp < 30) {
          if(temp < distance){
            distance = temp;
            nearestLine = i;
            nearest = j;
          }
        }
      }
    }
    if(nearest != -1){
      PVector direction = new PVector(lines.get(nearestLine).getPoint(nearest).x - position.x, lines.get(nearestLine).getPoint(nearest).y - position.y);
      angle = direction.heading();
      //angle = direction.heading() + random(-freedom, freedom);
    }
    else
      wander();
  }
  */
  void render() {
    strokeWeight(5);
    stroke(0);
    PVector tmp = position;
    tmp.add(getVelocity());
    PVector[] tmp2 = {position, tmp};
    Line l = new Line(tmp2);
    l.draw();
    stroke(255,0,0);
    point(position.x, position.y);
    stroke(0);
    strokeWeight(1); 
  }
  void run(ArrayList <Line> l) {
    lines = l;
    int pointSize = 0;
    for(int i = 0; i < l.size(); i++)
      pointSize = pointSize + l.get(i).getSize();
    // saves previous food value
    int[] tmp = new int[pointSize];
    if(food != null) {
      for(int i = 0; i < food.length; i++)
        tmp[i] = food[i];
      for(int i = food.length; i < pointSize; i++)
        tmp[i] = maxFood; // for now
    }
    else
      for(int i = 0; i < pointSize; i++)
        tmp[i] = maxFood;
    food = tmp;
    if(starve() == false)
    {
      render();
      step();   
    }
  }
  void findNextPoint() {
    //auto judge and walk
    bestPoint = -1;
    bestLine = -1; // the line that the best point is on
    bestGain = 0;
    count = 0;
    savedIndex = 0;
    for(int i = 0; i < lines.size(); i++){
      for(int j = 0; j < lines.get(i).getAllPoints().size(); j++) {
        float distance = dist(position.x, position.y, lines.get(i).getPoint(j).x, lines.get(i).getPoint(j).y);
        if(distance < 30) {
          float gain = food[count + j] - distance;
          //println("Line: " + i + " Point: " + j + " (" + lines.get(i).getPoint(j).x + ", " + lines.get(i).getPoint(j).y + ") Gain: " + gain);
          if(gain > bestGain){
            bestGain = gain;
            bestLine = i;
            bestPoint = j;
            savedIndex = count + j;
            //println("betterLine: " + i + " betterPoint: " + j + " (" + lines.get(i).getPoint(j).x + ", " + lines.get(i).getPoint(j).y + ") Gain: " + gain);
          }
        }
      }
      count = count + lines.get(i).getSize();
    }
  }
  void printFood() {
    for(int i = 0; i < food.length; i++)
      println("Food " + i + " : " + food[i]);
  }
}
