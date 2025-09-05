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
 * (stateless) utility methods for ART-2a and ART-2a-Euclid clustering.
 * <br><br>
 * Note: No checks are performed.
 *
 * @author Achim Zielesny
 */
public class Utils {

    //<editor-fold desc="Private static final constants">
    /**
     * Value 1.0
     */
    private static final float ONE = 1.0f;
    //</editor-fold>
    //<editor-fold desc="Protected helper record">
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
    //<editor-fold desc="Protected static helper classes">
    /**
     * Helper class: Rho winner.
     * <br><br>
     * Note: No checks are performed.
     */
    protected static class RhoWinner {

        //<editor-fold desc="Private class variables">
        /**
         * Rho value
         */
        private float rhoValue;
        /**
         * Index of cluster
         */
        private int indexOfCluster;
        //</editor-fold>

        //<editor-fold desc="Constructor">
        /**
         * Constructor
         */
        protected RhoWinner() {}
        //</editor-fold>

        //<editor-fold desc="Protected get/set methods">
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

        //<editor-fold desc="Private class variables">
        /**
         * True: Cluster is removed, false: Otherwise
         */
        private boolean isClusterRemoved;
        /**
         * Number of detected clusters
         */
        private int numberOfDetectedClusters;
        //</editor-fold>

        //<editor-fold desc="Constructor">
        /**
         * Constructor
         */
        protected ClusterRemovalInfo() {}
        //</editor-fold>

        //<editor-fold desc="Protected get/set methods">
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

    //<editor-fold desc="Constructor">
    /**
     * Constructor
     */
    protected Utils() {}
    //</editor-fold>

    // TODO: Make tests for public methods

    //<editor-fold desc="Public static utility methods">
    /**
     * Checks if aDataMatrix is valid.
     *
     * @param aDataMatrix Data matrix with data row vectors (IS NOT CHANGED)
     * @return True if aDataMatrix is valid, false otherwise.
     */
    public static boolean isDataMatrixValid(
            float[][] aDataMatrix
    ) {
        if(aDataMatrix == null || aDataMatrix.length == 0) {
            return false;
        }

        int tmpNumberOfDataVectorComponents = aDataMatrix[0].length;
        if(tmpNumberOfDataVectorComponents < 2) {
            return false;
        }

        for(float[] tmpDataVector : aDataMatrix) {
            if(tmpDataVector == null || tmpDataVector.length == 0) {
                return false;
            }

            if(tmpNumberOfDataVectorComponents != tmpDataVector.length) {
                return false;
            }
        }
        if (Utils.hasNonFiniteComponent(aDataMatrix)) {
            return false;
        }
        return true;
    }

    /**
     * Removes columns from data matrix with non-finite components.
     * Note: If aDataMatrix is null, empty or has an invalid structure
     * nothing is done and false is returned.
     *
     * @param aDataMatrix Data matrix with data row vectors (MAY BE CHANGED)
     * @return True if aDataMatrix was changed (i.e. column removal was
     * performed), false otherwise (i.e. data matrix is unchanged).
     */
    public static boolean isNonFiniteComponentRemoval(
            float[][] aDataMatrix
    ) {
        // <editor-fold desc="Checks">
        if(aDataMatrix == null || aDataMatrix.length == 0) {
            return false;
        }

        if(aDataMatrix[0].length < 2) {
            return false;
        }

        for(float[] tmpDataVector : aDataMatrix) {
            if(tmpDataVector == null || tmpDataVector.length == 0) {
                return false;
            }

            if(aDataMatrix[0].length != tmpDataVector.length) {
                return false;
            }
        }
        //</editor-fold>

        boolean tmpHasNonFiniteComponent = Utils.hasNonFiniteComponent(aDataMatrix);
        if (tmpHasNonFiniteComponent) {
            // Remove columns with non-finite components
            boolean[] tmpColumnsToBeRemoved = new boolean[aDataMatrix[0].length];
            Arrays.fill(tmpColumnsToBeRemoved, false);
            for (float[] tmpDataVector : aDataMatrix) {
                for (int i = 0; i < tmpDataVector.length; i++) {
                    if (!Float.isFinite(tmpDataVector[i])) {
                        tmpColumnsToBeRemoved[i] = true;
                    }
                }
            }
            int tmpNumberOfColumnsToBeRemoved = 0;
            for (boolean tmpColumnToBeRemoved : tmpColumnsToBeRemoved) {
                if (tmpColumnToBeRemoved) {
                    tmpNumberOfColumnsToBeRemoved++;
                }
            }
            for (int i = 0; i < aDataMatrix.length; i++) {
                float[] tmpOldDataVector = aDataMatrix[i];
                float[] tmpNewDataVector = new float[tmpOldDataVector.length - tmpNumberOfColumnsToBeRemoved];
                int tmpIndex = 0;
                for (int j = 0; j < tmpOldDataVector.length; j++) {
                    if (!tmpColumnsToBeRemoved[j]) {
                        tmpNewDataVector[tmpIndex++] = tmpOldDataVector[j];
                    }
                }
                aDataMatrix[i] = tmpNewDataVector;
            }
        }
        return tmpHasNonFiniteComponent;
    }
    //</editor-fold>
    //<editor-fold desc="Protected static utility methods">
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
                tmpSum += (float) Math.sqrt(Utils.getSquaredDistance(aMatrix[anIndicesOfRowVectors[i]], aMatrix[anIndicesOfRowVectors[j]]));
            }
        }
        //note: (n*(n-1)) is always even, so integer division can be used and is also preferred because it is correct
        return tmpSum / (float) (anIndicesOfRowVectors.length * (anIndicesOfRowVectors.length - 1) / 2);
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
     * Calculates the sum of squared differences between the components of the
     * specified vector and a value.
     *
     * @param aVector Vector (IS NOT CHANGED)
     * @param aValue Value
     * @return Sum of squared differences between the components of the
     * specified vector and a value.
     */
    protected static float getSumOfSquaredDifferences(
            float[] aVector,
            float aValue
    ) {
        float tmpSum = 0.0f;
        for (int i = 0; i < aVector.length; i++) {
            float tmpDelta = aVector[i] - aValue;
            // tmpSum += (aVector[i] - aValue)^2;
            tmpSum = Math.fma(tmpDelta, tmpDelta, tmpSum);
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
     * Checks if data matrix has a non-finite component.
     * Note: If aDataMatrix is null or empty nothing is done and false is
     * returned.
     *
     * @param aDataMatrix Data matrix with data row vectors (IS NOT CHANGED)
     * @return True: Data matrix has non-finite component, false: Otherwise
     */
    protected static boolean hasNonFiniteComponent(
            float[][] aDataMatrix
    ) {
        // <editor-fold desc="Checks">
        if(aDataMatrix == null || aDataMatrix.length == 0) {
            return false;
        }
        for(float[] tmpDataVector : aDataMatrix) {
            if(tmpDataVector == null || tmpDataVector.length == 0) {
                return false;
            }
        }
        //</editor-fold>

        for(float[] tmpDataVector : aDataMatrix) {
            for (float tmpComponent : tmpDataVector) {
                if (!Float.isFinite(tmpComponent)) {
                    return true;
                }
            }
        }
        return false;
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
     * Calculates normalized (unit) vector of length 1.
     *
     * @param aVector Vector to be normalized (MAY BE CHANGED)
     */
    protected static void normalizeVector(
            float[] aVector
    ) {
        float tmpInverseVectorLength = ONE / Utils.getVectorLength(aVector);
        for(int i = 0; i < aVector.length; i++) {
            aVector[i] *= tmpInverseVectorLength;
        }
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
     * Calculates contrast enhanced vector.
     *
     * @param aVector Vector to be contrast enhanced (MAY BE CHANGED)
     * @param aThresholdForContrastEnhancement Threshold for contrast enhancement
     */
    protected static void setContrastEnhancement(
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
        Utils.copyVector(aRowVector, tmpNewMatrixRowVector);
        aMatrix[anIndex] = tmpNewMatrixRowVector;
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

    /**
     * Creates clustering-based training and test data vector indices that cover a similar space.
     * The first data vector index of each cluster (which is most similar to the cluster center)
     * is assigned for training, all remaining data vector indices for test.
     * Returns a 2-dimensional jagged integer array where index 0 is the array of
     * training data vector indices and index 1 is the array of test data vector indices.
     * Note: No checks are performed.
     *
     * @return 2-dimensional jagged integer array where index 0 is the array of training data vector
     * indices and index 1 is the array of test data vector indices.
     */
    protected static int[][] getTrainingAndTestIndices(
        Art2aResult anArt2aResult
    ) {
        LinkedList<Integer> tmpTrainingIndexList = new LinkedList<>();
        LinkedList<Integer> tmpTestIndexList = new LinkedList<>();
        for (int i = 0; i < anArt2aResult.getNumberOfDetectedClusters(); i++) {
            int[] tmpClusterRepresentativeIndices = anArt2aResult.getClusterRepresentativeIndices(i);
            for (int k = 0; k < tmpClusterRepresentativeIndices.length; k++) {
                if (k == 0) {
                    tmpTrainingIndexList.add(tmpClusterRepresentativeIndices[k]);
                } else {
                    tmpTestIndexList.add(tmpClusterRepresentativeIndices[k]);
                }
            }
        }
        if (tmpTestIndexList.isEmpty()) {
            return new int[][]
                {
                    tmpTrainingIndexList.stream().mapToInt(Integer::intValue).toArray(),
                    null
                };
        } else {
            return new int[][]
                {
                    tmpTrainingIndexList.stream().mapToInt(Integer::intValue).toArray(),
                    tmpTestIndexList.stream().mapToInt(Integer::intValue).toArray()
                };
        }
    }
    //</editor-fold>

}
