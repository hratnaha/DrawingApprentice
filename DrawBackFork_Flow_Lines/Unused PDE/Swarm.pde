/* Needs to be made into a drawing algorithm in Line_mod class
class Swarm{
  //filter(INVERT);

  float r;
  //redraw();
  stroke(0);
  rect(0, 0, width, height);
  colorMode(HSB, 1);
  
  public Swarm(){}
  for (int i = 0; i < Z.length; i++) {
    /*
    for(int j = 0; j < curLineGroup.getSize(); j++) {
     for(int k = 0; k < curLineGroup.getLine(j).getSize(); k++) {
     //for(int k = 0; k < 1; k++) {
     //println(i + " " + j + " " + k);
     Z[i].gravitate(new particle(curLineGroup.getLine(j).getPoint(k).x, curLineGroup.getLine(j).getPoint(k).y, 0, 0, 0.001 ) );
     //else {
     //Z[i].deteriorate();
     //}
     //if(sqrt(sq(Z[i].x - curLineGroup.centerLine.getPoint(curLineGroup.centerLine.getSize() - 1).x) + sq(Z[i].y - curLineGroup.centerLine.getPoint(curLineGroup.centerLine.getSize() - 1).y)) < 100)
     //Z[i].deteriorate();
     //else {
     Z[i].update();
     r = float(i)/Z.length;
     stroke(colour, pow(r,0.1), 1-r, 0.15 );
     Z[i].display();
     //}
     }
     }
    if ((currentAttractor[i] + 1) < curLineGroup.getCenterLine().getSize() && dist(Z[i].x, Z[i].y, curLineGroup.getCenterLine().getPoint(currentAttractor[i]).x, curLineGroup.getCenterLine().getPoint(currentAttractor[i]).y) < 90)
      currentAttractor[i]++;
    //println(i + " attracted by " + currentAttractor[i] + " in " + curLineGroup.getCenterLine().getSize());
    Z[i].gravitate(new particle(curLineGroup.getCenterLine().getPoint(currentAttractor[i]).x, curLineGroup.getCenterLine().getPoint(currentAttractor[i]).y, 0, 0, 0.1 ) );
    Z[i].update();
    r = float(i)/Z.length;
    stroke(colour, pow(r, 0.1), 1-r, 0.15 );
    Z[i].display();
  }
  colorMode(RGB, 255);
  colour+=random(0.01);
  if ( colour > 1 ) {
    colour = colour%1;
  }
  stroke(0, 0, 0);
  //filter(INVERT);
}

/*Code from the old main class. In order to use the swarm, it has to be integrated as a drawing algorithm and added to the list of drawing algorithms that are selected.

if (key == 'f') { // generate a FoodSeeker and let it run
   for (int i = 0; i < lineGroups.size(); i++)
   {
   //int size = lineGroups.get(i).getCenterLine().getSize();
   //PVector position = lineGroups.get(i).getCenterLine().getPoint(round(random(size - 1)));
   for (int j = 0; j < lineGroups.get(i).getSize(); j++) {
   int size = lineGroups.get(i).getLine(j).getSize();
   PVector position = lineGroups.get(i).getLine(j).getPoint(round(random(size - 1)));
   
   //println(position);
   
   //PVector initialVector = new PVector(lineGroups.get(i).getCenterLine().getPoint(1).x - lineGroups.get(i).getCenterLine().getPoint(0).x, lineGroups.get(i).getCenterLine().getPoint(1).y - lineGroups.get(i).getCenterLine().getPoint(0).y);
   FoodSeeker foodSeeker = new FoodSeeker(position, 1, random(-3.14, 3.14), 30, 0.3);
   //foodSeeker.render();
   foodSeekers.add(foodSeeker);
   }
   }
   
*/
