/*
 * GNU General Public License v3.0
 *
 * Copyright (c) 2022 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.unijena.cheminf.clustering.art2a.Result;

import de.unijena.cheminf.clustering.art2a.Abstract.ART2aAbstractResult;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Result class for the double clustering.
 */
public class ART2aDoubleClusteringResult extends ART2aAbstractResult {
    //<editor-fold desc="Private class variables" defaultstate="collapsed">
    /**
     * Queue for clustering result (process)
     */
    private ConcurrentLinkedQueue<String> processLog;
    /**
     *  Queue for clustering result
     */
    private ConcurrentLinkedQueue<String> resultLog;
    //</editor-fold>
    //
    //<editor-fold desc="Private final class variables" defaultstate="collapsed">
    /**
     * Matrix contains all cluster vectors
     */
    private final double[][] doubleClusterMatrix;
    //</editor-fold>
    //
    //<editor-fold desc="Constructors" defaultstate="collapsed">
    /**
     * Constructor.
     *
     * @param aVigilanceParameter
     * @param aClusterMatrix
     * @param aClusterView
     * @param aNumberOfEpochs
     * @param aNumberOfDetectedClusters
     */
    public ART2aDoubleClusteringResult(float aVigilanceParameter, double[][] aClusterMatrix, int[] aClusterView, int aNumberOfEpochs, int aNumberOfDetectedClusters) {
        super(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters,aClusterView);
        this.doubleClusterMatrix = aClusterMatrix;
    }

    /**
     * Constructor.
     *
     * @param aVigilanceParameter
     * @param aClusterMatrix
     * @param aClusterView
     * @param aNumberOfEpochs
     * @param aNumberOfDetectedClusters
     * @param aProcessLog
     * @param aResultLog
     */
    public ART2aDoubleClusteringResult( float aVigilanceParameter, double[][] aClusterMatrix, int[] aClusterView,
                                        int aNumberOfEpochs,int aNumberOfDetectedClusters,ConcurrentLinkedQueue<String> aProcessLog,
                                        ConcurrentLinkedQueue<String> aResultLog) {
        super(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters, aProcessLog, aResultLog,aClusterView);
        this.doubleClusterMatrix = aClusterMatrix;
        this.processLog = aProcessLog;
        this.resultLog = aResultLog;
    }
    //</editor-fold>
    //
    //<editor-fold desc="Overriden public methods" defaultstate="collapsed">
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
    //
    /**
     * @see ART2aAbstractResult
     */
    @Override
    public Double getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException { //
        if(aFirstCluster < 0 || aSecondCluster < 0) {
            throw new IllegalArgumentException("The given cluster number is negative/invalid.");
        }
        int tmpNumberOfDetectedCluster = this.getNumberOfDetectedClusters() -1;
        if(aFirstCluster > tmpNumberOfDetectedCluster || aSecondCluster > tmpNumberOfDetectedCluster) {
            throw new IllegalArgumentException("The given cluster number does not exist.");
        }
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
    //</editor-fold>
}
