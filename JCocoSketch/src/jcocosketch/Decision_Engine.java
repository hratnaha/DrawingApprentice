package jcocosketch;

import processing.core.*;

import java.util.*;

import jcocosketch.intersectionResponse.IntersectionResponseMaster;

public class Decision_Engine {
	private final static float ELEMENTARY_DECISION_PROBABILITY = 0.0f;
	
	Random random = new Random();
	IntersectionResponseMaster xResponseMaster = new IntersectionResponseMaster();

	public Decision_Engine() {
	}

	public Line decision(List<Line> lines) {
		Line line = lines.get(lines.size() - 1);
		if (ELEMENTARY_DECISION_PROBABILITY < random.nextFloat()) {
			Line response = xResponseMaster.response(lines);
			if (null != response)
				return response;
		}
		int decision = 1 + random.nextInt(4);
		Line_Mod m = new Line_Mod(line, random);
		Line newLine = new Line();
		switch (decision) {
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
			newLine = m.drawBack(line);
			break;
		}
		return newLine;
	}
}
