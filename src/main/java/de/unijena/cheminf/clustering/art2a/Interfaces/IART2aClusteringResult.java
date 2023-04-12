package de.unijena.cheminf.clustering.art2a.Interfaces;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface IART2aClusteringResult {
    float getVigilanceParameter();
    int getNumberOfDetectedClusters();
    int getNumberOfEpochs();
    int[] getClusterIndices(int aClusterNumber);
    float getAngleBetweenClusters(int aFirstCluster, int aSecondCluster);
    int getClusterRepresentatives(int aClusterNumber);
    ConcurrentLinkedQueue<String> getProcessLog();
    ConcurrentLinkedQueue<String> getResultLog();
}
