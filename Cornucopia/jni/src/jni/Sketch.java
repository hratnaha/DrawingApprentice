package jni;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Sketch extends Component implements MouseListener, MouseMotionListener {

    int sX = -1, sY = -1;
    static Label stat;
    Image bImage;
    boolean dragging = false;
    boolean isnewline = false;
    int curX = -1, curY = -1;
    Cornucopia recognizer = new Cornucopia();
    
    ArrayList<Point[]> line = new ArrayList<Point[]>();
    ArrayList<BasicPrimitive[]> bps = new ArrayList<BasicPrimitive[]>();
    
    public static void main(String[] av) {

		JFrame jFrame = new JFrame("Mouse Dragger");

		Container cPane = jFrame.getContentPane();

		Image im = Toolkit.getDefaultToolkit().getImage(
				"C:/Users/nikos7/Desktop/pic.jpg");

		Sketch sk = new Sketch(im);

		cPane.setLayout(new BorderLayout());

		cPane.add(BorderLayout.NORTH, new Label(""));

		cPane.add(BorderLayout.CENTER, sk);

		cPane.add(BorderLayout.SOUTH, stat = new Label());

		stat.setSize(jFrame.getSize().width, stat.getSize().height);

		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jFrame.pack();

		jFrame.setVisible(true);
	}

	public Sketch(Image i) {
		
		super();

		bImage = i;

		setSize(300, 200);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void showStatus(String s) {

		stat.setText(s);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
		Point point = event.getPoint();

		sX = point.x;
		sY = point.y;
		isnewline = true;
		dragging = true;
	}

	@Override
	public void mouseReleased(MouseEvent event) {

		dragging = false;
		
		ArrayList<Point[]> newline = (ArrayList<Point[]>)line.clone();
		
		//reset the line
		line = new ArrayList<Point[]>();
		
		// convert the lines into cornucopia readable format
		int size = newline.size();
		int[] passin = new int[size * 2];
		int i=0;
		for(Point[] seg : newline){
			passin[i] = seg[1].x;
			passin[i+1] = seg[1].y;
			i = i+2;
		}
		
		if(size > 1){
			// FIT INTO THE CURVES
			BasicPrimitive[] bp = recognizer.getBasicPrimitives(passin, size, 6);
			bps = new ArrayList<BasicPrimitive[]>();
			bps.add(bp);
		}
		// Repaint the canvas
		this.isnewline = true;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent event) {

		Point p = event.getPoint();

		showStatus("mouse Dragged to " + p);

		curX = p.x;
		curY = p.y;

		if (dragging) {
			repaint();
		}
	}

	@Override
	public void paint(Graphics graphic) {
		super.paint(graphic);
		graphic.setColor(Color.black);
		if(sX < 0 || sY < 0)
			return;
		
		if(!this.isnewline){
			Point sp = new Point(sX, sY);
			Point ep = new Point(curX, curY);
			Point[] curseg = {sp, ep};
			line.add(curseg);
			
			drawLine(graphic, line);
		}	
		for(BasicPrimitive[] pline : bps){
			for(BasicPrimitive bp : pline){
				drawPath(graphic, bp);	
			}
		}
		
		this.isnewline = false;
		sX = curX;
		sY = curY;
	}

	private void drawPath(Graphics graphic, BasicPrimitive bp){
		Point spt = new Point();
		spt.x = (int)bp.startX;
		spt.y = (int)bp.startY;
		switch(bp.bptype){
		case 0: // LINE
			Point ept = new Point();
			ept.x = spt.x + (int)(bp.length * Math.cos(bp.startAngle));
			ept.y = spt.y + (int)(bp.length * Math.sin(bp.startAngle));
			Point[] seg = {spt, ept};
			drawSeg(graphic, seg);
			break;
		case 1: // ARC
			double radius = 1 / bp.startCurvature;
			double strangle = bp.startAngle * 180 / Math.PI;
			double a = (bp.length / radius) * 180 / Math.PI;
			strangle = radius < 0 ? -strangle + a : strangle;
			
			
			double rstrAngle = Math.abs(strangle * Math.PI / 180);
			double ctrX = bp.startX + radius * Math.cos(rstrAngle);
			double ctrY = bp.startY + radius * Math.sin(rstrAngle);
			
			System.out.println("ctrX: " + ctrX + "; startX: " + bp.startX + "; radius * rstrAngle: " + (radius * Math.cos(rstrAngle)));
			
			double strX = ctrX - Math.abs(radius);
			double strY = ctrY - Math.abs(radius);

			
			graphic.drawArc((int)strX, (int)strY, Math.abs((int)(2 * radius)), Math.abs((int)(2 * radius)), (int)strangle, Math.abs((int)a));
//			graphic.drawOval((int)bp.startX, (int)bp.startY, 10, 10);
//			graphic.setColor(Color.red);
//			graphic.drawOval((int)ctrX, (int)ctrY, 10, 10);
//			graphic.setColor(Color.blue);
//			graphic.drawOval((int)strX, (int)strY, 10, 10);
//			graphic.setColor(Color.black);
			break;
		case 2: // CLOTHOID
			break;
		}
	}
	
	private void drawLine(Graphics graphic, ArrayList<Point[]> line){
		for(Point[] seg : line){
			drawSeg(graphic, seg);
		}
	}
	
	private void drawSeg(Graphics graphic, Point[] seg){
		graphic.setColor(Color.black);
		graphic.drawLine(seg[0].x,seg[0].y,seg[1].x,seg[1].y);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {

		showStatus("Mouse to " + e.getPoint());
	}
}
