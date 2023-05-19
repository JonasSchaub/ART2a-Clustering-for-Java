package de.unijena.cheminf.clustering.art2a.Result;

import de.unijena.cheminf.clustering.art2a.Abstract.ART2aAbstractResult;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ART2aDoubleClusteringResult extends ART2aAbstractResult {

    private  double[][] doubleClusterMatrix;
    private ConcurrentLinkedQueue<String> processLog;
    private  ConcurrentLinkedQueue<String> resultLog;

    public ART2aDoubleClusteringResult(float aVigilanceParameter, double[][] aClusterMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters) {
        super( aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters,aClusterView);
        this.doubleClusterMatrix = aClusterMatrix;


    }
    public ART2aDoubleClusteringResult( float aVigilanceParameter, double[][] aClusterMatrix, int[] aClusterView,
                                        int aNumberOfEpochs,int aNumberOfDetectedClusters,ConcurrentLinkedQueue<String> aProcessLog,
                                        ConcurrentLinkedQueue<String> aResultLog) {
        super(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters, aProcessLog, aResultLog,aClusterView);
        this.doubleClusterMatrix = aClusterMatrix;
        this.processLog = aProcessLog;
        this.resultLog = aResultLog;

    }

    @Override
    public int getClusterRepresentatives(int aClusterNumber) {
        int[] tmpClusterIndices =  this.getClusterIndices(aClusterNumber);
        double[] tmpCurrentClusterVector = this.doubleClusterMatrix[aClusterNumber];
        System.out.println(java.util.Arrays.toString(tmpCurrentClusterVector) + "---cluster vector");
        double factor = 0;
        double[] tmpRaw;
        double[] tmpProduct  = new double[tmpClusterIndices.length];
        System.out.println(tmpProduct.length + "----length");
        int[] tmpSecondArray = new int[tmpClusterIndices.length];
        int t = 0;
        for(int tmpCurrentInput : tmpClusterIndices) {
            System.out.println(tmpCurrentInput + "----current input");
            tmpRaw = this.doubleClusterMatrix[tmpCurrentInput];
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
        double tmpFirstComponent = tmpProduct[0];
        int z = 0;
        for (double tmpComponentsOfScalingVector : tmpProduct) {
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
    public Double getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) {
        // TODO parameter check
        double[] tmpFirstCluster = this.doubleClusterMatrix[aFirstCluster]; // TODO ensure that the clusterMatrix represent the vectors of clusters in the right order
        System.out.println(java.util.Arrays.toString(tmpFirstCluster)+ "---first clsuter vector");
        double[] tmpSecondCluster = this.doubleClusterMatrix[aSecondCluster];
        System.out.println(java.util.Arrays.toString(tmpSecondCluster)+ "---second clsuter vector");
        double factor = 180.0 / Math.PI;
        double product = 0;
        for(int i = 0; i<tmpFirstCluster.length; i++) {
            product += tmpFirstCluster[i] * tmpSecondCluster[i];
        }
        System.out.println(tmpFirstCluster.length + "----length");
        System.out.println(product + "-----skalarprodukt");
        double tmpAngle =  factor * Math.acos(product);
        System.out.println(tmpAngle);
        return tmpAngle;
    }
}
