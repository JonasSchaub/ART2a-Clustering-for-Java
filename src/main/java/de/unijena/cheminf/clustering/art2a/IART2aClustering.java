package de.unijena.cheminf.clustering.art2a;

public interface IART2aClustering {
    void initializeMatrices();
    int[] randomizeVectorIndices();
    ART2aClusteringResult startClustering(float aVigilanceParameter, boolean aAddLog) throws Exception;

    boolean checkConvergence(int aNumberOfDetectedClasses, int aConvergenceEpoch);

}
