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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for preprocessed data.
 * <br><br>
 * Note: PreprocessedData instances are to be generated with static getPreprocessedData()
 * methods to obtain preprocessed data for faster clustering.
 * <br><br>
 * Note: PreprocessedData is also used for internal data preprocessing, i.e. it
 * may not necessarily contain preprocessed data.
 * A private constructor ensures that original dataMatrix and
 * preprocessedMatrix/dataVectorZeroLengthFlags are mutually exclusive.
 * Use method hasPreprocessedData() to check whether preprocessed
 * preprocessedMatrix/dataVectorZeroLengthFlags are available.
 * <br><br>
 * Note: PreprocessedData is a read-only class, i.e. thread-safe. The same PreprocessedData
 * object may be distributed to several concurrently working clustering tasks without
 * any mutual interference problems.
 * 
 * @author Achim Zielesny
 */
public class PreprocessedData {

    //<editor-fold desc="Private static final LOGGER" defaultstate="collapsed">
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(PreprocessedData.class.getName());
    //</editor-fold>
    //<editor-fold desc="Private final class variables" defaultstate="collapsed">
    /**
     * Original data matrix with data row vectors
     */
    private final float[][] dataMatrix;
    /**
     * Preprocessed matrix
     */
    private final float[][] preprocessedMatrix;
    /**
     * Flags array that indicates if scaled data row vectors have a length 
     * of zero (i.e. where all components are equal to zero, the corresponding 
     * preprocessed vector is set to null in this case). True:
     * Scaled data row vector has a length of zero, false: Otherwise.
     */
    private final boolean[] dataVectorZeroLengthFlags;
    /**
     * Min-max components of original data matrix (see method 
     * Utils.getMinMaxComponents() for data structure)
     */
    private final Utils.MinMaxValue[] minMaxComponentsOfDataMatrix;
    /**
     * Offset for contrast enhancement
     */
    private final float offsetForContrastEnhancement;
    /**
     * True: PreprocessedData object has preprocessed data, false: Otherwise
     */
    private final boolean hasPreprocessedData;
    //</editor-fold>
    
    
    //<editor-fold desc="Private constructor" defaultstate="collapsed">
    /**
     * Private constructor
     * Note: No checks are necessary
     * 
     * @param aDataMatrix Original data matrix with data row vectors (MAY BE NULL)
     * @param aPreprocessedMatrix Preprocessed matrix (MAY BE NULL)
     * @param aDataVectorZeroLengthFlags Flags array that indicates if scaled 
     * data row vectors have a length of zero (i.e. where all components are 
     * equal to zero). True: Scaled data row vector has a length of zero 
     * (corresponding contrast enhanced unit vector is set to null in this 
     * case), false: Otherwise.
     * @param aMinMaxComponentsOfDataMatrix Min-max components of original data 
     * matrix
     * @param anOffsetForContrastEnhancement Offset for contrast enhancement 
     * (must be greater zero)
     * @param aHasPreprocessedData True: PreprocessedData object has preprocessed data,
     * false: Otherwise
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    private PreprocessedData (
        float[][] aDataMatrix,        
        float[][] aPreprocessedMatrix,
        boolean[] aDataVectorZeroLengthFlags,
        Utils.MinMaxValue[] aMinMaxComponentsOfDataMatrix,
        float anOffsetForContrastEnhancement,
        boolean aHasPreprocessedData
    ) {
        this.dataMatrix = aDataMatrix;       
        this.preprocessedMatrix = aPreprocessedMatrix;
        this.dataVectorZeroLengthFlags = aDataVectorZeroLengthFlags;
        this.minMaxComponentsOfDataMatrix = aMinMaxComponentsOfDataMatrix;
        this.offsetForContrastEnhancement = anOffsetForContrastEnhancement;
        this.hasPreprocessedData = aHasPreprocessedData;
    }
    //</editor-fold>
    //<editor-fold desc="Public constructors" defaultstate="collapsed">
    /**
     * Constructor
     * 
     * @param aDataMatrix Original data matrix with data row vectors (NOT 
     * allowed to be null)
     * @param aMinMaxComponentsOfDataMatrix Min-max components of original data 
     * matrix
     * @param anOffsetForContrastEnhancement Offset for contrast enhancement 
     * (must be greater zero)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    protected PreprocessedData (
        float[][] aDataMatrix, 
        Utils.MinMaxValue[] aMinMaxComponentsOfDataMatrix,
        float anOffsetForContrastEnhancement
    ) {
        this (
            aDataMatrix,
            null,
            null,
            aMinMaxComponentsOfDataMatrix,
            anOffsetForContrastEnhancement,
            false
        );
        if (!Utils.isMatrixValid(aDataMatrix)) {
            PreprocessedData.LOGGER.log(
                Level.SEVERE, 
                "PreprocessedData.Constructor: aDataMatrix is invalid."
            );
            throw new IllegalArgumentException("PreprocessedData.Constructor: aDataMatrix is invalid");
        }
        if (aMinMaxComponentsOfDataMatrix == null || aMinMaxComponentsOfDataMatrix.length != aDataMatrix[0].length) {
            PreprocessedData.LOGGER.log(
                Level.SEVERE, 
                "PreprocessedData.Constructor: aMinMaxComponentsOfDataMatrix is invalid."
            );
            throw new IllegalArgumentException("PreprocessedData.Constructor: aMinMaxComponentsOfDataMatrix is invalid");
        }
        if (anOffsetForContrastEnhancement <= 0.0f) {
            PreprocessedData.LOGGER.log(
                Level.SEVERE, 
                "PreprocessedData.Constructor: anOffsetForContrastEnhancement must be greater zero."
            );
            throw new IllegalArgumentException("PreprocessedData.Constructor: anOffsetForContrastEnhancement must be greater zero.");
        }
    }

    /**
     * Constructor
     * 
     * @param aPreprocessedMatrix Preprocessed matrix (NOT allowed to be null)
     * @param aDataVectorZeroLengthFlags Flags array that indicates if scaled 
     * data row vectors have a length of zero (i.e. where all components are 
     * equal to zero). True: Scaled data row vector has a length of zero 
     * (corresponding preprocessed vector is set to null in this
     * case), false: Otherwise.
     * @param aMinMaxComponentsOfDataMatrix Min-max components of original data 
     * matrix
     * @param anOffsetForContrastEnhancement Offset for contrast enhancement 
     * (must be greater zero)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    protected PreprocessedData (
        float[][] aPreprocessedMatrix,
        boolean[] aDataVectorZeroLengthFlags,
        Utils.MinMaxValue[] aMinMaxComponentsOfDataMatrix,
        float anOffsetForContrastEnhancement
    ) {
        this (
            null,
            aPreprocessedMatrix,
            aDataVectorZeroLengthFlags,
            aMinMaxComponentsOfDataMatrix,
            anOffsetForContrastEnhancement,
            true
        );
        if (!Utils.isMatrixValid(aPreprocessedMatrix)) {
            PreprocessedData.LOGGER.log(
                Level.SEVERE, 
                "PreprocessedData.Constructor: aPreprocessedMatrix is invalid."
            );
            throw new IllegalArgumentException("PreprocessedData.Constructor: aPreprocessedMatrix is invalid.");
        }
        if (aDataVectorZeroLengthFlags == null || aDataVectorZeroLengthFlags.length == 0 || aDataVectorZeroLengthFlags.length != aPreprocessedMatrix.length) {
            PreprocessedData.LOGGER.log(
                Level.SEVERE, 
                "PreprocessedData.Constructor: aDataVectorZeroLengthFlags is illegal."
            );
            throw new IllegalArgumentException("PreprocessedData.Constructor: aDataVectorZeroLengthFlags is illegal.");
        }
        if (aMinMaxComponentsOfDataMatrix == null || aMinMaxComponentsOfDataMatrix.length != aPreprocessedMatrix[0].length) {
            PreprocessedData.LOGGER.log(
                Level.SEVERE, 
                "PreprocessedData.Constructor: aMinMaxComponentsOfDataMatrix is invalid."
            );
            throw new IllegalArgumentException("PreprocessedData.Constructor: aMinMaxComponentsOfDataMatrix is invalid");
        }
        if (anOffsetForContrastEnhancement <= 0.0f) {
            PreprocessedData.LOGGER.log(
                Level.SEVERE, 
                "PreprocessedData.Constructor: anOffsetForContrastEnhancement must be greater zero."
            );
            throw new IllegalArgumentException("PreprocessedData.Constructor: anOffsetForContrastEnhancement must be greater zero.");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Protected get/has methods" defaultstate="collapsed">
    /**
     * Original data matrix with data row vectors
     * 
     * @return Original data matrix with data row vectors or null if 
     * hasPreprocessedData() returns true
     */
    protected float[][] getDataMatrix() {
        return this.dataMatrix;
    }

    /**
     * Matrix of contrast enhanced unit vectors
     * 
     * @return Matrix of contrast enhanced unit vectors or null if 
     * hasPreprocessedData() returns false
     */
    protected float[][] getPreprocessedMatrix() {
        return this.preprocessedMatrix;
    }

    /**
     * Flags array that indicates if scaled data row vectors have a length 
     * of zero (i.e. where all components are equal to zero, the corresponding 
     * contrast enhanced unit vector is set to null in this case). True: 
     * Scaled data row vector has a length of zero, false: Otherwise.
     * 
     * @return Array with flags or null if hasPreprocessedData() returns false
     */
    protected boolean[] getDataVectorZeroLengthFlags() {
        return this.dataVectorZeroLengthFlags;
    }

    /**
     * Min-max components of original data matrix (see method Utils.getMinMaxComponents() for data structure)
     * 
     * @return Min-max components of original data matrix
     */
    protected Utils.MinMaxValue[] getMinMaxComponentsOfDataMatrix() {
        return this.minMaxComponentsOfDataMatrix;
    }
    
    /**
     * Returns if Art2aData object has preprocessed data, i.e. 
     * contrastEnhancedUnitMatrix and dataVectorZeroLengthFlags are defined.
     * 
     * @return True: Art2aData object has preprocessed data, false: Otherwise
     */
    protected boolean hasPreprocessedData() {
        return this.hasPreprocessedData;
    }

    /**
     * Returns offset for contrast enhancement
     * 
     * @return Offset for contrast enhancement
     */
    protected float getOffsetForContrastEnhancement() {
        return this.offsetForContrastEnhancement;
    }
    //</editor-fold>

}