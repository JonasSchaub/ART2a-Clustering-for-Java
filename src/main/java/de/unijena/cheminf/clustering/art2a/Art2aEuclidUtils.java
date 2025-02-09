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

/**
 * Library of helper records, static helper classes and static, thread-safe 
 * (stateless) utility methods for ART-2a-Euclid clustering.
 * <br><br>
 * Note: No checks are performed.
 * 
 * @author Achim Zielesny
 */
public class Art2aEuclidUtils {

    //<editor-fold desc="Private static final constants" defaultstate="collapsed">
    /**
     * Value 1.0
     */
    private static final float ONE = 1.0f;
    //</editor-fold>

    //<editor-fold desc="Constructor" defaultstate="collapsed">
    /**
     * Constructor
     */
    protected Art2aEuclidUtils() {}
    //</editor-fold>
    
    //<editor-fold desc="Protected static utility methods" defaultstate="collapsed">
    /**
     * Assigns data vectors to clusters
     * 
     * @param aNumberOfDetectedClusters Number of detected clusters
     * @param aDataVectorZeroLengthFlags Flags array that indicates if scaled 
     * data row vectors have a length of zero (i.e. where all components are 
     * equal to zero). True: Scaled data row vector has a length of zero 
     * (corresponding contrast enhanced unit vector is set to null in this 
     * case), false: Otherwise.
     * @param anArt2aEuclidData Art2aEuclidData instance (IS NOT CHANGED)
     * @param aBufferVector Buffer vector (MUST BE ALREADY INSTANTIATED)
     * @param aThresholdForContrastEnhancement Threshold for contrast 
     * enhancement
     * @param aClusterMatrix Cluster matrix (IS NOT CHANGED)
     * @param aClusterIndexOfDataVector Cluster index of data vector (MAY BE 
     * CHANGED and MUST ALREADY BE INSTANTIATED)
     * @param aClusterUsageFlags Flags for cluster usage. True: Cluster is used, 
     * false: Cluster is empty and has to be removed (MAY BE CHANGED and MUST 
     * ALREADY BE INSTANTIATED)
     */
    protected static void assignDataVectorsToClusters(
        int aNumberOfDetectedClusters,
        boolean[] aDataVectorZeroLengthFlags,
        Art2aEuclidData anArt2aEuclidData,
        float[] aBufferVector,
        float aThresholdForContrastEnhancement,
        float[][]aClusterMatrix,
        int[] aClusterIndexOfDataVector,
        boolean[] aClusterUsageFlags
    ) {
        // Assign data vectors to clusters (last pass)
        Arrays.fill(aClusterUsageFlags, false);
        for (int i = 0; i < aDataVectorZeroLengthFlags.length; i++) {
            if (!aDataVectorZeroLengthFlags[i]) {
                if (anArt2aEuclidData.hasPreprocessedData()) {
                    aBufferVector = anArt2aEuclidData.getContrastEnhancedMatrix()[i];
                } else {
                    // Check of length is NOT necessary
                    Art2aEuclidUtils.setContrastEnhancedVector(
                        anArt2aEuclidData.getDataMatrix()[i],
                        aBufferVector,
                        anArt2aEuclidData.getMinMaxComponentsOfDataMatrix(),
                        aThresholdForContrastEnhancement
                    );
                }
                int tmpWinnerClusterIndex = 
                    Art2aEuclidUtils.getClusterIndex(
                        aBufferVector, 
                        aNumberOfDetectedClusters,
                        aClusterMatrix
                    );
                aClusterIndexOfDataVector[i] = tmpWinnerClusterIndex;
                aClusterUsageFlags[tmpWinnerClusterIndex] = true;
            }
        }
    }

    /**
     * Returns index of cluster for contrast enhanced vector
     * 
     * @param aContrastEnhancedVector Contrast enhanced vector
     * @param aNumberOfDetectedClusters Number of detected clusters
     * @param aClusterMatrix Cluster matrix
     * @return Index of cluster for contrast enhanced unit vector
     */
    protected static int getClusterIndex(
        float[] aContrastEnhancedVector, 
        int aNumberOfDetectedClusters,
        float[][] aClusterMatrix
    ) {
        float tmpMinSquaredDistance = Float.MAX_VALUE;
        int tmpWinnerClusterIndex = -1;
        for (int i = 0; i < aNumberOfDetectedClusters; i++) {
            float tmpSquaredDistance = Utils.getSquaredDistance(aContrastEnhancedVector, aClusterMatrix[i]);
            if (tmpSquaredDistance < tmpMinSquaredDistance) {
                tmpMinSquaredDistance = tmpSquaredDistance;
                tmpWinnerClusterIndex = i;
            }
        }
        return tmpWinnerClusterIndex;
    }

    /**
     * Determines convergence of clustering process.
     * Note: No checks are performed.
     * 
     * @param aNumberOfDetectedClusters Number of detected clusters
     * @param anEpoch Current epochs
     * @param aClusterCentroidMatrix Cluster centroid matrix with centroid row 
     * vectors
     * @param aClusterCentroidMatrixOld Cluster centroid matrix with 
     * centroid row vectors of the previous epoch
     * @param aMaximumNumberOfEpochs Maximum number of epochs
     * @param aConvergenceThreshold Convergence threshold
     * @return True if clustering process has converged, false otherwise.
     */
    protected static boolean isConverged(
        int aNumberOfDetectedClusters, 
        int anEpoch, 
        float[][] aClusterCentroidMatrix, 
        float[][] aClusterCentroidMatrixOld,
        int aMaximumNumberOfEpochs,
        float aConvergenceThreshold
    ) {
        if (anEpoch == 1) {
            // Convergence check needs at least 2 epochs
            Utils.copyRows(aClusterCentroidMatrix, aClusterCentroidMatrixOld, aNumberOfDetectedClusters);
            return false;
        } else {
            float tmpSquaredConvergenceThreshold = aConvergenceThreshold * aConvergenceThreshold;
            boolean tmpIsConverged = false;
            if(anEpoch < aMaximumNumberOfEpochs) {
                // Check convergence by evaluating the similarity (scalar product) 
                // of the cluster vectors of this and the previous epoch
                tmpIsConverged = true;
                for (int i = 0; i < aNumberOfDetectedClusters; i++) {
                    if (
                        aClusterCentroidMatrixOld[i] == null || 
                        Utils.getSquaredDistance(
                            aClusterCentroidMatrix[i], 
                            aClusterCentroidMatrixOld[i]
                        ) > tmpSquaredConvergenceThreshold
                    ) {
                        tmpIsConverged = false;
                        break;
                    }
                }
                if(!tmpIsConverged) {
                    Utils.copyRows(aClusterCentroidMatrix, aClusterCentroidMatrixOld, aNumberOfDetectedClusters);
                }
            }
            return tmpIsConverged;
        }
    }

    /**
     * Modifies winner cluster (see code).
     * Note: aContrastEnhancedVector is used for modification and may be 
     * changed.
     * Note: No checks are performed.
     * 
     * @param aContrastEnhancedVector Contrast enhanced unit vector for 
     * modification (MAY BE CHANGED)
     * @param aWinnerClusterVector Winner cluster centroid vector (MAY BE CHANGED)
     * @param aThresholdForContrastEnhancement Threshold for contrast enhancement
     * @param aLearningParameter  Learning parameter
     */
    protected static void modifyWinnerCluster(
        float[] aContrastEnhancedVector,
        float[] aWinnerClusterVector,
        float aThresholdForContrastEnhancement,
        float aLearningParameter
    ) {
        // Note: aContrastEnhancedVector is used for modification
        for(int j = 0; j < aWinnerClusterVector.length; j++) {
            if(aWinnerClusterVector[j] <= aThresholdForContrastEnhancement) {
                aContrastEnhancedVector[j] = 0.0f;
            }
        }
        float tmpFactor = ONE - aLearningParameter;
        for(int j = 0; j < aWinnerClusterVector.length; j++) {
            aContrastEnhancedVector[j] = aLearningParameter * aContrastEnhancedVector[j] + tmpFactor * aWinnerClusterVector[j];
        }
        Utils.copyVector(aContrastEnhancedVector, aWinnerClusterVector);
    }

    /**
     * Transforms original data vector into corresponding contrast enhanced 
     * unit vector (see code).
     * Note: No checks are performed.
     * 
     * @param aDataVector Data vector (IS NOT CHANGED)
     * @param aBufferVector Buffer vector for contrast enhanced unit vector 
     * derived from data vector (MUST ALREADY BE INSTANTIATED and is set within 
     * the method)
     * @param aMinMaxComponents Min-max components of original data matrix
     * @param aThresholdForContrastEnhancement Threshold for contrast 
     * enhancement
     * @return True: Scaled data vector has a length of zero, false: Otherwise
     */
    protected static boolean setContrastEnhancedVector(
        float[] aDataVector,
        float[] aBufferVector,
        Utils.MinMaxValue[] aMinMaxComponents,
        float aThresholdForContrastEnhancement
    ) {
        // Already allocated memory of aBufferVector is reused
        Utils.copyVector(aDataVector, aBufferVector);
        // Scale components of vector to interval [0,1]
        Utils.scaleVector(aBufferVector, aMinMaxComponents);
        // Check length
        if (Utils.hasLengthOfZero(aBufferVector)) {
            // True: Scaled source vector has a length of zero
            return true;
        } else {
            // Enhance contrast
            Utils.setContrastEnhancement(aBufferVector, aThresholdForContrastEnhancement);
            // False: Scaled data vector has a length different from zero
            return false;
        }
    }

    /**
     * Sets rho winner with the rho value and the cluster index of the winner
     * (see code). If the cluster index is negative the first scaled rho value 
     * is the winner.
     * 
     * @param aContrastEnhancedVector Contrast enhanced unit vector (IS NOT 
     * CHANGED)
     * @param aClusterMatrix Cluster matrix (IS NOT CHANGED)
     * @param aNumberOfDetectedClusters Number of detected clusters
     * @param aScalingFactor Scaling factor
     * @param aRhoWinner Rho winner: Is set with the rho value and the cluster 
     * index of the winner. If the cluster index is negative the first scaled 
     * rho value is the winner.
     */
    protected static void setRhoWinner(
        float[] aContrastEnhancedVector,
        float[][] aClusterMatrix,
        int aNumberOfDetectedClusters,
        float aScalingFactor,
        Utils.RhoWinner aRhoWinner
    ) {
        // Calculate first rho value
        float tmpRhoValue = Utils.getSumOfSquaredDifferences(aContrastEnhancedVector, aScalingFactor);
        // Set winner index to negative value
        int tmpIndex = -1;
        // Calculate other rho values
        for(int i = 0; i < aNumberOfDetectedClusters; i++) {
            float tmpRhoForCluster = Utils.getSquaredDistance(aContrastEnhancedVector, aClusterMatrix[i]);
            if(tmpRhoForCluster < tmpRhoValue) {
                tmpRhoValue = tmpRhoForCluster;
                tmpIndex = i;
            }
        }
        aRhoWinner.setRhoWinner(tmpRhoValue, tmpIndex);
    }
    //</editor-fold>
    
}
