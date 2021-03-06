/* This class doesn't need to be implemented right now. We are working on a diff line analysis mechanisms that might change how this works. 
class BezierFit {
  //ArrayList<Point> points;
  float[][] M = new float[][] {
    {
      -1, 3, -3, 1
    }
    , {
      3, -6, 3, 0
    }
    , {
      -3, 3, 0, 0
    }
    , {
      1, 0, 0, 0
    }
  }; 
  public BezierFit() {
  }
  /**
   * Computes the best bezier fit of the supplied points using a simple RSS minimization.
   * Returns a list of 4 points, the control points
   * @param points
   * @return
   
  public ArrayList<PVector> fit(ArrayList<PVector> points) {
    //Matrix M = M();
    float[][] Minv = new float[4][4];
    Minv = Mat.inverse(M);
    float[][] U = U(points);
    float[][] UT = UT(points);
    float[][] X = X(points);
    float[][] Y = Y(points);

    float[][] A = Mat.multiply(UT, U);
    float[][] B = Mat.inverse(A);
    float[][] C = Mat.multiply(Minv, B);
    float[][] D = Mat.multiply(C, UT);
    float[][] E = Mat.multiply(D, X);
    float[][] F = Mat.multiply(D, Y);

    ArrayList<PVector> P = new ArrayList<PVector>();
    for (int i = 0; i < 4; i++) {
      float x = E[i][0];
      float y = F[i][0];

      PVector p = new PVector(x, y);
      P.add(p);
    }

    return P;
  }

  private float[][] Y(ArrayList<PVector> points) {
    float[][] Y = new float[points.size()][1];

    for (int i = 0; i < points.size(); i++)
      Y[i][0] = points.get(i).y;

    return Y;
  }

  private float[][] X(ArrayList<PVector> points) {
    float[][] X = new float[points.size()][1];

    for (int i = 0; i < points.size(); i++)
      X[i][0] = points.get(i).x;

    return X;
  }

  private float[][] U(ArrayList<PVector> points) {
    float[] npls = normalizedPathLengths(points);

    float[][] U = new float[npls.length][4];
    for (int i = 0; i < npls.length; i++) {
      U[i][0] = pow(npls[i], 3);
      U[i][1] = pow(npls[i], 2);
      U[i][2] = pow(npls[i], 1);
      U[i][3] = pow(npls[i], 0);
    }

    return U;
  }

  private float[][] UT(ArrayList<PVector> points) {
    float[] npls = normalizedPathLengths(points);

    float[][] UT = new float[4][npls.length];
    for (int i = 0; i < npls.length; i++) {
      UT[0][i] = pow(npls[i], 3);
      UT[1][i] = pow(npls[i], 2);
      UT[2][i] = pow(npls[i], 1);
      UT[3][i] = pow(npls[i], 0);
    }

    return UT;
  }
  /**
   * Computes b(t).
   * @param t
   * @param v1
   * @param v2
   * @param v3
   * @param v4
   * @return
   *
  private PVector pointOnCurve(float t, PVector v1, PVector v2, PVector v3, PVector v4) {
    PVector p;

    float x1 = v1.x;
    float x2 = v2.x;
    float x3 = v3.x;
    float x4 = v4.x;

    float y1 = v1.y;
    float y2 = v2.y;
    float y3 = v3.y;
    float y4 = v4.y;

    float xt, yt;

    xt = x1 * pow((1-t), 3) 
      + 3 * x2 * t * pow((1-t), 2)
        + 3 * x3 * pow(t, 2) * (1-t)
          + x4 * pow(t, 3);

    yt = y1 * pow((1-t), 3) 
      + 3 * y2 * t * pow((1-t), 2)
        + 3 * y3 * pow(t, 2) * (1-t)
          + y4 * pow(t, 3);

    p = new PVector(xt, yt);

    return p;
  }

//Computes the percentage of path length at each point. Can directly be used as t-indices into the bezier curve. 
  private float[] normalizedPathLengths(ArrayList<PVector> points) {
    float pathLength[] = new float[points.size()];

    pathLength[0] = 0;

    for (int i = 1; i < points.size(); i++) {
      PVector p1 = points.get(i);
      PVector p2 = points.get(i-1);
      float distance = sqrt(pow(p1.x - p2.x, 2) + pow(p1.y - p2.y, 2));
      pathLength[i] += pathLength[i-1] + distance;
    }

    float [] zpl = new float[pathLength.length];
    for (int i = 0; i < zpl.length; i++)
      zpl[i] = pathLength[i] / pathLength[pathLength.length-1];

    return zpl;
  }
}


// This code should somehow be made into a function and not located in the main class. It should probably be in the BezierFit class I would guess. 
void drawBezier()
{
  int sz = gPts.size();
  if ( sz == 0)
    return;
  beginShape();
  stroke(0, 250, 150);
  float x1 = ((PVector)gPts.get(0)).x;
  float y1 = ((PVector)gPts.get(0)).y;
  float xc = 0.0;
  float yc = 0.0;
  float x2 = 0.0;
  float y2 = 0.0;
  vertex(x1, y1);
  for ( int i = 1; i< sz - 2; ++i)
  {
    xc = ((PVector)gPts.get(i)).x;
    yc = ((PVector)gPts.get(i)).y;
    x2 = (xc + ((PVector)gPts.get(i+1)).x)*0.5;
    y2 = (yc + ((PVector)gPts.get(i+1)).y)*0.5;
    bezierVertex((x1 + 2.0*xc)/3.0, (y1 + 2.0*yc)/3.0, 
    (2.0*xc + x2)/3.0, (2.0*yc + y2)/3.0, x2, y2);
    x1 = x2;
    y1 = y2;
  }
  xc = ((PVector)gPts.get(sz-2)).x;
  yc = ((PVector)gPts.get(sz-2)).y;
  x2 = ((PVector)gPts.get(sz-1)).x;
  y2 = ((PVector)gPts.get(sz-1)).y;
  bezierVertex((x1 + 2.0*xc)/3.0, (y1 + 2.0*yc)/3.0, 
  (2.0*xc + x2)/3.0, (2.0*yc + y2)/3.0, x2, y2);
  endShape();
  stroke(0, 0, 0);
}

*/

