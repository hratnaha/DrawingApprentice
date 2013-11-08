class Line_Mod{
  Line line;
  ArrayList<PVector> allPoints;
  
  public Line_Mod(Line line){
    this.line = line; //I don't know what am I doing ahhhhh
    this.allPoints = line.getAllPoints();
  }
  
   public Line translation(){
    float lineSpacing = 80;
    Line translatedLine = new Line();
    for (int j = 0; j< allPoints.size(); j++){//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2){//special case for start and end of line
          //generate the normal point and add to the newLine
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y); 
          PVector normal = new PVector(-1, 1); //normal vector to the drawn line (might need to change signs based on slope)
          normal.setMag(lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          PVector newPoint = new PVector(normalPoint.x, normalPoint.y); 
          translatedLine.addPoint(newPoint);
        }
      }
    //translatedLine.drawLine();
    return translatedLine;
  }
  
  public Line reflection(){ //reflects along y = -x
    float lineSpacing = 20;
    Line reflectionLine = new Line();
      
    for (int j = 0; j< allPoints.size(); j++){//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2){//special case for start and end of line
          //generate the normal point and add to the newLine
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y); 
          PVector normal = new PVector(-1,1); //the reflection axis
          normal.setMag(lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          PVector newPoint = new PVector(normalPoint.y, normalPoint.x); 
          reflectionLine.addPoint(newPoint);
        }
      }
   //reflectionLine.drawLine();
   return reflectionLine;
  }
  
  public Line scaling(){ //scales it, just doesn't put the line where I'd like it to.
    float lineSpacing = 80;
    Line scaledLine = new Line();
    
    for (int j = 0; j< allPoints.size(); j++){//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2){//special case for start and end of line
          //generate the normal point and add to the newLine
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y); 
          PVector normal = new PVector(1,-1); //line to get a translation point from
          normal.setMag(lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          PVector newPoint = new PVector(normalPoint.x/2+100, normalPoint.y/2+100); 
          //hard-coded the +100 in an attempt to get the scaled line closer to the origional
          //needs actual fixing.
          scaledLine.addPoint(newPoint);
        }
      }
  // scaledLine.drawLine();
     return scaledLine;
  }
  
  public Line rotation(){ //requires some maths
    float lineSpacing = 20;
    Line rotatedLine = new Line();
    
    for (int j = 0; j< allPoints.size(); j++){//cycle through all points of line, exclude beginning and end point
        if (j < allPoints.size() - 2){//special case for start and end of line
          //generate the normal point and add to the newLine
          PVector p0 = new PVector(allPoints.get(j).x, allPoints.get(j).y);
          PVector p1 = new PVector(allPoints.get(allPoints.size()/2).x, allPoints.get(allPoints.size()/2).y);//rotation axis
          
          //math to rotate a point:
          float m = p0.mag() - p1.mag(); //distance between two points
          //float z = acos(abs(
          
          PVector normal = new PVector(1,-1); //line to get a translation point from
          normal.setMag(lineSpacing); 
          PVector normalPoint = PVector.add(p0, normal); //determine the location of new point
          PVector newPoint = new PVector(normalPoint.y, normalPoint.x); 
          rotatedLine.addPoint(newPoint);
        }
      }
   //rotatedLine.drawLine();
   return rotatedLine;
  } 

  public Line drawBack(Line line){
      //take the previous line and cycle through its components and add some noise to it
      //here, I will just add a 1 * random 0-1 + point to each point, then draw
      //create a new Line of the current line, then newLine.drawLine(); 
      Line newLine = new Line(line.startPoint.x, line.startPoint.y); 
      for (int i = 0; i < line.allPoints.size(); i++)
      {
        //cycle through the points and add in a bit of randomness to each points
    
        //first decide if we should interfere with this point, give it a P of .5 for interfering
        if (random(0, 1) > (1 - probRandom))
        {
          float x = line.allPoints.get(i).x; 
          float y = line.allPoints.get(i).y; 
    
          x = x + random(-degreeRandom, degreeRandom); 
          y = y + random(-degreeRandom, degreeRandom); 
    
          PVector newPoint = new PVector(x, y); 
          newLine.allPoints.add(newPoint);
        }
        else 
          //just add the point to the point array
        newLine.allPoints.add(line.allPoints.get(i));
      }
      //render the line
      //stack.add(newLine);
      return newLine;
  }
    
    //put a vehicle thing here?
  
public Line vehicleDraw(Line line){
  Vehicle v = new Vehicle (line.startPoint.x, line.startPoint.y);
  print("in vehicleDraw");
  Line newline = new Line(0,0);
  boolean nullFlag = false;
  while(!nullFlag){
  if(v!= null){
    counter += 1;
    if(line.getSize() > counter){
      PVector target = new PVector(line.getPoint(counter).x, line.getPoint(counter).y);
       v.arrive(target);
       print("following line");
    } else {
      PVector target = line.getEndPoint();
      v.arrive(target);
      print("reaching end");
    }
    if(curLine.insideBufferZone(v.loc)){
      newline = v.drawTrail();
      //println("car now null");
      nullFlag = true;
      v = null;
      print("car now null");
    }
    if(!nullFlag){
      v.update();
      }
    }
  }
      //displayAllPrevLines();
     // Line newline = v.drawTrail();
      print("at return");
      return newline;
} 
      
  }
  
