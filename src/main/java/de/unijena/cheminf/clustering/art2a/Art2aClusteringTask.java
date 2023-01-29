package de.unijena.cheminf.clustering.art2a;

import java.io.IOException;
import java.util.concurrent.Callable;

public class Art2aClusteringTask implements Callable<ART2aClustering> {
    private ART2aClustering art2aClustering;

    private float[] [] dataMatrix;
    private int number;
    private float vigilance;
   // private Art2aClusteringResult result;

    public Art2aClusteringTask(float vigilance, float[] [] dataMatrix, int number) throws IOException {
        art2aClustering = new ART2aClustering(dataMatrix, number);
      //  result = new Art2aClusteringResult();
        this.vigilance = vigilance;
    }
    public Art2aClusteringTask(float vigilance, String file, int number) throws IOException {
        art2aClustering = new ART2aClustering(file, number);
        this.vigilance = vigilance;
    }
    @Override
    public ART2aClustering call() throws Exception {
        art2aClustering.startArt2aClustering(this.vigilance);
        return art2aClustering;
    }
}
