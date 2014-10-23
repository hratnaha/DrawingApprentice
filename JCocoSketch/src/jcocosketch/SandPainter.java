package jcocosketch;
import processing.core.*;



public class SandPainter extends PApplet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	float g;
	PApplet cur;
	int dimx = 900;
	int dimy = 900;
	int num = 0;
	int maxnum = 200;
	

	public SandPainter(PApplet p) {
		g = random(0.01f,0.1f);
		cur = p;
		
	}
	public void regionColor(float x, float y, int t) {
	    // start checking one step away
	    float rx=x;
	    float ry=y;
	    boolean openspace=true;
	    
	    // find extents of open space
	  for(int i =0; i<10; ++i) {
	      // move perpendicular to crack
	      rx+=0.81*sin(t*PI/180);
	      ry-=0.81*cos(t*PI/180);
	      int cx = (int)(rx);
	      int cy = (int)(ry);
	  //    if ((cx>=0) && (cx<dimx) && (cy>=0) && (cy<dimy)) {
	    	  this.render(rx,ry,x,y);
	 //   }
	   //   openspace = false;
	    // draw sand painter
	   
	  }
	}
	
	public void render(float x, float y, float ox, float oy) {
		// modulate gain
		g+=cur.random(-0.050f,0.050f);
		float maxg = 1.0f;
		if (g<0) g=0;
		if (g>maxg) g=maxg;

		// calculate grains by distance
		//int grains = int(sqrt((ox-x)*(ox-x)+(oy-y)*(oy-y)));
		int grains = 64;

		// lay down grains of sand (transparent pixels)
		float w = g/(grains-1);
		for (int i=0;i<grains;i++) {
			float a = (float) (0.1-i/(grains*10.0f));
			cur.stroke(cur.red(cur.color(200)),cur.green(cur.color(200)),cur.blue(cur.color(200)),a*256);
			cur.point(ox+(x-ox)*sin(sin(i*w)),oy+(y-oy)*sin(sin(i*w)));
		}
	}
	
	public void render(float x, float y, float ox, float oy, Line line, PApplet cur) {
		// modulate gain
		g+=cur.random(-0.050f,0.050f);
		float maxg = 1.0f;
		if (g<0) g=0;
		if (g>maxg) g=maxg;

		// calculate grains by distance
		//int grains = int(sqrt((ox-x)*(ox-x)+(oy-y)*(oy-y)));
		int grains = 64;

		// lay down grains of sand (transparent pixels)
		float w = g/(grains-1);
		for (int i=0;i<grains;i++) {
			float a = (float) (0.1-i/(grains*10.0f));
			stroke(cur.red(cur.color(200)),cur.green(cur.color(200)),cur.blue(cur.color(200)),a*256);
			point(ox+(x-ox)*sin(sin(i*w)),oy+(y-oy)*sin(sin(i*w)));
		}
	}
	
	public void render(float x, float y, float ox, float oy, 
			Buffer buffer) {
		// modulate gain
		PGraphics cur = buffer.graphics;
		g+=buffer.master.random(-0.050f,0.050f);
		float maxg = 1.0f;
		if (g<0) g=0;
		if (g>maxg) g=maxg;

		// calculate grains by distance
		//int grains = int(sqrt((ox-x)*(ox-x)+(oy-y)*(oy-y)));
		int grains = 64;

		// lay down grains of sand (transparent pixels)
		float w = g/(grains-1);
		for (int i=0;i<grains;i++) {
			float a = (float) (0.1-i/(grains*10.0f));
			cur.stroke(cur.red(cur.color(200)),cur.green(cur.color(200)),cur.blue(cur.color(200)),a*256);
			cur.point(ox+(x-ox)*sin(sin(i*w)),oy+(y-oy)*sin(sin(i*w)));
		}
	}
}
