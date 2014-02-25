package jcocosketch;
//made a change
import processing.core.*;
import processing.data.StringList;

import java.util.*;

import g4p_controls.*;

public class DrawBackFork_Flow_Lines extends PApplet {
	GTextField teachMeTF;
	GTextField drawMeTF;
	GButton teachMeButton;
	GButton drawMeButton;

	ArrayList<Line> allLines = new ArrayList<Line>(); // keeps track of all
	// human lines
	ArrayList<Line> compLines = new ArrayList<Line>(); // keeps track of all
	// comp lines
	
	DrawBackStack stack = new DrawBackStack();
	
	ArrayList<Shape> allShapes = new ArrayList<Shape>();
	String drawingMode = "draw"; // draw, teach, drawPos
	StringList strings = new StringList(); // strings for file output
	int i; // iteration count for stack
	int lineSpeed = 25;
	PImage roboIcon;
	boolean shapeDrag = false;
	boolean activeDrawing = false; 
	boolean intClick = false;
	boolean lineGroup = false;
	boolean bufferChanged = false;
	Shape myShape;
	Shape targetShape;
	Rectangle shapeBound;
	Line curLine;
	Decision_Engine engine;
	int computerColor = color(253, 52, 91);
	int humanColor = color(0);
	Buffer buffer;

	public void setup() {
		buffer = new Buffer(this, this.g);
		roboIcon = loadImage("images/robot.png");
		stack.setIcon(roboIcon); 
		myShape = new Shape();
		size(1200, 700, JAVA2D);
		createGUI();
		customGUI();
		background(255);
		noFill();
		strokeWeight(1);
		smooth();
	}

	public void draw() {
		
		PImage buffImage = buffer.getImage();
		if (null != buffImage) {
			image(buffImage, 0, 0);
			//canvasImage = buffImage
		}
		
		
		//say check if the buffImage has changed
		
		stack.draw(this.g, buffer);

		textSize(16);
		if (drawingMode == "teach") {
			fill(0);
			text("Type name of object, draw it, then click done!", 235, 10,
					220, 50);
		}
		if (drawingMode == "drawPos") {
			fill(0);
			text("Click & drag where you want me to draw.", 235, 10, 220, 50);
		}
		if (shapeDrag) {
			shapeBound.drawRect(this.g);
		}
	}

	// ##### Event Handling
	public void mousePressed() {
		checkInterface(new PVector(mouseX, mouseY));
		// println("intClick = " + intClick +" Drawing Mode: " + drawingMode);
		if (drawingMode == "teach" || drawingMode == "draw" && intClick != true) {
			curLine = new Line();
			//curLine.setStart(new PVector(mouseX, mouseY));
			curLine.setColor(humanColor);
			allLines.add(curLine);
		} else if (drawingMode == "drawPos" && intClick != true) {
			shapeBound = new Rectangle(new PVector(mouseX, mouseY), 0, 0);
			println(shapeBound.getPos());
			// println("shapeBound w: " + shapeBound.t);
		}
	}

	public void mouseDragged() {
		
		//if it is draw mode
		//have to define that we are in draw mode here if we are not already in shapeDrag
		//it is either one or the other, but drawMode is not being set or happens by default
		
		if(!intClick){
		if (drawingMode == "draw") {
			LineSegment l = new LineSegment(new PVector(pmouseX, pmouseY),
					new PVector(mouseX, mouseY));
			curLine.addPoint(new PVector(mouseX, mouseY));
			//line(l.start.x, l.start.y, l.end.x, l.end.y);
			buffer.addSegment(l);
			activeDrawing = true; 
		}
		if (drawingMode == "teach") {
			LineSegment l = new LineSegment(new PVector(pmouseX, pmouseY),
					new PVector(mouseX, mouseY));
			curLine.addPoint(new PVector(mouseX, mouseY));
			//line(l.start.x, l.start.y, l.end.x, l.end.y);
			buffer.addSegment(l);
		}
		if (drawingMode == "drawPos" && !shapeDrag) {
			shapeDrag = true;
		}
		if (shapeDrag) {
			shapeBound.setPos2(new PVector(mouseX, mouseY));
		}
		}
	}

	public void mouseReleased() {
		if (!intClick) {
			//line(pmouseX, pmouseY, mouseX, mouseY);
			if (drawingMode == "draw" && activeDrawing) {
				buffer.addToBuffer(curLine); 
				if(stack.getSize() ==0) buffer.update(); 
				engine = new Decision_Engine(curLine, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
				buffer.allLines.add(curLine); //add human line to buffer storage
				curLine = null;
				Line l = engine.decision();
				l.compGenerated = true; 
				stack.push(l);
				//buffer.allLines.add(l); //add comp line to buffer storage
				compLines.add(l);
				activeDrawing = false; 
			} else if (drawingMode == "drawPos") {
				createShape(shapeBound.origin);
				drawingMode = "draw";
			} else if (drawingMode == "teach") {
				myShape.addLine(curLine);
				myShape.completeShape();
			}
		} else
			intClick = false;
		shapeDrag = false;
	}

	public void keyPressed(){
		if(key=='z')
		{
			buffer.showComp = true; 
			buffer.update(); 
		}
		if(key=='x')
		{
			buffer.showComp = false; 
			buffer.update(); 
		}
	}

	public void clear() {
		allLines = new ArrayList<Line>();
		background(255);
		buffer.clear();
	}

	public void customGUI() {
	}

	public void createShape(PVector pos) {
		println("Creating shape. Target: " + targetShape);
		Shape s = targetShape.createInstance(pos, shapeBound.w, shapeBound.h);
		for (int i = 0; i < s.allLines.size(); i++) {
			stack.push(s.allLines.get(i));
		}
	}

	public void checkInterface(PVector pos) {
		float[][] elements = {
				{ teachMeTF.getX(), teachMeTF.getY(), teachMeTF.getWidth(),
					teachMeTF.getHeight() },
					{ drawMeTF.getX(), drawMeTF.getY(), drawMeTF.getWidth(),
						drawMeTF.getHeight() },
						{ teachMeButton.getX(), teachMeButton.getY(),
							teachMeButton.getWidth(), teachMeButton.getHeight() },
							{ drawMeButton.getX(), drawMeButton.getY(),
								drawMeButton.getWidth(), drawMeButton.getHeight() } };
		for (int i = 0; i < elements.length; i++) {
			float[] s = elements[i];
			if (pos.x > s[0] && pos.x < s[0] + s[2]) {
				if (pos.y > s[1] && pos.y < s[1] + s[3]) {
					intClick = true;
					break;
				}
			} else
				intClick = false;
		}
	}

	public void textfield1_change1(GTextField source, GEvent event) { // _CODE_:teachMeTF:397121:
		// println("teachMeTF - GTextField event occured " +
		// System.currentTimeMillis()%10000000 );
		if (event == event.GETS_FOCUS) {
			drawingMode = "teach";
			println("Drawing mode = " + drawingMode);
			myShape = new Shape();
		}
		if (event == event.DRAGGING) {
			println("dragging");
		}
	} // _CODE_:teachMeTF:397121:

	public void drawMeTF_change1(GTextField source, GEvent event) { // _CODE_:drawMeTF:943118:
		// println("drawMeTF - GTextField event occured " +
		// System.currentTimeMillis()%10000000 );
	} // _CODE_:drawMeTF:943118:

	public void teachMeButton_click1(GButton source, GEvent event) { // _CODE_:teachMeButton:665691:
		if (teachMeTF.getText() == null) {
			println("I don't know what you taught me! Please type a label first and then draw object");
		} else {
			myShape.setID(teachMeTF.getText());
			// println("My shape ID = "+ myShape.getID());
			teachMeTF.setText("");
			allShapes.add(myShape);
			drawingMode = "draw";
		}
	} // _CODE_:teachMeButton:665691:

	public void drawMeButton_click1(GButton source, GEvent event) { // _CODE_:drawMeButton:574185:
		println("Draw me clicked, label = " + drawMeTF.getText());
		if (drawMeTF.getText() == null) {
			println("Nothing to draw");
		} else {
			// create shape here
			// then modify pos, and add to stack, after click
			for (int i = 0; i < allShapes.size(); i++) {
				String ID = allShapes.get(i).getID();
				if (ID.equals(drawMeTF.getText())) {
					Shape s = allShapes.get(i);
					targetShape = new Shape();
					targetShape = s;
					for (int j = 0; j < targetShape.allLines.size(); j++) {
						Line l = targetShape.allLines.get(j);
					}
					strokeWeight(1);
				} else
					println("I don't know the shape");
			}
			drawingMode = "drawPos";
		}
		drawMeTF.setText("");
	} // _CODE_:drawMeButton:574185:

	// Create all the GUI controls.
	// autogenerated do not edit
	public void createGUI() {
		G4P.messagesEnabled(false);
		G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
		G4P.setCursor(ARROW);
		if (frame != null)
			frame.setTitle("Apprentice AI Drawing Partner");
		teachMeTF = new GTextField(this, 15, 10, 120, 30, G4P.SCROLLBARS_NONE);
		teachMeTF.setDefaultText("Teach me!");
		teachMeTF.setLocalColorScheme(GCScheme.CYAN_SCHEME);
		teachMeTF.setOpaque(true);
		teachMeTF.addEventHandler(this, "textfield1_change1");
		drawMeTF = new GTextField(this, 569, 10, 120, 30, G4P.SCROLLBARS_NONE);
		drawMeTF.setDefaultText("[tree]");
		drawMeTF.setLocalColorScheme(GCScheme.CYAN_SCHEME);
		drawMeTF.setOpaque(true);
		drawMeTF.addEventHandler(this, "drawMeTF_change1");
		teachMeButton = new GButton(this, 147, 10, 80, 30);
		teachMeButton.setText("Done!");
		teachMeButton.setLocalColorScheme(GCScheme.CYAN_SCHEME);
		teachMeButton.addEventHandler(this, "teachMeButton_click1");
		drawMeButton = new GButton(this, 473, 10, 80, 30);
		drawMeButton.setText("Draw me a...");
		drawMeButton.setLocalColorScheme(GCScheme.CYAN_SCHEME);
		drawMeButton.addEventHandler(this, "drawMeButton_click1");
	}
}
