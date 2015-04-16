package jcocosketch;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.SimulatedAnnealing;
import opt.example.MaximumEvaluation;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.*;
import shared.FixedIterationTrainer;
import util.linalg.Vector;
public class EMCCTMOPTIMIZE {
	
	public static float[] OptimizePoints(int[] actualYPoints) {
		 EvaluationFunction ef = new SimilarLineEvaluatonFunction(actualYPoints);//MaximumEvaluation(); //change this eval function
		 int N = actualYPoints.length;
         Distribution odd = new DiscreteUniformDistribution(actualYPoints);
         Distribution df = new DiscreteDependencyTree(.1, actualYPoints);
         GenericProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        /* NeighborFunction nf = new DiscreteChangeOneNeighbor(actualYPoints);
         HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
         SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp);
         FixedIterationTrainer fit = new FixedIterationTrainer(sa, 1000); //old = 200000
         fit.train();
         System.out.println("SA: " + ef.value(sa.getOptimal()));*/
       //  Vector v = sa.getOptimal().getData();
        EMCExperiment em = new EMCExperiment(2, 3, pop);
         FixedIterationTrainer fit = new FixedIterationTrainer(em, 20);
         fit.train();
         System.out.println(ef.value(em.getOptimal()));
         Vector v = em.getOptimal().getData();
        
         float[] points = new float[v.size()];
         points[0] = actualYPoints[0];
         for (int i =1; i<points.length;++i) {
        	 points[i] = (float)v.get(i);
         }
        return points;
	}
	
	public static float[] OptimizePoints(int[] actualYPoints, boolean useGA, int trainingTime) {
		 EvaluationFunction ef = new SimilarLineEvaluatonFunction(actualYPoints);//MaximumEvaluation(); //change this eval function
		 int N = actualYPoints.length;
        Distribution odd = new DiscreteUniformDistribution(actualYPoints);
        MutationFunction mf = new DiscreteChangeOneMutation(actualYPoints);
        CrossoverFunction cf = new SingleCrossOver();
        cf = new UniformCrossOver();
       // GenericProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);

        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(400, 200, 10, gap);
        FixedIterationTrainer fit = new FixedIterationTrainer(ga, 100);
        fit.train();
        Vector v = ga.getOptimal().getData();
       
        float[] points = new float[v.size()];
        points[0] = actualYPoints[0];
        for (int i =1; i<points.length;++i) {
       	 points[i] = (float)v.get(i);
        }
       return points;
	}

}
