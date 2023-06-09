/*
 * GNU General Public License v3.0
 *
 * Copyright (c) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
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

package de.unijena.cheminf.clustering.art2a.results;

import de.unijena.cheminf.clustering.art2a.abstractResult.ART2aAbstractResult;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Result class for the double clustering.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class ART2aDoubleClusteringResult extends ART2aAbstractResult {
    //<editor-fold desc="Private final class variables" defaultstate="collapsed">
    /**
     * Matrix contains all cluster vectors.
     */
    private final double[][] doubleClusterMatrix;
    /**
     * Matrix contains all input vector/fingerprints to be clustered.
     * Each row in the matrix corresponds to an input vector.
     */
    private final double[][] dataMatrix;
    //</editor-fold>
    //
    //<editor-fold desc="Private final static class variables" defaultstate="collapsed">
    private static final Logger LOGGER = Logger.getLogger(ART2aDoubleClusteringResult.class.getName());
    //</editor-fold>
    //
    //<editor-fold desc="Constructors" defaultstate="collapsed">
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusteringProcessQueue clustering result (process) queue.
     * @param aClusteringResultQueue clustering result queue.
     * @param aClusterView array for cluster assignment of each input vector.
     * @param aClusterMatrix cluster vector matrix. All cluster vectors created after double ART-2a clustering are
     *                       stored in this matrix.
     * @param aConvergenceStatus false, if the system has not converged within the specified maximum epoch,
     *                           otherwise it is true.
     * @param aDataMatrix matrix with all input vectors/fingerprints.
     *                    Each row in the matrix corresponds to an input vector.
     *
     */
    public ART2aDoubleClusteringResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters, ConcurrentLinkedQueue<String> aClusteringProcessQueue,
                                       ConcurrentLinkedQueue<String> aClusteringResultQueue, int[] aClusterView, boolean aConvergenceStatus,
                                       double[][] aClusterMatrix, double[][] aDataMatrix){
        super(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters,aClusterView, aConvergenceStatus,aClusteringProcessQueue, aClusteringResultQueue);
        Objects.requireNonNull(aClusterMatrix, "aClusterMatrix is null.");
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        this.doubleClusterMatrix = aClusterMatrix;
        this.dataMatrix = aDataMatrix;
    }
    //
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusterView array for cluster assignment of each input vector.
     * @param aConvergenceStatus false, if the system has not converged within the specified maximum epoch,
     *                           otherwise it is true.
     * @param aClusterMatrix double cluster vector matrix. All cluster vectors created after double ART-2a clustering are
     *                       stored in this matrix.
     * @param aDataMatrix double matrix with all input vectors/fingerprints.
     *                    Each row in the matrix corresponds to an input vector.
     */
    public ART2aDoubleClusteringResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters, int[] aClusterView, boolean aConvergenceStatus, double[][] aClusterMatrix, double[][] aDataMatrix) {
        this(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters, null, null, aClusterView,aConvergenceStatus, aClusterMatrix, aDataMatrix);
    }
    //</editor-fold>
    //
    //<editor-fold desc="Overriden public methods" defaultstate="collapsed">
    /**
     * {@inheritDoc}
     */
    @Override
    public int getClusterRepresentatives(int aClusterNumber) throws IllegalArgumentException {
        if(aClusterNumber >= this.getNumberOfDetectedClusters() || aClusterNumber < 0) {
            throw new IllegalArgumentException("The given cluster number does not exist or is invalid.");
        }
        int[] tmpClusterIndices =  this.getClusterIndices(aClusterNumber);
        double[] tmpCurrentClusterVector = this.doubleClusterMatrix[aClusterNumber];
        double tmpFactor = 0;
        double[] tmpMatrixRow;
        double[] tmpScalarProductArray = new double[tmpClusterIndices.length+1];
        int tmpIterator = 0;
        for(int tmpCurrentInput : tmpClusterIndices) {
            tmpMatrixRow = this.dataMatrix[tmpCurrentInput];
            for(int i = 0; i < tmpMatrixRow.length; i++) {
                tmpFactor += tmpMatrixRow[i] * tmpCurrentClusterVector[i];
            }
            tmpScalarProductArray[tmpIterator] = tmpFactor;
            tmpIterator++;
        }
        int tmpIndexOfGreatestScalarProduct = 0;
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
    public Double getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException {
        if(aFirstCluster < 0 || aSecondCluster < 0) {
            throw new IllegalArgumentException("The given cluster number is negative/invalid.");
        }
        int tmpNumberOfDetectedCluster = this.getNumberOfDetectedClusters();
        double tmpAngle;
        if(aFirstCluster == aSecondCluster && (aFirstCluster >= tmpNumberOfDetectedCluster || aSecondCluster >= tmpNumberOfDetectedCluster)) {
            throw new IllegalArgumentException("The given cluster number(s) do(es) not exist");
        } else if (aFirstCluster == aSecondCluster) {
            tmpAngle = 0;
        } else {
            if (aFirstCluster >= tmpNumberOfDetectedCluster || aSecondCluster >= tmpNumberOfDetectedCluster) {
                throw new IllegalArgumentException("The given cluster number(s) do(es) not exist.");
            }
            double[] tmpFirstCluster = this.doubleClusterMatrix[aFirstCluster];
            double[] tmpSecondCluster = this.doubleClusterMatrix[aSecondCluster];
            double tmpFactor = 180.0 / Math.PI;
            double tmpProduct = 0;
            for (int i = 0; i < tmpFirstCluster.length; i++) {
                tmpProduct += tmpFirstCluster[i] * tmpSecondCluster[i];
            }
            tmpAngle = tmpFactor * Math.acos(tmpProduct);
        }
        return tmpAngle;
    }
    //</editor-fold>
}
