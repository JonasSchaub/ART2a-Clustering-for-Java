package de.unijena.cheminf.clustering.art2a;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ART2aClusteringResult implements IART2aClusteringResult {
    private final int[] clusterView;
    private  float[][] clusterMatrix;
    private  double[][] doubleClusterMatrix;
    private final int numberOfEpochs;
    private final int numberOfDetectedClusters;
    private ConcurrentLinkedQueue<String> processLog;
    private  ConcurrentLinkedQueue<String> resultLog;
    public ART2aClusteringResult(float[][] aClusterMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters) {
        this.clusterMatrix = aClusterMatrix;
        this.clusterView = aClusterView;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
    }
    public ART2aClusteringResult(float[][] aClusterMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters,ConcurrentLinkedQueue<String> processLog, ConcurrentLinkedQueue<String>resultLog){
        this.clusterMatrix = aClusterMatrix;
        this.clusterView = aClusterView;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.processLog = processLog;
        this.resultLog = resultLog;
    }
    public ART2aClusteringResult(double[][] aClusterMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters) {
        this.doubleClusterMatrix = aClusterMatrix;
        this.clusterView = aClusterView;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
    }
    public ART2aClusteringResult(double[][] aClusterMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters,ConcurrentLinkedQueue<String> processLog, ConcurrentLinkedQueue<String>resultLog){
        this.doubleClusterMatrix = aClusterMatrix;
        this.clusterView = aClusterView;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.processLog = processLog;
        this.resultLog = resultLog;
    }

    @Override
    public float getVigilanceParameter() {
        return 0;
    }

    public int getNumberOfDetectedClusters() {
        return this.numberOfDetectedClusters;
    }

    @Override
    public int getNumberOfEpochs() {
        return 0;
    }

    public int[] getClusterIndices(int aNumberOfCluster) {
        if (aNumberOfCluster > this.numberOfDetectedClusters) {
            throw new IllegalArgumentException("The specified cluster number does not exist and exceeds the maximum number of clusters.");
        } else {
            System.out.println(this.getClusterMembers(this.clusterView).get(aNumberOfCluster) + "----Array size");
            int[] tmpIndicesInCluster = new int[this.getClusterMembers(this.clusterView).get(aNumberOfCluster)];
            int i = 0;
            int in = 0;
            for (int tmpClusterMember : this.clusterView) {
                if (tmpClusterMember == aNumberOfCluster) {
                    tmpIndicesInCluster[in] = i;
                    in++;
                }
                i++;


            }
            System.out.println(java.util.Arrays.toString(tmpIndicesInCluster));
            return tmpIndicesInCluster;
        }
    }
    private HashMap<Integer, Integer> getClusterMembers(int[] aClusterView) {
        HashMap<Integer, Integer> tmpClusterToMembersMap = new HashMap<>(this.getNumberOfDetectedClusters());
        int i = 1;
        for(int tmpClusterMembers : aClusterView) {
            if (tmpClusterMembers == -1) {
                continue;
            }
            if(!tmpClusterToMembersMap.containsKey(tmpClusterMembers)) {
                tmpClusterToMembersMap.put(tmpClusterMembers, i);
            } else {
                tmpClusterToMembersMap.put(tmpClusterMembers, tmpClusterToMembersMap.get(tmpClusterMembers) + 1);
            }
        }
        return tmpClusterToMembersMap;
    }
    public float getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) {
        // TODO parameter check
        float[] tmpFirstCluster = this.clusterMatrix[aFirstCluster]; // TODO ensure that the clusterMatrix represent the vectors of clusters in the right order
        System.out.println(java.util.Arrays.toString(tmpFirstCluster)+ "---first clsuter vector");
        float[] tmpSecondCluster = this.clusterMatrix[aSecondCluster];
        System.out.println(java.util.Arrays.toString(tmpSecondCluster)+ "---second clsuter vector");
        float factor = (float) (180 / Math.PI);
        float product = 0;
        for(int i = 0; i<tmpFirstCluster.length; i++) {
            product += tmpFirstCluster[i] * tmpSecondCluster[i];
        }
        float tmpAngle = (float) (factor * Math.acos(product));
        System.out.println(tmpAngle);
        return tmpAngle;
    }
    public ConcurrentLinkedQueue<String> getProcessLog() {
        return this.processLog;
    }
    public ConcurrentLinkedQueue<String> getResultLog() {
        return this.resultLog;
    }
}
