package jcocosketch;

import java.util.ArrayList;
import java.util.Arrays;

import org.encog.ml.MLCluster;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.kmeans.KMeansClustering;

public class KMeans {

	public static void Cluster(ArrayList<Line> allLines, int K) {
		final BasicMLDataSet set = new BasicMLDataSet();
		
		for (Line element : allLines) {
			ArrayList<Point> allPoints = element.allPoints;
			for (int i=0; i <allPoints.size(); ++i){
				double[] point = {allPoints.get(i).x, allPoints.get(i).y};
				set.add(new BasicMLData(point));
			}
		}

		final KMeansClustering kmeans = new KMeansClustering(K, set);

		kmeans.iteration(100);
		//System.out.println("Final WCSS: " + kmeans.getWCSS());

		// Display the cluster
		int i = 1;
		for (final MLCluster cluster : kmeans.getClusters()) {
			System.out.println("*** Cluster " + (i++) + " ***");
			final MLDataSet ds = cluster.createDataSet();
			final MLDataPair pair = BasicMLDataPair.createPair(
					ds.getInputSize(), ds.getIdealSize());
			for (int j = 0; j < ds.getRecordCount(); j++) {
				ds.getRecord(j, pair);
				System.out.println(Arrays.toString(pair.getInputArray()));

			}
		}
	}
}
