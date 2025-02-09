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

import de.unijena.cheminf.clustering.art2a.Art2aEuclidKernel;
import de.unijena.cheminf.clustering.art2a.Art2aEuclidResult;
import de.unijena.cheminf.clustering.art2a.Art2aEuclidTask;
import de.unijena.cheminf.clustering.art2a.Art2aEuclidData;
import de.unijena.cheminf.clustering.art2a.Art2aEuclidUtils;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for ART-2a-Euclid clustering.
 *
 * @author Achim Zielesny
 */
public class Art2aEuclidTest {

    /**
     * Test method for development purposes only
     */
    @Test
    public void test_Development_IrisFlowerData() {
        System.out.println("---------------------------------");
        System.out.println("test_Development_IrisFlowerData()");
        System.out.println("---------------------------------");
        float[][] tmpIrisFlowerDataMatrix = this.getIrisFlowerDataMatrix();
        
        // float[] tmpVigilances = new float[] {0.01f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 0.99f};
        float[] tmpVigilances = new float[] {0.1f};
        boolean tmpIsClusterAnalysis = true;
        int tmpMaximumNumberOfClusters = 150;
        boolean tmpIsDataPreprocessing = false;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;

        for (float tmpVigilance : tmpVigilances) {
            System.out.println("  Vigilance parameter = " + String.valueOf(tmpVigilance));
            Art2aEuclidKernel tmpArt2aEuclidKernel = 
                new Art2aEuclidKernel(
                    tmpIrisFlowerDataMatrix, 
                    tmpMaximumNumberOfClusters,
                    tmpMaximumNumberOfEpochs,
                    tmpConvergenceThreshold,
                    tmpLearningParameter,
                    tmpOffsetForContrastEnhancement,
                    tmpRandomSeed,
                    tmpIsDataPreprocessing
                );
            Assertions.assertNotNull(tmpArt2aEuclidKernel);
            Art2aEuclidResult tmpArt2aEuclidResult = null;
            try {
                tmpArt2aEuclidResult = tmpArt2aEuclidKernel.getClusterResult(tmpVigilance);
            } catch (Exception anException) {
                Assertions.assertTrue(false);
            }
            Assertions.assertNotNull(tmpArt2aEuclidResult);
            int tmpNumberOfDetectedClusters = tmpArt2aEuclidResult.getNumberOfDetectedClusters();
            System.out.println("  - Number of detected clusters = " + String.valueOf(tmpArt2aEuclidResult.getNumberOfDetectedClusters()));
            System.out.println("  - Number of epochs            = " + String.valueOf(tmpArt2aEuclidResult.getNumberOfEpochs()));
            if (tmpIsClusterAnalysis) {
                for (int i = 0; i < tmpNumberOfDetectedClusters; i++) {
                    System.out.println("  - Cluster " + String.valueOf(i) + " of size " + String.valueOf(tmpArt2aEuclidResult.getClusterSize(i)));
                    int[] tmpDataVectorIndicesOfCluster = tmpArt2aEuclidResult.getDataVectorIndicesOfCluster(i);
                    System.out.println("    " + this.getStringFromIntArray(tmpDataVectorIndicesOfCluster));
                }
                for (int i = 0; i < tmpNumberOfDetectedClusters; i++) {
                    for (int j = i + 1; j < tmpNumberOfDetectedClusters; j++) {
                        System.out.println("  - Distance between cluster " + String.valueOf(i) + " and cluster " + String.valueOf(j) + " = " + String.valueOf(tmpArt2aEuclidResult.getDistanceBetweenClusters(i, j)));
                    }
                }
            }
            System.out.println("");
        }
    }

    /**
     * Test method for development purposes only
     */
    @Test
    public void test_Development_CombinedGaussianCouldData() {
        System.out.println("--------------------------------------------");
        System.out.println("test_Development_CombinedGaussianCouldData()");
        System.out.println("--------------------------------------------");
        int tmpNumberOfDimensions = 10;
        int tmpNumberOfGaussianCloudVectors = 100;
        float tmpStandardDeviation = 0.1f;
        Random tmpRandomNumberGenerator = new Random(1L);
        float[][] tmpCombinedGaussianCloudDataMatrix = 
            this.getCombinedGaussianCloudMatrix(
                tmpNumberOfDimensions, 
                tmpNumberOfGaussianCloudVectors, 
                tmpStandardDeviation, 
                tmpRandomNumberGenerator
            );

        float tmpVigilance = 0.1f;
        int tmpMaximumNumberOfClusters = 1000;
        boolean tmpIsDataPreprocessing = false;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;

        long tmpStart = System.currentTimeMillis();
        Art2aEuclidKernel tmpArt2aEuclidKernel = 
            new Art2aEuclidKernel(
                tmpCombinedGaussianCloudDataMatrix, 
                tmpMaximumNumberOfClusters,
                tmpMaximumNumberOfEpochs,
                tmpConvergenceThreshold,
                tmpLearningParameter,
                tmpOffsetForContrastEnhancement,
                tmpRandomSeed,
                tmpIsDataPreprocessing
            );
        Art2aEuclidResult tmpArt2aEuclidResult = null;
        try {
            tmpArt2aEuclidResult = tmpArt2aEuclidKernel.getClusterResult(tmpVigilance);
        } catch (Exception anException) {
            Assertions.assertTrue(false);
        }
        long tmpEnd = System.currentTimeMillis();

        System.out.println("  Number of data vectors      = " + String.valueOf(tmpNumberOfDimensions * tmpNumberOfGaussianCloudVectors));
        System.out.println("  Elapsed time in ms          = " + String.valueOf(tmpEnd - tmpStart));
        Assertions.assertNotNull(tmpArt2aEuclidKernel);
        Assertions.assertNotNull(tmpArt2aEuclidResult);
        int tmpNumberOfDetectedClusters = tmpArt2aEuclidResult.getNumberOfDetectedClusters();
        System.out.println("  Number of detected clusters = " + String.valueOf(tmpArt2aEuclidResult.getNumberOfDetectedClusters()));
        System.out.println("  Number of epochs            = " + String.valueOf(tmpArt2aEuclidResult.getNumberOfEpochs()));
        for (int i = 0; i < tmpNumberOfDetectedClusters; i++) {
            System.out.println("  Cluster " + String.valueOf(i) + " of size " + String.valueOf(tmpArt2aEuclidResult.getClusterSize(i)));
            System.out.println("  - Representative  = " + String.valueOf(tmpArt2aEuclidResult.getClusterRepresentativeIndex(i)));
            System.out.println("  - Representatives = " + this.getStringFromIntArray(tmpArt2aEuclidResult.getClusterRepresentativeIndices(i)));
            int[] tmpDataVectorIndicesOfCluster = tmpArt2aEuclidResult.getDataVectorIndicesOfCluster(i);
            System.out.println("  " + this.getStringFromIntArray(tmpDataVectorIndicesOfCluster));
        }
        for (int i = 0; i < tmpNumberOfDetectedClusters; i++) {
            for (int j = i + 1; j < tmpNumberOfDetectedClusters; j++) {
                System.out.println("  Distance between cluster " + String.valueOf(i) + " and cluster " + String.valueOf(j) + " = " + String.valueOf(tmpArt2aEuclidResult.getDistanceBetweenClusters(i, j)));
            }
        }
    }

    /**
     * Test method for development purposes only
     */
    @Test
    public void test_Development_GetRepresentatives() {
        System.out.println("-------------------------------------");
        System.out.println("test_Development_GetRepresentatives()");
        System.out.println("-------------------------------------");
        float[][] tmpIrisFlowerDataMatrix = this.getIrisFlowerDataMatrix();
        int tmpMaximumNumberOfClusters = 150;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;
        boolean tmpIsDataPreprocessing = false;

        float tmpVigilanceMin = 0.0001f;
        float tmpVigilanceMax = 0.9999f;
        int tmpNumberOfTrialSteps = 32;
        
        Art2aEuclidKernel tmpArt2aEuclidKernel = 
            new Art2aEuclidKernel(
                tmpIrisFlowerDataMatrix, 
                tmpMaximumNumberOfClusters,
                tmpMaximumNumberOfEpochs,
                tmpConvergenceThreshold,
                tmpLearningParameter,
                tmpOffsetForContrastEnhancement,
                tmpRandomSeed,
                tmpIsDataPreprocessing
            );

        try {
            int[] tmpBestRepresentatives = tmpArt2aEuclidKernel.getBestRepresentatives(tmpIrisFlowerDataMatrix, 2, tmpIrisFlowerDataMatrix.length);
            Arrays.sort(tmpBestRepresentatives);
            System.out.println(
                String.valueOf(tmpBestRepresentatives.length) + " best representatives = " + this.getStringFromIntArray(tmpBestRepresentatives)
            );
        } catch (Exception anException) {
            Assertions.assertTrue(false);
        }
        
        int[] tmpAllIndices = new int[150];
        for (int i = 0; i < 150; i++) {
            tmpAllIndices[i] = i;
        }
        float tmpBaseMeanDistance = Art2aEuclidUtils.getMeanDistance(tmpIrisFlowerDataMatrix, tmpAllIndices);
        System.out.println(
            "Base mean distance = " + String.valueOf(tmpBaseMeanDistance)
        );
        for (int tmpNumberOfRepresentatives = 2; tmpNumberOfRepresentatives < tmpIrisFlowerDataMatrix.length; tmpNumberOfRepresentatives++) {
            try {
                int[] tmpRepresentatives = 
                    tmpArt2aEuclidKernel.getRepresentatives(
                        tmpNumberOfRepresentatives, 
                        tmpVigilanceMin, 
                        tmpVigilanceMax, 
                        tmpNumberOfTrialSteps
                    );
                if (tmpNumberOfRepresentatives == tmpRepresentatives.length) {
                    Arrays.sort(tmpRepresentatives);
                    float tmpMeanDistance = Art2aEuclidUtils.getMeanDistance(tmpIrisFlowerDataMatrix, tmpRepresentatives);
                    System.out.println(
                        String.valueOf(tmpNumberOfRepresentatives) + 
                        " Representatives (Mean distance = " +
                        String.valueOf(tmpMeanDistance) + 
                        ") = " + 
                        this.getStringFromIntArray(tmpRepresentatives)
                    );
                }
            } catch (Exception anException) {
                Assertions.assertTrue(false);
            }
        }
    }

    /**
     * Tests Art2aEuclidKernel method getRepresentatives().
     */
    @Test
    public void test_GetRepresentatives() {
        System.out.println("-------------------------");
        System.out.println("test_GetRepresentatives()");
        System.out.println("-------------------------");
        float[][] tmpIrisFlowerDataMatrix = this.getIrisFlowerDataMatrix();
        int tmpMaximumNumberOfClusters = 150;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;
        boolean tmpIsDataPreprocessing = false;

        int tmpNumberOfRepresentatives = 10;
        float tmpVigilanceMin = 0.0001f;
        float tmpVigilanceMax = 0.9999f;
        int tmpNumberOfTrialSteps = 32;
        
        Art2aEuclidKernel tmpArt2aEuclidKernel = 
            new Art2aEuclidKernel(
                tmpIrisFlowerDataMatrix, 
                tmpMaximumNumberOfClusters,
                tmpMaximumNumberOfEpochs,
                tmpConvergenceThreshold,
                tmpLearningParameter,
                tmpOffsetForContrastEnhancement,
                tmpRandomSeed,
                tmpIsDataPreprocessing
            );
        try {
            int[] tmpRepresentatives = 
                tmpArt2aEuclidKernel.getRepresentatives(
                    tmpNumberOfRepresentatives, 
                    tmpVigilanceMin, 
                    tmpVigilanceMax, 
                    tmpNumberOfTrialSteps
                );
            System.out.println(
                String.valueOf(tmpNumberOfRepresentatives) + 
                " wanted Representatives, " + 
                String.valueOf(tmpRepresentatives.length) + 
                " generated = " + 
                this.getStringFromIntArray(tmpRepresentatives)
            );
            for (int i = 0; i < tmpRepresentatives.length; i++) {
                for (int j = i + 1; j < tmpRepresentatives.length; j++) {
                    System.out.println(
                        "Distance between representatives " + 
                        String.valueOf(i) + 
                        " and representative " + 
                        String.valueOf(j) + 
                        "= " + 
                        String.valueOf(
                            Math.sqrt(
                                Art2aEuclidUtils.getSquaredDistance(
                                    tmpIrisFlowerDataMatrix[tmpRepresentatives[i]], 
                                    tmpIrisFlowerDataMatrix[tmpRepresentatives[j]]
                                )
                            )
                        )
                    );
                }
            }
            Assertions.assertEquals(tmpRepresentatives.length, tmpNumberOfRepresentatives);
        } catch (Exception anException) {
            Assertions.assertTrue(false);
        }
    }

    /**
     * Test for perfect clustering
     */
    @Test
    public void test_PerfectClustering() {
        System.out.println("------------------------");
        System.out.println("test_PerfectClustering()");
        System.out.println("------------------------");
        int tmpNumberOfDimensions = 10;
        int tmpNumberOfGaussianCloudVectors = 1000;
        float tmpStandardDeviation = 0.01f;
        Random tmpRandomNumberGenerator = new Random(1L);
        float[][] tmpCombinedGaussianCloudDataMatrix = 
            this.getCombinedGaussianCloudMatrix(
                tmpNumberOfDimensions, 
                tmpNumberOfGaussianCloudVectors, 
                tmpStandardDeviation, 
                tmpRandomNumberGenerator
            );

        float tmpVigilance = 0.1f;
        int tmpMaximumNumberOfClusters = 100;
        boolean tmpIsDataPreprocessing = false;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;

        Art2aEuclidKernel tmpArt2aEuclidKernel = 
            new Art2aEuclidKernel(
                tmpCombinedGaussianCloudDataMatrix, 
                tmpMaximumNumberOfClusters,
                tmpMaximumNumberOfEpochs,
                tmpConvergenceThreshold,
                tmpLearningParameter,
                tmpOffsetForContrastEnhancement,
                tmpRandomSeed,
                tmpIsDataPreprocessing
            );
        Art2aEuclidResult tmpArt2aEuclidResult = null;
        try {
            tmpArt2aEuclidResult = tmpArt2aEuclidKernel.getClusterResult(tmpVigilance);
        } catch (Exception anException) {
            Assertions.assertTrue(false);
        }

        Assertions.assertEquals(tmpArt2aEuclidResult.getNumberOfDetectedClusters(), tmpNumberOfDimensions);
        Assertions.assertTrue(tmpArt2aEuclidResult.getNumberOfEpochs() < tmpMaximumNumberOfEpochs);
        for (int i = 0; i < tmpArt2aEuclidResult.getNumberOfDetectedClusters(); i++) {
            Assertions.assertEquals(tmpArt2aEuclidResult.getClusterSize(i), tmpNumberOfGaussianCloudVectors);
            int[] tmpDataVectorIndicesOfCluster = tmpArt2aEuclidResult.getDataVectorIndicesOfCluster(i);
            int[] tmpClusterRepresentativeIndices = tmpArt2aEuclidResult.getClusterRepresentativeIndices(i);
            Assertions.assertEquals(tmpArt2aEuclidResult.getClusterRepresentativeIndex(i), tmpClusterRepresentativeIndices[0]);
            Arrays.sort(tmpDataVectorIndicesOfCluster);
            Arrays.sort(tmpClusterRepresentativeIndices);
            Assertions.assertArrayEquals(tmpDataVectorIndicesOfCluster, tmpClusterRepresentativeIndices);
        }
        Assertions.assertFalse(tmpArt2aEuclidResult.isClusterOverflow());
        for (int i = 0; i < tmpArt2aEuclidResult.getNumberOfDetectedClusters(); i++) {
            Assertions.assertEquals(tmpArt2aEuclidResult.getClusterRepresentativeIndex(i), tmpArt2aEuclidResult.getClusterRepresentativeIndices(i)[0]);
        }
    }
    
    /**
     * Tests that clustering with and without preprocessing has identical 
     * results.
     */
    @Test
    public void test_Preprocessing() {
        System.out.println("--------------------");
        System.out.println("test_Preprocessing()");
        System.out.println("--------------------");
        float[][] tmpIrisFlowerDataMatrix = this.getIrisFlowerDataMatrix();
        float[] tmpVigilances = new float[] {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f};
        int tmpMaximumNumberOfClusters = 150;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;

        for (float tmpVigilance : tmpVigilances) {
            // No preprocessing
            boolean tmpIsDataPreprocessing = false;
            Art2aEuclidKernel tmpArt2aEuclidKernelWithoutPreprocessing = 
                new Art2aEuclidKernel(
                    tmpIrisFlowerDataMatrix, 
                    tmpMaximumNumberOfClusters,
                    tmpMaximumNumberOfEpochs,
                    tmpConvergenceThreshold,
                    tmpLearningParameter,
                    tmpOffsetForContrastEnhancement,
                    tmpRandomSeed,
                    tmpIsDataPreprocessing
                );
            Art2aEuclidResult tmpArt2aEuclidResultWithoutPreprocessing = null;
            try {
                tmpArt2aEuclidResultWithoutPreprocessing = tmpArt2aEuclidKernelWithoutPreprocessing.getClusterResult(tmpVigilance);
            } catch (Exception anException) {
                Assertions.assertTrue(false);
            }

            // Preprocessing
            tmpIsDataPreprocessing = true;
            Art2aEuclidKernel tmpArt2aEuclidKernelWithPreprocessing = 
                new Art2aEuclidKernel(
                    tmpIrisFlowerDataMatrix, 
                    tmpMaximumNumberOfClusters,
                    tmpMaximumNumberOfEpochs,
                    tmpConvergenceThreshold,
                    tmpLearningParameter,
                    tmpOffsetForContrastEnhancement,
                    tmpRandomSeed,
                    tmpIsDataPreprocessing
                );
            Art2aEuclidResult tmpArt2aEuclidResultWithPreprocessing = null;
            try {
                tmpArt2aEuclidResultWithPreprocessing = tmpArt2aEuclidKernelWithPreprocessing.getClusterResult(tmpVigilance);
            } catch (Exception anException) {
                Assertions.assertTrue(false);
            }
            
            // Assert that results without and with preprocessing are identical
            Assertions.assertTrue(
                tmpArt2aEuclidResultWithoutPreprocessing.getNumberOfDetectedClusters() == 
                    tmpArt2aEuclidResultWithPreprocessing.getNumberOfDetectedClusters()
            );
            Assertions.assertTrue(
                tmpArt2aEuclidResultWithoutPreprocessing.getNumberOfEpochs() ==  
                    tmpArt2aEuclidResultWithPreprocessing.getNumberOfEpochs()
            );
            
            int tmpNumberOfDetectedClusters = tmpArt2aEuclidResultWithoutPreprocessing.getNumberOfDetectedClusters();
            for (int i = 0; i < tmpNumberOfDetectedClusters; i++) {
                Assertions.assertArrayEquals(
                    tmpArt2aEuclidResultWithoutPreprocessing.getDataVectorIndicesOfCluster(i), 
                    tmpArt2aEuclidResultWithPreprocessing.getDataVectorIndicesOfCluster(i)
                );
            }
            for (int i = 0; i < tmpNumberOfDetectedClusters; i++) {
                for (int j = i + 1; j < tmpNumberOfDetectedClusters; j++) {
                    Assertions.assertTrue(
                        tmpArt2aEuclidResultWithoutPreprocessing.getDistanceBetweenClusters(i, j) == 
                            tmpArt2aEuclidResultWithPreprocessing.getDistanceBetweenClusters(i, j)
                    );
                }
            }
        }
    }

    /**
     * Test that generated Art2aEuclidData object leads to identical clustering 
 results.
     */
    @Test
    public void test_Art2aEuclidData() {
        System.out.println("----------------");
        System.out.println("test_Art2aEuclidData()");
        System.out.println("----------------");
        float[][] tmpIrisFlowerDataMatrix = this.getIrisFlowerDataMatrix();
        float[] tmpVigilances = new float[] {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f};
        int tmpMaximumNumberOfClusters = 150;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;
        for (float tmpVigilance : tmpVigilances) {
            // No preprocessing
            boolean tmpIsDataPreprocessing = false;
            Art2aEuclidKernel tmpArt2aEuclidKernelWithoutPreprocessing = 
                new Art2aEuclidKernel(
                    tmpIrisFlowerDataMatrix, 
                    tmpMaximumNumberOfClusters,
                    tmpMaximumNumberOfEpochs,
                    tmpConvergenceThreshold,
                    tmpLearningParameter,
                    tmpOffsetForContrastEnhancement,
                    tmpRandomSeed,
                    tmpIsDataPreprocessing
                );
            Art2aEuclidResult tmpArt2aEuclidResultWithoutPreprocessing = null;
            try {
                tmpArt2aEuclidResultWithoutPreprocessing = tmpArt2aEuclidKernelWithoutPreprocessing.getClusterResult(tmpVigilance);
            } catch (Exception anException) {
                Assertions.assertTrue(false);
            }

            // Preprocessed Art2aEuclidData
            Art2aEuclidData tmpArt2aEuclidData = Art2aEuclidKernel.getArt2aEuclidData(tmpIrisFlowerDataMatrix, tmpOffsetForContrastEnhancement);
            Art2aEuclidKernel tmpArt2aEuclidKernelWithArt2aEuclidData = 
                new Art2aEuclidKernel(
                    tmpArt2aEuclidData, 
                    tmpMaximumNumberOfClusters,
                    tmpMaximumNumberOfEpochs,
                    tmpConvergenceThreshold,
                    tmpLearningParameter,
                    tmpRandomSeed
                );
            Art2aEuclidResult tmpArt2aEuclidResultWithArt2aEuclidData = null;
            try {
                tmpArt2aEuclidResultWithArt2aEuclidData = tmpArt2aEuclidKernelWithArt2aEuclidData.getClusterResult(tmpVigilance);
            } catch (Exception anException) {
                Assertions.assertTrue(false);
            }

            // Assert that results without preprocessing and preprocessed 
            // Art2aEuclidData are identical
            Assertions.assertTrue(
                tmpArt2aEuclidResultWithoutPreprocessing.getNumberOfDetectedClusters() == 
                    tmpArt2aEuclidResultWithArt2aEuclidData.getNumberOfDetectedClusters()
            );
            Assertions.assertTrue(
                tmpArt2aEuclidResultWithoutPreprocessing.getNumberOfEpochs() ==  
                    tmpArt2aEuclidResultWithArt2aEuclidData.getNumberOfEpochs()
            );
            
            int tmpNumberOfDetectedClusters = tmpArt2aEuclidResultWithoutPreprocessing.getNumberOfDetectedClusters();
            for (int i = 0; i < tmpNumberOfDetectedClusters; i++) {
                Assertions.assertArrayEquals(
                    tmpArt2aEuclidResultWithoutPreprocessing.getDataVectorIndicesOfCluster(i), 
                    tmpArt2aEuclidResultWithArt2aEuclidData.getDataVectorIndicesOfCluster(i)
                );
            }
            for (int i = 0; i < tmpNumberOfDetectedClusters; i++) {
                for (int j = i + 1; j < tmpNumberOfDetectedClusters; j++) {
                    Assertions.assertTrue(
                        tmpArt2aEuclidResultWithoutPreprocessing.getDistanceBetweenClusters(i, j) == 
                            tmpArt2aEuclidResultWithArt2aEuclidData.getDistanceBetweenClusters(i, j)
                    );
                }
            }
        }
    }

    /**
     * Tests that sequential and parallelized clustering leads to identical
     * results.
     */
    @Test
    public void test_ParallelClustering() {
        System.out.println("-------------------------");
        System.out.println("test_ParallelClustering()");
        System.out.println("-------------------------");
        float[][] tmpIrisFlowerDataMatrix = this.getIrisFlowerDataMatrix();
        float[] tmpVigilances = new float[] {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f};
        int tmpMaximumNumberOfClusters = 150;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;
        
        // Sequential clustering one after another
        Art2aEuclidResult[] tmpSequentialResults = new Art2aEuclidResult[tmpVigilances.length];
        int tmpIndex = 0;
        for (float tmpVigilance : tmpVigilances) {
            boolean tmpIsDataPreprocessing = false;
            Art2aEuclidKernel tmpArt2aEuclidKernelWithoutPreprocessing = 
                new Art2aEuclidKernel(
                    tmpIrisFlowerDataMatrix, 
                    tmpMaximumNumberOfClusters,
                    tmpMaximumNumberOfEpochs,
                    tmpConvergenceThreshold,
                    tmpLearningParameter,
                    tmpOffsetForContrastEnhancement,
                    tmpRandomSeed,
                    tmpIsDataPreprocessing
                );
            try {
                tmpSequentialResults[tmpIndex++] = tmpArt2aEuclidKernelWithoutPreprocessing.getClusterResult(tmpVigilance);
            } catch (Exception anException) {
                Assertions.assertTrue(false);
            }
        }

        // Concurrent (parallelized) clustering
        LinkedList<Art2aEuclidTask> tmpArt2aEuclidTaskList = new LinkedList<>();
        Art2aEuclidData tmpArt2aEuclidData = Art2aEuclidKernel.getArt2aEuclidData(tmpIrisFlowerDataMatrix, tmpOffsetForContrastEnhancement);
        for (float tmpVigilance : tmpVigilances) {
            tmpArt2aEuclidTaskList.add(new Art2aEuclidTask(
                    tmpArt2aEuclidData, 
                    tmpVigilance, 
                    tmpMaximumNumberOfClusters,
                    tmpMaximumNumberOfEpochs,
                    tmpConvergenceThreshold,
                    tmpLearningParameter,
                    tmpRandomSeed
                )
            );
        }
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(tmpVigilances.length);
        List<Future<Art2aEuclidResult>> tmpFutureList = null;
        try {
            tmpFutureList = tmpExecutorService.invokeAll(tmpArt2aEuclidTaskList);
        } catch (InterruptedException e) {
            System.out.println("test_ParallelClustering: InterruptedException occurred.");
        }
        tmpExecutorService.shutdown();
        Art2aEuclidResult[] tmpParallelResults = new Art2aEuclidResult[tmpVigilances.length];
        tmpIndex = 0;
        for (Future<Art2aEuclidResult> tmpFuture : tmpFutureList) {
            try {
                tmpParallelResults[tmpIndex++] = tmpFuture.get();
            } catch (Exception e) {
                System.out.println("test_ParallelClustering: Exception occurred.");
            }
        }
        
        // Assert that sequential results without preprocessing and concurrent 
        // results with preprocessed Art2aEuclidData are identical
        for (int i = 0; i < tmpVigilances.length; i++) {
            Assertions.assertTrue(
                tmpSequentialResults[i].getNumberOfDetectedClusters() == 
                    tmpParallelResults[i].getNumberOfDetectedClusters()
            );

            Assertions.assertTrue(
                tmpSequentialResults[i].getNumberOfEpochs() ==  
                    tmpParallelResults[i].getNumberOfEpochs()
            );

            int tmpNumberOfDetectedClusters = tmpSequentialResults[i].getNumberOfDetectedClusters();
            for (int j = 0; j < tmpNumberOfDetectedClusters; j++) {
                Assertions.assertArrayEquals(
                    tmpSequentialResults[i].getDataVectorIndicesOfCluster(j), 
                    tmpParallelResults[i].getDataVectorIndicesOfCluster(j)
                );
            }

            for (int j = 0; j < tmpNumberOfDetectedClusters; j++) {
                for (int k = j + 1; k < tmpNumberOfDetectedClusters; k++) {
                    Assertions.assertTrue(
                        tmpSequentialResults[i].getDistanceBetweenClusters(j, k) == 
                            tmpParallelResults[i].getDistanceBetweenClusters(j, k)
                    );
                }
            }
        }        
    }

    /**
     * Tests that sequential and parallelized clustering with 
 Art2aEuclidKernel.getClusterResults() leads to identical results.
     */
    @Test
    public void test_ParallelClusteringWithGetGlusterResults() {
        System.out.println("----------------------------------------------");
        System.out.println("test_ParallelClusteringWithGetGlusterResults()");
        System.out.println("----------------------------------------------");
        float[][] tmpIrisFlowerDataMatrix = this.getIrisFlowerDataMatrix();
        float[] tmpVigilances = new float[] {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f};
        int tmpMaximumNumberOfClusters = 150;
        int tmpMaximumNumberOfEpochs = 100;
        float tmpConvergenceThreshold = 0.1f;
        float tmpLearningParameter = 0.01f;
        float tmpOffsetForContrastEnhancement = 1.0f;
        long tmpRandomSeed = 1L;
        boolean tmpIsDataPreprocessing = false;

        Art2aEuclidKernel tmpArt2aEuclidKernel = 
            new Art2aEuclidKernel(
                tmpIrisFlowerDataMatrix, 
                tmpMaximumNumberOfClusters,
                tmpMaximumNumberOfEpochs,
                tmpConvergenceThreshold,
                tmpLearningParameter,
                tmpOffsetForContrastEnhancement,
                tmpRandomSeed,
                tmpIsDataPreprocessing
            );

        // Sequential clustering one after another
        Art2aEuclidResult[] tmpSequentialResults = null;
        try {
            tmpSequentialResults = tmpArt2aEuclidKernel.getClusterResults(tmpVigilances, 0);
        } catch (Exception anException) {
            Assertions.assertTrue(false);
        }

        // Concurrent (parallel) clustering
        int tmpNumberOfParallelCalculationThreads = 2;
        Art2aEuclidResult[] tmpParallelResults = null;
        try {
            tmpParallelResults = tmpArt2aEuclidKernel.getClusterResults(tmpVigilances, tmpNumberOfParallelCalculationThreads);
        } catch (Exception anException) {
            Assertions.assertTrue(false);
        }
        
        // Assert that sequential results without preprocessing and concurrent 
        // results with preprocessed Art2aEuclidData are identical
        for (int i = 0; i < tmpVigilances.length; i++) {
            Assertions.assertTrue(
                tmpSequentialResults[i].getNumberOfDetectedClusters() == 
                    tmpParallelResults[i].getNumberOfDetectedClusters()
            );

            Assertions.assertTrue(
                tmpSequentialResults[i].getNumberOfEpochs() ==  
                    tmpParallelResults[i].getNumberOfEpochs()
            );

            int tmpNumberOfDetectedClusters = tmpSequentialResults[i].getNumberOfDetectedClusters();
            for (int j = 0; j < tmpNumberOfDetectedClusters; j++) {
                Assertions.assertArrayEquals(
                    tmpSequentialResults[i].getDataVectorIndicesOfCluster(j), 
                    tmpParallelResults[i].getDataVectorIndicesOfCluster(j)
                );
            }

            for (int j = 0; j < tmpNumberOfDetectedClusters; j++) {
                for (int k = j + 1; k < tmpNumberOfDetectedClusters; k++) {
                    Assertions.assertTrue(
                        tmpSequentialResults[i].getDistanceBetweenClusters(j, k) == 
                            tmpParallelResults[i].getDistanceBetweenClusters(j, k)
                    );
                }
            }
        }        
    }
    
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Returns int array as a string.
     * Note: No checks are performed.
     * 
     * @param anIntArray Int array
     * @return The int array as a string
     */
    private String getStringFromIntArray(
        int[] anIntArray
    ) {
        // Assumes 6 characters for int number plus comma plus space
        StringBuilder tmpStringBuilder = new StringBuilder(anIntArray.length * 6);
        tmpStringBuilder.append(String.valueOf(anIntArray[0]));
        for (int i = 1; i < anIntArray.length; i++) {
            tmpStringBuilder.append(", ");
            tmpStringBuilder.append(String.valueOf(anIntArray[i]));
        }
        return tmpStringBuilder.toString();
    }
    
    /**
     * Compares two arrays.
     * Note: No checks are performed.
     * 
     * @param anArray1 Array 1
     * @param anArray2 Array 2
     * @return True: Arrays have the same values in the same order, false: 
     * Otherwise
     */
    private boolean compareArrays(
        int[] anArray1, 
        int[] anArray2
    ) {
        boolean isEqual = true;
        if (anArray1.length != anArray2.length) {
            return false;
        }
        for (int i = 0; i < anArray1.length; i++) {
            if (anArray1[i] != anArray2[i]) {
                return false;
            }
        }
        return isEqual;
    }
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Private Gaussian cloud methods">
    /**
     * Returns Gaussian cloud matrix
     * 
     * @param aCentroidVector Centroid vector (IS NOT CHANGED)
     * @param aNumberOfGaussianCloudVectors Number of Gaussian cloud vectors
     * @param aStandardDeviation Standard deviation of Gaussian distribution
     * @param aRandomNumberGenerator Random number generator
     * @return Gaussian cloud matrix
     */
    private float[][] getGaussianCloudMatrix(
        float[] aCentroidVector,
        int aNumberOfGaussianCloudVectors,
        float aStandardDeviation,
        Random aRandomNumberGenerator
    ) {
        float[][] tmpGaussianCloudMatrix = new float[aNumberOfGaussianCloudVectors][];
        for (int i = 0; i < aNumberOfGaussianCloudVectors; i++) {
            float[] tmpCloudVector = new float[aCentroidVector.length];
            for (int j = 0; j < aCentroidVector.length; j++) {
                tmpCloudVector[j] = aCentroidVector[j] + (float) aRandomNumberGenerator.nextGaussian() * aStandardDeviation;
            }
            tmpGaussianCloudMatrix[i] = tmpCloudVector;
        }
        return tmpGaussianCloudMatrix;
    }

    /**
     * Returns combined Gaussian cloud matrix (see code)
     * 
     * @param aNumberOfDimensions Number of dimensions
     * @param aNumberOfGaussianCloudVectors Number of Gaussian cloud vectors
     * @param aStandardDeviation Standard deviation of Gaussian distribution
     * @param aRandomNumberGenerator Random number generator
     * @return Combined Gaussian cloud matrix (see code)
     */
    private float[][] getCombinedGaussianCloudMatrix(
        int aNumberOfDimensions,
        int aNumberOfGaussianCloudVectors,
        float aStandardDeviation,
        Random aRandomNumberGenerator
    ) {
        float[][] tmpCombinedGaussianCloudMatrix = new float[aNumberOfDimensions * aNumberOfGaussianCloudVectors][];
        int tmpIndex = 0;
        for (int i = 0; i < aNumberOfDimensions; i++) {
            float[] tmpCentroidVector = new float[aNumberOfDimensions];
            Arrays.fill(tmpCentroidVector, 0.0f);
            tmpCentroidVector[i] = 1.0f;
            float[][] tmpGaussianCloudMatrix = 
                this.getGaussianCloudMatrix(
                    tmpCentroidVector, 
                    aNumberOfGaussianCloudVectors, 
                    aStandardDeviation, 
                    aRandomNumberGenerator
            );
            for (int j = 0; j < tmpGaussianCloudMatrix.length; j++) {
                tmpCombinedGaussianCloudMatrix[tmpIndex++] = tmpGaussianCloudMatrix[j];
            }
        }
        return tmpCombinedGaussianCloudMatrix;
    }
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Private Iris data methods">
    /**
     * Returns Iris flower data: Indices 0-49 = Iris setosa, indices 50-99 = 
     * Iris versicolor, indices 100-149 = Iris virginica
     * 
     * Literature: R. A. Fisher, The Use of Multiple Measurements in Taxonomic 
     * Problems, Annals of Eugenics 7, 179-188, 1936.	
     * 
     * @return Iris flower data
     */
    private float[][] getIrisFlowerDataMatrix() {
        float[][] tmpIrisSetosaData = this.getIrisSetosaDataMatrix();
        float[][] tmpIrisVersicolorData = this.getIrisVersicolorDataMatrix();
        float[][] tmpIrisVirginicaData = this.getIrisVirginicaDataMatrix();
        float[][] tmpIrisFlowerData = 
            new float[tmpIrisSetosaData.length + tmpIrisVersicolorData.length + tmpIrisVirginicaData.length][];
        int tmpIndex = 0;
        for (int i = 0; i < tmpIrisSetosaData.length; i++) {
            tmpIrisFlowerData[tmpIndex++] = tmpIrisSetosaData[i];
        }
        for (int i = 0; i < tmpIrisVersicolorData.length; i++) {
            tmpIrisFlowerData[tmpIndex++] = tmpIrisVersicolorData[i];
        }
        for (int i = 0; i < tmpIrisVirginicaData.length; i++) {
            tmpIrisFlowerData[tmpIndex++] = tmpIrisVirginicaData[i];
        }
        return tmpIrisFlowerData;
    }
    
    /**
     * Returns Iris setosa data
     * 
     * Literature: R. A. Fisher, The Use of Multiple Measurements in Taxonomic 
     * Problems, Annals of Eugenics 7, 179-188, 1936.	
     * 
     * @return Iris setosa data
     */
    private float[][] getIrisSetosaDataMatrix() {
        return new 
            float[][] {
                {49.0f, 30.0f, 14.0f, 2.0f}, {51.0f, 38.0f, 19.0f, 4.0f}, {52.0f, 41.0f, 15.0f, 1.0f}, {54.0f, 34.0f, 15.0f, 4.0f}, 
                {50.0f, 36.0f, 14.0f, 2.0f}, {57.0f, 44.0f, 15.0f, 4.0f}, {46.0f, 32.0f, 14.0f, 2.0f}, {50.0f, 34.0f, 16.0f, 4.0f}, 
                {51.0f, 35.0f, 14.0f, 2.0f}, {49.0f, 31.0f, 15.0f, 2.0f}, {50.0f, 34.0f, 15.0f, 2.0f}, {58.0f, 40.0f, 12.0f, 2.0f}, 
                {43.0f, 30.0f, 11.0f, 1.0f}, {50.0f, 32.0f, 12.0f, 2.0f}, {50.0f, 30.0f, 16.0f, 2.0f}, {48.0f, 34.0f, 19.0f, 2.0f}, 
                {51.0f, 38.0f, 16.0f, 2.0f}, {48.0f, 30.0f, 14.0f, 3.0f}, {55.0f, 42.0f, 14.0f, 2.0f}, {44.0f, 30.0f, 13.0f, 2.0f}, 
                {54.0f, 39.0f, 17.0f, 4.0f}, {48.0f, 34.0f, 16.0f, 2.0f}, {51.0f, 35.0f, 14.0f, 3.0f}, {52.0f, 35.0f, 15.0f, 2.0f}, 
                {51.0f, 37.0f, 15.0f, 4.0f}, {54.0f, 34.0f, 17.0f, 2.0f}, {51.0f, 38.0f, 15.0f, 3.0f}, {57.0f, 38.0f, 17.0f, 3.0f}, 
                {45.0f, 23.0f, 13.0f, 3.0f}, {48.0f, 30.0f, 14.0f, 1.0f}, {53.0f, 37.0f, 15.0f, 2.0f}, {44.0f, 29.0f, 14.0f, 2.0f}, 
                {54.0f, 39.0f, 13.0f, 4.0f}, {54.0f, 37.0f, 15.0f, 2.0f}, {49.0f, 31.0f, 15.0f, 1.0f}, {50.0f, 35.0f, 13.0f, 3.0f}, 
                {51.0f, 34.0f, 15.0f, 2.0f}, {46.0f, 31.0f, 15.0f, 2.0f}, {47.0f, 32.0f, 13.0f, 2.0f}, {47.0f, 32.0f, 16.0f, 2.0f}, 
                {50.0f, 33.0f, 14.0f, 2.0f}, {50.0f, 35.0f, 16.0f, 6.0f}, {55.0f, 35.0f, 13.0f, 2.0f}, {46.0f, 34.0f, 14.0f, 3.0f}, 
                {51.0f, 33.0f, 17.0f, 5.0f}, {52.0f, 34.0f, 14.0f, 2.0f}, {49.0f, 36.0f, 14.0f, 1.0f}, {48.0f, 31.0f, 16.0f, 2.0f}, 
                {46.0f, 36.0f, 10.0f, 2.0f}, {44.0f, 32.0f, 13.0f, 2.0f}
        };
    }

    /**
     * Returns Iris versicolor data
     * 
     * Literature: R. A. Fisher, The Use of Multiple Measurements in Taxonomic 
     * Problems, Annals of Eugenics 7, 179-188, 1936.	
     * 
     * @return Iris versicolor data
     */
    private float[][] getIrisVersicolorDataMatrix() {
        return new 
            float[][] {
                {66.0f, 29.0f, 46.0f, 13.0f}, {61.0f, 29.0f, 47.0f, 14.0f}, {60.0f, 34.0f, 45.0f, 16.0f}, {52.0f, 27.0f, 39.0f, 14.0f}, 
                {49.0f, 24.0f, 33.0f, 10.0f}, {60.0f, 27.0f, 51.0f, 16.0f}, {56.0f, 27.0f, 42.0f, 13.0f}, {61.0f, 30.0f, 46.0f, 14.0f}, 
                {55.0f, 24.0f, 37.0f, 10.0f}, {57.0f, 30.0f, 42.0f, 12.0f}, {63.0f, 33.0f, 47.0f, 16.0f}, {69.0f, 31.0f, 49.0f, 15.0f}, 
                {57.0f, 28.0f, 45.0f, 13.0f}, {61.0f, 28.0f, 47.0f, 12.0f}, {64.0f, 29.0f, 43.0f, 13.0f}, {63.0f, 23.0f, 44.0f, 13.0f}, 
                {60.0f, 22.0f, 40.0f, 10.0f}, {56.0f, 30.0f, 41.0f, 13.0f}, {63.0f, 25.0f, 49.0f, 15.0f}, {50.0f, 20.0f, 35.0f, 10.0f}, 
                {59.0f, 30.0f, 42.0f, 15.0f}, {55.0f, 25.0f, 40.0f, 13.0f}, {62.0f, 29.0f, 43.0f, 13.0f}, {51.0f, 25.0f, 30.0f, 11.0f}, 
                {57.0f, 28.0f, 41.0f, 13.0f}, {58.0f, 27.0f, 39.0f, 12.0f}, {56.0f, 29.0f, 36.0f, 13.0f}, {67.0f, 31.0f, 47.0f, 15.0f}, 
                {67.0f, 31.0f, 44.0f, 14.0f}, {55.0f, 24.0f, 38.0f, 11.0f}, {56.0f, 30.0f, 45.0f, 15.0f}, {61.0f, 28.0f, 40.0f, 13.0f}, 
                {50.0f, 23.0f, 33.0f, 10.0f}, {55.0f, 26.0f, 44.0f, 12.0f}, {64.0f, 32.0f, 45.0f, 15.0f}, {55.0f, 23.0f, 40.0f, 13.0f}, 
                {66.0f, 30.0f, 44.0f, 14.0f}, {68.0f, 28.0f, 48.0f, 14.0f}, {58.0f, 27.0f, 41.0f, 10.0f}, {54.0f, 30.0f, 45.0f, 15.0f}, 
                {56.0f, 25.0f, 39.0f, 11.0f}, {62.0f, 22.0f, 45.0f, 15.0f}, {65.0f, 28.0f, 46.0f, 15.0f}, {58.0f, 26.0f, 40.0f, 12.0f}, 
                {57.0f, 29.0f, 42.0f, 13.0f}, {59.0f, 32.0f, 48.0f, 18.0f}, {70.0f, 32.0f, 47.0f, 14.0f}, {60.0f, 29.0f, 45.0f, 15.0f}, 
                {57.0f, 26.0f, 35.0f, 10.0f}, {67.0f, 30.0f, 50.0f, 17.0f}            
        };
    }

    /**
     * Returns Iris virginica data
     * 
     * Literature: R. A. Fisher, The Use of Multiple Measurements in Taxonomic 
     * Problems, Annals of Eugenics 7, 179-188, 1936.	
     * 
     * @return Iris versicolor data
     */
    private float[][] getIrisVirginicaDataMatrix() {
        return new 
            float[][] {
                {63.0f, 33.0f, 60.0f, 25.0f}, {65.0f, 30.0f, 52.0f, 20.0f}, {58.0f, 28.0f, 51.0f, 24.0f}, {68.0f, 30.0f, 55.0f, 21.0f}, 
                {67.0f, 31.0f, 56.0f, 24.0f}, {63.0f, 28.0f, 51.0f, 15.0f}, {69.0f, 31.0f, 51.0f, 23.0f}, {64.0f, 27.0f, 53.0f, 19.0f}, 
                {69.0f, 31.0f, 54.0f, 21.0f}, {72.0f, 36.0f, 61.0f, 25.0f}, {57.0f, 25.0f, 50.0f, 20.0f}, {65.0f, 32.0f, 51.0f, 20.0f}, 
                {65.0f, 30.0f, 58.0f, 22.0f}, {62.0f, 34.0f, 54.0f, 23.0f}, {64.0f, 28.0f, 56.0f, 21.0f}, {61.0f, 26.0f, 56.0f, 14.0f}, 
                {64.0f, 28.0f, 56.0f, 22.0f}, {77.0f, 30.0f, 61.0f, 23.0f}, {67.0f, 30.0f, 52.0f, 23.0f}, {62.0f, 28.0f, 48.0f, 18.0f}, 
                {59.0f, 30.0f, 51.0f, 18.0f}, {63.0f, 25.0f, 50.0f, 19.0f}, {72.0f, 30.0f, 58.0f, 16.0f}, {76.0f, 30.0f, 66.0f, 21.0f}, 
                {64.0f, 32.0f, 53.0f, 23.0f}, {61.0f, 30.0f, 49.0f, 18.0f}, {79.0f, 38.0f, 64.0f, 20.0f}, {72.0f, 32.0f, 60.0f, 18.0f}, 
                {63.0f, 27.0f, 49.0f, 18.0f}, {77.0f, 28.0f, 67.0f, 20.0f}, {58.0f, 27.0f, 51.0f, 19.0f}, {67.0f, 25.0f, 58.0f, 18.0f}, 
                {49.0f, 25.0f, 45.0f, 17.0f}, {67.0f, 33.0f, 57.0f, 21.0f}, {77.0f, 38.0f, 67.0f, 22.0f}, {56.0f, 28.0f, 49.0f, 20.0f}, 
                {65.0f, 30.0f, 55.0f, 18.0f}, {58.0f, 27.0f, 51.0f, 19.0f}, {74.0f, 28.0f, 61.0f, 19.0f}, {69.0f, 32.0f, 57.0f, 23.0f}, 
                {68.0f, 32.0f, 59.0f, 23.0f}, {73.0f, 29.0f, 63.0f, 18.0f}, {71.0f, 30.0f, 59.0f, 21.0f}, {60.0f, 22.0f, 50.0f, 15.0f}, 
                {77.0f, 26.0f, 69.0f, 23.0f}, {67.0f, 33.0f, 57.0f, 25.0f}, {63.0f, 29.0f, 56.0f, 18.0f}, {60.0f, 30.0f, 48.0f, 18.0f}, 
                {64.0f, 31.0f, 55.0f, 18.0f}, {63.0f, 34.0f, 56.0f, 24.0f}
        };
    }
    //</editor-fold>
    
}
