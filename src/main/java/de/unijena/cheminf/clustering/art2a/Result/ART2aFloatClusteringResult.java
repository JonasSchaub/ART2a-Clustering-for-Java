package de.unijena.cheminf.clustering.art2a.Result;

import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClusteringResult;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ART2aFloatClusteringResult implements IART2aClusteringResult {
    private final int[] clusterView;
    private  float[][] clusterMatrix;
    private  double[][] doubleClusterMatrix;
    private float[][] dataMatrix;
    private final int numberOfEpochs;
    private final int numberOfDetectedClusters;
    private ConcurrentLinkedQueue<String> processLog;
    private  ConcurrentLinkedQueue<String> resultLog;

    public ART2aFloatClusteringResult(float[][] aClusterMatrix, float[][] aDataMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters) {
        this.clusterMatrix = aClusterMatrix;
        this.dataMatrix = aDataMatrix;
        this.clusterView = aClusterView;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
    }
    public ART2aFloatClusteringResult(float[][] aClusterMatrix,float[][] aDataMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters,ConcurrentLinkedQueue<String> processLog, ConcurrentLinkedQueue<String>resultLog){
        this.clusterMatrix = aClusterMatrix;
        this.dataMatrix =  aDataMatrix;
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
        return 0;
    }

    @Override
    public int getNumberOfEpochs() {
        return 0;
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
            return tmpIndicesInCluster;
        }
    }

    @Override
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

    @Override
    public int getClusterRepresentatives(int aClusterNumber) {
       int[] tmpClusterIndices =  this.getClusterIndices(aClusterNumber);
       float[] tmpCurrentClusterVector = this.clusterMatrix[aClusterNumber];
        System.out.println(java.util.Arrays.toString(tmpCurrentClusterVector) + "---cluster vector");
       float factor = 0;
       float[] tmpRaw;
       float[] tmpProduct  = new float[tmpClusterIndices.length];
        System.out.println(tmpProduct.length + "----length");
       int[] tmpSecondArray = new int[tmpClusterIndices.length];
       int t = 0;
       for(int tmpCurrentInput : tmpClusterIndices) {
           System.out.println(tmpCurrentInput + "----current input");
           tmpRaw = this.dataMatrix[tmpCurrentInput];
           System.out.println(java.util.Arrays.toString(tmpRaw)+ "----tmpRaw");
           for(int i = 0; i <tmpRaw.length; i++) {
               factor += tmpRaw[i] * tmpCurrentClusterVector[i];
           }
           System.out.println(factor + "----factor");
           System.out.println(t + "---t");
           tmpSecondArray[t] = tmpCurrentInput;
           tmpProduct[t] = factor;
           t++;
       }
        System.out.println("beginn");
        System.out.println(java.util.Arrays.toString(tmpProduct) + "----product");
        float tmpFirstComponent = tmpProduct[0];
        int z = 0;
        for (float tmpComponentsOfScalingVector : tmpProduct) {
            if (tmpComponentsOfScalingVector > tmpFirstComponent) {
                z++;
                System.out.println(z + "----z");
                tmpFirstComponent = tmpComponentsOfScalingVector;
                System.out.println(tmpFirstComponent + "-----reprsentent");
            }
        }
        System.out.println(java.util.Arrays.toString(tmpSecondArray)+"----second array");
        System.out.println(tmpSecondArray[z]);
        return tmpSecondArray[z];
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
