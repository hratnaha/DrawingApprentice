class FlowField{
PVector[][] field;
int cols, rows, resolution;
ArrayList<Line> lineList;  //list of lines to base the flow field off of
//PVector[] allmins;  //array of all minimum points
float[] allmins;

FlowField(int r, ArrayList<Line> lineList){
  this.lineList = lineList;
  resolution = r;
  cols = 600/r;
  rows = 600/r;
  //allmins = new PVector[lineList.size()];
  allmins = new float[lineList.size()];
  field = new PVector[cols][rows];  //25x25 grid
  init();
}

void init(){
    float xoff = 0; //the x position of cells
    float yoff = 0; //the y position of cells
    float totaldistance;
    //ArrayList<PVector> allmins = new ArrayList<PVector>(); //all the minimum PVectors
    for (int i = 1; i <= cols; i++){
      for(int j = 1; j <= rows; j++){
        int counter =0;
        totaldistance=0;
        for(Line l: lineList){   //for each line in lineList, get all pvectors of the line and...
          ArrayList<PVector> allPoints = l.getAllPoints();
          xoff = resolution*i;
          yoff = resolution*j;
          HashMap<Float, PVector> mindist = new HashMap<Float, PVector>();
          for(PVector a : allPoints){
            mindist.put(sqrt(pow(a.x-xoff,2) + pow(a.y-yoff,2)), a);  //calculate minimum distance from cell to line and store it with the corresponding PVector
          }
          Object[] treesobject = mindist.keySet().toArray();  //calculates the minimum out of the whole list
          float[] treesfloat = new float[treesobject.length];
          for(int e = 0; e < treesfloat.length; e++)   //puts treesobject into treesfloat; had to do it this way because it wouldn't let me cast an Object[] to a float[]
            treesfloat[e] = (Float)treesobject[e];
          float[] absmin = getDistance(treesfloat);  //takes minimum value of treesfloat, uses the method getDistance() which returns float[] with minimum value and the second smallest value
          PVector min = mindist.get(absmin[0]);  //minimum value
          PVector min2 = mindist.get(absmin[1]);  //second smallest value
          float slope = (min2.y - min.y)/(min2.x - min.x);
          if(slope >= 10000) slope = 10000;
          else if(slope <= -10000) slope = -10000;
          else if(slope <= .00001 && slope >=0) slope = .00001;
          else if(slope >= -.00001 && slope <= 0) slope = -.00001;
          //println(slope);
          //allmins[counter] = PVector.sub(min, min2);
            //println(PVector.angleBetween(min, min2));
            //PVector.fromAngle(PVector.angleBetween(min, min2));
            //new PVector(sqrt(pow(min2.x-min.x,2)), sqrt(pow(min2.y-min.y,2)));
            //new PVector.sub(min, min2);
          //allmins[counter] = PVector.mult(allmins[counter], 1/absmin[0]);
          allmins[counter] = slope*(1/absmin[0]);
          //println(allmins[counter]);
          totaldistance += (1/(absmin[0]));
          counter++;
        }
        //PVector finalvalue = allmins[0];
        float finalvalue = allmins[0];
        //println(finalvalue);
        for(int m = 1; m < allmins.length; m++)
          //finalvalue = PVector.add(finalvalue, allmins[m]);
          finalvalue += allmins[m];
        //finalvalue = PVector.div(finalvalue, totaldistance);
        //finalvalue = PVector.div(finalvalue, allmins.length);
        finalvalue = (finalvalue/totaldistance)/allmins.length;
        //int finalval = Math.round(finalvalue);
            //finalvalue = PVector.fromAngle(PVector.angleBetween(finalvalue, allmins[m]));
        //println(finalvalue);
        PVector finalle = new PVector(1,finalvalue);
        finalle.normalize();
        field[i-1][j-1] = finalle;
      }
   }
}

  //takes a float[] and returns a float[] with minimum and second smallest value
  float[] getDistance(float[] list){
    float min = min(list);
    ArrayList<Float> minlist = new ArrayList<Float>();
    for(int i = 0; i<list.length; i++){
      if(list[i] == min)
        list[i] = Float.MAX_VALUE;
    }
    float[] minl = new float[2];
    minl[0] = min;
    minl[1] = min(list);
    //println("minimum distance value: "+ min);
    //println("2nd minimum distance value: "+ minl[1]);
    return minl;
  }
  

PVector lookup(PVector lookup){
  int col = int(constrain(lookup.x/resolution,0,cols-1));
  int row = int(constrain(lookup.y/resolution,0,rows-1));
  return field[col][row].get();
}

void display(){
  for(int i = 0; i < cols; i++){
    for(int j = 0; j < rows; j++)
        drawVector(field[i][j],i*resolution,j*resolution,resolution-2);
  }
}

  // Renders a vector object 'v' as an arrow and a location 'x,y'
  void drawVector(PVector v, float x, float y, float scayl) {
    pushMatrix(); //pushes current transformation matrix onto matrix stack aka puts it on top of other stuff
    float arrowsize = 4;
    // Translate to location to render vector
    translate(x,y);
    stroke(0,100);
    // Call vector heading function to get direction (note that pointing up is a heading of 0) and rotate
    float head = v.heading();
    //println("head " + head);
    if (head < 0)
    {
      translate(0, 18);
    }
    rotate(head);
    // Calculate length of vector & scale it to be bigger or smaller if necessary
    float len = v.mag()*scayl;
    
    line(0,0,len,0);
    popMatrix();
  }


}
