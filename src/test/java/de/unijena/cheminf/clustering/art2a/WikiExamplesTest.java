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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Example usages of the ART-2a clustering library for the GitHub wiki.
 *
 * @author Jonas Schaub
 */
class WikiExamplesTest {
    /**
     * Really basic example with only ten data vectors.
     */
    @Test
    void basicClusteringExample() throws Exception {
        // Prepare your data as a 2D float array
        // Each row represents a data vector
        float[][] dataMatrix = {
                {0.1f, 0.2f, 0.3f},
                {0.2f, 0.1f, 0.4f},
                {0.9f, 0.8f, 0.7f},
                {0.8f, 0.9f, 0.6f},
                {0.15f, 0.25f, 0.35f},
                {0.85f, 0.75f, 0.65f},
                {0.05f, 0.15f, 0.25f},
                {0.95f, 0.85f, 0.75f},
                {0.12f, 0.22f, 0.32f},
                {0.88f, 0.78f, 0.68f}
        };

        // Configure clustering parameters
        //Vigilance parameter in interval [0,1]
        float vigilance = 0.5f;
        //Maximum number of clusters in interval [2, number of data row vectors of getDataMatrix]
        int maximumNumberOfClusters = 10;
        //default value
        int maximumNumberOfEpochs = 10;
        //default value
        float convergenceThreshold = 0.99f;
        //default value
        float learningParameter = 0.01f;
        //default value
        float offsetForContrastEnhancement = 1.0f;
        //default value
        long randomSeed = 1L;
        //do not preprocess data because it is already in range [0,1]
        boolean isDataPreprocessing = false;

        // Validate data matrix (same length in all rows, no empty rows, etc.)
        if (Utils.isDataMatrixValid(dataMatrix)) {
            // Create ART-2a kernel
            Art2aKernel art2aKernel = new Art2aKernel(
                    dataMatrix,
                    maximumNumberOfClusters,
                    maximumNumberOfEpochs,
                    convergenceThreshold,
                    learningParameter,
                    offsetForContrastEnhancement,
                    randomSeed,
                    isDataPreprocessing
            );

            // Perform clustering with set vigilance parameter and do the determination of which cluster is closest to the new data point sequentially
            Art2aResult result = art2aKernel.getClusterResult(vigilance, false);

            // Access results
            int numberOfClusters = result.getNumberOfDetectedClusters();
            System.out.println("Number of epochs: " + result.getNumberOfEpochs());
            System.out.println("Is converged: " + result.isConverged());

            // Get cluster information
            for (int i = 0; i < numberOfClusters; i++) {
                System.out.println("Cluster " + i + ":");
                System.out.println("\tSize: " + result.getClusterSize(i));
                System.out.println("\tMembers: " + Arrays.toString(result.getDataVectorIndicesOfCluster(i)));
                System.out.println("\tRepresentative Index: " + result.getClusterRepresentativeIndex(i));
            }
        }
    }

    /**
     * Input vectors need to be preprocessed first.
     */
    @Test
    void withPreprocessingExample() throws Exception {
        // Prepare your data as a 2D float array
        // Each row represents a data vector
        float[][] dataMatrix = {
                {10f, 20f, 30f},
                {20f, 10f, 40f},
                {90f, 80f, 70f},
                {80f, 90f, 60f},
                {15f, 25f, 35f},
                {85f, 75f, 65f},
                {5f, 15f, 25f},
                {95f, 85f, 75f},
                {12f, 22f, 32f},
                {88f, 78f, 68f}
        };

        // Configure clustering parameters
        //Vigilance parameter in interval [0,1]
        float vigilance = 0.5f;
        //Maximum number of clusters in interval [2, number of data row vectors of getDataMatrix]
        int maximumNumberOfClusters = 10;
        //default value
        int maximumNumberOfEpochs = 10;
        //default value
        float convergenceThreshold = 0.99f;
        //default value
        float learningParameter = 0.01f;
        //default value
        float offsetForContrastEnhancement = 1.0f;
        //default value
        long randomSeed = 1L;
        //preprocess data because it is not in range [0,1]
        boolean isDataPreprocessing = true;

        // Validate data matrix (same length in all rows, no empty rows, etc.)
        if (Utils.isDataMatrixValid(dataMatrix)) {
            // Create ART-2a kernel
            Art2aKernel art2aKernel = new Art2aKernel(
                    dataMatrix,
                    maximumNumberOfClusters,
                    maximumNumberOfEpochs,
                    convergenceThreshold,
                    learningParameter,
                    offsetForContrastEnhancement,
                    randomSeed,
                    isDataPreprocessing
            );

            // Perform clustering with set vigilance parameter and do the determination of which cluster is closest to the new data point sequentially
            Art2aResult result = art2aKernel.getClusterResult(vigilance, false);

            // Access results
            int numberOfClusters = result.getNumberOfDetectedClusters();
            System.out.println("Number of epochs: " + result.getNumberOfEpochs());
            System.out.println("Is converged: " + result.isConverged());

            // Get cluster information
            for (int i = 0; i < numberOfClusters; i++) {
                System.out.println("Cluster " + i + ":");
                System.out.println("\tSize: " + result.getClusterSize(i));
                System.out.println("\tMembers: " + Arrays.toString(result.getDataVectorIndicesOfCluster(i)));
                System.out.println("\tRepresentative Index: " + result.getClusterRepresentativeIndex(i));
            }
        }
    }

    /**
     * Basic example of clustering with multiple vigilance parameters and with internal rho winner determination
     * parallelization.
     */
    @Test
    void multipleVigilanceParametersExample() throws Exception {
        // Prepare your data as a 2D float array
        // Each row represents a data vector
        float[][] dataMatrix = {
                {0.1f, 0.2f, 0.3f},
                {0.2f, 0.1f, 0.4f},
                {0.9f, 0.8f, 0.7f},
                {0.8f, 0.9f, 0.6f},
                {0.15f, 0.25f, 0.35f},
                {0.85f, 0.75f, 0.65f},
                {0.05f, 0.15f, 0.25f},
                {0.95f, 0.85f, 0.75f},
                {0.12f, 0.22f, 0.32f},
                {0.88f, 0.78f, 0.68f}
        };

        // Configure clustering parameters
        //Multiple vigilance parameters in interval [0,1]
        float[] vigilances = {0.1f, 0.3f, 0.5f, 0.7f, 0.9f};
        //Maximum number of clusters in interval [2, number of data row vectors of getDataMatrix]
        int maximumNumberOfClusters = 10;
        //default value
        int maximumNumberOfEpochs = 10;
        //default value
        float convergenceThreshold = 0.99f;
        //default value
        float learningParameter = 0.01f;
        //default value
        float offsetForContrastEnhancement = 1.0f;
        //default value
        long randomSeed = 1L;
        //do not preprocess data because it is already in range [0,1]
        boolean isDataPreprocessing = false;

        // Validate data matrix (same length in all rows, no empty rows, etc.)
        if (Utils.isDataMatrixValid(dataMatrix)) {
            // Create ART-2a kernel
            Art2aKernel art2aKernel = new Art2aKernel(
                    dataMatrix,
                    maximumNumberOfClusters,
                    maximumNumberOfEpochs,
                    convergenceThreshold,
                    learningParameter,
                    offsetForContrastEnhancement,
                    randomSeed,
                    isDataPreprocessing
            );

            // Perform clustering with set vigilance parameters and do the determination of which cluster is closest to the new data point in parallel(!)
            Art2aResult[] results = art2aKernel.getClusterResults(vigilances, true);

            // Compare results
            for (int i = 0; i < vigilances.length; i++) {
                System.out.println("Vigilance " + vigilances[i] +
                        ": " + results[i].getNumberOfDetectedClusters() + " clusters");
            }
        }
    }

    /**
     * Basic example of clustering with multiple vigilance parameters on multiple threads in parallel.
     */
    @Test
    void multipleVigilanceParametersInParallelExample() throws Exception {
        // Prepare your data as a 2D float array
        // Each row represents a data vector
        float[][] dataMatrix = {
                {0.1f, 0.2f, 0.3f},
                {0.2f, 0.1f, 0.4f},
                {0.9f, 0.8f, 0.7f},
                {0.8f, 0.9f, 0.6f},
                {0.15f, 0.25f, 0.35f},
                {0.85f, 0.75f, 0.65f},
                {0.05f, 0.15f, 0.25f},
                {0.95f, 0.85f, 0.75f},
                {0.12f, 0.22f, 0.32f},
                {0.88f, 0.78f, 0.68f}
        };

        // Configure clustering parameters
        //Multiple vigilance parameters in interval [0,1]
        float[] vigilances = {0.1f, 0.3f, 0.5f, 0.7f, 0.9f};
        //Maximum number of clusters in interval [2, number of data row vectors of getDataMatrix]
        int maximumNumberOfClusters = 10;
        //default value
        int maximumNumberOfEpochs = 10;
        //default value
        float convergenceThreshold = 0.99f;
        //default value
        float learningParameter = 0.01f;
        //default value
        float offsetForContrastEnhancement = 1.0f;
        //default value
        long randomSeed = 1L;

        // Validate data matrix (same length in all rows, no empty rows, etc.)
        if (Utils.isDataMatrixValid(dataMatrix)) {
            //create list of Art2aTask instances to run in parallel below
            LinkedList<Art2aTask> art2aTaskList = new LinkedList<>();
            PreprocessedArt2aData preprocessedArt2aData = Art2aKernel.getPreprocessedArt2aData(dataMatrix, offsetForContrastEnhancement);
            for (float vigilance : vigilances) {
                art2aTaskList.add(
                        new Art2aTask(
                                preprocessedArt2aData,
                                vigilance,
                                maximumNumberOfClusters,
                                maximumNumberOfEpochs,
                                convergenceThreshold,
                                learningParameter,
                                randomSeed
                        )
                );
            }
            //run tasks in parallel
            ExecutorService executorService = Executors.newFixedThreadPool(vigilances.length);
            List<Future<Art2aResult>> futureList = null;
            try {
                futureList = executorService.invokeAll(art2aTaskList);
            } catch (InterruptedException e) {
                System.out.println("test_ParallelClustering: InterruptedException occurred.");
                System.exit(1);
            }
            executorService.shutdown();
            //collect results
            Art2aResult[] parallelResults = new Art2aResult[vigilances.length];
            int index = 0;
            for (Future<Art2aResult> future : futureList) {
                try {
                    parallelResults[index++] = future.get();
                } catch (Exception e) {
                    System.out.println("test_ParallelClustering: Exception occurred.");
                    System.exit(1);
                }
            }
            // Compare results
            for (int i = 0; i < vigilances.length; i++) {
                System.out.println("Vigilance " + vigilances[i] +
                        ": " + parallelResults[i].getNumberOfDetectedClusters() + " clusters");
            }
        }
    }

    /**
     * Basic example of extracting a set number of representative data vectors from a larger data set.
     */
    @Test
    void extractRepresentativeSubsetExample() throws Exception {
        // Prepare your data as a 2D float array
        // Each row represents a data vector
        float[][] dataMatrix = {
                {0.1f, 0.2f, 0.3f},
                {0.2f, 0.1f, 0.4f},
                {0.9f, 0.8f, 0.7f},
                {0.8f, 0.9f, 0.6f},
                {0.15f, 0.25f, 0.35f},
                {0.85f, 0.75f, 0.65f},
                {0.05f, 0.15f, 0.25f},
                {0.95f, 0.85f, 0.75f},
                {0.12f, 0.22f, 0.32f},
                {0.88f, 0.78f, 0.68f}
        };

        // Configure clustering parameters
        //Maximum number of clusters in interval [2, number of data row vectors of getDataMatrix]
        int maximumNumberOfClusters = 10;
        //default value
        int maximumNumberOfEpochs = 10;
        //default value
        float convergenceThreshold = 0.99f;
        //default value
        float learningParameter = 0.01f;
        //default value
        float offsetForContrastEnhancement = 1.0f;
        //default value
        long randomSeed = 1L;
        //do not preprocess data because it is already in range [0,1]
        boolean isDataPreprocessing = false;

        // Validate data matrix (same length in all rows, no empty rows, etc.)
        if (Utils.isDataMatrixValid(dataMatrix)) {
            // Create ART-2a kernel
            Art2aKernel art2aKernel = new Art2aKernel(
                    dataMatrix,
                    maximumNumberOfClusters,
                    maximumNumberOfEpochs,
                    convergenceThreshold,
                    learningParameter,
                    offsetForContrastEnhancement,
                    randomSeed,
                    isDataPreprocessing
            );

            int numberOfRepresentatives = 2;
            float vigilanceMin = 0.0001f;
            float vigilanceMax = 0.9999f;
            int numberOfTrialSteps = 32;

            //cluster repeatedly with varying vigilance parameters to get as closely as possible to
            // nr. of clusters = the desired number of representatives
            int[] representatives = art2aKernel.getRepresentatives(
                    numberOfRepresentatives,
                    vigilanceMin,
                    vigilanceMax,
                    numberOfTrialSteps,
                    false
            );
            System.out.println("Representative indices: " + Arrays.toString(representatives));
        }
    }

    /**
     * Basic example of splitting a data set into training and test sets using clustering.
     */
    @Test
    void trainTestSplitExample() throws Exception {
        // Prepare your data as a 2D float array
        // Each row represents a data vector
        float[][] dataMatrix = {
                {0.1f, 0.2f, 0.3f},
                {0.2f, 0.1f, 0.4f},
                {0.9f, 0.8f, 0.7f},
                {0.8f, 0.9f, 0.6f},
                {0.15f, 0.25f, 0.35f},
                {0.85f, 0.75f, 0.65f},
                {0.05f, 0.15f, 0.25f},
                {0.95f, 0.85f, 0.75f},
                {0.12f, 0.22f, 0.32f},
                {0.88f, 0.78f, 0.68f}
        };

        // Configure clustering parameters
        //Maximum number of clusters in interval [2, number of data row vectors of getDataMatrix]
        int maximumNumberOfClusters = 10;
        //default value
        int maximumNumberOfEpochs = 10;
        //default value
        float convergenceThreshold = 0.99f;
        //default value
        float learningParameter = 0.01f;
        //default value
        float offsetForContrastEnhancement = 1.0f;
        //default value
        long randomSeed = 1L;
        //do not preprocess data because it is already in range [0,1]
        boolean isDataPreprocessing = false;

        // Validate data matrix (same length in all rows, no empty rows, etc.)
        if (Utils.isDataMatrixValid(dataMatrix)) {
            // Create ART-2a kernel
            Art2aKernel art2aKernel = new Art2aKernel(
                    dataMatrix,
                    maximumNumberOfClusters,
                    maximumNumberOfEpochs,
                    convergenceThreshold,
                    learningParameter,
                    offsetForContrastEnhancement,
                    randomSeed,
                    isDataPreprocessing
            );

            float trainingFraction = 0.2f;  // 20% for training, 80% for testing

            // Get training and test indices
            int[][] trainingAndTestIndices = art2aKernel.getTrainingAndTestIndices(
                    trainingFraction,
                    0.0001f,  // vigilance min
                    0.9999f,  // vigilance max
                    32,  // number of trial steps
                    true  // parallel processing
            );

            int[] trainingIndices = trainingAndTestIndices[0];
            int[] testIndices = trainingAndTestIndices[1];
            System.out.println("Training indices: " + Arrays.toString(trainingIndices));
            System.out.println("Test indices: " + Arrays.toString(testIndices));
        }
    }

    /**
     * Basic example of analysing the angels (distances) between clusters.
     */
    @Test
    void clusterAngelsExample() throws Exception {
        // Prepare your data as a 2D float array
        // Each row represents a data vector
        float[][] dataMatrix = {
                {0.1f, 0.2f, 0.3f},
                {0.2f, 0.1f, 0.4f},
                {0.9f, 0.8f, 0.7f},
                {0.8f, 0.9f, 0.6f},
                {0.15f, 0.25f, 0.35f},
                {0.85f, 0.75f, 0.65f},
                {0.05f, 0.15f, 0.25f},
                {0.95f, 0.85f, 0.75f},
                {0.12f, 0.22f, 0.32f},
                {0.88f, 0.78f, 0.68f}
        };

        // Configure clustering parameters
        //Maximum number of clusters in interval [2, number of data row vectors of getDataMatrix]
        int maximumNumberOfClusters = 10;
        //default value
        int maximumNumberOfEpochs = 10;
        //default value
        float convergenceThreshold = 0.99f;
        //default value
        float learningParameter = 0.01f;
        //default value
        float offsetForContrastEnhancement = 1.0f;
        //default value
        long randomSeed = 1L;
        //do not preprocess data because it is already in range [0,1]
        boolean isDataPreprocessing = false;

        // Validate data matrix (same length in all rows, no empty rows, etc.)
        if (Utils.isDataMatrixValid(dataMatrix)) {
            // Create ART-2a kernel
            Art2aKernel art2aKernel = new Art2aKernel(
                    dataMatrix,
                    maximumNumberOfClusters,
                    maximumNumberOfEpochs,
                    convergenceThreshold,
                    learningParameter,
                    offsetForContrastEnhancement,
                    randomSeed,
                    isDataPreprocessing
            );

            Art2aResult result = art2aKernel.getClusterResult(0.9f, false);

            int numberOfClusters = result.getNumberOfDetectedClusters();

            // Calculate angles between all cluster pairs
            for (int i = 0; i < numberOfClusters; i++) {
                for (int j = i + 1; j < numberOfClusters; j++) {
                    double angle = result.getAngleBetweenClusters(i, j);
                    System.out.println("Angle between cluster " + i +
                            " and " + j + ": " + angle + " degrees");
                }
            }
        }
    }

    /**
     * Basic example with only ten data vectors for ART-2a-Euclid clustering.
     */
    @Test
    void basicClusteringExampleEuclid() throws Exception {
        // Prepare your data as a 2D float array
        // Each row represents a data vector
        float[][] dataMatrix = {
                {0.1f, 0.2f, 0.3f},
                {0.2f, 0.1f, 0.4f},
                {0.9f, 0.8f, 0.7f},
                {0.8f, 0.9f, 0.6f},
                {0.15f, 0.25f, 0.35f},
                {0.85f, 0.75f, 0.65f},
                {0.05f, 0.15f, 0.25f},
                {0.95f, 0.85f, 0.75f},
                {0.12f, 0.22f, 0.32f},
                {0.88f, 0.78f, 0.68f}
        };

        // Configure clustering parameters
        //Vigilance parameter in interval [0,1]
        float vigilance = 0.5f;
        //Maximum number of clusters in interval [2, number of data row vectors of dataMatrix]
        int maximumNumberOfClusters = 10;
        //default value
        int maximumNumberOfEpochs = 10;
        //default value
        float convergenceThreshold = 0.99f;
        //default value
        float learningParameter = 0.01f;
        //default value
        float offsetForContrastEnhancement = 1.0f;
        //default value
        long randomSeed = 1L;
        //do not preprocess data because it is already in range [0,1]
        boolean isDataPreprocessing = false;

        // Validate data matrix (same length in all rows, no empty rows, etc.)
        if (Utils.isDataMatrixValid(dataMatrix)) {
            // Create ART-2a-Euclid kernel
            Art2aEuclidKernel art2aEuclidKernel = new Art2aEuclidKernel(
                    dataMatrix,
                    maximumNumberOfClusters,
                    maximumNumberOfEpochs,
                    convergenceThreshold,
                    learningParameter,
                    offsetForContrastEnhancement,
                    randomSeed,
                    isDataPreprocessing
            );

            // Perform clustering with set vigilance parameter and do the determination of which cluster is closest to the new data point sequentially
            Art2aEuclidResult result = art2aEuclidKernel.getClusterResult(vigilance, false);

            // Access results
            int numberOfClusters = result.getNumberOfDetectedClusters();
            System.out.println("Number of epochs: " + result.getNumberOfEpochs());
            System.out.println("Is converged: " + result.isConverged());

            // Get cluster information
            for (int i = 0; i < numberOfClusters; i++) {
                System.out.println("Cluster " + i + ":");
                System.out.println("\tSize: " + result.getClusterSize(i));
                System.out.println("\tMembers: " + Arrays.toString(result.getDataVectorIndicesOfCluster(i)));
                System.out.println("\tRepresentative Index: " + result.getClusterRepresentativeIndex(i));
            }
        }
    }
}
