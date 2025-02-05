/*
 * ART-2a Clustering for Java
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
import java.util.Random;

/**
 * Library of helper records, static helper classes and static, thread-safe 
 * (stateless) utility methods for ART-2a clustering.
 * <br><br>
 * Note: No checks are performed.
 * 
 * @author Achim Zielesny
 */
public class Art2aUtils {
    
    //<editor-fold desc="Private static final constants" defaultstate="collapsed">
    /**
     * Value 1.0
     */
    private static final float ONE = 1.0f;
    //</editor-fold>
    //<editor-fold desc="Protected helper record" defaultstate="collapsed">
    /**
     * Helper record: Minimum and maximum value.
     * <br><br>
     * Note: No checks are performed.
     * 
     * @param minValue Minimum value
     * @param maxValue Maximum value
    */
    protected record MinMaxValue(float minValue, float maxValue) {
    
        /**
         * Constructor
         * 
         * @param minValue Minimum value
         * @param maxValue Maximum value
         */
        public MinMaxValue {}
        
    }
    //</editor-fold>
    //<editor-fold desc="Protected static helper classes" defaultstate="collapsed">
    /**
     * Helper class: Rho winner.
     * <br><br>
     * Note: No checks are performed.
     */
    protected static class RhoWinner {

        //<editor-fold desc="Private class variables" defaultstate="collapsed">
        /**
         * Rho value
         */
        private float rhoValue;
        /**
         * Index of cluster
         */
        private int indexOfCluster;
        //</editor-fold>

        //<editor-fold desc="Constructor" defaultstate="collapsed">
        /**
         * Constructor
         */
        protected RhoWinner() {}
        //</editor-fold>

        //<editor-fold desc="Protected get/set methods" defaultstate="collapsed">
        /**
         * Set rho winner
         * 
         * @param aRhoValue Rho value
         * @param anIndexOfCluster Index of cluster
         */
        protected void setRhoWinner(
            float aRhoValue,
            int anIndexOfCluster
        ) {
            this.rhoValue = aRhoValue;
            this.indexOfCluster = anIndexOfCluster;
        }

        /**
         * Rho value
         * 
         * @return Rho value
         */
        protected float getRhoValue() {
            return this.rhoValue;
        }

        /**
         * Index of cluster
         * 
         * @return Index of cluster
         */
        protected int getIndexOfCluster() {
            return this.indexOfCluster;
        }
        //</editor-fold>

    }

    /**
     * Helper class: Cluster removal info.
     * <br><br>
     * Note: No checks are performed.
     */
    protected static class ClusterRemovalInfo {

        //<editor-fold desc="Private class variables" defaultstate="collapsed">
        /**
         * True: Cluster is removed, false: Otherwise
         */
        private boolean isClusterRemoved;
        /**
         * Number of detected clusters
         */
        private int numberOfDetectedClusters;
        //</editor-fold>

        //<editor-fold desc="Constructor" defaultstate="collapsed">
        /**
         * Constructor
         */
        protected ClusterRemovalInfo() {}
        //</editor-fold>

        //<editor-fold desc="Protected get/set methods" defaultstate="collapsed">
        /**
         * Set cluster removal info
         * 
         * @param anIsClusterRemoved True: Cluster is removed, false: Otherwise
         * @param aNumberOfDetectedClusters Number of detected clusters
         */
        protected void setClusterRemovalInfo(
            boolean anIsClusterRemoved,
            int aNumberOfDetectedClusters
        ) {
            this.isClusterRemoved = anIsClusterRemoved;
            this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        }

        /**
         * True: Cluster is removed, false: Otherwise
         * 
         * @return True: Cluster is removed, false: Otherwise
         */
        protected boolean isClusterRemoved() {
            return this.isClusterRemoved;
        }

        /**
         * Number of detected clusters
         * 
         * @return Number of detected clusters
         */
        protected int getNumberOfDetectedClusters() {
            return this.numberOfDetectedClusters;
        }
        //</editor-fold>

    }
    //</editor-fold>
    
    //<editor-fold desc="Constructor" defaultstate="collapsed">
    /**
     * Constructor
     */
    protected Art2aUtils() {}
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
     * @param anArt2aData Art2aData instance (IS NOT CHANGED)
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
        Art2aData anArt2aData,
        float[] aBufferVector,
        float aThresholdForContrastEnhancement,
        float[][]aClusterMatrix,
        int[] aClusterIndexOfDataVector,
        boolean[] aClusterUsageFlags
    ) {
        Arrays.fill(aClusterUsageFlags, false);
        for (int i = 0; i < aDataVectorZeroLengthFlags.length; i++) {
            if (!aDataVectorZeroLengthFlags[i]) {
                if (anArt2aData.hasPreprocessedData()) {
                    aBufferVector = anArt2aData.getContrastEnhancedUnitMatrix()[i];
                } else {
                    // Check of length is NOT necessary
                    Art2aUtils.setContrastEnhancedUnitVector(
                        anArt2aData.getDataMatrix()[i],
                        aBufferVector,
                        anArt2aData.getMinMaxComponentsOfDataMatrix(),
                        aThresholdForContrastEnhancement
                    );
                }
                int tmpWinnerClusterIndex = 
                    Art2aUtils.getClusterIndex(
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
     * (Deep) Copies source matrix to destination matrix. Row vectors of 
     * destination matrix may not have been instantiated.
     * 
     * @param aSourceMatrix Source matrix (IS NOT CHANGED)
     * @param aDestinationMatrix Destination matrix (MUST HAVE BEEN 
     * INSTANTIATED and MAY BE CHANGED)
     */
    protected static void copyMatrix(
        float[][] aSourceMatrix, 
        float[][] aDestinationMatrix
    ) {
        for (int i = 0; i < aSourceMatrix.length; i++) {
            if (aDestinationMatrix[i] == null) {
                aDestinationMatrix[i] = new float[aSourceMatrix[i].length];
            }
            System.arraycopy(
                aSourceMatrix[i], 
                0, 
                aDestinationMatrix[i], 
                0, 
                aSourceMatrix[i].length
            );
        }
    }

    /**
     * (Deep) Copies specified number of rows of source matrix to destination 
     * matrix. Row vectors of destination matrix may not have been instantiated.
     * 
     * @param aSourceMatrix Source matrix (IS NOT CHANGED)
     * @param aDestinationMatrix Destination matrix (MUST HAVE BEEN 
     * INSTANTIATED and MAY BE CHANGED)
     * @param aNumberOfRows Number of rows to be copied from source matrix to 
     * destination matrix
     */
    protected static void copyRows(
        float[][] aSourceMatrix, 
        float[][] aDestinationMatrix,
        int aNumberOfRows
    ) {
        for (int i = 0; i < aNumberOfRows; i++) {
            if (aDestinationMatrix[i] == null) {
                aDestinationMatrix[i] = new float[aSourceMatrix[i].length];
            }
            System.arraycopy(
                aSourceMatrix[i], 
                0, 
                aDestinationMatrix[i], 
                0, 
                aSourceMatrix[i].length
            );
        }
    }
    
    /**
     * (Deep) Copies source vector to destination vector.
     * 
     * @param aSourceVector Source vector (IS NOT CHANGED)
     * @param aDestinationVector Destination vector (MUST HAVE BEEN 
     * INSTANTIATED and MAY BE CHANGED)
     */
    protected static void copyVector(
        float[] aSourceVector, 
        float[] aDestinationVector
    ) {
        System.arraycopy(
            aSourceVector, 
            0, 
            aDestinationVector, 
            0, 
            aSourceVector.length
        );
    }

    /**
     * Calculates contrast enhanced vector.
     *
     * @param aVector Vector to be contrast enhanced (MAY BE CHANGED)
     * @param aThresholdForContrastEnhancement Threshold for contrast enhancement
     */
    protected static void enhanceContrast(
        float[] aVector, 
        float aThresholdForContrastEnhancement
    ) {
        for(int i = 0; i < aVector.length; i++) {
            if(aVector[i] != 0.0f && aVector[i] <= aThresholdForContrastEnhancement) {
                aVector[i] = 0.0f;
            }
        }
    }
    
    /**
     * Fills matrix with value.
     * 
     * @param aMatrix Matrix (MAY BE CHANGED)
     * @param aValue Value
     */
    protected static void fillMatrix(
        float[][] aMatrix, 
        float aValue
    ) {
        for (float [] tmpRowVector : aMatrix) {
            Arrays.fill(tmpRowVector , aValue);        
        }
    }
    
    /**
     * Fills vector with value.
     * 
     * @param aVector Vector (MAY BE CHANGED) 
     * @param aValue Value
     */
    protected static void fillVector(
        float[] aVector, 
        float aValue
    ) {
        Arrays.fill(aVector , aValue);        
    }

    /**
     * Fills vector with value.
     * 
     * @param aVector Vector (MAY BE CHANGED) 
     * @param aValue Value
     */
    protected static void fillVector(
        boolean[] aVector, 
        boolean aValue
    ) {
        Arrays.fill(aVector , aValue);        
    }

    /**
     * Fills vector with value.
     * 
     * @param aVector Vector (MAY BE CHANGED) 
     * @param aValue Value
     */
    protected static void fillVector(
        int[] aVector, 
        int aValue
    ) {
        Arrays.fill(aVector , aValue);        
    }

    /**
     * Returns mean distance of all specified row vectors.
     * 
     * @param aMatrix Matrix with row vectors (IS NOT CHANGED)
     * @param anIndicesOfRowVectors Indices of row vectors of aMatrix
     * @return Mean squared distance of all specified row vectors.
     */
    protected static float getMeanDistance(
        float[][] aMatrix,
        int[] anIndicesOfRowVectors
    ) {
        float tmpSum = 0.0f;
        for (int i = 0; i < anIndicesOfRowVectors.length; i++) {
            for (int j = i + 1; j < anIndicesOfRowVectors.length; j++) {
                tmpSum += (float) Math.sqrt(Art2aUtils.getSquaredDistance(aMatrix[anIndicesOfRowVectors[i]], aMatrix[anIndicesOfRowVectors[j]]));
            }
        }
        return tmpSum / (float) (anIndicesOfRowVectors.length * (anIndicesOfRowVectors.length - 1) / 2);
    }

    /**
     * Returns index of cluster for contrast enhanced unit vector
     * 
     * @param aContrastEnhancedUnitVector Contrast enhanced unit vector
     * @param aNumberOfDetectedClusters Number of detected clusters
     * @param aClusterMatrix Cluster matrix
     * @return Index of cluster for contrast enhanced unit vector
     */
    protected static int getClusterIndex(
        float[] aContrastEnhancedUnitVector, 
        int aNumberOfDetectedClusters,
        float[][] aClusterMatrix
    ) {
        float tmpMaxScalarProduct = Float.MIN_VALUE;
        int tmpWinnerClusterIndex = -1;
        for (int i = 0; i < aNumberOfDetectedClusters; i++) {
            float tmpScalarProduct = Art2aUtils.getScalarProduct(aContrastEnhancedUnitVector, aClusterMatrix[i]);
            if (tmpScalarProduct > tmpMaxScalarProduct) {
                tmpMaxScalarProduct = tmpScalarProduct;
                tmpWinnerClusterIndex = i;
            }
        }
        return tmpWinnerClusterIndex;
    }
    
    /**
     * Returns min-max components for matrix where MinMaxValue[j] 
     * corresponds to column j of the row vectors of the matrix. The min-max 
     * components may be used to scale row vectors to interval [0,1], see 
     * method scaleVector().
     * 
     * @param aMatrix Matrix (IS NOT CHANGED)
     * @return Min-max components
     */
    protected static MinMaxValue[] getMinMaxComponents(
        float[][] aMatrix
    ) {
        MinMaxValue[] tmpMinMaxComponents = new MinMaxValue[aMatrix[0].length];
        for (int j = 0; j < aMatrix[0].length; j++) {
            float tmpMinValue = aMatrix[0][j];
            float tmpMaxValue = aMatrix[0][j];
            for (int i = 1; i < aMatrix.length; i++) {
                if (aMatrix[i][j] < tmpMinValue) {
                    tmpMinValue = aMatrix[i][j];
                } else if (aMatrix[i][j] > tmpMaxValue) {
                    tmpMaxValue = aMatrix[i][j];
                }
            }
            tmpMinMaxComponents[j] = new MinMaxValue(tmpMinValue, tmpMaxValue);
        }
        return tmpMinMaxComponents;
    }
    
    /**
     * Calculates the scalar product (dot product) of aVector1 and aVector2.
     * 
     * @param aVector1 Vector 1 (IS NOT CHANGED)
     * @param aVector2 Vector 2 (IS NOT CHANGED)
     * @return Scalar product (dot product)
     */
    protected static float getScalarProduct(
        float[] aVector1, 
        float[] aVector2
    ) {
        float tmpSum = 0.0f;
        for (int i = 0; i < aVector1.length; i++) {
            // tmpSum += aVector1[i] * aVector2[i];
            tmpSum = Math.fma(aVector1[i], aVector2[i], tmpSum);
        }
        return tmpSum;
    }
    
    /**
     * Scales components of aVectorToBeScaled to interval [0,1].
     * 
     * @param aVectorToBeScaled Vector (IS NOT CHANGED)
     * @return New scaled vector with components in interval [0,1] or new 
     * vector of length zero if all components of aVectorToBeScaled are the 
     * same.
     */
    protected static float[] getScaledVector(
        float[] aVectorToBeScaled
    ) {
        float tmpMinValue = aVectorToBeScaled[0];
        float tmpMaxValue = aVectorToBeScaled[0];
        for(int i = 0; i < aVectorToBeScaled.length; i++) {
            if (aVectorToBeScaled[i] < tmpMinValue) {
                tmpMinValue = aVectorToBeScaled[i];
            } else if (aVectorToBeScaled[i] > tmpMaxValue) {
                tmpMaxValue = aVectorToBeScaled[i];
            }
        }
        float[] tmpScaledVector = new float[aVectorToBeScaled.length];
        if (tmpMinValue == tmpMaxValue) {
            for(int i = 0; i < aVectorToBeScaled.length; i++) {
                tmpScaledVector[i] = aVectorToBeScaled[i] - tmpMinValue;
            }
        } else {
            float tmpDenominator = tmpMaxValue - tmpMinValue;
            for(int i = 0; i < aVectorToBeScaled.length; i++) {
                tmpScaledVector[i] = (aVectorToBeScaled[i] - tmpMinValue) / tmpDenominator;
            }
        }
        return tmpScaledVector;
    }
    
    /**
     * Calculates the squared distance between aVector1 and aVector2.
     * 
     * @param aVector1 Vector 1 (IS NOT CHANGED)
     * @param aVector2 Vector 2 (IS NOT CHANGED)
     * @return Squared distance
     */
    protected static float getSquaredDistance(
        float[] aVector1, 
        float[] aVector2
    ) {
        float tmpSum = 0.0f;
        for (int i = 0; i < aVector1.length; i++) {
            float tmpDelta = aVector1[i] - aVector2[i];
            // tmpSum += (aVector1[i] - aVector2[i])^2;
            tmpSum = Math.fma(tmpDelta, tmpDelta, tmpSum);
        }
        return tmpSum;
    }

    /**
     * Calculates the sum of components of aVector.
     *
     * @param aVector Vector (IS NOT CHANGED)
     * @return Sum of components
     */
    protected static float getSumOfComponents(
        float[] aVector
    ) {
        float tmpSum = 0.0f;
        for (float tmpComponent : aVector) {
            tmpSum += tmpComponent;
        }
        return tmpSum;
    }

    /**
     * Threshold for contrast enhancement
     * 
     * @param aNumberOfComponents Number of components
     * @param anOffsetForContrastEnhancement Offset for contrast enhancement 
     * @return Threshold for contrast enhancement
     */
    protected static float getThresholdForContrastEnhancement(
        int aNumberOfComponents,
        float anOffsetForContrastEnhancement
    ) {
        // Original code:
        // return (float) (1.0 / Math.sqrt(aNumberOfComponents + 1.0));
        return (float) (1.0 / Math.sqrt(aNumberOfComponents + anOffsetForContrastEnhancement));
    }
    
    /**
     * Calculates the length of aVector.
     *
     * @param aVector Vector (IS NOT CHANGED)
     * @return Length of vector
     */
    protected static float getVectorLength(
        float[] aVector
    ) {
        float tmpSum = 0.0f;
        for (float tmpComponent : aVector) {
            // tmpSum += tmpComponent * tmpComponent;
            tmpSum = Math.fma(tmpComponent, tmpComponent, tmpSum);
        }
        return (float) Math.sqrt(tmpSum);
    }
    
    /**
     * Checks if vector has a length of zero (i.e. if all components are equal 
     * to zero).
     * 
     * @param aVector Vector (IS NOT CHANGED)
     * @return True: Vector has a length of zero, false: Otherwise
     */
    protected static boolean hasLengthOfZero(
        float[] aVector
    ) {
        for(float tmpComponent : aVector) {
            if (tmpComponent != 0.0f) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes empty clusters from cluster matrix
     * 
     * @param aClusterUsageFlags Flags for cluster usage. True: Cluster is used, 
     * false: Cluster is empty and has to be removed (IS NOT CHANGED)
     * @param aClusterMatrix Cluster matrix (MAY BE CHANGED)
     * @param aNumberOfDetectedClusters Number of detected clusters
     * @param aClusterRemovalInfo Cluster removal info (is set according to the 
     * operations performed, IS CHANGED)
     */
    protected static void removeEmptyClusters(
        boolean[] aClusterUsageFlags,
        float[][] aClusterMatrix,
        int aNumberOfDetectedClusters,
        ClusterRemovalInfo aClusterRemovalInfo
    ) {
        boolean tmpIsEmptyClusterRemoval = false;
        for (int i = 0; i < aNumberOfDetectedClusters; i++) {
            if (!aClusterUsageFlags[i]) {
                tmpIsEmptyClusterRemoval = true;
                break;
            }
        }
        if (tmpIsEmptyClusterRemoval) {
            // Remove empty clusters from cluster matrix
            LinkedList<float[]> tmpClusterVectorList = new LinkedList<>();
            for (int i = 0; i < aNumberOfDetectedClusters; i++) {
                if (aClusterUsageFlags[i]) {
                    tmpClusterVectorList.add(aClusterMatrix[i]);
                    aClusterMatrix[i] = null;
                }
            }
            int tmpIndex = 0;
            for (float[] tmpClusterVector : tmpClusterVectorList) {
                aClusterMatrix[tmpIndex++] = tmpClusterVector;
            }
            aClusterRemovalInfo.setClusterRemovalInfo(tmpIsEmptyClusterRemoval, tmpClusterVectorList.size());
        } else {
            aClusterRemovalInfo.setClusterRemovalInfo(tmpIsEmptyClusterRemoval, aNumberOfDetectedClusters);
        }
    }
    
    /**
     * Calculates contrast enhanced vector.
     *
     * @param aVector Vector to be contrast enhanced (MAY BE CHANGED)
     * @param aThresholdForContrastEnhancement Threshold for contrast enhancement
     * @return True if aVector is changed by contrast enhancement, false otherwise.
     */
    protected static boolean isContrastEnhanced(
        float[] aVector, 
        float aThresholdForContrastEnhancement
    ) {
        boolean tmpIsVectorChanged = false;
        for(int i = 0; i < aVector.length; i++) {
            if(aVector[i] != 0.0f && aVector[i] <= aThresholdForContrastEnhancement) {
                aVector[i] = 0.0f;
                tmpIsVectorChanged = true;
            }
        }
        return tmpIsVectorChanged;
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
            Art2aUtils.copyRows(aClusterCentroidMatrix, aClusterCentroidMatrixOld, aNumberOfDetectedClusters);
            return false;
        } else {
            boolean tmpIsConverged = false;
            if(anEpoch < aMaximumNumberOfEpochs) {
                // Check convergence by evaluating the similarity (scalar product) 
                // of the cluster vectors of this and the previous epoch
                tmpIsConverged = true;
                for (int i = 0; i < aNumberOfDetectedClusters; i++) {
                    if (
                        aClusterCentroidMatrixOld[i] == null || 
                        Art2aUtils.getScalarProduct(aClusterCentroidMatrix[i], aClusterCentroidMatrixOld[i]) < aConvergenceThreshold
                    ) {
                        tmpIsConverged = false;
                        break;
                    }
                }
                if(!tmpIsConverged) {
                    Art2aUtils.copyRows(aClusterCentroidMatrix, aClusterCentroidMatrixOld, aNumberOfDetectedClusters);
                }
            }
            return tmpIsConverged;
        }
    }
    
    /**
     * Checks if matrix is valid.
     * 
     * @param aMatrix Matrix
     * @return True: Matrix is valid, false: Otherwise
     */
    protected static boolean isMatrixValid(
        float[][] aMatrix
    ) {
        if (aMatrix == null || aMatrix.length == 0) {
            return false;
        }
        for (float[] tmpRowVector : aMatrix) {
            if (tmpRowVector == null || tmpRowVector.length == 0) {
                return false;
            }
        }
        int tmpRowVectorLength = aMatrix[0].length;
        for (int i = 1; i < aMatrix.length; i++) {
            if (aMatrix[i].length != tmpRowVectorLength) {
                return false;
            }
        }
        return true;
    }

    /**
     * Modifies winner cluster (see code).
     * Note: aContrastEnhancedUnitVector is used for modification and may be 
     * changed.
     * Note: No checks are performed.
     * 
     * @param aContrastEnhancedUnitVector Contrast enhanced unit vector for 
     * modification (MAY BE CHANGED)
     * @param aWinnerClusterVector Winner cluster centroid vector (MAY BE CHANGED)
     * @param aThresholdForContrastEnhancement Threshold for contrast enhancement
     * @param aLearningParameter  Learning parameter
     */
    protected static void modifyWinnerCluster(
        float[] aContrastEnhancedUnitVector,
        float[] aWinnerClusterVector,
        float aThresholdForContrastEnhancement,
        float aLearningParameter
    ) {
        // Note: aContrastEnhancedUnitVector is used for modification
        boolean tmpIsChanged = false;
        for(int j = 0; j < aWinnerClusterVector.length; j++) {
            if(aWinnerClusterVector[j] <= aThresholdForContrastEnhancement) {
                aContrastEnhancedUnitVector[j] = 0.0f;
                tmpIsChanged = true;
            }
        }
        float tmpFactor1;
        if (tmpIsChanged) {
            tmpFactor1 = aLearningParameter / Art2aUtils.getVectorLength(aContrastEnhancedUnitVector);
        } else {
            tmpFactor1 = aLearningParameter;
        }
        float tmpFactor2 = ONE - aLearningParameter;
        for(int j = 0; j < aWinnerClusterVector.length; j++) {
            aContrastEnhancedUnitVector[j] = tmpFactor1 * aContrastEnhancedUnitVector[j] + tmpFactor2 * aWinnerClusterVector[j];
        }
        Art2aUtils.normalizeVector(aContrastEnhancedUnitVector);
        Art2aUtils.copyVector(aContrastEnhancedUnitVector, aWinnerClusterVector);
    }
    
    /**
     * Calculates normalized (unit) vector of length 1.
     *
     * @param aVector Vector to be normalized (MAY BE CHANGED)
     */
    protected static void normalizeVector(
        float[] aVector
    ) {
        float tmpInverseVectorLength = ONE / Art2aUtils.getVectorLength(aVector);
        for(int i = 0; i < aVector.length; i++) {
            aVector[i] *= tmpInverseVectorLength;
        }
    }

    /**
     * Sets rho winner with the rho value and the cluster index of the winner
     * (see code). If the cluster index is negative the first scaled rho value 
     * is the winner.
     * 
     * @param aContrastEnhancedUnitVector Contrast enhanced unit vector (IS NOT 
     * CHANGED)
     * @param aClusterMatrix Cluster matrix (IS NOT CHANGED)
     * @param aNumberOfDetectedClusters Number of detected clusters
     * @param aScalingFactor Scaling factor
     * @param aRhoWinner Rho winner: Is set with the rho value and the cluster 
     * index of the winner. If the cluster index is negative the first scaled 
     * rho value is the winner.
     */
    protected static void setRhoWinner(
        float[] aContrastEnhancedUnitVector,
        float[][] aClusterMatrix,
        int aNumberOfDetectedClusters,
        float aScalingFactor,
        Art2aUtils.RhoWinner aRhoWinner
    ) {
        // Calculate first rho value
        float tmpRhoValue = aScalingFactor * Art2aUtils.getSumOfComponents(aContrastEnhancedUnitVector);
        // Set winner index to negative value
        int tmpIndex = -1;
        // Calculate other rho values
        for(int i = 0; i < aNumberOfDetectedClusters; i++) {
            float tmpRhoForCluster = Art2aUtils.getScalarProduct(aContrastEnhancedUnitVector, aClusterMatrix[i]);
            if(tmpRhoForCluster > tmpRhoValue) {
                tmpRhoValue = tmpRhoForCluster;
                tmpIndex = i;
            }
        }
        aRhoWinner.setRhoWinner(tmpRhoValue, tmpIndex);
    }

    /**
     * Sets copied (!) row vector at index in matrix.
     * 
     * @param aMatrix Matrix (MAY BE CHANGED)
     * @param aRowVector Row vector (IS NOT CHANGED)
     * @param anIndex Index of row vector in matrix
     */
    protected static void setRowVector(
        float[][] aMatrix,
        float[] aRowVector,
        int anIndex
    ) {
        float[] tmpNewMatrixRowVector = new float[aRowVector.length];
        Art2aUtils.copyVector(aRowVector, tmpNewMatrixRowVector);
        aMatrix[anIndex] = tmpNewMatrixRowVector;
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
    protected static boolean setContrastEnhancedUnitVector(
        float[] aDataVector,
        float[] aBufferVector,
        Art2aUtils.MinMaxValue[] aMinMaxComponents,
        float aThresholdForContrastEnhancement
    ) {
        // Already allocated memory of aBufferVector is reused
        Art2aUtils.copyVector(aDataVector, aBufferVector);
        // Scale components of vector to interval [0,1]
        Art2aUtils.scaleVector(aBufferVector, aMinMaxComponents);
        // Check length
        if (Art2aUtils.hasLengthOfZero(aBufferVector)) {
            // True: Scaled source vector has a length of zero
            return true;
        } else {
            Art2aUtils.normalizeVector(aBufferVector);
            // Enhance contrast
            if (Art2aUtils.isContrastEnhanced(aBufferVector, aThresholdForContrastEnhancement)) {
                Art2aUtils.normalizeVector(aBufferVector);
            }
            // False: Scaled data vector has a length different from zero
            return false;
        }
    }
    
    /**
     * Scales components of aVectorToBeScaled according to min-max components 
     * to interval [0,1] (see code and method getMinMaxComponents()).
     * 
     * @param aVectorToBeScaled Vector to be scaled (MAY BE CHANGED)
     * @param aMinMaxComponents Min-max components
     */
    protected static void scaleVector(
        float[] aVectorToBeScaled,
        MinMaxValue[] aMinMaxComponents
    ) {
        for(int i = 0; i < aVectorToBeScaled.length; i++) {
            if (aMinMaxComponents[i].minValue() < aMinMaxComponents[i].maxValue()) {
                // Scale component to interval [0,1]
                aVectorToBeScaled[i] = 
                    (aVectorToBeScaled[i] - aMinMaxComponents[i].minValue()) / (aMinMaxComponents[i].maxValue() - aMinMaxComponents[i].minValue());
            } else {
                // Shift component to zero
                aVectorToBeScaled[i] -= aMinMaxComponents[i].minValue();
            }
        }
    }

    /**
     * Randomly shuffles indices from 0 to (anIndices.Length - 1) in 
     * anIndexArray using Fisher-Yates shuffling (i.e. the modern version 
     * introduced by Richard Durstenfeld).
     * Note: No checks are performed.
     *
     * @param anIndexArray Array with indices from 0 to (anIndices.Length - 1)
     * @param aRandomNumberGenerator Random number generator
     */
    protected static void shuffleIndices(
        int[] anIndexArray, 
        Random aRandomNumberGenerator
    ) {
        for (int i = anIndexArray.length - 1; i > 0; i--) {
            // Generate a random index between 0 and i (inclusive)
            int j = aRandomNumberGenerator.nextInt(i + 1);
            // Swap the elements at indices i and j  
            int tmpIntBuffer = anIndexArray[i];
            anIndexArray[i] = anIndexArray[j];
            anIndexArray[j] = tmpIntBuffer;
        }
    }
    //</editor-fold>
    
}
