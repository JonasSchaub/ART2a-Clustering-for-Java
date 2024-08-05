/*
 * ART2a Clustering for Java
 * Copyright (C) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
 *
 * Source code is available at <https://github.com/JonasSchaub/ART2a-Clustering-for-Java>
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

package de.unijena.cheminf.clustering.art2a.results.euclideanClusteringResult;

import de.unijena.cheminf.clustering.art2a.abstractResult.euclideanClusteringAbstractResult.Art2aEuclideanAbstractResult;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Result class for Euclidean double clustering.
 *
 * @author Zeynep Dagtekin, based on Betuel Sevindik's result class.
 * @version 1.0.0.0
 */
public class Art2aEuclideanDoubleClusteringResult extends Art2aEuclideanAbstractResult {
    /**
     * Cache for cluster representatives.
     */
    private int[] cacheClusterRepresentativesIndices;
    /**
     * Cache for cluster distances.
     */
    private double[][] cacheDistanceBetweenClusters;
    //</editor-fold>
    //
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
    /**
     * The vigilance parameter is above 0. The parameter influences the sensitivity of the clustering.
     * A vigilance parameter close to 0 leads to a coarse clustering (few clusters).
     */
    private final double vigilanceParameter;
    //
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(Art2aEuclideanDoubleClusteringResult.class.getName());
    /**
     * Constructor.
     *
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusteringProcessQueue clustering result (process) queue of ty String.
     * The queue is required to be able to export the cluster results. If it is not specified, they are set to null and
     * export is not possible.
     * @param aClusteringResultQueue clustering result queue of typ String. See {@code #aClusteringProcessQueue}
     * @param aClusterView array for cluster assignment of each input vector.
     * @param aClusterMatrix cluster vector matrix. All cluster vectors created after double ART-2a clustering are
     * stored in this matrix.
     * @param aDataMatrix matrix with all input vectors/fingerprints.
     * Each row in the matrix corresponds to an input vector.
     * @throws NullPointerException is thrown, if the specified matrices are null.
     * @throws IllegalArgumentException is thrown, if the specified vigilance parameter is invalid.
     *
     */
    public Art2aEuclideanDoubleClusteringResult(double aVigilanceParameter, int aNumberOfEpochs,
                                                int aNumberOfDetectedClusters, int[] aClusterView,
                                                double[][] aClusterMatrix, double [][] aDataMatrix,
                                                ConcurrentLinkedQueue<String> aClusteringProcessQueue,
                                                ConcurrentLinkedQueue<String> aClusteringResultQueue)
            throws NullPointerException, IllegalArgumentException {
        super (aNumberOfEpochs, aNumberOfDetectedClusters, aClusterView, aClusteringProcessQueue, aClusteringResultQueue);
        Objects.requireNonNull(aClusterMatrix, "aClusterMatrix is null.");
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        if (aVigilanceParameter <= 0.0) {
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0.");
        }
        this.vigilanceParameter = aVigilanceParameter;
        this.doubleClusterMatrix = aClusterMatrix;
        this.dataMatrix = aDataMatrix;
        this.cacheClusterRepresentativesIndices = new int[aNumberOfDetectedClusters];
        Arrays.fill(this.cacheClusterRepresentativesIndices, -2);
        this.cacheDistanceBetweenClusters = new double[aNumberOfDetectedClusters][aNumberOfDetectedClusters];

    }
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusterView array for cluster assignment of each input vector.
     * @param aClusterMatrix double cluster vector matrix. All cluster vectors created after double ART-2a clustering are
     * stored in this matrix.
     * @param aDataMatrix double matrix with all input vectors/fingerprints.
     * Each row in the matrix corresponds to an input vector.
     * @throws NullPointerException is thrown, if the specified matrices are null.
     * @throws IllegalArgumentException is thrown, if the specified vigilance parameter is invalid.
     * <br><br>
     *
     * @see de.unijena.cheminf.clustering.art2a.results.Art2aDoubleClusteringResult#Art2aDoubleClusteringResult(double,
     * int, int, int[], double[][], double[][], ConcurrentLinkedQueue, ConcurrentLinkedQueue)
     *
     */
    public Art2aEuclideanDoubleClusteringResult(double aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters,
                                                int[] aClusterView, double[][] aClusterMatrix, double [][] aDataMatrix)
        throws NullPointerException {
        this(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters, aClusterView, aClusterMatrix, aDataMatrix,
                null, null);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Double getVigilanceParameter() {
        return this.vigilanceParameter;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getClusterRepresentatives(int aClusterNumber) throws IllegalArgumentException {
        if(aClusterNumber >= this.getNumberOfDetectedClusters() || aClusterNumber < 0) {
            throw new IllegalArgumentException("The given cluster number does not exist or is invalid.");
        }
        if (this.cacheClusterRepresentativesIndices[aClusterNumber] == -2) {
            int[] tmpClusterIndices = this.getClusterIndices(aClusterNumber);
            double[] tmpCurrentClusterVector = this.doubleClusterMatrix[aClusterNumber];
            double tmpDifference;
            double tmpDistance;
            double[] tmpMatrixRow;
            double [] tmpEuclideanDistanceArray = new double[tmpClusterIndices.length + 1];
            int tmpIterator = 0;
            for (int tmpCurrentClusterVectorIndex = 0; tmpCurrentClusterVectorIndex < aClusterNumber; tmpCurrentClusterVectorIndex++) {
                tmpMatrixRow = this.dataMatrix[tmpCurrentClusterVectorIndex];
                tmpDistance = 0.0;
                for (int i = 0; i < tmpMatrixRow.length; i++) {
                    tmpDifference = tmpMatrixRow[i] - tmpCurrentClusterVector[i];
                    tmpDistance += tmpDifference * tmpDifference;
                }
                tmpEuclideanDistanceArray[tmpIterator] = tmpDistance;
                tmpIterator++;
            }
            int tmpIndexOfGreatestDistance = 0;
            for (int i = 0; i < tmpEuclideanDistanceArray.length; i++) {
                if (tmpEuclideanDistanceArray[i] > tmpEuclideanDistanceArray[tmpIndexOfGreatestDistance]) {
                    tmpIndexOfGreatestDistance = i;
                }
            }
            this.cacheClusterRepresentativesIndices[aClusterNumber] = tmpClusterIndices[tmpIndexOfGreatestDistance];
            return tmpClusterIndices[tmpIndexOfGreatestDistance];
        } else {
            return this.cacheClusterRepresentativesIndices[aClusterNumber];
        }
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDistanceBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException {
        if(aFirstCluster < 0 || aSecondCluster < 0) {
            throw new IllegalArgumentException("The given cluster number is less than zero or invalid.");
        }
        int tmpNumberOfDetectedCluster = this.getNumberOfDetectedClusters();
        if(aFirstCluster == aSecondCluster && (aFirstCluster >= tmpNumberOfDetectedCluster)) {
            throw new IllegalArgumentException("The given cluster number(s) do(es) not exist.");
        } else if (aFirstCluster == aSecondCluster) {
            return 0.0;
        } else {
            if (aFirstCluster >= tmpNumberOfDetectedCluster || aSecondCluster>= tmpNumberOfDetectedCluster) {
                throw new IllegalArgumentException("The given cluster number(s) do(es) not exist.");
            }
            if(this.cacheDistanceBetweenClusters[aFirstCluster] [aSecondCluster] == 0) {
                double[] tmpFirstCluster = this.doubleClusterMatrix[aFirstCluster];
                double[] tmpSecondCluster = this.doubleClusterMatrix[aSecondCluster];
                double tmpDifferenceBetweenClusters;
                double tmpDistanceBetweenClusters = 0.0;
                for (int tmpFirstClusterVectorIndex = 0; tmpFirstClusterVectorIndex < tmpFirstCluster.length; tmpFirstClusterVectorIndex++) {

                    tmpDifferenceBetweenClusters = tmpSecondCluster[tmpFirstClusterVectorIndex] - tmpFirstCluster[tmpFirstClusterVectorIndex];
                    tmpDistanceBetweenClusters = tmpDifferenceBetweenClusters * tmpDifferenceBetweenClusters;
                }
                this.cacheDistanceBetweenClusters[aFirstCluster][aSecondCluster] = tmpDistanceBetweenClusters;
                this.cacheDistanceBetweenClusters[aSecondCluster][aFirstCluster] = tmpDistanceBetweenClusters;
                return tmpDistanceBetweenClusters;
            } else {
                return this.cacheDistanceBetweenClusters[aFirstCluster][aSecondCluster];
            }
        }

    }
    //
}
