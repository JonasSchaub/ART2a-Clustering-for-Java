/*
 * ART-2a-Euclid Clustering for Java
 * Copyright (C) 2025 Jonas Schaub, Betuel Sevindik, Achim Zielesny
 *
 * Source code is available at 
 * <https://github.com/JonasSchaub/ART2a-Clustering-for-Java>
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

package de.unijena.cheminf.clustering.art2a;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Result of an ART-2a-Euclid clustering process.
 * <br><br>
 * Note: Art2aEuclidResult is a read-only class, i.e. thread-safe. In addition, 
 * there are NO internal calculated values cached, i.e. each method call 
 * performs a full calculation procedure. An Art2aEuclidResult object may be 
 * distributed to several concurrent (parallelized) evaluation tasks without 
 * any mutual interference problems.
 *
 * @author Betuel Sevindik, Achim Zielesny
 */
public class Art2aEuclidResult {

    //<editor-fold desc="Private static final LOGGER">
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(Art2aEuclidResult.class.getName());
    //</editor-fold>
    //<editor-fold desc="Private final class variables">
    /**
     * Cluster index of data vector
     */
    private final int[] clusterIndexOfDataVector;
    /**
     * Vigilance parameter
     */
    private final float vigilance;
    /**
     * Threshold for contrast enhancement
     */
    private final float thresholdForContrastEnhancement;
    /**
     * Number of epochs
     */
    private final int numberOfEpochs;
    /**
     * Number of detected clusters
     */
    private final int numberOfDetectedClusters;
    /**
     * Cluster matrix
     */
    private final float[][] clusterMatrix;
    /**
     * Array with flags. True: Scaled data vector has a length of zero 
     * (corresponding contrast enhanced unit vector is set to null in this 
     * case), false: Otherwise
     */
    private final boolean[] dataVectorZeroLengthFlags;
    /**
     * True: Cluster overflow occurred, false: Otherwise
     */
    private final boolean isClusterOverflow;
    /**
     * True: Clustering process converged, false: Otherwise
     */
    private final boolean isConverged;
    /**
     * PreprocessedData object
     */
    private final PreprocessedData preprocessedArt2aEuclidData;
    //</editor-fold>
    //<editor-fold desc="Private record IndexedValue">
    /**
     * Indexed value
     */
    private record IndexedValue (
        int index, 
        float value
    ) implements Comparable<IndexedValue> {
        
        /**
         * Constructor
         * 
         * @param index Index
         * @param value Value
         */
        public IndexedValue {}
        
        @Override
        public int compareTo(IndexedValue anotherIndexedValue) {
            return Float.compare(value, anotherIndexedValue.value());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Public constructor">
    /**
     * Constructor.
     * Note: No checks are performed.
     * 
     * @param aVigilance Vigilance parameter in interval (0,1)
     * @param aThresholdForContrastEnhancement Threshold for contrast 
     * enhancement
     * @param aNumberOfEpochs Number of epochs used for clustering
     * @param aNumberOfDetectedClusters Number of detected clusters
     * @param aClusterIndexOfDataVector Cluster index of data vector
     * @param aClusterMatrix Cluster matrix
     * @param aDataVectorZeroLengthFlags Flags array that indicates if scaled 
     * data row vectors have a length of zero (i.e. where all components are 
     * equal to zero). True: Scaled data row vector has a length of zero 
     * (corresponding contrast enhanced unit vector is set to null in this 
     * case), false: Otherwise.
     * @param anIsClusterOverflow True: Cluster overflow occurred, false: 
     * Otherwise
     * @param anIsConverged True: Clustering process converged, false: Otherwise
     * @param aPreprocessedArt2aEuclidData PreprocessedData instance
     */
    public Art2aEuclidResult(
        float aVigilance,
        float aThresholdForContrastEnhancement,
        int aNumberOfEpochs,
        int aNumberOfDetectedClusters, 
        int[] aClusterIndexOfDataVector,
        float[][] aClusterMatrix,
        boolean[] aDataVectorZeroLengthFlags,
        boolean anIsClusterOverflow,
        boolean anIsConverged,
        PreprocessedData aPreprocessedArt2aEuclidData
    ) {
        this.vigilance = aVigilance;
        this.thresholdForContrastEnhancement = aThresholdForContrastEnhancement;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.clusterIndexOfDataVector = aClusterIndexOfDataVector;
        this.clusterMatrix = aClusterMatrix;
        this.dataVectorZeroLengthFlags = aDataVectorZeroLengthFlags;
        this.isClusterOverflow = anIsClusterOverflow;
        this.isConverged = anIsConverged;
        this.preprocessedArt2aEuclidData = aPreprocessedArt2aEuclidData;
    }
    //</editor-fold>

    //<editor-fold desc="Public methods">
    /**
     * Returns specified cluster vector with index aClusterIndex in 
     * clusterMatrix.
     * 
     * @param aClusterIndex Index of cluster vector in clusterMatrix
     * @return Specified cluster vector
     * @throws IllegalArgumentException Thrown if argument is illegal.
     */
    public float[] getClusterVector(
        int aClusterIndex
    ) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        if(aClusterIndex < 0 || aClusterIndex >= this.numberOfDetectedClusters) {
            Art2aEuclidResult.LOGGER.log(
                Level.SEVERE, 
                "Art2aEuclidResult.getClusterVector: aClusterIndex is illegal."
            );
            throw new IllegalArgumentException("Art2aEuclidResult.getClusterVector: aClusterIndex is illegal.");
        }
        //</editor-fold>
        return this.clusterMatrix[aClusterIndex];
    }
    
    /**
     * Returns specified cluster vector with index aClusterIndex in 
     * cluster matrix with components being scaled to interval [0,1].
     * Note: Cluster matrix is NOT changed.
     * 
     * @param aClusterIndex Index of cluster vector in cluster matrix
     * @return Specified scaled cluster vector
     * @throws IllegalArgumentException Thrown if argument is illegal.
     */
    public float[] getScaledClusterVector(
        int aClusterIndex
    ) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        if(aClusterIndex < 0 || aClusterIndex >= this.numberOfDetectedClusters) {
            Art2aEuclidResult.LOGGER.log(
                Level.SEVERE, 
                "Art2aEuclidResult.getClusterVector: aClusterIndex is illegal."
            );
            throw new IllegalArgumentException("Art2aEuclidResult.getClusterVector: aClusterIndex is illegal.");
        }
        //</editor-fold>
        return Utils.getScaledVector(this.clusterMatrix[aClusterIndex]);
    }
    
    /**
     * Returns indices of data vectors in original data matrix that belong to
     * the specified cluster with index aClusterIndex.
     * Note: The returned indices are cached for successive fast usage.
     * 
     * @param aClusterIndex Index of cluster in cluster matrix
     * @return Indices of data vectors in original data matrix that belong to
     * the specified cluster with index aClusterIndex.
     * @throws IllegalArgumentException Thrown if argument is illegal.
     */
    public int[] getDataVectorIndicesOfCluster(
        int aClusterIndex
    ) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        if(aClusterIndex < 0 || aClusterIndex >= this.numberOfDetectedClusters) {
            Art2aEuclidResult.LOGGER.log(
                Level.SEVERE, 
                "Art2aEuclidResult.getDataVectorIndicesOfCluster: aClusterIndex is illegal."
            );
            throw new IllegalArgumentException("rt2aClusteringResult.getDataVectorIndicesOfCluster: aClusterIndex is illegal.");
        }
        //</editor-fold>
        
        LinkedList<Integer> tmpIndexListOfCluster = new LinkedList<>();
        for (int i = 0; i < this.clusterIndexOfDataVector.length; i++) {
            if (this.clusterIndexOfDataVector[i] == aClusterIndex) {
                tmpIndexListOfCluster.add(i);
            }
        }
        return tmpIndexListOfCluster.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Returns all indices of (scaled) data vectors that have a length of 
     * zero. The indices refer to the original data matrix.
     * Note: The returned indices are cached for successive fast usage.
     * 
     * @return All indices of (scaled) data vectors that have a length of 
     * zero. The indices refer to the original data matrix.
     */
    public int[] getZeroLengthDataVectorIndices() {
        LinkedList<Integer> tmpIndexList = new LinkedList<>();
        for (int i = 0; i < this.dataVectorZeroLengthFlags.length; i++) {
            if (this.dataVectorZeroLengthFlags[i]) {
                tmpIndexList.add(i);
            }
        }
        return tmpIndexList.stream().mapToInt(Integer::intValue).toArray();
    }
    
    /**
     * Return distance between specified clusters with aClusterIndex1 and
     * aClusterIndex2.
     * 
     * @param aClusterIndex1 Index of cluster 1 in cluster matrix
     * @param aClusterIndex2 Index of cluster 2 in cluster matrix
     * @return Distance between specified clusters with aClusterIndex1 and 
     * aClusterIndex2.
     * @throws IllegalArgumentException Thrown if an argument is illegal.
     */
    public float getDistanceBetweenClusters(
        int aClusterIndex1, 
        int aClusterIndex2
    ) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        if(aClusterIndex1 < 0 || aClusterIndex1 >= this.numberOfDetectedClusters) {
            Art2aEuclidResult.LOGGER.log(
                Level.SEVERE, 
                "Art2aEuclidResult.getDistanceBetweenClusters: aClusterIndex1 is illegal."
            );
            throw new IllegalArgumentException("Art2aEuclidResult.getDistanceBetweenClusters: aClusterIndex1 is illegal.");
        }
        if(aClusterIndex2 < 0 || aClusterIndex2 >= this.numberOfDetectedClusters) {
            Art2aEuclidResult.LOGGER.log(
                Level.SEVERE, 
                "Art2aEuclidResult.getDistanceBetweenClusters: aClusterIndex2 is illegal."
            );
            throw new IllegalArgumentException("Art2aEuclidResult.getDistanceBetweenClusters: aClusterIndex2 is illegal.");
        }
        //</editor-fold>
        
        if (aClusterIndex1 == aClusterIndex2) {
            return 0.0f;
        } else {
            return 
                (float) Math.sqrt(
                    Utils.getSquaredDistance(
                        this.clusterMatrix[aClusterIndex1], 
                        this.clusterMatrix[aClusterIndex2]
                    )
                );
        }
    }
    
    /**
     * Returns size of the specified cluster with index aClusterIndex, i.e. the 
     * number of data vectors of original data matrix that belong to the 
     * cluster.
     * Note: The internally evaluated indices of data vectors that belong to the
     * specified cluster are cached for successive fast usage.
     * 
     * @param aClusterIndex Index of cluster in cluster matrix
     * @return Size of the specified cluster with index aClusterIndex, i.e. the 
     * number of data vectors of original data matrix that belong to the 
     * cluster.
     * @throws IllegalArgumentException Thrown if argument is illegal.
     */
    public int getClusterSize(
        int aClusterIndex
    ) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        if(aClusterIndex < 0 || aClusterIndex >= this.numberOfDetectedClusters) {
            Art2aEuclidResult.LOGGER.log(
                Level.SEVERE, 
                "Art2aEuclidResult.getClusterSize: aClusterIndex is illegal."
            );
            throw new IllegalArgumentException("rt2aClusteringResult.getClusterSize: aClusterIndex is illegal.");
        }
        //</editor-fold>

        int tmpCounter = 0;
        for (int i = 0; i < this.clusterIndexOfDataVector.length; i++) {
            if (this.clusterIndexOfDataVector[i] == aClusterIndex) {
                tmpCounter++;
            }
        }
        return tmpCounter;
    }

    /**
     * Returns if cluster overflow occurred.
     * 
     * @return True: Cluster overflow occurred, false: Otherwise
     */
    public boolean isClusterOverflow() {
        return this.isClusterOverflow;
    }

    /**
     * Returns if clustering process converged.
     * 
     * @return True: Clustering process converged, false: Otherwise
     */
    public boolean isConverged() {
        return this.isConverged;
    }
    
    /**
     * Calculates index of representative data vector which is closest to the
     * specified cluster vector with index aClusterIndex.
     * 
     * @param aClusterIndex Index of cluster vector in cluster matrix
     * @return Index of representative data vector which is closest to the
     * specified cluster vector with index aClusterIndex
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public int getClusterRepresentativeIndex(
        int aClusterIndex
    ) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        if(aClusterIndex < 0 || aClusterIndex >= this.numberOfDetectedClusters) {
            Art2aEuclidResult.LOGGER.log(
                Level.SEVERE, 
                "Art2aEuclidResult.getClusterRepresentativeIndex: aClusterIndex is illegal."
            );
            throw new IllegalArgumentException("Art2aEuclidResult.getClusterRepresentativeIndex: aClusterIndex is illegal.");
        }
        //</editor-fold>
        int[] tmpDataVectorIndicesOfCluster = this.getDataVectorIndicesOfCluster(aClusterIndex);
        if (tmpDataVectorIndicesOfCluster.length == 1) {
            return tmpDataVectorIndicesOfCluster[0];
        }
        float[] tmpClusterVector = this.clusterMatrix[aClusterIndex];
        int tmpBestIndex = 0;
        float tmpMinimumDistance = Float.MAX_VALUE;
        float[] tmpContrastEnhancedVector = null;
        if (!this.preprocessedArt2aEuclidData.hasPreprocessedData()) {
            tmpContrastEnhancedVector = new float[tmpClusterVector.length];
        }
        for (int i = 0; i < tmpDataVectorIndicesOfCluster.length; i++) {
            int tmpIndex = tmpDataVectorIndicesOfCluster[i];
            if (this.preprocessedArt2aEuclidData.hasPreprocessedData()) {
                tmpContrastEnhancedVector = this.preprocessedArt2aEuclidData.getPreprocessedMatrix()[tmpIndex];
            } else {
                // Check of length is NOT necessary
                Art2aEuclidUtils.setContrastEnhancedVector(
                    this.preprocessedArt2aEuclidData.getDataMatrix()[tmpIndex],
                    tmpContrastEnhancedVector,
                    this.preprocessedArt2aEuclidData.getMinMaxComponentsOfDataMatrix(),
                    this.thresholdForContrastEnhancement
                );
            }
            float tmpSquaredDistance = Utils.getSquaredDistance(tmpContrastEnhancedVector, tmpClusterVector);
            if (tmpSquaredDistance < tmpMinimumDistance) {
                tmpBestIndex = tmpIndex;
                tmpMinimumDistance = tmpSquaredDistance;
            }
        }
        return tmpBestIndex;
    }    

    /**
     * Calculates array of indices of sorted representative data vectors of the
     * specified cluster with index aClusterIndex. The data vector with index 0 
     * is closest to the cluster vector, the one with index 1 is the second 
     * closest etc.
     * 
     * @param aClusterIndex Index of cluster vector in cluster matrix
     * @return Array of indices of sorted representative data vectors of the
     * specified cluster 
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public int[] getClusterRepresentativeIndices(
        int aClusterIndex
    ) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        if(aClusterIndex < 0 || aClusterIndex >= this.numberOfDetectedClusters) {
            Art2aEuclidResult.LOGGER.log(
                Level.SEVERE, 
                "Art2aEuclidResult.getClusterRepresentativeIndices: aClusterIndex is illegal."
            );
            throw new IllegalArgumentException("Art2aEuclidResult.getClusterRepresentativeIndices: aClusterIndex is illegal.");
        }
        //</editor-fold>
        int[] tmpDataVectorIndicesOfCluster = this.getDataVectorIndicesOfCluster(aClusterIndex);
        if (tmpDataVectorIndicesOfCluster.length == 1) {
            return tmpDataVectorIndicesOfCluster;
        }
        float[] tmpClusterVector = this.clusterMatrix[aClusterIndex];
        IndexedValue[] tmpIndexedValues = new IndexedValue[tmpDataVectorIndicesOfCluster.length];
        float[] tmpContrastEnhancedVector = null;
        if (!this.preprocessedArt2aEuclidData.hasPreprocessedData()) {
            tmpContrastEnhancedVector = new float[tmpClusterVector.length];
        }
        for (int i = 0; i < tmpDataVectorIndicesOfCluster.length; i++) {
            int tmpIndex = tmpDataVectorIndicesOfCluster[i];
            if (this.preprocessedArt2aEuclidData.hasPreprocessedData()) {
                tmpContrastEnhancedVector = this.preprocessedArt2aEuclidData.getPreprocessedMatrix()[tmpIndex];
            } else {
                // Check of length is NOT necessary
                Art2aEuclidUtils.setContrastEnhancedVector(
                    this.preprocessedArt2aEuclidData.getDataMatrix()[tmpIndex],
                    tmpContrastEnhancedVector,
                    this.preprocessedArt2aEuclidData.getMinMaxComponentsOfDataMatrix(),
                    this.thresholdForContrastEnhancement
                );
            }
            tmpIndexedValues[i] = new IndexedValue(tmpIndex, Utils.getSquaredDistance(tmpContrastEnhancedVector, tmpClusterVector));
        }
        // NOTE: SMALLEST squared distance FIRST!
        Arrays.sort(tmpIndexedValues);
        int[] tmpClusterRepresentativeIndices = new int[tmpIndexedValues.length];
        for (int i = 0; i < tmpIndexedValues.length; i++) {
            tmpClusterRepresentativeIndices[i] = tmpIndexedValues[i].index();
        }
        return tmpClusterRepresentativeIndices;
    }

    /**
     * Returns data vector indices which are closest to their cluster vectors.
     * 
     * @return Data vector indices which are closest to their cluster vectors
     */
    public int[] getRepresentativeIndicesOfClusters() {
        int[] tmpRepresentativeIndicesOfClusters = new int[this.numberOfDetectedClusters];
        for (int i = 0; i < this.numberOfDetectedClusters; i++) {
            tmpRepresentativeIndicesOfClusters[i] = this.getClusterRepresentativeIndex(i);
        }
        return tmpRepresentativeIndicesOfClusters;
    }
    
    /**
     * Vigilance parameter
     * 
     * @return Vigilance parameter
     */
    public float getVigilance() {
        return this.vigilance;
    }

    /**
     * Number of epochs
     * 
     * @return Number of epochs
     */
    public int getNumberOfEpochs() {
        return this.numberOfEpochs;
    }
    
    /**
     * Number of detected clusters
     * 
     * @return Number of detected clusters
     */
    public int getNumberOfDetectedClusters() {
        return this.numberOfDetectedClusters;
    }
    //</editor-fold>

}
