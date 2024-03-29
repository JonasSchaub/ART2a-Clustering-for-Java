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

package de.unijena.cheminf.clustering.art2a.results;

import de.unijena.cheminf.clustering.art2a.abstractResult.Art2aAbstractResult;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Result class for float clustering.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class Art2aFloatClusteringResult extends Art2aAbstractResult {
    //<editor-fold desc="Private class variables" defaultstate="collapsed">
    /**
     * Cache for cluster representatives.
     */
    private int[] cacheClusterRepresentativesIndices;
    /**
     * Cache for cluster angles.
     */
    private float[][] cacheAngleBetweenClusters;
    //</editor-fold>
    //
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
    /**
     * The vigilance parameter is between 0 and 1. The parameter influences the type of clustering.
     * A vigilance parameter close to 0 leads to a coarse clustering (few clusters) and a vigilance
     * parameter close to 1, on the other hand, leads to a fine clustering (many clusters).
     */
    private final float vigilanceParameter;
    //</editor-fold>
    //
    //<editor-fold desc="Private final static class variables" defaultstate="collapsed">
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(Art2aFloatClusteringResult.class.getName());
    //</editor-fold>
    //
    //<editor-fold desc="Constructors" defaultstate="collapsed">
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusteringProcessQueue clustering result (process) queue of typ String.
     * The queue is required to be able to export the cluster results. If it is not specified, they are set to null and
     * export is not possible.
     * @param aClusteringResultQueue clustering result queue of typ String. See {@code #aClusteringProcessQueue}
     * @param aClusterView array for cluster assignment of each input vector.
     * @param aClusterMatrix float cluster vector matrix. All cluster vectors created after float ART-2a clustering are
     * stored in this matrix.
     * @param aDataMatrix float matrix with all input vectors/fingerprints.
     * Each row in the matrix corresponds to an input vector.
     * @throws NullPointerException is thrown, if the specified matrices are null.
     * @throws IllegalArgumentException is thrown, if the specified vigilance parameter is invalid.
     *
     */
    public Art2aFloatClusteringResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters,
                                      int[] aClusterView, float[][] aClusterMatrix, float[][] aDataMatrix,
                                      ConcurrentLinkedQueue<String> aClusteringProcessQueue,
                                      ConcurrentLinkedQueue<String> aClusteringResultQueue)
            throws NullPointerException, IllegalArgumentException {
        super(aNumberOfEpochs, aNumberOfDetectedClusters, aClusterView, aClusteringProcessQueue, aClusteringResultQueue);
        Objects.requireNonNull(aClusterMatrix, "aClusterMatrix is null.");
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        if (aVigilanceParameter <= 0.0f || aVigilanceParameter >= 1.0f) {
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0 and smaller than 1.");
        }
        this.vigilanceParameter = aVigilanceParameter;
        this.floatClusterMatrix = aClusterMatrix;
        this.dataMatrix = aDataMatrix;
        this.cacheClusterRepresentativesIndices = new int[aNumberOfDetectedClusters];
        Arrays.fill(this.cacheClusterRepresentativesIndices, -2);
        this.cacheAngleBetweenClusters = new float[aNumberOfDetectedClusters][aNumberOfDetectedClusters];
    }
    //
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusterView array for cluster assignment of each input vector.
     * @param aClusterMatrix float cluster vector matrix. All cluster vectors created after float Art-2a clustering are
     * stored in this matrix.
     * @param aDataMatrix float matrix with all input vectors/fingerprints.
     * Each row in the matrix corresponds to an input vector.
     * @throws NullPointerException is thrown, if the specified matrices are null.
     * @throws IllegalArgumentException is thrown, if the specified vigilance parameter is invalid.
     * <br><br>
     *
     *  @see de.unijena.cheminf.clustering.art2a.results.Art2aFloatClusteringResult#Art2aFloatClusteringResult(float,
     *  int, int, int[], float[][], float[][],ConcurrentLinkedQueue, ConcurrentLinkedQueue)
     */
    public Art2aFloatClusteringResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters,
                                      int[] aClusterView, float[][] aClusterMatrix, float[][] aDataMatrix) throws NullPointerException {
        this(aVigilanceParameter, aNumberOfEpochs, aNumberOfDetectedClusters, aClusterView, aClusterMatrix, aDataMatrix,null, null);
    }
    //</editor-fold>
    //
    //<editor-fold desc="Overriden public methods" defaultstate="collapsed">
    /**
     * {@inheritDoc}
     */
    @Override
    public Float getVigilanceParameter() {
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
        if(this.cacheClusterRepresentativesIndices[aClusterNumber] == -2) {
            int[] tmpClusterIndices = this.getClusterIndices(aClusterNumber);
            float[] tmpCurrentClusterVector = this.floatClusterMatrix[aClusterNumber];
            float tmpFactor;
            float[] tmpMatrixRow;
            float[] tmpScalarProductArray = new float[tmpClusterIndices.length + 1];
            int tmpIterator = 0;
            for (int tmpCurrentInput : tmpClusterIndices) {
                tmpMatrixRow = this.dataMatrix[tmpCurrentInput];
                tmpFactor = 0.0f;
                for (int i = 0; i < tmpMatrixRow.length; i++) {
                    tmpFactor += tmpMatrixRow[i] * tmpCurrentClusterVector[i];
                }
                tmpScalarProductArray[tmpIterator] = tmpFactor;
                tmpIterator++;
            }
            int tmpIndexOfGreatestScalarProduct = 0;
            for (int i = 0; i < tmpScalarProductArray.length; i++) {
                if (tmpScalarProductArray[i] > tmpScalarProductArray[tmpIndexOfGreatestScalarProduct]) {
                    tmpIndexOfGreatestScalarProduct = i;
                }
            }
            this.cacheClusterRepresentativesIndices[aClusterNumber] = tmpClusterIndices[tmpIndexOfGreatestScalarProduct];
            return tmpClusterIndices[tmpIndexOfGreatestScalarProduct];
        } else {
            return this.cacheClusterRepresentativesIndices[aClusterNumber];
        }
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public Float getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException {
        if(aFirstCluster < 0 || aSecondCluster < 0) {
            throw new IllegalArgumentException("The given cluster number is negative/invalid.");
        }
        int tmpNumberOfDetectedCluster = this.getNumberOfDetectedClusters();
        float tmpAngle;
        if(aFirstCluster == aSecondCluster && (aFirstCluster >= tmpNumberOfDetectedCluster)) {
            throw new IllegalArgumentException("The given cluster number(s) do(es) not exist");
        } else if (aFirstCluster == aSecondCluster) {
           return 0.0f;
        } else {
            if (aFirstCluster >= tmpNumberOfDetectedCluster || aSecondCluster >= tmpNumberOfDetectedCluster) {
                throw new IllegalArgumentException("The given cluster number does not exist.");
            }
            if(this.cacheAngleBetweenClusters[aFirstCluster] [aSecondCluster] == 0) {
                float[] tmpFirstCluster = this.floatClusterMatrix[aFirstCluster];
                float[] tmpSecondCluster = this.floatClusterMatrix[aSecondCluster];
                float tmpFactor = (float) (180.0 / Math.PI);
                float tmpProduct = 0.0f;
                for (int i = 0; i < tmpFirstCluster.length; i++) {
                    tmpProduct += tmpFirstCluster[i] * tmpSecondCluster[i];
                }
                tmpAngle = (float) (tmpFactor * Math.acos(tmpProduct));
                this.cacheAngleBetweenClusters[aFirstCluster][aSecondCluster] = tmpAngle;
                this.cacheAngleBetweenClusters[aSecondCluster][aFirstCluster] = tmpAngle;
                return tmpAngle;
            } else {
                return this.cacheAngleBetweenClusters[aFirstCluster][aSecondCluster];
            }
        }
    }
    //</editor-fold>
}
