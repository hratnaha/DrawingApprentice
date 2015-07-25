package jcocosketch;

import javax.script.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DQNJS {

	//static final String DQNJS_PATH = "/Users/Yashraj/git/DrawingApprentice/JCocoSketch/src/jcocosketch/DQN.js";
	
	//FIX THIS - path should point to the DQN.js file in the jcocosketch directory
	static final String DQNJS_PATH = "/jcocosketch/DQN.js";
	static ScriptEngineManager manager = new ScriptEngineManager();
	static ScriptEngine engine = manager.getEngineByName("JavaScript");
	static Invocable inv;
	static boolean isinit = false;
	
	
	public static void init() throws IOException {

		if (!isinit) {
			// read script file
			try {
				
				engine.eval(Files.newBufferedReader(
						Paths.get(DQNJS_PATH),
						StandardCharsets.UTF_8));
				
				isinit = true;

			} catch (Exception e) {
				System.out.println("error caught: " + e.toString());
			}
		}
	}

	public static int getAction(int x, int y) {

		inv = (Invocable) engine;
		// call function from script file
		
		Double action;
		try {
			action = (Double) inv.invokeFunction("actDQN", x, y);
			System.out.println("Agent decison : " + action);
			return action.intValue();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0; // could not invoke method

	}

	public static void learn(float reward) {

		inv = (Invocable) engine;
		// call function from script file

		try {
			inv.invokeFunction("learnDQN", reward);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void learn(int x, int y, float reward) {

		inv = (Invocable) engine;

		try {
			getAction(x, y);
			inv.invokeFunction("learnDQN", reward);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
