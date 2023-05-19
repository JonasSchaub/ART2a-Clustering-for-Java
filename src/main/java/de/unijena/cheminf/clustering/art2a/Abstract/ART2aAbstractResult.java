package de.unijena.cheminf.clustering.art2a.Abstract;

import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClusteringResult;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ART2aAbstractResult<T> implements IART2aClusteringResult {
    private final int[] clusterView;
    private float vigilanceParameter;
    private final int numberOfEpochs;
    private final int numberOfDetectedClusters;
    private ConcurrentLinkedQueue<String> processLog;
    private  ConcurrentLinkedQueue<String> resultLog;

    public ART2aAbstractResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters, int[] aClusterView) {
        this.vigilanceParameter = aVigilanceParameter;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.clusterView = aClusterView;
    }
    public ART2aAbstractResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters,ConcurrentLinkedQueue<String> processLog, ConcurrentLinkedQueue<String>resultLog, int[] aClusterView) {
        this.clusterView = aClusterView;
        this.vigilanceParameter = aVigilanceParameter;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.processLog = processLog;
        this.resultLog = resultLog;
    }

    @Override
    public float getVigilanceParameter() {
        return this.vigilanceParameter;
    }

    @Override
    public int getNumberOfEpochs() {
        return this.numberOfEpochs;
    }

    @Override
    public int getNumberOfDetectedClusters() {
        return this.numberOfDetectedClusters;
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
    public ConcurrentLinkedQueue<String> getProcessLog() {
        return this.processLog;
    }
    @Override
    public ConcurrentLinkedQueue<String> getResultLog() {
        return this.resultLog;
    }
    public abstract int getClusterRepresentatives(int aClusterNumber);
    public abstract T getAngleBetweenClusters(int aFirstCluster, int aSecondCluster);

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
        System.out.println(tmpClusterToMembersMap +"------cluster members");
        return tmpClusterToMembersMap;
    }
}
