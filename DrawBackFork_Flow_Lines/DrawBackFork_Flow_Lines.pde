ArrayList <Line> allLines = new ArrayList<Line>(); 
Line curLine; 
ArrayList <Line> stack = new ArrayList<Line>(); //keep track of the drawBack stack
float probRandom; //the amount of time that the drawBack will interject randomness
float degreeRandom; //how much fluctuation is introduced in random interjections
int i; //iteration count for stack
boolean drawBack = false; 

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

void draw() 
{
  //could have a stack of lines that need to be processed
  checkStack();
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
  //printAllLines();
  if(drawBack)
    {
      drawBack(curLine);
    }
}

void keyPressed()
{
  if (key == 'f')
  {
    generateFlowLines(); 
  }
  
}

void generateFlowLines()
{
  //cycle through all lines to determine their flow lines
  for(int i = 0; i < allLines.size(); i++)
  {
    Line curLine = allLines.get(i); 
    curLine.generateFlowLines(); 
  }
}


// Random drawback function
void drawBack(Line line)
{
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

      Point newPoint = new Point(x, y); 
      newLine.allPoints.add(newPoint);
    }
    else 
      //just add the point to the point array
    newLine.allPoints.add(line.allPoints.get(i));
  }
  //render the line
  stack.add(newLine);
}



void changeMode (String mode)
{
  //change the mode of the sketch 
  //this information is coming from the button
}


//######## Random DrawBack Mode
void checkStack()
{
  if (stack.size() >= 1)
  {
    //println("i: " + i + "Size of allPoints: " + stack.get(0).allPoints.size()); 
    if (i < stack.get(0).allPoints.size())
    {
      //println("Size: " + stack.size() + "i: " + i); 
      //start the i at 0
      //look at the 
      float x1 = stack.get(0).allPoints.get(i).x;
      float y1 = stack.get(0).allPoints.get(i).y;

      float x2 = stack.get(0).allPoints.get(i + 1).x;
      float y2 = stack.get(0).allPoints.get(i + 1).y;

      //println("x1: " + x1 + "y1: " + y1 + "x2: " + x2 + "y2: " + y2); 
      line (x1, y1, x2, y2); 
      i++; 

      if (i == stack.get(0).allPoints.size() - 1)
      {
        println("Completed line response"); 
        stack.remove(0); 
        i = 0; //reset the counter
      }
    }
  }
}

