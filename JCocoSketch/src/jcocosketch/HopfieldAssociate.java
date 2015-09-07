package jcocosketch;

/**
 * Created by Yashraj on 4/12/15.
 */

import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.neural.thermal.HopfieldNetwork;


import org.encog.Encog;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.neural.thermal.HopfieldNetwork;

/**
 * Simple class to recognize some patterns with a Hopfield Neural Network.
 * This is very loosely based on a an example by Karsten Kutza,
 * written in C on 1996-01-30.
 * http://www.neural-networks-at-your-fingertips.com/hopfield.html
 * <p/>
 * I translated it to Java and adapted it to use Encog for neural
 * network processing.  I mainly kept the patterns from the
 * original example.
 */
public class HopfieldAssociate {

    final static int HEIGHT = 940;
    final static int WIDTH = 100;
    int width = 2160;
	int height= 1440;
	public static HopfieldNetwork hopfieldLogic;
    /**
     * The neural network will learn these patterns.
     */
	
	public static void Init() {
		hopfieldLogic = new HopfieldNetwork(WIDTH * HEIGHT);
	}
    public static final String[][] PATTERN = {{
            "O O O O O ",
            " O O O O O",
            "O O O O O ",
            " O O O O O",
            "O O O O O ",
            " O O O O O",
            "O O O O O ",
            " O O O O O",
            "O O O O O ",
            " O O O O O"},

                    {"OO  OO  OO",
                    "OO  OO  OO",
                    "  OO  OO  ",
                    "  OO  OO  ",
                    "OO  OO  OO",
                    "OO  OO  OO",
                    "  OO  OO  ",
                    "  OO  OO  ",
                    "OO  OO  OO",
                    "OO  OO  OO"},

                    {"OOOOO     ",
                    "OOOOO     ",
                    "OOOOO     ",
                    "OOOOO     ",
                    "OOOOO     ",
                    "     OOOOO",
                    "     OOOOO",
                    "     OOOOO",
                    "     OOOOO",
                    "     OOOOO"},

                    {"O  O  O  O",
                    " O  O  O  ",
                    "  O  O  O ",
                    "O  O  O  O",
                    " O  O  O  ",
                    "  O  O  O ",
                    "O  O  O  O",
                    " O  O  O  ",
                    "  O  O  O ",
                    "O  O  O  O"},

                    {"OOOOOOOOOO",
                    "O        O",
                    "O OOOOOO O",
                    "O O    O O",
                    "O O OO O O",
                    "O O OO O O",
                    "O O    O O",
                    "O OOOOOO O",
                    "O        O",
                    "OOOOOOOOOO"}};

    /**
     * The neural network will be tested on these patterns, to see
     * which of the last set they are the closest to.
     */
    public static final String[][] PATTERN2 = {{
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            " O O O O O",
            "O O O O O ",
            " O O O O O",
            "O O O O O ",
            " O O O O O"},

            {"OOO O    O",
                    " O  OOO OO",
                    "  O O OO O",
                    " OOO   O  ",
                    "OO  O  OOO",
                    " O OOO   O",
                    "O OO  O  O",
                    "   O OOO  ",
                    "OO OOO  O ",
                    " O  O  OOO"},

            {"OOOOO     ",
                    "O   O OOO ",
                    "O   O OOO ",
                    "O   O OOO ",
                    "OOOOO     ",
                    "     OOOOO",
                    " OOO O   O",
                    " OOO O   O",
                    " OOO O   O",
                    "     OOOOO"},

            {"O  OOOO  O",
                    "OO  OOOO  ",
                    "OOO  OOOO ",
                    "OOOO  OOOO",
                    " OOOO  OOO",
                    "  OOOO  OO",
                    "O  OOOO  O",
                    "OO  OOOO  ",
                    "OOO  OOOO ",
                    "OOOO  OOOO"},

            {"OOOOOOOOOO",
                    "O        O",
                    "O        O",
                    "O        O",
                    "O   OO   O",
                    "O   OO   O",
                    "O        O",
                    "O        O",
                    "O        O",
                    "OOOOOOOOOO"}};

    public static BiPolarNeuralData convertPattern(String[][] data, int index) {
        int resultIndex = 0;
        BiPolarNeuralData result = new BiPolarNeuralData(WIDTH * HEIGHT);
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                char ch = data[index][row].charAt(col);
                result.setData(resultIndex++, ch == 'O');
            }
        }
        return result;
    }
    

    public static BiPolarNeuralData convertPattern(int[][] data) {
        int resultIndex = 0;
        BiPolarNeuralData result = new BiPolarNeuralData(WIDTH * HEIGHT);
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                result.setData(resultIndex++, data[row][col] == 1);
            }
        }
        return result;
    }

    public static void display(BiPolarNeuralData pattern1, BiPolarNeuralData pattern2) {
        int index1 = 0;
        int index2 = 0;

        for (int row = 0; row < HEIGHT; row++) {
            StringBuilder line = new StringBuilder();

            for (int col = 0; col < WIDTH; col++) {
                if (pattern1.getBoolean(index1++))
                    line.append('O');
                else
                    line.append(' ');
            }

            line.append("   ->   ");

            for (int col = 0; col < WIDTH; col++) {
                if (pattern2.getBoolean(index2++))
                    line.append('O');
                else
                    line.append(' ');
            }


            System.out.println(line.toString());
        }
    }


    public static void evaluate(HopfieldNetwork hopfieldLogic, String[][] pattern) {
        for (int i = 0; i < pattern.length; i++) {
            BiPolarNeuralData pattern1 = convertPattern(pattern, i);
            hopfieldLogic.setCurrentState(pattern1);
            int cycles = hopfieldLogic.runUntilStable(100);
            BiPolarNeuralData pattern2 = (BiPolarNeuralData) hopfieldLogic.getCurrentState();
            System.out.println("Cycles until stable(max 100): " + cycles + ", result=");
            display(pattern1, pattern2);
            System.out.println("----------------------");
        }
    }

    
    public void run() {
        
        

        for (int i = 0; i < PATTERN.length; i++) {
            hopfieldLogic.addPattern(convertPattern(PATTERN, i));
        }

        evaluate(hopfieldLogic, PATTERN);
        evaluate(hopfieldLogic, PATTERN2);
    }
    
    public static void Learn(Line line) {
    	int[][] lineData = new int[HEIGHT][];
    			lineData = ClassificationUtility.convertToPattern(line);
    	Learn(lineData);
    }
    
    public static void Learn(int[][] data) {
    	hopfieldLogic.addPattern(convertPattern(data));
    }
    
    public static Line Generate(Line line) {
    	int[][] lineData = ClassificationUtility.convertToPattern(line);
    	return Generate(lineData);
    }
    public static Line Generate(int[][] currentPartialData) {
    	BiPolarNeuralData pattern1 = convertPattern(currentPartialData);
        hopfieldLogic.setCurrentState(pattern1);
        int cycles = hopfieldLogic.runUntilStable(100);
        BiPolarNeuralData pattern2 = (BiPolarNeuralData) hopfieldLogic.getCurrentState();
        System.out.println("Cycles until stable(max 100): " + cycles + ", result=");
        System.out.println("----------------------");
        return getLine(pattern2);
    }
    
    public static Line getLine(BiPolarNeuralData pattern1) {
    	Line line = new Line();
    	int index1 = 0;
    	 for (int row = 0; row < HEIGHT; row++) {

             for (int col = 0; col < WIDTH; col++) {
                 if (pattern1.getBoolean(index1++)) {
                	 Point p = new Point(col, row);
                     line.allPoints.add(p);
                 }
             
             }
           
         }
    	 return line;
    }

  /*  public static void main(String[] args) {
        HopfieldAssociate program = new HopfieldAssociate();
        program.run();
        Encog.getInstance().shutdown();
    }*/

}

