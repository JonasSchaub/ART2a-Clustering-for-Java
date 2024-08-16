/*
 * ART2a Clustering for Java
 * Copyright (C) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
 *
 * Source code is available at <https://github.com/JonasSchaub/ART2a-Clustering-for-Java>
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

package de.unijena.cheminf.clustering.art2a.clustering.euclideanClustering;


import de.unijena.cheminf.clustering.art2a.Art2aEuclideanClusteringTask;
import de.unijena.cheminf.clustering.art2a.exceptions.ConvergenceFailedException;
import de.unijena.cheminf.clustering.art2a.interfaces.euclideanClusteringInterfaces.IArt2aEuclideanClusteringResult;
import de.unijena.cheminf.clustering.art2a.util.FileUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Test class for double clustering.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
class Art2aEuclideanDoubleClusteringTest {
    //
    /**
     * Clustering result instance
     */
    private static IArt2aEuclideanClusteringResult clusteringResult;
    /**
     * Array for storing number of epochs for all vigilance parameters
     */
    private static int[] numberOfEpochsForAllVigilanceParameter;
    /**
     * Array for storing the number of detected clusters for all vigilance parameters
     */
    private static int[] numberOfDetectedClustersForAllVigilanceParameter;
    /**
     * Matrix for storing the indices in different clusters for certain vigilance parameters
     */
    private static int[][] clusterIndicesForAllVigilanceParameter;
    /**
     * Array for storing the cluster representatives in different clusters for certain vigilance parameters
     */
    private static int[] clusterRepresentativesForAllVigilanceParameter;
    /**
     * Array for storing the angle between different clusters for certain vigilance parameters
     */
    private static double[] clusterDistancesForAllVigilanceParameter;
    //
    /**
     * Starts double clustering and stores the results in arrays to check for correctness.
     * Clustering is performed for vigilance parameters from 0.1 to 0.9 in 0.1 steps.
     * The clustering process for the different vigilance parameters is performed in parallel.
     *
     */

    @Test
    void TestInstance() throws ConvergenceFailedException {
        double[][] tmpTestDataMatrix = FileUtil.importDoubleDataMatrixFromTextFile(
                "src/test/resources/de/unijena/cheminf/clustering/art2a/Bit_Fingerprints.txt", ',');
        Art2aEuclideanDoubleClustering tmpInstance = new Art2aEuclideanDoubleClustering(tmpTestDataMatrix, 20,
                5, 0.99,0.1);
        tmpInstance.getClusterResult(true, 2 );

    }
}
