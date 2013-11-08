ArrayList <Line> allLines = new ArrayList<Line>(); 
Line curLine; 
ArrayList <Line> stack = new ArrayList<Line>(); //keep track of the drawBack stack
float probRandom; //the amount of time that the drawBack will interject randomness
float degreeRandom; //how much fluctuation is introduced in random interjections
int i; //iteration count for stack
Decision_Engine engine;

//vehice's extra instance variables
Vehicle v;
int counter;

void setup() 
{
  size(600, 600);
  background(255);
  fill(0); 
  strokeWeight(1); 
  smooth(); 
  i = 0;// initialize the count for the 
  frameRate(20); 
  probRandom = .15; 
  degreeRandom = 5;
  colorMode(HSB, 100, 100, 100);
}

void draw() //veh code copied
{
  //could have a stack of lines that need to be processed
  checkStack();
  //added to make vehicle work correctly
  colorMode(RGB);
  background(255,255,255);
  if(allLines.size() > 0){
   displayAllPrevLines();
 }
 if(curLine != null && curLine.getSize() > 1){
   curLine.draw();
 } 
 
 //this updates the vehicle's path:
 /*
 boolean nullFlag = false;
  if(v!= null){
    counter += 1;
    if(curLine.getSize() > counter){
      PVector target = new PVector(curLine.getPoint(counter).x, curLine.getPoint(counter).y);
       v.arrive(target);
    } else {
      PVector target = curLine.getEndPoint();
      v.arrive(target);
    }
    if(curLine.insideBufferZone(v.loc)){
      println("car now null");
      nullFlag = true;
      v = null;
      displayAllPrevLines();
    }
    if(!nullFlag){
      v.update();
     // v.display();
      if(counter%20 == 0){
       Line line = v.drawTrail(); 
       allLines.add(line);
      }
    }
  } */

}

//##### Event Handling
void mousePressed() 
{
  //line(pmouseX, pmouseY, mouseX, mouseY); 
  curLine = new Line(mouseX, mouseY); 
  allLines.add(curLine);
}
void mouseDragged() 
{
  line(pmouseX, pmouseY, mouseX, mouseY); 
  //check if the slope has not change by 90 degrees
  //if so set line end to previous point and begin new line with current point add previous line to stack
  curLine.curEnd(mouseX, mouseY);
}

void mouseReleased() 
{
  line(pmouseX, pmouseY, mouseX, mouseY); 
  curLine.setEnd(mouseX, mouseY); 
  engine = new Decision_Engine(curLine);
  Line compLine = engine.decision();
  //allLines.add(compLine);
  stack.add(compLine); //not working QQ
  //displayAllPrevLines();
  }

void keyPressed(){
  if(key == 'c'){
    clear();
  }
  if(key == 'v'){
    v = new Vehicle(curLine.getPoint(0).x, curLine.getPoint(0).y);
    counter = 0;
    //create a new veh at the current line's start
    //will need a better solution for creating the car
  }
}

void clear(){
    allLines = new ArrayList<Line>(); 
    background(100);
}

/*void generateFlowLines()
{
  //cycle through all lines to determine their flow lines
  for(int i = 0; i < allLines.size(); i++)
  {
    Line curLine = allLines.get(i); 
    curLine.generateFlowLines(); 
  }
}
*/

void changeMode (String mode)
{
  //change the mode of the sketch 
  //this information is coming from the button
}

//######## Random DrawBack Mode
void checkStack(){
  if (stack.size() >= 1){
    //println("i: " + i + "Size of allPoints: " + stack.get(0).allPoints.size()); 
    if (i < stack.get(0).allPoints.size()){
      //println("Size: " + stack.size() + "i: " + i); 
      //start the i at 0
      //look at the 
      float x1 = stack.get(0).allPoints.get(i).x;
      float y1 = stack.get(0).allPoints.get(i).y;

      float x2 = stack.get(0).allPoints.get(i + 1).x;
      float y2 = stack.get(0).allPoints.get(i + 1).y;

      //println("x1: " + x1 + "y1: " + y1 + "x2: " + x2 + "y2: " + y2); 
      //line (x1, y1, x2, y2); 
      PVector point1 = new PVector(x1,y1);
      PVector point2 = new PVector(x2,y2);
      Line stackLine = new Line(x1,y1);
      stackLine.addPoint(point2);
      allLines.add(stackLine);
      i++; 

      if (i == stack.get(0).allPoints.size() - 1){
        println("Completed line response"); 
        stack.remove(0); 
        i = 0; //reset the counter
      }
    }
  }
}

void displayAllPrevLines(){
  for(int i = 0; i < allLines.size(); i++){
    if(allLines.get(i).getSize() > 1){
      Line l = allLines.get(i);
      l.draw();
    }
  } 
}

