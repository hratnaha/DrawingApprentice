/**
 * OBSOLETE
 */

package jcocosketch;
import java.util.*;
public class DecisionTable {
	/**
	 * TODO add Markov Decision Process instead of just a table
	 * @param x
	 * @param y
	 * @param decision
	 */

	public static int[][] toNotExecute = new int[2160][1440];
	public static void negativeFeedback(int x, int y, int decision) {
		toNotExecute[x - 1][y - 1] = decision;
		
	}
	
	public static int getDecision(int x, int y) {
		//toNotExecute[x - 1][y - 1] = decision;
		return 0;
	}
}
