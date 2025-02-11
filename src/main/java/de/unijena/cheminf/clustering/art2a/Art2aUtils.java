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
 * Library of static, thread-safe (stateless) utility methods for ART-2a clustering.
 * <br><br>
 * Note: No checks are performed.
 * 
 * @author Achim Zielesny
 */
public class Art2aUtils {

    //<editor-fold desc="Constructor">
    /**
     * Constructor
     */
    protected Art2aUtils() {}
    //</editor-fold>

    //<editor-fold desc="Protected methods">
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
            Utils.normalizeVector(aBufferVector);
            // Enhance contrast
            if (Utils.isContrastEnhanced(aBufferVector, aThresholdForContrastEnhancement)) {
                Utils.normalizeVector(aBufferVector);
            }
            // False: Scaled data vector has a length different from zero
            return false;
        }
    }
    //</editor-fold>

}
