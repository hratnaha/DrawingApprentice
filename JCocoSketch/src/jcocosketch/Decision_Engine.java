package jcocosketch;

import processing.core.*;

import java.io.IOException;
import java.util.*;

import jcocosketch.intersectionResponse.IntersectionResponseMaster;

public class Decision_Engine {
	private final static float ELEMENTARY_DECISION_PROBABILITY = 0.65f;
	
	Random random = new Random();
	IntersectionResponseMaster xResponseMaster = new IntersectionResponseMaster();
	
	Line line;
	float screenDiag = 0.0f;
	Line line2;
	public Decision_Engine(Line line, Line line2, float screenDiag) {
		this.line = line;
		this.line2 = line;
		this.screenDiag = screenDiag;
		try {
			DQNJS.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Line decision()  {
		/*if (this.line.getTotalDistance() < 2 * screenDiag && ELEMENTARY_DECISION_PROBABILITY < random.nextFloat()) {
			Line response = xResponseMaster.response(this.line);
			if (null != response)
				return response;
		}
		int decision = 1 + random.nextInt(6); //was 4 before default case, its just to increase probability of mutation
		}*/

		int decision = 1 + random.nextInt(11); //was 4 before default case, its just to increase probability of mutation
		try {
		// Use Deep Q-Learning and Reward Shaping
		int x = (int)(line.allPoints.get(0).x/10);
		int y = (int)(line.allPoints.get(0).y/10);
		
		decision = 1 + DQNJS.getAction(x,y);
		float creativitySliderValue = DQNJS.creativityValue;
		if (creativitySliderValue > 0 && creativitySliderValue < 0.33) {
			decision = decision % 4;
		}
		else if (creativitySliderValue > 0.33 && creativitySliderValue < 0.66) {
			decision = 3 + (decision % 7);
		}
		else {
			decision = 9 + (decision % 5);
		}
		
		System.out.println("Action taken by DQN-agent: " + decision);
		} catch (Exception e) {}
		
		return decisionLine(decision);
	}

	public Line decisionLine(int decision) {
		/** Learn the structure and pattern using Hopfield
		 * 
		 */
		//HopfieldAssociate.Init();
		//HopfieldAssociate.Learn(this.line);
		//System.out.println(ClassificationUtility.convertToPattern(line)[0][0]);
		boolean usedSegmentation = false;
		Line_Mod m = new Line_Mod(this.line, random);
		Line newLine = new Line();
		int X = (int) line.allPoints.get(0).x;
		int Y = (int) line.allPoints.get(0).y;
		switch (decision) {
		case 4:
			newLine = m.translation();
			break;
		case 5:
			newLine = m.reflection();
			break;
		case 6:
			newLine = m.scaling();
			break;
		case 1:
			newLine = m.drawBack(this.line);
			break;
			
		case 2:
			newLine = m.drawBackNoisy(this.line);
			
			break;
			//Added new Decision cases, Sept8, 2014 by Kunwar Yashraj Singh
		/*case 5:
			newLine = m.drawMutation(this.line, this.line2, true);
			System.out.println("Invoked the Second Experimental Mutation Function");
			break;
			*/
		case 7:
			newLine = m.drawApproximation(this.line, true);
			usedSegmentation = true;
			newLine = moveSegmentation(m, newLine, X);
			//newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Cauchy Approximation");
			break;
			
		case 8:
			newLine = m.drawPolynomial(false);
			usedSegmentation = true;
			newLine = moveSegmentation(m, newLine, X);
		//	newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Random Polynomial");
			break;
			
		case 9:
			newLine = m.drawOnlyMutation(this.line, this.line2);
			System.out.println("Invoked Only Mutation Algorithm");
			break;
		
		case 3:
			newLine = m.drawBackNoisy(this.line);
			//m.drawBackShade(this.line);
			break;
			
		case 10:
			newLine = m.Segment(this.line, 2, true);
			newLine = moveSegmentation(m, newLine, X);
			usedSegmentation = true;
			//newLine = m.Segment(this.line, true);
			//Print VIA NEAT LEARNING
			//newLine = m.generateBYNEATLEARNING(newLine, 2);
		//	newLine = m.SegmentNEAT(newLine, 1, true);  //Remove if you do not want to learn
			break;
			
		case 11:
		//	newLine = m.generateBYCTMExploration(this.line);
			newLine = m.SegmentAndCTM(this.line, 1);
			newLine = moveSegmentation(m, newLine, X);
			usedSegmentation = true;
			break;
			
		case 12:
			newLine = m.Segment(this.line, 3, true);
			newLine = moveSegmentation(m, newLine, X);
			usedSegmentation = true;
		//	newLine = m.generateBYCTMExploration(this.line);
			break;
			
		case 13:
			newLine = m.Segment(this.line, 2, true);
			newLine = moveSegmentation(m, newLine, X);
			usedSegmentation = true;
		//	newLine = HopfieldAssociate.Generate(this.line);
			break;
			
		default:
			newLine = m.drawMutation(this.line, this.line2);
			//newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Invoked the Main Mutation Algorithm");
			//Fix this cause it sometimes throws NullPointer Exception
			break;
			//newLine = 
		}
		//Force line to be drawn near input line
		if(!usedSegmentation) {
			int offset = 100;
			if (Y > 700)
				offset = -100;
			else
				offset = 100;
		
			newLine = m.MoveTo(newLine, X, Y + offset);
		}
		
		newLine = m.Trim(newLine, 2160, 1440);//newLine;
		newLine.compGenerated = true;

		return newLine;
	}

	private Line moveSegmentation(Line_Mod m, Line newLine, int X) {
		int maxY = m.getMaxY(line);
		int maxYSegment = m.getMaxY(newLine);
		int offsetY = (maxY - maxYSegment)/4;
		newLine = m.MoveTo(newLine, X, maxY + offsetY);
		return newLine;
	}

	
	public void setLine(Line line) {
		this.line = line;
	}
}
