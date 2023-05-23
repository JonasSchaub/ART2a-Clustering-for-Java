package de.unijena.cheminf.clustering.art2a.Interfaces;

import java.io.Writer;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface IART2aClusteringResult {
    float getVigilanceParameter();
    int getNumberOfDetectedClusters();
    int getNumberOfEpochs();
    int[] getClusterIndices(int aClusterNumber);
    void getProcessLog(String aPathName);
}
