package jcocosketch;

import processing.core.*;

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
	}

	public Line decision() {
		/*if (this.line.getTotalDistance() < 2 * screenDiag && ELEMENTARY_DECISION_PROBABILITY < random.nextFloat()) {
			Line response = xResponseMaster.response(this.line);
			if (null != response)
				return response;
		}
		int decision = 1 + random.nextInt(6); //was 4 before default case, its just to increase probability of mutation
		}*/

		int decision = 1 + random.nextInt(11); //was 4 before default case, its just to increase probability of mutation

		return decisionLine(decision);
	}

	protected Line decisionLine(int decision) {
		Line_Mod m = new Line_Mod(this.line, random);
		Line newLine = new Line();
		switch (/*decision*/10) {
		case 1:
			newLine = m.translation();
			break;
		case 2:
			newLine = m.reflection();
			break;
		case 3:
			newLine = m.scaling();
			break;
		case 4:
			newLine = m.drawBack(this.line);
			break;
			
		case 5:
			newLine = m.drawBackNoisy(this.line);
			break;
			//Added new Decision cases, Sept8, 2014 by Kunwar Yashraj Singh
		/*case 5:
			newLine = m.drawMutation(this.line, this.line2, true);
			System.out.println("Invoked the Second Experimental Mutation Function");
			break;
			*/
		case 6:
			newLine = m.drawApproximation(this.line, true);
			//newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Cauchy Approximation");
			break;
			
		case 7:
			newLine = m.drawPolynomial(false);
		//	newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Random Polynomial");
			break;
			
		case 8:
			newLine = m.drawOnlyMutation(this.line, this.line2);
			System.out.println("Invoked Only Mutation Algorithm");
			break;
		
		case 9:
			newLine = m.drawBackShade(this.line);
			break;
			
		case 10:
			newLine = m.Segment(this.line, 2, true);
			//newLine = m.Segment(this.line, true);
			break;
			
		default:
			newLine = m.drawMutation(this.line, this.line2);
			//newLine = m.Trim(newLine, 2160, 1440);
			System.out.println("Invoked the Main Mutation Algorithm");
			//Fix this cause it sometimes throws NullPointer Exception
			break;
			//newLine = 
		}
		newLine = m.Trim(newLine, 2160, 1440);//newLine;
		newLine.compGenerated = true;
		return newLine;
	}

	
	public void setLine(Line line) {
		this.line = line;
	}
}
