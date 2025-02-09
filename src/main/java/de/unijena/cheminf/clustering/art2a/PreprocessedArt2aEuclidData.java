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

/**
 * Class for preprocessed ART-2a-Euclid data
 */
public class PreprocessedArt2aEuclidData extends PreprocessedData {

    //<editor-fold desc="Public constructors" defaultstate="collapsed">
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
    protected PreprocessedArt2aEuclidData (
            float[][] aPreprocessedMatrix,
            boolean[] aDataVectorZeroLengthFlags,
            Utils.MinMaxValue[] aMinMaxComponentsOfDataMatrix,
            float anOffsetForContrastEnhancement
    ) {
        super (
                aPreprocessedMatrix,
                aDataVectorZeroLengthFlags,
                aMinMaxComponentsOfDataMatrix,
                anOffsetForContrastEnhancement
        );
    }
    //</editor-fold>

}
