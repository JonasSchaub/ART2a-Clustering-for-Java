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

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Callable that wraps an Art2aKernel instance where the call() method returns 
 * an Art2aResult object. See Art2aKernel for further details.
 *
 * @author Betuel Sevindik, Achim Zielesny
 */
public class Art2aTask implements Callable<Art2aResult> {

    //<editor-fold desc="Private static final LOGGER">
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(Art2aTask.class.getName());
    //</editor-fold>
    //<editor-fold desc="Private final class variables">
    /**
     * ART-2a clustering kernel instance
     */
    private final Art2aKernel art2aClusteringKernel;
    /**
     * Vigilance parameter (must be in interval (0,1))
     */
    private final float vigilance;
    //</editor-fold>

    // <editor-fold desc="Public constructors">
    /**
     * Constructor.
     *
     * @param aDataMatrix Data matrix with data row vectors (IS NOT CHANGED)
     * @param aVigilance Vigilance parameter (must be in interval (0,1))
     * @param aMaximumNumberOfClusters Maximum number of clusters (must be in 
     * interval [2, number of data row vectors of aDataMatrix])
     * @param aMaximumNumberOfEpochs Maximum number of epochs for training 
     * (must be greater zero)
     * @param aConvergenceThreshold Convergence threshold for cluster centroid 
     * similarity (must be in interval (0,1))
     * @param aLearningParameter Learning parameter (must be in interval (0,1))
     * @param anOffsetForContrastEnhancement Offset for contrast enhancement 
     * (must be greater zero)
     * @param aRandomSeed Random seed value for random number generator 
     * (must be greater zero)
     * @param anIsDataPreprocessing True: Data preprocessing is performed, false:
     * Otherwise.
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public Art2aTask(
        float[][] aDataMatrix, 
        float aVigilance,
        int aMaximumNumberOfClusters,
        int aMaximumNumberOfEpochs,
        float aConvergenceThreshold, 
        float aLearningParameter,
        float anOffsetForContrastEnhancement,
        long aRandomSeed,
        boolean anIsDataPreprocessing
    ) throws IllegalArgumentException {
        // <editor-fold desc="Checks">
        if(aVigilance <= 0.0f || aVigilance >= 1.0f) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.Constructor: aVigilance must be in interval (0,1)."
            );
            throw new IllegalArgumentException("Art2aTask.Constructor: aVigilance must be in interval (0,1).");
        }
        //</editor-fold>
        this.vigilance = aVigilance;

        try {
            this.art2aClusteringKernel = 
                new Art2aKernel(
                    aDataMatrix, 
                    aMaximumNumberOfClusters,
                    aMaximumNumberOfEpochs, 
                    aConvergenceThreshold, 
                    aLearningParameter,
                    anOffsetForContrastEnhancement,
                    aRandomSeed,
                    anIsDataPreprocessing
                );
        } catch (IllegalArgumentException anIllegalArgumentException) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.Constructor: Can not instantiate Art2aKernel object."
            );
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                anIllegalArgumentException.toString(), 
                anIllegalArgumentException
            );
            throw anIllegalArgumentException;
        }
    }

    /**
     * Constructor with default values for
     * MAXIMUM_NUMBER_OF_EPOCHS (= 100), CONVERGENCE_THRESHOLD (= 0.99), 
     * LEARNING_PARAMETER (= 0.01), DEFAULT_OFFSET_FOR_CONTRAST_ENHANCEMENT 
     * (= 1.0) and RANDOM_SEED (= 1).
     *
     * @param aDataMatrix Data matrix with data row vectors (IS NOT CHANGED)
     * @param aVigilance Vigilance parameter (must be in interval (0,1))
     * @param aMaximumNumberOfClusters Maximum number of clusters (must be in
     * interval [2, number of data row vectors of aDataMatrix])
     * @param anIsDataPreprocessing True: Data preprocessing is performed, false:
     * Otherwise.
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public Art2aTask(
        float[][] aDataMatrix,
        float aVigilance,
        int aMaximumNumberOfClusters,
        boolean anIsDataPreprocessing
    ) throws IllegalArgumentException {
        // <editor-fold desc="Checks">
        if(aVigilance <= 0.0f || aVigilance >= 1.0f) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.Constructor: aVigilance must be in interval (0,1)."
            );
            throw new IllegalArgumentException("Art2aTask.Constructor: aVigilance must be in interval (0,1).");
        }
        //</editor-fold>
        this.vigilance = aVigilance;

        try {
            this.art2aClusteringKernel = 
                new Art2aKernel(
                    aDataMatrix,
                    aMaximumNumberOfClusters,
                    anIsDataPreprocessing
                );
        } catch (IllegalArgumentException anIllegalArgumentException) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.Constructor: Can not instantiate Art2aKernel object."
            );
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                anIllegalArgumentException.toString(), 
                anIllegalArgumentException
            );
            throw anIllegalArgumentException;
        }
    }
    
    /**
     * Constructor.
     *
     * @param aPreprocessedArt2aData PreprocessedData object created by method
     * Art2aKernel.getPreprocessedData()
     * @param aVigilance Vigilance parameter (must be in interval (0,1))
     * @param aMaximumNumberOfClusters Maximum number of clusters (must be in 
     * interval [2, number of data row vectors of aDataMatrix])
     * @param aMaximumNumberOfEpochs Maximum number of epochs for training 
     * (must be greater zero)
     * @param aConvergenceThreshold Convergence threshold for cluster centroid 
     * similarity (must be in interval (0,1))
     * @param aLearningParameter Learning parameter (must be in interval (0,1))
     * @param aRandomSeed Random seed value for random number generator 
     * (must be greater zero)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public Art2aTask(
        PreprocessedArt2aData aPreprocessedArt2aData,
        float aVigilance,
        int aMaximumNumberOfClusters,
        int aMaximumNumberOfEpochs,
        float aConvergenceThreshold, 
        float aLearningParameter,
        long aRandomSeed
    ) throws IllegalArgumentException {
        // <editor-fold desc="Checks">
        if(aVigilance <= 0.0f || aVigilance >= 1.0f) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.Constructor: aVigilance must be in interval (0,1)."
            );
            throw new IllegalArgumentException("Art2aTask.Constructor: aVigilance must be in interval (0,1).");
        }
        //</editor-fold>
        this.vigilance = aVigilance;

        try {
            this.art2aClusteringKernel = 
                new Art2aKernel(
                    aPreprocessedArt2aData,
                    aMaximumNumberOfClusters,
                    aMaximumNumberOfEpochs, 
                    aConvergenceThreshold, 
                    aLearningParameter,
                    aRandomSeed
                );
        } catch (IllegalArgumentException anIllegalArgumentException) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.Constructor: Can not instantiate Art2aKernel object."
            );
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                anIllegalArgumentException.toString(), 
                anIllegalArgumentException
            );
            throw anIllegalArgumentException;
        }
    }

    /**
     * Constructor with default values for
     * MAXIMUM_NUMBER_OF_EPOCHS (= 100), CONVERGENCE_THRESHOLD (= 0.99), 
     * LEARNING_PARAMETER (= 0.01) and RANDOM_SEED (= 1).
     *
     * @param aPreprocessedArt2aData PreprocessedData object created by method
     * Art2aKernel.getPreprocessedData()
     * @param aVigilance Vigilance parameter (must be in interval (0,1))
     * @param aMaximumNumberOfClusters Maximum number of clusters (must be in
     * interval [2, number of data row vectors of aDataMatrix])
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public Art2aTask(
        PreprocessedArt2aData aPreprocessedArt2aData,
        float aVigilance,
        int aMaximumNumberOfClusters
    ) throws IllegalArgumentException {
        // <editor-fold desc="Checks">
        if(aVigilance <= 0.0f || aVigilance >= 1.0f) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.Constructor: aVigilance must be in interval (0,1)."
            );
            throw new IllegalArgumentException("Art2aTask.Constructor: aVigilance must be in interval (0,1).");
        }
        //</editor-fold>
        this.vigilance = aVigilance;

        try {
            this.art2aClusteringKernel = 
                new Art2aKernel(
                    aPreprocessedArt2aData,
                    aMaximumNumberOfClusters
                );
        } catch (IllegalArgumentException anIllegalArgumentException) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.Constructor: Can not instantiate Art2aKernel object."
            );
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                anIllegalArgumentException.toString(), 
                anIllegalArgumentException
            );
            throw anIllegalArgumentException;
        }
    }
    //</editor-fold>

    // <editor-fold desc="Overriden call() method">
    /**
     * Performs the clustering process.
     * Note: Parallel Rho winner evaluation is disabled.
     *
     * @return Clustering result or null if clustering process could not be 
     * performed.
     */
    @Override
    public Art2aResult call() {
        try {
            // Note: Parallel Rho winner evaluations is disabled: Parameter false.
            return this.art2aClusteringKernel.getClusterResult(this.vigilance, false);
        } catch (Exception anException) {
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                "Art2aTask.call: Can not calculate a cluster result."
            );
            Art2aTask.LOGGER.log(
                Level.SEVERE, 
                anException.toString(), 
                anException
            );
            return null;
        }
    }
    //</editor-fold>

}
