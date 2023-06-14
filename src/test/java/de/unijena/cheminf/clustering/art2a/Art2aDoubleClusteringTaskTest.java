/*
 * GNU General Public License v3.0
 *
 * Copyright (c) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
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
import de.unijena.cheminf.clustering.art2a.clustering.Art2aDoubleClustering;
import de.unijena.cheminf.clustering.art2a.interfaces.IArt2aClusteringResult;
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
public class Art2aDoubleClusteringTaskTest {
    //<editor-fold desc="Private static class variables" defaultstate="collapsed">
    /**
     * Clustering result instance
     */
    private static IArt2aClusteringResult clusteringResult;
    /**
     * Array for storing number of epochs for all vigilance parameters
     */
    private static int[] numberOfEpochsForAllVigilanceParameter;
    /**
     * Array for storing number of detected clusters for all vigilance parameters
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
    private static double[] clusterAnglesForAllVigilanceParameter;
    //</editor-fold>
    //
    //<editor-fold desc="Before all" defaultstate="collapsed">
    /**
     * Starts float clustering and stores the results in arrays to check for correctness.
     * Clustering is performed for vigilance parameters from 0.1 to 0.9 in 0.1 steps.
     * The clustering process for the different vigilance parameters is performed in parallel.
     *
     */
    @BeforeAll
    public static void startArt2aClusteringTest() throws Exception {
        double[][] tmpTestDataMatrix = FileUtil.importDoubleDataMatrixFromTextFile("src/test/resources/de/unijena/cheminf/clustering/art2a/Bit_Fingerprints.txt", ',');
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(9); // number of tasks
        List<Art2aClusteringTask> tmpClusteringTask = new LinkedList<>();
        for (double tmpVigilanceParameter = 0.1; tmpVigilanceParameter < 0.9; tmpVigilanceParameter += 0.1) { //to 0.9 in order to leave the number of vigilance parameters at 9.
            Art2aClusteringTask tmpART2aDoubleClusteringTask = new Art2aClusteringTask(tmpVigilanceParameter, tmpTestDataMatrix, 100, true);
            tmpClusteringTask.add(tmpART2aDoubleClusteringTask);
        }
        BufferedWriter[] tmpWriter = FileUtil.setUpClusteringResultTextFilePrinters("Clustering_Result_Folder", BufferedWriter.class);
        List<Future<IArt2aClusteringResult>> tmpFuturesList;
        Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter = new int[9];
        Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter = new int[9];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter = new int[9][];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[0] = new int[4];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[1] = new int[1];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[2] = new int[1];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[3] = new int[1];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[4] = new int[1];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[5] = new int[1];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[6] = new int[1];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[7] = new int[1];
        Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[8] = new int[1];
        Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter = new int[9];
        Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter = new double[9];
        tmpFuturesList = tmpExecutorService.invokeAll(tmpClusteringTask);
        int tmpIterator = 0;
        for (Future<IArt2aClusteringResult> tmpFuture : tmpFuturesList) {
            try {
                Art2aDoubleClusteringTaskTest.clusteringResult = tmpFuture.get();
                Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[tmpIterator] = Art2aDoubleClusteringTaskTest.clusteringResult.getNumberOfEpochs();
                Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[tmpIterator] = Art2aDoubleClusteringTaskTest.clusteringResult.getNumberOfDetectedClusters();
                Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[tmpIterator] = Art2aDoubleClusteringTaskTest.clusteringResult.getClusterIndices(tmpIterator);
                Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[tmpIterator] = (double) Art2aDoubleClusteringTaskTest.clusteringResult.calculateAngleBetweenClusters(tmpIterator, tmpIterator + 1);
                Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[tmpIterator] = Art2aDoubleClusteringTaskTest.clusteringResult.getClusterRepresentatives(tmpIterator);
                Art2aDoubleClusteringTaskTest.clusteringResult.exportClusteringResultsToTextFiles(tmpWriter[0], tmpWriter[1]);
                tmpIterator++;
            } catch (RuntimeException anException) {
                throw anException;
            }
        }
        tmpWriter[0].flush();
        tmpWriter[0].close();
        tmpWriter[1].flush();
        tmpWriter[1].close();
        tmpExecutorService.shutdown();
    }
    // <editor-fold defaultstate="collapsed" desc="tests the number of epochs for all vigilance parameter">
    /**
     * Tests number of epoch for vigilance parameter 0.1
     */
    @Test
    public void testNumberOfEpochsFor01f() {
        int tmpTestNumberOfEpochsFor01f = 2;
        int tmpNumberOfEpochs01f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[0];
        Assertions.assertEquals(tmpTestNumberOfEpochsFor01f, tmpNumberOfEpochs01f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.2
     */
    @Test
    public void testNumberOfEpochsFor02f() {
        int tmpTestNumberOfEpochs02f = 2;
        int tmpNumberOfEpochs02f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[1];
        Assertions.assertEquals(tmpTestNumberOfEpochs02f, tmpNumberOfEpochs02f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.3
     */
    @Test
    public void testNumberOfEpochsFor03() {
        int tmpTestNumberOfEpochs03f = 2;
        int tmpNumberOfEpochs03f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[2];
        Assertions.assertEquals(tmpTestNumberOfEpochs03f, tmpNumberOfEpochs03f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.4
     */
    @Test
    public void testNumberOfEpochsFor04() {
        int tmpTestNumberOfEpochs04f = 2;
        int tmpNumberOfEpochs04f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[3];
        Assertions.assertEquals(tmpTestNumberOfEpochs04f, tmpNumberOfEpochs04f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.5
     */
    @Test
    public void testNumberOfEpochsFor05() {
        int tmpTestNumberOfEpochs05f = 2;
        int tmpNumberOfEpochs05f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[4];
        Assertions.assertEquals(tmpTestNumberOfEpochs05f, tmpNumberOfEpochs05f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.6
     */
    @Test
    public void testNumberOfEpochsFor06() {
        int tmpTestNumberOfEpochs06f = 2;
        int tmpNumberOfEpochs06f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[5];
        Assertions.assertEquals(tmpTestNumberOfEpochs06f, tmpNumberOfEpochs06f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.7
     */
    @Test
    public void testNumberOfEpochsFor07() {
        int tmpTestNumberOfEpochs07f = 2;
        int tmpNumberOfEpochs07f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[6];
        Assertions.assertEquals(tmpTestNumberOfEpochs07f, tmpNumberOfEpochs07f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.8
     */
    @Test
    public void testNumberOfEpochsFor08() {
        int tmpTestNumberOfEpochs08f = 2;
        int tmpNumberOfEpochs08f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[7];
        Assertions.assertEquals(tmpTestNumberOfEpochs08f, tmpNumberOfEpochs08f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.9
     */
    @Test
    public void testNumberOfEpochsFor09() {
        int tmpTestNumberOfEpochs09f = 2;
        int tmpNumberOfEpochs09f = Art2aDoubleClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[8];
        Assertions.assertEquals(tmpTestNumberOfEpochs09f, tmpNumberOfEpochs09f);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="tests the number of detected clusters for all vigilance parameter">
    /**
     * Tests number of detected clusters for vigilance parameter 0.1
     */
    @Test
    public void testNumberOfDetectedClustersFor01() {
        int tmpTestNumberOfDetectedClusters01 = 6;
        int tmpNumberOfDetectedClusters01 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[0];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters01, tmpNumberOfDetectedClusters01);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.2
     */
    @Test
    public void testNumberOfDetectedClustersFor02() {
        int tmpTestNumberOfDetectedClusters02 = 6;
        int tmpNumberOfDetectedClusters02 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[1];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters02, tmpNumberOfDetectedClusters02);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.3
     */
    @Test
    public void testNumberOfDetectedClustersFor03() {
        int tmpTestNumberOfDetectedClusters03 = 6;
        int tmpNumberOfDetectedClusters03 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[2];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters03, tmpNumberOfDetectedClusters03);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.4
     */
    @Test
    public void testNumberOfDetectedClustersFor04() {
        int tmpTestNumberOfDetectedClusters04 = 8;
        int tmpNumberOfDetectedClusters04 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[3];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters04, tmpNumberOfDetectedClusters04);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.5
     */
    @Test
    public void testNumberOfDetectedClustersFor05() {
        int tmpTestNumberOfDetectedClusters05 = 9;
        int tmpNumberOfDetectedClusters05 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[4];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters05, tmpNumberOfDetectedClusters05);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.6
     */
    @Test
    public void testNumberOfDetectedClustersFor06() {
        int tmpTestNumberOfDetectedClusters06 = 10;
        int tmpNumberOfDetectedClusters06 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[5];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters06, tmpNumberOfDetectedClusters06);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.7
     */
    @Test
    public void testNumberOfDetectedClustersFor07() {
        int tmpTestNumberOfDetectedClusters07 = 10;
        int tmpNumberOfDetectedClusters07 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[6];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters07, tmpNumberOfDetectedClusters07);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.8
     */
    @Test
    public void testNumberOfDetectedClustersFor08() {
        int tmpTestNumberOfDetectedClusters08 = 10;
        int tmpNumberOfDetectedClusters08 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[7];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters08, tmpNumberOfDetectedClusters08);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.9
     */
    @Test
    public void testNumberOfDetectedClustersFor09() {
        int tmpTestNumberOfDetectedClusters09 = 10;
        int tmpNumberOfDetectedClusters09 = Art2aDoubleClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[8];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters09, tmpNumberOfDetectedClusters09);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="tests the cluster indices of detected clusters for all vigilance parameter">
    /**
     * Tests the cluster indices in cluster 0 for vigilance parameter 0.1
     */
    @Test
    public void testClusterIndicesInCluster0ForVigilanceParameter01() {
        int[] tmpTestClusterIndicesInCluster0For01 = {4,6,7,9};
        int[] tmpClusterIndicesInClusterFor0For01 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[0];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster0For01, tmpClusterIndicesInClusterFor0For01);
    }
    //
    /**
     *  Tests the cluster indices in cluster 1 for vigilance parameter 0.2
     */
    @Test
    public void testClusterIndicesInCLuster1ForVigilanceParameter02() {
        int[] tmpTestClusterIndicesInCluster1For02 = {1,2};
        int[] tmpClusterIndicesInClusterFor1For02 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[1];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster1For02, tmpClusterIndicesInClusterFor1For02);
    }
    //
    /**
     *  Tests the cluster indices in cluster 2 for vigilance parameter 0.3
     */
    @Test
    public void testClusterIndicesInCLuster2ForVigilanceParameter03() {
        int[] tmpTestClusterIndicesInCluster2For03 = {3};
        int[] tmpClusterIndicesInClusterFor2For03 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[2];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster2For03, tmpClusterIndicesInClusterFor2For03);
    }
    //
    /**
     *  Tests the cluster indices in cluster 3 for vigilance parameter 0.4
     */
    @Test
    public void testClusterIndicesInCLuster3ForVigilanceParameter04() {
        int[] tmpTestClusterIndicesInCluster3For04 = {2};
        int[] tmpClusterIndicesInClusterFor3For04 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[3];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster3For04, tmpClusterIndicesInClusterFor3For04);
    }
    //
    /**
     *  Tests the cluster indices in cluster 4 for vigilance parameter 0.5
     */
    @Test
    public void testClusterIndicesInCLuster4ForVigilanceParameter05() {
        int[] tmpTestClusterIndicesInCluster4For05 = {5};
        int[] tmpClusterIndicesInCluster4For05 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[4];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster4For05, tmpClusterIndicesInCluster4For05);
    }
    //
    /**
     *  Tests the cluster indices in cluster 5 for vigilance parameter 0.6
     */
    @Test
    public void testClusterIndicesInCLuster5ForVigilanceParameter06() {
        int[] tmpTestClusterIndicesInCluster5For06 = {5};
        int[] tmpClusterIndicesInCluster5For06 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[5];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster5For06, tmpClusterIndicesInCluster5For06);
    }
    //
    /**
     *  Tests the cluster indices in cluster 6 for vigilance parameter 0.7
     */
    @Test
    public void testClusterIndicesInCLuster6ForVigilanceParameter07() {
        int[] tmpTestClusterIndicesInCluster6For07 = {6};
        int[] tmpClusterIndicesInClusterFor6For07 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[6];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster6For07, tmpClusterIndicesInClusterFor6For07);
    }
    //
    /**
     *  Tests the cluster indices in cluster 7 for vigilance parameter 0.8
     */
    @Test
    public void testClusterIndicesInCLuster7ForVigilanceParameter08() {
        int[] tmpTestClusterIndicesInCluster7For08 = {4};
        int[] tmpClusterIndicesInCluster7For08 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[7];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster7For08, tmpClusterIndicesInCluster7For08);
    }
    //
    /**
     *  Tests the cluster indices in cluster 8 for vigilance parameter 0.9
     */
    @Test
    public void testClusterIndicesInCLuster8ForVigilanceParameter09() {
        int[] tmpTestClusterIndicesInCluster8For09 = {8};
        int[] tmpClusterIndicesInCluster8For09 = Art2aDoubleClusteringTaskTest.clusterIndicesForAllVigilanceParameter[8];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster8For09, tmpClusterIndicesInCluster8For09);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="tests cluster representatives in different clusters for all vigilance parameter">
    /**
     * Tests the cluster representatives in cluster 0 for vigilance parameter 0.1
     */
    @Test
    public void testClusterRepresentativesInCluster0ForVigilanceParameter01() {
        int tmpTestClusterRepresentativesIndexInCluster0For01 = 9;
        int tmpClusterRepresentativesIndexInCluster0For01 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[0];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster0For01, tmpClusterRepresentativesIndexInCluster0For01);
    }
    //
    /**
     * Tests the cluster representatives in cluster 1 for vigilance parameter 0.2
     */
    @Test
    public void testClusterRepresentativesInCluster1ForVigilanceParameter02() {
        int tmpTestClusterRepresentativesIndexInCluster1For02 = 1;
        int tmpClusterRepresentativesIndexInCluster1For02 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[1];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster1For02, tmpClusterRepresentativesIndexInCluster1For02);
    }
    //
    /**
     * Tests the cluster representatives in cluster 2 for vigilance parameter 0.3
     */
    @Test
    public void testClusterRepresentativesInCluster2ForVigilanceParameter03() {
        int tmpTestClusterRepresentativesIndexInCluster2For03 = 3;
        int tmpClusterRepresentativesIndexInCluster2For03 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[2];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster2For03, tmpClusterRepresentativesIndexInCluster2For03);
    }
    //
    /**
     * Tests the cluster representatives in cluster 3 for vigilance parameter 0.4
     */
    @Test
    public void testClusterRepresentativesInCluster3ForVigilanceParameter04() {
        int tmpTestClusterRepresentativesIndexInCluster3For04 = 2;
        int tmpClusterRepresentativesIndexInCluster3For04 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[3];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster3For04, tmpClusterRepresentativesIndexInCluster3For04);
    }
    //
    /**
     * Tests the cluster representatives in cluster 4 for vigilance parameter 0.5
     */
    @Test
    public void testClusterRepresentativesInCluster4ForVigilanceParameter05() {
        int tmpTestClusterRepresentativesIndexInCluster4For05 = 5;
        int tmpClusterRepresentativesIndexInCluster4For05 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[4];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster4For05, tmpClusterRepresentativesIndexInCluster4For05);
    }
    //
    /**
     * Tests the cluster representatives in cluster 5 for vigilance parameter 0.6
     */
    @Test
    public void testClusterRepresentativesInCluster5ForVigilanceParameter06() {
        int tmpTestClusterRepresentativesIndexInCluster5For06 = 5;
        int tmpClusterRepresentativesIndexInCluster5For06 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[5];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster5For06, tmpClusterRepresentativesIndexInCluster5For06);
    }
    //
    /**
     * Tests the cluster representatives in cluster 6 for vigilance parameter 0.7
     */
    @Test
    public void testClusterRepresentativesInCluster6ForVigilanceParameter07() {
        int tmpTestClusterRepresentativesIndexInCluster6For07 = 6;
        int tmpClusterRepresentativesIndexInCluster6For07 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[6];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster6For07, tmpClusterRepresentativesIndexInCluster6For07);
    }
    //
    /**
     * Tests the cluster representatives in cluster 7 for vigilance parameter 0.8
     */
    @Test
    public void testClusterRepresentativesInCluster7ForVigilanceParameter08() {
        int tmpTestClusterRepresentativesIndexInCluster7For08 = 4;
        int tmpClusterRepresentativesIndexInCluster7For08 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[7];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster7For08, tmpClusterRepresentativesIndexInCluster7For08);
    }
    //
    /**
     * Tests the cluster representatives in cluster 8 for vigilance parameter 0.9
     */
    @Test
    public void testClusterRepresentativesInCluster8ForVigilanceParameter09() {
        int tmpTestClusterRepresentativesIndexInCluster8For09 = 8;
        int tmpClusterRepresentativesIndexInCluster8For09 = Art2aDoubleClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[8];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster8For09, tmpClusterRepresentativesIndexInCluster8For09);
    }
    //</editor-fold>
    //
    //
    // <editor-fold defaultstate="collapsed" desc="tests angle between cluster for all vigilance parameter">
    /**
     * Tests the angle between cluster 0 and 1 for vigilance parameter 0.1
     */
    @Test
    public void testAngleBetweenCluster0And1For01() {
        double tmpTestAngleBetweenCluster0And1For01 = 64.71956849036344;
        double tmpAngleBetweenCluster0And1For01 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[0];
        Assertions.assertEquals(tmpTestAngleBetweenCluster0And1For01, tmpAngleBetweenCluster0And1For01, 1e-8f);
    }
    //
    /**
     *  Tests the angle between cluster 1 and 2 for vigilance parameter 0.2
     */
    @Test
    public void testAngleBetweenCluster1And2For02() {
        double tmpTestAngleBetweenCluster1And2For02 = 80.98592593273575;
        double tmpAngleBetweenCluster1And2For02 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[1];
        Assertions.assertEquals(tmpTestAngleBetweenCluster1And2For02, tmpAngleBetweenCluster1And2For02, 1e-8);
    }
    //
    /**
     *  Tests the angle between cluster 2 and 3 for vigilance parameter 0.3
     */
    @Test
    public void testAngleBetweenCluster2And3For03() {
        double tmpTestAngleBetweenCluster2And3For03 = 90.0000;
        double tmpAngleBetweenCluster2And3For03 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[2];
        Assertions.assertEquals(tmpTestAngleBetweenCluster2And3For03, tmpAngleBetweenCluster2And3For03, 1e-8);
    }
    //
    /**
     *  Tests the angle between cluster 3 and 4 for vigilance parameter 0.4
     */
    @Test
    public void testAngleBetweenCluster3And4For04() {
        double tmpTestAngleBetweenCluster3And4For04 = 90.0000;
        double tmpAngleBetweenCluster3And4For04 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[3];
        Assertions.assertEquals(tmpTestAngleBetweenCluster3And4For04, tmpAngleBetweenCluster3And4For04, 1e-8);
    }
    //
    /**
     *  Tests the angle between cluster 4 and 5 for vigilance parameter 0.5
     */
    @Test
    public void testAngleBetweenCluster4And5For05() {
        double tmpTestAngleBetweenCluster4And5For05 = 71.56505117707799;
        double tmpAngleBetweenCluster4And5For05 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[4];
        Assertions.assertEquals(tmpTestAngleBetweenCluster4And5For05, tmpAngleBetweenCluster4And5For05, 1e-8);
    }
    //
    /**
     *  Tests the angle between cluster 5 and 6 for vigilance parameter 0.6
     */
    @Test
    public void testAngleBetweenCluster5And6For06() {
        double tmpTestAngleBetweenCluster5And6For06 = 71.56505117707799;
        double tmpAngleBetweenCluster5And6For06 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[5];
        Assertions.assertEquals(tmpTestAngleBetweenCluster5And6For06, tmpAngleBetweenCluster5And6For06, 1e-8);
    }
    //
    /**
     *  Tests the angle between cluster 6 and 7 for vigilance parameter 0.7
     */
    @Test
    public void testAngleBetweenCluster6And7For07() {
        double tmpTestAngleBetweenCluster6And7For07 = 75.03678256669288;
        double tmpAngleBetweenCluster6And7For07 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[6];
        Assertions.assertEquals(tmpTestAngleBetweenCluster6And7For07, tmpAngleBetweenCluster6And7For07, 1e-8);
    }
    //
    /**
     *  Tests the angle between cluster 7 and 8 for vigilance parameter 0.8
     */
    @Test
    public void testAngleBetweenCluster7And8For08() {
        double tmpTestAngleBetweenCluster7And8For08 = 90.000;
        double tmpAngleBetweenCluster7And8For08 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[7];
        Assertions.assertEquals(tmpTestAngleBetweenCluster7And8For08, tmpAngleBetweenCluster7And8For08, 1e-8);
    }
    //
    /**
     *  Tests the angle between cluster 8 and 9 for vigilance parameter 0.9
     */
    @Test
    public void testAngleBetweenCluster8And9For09() {
        double tmpTestAngleBetweenCluster8And9For09 = 90.000;
        double tmpAngleBetweenCluster8And9For09 = Art2aDoubleClusteringTaskTest.clusterAnglesForAllVigilanceParameter[8];
        Assertions.assertEquals(tmpTestAngleBetweenCluster8And9For09, tmpAngleBetweenCluster8And9For09, 1e-8);
    }
    /**
     * Method tests whether the import of a data matrix from a text file works correctly.
     *
     * @throws NoSuchMethodException is thrown, if the private method is not found
     */
    @Test
    public void testImportFloatDataMatrix() throws  NoSuchMethodException {
        double[][] tmpImportDoubleDataMatrix = FileUtil.importDoubleDataMatrixFromTextFile("src/test/resources/de/unijena/cheminf/clustering/art2a/Count_Fingerprints.txt", ',');
        Method tmpImportMethod = FileUtil.class.getDeclaredMethod("importDoubleDataMatrixFromTextFile", String.class, char.class);
        tmpImportMethod.setAccessible(true);
        double[][] tmpTestDataMatrix = new double[6][10];

        tmpTestDataMatrix[0][0] = 1.0;
        tmpTestDataMatrix[0][1] = 0.0;
        tmpTestDataMatrix[0][2] = 0.0;
        tmpTestDataMatrix[0][3] = 0.0;
        tmpTestDataMatrix[0][4] = 0.0;
        tmpTestDataMatrix[0][5] = 0.0;
        tmpTestDataMatrix[0][6] = 0.0;
        tmpTestDataMatrix[0][7] = 0.0;
        tmpTestDataMatrix[0][8] = 1.0;
        tmpTestDataMatrix[0][9] = 0.0;

        tmpTestDataMatrix[1][0] = 0.0;
        tmpTestDataMatrix[1][1] = 0.0;
        tmpTestDataMatrix[1][2] = 0.0;
        tmpTestDataMatrix[1][3] = 0.0;
        tmpTestDataMatrix[1][4] = 0.0;
        tmpTestDataMatrix[1][5] = 3.0;
        tmpTestDataMatrix[1][6] = 1.0;
        tmpTestDataMatrix[1][7] = 1.0;
        tmpTestDataMatrix[1][8] = 0.0;
        tmpTestDataMatrix[1][9] = 0.0;

        tmpTestDataMatrix[2][0] = 0.0;
        tmpTestDataMatrix[2][1] = 0.0;
        tmpTestDataMatrix[2][2] = 0.0;
        tmpTestDataMatrix[2][3] = 0.0;
        tmpTestDataMatrix[2][4] = 0.0;
        tmpTestDataMatrix[2][5] = 0.0;
        tmpTestDataMatrix[2][6] = 0.0;
        tmpTestDataMatrix[2][7] = 0.0;
        tmpTestDataMatrix[2][8] = 0.0;
        tmpTestDataMatrix[2][9] = 0.0;

        tmpTestDataMatrix[3][0] = 0.0;
        tmpTestDataMatrix[3][1] = 0.0;
        tmpTestDataMatrix[3][2] = 0.0;
        tmpTestDataMatrix[3][3] = 0.0;
        tmpTestDataMatrix[3][4] = 0.0;
        tmpTestDataMatrix[3][5] = 0.0;
        tmpTestDataMatrix[3][6] = 0.0;
        tmpTestDataMatrix[3][7] = 0.0;
        tmpTestDataMatrix[3][8] = 0.0;
        tmpTestDataMatrix[3][9] = 0.0;

        tmpTestDataMatrix[4][0] = 0.0;
        tmpTestDataMatrix[4][1] = 0.0;
        tmpTestDataMatrix[4][2] = 0.0;
        tmpTestDataMatrix[4][3] = 0.0;
        tmpTestDataMatrix[4][4] = 1.0;
        tmpTestDataMatrix[4][5] = 0.0;
        tmpTestDataMatrix[4][6] = 0.0;
        tmpTestDataMatrix[4][7] = 0.0;
        tmpTestDataMatrix[4][8] = 0.0;
        tmpTestDataMatrix[4][9] = 0.0;

        tmpTestDataMatrix[5][0] = 0.0;
        tmpTestDataMatrix[5][1] = 1.0;
        tmpTestDataMatrix[5][2] = 0.0;
        tmpTestDataMatrix[5][3] = 0.0;
        tmpTestDataMatrix[5][4] = 0.0;
        tmpTestDataMatrix[5][5] = 0.0;
        tmpTestDataMatrix[5][6] = 0.0;
        tmpTestDataMatrix[5][7] = 0.0;
        tmpTestDataMatrix[5][8] = 0.0;
        tmpTestDataMatrix[5][9] = 8.0;
        Assertions.assertArrayEquals(tmpTestDataMatrix,tmpImportDoubleDataMatrix);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="test private some methods">
    /**
     * Method tests whether the checks and possible scaling in the data matrix work successfully.
     *
     * @throws NoSuchMethodException is thrown, if the private method is not found.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Test
    public void testCheckAndScaleDataMatrix() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        double[][] tmpImportDoubleDataMatrix = FileUtil.importDoubleDataMatrixFromTextFile("src/test/resources/de/unijena/cheminf/clustering/art2a/Count_Fingerprints.txt", ',');
        Art2aDoubleClustering tmpDoubleClustering = new Art2aDoubleClustering(tmpImportDoubleDataMatrix, 10, 0.1, 0.99, 0.1);
        Method tmpCheckAndScaleDataMatrix = Art2aDoubleClustering.class.getDeclaredMethod("checkAndScaleDataMatrix", double[][].class);
        tmpCheckAndScaleDataMatrix.setAccessible(true);
        tmpCheckAndScaleDataMatrix.invoke(tmpDoubleClustering, (Object) tmpImportDoubleDataMatrix);
        double[][] tmpTestDataMatrix = new double[6][10];

        tmpTestDataMatrix[0][0] = 0.125;
        tmpTestDataMatrix[0][1] = 0.0;
        tmpTestDataMatrix[0][2] = 0.0;
        tmpTestDataMatrix[0][3] = 0.0;
        tmpTestDataMatrix[0][4] = 0.0;
        tmpTestDataMatrix[0][5] = 0.0;
        tmpTestDataMatrix[0][6] = 0.0;
        tmpTestDataMatrix[0][7] = 0.0;
        tmpTestDataMatrix[0][8] = 0.125;
        tmpTestDataMatrix[0][9] = 0.0;

        tmpTestDataMatrix[1][0] = 0.0;
        tmpTestDataMatrix[1][1] = 0.0;
        tmpTestDataMatrix[1][2] = 0.0;
        tmpTestDataMatrix[1][3] = 0.0;
        tmpTestDataMatrix[1][4] = 0.0f;
        tmpTestDataMatrix[1][5] = 0.375;
        tmpTestDataMatrix[1][6] = 0.125;
        tmpTestDataMatrix[1][7] = 0.125;
        tmpTestDataMatrix[1][8] = 0.0;
        tmpTestDataMatrix[1][9] = 0.0;

        tmpTestDataMatrix[2][0] = 0.0;
        tmpTestDataMatrix[2][1] = 0.0;
        tmpTestDataMatrix[2][2] = 0.0;
        tmpTestDataMatrix[2][3] = 0.0;
        tmpTestDataMatrix[2][4] = 0.0;
        tmpTestDataMatrix[2][5] = 0.0;
        tmpTestDataMatrix[2][6] = 0.0;
        tmpTestDataMatrix[2][7] = 0.0;
        tmpTestDataMatrix[2][8] = 0.0;
        tmpTestDataMatrix[2][9] = 0.0;

        tmpTestDataMatrix[3][0] = 0.0;
        tmpTestDataMatrix[3][1] = 0.0;
        tmpTestDataMatrix[3][2] = 0.0;
        tmpTestDataMatrix[3][3] = 0.0;
        tmpTestDataMatrix[3][4] = 0.0;
        tmpTestDataMatrix[3][5] = 0.0;
        tmpTestDataMatrix[3][6] = 0.0;
        tmpTestDataMatrix[3][7] = 0.0;
        tmpTestDataMatrix[3][8] = 0.0;
        tmpTestDataMatrix[3][9] = 0.0;

        tmpTestDataMatrix[4][0] = 0.0;
        tmpTestDataMatrix[4][1] = 0.0;
        tmpTestDataMatrix[4][2] = 0.0;
        tmpTestDataMatrix[4][3] = 0.0;
        tmpTestDataMatrix[4][4] = 0.125;
        tmpTestDataMatrix[4][5] = 0.0;
        tmpTestDataMatrix[4][6] = 0.0;
        tmpTestDataMatrix[4][7] = 0.0;
        tmpTestDataMatrix[4][8] = 0.0;
        tmpTestDataMatrix[4][9] = 0.0;

        tmpTestDataMatrix[5][0] = 0.0;
        tmpTestDataMatrix[5][1] = 0.125;
        tmpTestDataMatrix[5][2] = 0.0;
        tmpTestDataMatrix[5][3] = 0.0;
        tmpTestDataMatrix[5][4] = 0.0;
        tmpTestDataMatrix[5][5] = 0.0;
        tmpTestDataMatrix[5][6] = 0.0;
        tmpTestDataMatrix[5][7] = 0.0;
        tmpTestDataMatrix[5][8] = 0.0;
        tmpTestDataMatrix[5][9] = 1.0;
        Assertions.assertArrayEquals(tmpTestDataMatrix,tmpImportDoubleDataMatrix);
    }
    //</editor-fold>
}

