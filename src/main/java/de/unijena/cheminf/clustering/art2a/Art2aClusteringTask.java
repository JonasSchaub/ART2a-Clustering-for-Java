package de.unijena.cheminf.clustering.art2a;

import de.unijena.cheminf.clustering.art2a.Logger.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;

public class Art2aClusteringTask implements Callable<ART2aClustering> {
    private ART2aClustering art2aClustering;

    private float[] [] dataMatrix;
    private int number;
    private float vigilance;
   // private Art2aClusteringResult result;

    public Art2aClusteringTask(float vigilance, float[] [] dataMatrix, int number, Logger aLogger) throws IOException {
        art2aClustering = new ART2aClustering(dataMatrix, number, aLogger);
      //  result = new Art2aClusteringResult();
        this.vigilance = vigilance;
    }
    public Art2aClusteringTask(float vigilance, String file, int number, Logger aLogger) throws IOException {
        art2aClustering = new ART2aClustering(file, number, aLogger);
        this.vigilance = vigilance;
    }
    @Override
    public ART2aClustering call() throws Exception {
        art2aClustering.getClustering(this.vigilance);
        return art2aClustering;
    }
}
