package jcocosketch;

import javax.script.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DQNJS {

	static final String DQNJS_PATH = "/Users/Yashraj/git/DrawingApprentice/JCocoSketch/src/jcocosketch/DQN.js";
	
	// FIX THIS - path should point to the DQN.js file in the jcocosketch directory
	// 
	//static final String DQNJS_PATH = "DQN.js";
	static ScriptEngineManager manager = new ScriptEngineManager();
	static ScriptEngine engine = manager.getEngineByName("JavaScript");
	static Invocable inv;
	static public boolean isinit = false;
	
	
	public static void init() throws IOException {

		if (!isinit) {
			// read script file
			try {
				Path pathToDQNJS = Paths.get(DQNJS_PATH);
				engine.eval(Files.newBufferedReader(
						pathToDQNJS,
						StandardCharsets.UTF_8));
				System.out.println("Deep RL Agent Initialized!");
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
	
	public static float creativityValue = 0.5f;

	public static void setCreativity(float value) {
		
		inv = (Invocable) engine;
		
		try {
			creativityValue = value;
			inv.invokeFunction("setEpsilon", value);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
