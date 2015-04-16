package jcocosketch;
import org.encog.*;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.basic.BasicMLSequenceSet;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.simple.EncogUtility;

public class NEATLearner {

	public static  NEATPopulation pop = new NEATPopulation(2,1,1000);
	public static NEATNetwork network;
	public static void SimpleLearner(double[][] INPUT, double[][] IDEAL) {
		 MLDataSet trainingSet = new BasicMLDataSet(INPUT, IDEAL);
        
         pop.setInitialConnectionDensity(1.0);// not required, but speeds training
         pop.reset();

         CalculateScore score = new TrainingSetScore(trainingSet);
         // train the neural network

         final EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop,score);

         do {
             train.iteration();
             System.out.println("Epoch #" + train.getIteration() + " Error:" + train.getError()+ ", Species:" + pop.getSpecies().size());
         } while(train.getError() > 0.0001);

         network = (NEATNetwork)train.getCODEC().decode(train.getBestGenome());

         // test the neural network
         System.out.println("Neural Network Results:");
         EncogUtility.evaluate(network, trainingSet);

        // MLDataSet input = new BasicMLSequenceSet()
         Encog.getInstance().shutdown();
	}
	
	public static double getCoeffecientsByNEAT(double[] data) {
		MLData input = new BasicMLData(data);
        MLData out = network.compute(input);
        System.out.println("predicted = " + out.getData(0));
        return out.getData(0);
	}
}
