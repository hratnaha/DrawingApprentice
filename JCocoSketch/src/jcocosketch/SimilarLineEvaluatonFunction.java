package jcocosketch;

import opt.EvaluationFunction;
import shared.Instance;
import util.linalg.Vector;

public class SimilarLineEvaluatonFunction implements EvaluationFunction {
    /**
     * @see opt.EvaluationFunction#value(opt.OptimizationData)
     */
    public double value(Instance d) {
        Vector data = d.getData();

        double max = 0.0;
        for (int i = 0; i < data.size(); i++) {
           max += allY[i] - (int)data.get(i);
        }
        return 1/max;
    }
    int[] allY;
    public SimilarLineEvaluatonFunction(int[] allY) {
    	this.allY = allY;
    }

}
