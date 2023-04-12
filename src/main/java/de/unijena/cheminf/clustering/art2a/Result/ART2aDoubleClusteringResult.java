package de.unijena.cheminf.clustering.art2a.Result;

import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClusteringResult;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ART2aDoubleClusteringResult implements IART2aClusteringResult {
    private final int[] clusterView;
    private  double[][] doubleClusterMatrix;
    private final int numberOfEpochs;
    private final int numberOfDetectedClusters;
    private final float vigilanceParameter;
    private ConcurrentLinkedQueue<String> processLog;
    private  ConcurrentLinkedQueue<String> resultLog;

    public ART2aDoubleClusteringResult(float aVigilanceParameter, double[][] aClusterMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters) {
        this.vigilanceParameter = aVigilanceParameter;
        this.doubleClusterMatrix = aClusterMatrix;
        this.clusterView = aClusterView;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
    }
    public ART2aDoubleClusteringResult(
            float aVigilanceParameter, double[][] aClusterMatrix, int[] aClusterView,
            int aNumberOfEpochs,int aNumberOfDetectedClusters,ConcurrentLinkedQueue<String> processLog,
            ConcurrentLinkedQueue<String>resultLog) {
        this.vigilanceParameter = aVigilanceParameter;
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

    @Override
    public int getNumberOfDetectedClusters() {
        return this.numberOfDetectedClusters;
    }

    @Override
    public int getNumberOfEpochs() {
        return this.numberOfEpochs;
    }

    @Override
    public int[] getClusterIndices(int aClusterNumber) {
        if (aClusterNumber > this.numberOfDetectedClusters) {
            throw new IllegalArgumentException("The specified cluster number does not exist and exceeds the maximum number of clusters.");
        } else {
            System.out.println(this.getClusterMembers(this.clusterView).get(aClusterNumber) + "----Array size");
            int[] tmpIndicesInCluster = new int[this.getClusterMembers(this.clusterView).get(aClusterNumber)];
            int i = 0;
            int in = 0;
            for (int tmpClusterMember : this.clusterView) {
                if (tmpClusterMember == aClusterNumber) {
                    tmpIndicesInCluster[in] = i;
                    in++;
                }
                i++;


            }
            System.out.println(java.util.Arrays.toString(tmpIndicesInCluster));
            return tmpIndicesInCluster;
        }
    }

    @Override
    public float getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) {
        // TODO parameter check
        double[] tmpFirstCluster = this.doubleClusterMatrix[aFirstCluster]; // TODO ensure that the clusterMatrix represent the vectors of clusters in the right order
        System.out.println(java.util.Arrays.toString(tmpFirstCluster)+ "---first clsuter vector");
        double[] tmpSecondCluster = this.doubleClusterMatrix[aSecondCluster];
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

    @Override
    public int getClusterRepresentatives(int aClusterNumber) {
        return 0;
    }

    @Override
    public ConcurrentLinkedQueue<String> getProcessLog() {
        return this.processLog;
    }

    @Override
    public ConcurrentLinkedQueue<String> getResultLog() {
        return this.resultLog;
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
}
