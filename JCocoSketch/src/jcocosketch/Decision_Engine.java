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
	public int curDecisionID;

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

	public Line decision(int preDecision) {
		/*
		 * if (this.line.getTotalDistance() < 2 * screenDiag &&
		 * ELEMENTARY_DECISION_PROBABILITY < random.nextFloat()) { Line response
		 * = xResponseMaster.response(this.line); if (null != response) return
		 * response; } int decision = 1 + random.nextInt(6); //was 4 before
		 * default case, its just to increase probability of mutation }
		 */

		int decisionID = 3 + random.nextInt(11); // was 4 before default case,
												 // its just to increase
												 // probability of mutation
//		System.out.println("Agent first Decision: " + decisionID);
//		System.out.println("Previous Decision: " + preDecision);
		try {
			// Use Deep Q-Learning and Reward Shaping
			int x = (int) (line.allPoints.get(0).x / 10);
			int y = (int) (line.allPoints.get(0).y / 10);

			decisionID = 1 + DQNJS.getAction(x, y);
			float creativitySliderValue = DQNJS.creativityValue;
			if (creativitySliderValue >= 0 && creativitySliderValue < 0.33) {
				decisionID = 1 + decisionID % 4;
			} else if (creativitySliderValue > 0.33
					&& creativitySliderValue < 0.66) {
				decisionID = 3 + (decisionID % 7);
			} else {
				decisionID = 9 + (decisionID % 5);
			}

		} catch (Exception e) {
		}

		if (preDecision > -1)
			curDecisionID = preDecision;
		else
			curDecisionID = decisionID;
		System.out.println("Agent Final Decision: " + curDecisionID);
		return decisionLine(curDecisionID);
	}

	public Line decisionLine(int decision) {
		/**
		 * Learn the structure and pattern using Hopfield
		 * 
		 */
		// HopfieldAssociate.Init();
		// HopfieldAssociate.Learn(this.line);
		// System.out.println(ClassificationUtility.convertToPattern(line)[0][0]);
		boolean usedSegmentation = false;
		Line_Mod m = new Line_Mod(this.line, random);
		Line newLine = new Line();
		int X = (int) line.allPoints.get(0).x;
		int Y = (int) line.allPoints.get(0).y;
		switch (decision) {
		case 1:
		case 2:
			newLine = m.drawBack(this.line);
			usedSegmentation = true;
			break;
//		case 1:
//		case 2:
//			newLine = m.drawBackNoisy(this.line);
//			usedSegmentation = true;
//			break;
		// Added new Decision cases, Sept8, 2014 by Kunwar Yashraj Singh
		case 3:
			newLine = m.translation();
			usedSegmentation = true;
			break;

		// case 3:
		// newLine = m.drawBackNoisy(this.line);
		//
		// usedSegmentation = true;
		// //m.drawBackShade(this.line);
		// break;

		case 4:
		case 5:
			newLine = m.reflection();
			usedSegmentation = true;
			break;
		// case 5:
		// newLine = m.drawMutation(this.line, this.line2, true);
		// System.out.println("Invoked the Second Experimental Mutation Function");
		// break;
		case 6:
			newLine = m.scaling();
			usedSegmentation = true;
			break;
		case 7:
			newLine = m.drawApproximation(this.line, true);
			usedSegmentation = true;
			newLine = moveSegmentation(m, newLine, X);
			// newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Cauchy Approximation");
			break;
		case 8:
			newLine = m.drawPolynomial(false);
			usedSegmentation = true;
			newLine = moveSegmentation(m, newLine, X);
			// newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Random Polynomial");
			break;
		case 9:
			newLine = m.drawOnlyMutation(this.line, this.line2);
			usedSegmentation = true;
			newLine = moveSegmentation(m, newLine, X);
			System.out.println("Invoked Only Mutation Algorithm");
			break;
		case 10:
			newLine = m.Segment(this.line, 2, true);
			newLine = moveSegmentation(m, newLine, X);
			usedSegmentation = true;
			// newLine = m.Segment(this.line, true);
			// Print VIA NEAT LEARNING
			// newLine = m.generateBYNEATLEARNING(newLine, 2);
			// newLine = m.SegmentNEAT(newLine, 1, true); //Remove if you do not
			// want to learn
			break;

		case 11:
			// newLine = m.generateBYCTMExploration(this.line);
			newLine = m.SegmentAndCTM(this.line, 1);
			newLine = moveSegmentation(m, newLine, X);
			usedSegmentation = true;
			break;

		case 12:
			newLine = m.Segment(this.line, 3, true);
			newLine = moveSegmentation(m, newLine, X);
			usedSegmentation = true;
			// newLine = m.generateBYCTMExploration(this.line);
			break;

		case 13:
			newLine = m.Segment(this.line, 2, true);
			newLine = moveSegmentation(m, newLine, X);
			usedSegmentation = true;
			// newLine = HopfieldAssociate.Generate(this.line);
			break;

		default:
			newLine = m.drawMutation(this.line, this.line2);
			// newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Invoked the Main Mutation Algorithm");
			// Fix this cause it sometimes throws NullPointer Exception
			break;
		// newLine =
		}
		// Force line to be drawn near input line
		if (!usedSegmentation) {
			int offset = 100;
			if (Y > 700)
				offset = -100;
			else
				offset = 100;

			newLine = m.MoveTo(newLine, X, Y + offset);
		}

		newLine = m.Trim(newLine, 2160, 1440);// newLine;
		newLine.compGenerated = true;

		return newLine;
	}

	private Line moveSegmentation(Line_Mod m, Line newLine, int X) {
		int maxY = m.getMaxY(line);
		int maxYSegment = m.getMaxY(newLine) + 400;
		int offsetY = (maxY - maxYSegment) / 2;
		newLine = m.MoveTo(newLine, X, maxY + offsetY);
		return newLine;
	}

	public void setLine(Line line) {
		this.line = line;
	}
}
