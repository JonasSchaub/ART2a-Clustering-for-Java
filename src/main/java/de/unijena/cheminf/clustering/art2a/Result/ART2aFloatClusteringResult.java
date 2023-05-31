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
import de.unijena.cheminf.clustering.art2a.Clustering.ART2aFloatClustering;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Result class for float clustering.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class ART2aFloatClusteringResult extends ART2aAbstractResult  {
    //<editor-fold desc="Private final class variables" defaultstate="collapsed">
    /**
     * Matrix contains all cluster vectors.
     */
    private final float[][] floatClusterMatrix;
    /**
     * Matrix contains all input vector/fingerprints to be clustered.
     * Each row in the matrix corresponds to an input vector.
     */
    private final float[][] dataMatrix;
    //</editor-fold>
    //<editor-fold desc="Private final static class variables" defaultstate="collapsed">
    private static final Logger LOGGER = Logger.getLogger(ART2aFloatClusteringResult.class.getName());
    //</editor-fold>
    //
    //<editor-fold desc="Constructors" defaultstate="collapsed">
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusterView array for cluster assignment of each input vector.
     * @param aClusterMatrix float cluster vector matrix. All cluster vectors created after float ART-2a clustering are
     *                       stored in this matrix.
     * @param aDataMatrix float matrix with all input vectors/fingerprints.
     *                    Each row in the matrix corresponds to an input vector.
     */
    public ART2aFloatClusteringResult(float aVigilanceParameter,int aNumberOfEpochs, int aNumberOfDetectedClusters,
                                      int[] aClusterView, boolean aConvergenceStatus, float[][] aClusterMatrix, float[][] aDataMatrix) {
        super(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters, aClusterView, aConvergenceStatus);
        Objects.requireNonNull(aClusterMatrix, "aClusterMatrix is null.");
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        this.floatClusterMatrix = aClusterMatrix;
        this.dataMatrix = aDataMatrix;
    }
    //
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusteringProcessQueue clustering result (process) queue.
     * @param aClusteringResultQueue clustering result queue.
     * @param aClusterView array for cluster assignment of each input vector.
     * @param aClusterMatrix float cluster vector matrix. All cluster vectors created after float ART-2a clustering are
     *                       stored in this matrix.
     * @param aDataMatrix float matrix with all input vectors/fingerprints.
     *                    Each row in the matrix corresponds to an input vector.
     *
     */
    public ART2aFloatClusteringResult(float aVigilanceParameter,int aNumberOfEpochs, int aNumberOfDetectedClusters, ConcurrentLinkedQueue<String> aClusteringProcessQueue,
                                      ConcurrentLinkedQueue<String> aClusteringResultQueue, int[] aClusterView, boolean aConvergenceStatus, float[][] aClusterMatrix, float[][] aDataMatrix) {
        super(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters, aClusteringProcessQueue, aClusteringResultQueue, aClusterView, aConvergenceStatus);
        Objects.requireNonNull(aClusterMatrix, "aClusterMatrix is null.");
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        this.floatClusterMatrix = aClusterMatrix;
        this.dataMatrix = aDataMatrix;
    }
    //</editor-fold>
    //
    //<editor-fold desc="Overriden public methods" defaultstate="collapsed">
    /**
     * {@inheritDoc}
     */
    @Override
    public int getClusterRepresentatives(int aClusterNumber) throws IllegalArgumentException {
        if(aClusterNumber > this.getNumberOfDetectedClusters() || aClusterNumber < 0) {
            ART2aFloatClusteringResult.LOGGER.log(Level.SEVERE, "The given cluster number does not exist or is invalid.");
            throw new IllegalArgumentException("The given cluster number does not exist or is invalid.");
        }
        int[] tmpClusterIndices =  this.getClusterIndices(aClusterNumber);
        float[] tmpCurrentClusterVector = this.floatClusterMatrix[aClusterNumber];
        System.out.println(java.util.Arrays.toString(tmpCurrentClusterVector) + "---cluster vector");
        float tmpFactor = 0;
        float[] tmpMatrixRow;
        float[] tmpScalarProductArray = new float[tmpClusterIndices.length+1];
        int tmpIterator = 0;
        for(int tmpCurrentInput : tmpClusterIndices) {
            System.out.println(tmpCurrentInput + "----current input");
            tmpMatrixRow = this.dataMatrix[tmpCurrentInput];
            System.out.println(java.util.Arrays.toString(tmpMatrixRow)+ "----tmpRaw");
            for(int i = 0; i < tmpMatrixRow.length; i++) {
                tmpFactor += tmpMatrixRow[i] * tmpCurrentClusterVector[i];
            }
            System.out.println(tmpFactor + "----factor");
            tmpScalarProductArray[tmpIterator] = tmpFactor;
            tmpIterator++;
        }
        int tmpIndexOfGreatestScalarProduct = 0;
        System.out.println(java.util.Arrays.toString(tmpScalarProductArray) + "----product");
        for(int i = 0; i < tmpScalarProductArray.length; i++) {
            if(tmpScalarProductArray[i] > tmpScalarProductArray[tmpIndexOfGreatestScalarProduct]) {
                tmpIndexOfGreatestScalarProduct = i;
            }
        }
        return tmpClusterIndices[tmpIndexOfGreatestScalarProduct];
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public Float getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException {
        if(aFirstCluster < 0 || aSecondCluster < 0) {
            ART2aFloatClusteringResult.LOGGER.log(Level.SEVERE, "The given cluster number is negative/invalid.");
            throw new IllegalArgumentException("The given cluster number is negative/invalid.");
        }
        float tmpAngle;
        if(aFirstCluster == aSecondCluster) {
            tmpAngle = 0;
        } else {
            int tmpNumberOfDetectedCluster = this.getNumberOfDetectedClusters() - 1;
            if (aFirstCluster > tmpNumberOfDetectedCluster || aSecondCluster > tmpNumberOfDetectedCluster) {
                ART2aFloatClusteringResult.LOGGER.log(Level.SEVERE, "The given cluster number does not exist.");
                throw new IllegalArgumentException("The given cluster number does not exist.");
            }
            float[] tmpFirstCluster = this.floatClusterMatrix[aFirstCluster];// TODO ensure that the clusterMatrix represent the vectors of clusters in the right order
            System.out.println(java.util.Arrays.toString(tmpFirstCluster) + "---first clsuter vector");
            float[] tmpSecondCluster = this.floatClusterMatrix[aSecondCluster];
            System.out.println(java.util.Arrays.toString(tmpSecondCluster) + "---second clsuter vector");
            float factor = (float) (180 / Math.PI);
            float product = 0;
            for (int i = 0; i < tmpFirstCluster.length; i++) {
                product += tmpFirstCluster[i] * tmpSecondCluster[i];
            }
            tmpAngle = (float) (factor * Math.acos(product));
            System.out.println(tmpAngle);
        }
        return tmpAngle;
    }
}
