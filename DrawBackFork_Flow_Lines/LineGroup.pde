class LineGroup {
  Line centerLine;
  ArrayList<PVector> groupPoints = new ArrayList<PVector>();
  ArrayList<Line> groupLines = new ArrayList<Line>(); 
  PVector myPoint; 
  float startTime; 
  float endTime; 
  boolean isSelected = false; 
  float maxRange = 10; //arbitaryily set range to judge whether a point is in group
  int lineGroupID;
  BezierFit bezierFit;
  
  public LineGroup() {
     //startTime = millis();
     bezierFit = new BezierFit();
  }
  
  public LineGroup(Line line){
    groupLines.add(line);
    computeCenterLine();
    //drawCenterLine();
    //startTime = millis();
  }

  public void addLine(Line line)
  {//manually add a point to groupPoints
    groupLines.add(line);
    computeCenterLine();
    //drawCenterLine();
  }

  public boolean inGroup(Line curLine) 
  {
    boolean in = false;
    float num_centerLine_overlap = 0;
    float num_curLine_overlap = 0;
    int[] centerLine_overlap = new int[centerLine.allPoints.size()];
    int[] curLine_overlap = new int[curLine.allPoints.size()];
    float[] curLine_distance = new float[curLine.allPoints.size()];
    float centerLine_distance = 100000;
    boolean at_tail = false;
    boolean at_head = false;
    float curAvg = 0;
    float curStd = -1; // serve as an indicator
    float centerAvg = 0;
    float centerStd = 0;
    int curLine_overlap_start = -1;
    int curLine_overlap_end = -1;
    int centerLine_overlap_start = -1;
    int centerLine_overlap_end = -1;
    
    for(int j = 0; j < curLine.allPoints.size(); j++)
    {
      curLine_overlap[j] = -1;
      curLine_distance[j] = 100000;
    }
      
    for(int i = 0; i < centerLine.allPoints.size(); i++)
    {
      centerLine_distance = 100000;
      centerLine_overlap[i] = -1;
      for(int j = 0; j < curLine.allPoints.size(); j++)
      {
        float range = sqrt(sq(centerLine.getPoint(i).x - curLine.getPoint(j).x) + sq(centerLine.getPoint(i).y - curLine.getPoint(j).y)); 
        if(range < 100)
        {
          if(centerLine_distance > range){
            centerLine_distance = range;
            centerLine_overlap[i] = j;
          }
          if(curLine_distance[j] > range){
            curLine_distance[j] = range;
            curLine_overlap[j] = i;
          }
        }
      }
    }
    for(int i = 0; i < centerLine.allPoints.size(); i++)
    {
      //println("Center  " + centerLine_overlap[i]);
      if(centerLine_overlap[i] >= 0)
      {
        num_centerLine_overlap ++;
        if(centerLine_overlap_start == -1)
          centerLine_overlap_start = i;
        centerLine_overlap_end = i;
      }
    }
    for(int i = 0; i < curLine.allPoints.size(); i++)
    {
      //println("Cur  " + curLine_overlap[i]);
      if(curLine_overlap[i] >= 0)
      {
        num_curLine_overlap ++;
        if(curLine_overlap_start == -1)
          curLine_overlap_start = i;
        curLine_overlap_end = i;
      }
    }
    //println(num_centerLine_overlap + " " + centerLine.groupPoints.size());
    //println(num_curLine_overlap + " " + curLine.groupPoints.size());
    
    //Overlapping over 80% of either line is considered in one group
    if(num_centerLine_overlap / float(centerLine.allPoints.size()) > 0.8) { 
      in = true;
      println("before");
      curLine.printPoints();
      PVector[] tmp = new PVector[curLine.getSize()];
      curLine.getAllPoints().toArray(tmp);
      groupPoints = curLine.getAllPoints();
      if(centerLine_overlap[centerLine_overlap_start] > centerLine_overlap[centerLine_overlap_end]) {
        for(int i = centerLine_overlap_start; i <= centerLine_overlap_end; i ++)
          groupPoints.add(centerLine_overlap[i], centerLine.getPoint(i));
      }
      else {
        for(int i = centerLine_overlap_start; i <= centerLine_overlap_end; i ++)
          groupPoints.add(centerLine_overlap[i] + i - centerLine_overlap_start, centerLine.getPoint(i));
      } 
      curLine.allPoints = new ArrayList<PVector>();
      for(int i = 0; i < tmp.length; i++)
        curLine.allPoints.add(tmp[i]);
      println("after");
      curLine.printPoints();
    }
    else if(num_curLine_overlap / float(curLine.allPoints.size()) > 0.8) {
      in = true;
      groupPoints = centerLine.getAllPoints();
      if(curLine_overlap[curLine_overlap_start] > curLine_overlap[curLine_overlap_end]) {
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i], curLine.getPoint(i));
      }
      else {
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i] + i - curLine_overlap_start, curLine.getPoint(i));
      }  
    }
    //If ends of two lines parallel, which will result in 4 cases. Means and standard deviations computed from line segments are compared.
    else if(centerLine_overlap[centerLine.allPoints.size() - 1] >= 0 && curLine_overlap[0] >= 0)
    {
      ArrayList<Float> tmp = new ArrayList<Float>();
      for(int i = centerLine.allPoints.size() - 1; centerLine_overlap[i] >= 0; i--)
        tmp.add(new PVector(centerLine.getPoint(i).y - centerLine.getPoint(i - 1).y, centerLine.getPoint(i).x - centerLine.getPoint(i - 1).x).heading());
      centerAvg = average(tmp);
      centerStd = std(tmp);
      tmp = new ArrayList<Float>();
      for(int i = 0; curLine_overlap[i] >= 0; i++)
        tmp.add(new PVector(curLine.getPoint(i + 1).y - curLine.getPoint(i).y, curLine.getPoint(i + 1).x - curLine.getPoint(i).x).heading());
      curAvg = average(tmp);
      curStd = std(tmp);
      println(centerAvg + " " + centerStd);
      println(curAvg + " " + curStd);
      if(curStd > 0 && abs(centerAvg - curAvg) < 1 && abs(centerStd - curStd) < 1)
      {
        in = true;
        groupPoints = centerLine.getAllPoints();
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i] + i - curLine_overlap_start, curLine.getPoint(i));
        for(int i = curLine_overlap_end + 1; i < curLine.getSize(); i ++)
          groupPoints.add(groupPoints.size(), curLine.getPoint(i));
      }
    }
    else if(centerLine_overlap[0] >= 0 && curLine_overlap[curLine.allPoints.size() - 1] >= 0)
    {
      ArrayList<Float> tmp = new ArrayList<Float>();
      for(int i = curLine.allPoints.size() - 1; curLine_overlap[i] >= 0; i--)
        tmp.add(new PVector(curLine.getPoint(i).y - curLine.getPoint(i - 1).y, curLine.getPoint(i).x - curLine.getPoint(i - 1).x).heading());
      curAvg = average(tmp);
      curStd = std(tmp);
      tmp = new ArrayList<Float>();
      for(int i = 0; centerLine_overlap[i] >= 0; i++)
        tmp.add(new PVector(centerLine.getPoint(i + 1).y - centerLine.getPoint(i).y, centerLine.getPoint(i + 1).x - centerLine.getPoint(i).x).heading());
      centerAvg = average(tmp);
      centerStd = std(tmp);
      println(centerAvg + " " + centerStd);
      println(curAvg + " " + curStd);
      if(curStd > 0 && abs(centerAvg - curAvg) < 1 && abs(centerStd - curStd) < 1)
      {
        in = true;
        groupPoints = curLine.getAllPoints();
        for(int i = centerLine_overlap_start; i <= centerLine_overlap_end; i ++)
          groupPoints.add(centerLine_overlap[i] + i - centerLine_overlap_start, centerLine.getPoint(i));
        for(int i = centerLine_overlap_end + 1; i < centerLine.getSize(); i ++)
          groupPoints.add(groupPoints.size(), centerLine.getPoint(i));
      }
    }
    else if(centerLine_overlap[centerLine.allPoints.size() - 1] >= 0 && curLine_overlap[curLine.allPoints.size() - 1] >= 0)
    {
      ArrayList<Float> tmp = new ArrayList<Float>();
      for(int i = curLine.allPoints.size() - 1; curLine_overlap[i] >= 0; i--)
        tmp.add(new PVector(curLine.getPoint(i).y - curLine.getPoint(i - 1).y, curLine.getPoint(i).x - curLine.getPoint(i - 1).x).heading());
      curAvg = average(tmp);
      curStd = std(tmp);
      tmp = new ArrayList<Float>();
      for(int i = centerLine.allPoints.size() - 1; centerLine_overlap[i] >= 0; i--)
        tmp.add(new PVector(centerLine.getPoint(i - 1).y - centerLine.getPoint(i).y, centerLine.getPoint(i - 1).x - centerLine.getPoint(i).x).heading());
      centerAvg = average(tmp);
      centerStd = std(tmp);
      println(centerAvg + " " + centerStd);
      println(curAvg + " " + curStd);
      if(curStd > 0 && abs(centerAvg - curAvg) < 1 && abs(centerStd - curStd) < 1)
      {
        in = true;
        groupPoints = centerLine.getAllPoints();
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i], curLine.getPoint(i));
        for(int i = curLine_overlap_start - 1; i >= 0; i --)
          groupPoints.add(groupPoints.size(), curLine.getPoint(i));
      }
    }
    else if(centerLine_overlap[0] >= 0 && curLine_overlap[0] >= 0)
    {
      ArrayList<Float> tmp = new ArrayList<Float>();
      for(int i = 0; curLine_overlap[i] >= 0; i++)
        tmp.add(new PVector(curLine.getPoint(i).y - curLine.getPoint(i + 1).y, curLine.getPoint(i).x - curLine.getPoint(i + 1).x).heading());
      curAvg = average(tmp);
      curStd = std(tmp);
      tmp = new ArrayList<Float>();
      for(int i = 0; centerLine_overlap[i] >= 0; i++)
        tmp.add(new PVector(centerLine.getPoint(i + 1).y - centerLine.getPoint(i).y, centerLine.getPoint(i + 1).x - centerLine.getPoint(i).x).heading());
      centerAvg = average(tmp);
      centerStd = std(tmp);
      println(centerAvg + " " + centerStd);
      println(curAvg + " " + curStd);
      if(curStd > 0 && abs(centerAvg - curAvg) < 1 && abs(centerStd - curStd) < 1)
      {
        in = true;
        groupPoints = centerLine.getAllPoints();
        for(int i = curLine_overlap_start; i <= curLine_overlap_end; i ++)
          groupPoints.add(curLine_overlap[i], curLine.getPoint(i));
        for(int i = curLine_overlap_end + 1; i < curLine.getSize(); i ++)
          groupPoints.add(0, curLine.getPoint(i));
      }
    }
    
    return in; 
  }

  public void printLines() 
  {
    for (int i = 0; i < groupLines.size(); i++) {
      //println("i = " + i); 
      getLine(i).printLineID(); 
    }
  }

  public float getTotalDistance() 
  {
    float totalDistance = 0; 
    //println("In getTotalDistance()"); 
    for (int i = 0; i < groupLines.size(); i++) {
      totalDistance += groupLines.get(i).getTotalDistance();
    }
    return totalDistance;
  }


  public float getTotalTime() 
  {
    //println("In getTotalTime()" ); 
    float totalTime = endTime - startTime; 
    return totalTime;
  }

  public float getAverageVelocity() 
  {
    //println("In averageVelocity"); 
    float averageVelocity = getTotalDistance()/getTotalTime(); 
    //in pixels/millis
    return averageVelocity;
  }

  public ArrayList<Line> getGroupLines() 
  {
    return groupLines;
  }
  public Line getLine(int i) 
  {
    return groupLines.get(i);
  }
  public int getSize() 
  {
    return groupLines.size();
  }
  public void setLineGroupID(int lineGroupID) 
  {
    this.lineGroupID = lineGroupID;
  }
  public float getLineGroupID() 
  {
    return lineGroupID;
  }
  public void printLineGroupID()
  {
    println("Group " + lineGroupID);
  }
  public void computeCenterLine() 
  {
    //centerLine = getLine(getSize() - 1); // mock up
    
    //for(int i = 0; i < getSize(); i++)
    if(centerLine == null)
      groupPoints.addAll(getLine(getSize() - 1).getAllPoints());
    ArrayList<PVector> controlPoints = bezierFit.fit(groupPoints);
    //bezier(controlPoints.get(0).x, controlPoints.get(0).y, controlPoints.get(1).x, controlPoints.get(1).y, controlPoints.get(2).x, controlPoints.get(2).y, controlPoints.get(3).x, controlPoints.get(3).y);
    int steps = 20;
    ArrayList<PVector> points = new ArrayList<PVector>();
    for (int i = 0; i <= steps; i++) {
      float t = i / float(steps);
      float x = bezierPoint(controlPoints.get(0).x, controlPoints.get(1).x, controlPoints.get(2).x, controlPoints.get(3).x, t);
      float y = bezierPoint(controlPoints.get(0).y, controlPoints.get(1).y, controlPoints.get(2).y, controlPoints.get(3).y, t);
      points.add(new PVector(x, y));
      //ellipse(x, y, 5, 5);
    }
    centerLine = new Line(points);
  }
  public void drawCenterLine()
  {
    stroke(0, 250, 150);
    centerLine.draw();
    stroke(0, 0, 0);
  }
  public Line getCenterLine()
  {
    return centerLine;
  }
  public void clear()
  {
    groupLines = new ArrayList<Line>(); 
  }
  public float average(ArrayList<Float> numbers)
  {
    float total = 0;
    for (int i = 0; i < numbers.size(); i++) 
     total += numbers.get(i);
    return total / numbers.size(); 
  }
  public float std(ArrayList<Float> numbers)
  {
    float total = 0;
    for (int i = 0; i < numbers.size(); i++) 
     total += numbers.get(i);
    float average = total / numbers.size(); 
    float totalSquares = 0;
    for (int i = 0; i < numbers.size(); i++) 
      totalSquares += (numbers.get(i) - average)*(numbers.get(i) - average);
    float averageSquare = totalSquares / numbers.size();
    return sqrt(averageSquare);
  } 
}
