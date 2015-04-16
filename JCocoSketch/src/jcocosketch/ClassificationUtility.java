package jcocosketch;

import java.util.ArrayList;

public class ClassificationUtility {

	public static ArrayList<Line> countClosedFigures(ArrayList<Line> allLines) {
		ArrayList<Line> closed = new ArrayList<Line>();
		for (Line line : allLines) {
			int n = line.allPoints.size() - 1;
			for (int j =1; j <=n ; ++j) {
				if (line.allPoints.get(0).x == line.allPoints.get(j).x 
						&& line.allPoints.get(j).y == line.allPoints.get(j).y) {
					System.out.println("Closed Lines = " + line.lineID);
					closed.add(line);
					break;
			}
			}
		}
		return closed;
	}
	
	public static void findUserDrawnPatterns(ArrayList<Line> allLines) {
		
	}
}
