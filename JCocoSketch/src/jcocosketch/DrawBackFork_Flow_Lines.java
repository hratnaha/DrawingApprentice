package jcocosketch;
//made a change
import processing.core.*;
import processing.data.StringList;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import g4p_controls.*;

public class DrawBackFork_Flow_Lines extends PApplet {
	GTextField teachMeTF;
	GTextField drawMeTF;
	GButton teachMeButton;
	GButton drawMeButton;
	GButton localButton;
	GButton regionalButton;
	GButton globalButton;
	GLabel modeText;

	ArrayList<Line> allLines = new ArrayList<Line>(); // keeps track of all
	// human lines
	ArrayList<Line> compLines = new ArrayList<Line>(); // keeps track of all
	// comp lines
	
	DrawBackStack stack = new DrawBackStack();
	
	ArrayList<Shape> allShapes = new ArrayList<Shape>();
	String drawingMode = "draw"; // draw, teach, drawPos
	String perceptionMode = "local";
	StringList strings = new StringList(); // strings for file output
	int i; // iteration count for stack
	int lineSpeed = 25;
	PImage roboIcon;
	boolean shapeDrag = false;
	boolean activeDrawing = false; 
	boolean intClick = false;
	boolean lineGroup = false;
	boolean bufferChanged = false;
	boolean lassoOn = false;
	Shape myShape;
	Shape targetShape;
	Rectangle shapeBound;
	Line curLine;
	LassoLine curLasso;
	Decision_Engine engine;
	int computerColor = color(253, 52, 91);
	int humanColor = color(0);
	Buffer buffer;
	int width = 2160;
	int height= 1440;
	Timer perceptualTimer;
	Timer generalTimer;
	int humanNotActiveSec;
	int localSec;
	int regionalSec;
	int globalSec;
	int AIPausedSec = 0;
	
	public void setup() {
		buffer = new Buffer(this, this.g, width, height);
		roboIcon = loadImage("images/robot.png");
		stack.setIcon(roboIcon); 
		myShape = new Shape();
		
		//size(PApplet.WIDTH, PApplet.HEIGHT, JAVA2D); 
		size(2160, 1440, JAVA2D);
		createGUI();
		customGUI();
		background(255);
		noFill();
		strokeWeight(1);
		
		//Initialize time counters
		humanNotActiveSec = 0;
		localSec = 0;
		regionalSec = 0;
		globalSec = 0;
		
		perceptualTimer = new Timer(1000, new PerceptualMonitor());
		perceptualTimer.setInitialDelay(1000);
		
		generalTimer = new Timer(1000, new GeneralTiming());
		generalTimer.setInitialDelay(1000);
		
		perceptualTimer.start();
		generalTimer.start();
		//smooth();
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
		if(perceptionMode.equals("local")){
			fill(0);
			textSize(24); 
			text("Local", 400, 25, 220, 50);
		}
		if(perceptionMode.equals("regional")){
			fill(0);
			textSize(24); 
			text("Regional", 400, 25, 220, 50);
		}
		if(perceptionMode.equals("global")){
			fill(0);
			textSize(24); 
			text("Global", 400, 25, 220, 50);
		}
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
		//checkInterface(new PVector(mouseX, mouseY));
		// println("intClick = " + intClick +" Drawing Mode: " + drawingMode);
		this.humanNotActiveSec = 0;
		generalTimer.stop();
		if (drawingMode == "teach" || drawingMode == "draw" && intClick != true) {
			if(mouseButton == LEFT){
				curLine = new Line();
				//curLine.setStart(new PVector(mouseX, mouseY));
				curLine.setColor(humanColor);
				allLines.add(curLine);
			}
			if(mouseButton == RIGHT){
				System.out.println("Right Button Pressed");
				curLasso = new LassoLine();
				//curLine.setStart(new PVector(mouseX, mouseY));
				allLines.add(curLasso);
			}
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
		mouseX = mouseX < 0 ? 0 : mouseX;
		mouseY = mouseY < 0 ? 0 : mouseY;
		this.humanNotActiveSec = -1;
		if(!intClick){
		if (drawingMode == "draw" && this.mouseButton == LEFT) {
			LineSegment l = new LineSegment(new PVector(pmouseX, pmouseY),
					new PVector(mouseX, mouseY));
			curLine.addPoint(new Point(mouseX, mouseY, curLine.lineID));
			//ellipse(mouseX, mouseY, 10, 10); 
			//line(l.start.x, l.start.y, l.end.x, l.end.y);
			buffer.addSegment(l);
			
			activeDrawing = true; 
		}
		//Lasso Tool with right click
		if (drawingMode == "draw" && mouseButton == RIGHT ) {
			//System.out.println("Right Button Dragged");
			lassoOn = true;
			LineSegment l = new LineSegment(new PVector(pmouseX, pmouseY),
					new PVector(mouseX, mouseY));
			curLasso.addPoint(new Point(mouseX, mouseY, curLasso.lineID));
			ellipse(mouseX, mouseY, 10, 10); 
			//line(l.start.x, l.start.y, l.end.x, l.end.y);
			//Need to figure out how to get lasso to work without adding it to the segment and being accounted for as an extra line
			buffer.addSegment(l);	
			activeDrawing = true; 
		}
		if (drawingMode == "teach") {
			LineSegment l = new LineSegment(new PVector(pmouseX, pmouseY),
					new PVector(mouseX, mouseY));
			curLine.addPoint(new Point(mouseX, mouseY, curLine.lineID));
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
Line line2;
	public void mouseReleased() {
		this.humanNotActiveSec = 0;
		generalTimer.restart();
		if (!intClick) {
			//line(pmouseX, pmouseY, mouseX, mouseY);
			if (drawingMode == "draw" && activeDrawing && lassoOn == false) {
				buffer.addToBuffer(curLine); 
				if(stack.getSize() ==0) buffer.update();
				
				buffer.allLines.add(curLine); //add human line to buffer storage			
				/**Local Perceptual Logic**/
				if(perceptionMode.equals("local")){
					int offset = 3;
					if(allLines.size()>offset){
						
						line2 = allLines.get(allLines.size()-offset);
					}
					else
					{
						line2 = curLine; ///can add other shapes to mutate with
					}
					//Added new stuff here - KYS
				//	if (buffer.allGroups.size() > 0) {
				//		Line l1 = buffer.allGroups.get(0).get(0);
			//			engine = new Decision_Engine(l1, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
				//		System.out.println("Added line from lasso");
			//		}else {
					if(curLine == null){
						engine = new Decision_Engine(buffer.allLines.get(buffer.allLines.size()-1), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
					}else{
						engine = new Decision_Engine(curLine, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
					}
			//		}
					buffer.allLines.add(curLine); //add human line to buffer storage
					
					curLine = null;
					Line l = engine.decision();
					l.compGenerated = true; 
					stack.push(l);
					for (int j= 0; j < l.allPoints.size() - 1; j++) {
						Point p1 = l.allPoints.get(j); 
						Point p2 = l.allPoints.get(j+1);
						buffer.mainTree.set(p1.getX(),p1.getY(),p1);
						buffer.mainTree.set(p2.getX(),p2.getY(),p2);
					}
					//buffer.allLines.add(l); //add comp line to buffer storage
					compLines.add(l);
					activeDrawing = false; 
				}
				if(perceptionMode.equals("regional")){
					//boolean isInGroup = true;
					Group enclosingGroup = null;
					curLine.makeBoundingBox();
					for(int i = 0; i < buffer.allGroups.size(); i++){
						if(curLine.xmin > buffer.allGroups.get(i).getXmin() && curLine.ymin > buffer.allGroups.get(i).getYmin()
								&& curLine.xmax < buffer.allGroups.get(i).getXmax() && curLine.ymax < buffer.allGroups.get(i).getYmax()){
							enclosingGroup = buffer.allGroups.get(i);
							i=buffer.allGroups.size();
						}
					}
					if(enclosingGroup == null){
						System.out.println("Regional: Current Line is not in a group");
					}
					else{
						System.out.println("Regional: Current Line is in a group");
						Random randy = new Random();
						int randomLineIndex = randy.nextInt(enclosingGroup.lines.size());
						line2 = curLine;
						engine = new Decision_Engine(enclosingGroup.lines.get(randomLineIndex), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
						Line aiLine = engine.decision();
						aiLine.compGenerated = true; 
						stack.push(aiLine);
						compLines.add(aiLine);
						for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
							Point p1 = aiLine.allPoints.get(j); 
							Point p2 = aiLine.allPoints.get(j+1);
							buffer.mainTree.set(p1.getX(),p1.getY(),p1);
							buffer.mainTree.set(p2.getX(),p2.getY(),p2);
						}
					}
				}
//				if(perceptionMode.equals("global")){
//					//boolean isInGroup = true;
//					//Different Global Behaviors
//					Random randy = new Random();
//					int randomSelection = randy.nextInt(3);
//					//Behavior 1: AI draws in least dense node based on random group
//					if(randomSelection == 0){
//						Node leastDense = buffer.mainTree.leastDenseNode();
//						//System.out.println(leastDense.getX() + "," + leastDense.getY());
//						
//						if(buffer.allGroups.size() > 0){
//							Group randomGroup = buffer.allGroups.get(randy.nextInt(buffer.allGroups.size()));
//							Group normGroup = randomGroup.normalizedGroup();
//							System.out.println("Actual Norm Mins: " + randomGroup.getXmin() + "," + randomGroup.getYmin());
//							Group shiftedGroup = normGroup.shiftGroup(leastDense.getX(), leastDense.getY());
//							for(int i = 0; i < shiftedGroup.lines.size(); i++){
//								engine = new Decision_Engine(shiftedGroup.lines.get(i), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//								Line aiLine = engine.decision();
//								aiLine.compGenerated = true; 
//								stack.push(aiLine);
//								compLines.add(aiLine);
//								for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
//									Point p1 = aiLine.allPoints.get(j); 
//									Point p2 = aiLine.allPoints.get(j+1);
//									buffer.mainTree.set(p1.getX(),p1.getY(),p1);
//									buffer.mainTree.set(p2.getX(),p2.getY(),p2);
//								}
//							}
//						}
//						else{
//							Group randomGroup = new Group();
//							randomGroup.addLine(curLine);
//							Group normGroup = randomGroup.normalizedGroup();
//							Group shiftedGroup = normGroup.shiftGroup(leastDense.getX(), leastDense.getY());
//							engine = new Decision_Engine(shiftedGroup.lines.get(0), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//							Line aiLine = engine.decision();
//							aiLine.compGenerated = true; 
//							stack.push(aiLine);
//							compLines.add(aiLine);
//							for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
//								Point p1 = aiLine.allPoints.get(j); 
//								Point p2 = aiLine.allPoints.get(j+1);
//								buffer.mainTree.set(p1.getX(),p1.getY(),p1);
//								buffer.mainTree.set(p2.getX(),p2.getY(),p2);
//							}
//						}
//						curLine = null;
//					}
//					//Behavior 2: AI draws in least dense node based on random line
//					if(randomSelection == 1){
//						Node leastDense = buffer.mainTree.leastDenseNode();
//						
//						if(buffer.allLines.size() > 0){
//							Line randomLine = buffer.allLines.get(randy.nextInt(buffer.allLines.size()));
//							Line normalizedLine = randomLine.normalizeLine();
//							Line shiftedLine = normalizedLine.shiftLine(leastDense.getX(), leastDense.getY());
//							engine = new Decision_Engine(shiftedLine, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//							Line aiLine = engine.decision();
//							aiLine.compGenerated = true; 
//							stack.push(aiLine);
//							compLines.add(aiLine);
//							for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
//								Point p1 = aiLine.allPoints.get(j); 
//								Point p2 = aiLine.allPoints.get(j+1);
//								buffer.mainTree.set(p1.getX(),p1.getY(),p1);
//								buffer.mainTree.set(p2.getX(),p2.getY(),p2);
//							}
//						} else{
//							if(curLine != null){
//								Line normalizedLine = curLine.normalizeLine();
//								Line shiftedLine = normalizedLine.shiftLine(leastDense.getX(), leastDense.getY());
//								engine = new Decision_Engine(shiftedLine, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//								Line aiLine = engine.decision();
//								aiLine.compGenerated = true; 
//								stack.push(aiLine);
//								compLines.add(aiLine);
//								for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
//									Point p1 = aiLine.allPoints.get(j); 
//									Point p2 = aiLine.allPoints.get(j+1);
//									buffer.mainTree.set(p1.getX(),p1.getY(),p1);
//									buffer.mainTree.set(p2.getX(),p2.getY(),p2);
//								}
//							}
//						}
//						curLine = null;
//					}
//					//Behavior 3: AI selects random group and reacts to all lines in the group (like local reaction)
//					if(randomSelection == 2){
//						Group randomGroup = buffer.allGroups.get(randy.nextInt(buffer.allGroups.size()));
//						
//						for(int i = 0; i < randomGroup.lines.size(); i++){
//							engine = new Decision_Engine(randomGroup.lines.get(i), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//							Line aiLine = engine.decision();
//							aiLine.compGenerated = true; 
//							stack.push(aiLine);
//							compLines.add(aiLine);
//							for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
//								Point p1 = aiLine.allPoints.get(j); 
//								Point p2 = aiLine.allPoints.get(j+1);
//								buffer.mainTree.set(p1.getX(),p1.getY(),p1);
//								buffer.mainTree.set(p2.getX(),p2.getY(),p2);
//							}
//						}
//					}
//				}
				curLine = null;
			} if (drawingMode == "draw" && lassoOn == true) {
				System.out.println("Right Button Released");
				//perceptionMode = "regional";
				buffer.lassoLine = curLasso;
				curLasso = null;
				lassoOn = false;
				buffer.update();
				//this.drawAfterLasso();
				
			} if (drawingMode == "drawPos") {
				createShape(shapeBound.origin);
				drawingMode = "draw";
			} if (drawingMode == "teach") {
				myShape.addLine(curLine);
				myShape.completeShape();
			}
		} else
			intClick = false;
		shapeDrag = false;
	}

	/**
	 * Added new funciton to draw in a region after lasso is done
	 */
	public void drawAfterLasso() {
		System.out.println("Added line from lasso - regional after lasso");
		if (buffer.allGroups.size() > 0) {
			//for (int k =0; k<buffer.allGroups.size(); ++k) {
			int size_main = buffer.allGroups.size() -1; //k;//
			int size_lines = buffer.allGroups.get(size_main).getSize() -1;
			int lineCount = 0;
			while(lineCount < 10){
				for (int i = 0; i< buffer.allGroups.get(size_main).getSize(); i++ )
				{		
					Line l1 = buffer.allGroups.get(size_main).lines.get(/*size_lines*/i);
					engine = new Decision_Engine(l1, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
					curLine = null;
					Line l = engine.decision();
					l.compGenerated = true; 
					stack.push(l);
					//buffer.allLines.add(l); //add comp line to buffer storage
					compLines.add(l);
					activeDrawing = false; 
					System.out.println("Added line from lasso");
					if(lineCount >=9){
						i = buffer.allGroups.get(size_main).getSize();
					}
					lineCount++;
				}
			}
			//}
			
			
		}else {
			engine = new Decision_Engine(curLine, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
		}
		//perceptionMode = "local";
//		if (buffer.allGroups.size() > 0) {
//			//for (int k =0; k<buffer.allGroups.size(); ++k) {
//			int size_main = buffer.allGroups.size() -1; //k;//
//			int size_lines = buffer.allGroups.get(size_main).getSize() -1;
//			for (int i = 0; i< buffer.allGroups.get(size_main).getSize(); ++i )
//			{
//			
//			Line l1 = buffer.allGroups.get(size_main).lines.get(/*size_lines*/i);
//			engine = new Decision_Engine(l1, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//			curLine = null;
//			Line l = engine.decision();
//			l.compGenerated = true; 
//			stack.push(l);
//			//buffer.allLines.add(l); //add comp line to buffer storage
//			compLines.add(l);
//			activeDrawing = false; 
//			System.out.println("Added line from lasso");
//			}
//			//}
//			
//		}else {
//		engine = new Decision_Engine(curLine, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//		}
		
		
	}
	public void keyPressed(){
		//If 'm' is pressed bot draws a random group is drawn on the the quadrant with lead density
		if(key=='m'){
			Node leastDense = buffer.mainTree.leastDenseNode();
			System.out.println(leastDense.getX() + "," + leastDense.getY());
			Random randy = new Random();
			Group randomGroup = buffer.allGroups.get(randy.nextInt(buffer.allGroups.size()));
			Group normGroup = randomGroup.normalizedGroup();
			System.out.println("Actual Norm Mins: " + randomGroup.getXmin() + "," + randomGroup.getYmin());
			Group shiftedGroup = normGroup.shiftGroup(leastDense.getX(), leastDense.getY());
			for(int i = 0; i < shiftedGroup.lines.size(); i++){
				engine = new Decision_Engine(shiftedGroup.lines.get(i), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
				Line aiLine = engine.decision();
				aiLine.compGenerated = true; 
				stack.push(aiLine);
				compLines.add(aiLine);
			}
			//buffer.allGroups.add(shiftedGroup);
			
		}
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
		if(key=='c')
		{
			buffer.clear();
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
/*
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
*/
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
	
	public void localButton_click1(GButton source, GEvent event) { 
		localSec = 0;
		regionalSec = 0;
		globalSec = 0;
		System.out.println("Local Button Pressed");
		perceptionMode="local";
	} 
	
	public void regionalButton_click1(GButton source, GEvent event) { 
		localSec = 0;
		regionalSec = 0;
		globalSec = 0;
		System.out.println("Regional Button Pressed");
		perceptionMode="regional";
	}
	
	public void globalButton_click1(GButton source, GEvent event) { 
		localSec = 0;
		regionalSec = 0;
		globalSec = 0;
		System.out.println("Global Button Pressed");
		perceptionMode="global";
	}

	// Create all the GUI controls.
	// autogenerated do not edit
	public void createGUI() {
		G4P.messagesEnabled(false);
		G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
		G4P.setCursor(ARROW);
		if (frame != null)
			frame.setTitle("Apprentice AI Drawing Partner");
		/*
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
		*/
		
		localButton = new GButton(this, 25, 10, 100, 50);
		localButton.setText("Local");
		//localButton.setLocalColorScheme(GCScheme.CYAN_SCHEME);
		localButton.addEventHandler(this, "localButton_click1");
		
		regionalButton = new GButton(this, 150, 10, 100, 50);
		regionalButton.setText("Regional");
		//localButton.setLocalColorScheme(GCScheme.CYAN_SCHEME);
		regionalButton.addEventHandler(this, "regionalButton_click1");
		
		globalButton = new GButton(this, 275, 10, 100, 50);
		globalButton.setText("Global");
		//localButton.setLocalColorScheme(GCScheme.CYAN_SCHEME);
		globalButton.addEventHandler(this, "globalButton_click1");
	}
	
	//Function starts global behaviors up again
	//Call after DRAWING of a global behavior is done
	public void afterGlobalBehavior(){
		perceptualTimer.setDelay(5000);
		perceptualTimer.start();
	}
	
	public class GeneralTiming implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			humanNotActiveSec++;
			System.out.println("Human last active " + humanNotActiveSec + " seconds ago\n");
			if(!perceptualTimer.isRunning()){
				AIPausedSec++;
				System.out.println("AI System paused for " + AIPausedSec + " seconds");
			}
			else{
				AIPausedSec = 0;
			}
		}
		
	}
	
	/**
	 * This is where the perceptual logic happens seperate from user input
	 *
	 */
	public class PerceptualMonitor implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//Perceptual Modes
			if(buffer.allLines.size() > 0){
				
				//Get's last line (the current line)
				Line lastLine = buffer.allLines.get(buffer.allLines.size()-1);
				
				/**Local Perceptual Logic**/
//				if(perceptionMode.equals("local")){
//					localSec++;
//					if(localSec > 0 && localSec % 3==0){
//						perceptualTimer.stop();
//						System.out.println(perceptualTimer.isRunning());
//						int offset = 3;
//						if(allLines.size()>offset){
//							
//							line2 = allLines.get(allLines.size()-offset);
//						}
//						else
//						{
//							line2 = lastLine; ///can add other shapes to mutate with
//						}
//						//Added new stuff here - KYS
//					//	if (buffer.allGroups.size() > 0) {
//					//		Line l1 = buffer.allGroups.get(0).get(0);
//				//			engine = new Decision_Engine(l1, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//					//		System.out.println("Added line from lasso");
//				//		}else {
//						engine = new Decision_Engine(lastLine, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//						
//				//		}
//						
//						Line l = engine.decision();
//						l.compGenerated = true; 
//						stack.push(l);
//						for (int j= 0; j < l.allPoints.size() - 1; j++) {
//							Point p1 = l.allPoints.get(j); 
//							Point p2 = l.allPoints.get(j+1);
//							buffer.mainTree.set(p1.getX(),p1.getY(),p1);
//							buffer.mainTree.set(p2.getX(),p2.getY(),p2);
//						}
//						//buffer.allLines.add(l); //add comp line to buffer storage
//						compLines.add(l);
//						
//						localSec = 0;
//						perceptualTimer.setInitialDelay(2500);
//						perceptualTimer.start();
//					}
//				}
				
				/**Regional Perceptual Logic**/
//				if(perceptionMode.equals("regional") && buffer.allGroups.size() > 0){
//					//boolean isInGroup = true;
//					Group enclosingGroup = null;
//					lastLine.makeBoundingBox();
//					for(int i = 0; i < buffer.allGroups.size(); i++){
//						if(lastLine.xmin > buffer.allGroups.get(i).getXmin() && lastLine.ymin > buffer.allGroups.get(i).getYmin()
//								&& lastLine.xmax < buffer.allGroups.get(i).getXmax() && lastLine.ymax < buffer.allGroups.get(i).getYmax()){
//							enclosingGroup = buffer.allGroups.get(i);
//							i=buffer.allGroups.size();
//						}
//					}
//					if(enclosingGroup == null){
//						System.out.println("Regional: Current Line is not in a group");
//					}
//					else{
//						System.out.println("Regional: Current Line is in a group");
//						Random randy = new Random();
//						int randomLineIndex = randy.nextInt(enclosingGroup.lines.size());
//						line2 = lastLine;
//						engine = new Decision_Engine(enclosingGroup.lines.get(randomLineIndex), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
//						Line aiLine = engine.decision();
//						aiLine.compGenerated = true; 
//						stack.push(aiLine);
//						compLines.add(aiLine);
//						for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
//							Point p1 = aiLine.allPoints.get(j); 
//							Point p2 = aiLine.allPoints.get(j+1);
//							buffer.mainTree.set(p1.getX(),p1.getY(),p1);
//							buffer.mainTree.set(p2.getX(),p2.getY(),p2);
//						}
//					}
//				}
					
				/**Global Perceptual Logic**/
				if(perceptionMode.equals("global")){
					globalSec++;
					if(humanNotActiveSec >= 15){
						perceptionMode = "local";
					}	
					else if(globalSec > 0 && globalSec % 3==0){
						//boolean isInGroup = true;
						//Different Global Behaviors
						perceptualTimer.stop();
						
						int randomSelection;
						Random randy = new Random();
						
						if(buffer.allGroups.size() > 0){
							randomSelection = randy.nextInt(3);
						}
						else{
							randomSelection = 1;
						}
						//Behavior 1: AI draws in least dense node based on random group
						if(randomSelection == 0 && buffer.allGroups.size() > 0){
							Node leastDense = buffer.mainTree.leastDenseNode();
							//System.out.println(leastDense.getX() + "," + leastDense.getY());
							
							if(buffer.allGroups.size() > 0){
								Group randomGroup = buffer.allGroups.get(randy.nextInt(buffer.allGroups.size()));
								Group normGroup = randomGroup.normalizedGroup();
								System.out.println("Actual Norm Mins: " + randomGroup.getXmin() + "," + randomGroup.getYmin());
								Group shiftedGroup = normGroup.shiftGroup(leastDense.getX(), leastDense.getY());
								for(int i = 0; i < shiftedGroup.lines.size(); i++){
									engine = new Decision_Engine(shiftedGroup.lines.get(i), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
									Line aiLine = engine.decision();
									perceptualTimer.stop();
									aiLine.compGenerated = true; 
									stack.push(aiLine);
									compLines.add(aiLine);
									
									for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
										Point p1 = aiLine.allPoints.get(j); 
										Point p2 = aiLine.allPoints.get(j+1);
										buffer.mainTree.set(p1.getX(),p1.getY(),p1);
										buffer.mainTree.set(p2.getX(),p2.getY(),p2);
									}
								}
							}
							else{
								Group randomGroup = new Group();
								randomGroup.addLine(lastLine);
								Group normGroup = randomGroup.normalizedGroup();
								Group shiftedGroup = normGroup.shiftGroup(leastDense.getX(), leastDense.getY());
								engine = new Decision_Engine(shiftedGroup.lines.get(0), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
								Line aiLine = engine.decision();
								aiLine.compGenerated = true; 
								stack.push(aiLine);
								compLines.add(aiLine);
								for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
									Point p1 = aiLine.allPoints.get(j); 
									Point p2 = aiLine.allPoints.get(j+1);
									buffer.mainTree.set(p1.getX(),p1.getY(),p1);
									buffer.mainTree.set(p2.getX(),p2.getY(),p2);
								}
							}
						}
						//Behavior 2: AI draws in least dense node based on random line
						if(randomSelection == 1){
							Node leastDense = buffer.mainTree.leastDenseNode();
							
							if(buffer.allLines.size() > 0){
								Line randomLine = buffer.allLines.get(randy.nextInt(buffer.allLines.size()));
								Line normalizedLine = randomLine.normalizeLine();
								Line shiftedLine = normalizedLine.shiftLine(leastDense.getX(), leastDense.getY());
								engine = new Decision_Engine(shiftedLine, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
								Line aiLine = engine.decision();
								aiLine.compGenerated = true; 
								stack.push(aiLine);
								compLines.add(aiLine);
								for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
									Point p1 = aiLine.allPoints.get(j); 
									Point p2 = aiLine.allPoints.get(j+1);
									buffer.mainTree.set(p1.getX(),p1.getY(),p1);
									buffer.mainTree.set(p2.getX(),p2.getY(),p2);
								}
							} else{
								if(lastLine != null){
									Line normalizedLine = lastLine.normalizeLine();
									Line shiftedLine = normalizedLine.shiftLine(leastDense.getX(), leastDense.getY());
									engine = new Decision_Engine(shiftedLine, line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
									Line aiLine = engine.decision();
									aiLine.compGenerated = true; 
									stack.push(aiLine);
									compLines.add(aiLine);
									for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
										Point p1 = aiLine.allPoints.get(j); 
										Point p2 = aiLine.allPoints.get(j+1);
										buffer.mainTree.set(p1.getX(),p1.getY(),p1);
										buffer.mainTree.set(p2.getX(),p2.getY(),p2);
									}
								}
							}
						}
						//Behavior 3: AI selects random group and reacts to all lines in the group (like local reaction)
						if(randomSelection == 2 && buffer.allGroups.size() > 0){
							Group randomGroup = buffer.allGroups.get(randy.nextInt(buffer.allGroups.size()));
							
							for(int i = 0; i < randomGroup.lines.size(); i++){
								engine = new Decision_Engine(randomGroup.lines.get(i), line2, (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
								Line aiLine = engine.decision();
								aiLine.compGenerated = true; 
								stack.push(aiLine);
								compLines.add(aiLine);
								for (int j= 0; j < aiLine.allPoints.size() - 1; j++) {
									Point p1 = aiLine.allPoints.get(j); 
									Point p2 = aiLine.allPoints.get(j+1);
									buffer.mainTree.set(p1.getX(),p1.getY(),p1);
									buffer.mainTree.set(p2.getX(),p2.getY(),p2);
								}
							}
						}
						globalSec=0;
						afterGlobalBehavior();//Remove this once this same call is made after all line_mod DRAWINGS (not computations)
					}
				}
			}
		}
		
	}
}
