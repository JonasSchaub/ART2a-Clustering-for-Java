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

import de.unijena.cheminf.clustering.art2a.abstractResult.ART2aAbstractResult;

import de.unijena.cheminf.clustering.art2a.interfaces.IART2aClusteringResult;
import de.unijena.cheminf.clustering.art2a.util.FileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Test class for float clustering.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class ART2aFloatClusteringTaskTest {
    //<editor-fold desc="Private static class variables" defaultstate="collapsed">
    /**
     * Clustering result instance
     */
    private static ART2aAbstractResult clusteringResult;
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
    private static float[] clusterAnglesForAllVigilanceParameter;
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

        float[][] tmpTestDataMatrix = new float[10][28];

        //valdiazen
        tmpTestDataMatrix[0][0] = 1;
        tmpTestDataMatrix[0][1] = 0;
        tmpTestDataMatrix[0][2] = 0;
        tmpTestDataMatrix[0][3] = 0;
        tmpTestDataMatrix[0][4] = 0;
        tmpTestDataMatrix[0][5] = 0;
        tmpTestDataMatrix[0][6] = 0;
        tmpTestDataMatrix[0][7] = 0;
        tmpTestDataMatrix[0][8] = 1;
        tmpTestDataMatrix[0][9] = 0;
        tmpTestDataMatrix[0][10] = 0;
        tmpTestDataMatrix[0][11] = 0;
        tmpTestDataMatrix[0][12] = 0;
        tmpTestDataMatrix[0][13] = 0;
        tmpTestDataMatrix[0][14] = 0;
        tmpTestDataMatrix[0][15] = 0;
        tmpTestDataMatrix[0][16] = 0;
        tmpTestDataMatrix[0][17] = 1;
        tmpTestDataMatrix[0][18] = 0;
        tmpTestDataMatrix[0][19] = 0;
        tmpTestDataMatrix[0][20] = 0;
        tmpTestDataMatrix[0][21] = 0;
        tmpTestDataMatrix[0][22] = 0;
        tmpTestDataMatrix[0][23] = 0;
        tmpTestDataMatrix[0][24] = 0;
        tmpTestDataMatrix[0][25] = 0;
        tmpTestDataMatrix[0][26] = 0;
        tmpTestDataMatrix[0][27] = 0;

        // Napthomycin d
        tmpTestDataMatrix[1][0] = 0;
        tmpTestDataMatrix[1][1] = 0;
        tmpTestDataMatrix[1][2] = 0;
        tmpTestDataMatrix[1][3] = 0;
        tmpTestDataMatrix[1][4] = 0;
        tmpTestDataMatrix[1][5] = 1;
        tmpTestDataMatrix[1][6] = 1;
        tmpTestDataMatrix[1][7] = 1;
        tmpTestDataMatrix[1][8] = 0;
        tmpTestDataMatrix[1][9] = 0;
        tmpTestDataMatrix[1][10] = 0;
        tmpTestDataMatrix[1][11] = 0;
        tmpTestDataMatrix[1][12] = 1;
        tmpTestDataMatrix[1][13] = 0;
        tmpTestDataMatrix[1][14] = 0;
        tmpTestDataMatrix[1][15] = 1;
        tmpTestDataMatrix[1][16] = 0;
        tmpTestDataMatrix[1][17] = 1;
        tmpTestDataMatrix[1][18] = 0;
        tmpTestDataMatrix[1][19] = 0;
        tmpTestDataMatrix[1][20] = 0;
        tmpTestDataMatrix[1][21] = 1;
        tmpTestDataMatrix[1][22] = 0;
        tmpTestDataMatrix[1][23] = 0;
        tmpTestDataMatrix[1][24] = 0;
        tmpTestDataMatrix[1][25] = 1;
        tmpTestDataMatrix[1][26] = 1;
        tmpTestDataMatrix[1][27] = 1;

        // Nona-2,6-dienal
        tmpTestDataMatrix[2][0] = 0;
        tmpTestDataMatrix[2][1] = 0;
        tmpTestDataMatrix[2][2] = 0;
        tmpTestDataMatrix[2][3] = 0;
        tmpTestDataMatrix[2][4] = 0;
        tmpTestDataMatrix[2][5] = 0;
        tmpTestDataMatrix[2][6] = 0;
        tmpTestDataMatrix[2][7] = 0;
        tmpTestDataMatrix[2][8] = 0;
        tmpTestDataMatrix[2][9] = 0;
        tmpTestDataMatrix[2][10] = 0;
        tmpTestDataMatrix[2][11] = 0;
        tmpTestDataMatrix[2][12] = 1;
        tmpTestDataMatrix[2][13] = 0;
        tmpTestDataMatrix[2][14] = 0;
        tmpTestDataMatrix[2][15] = 0;
        tmpTestDataMatrix[2][16] = 0;
        tmpTestDataMatrix[2][17] = 0;
        tmpTestDataMatrix[2][18] = 0;
        tmpTestDataMatrix[2][19] = 1;
        tmpTestDataMatrix[2][20] = 0;
        tmpTestDataMatrix[2][21] = 1;
        tmpTestDataMatrix[2][22] = 0;
        tmpTestDataMatrix[2][23] = 0;
        tmpTestDataMatrix[2][24] = 0;
        tmpTestDataMatrix[2][25] = 0;
        tmpTestDataMatrix[2][26] = 0;
        tmpTestDataMatrix[2][27] = 0;

        // Istanbulin A
        tmpTestDataMatrix[3][0] = 0;
        tmpTestDataMatrix[3][1] = 0;
        tmpTestDataMatrix[3][2] = 0;
        tmpTestDataMatrix[3][3] = 0;
        tmpTestDataMatrix[3][4] = 0;
        tmpTestDataMatrix[3][5] = 1;
        tmpTestDataMatrix[3][6] = 0;
        tmpTestDataMatrix[3][7] = 0;
        tmpTestDataMatrix[3][8] = 0;
        tmpTestDataMatrix[3][9] = 0;
        tmpTestDataMatrix[3][10] = 0;
        tmpTestDataMatrix[3][11] = 0;
        tmpTestDataMatrix[3][12] = 0;
        tmpTestDataMatrix[3][13] = 1;
        tmpTestDataMatrix[3][14] = 0;
        tmpTestDataMatrix[3][15] = 0;
        tmpTestDataMatrix[3][16] = 1;
        tmpTestDataMatrix[3][17] = 0;
        tmpTestDataMatrix[3][18] = 0;
        tmpTestDataMatrix[3][19] = 0;
        tmpTestDataMatrix[3][20] = 0;
        tmpTestDataMatrix[3][21] = 0;
        tmpTestDataMatrix[3][22] = 0;
        tmpTestDataMatrix[3][23] = 0;
        tmpTestDataMatrix[3][24] = 1;
        tmpTestDataMatrix[3][25] = 0;
        tmpTestDataMatrix[3][26] = 0;
        tmpTestDataMatrix[3][27] = 0;

        // Estradiol
        tmpTestDataMatrix[4][0] = 0;
        tmpTestDataMatrix[4][1] = 0;
        tmpTestDataMatrix[4][2] = 0;
        tmpTestDataMatrix[4][3] = 0;
        tmpTestDataMatrix[4][4] = 1;
        tmpTestDataMatrix[4][5] = 0;
        tmpTestDataMatrix[4][6] = 0;
        tmpTestDataMatrix[4][7] = 0;
        tmpTestDataMatrix[4][8] = 0;
        tmpTestDataMatrix[4][9] = 0;
        tmpTestDataMatrix[4][10] = 0;
        tmpTestDataMatrix[4][11] = 0;
        tmpTestDataMatrix[4][12] = 0;
        tmpTestDataMatrix[4][13] = 0;
        tmpTestDataMatrix[4][14] = 0;
        tmpTestDataMatrix[4][15] = 0;
        tmpTestDataMatrix[4][16] = 0;
        tmpTestDataMatrix[4][17] = 1;
        tmpTestDataMatrix[4][18] = 0;
        tmpTestDataMatrix[4][19] = 0;
        tmpTestDataMatrix[4][20] = 0;
        tmpTestDataMatrix[4][21] = 0;
        tmpTestDataMatrix[4][22] = 0;
        tmpTestDataMatrix[4][23] = 0;
        tmpTestDataMatrix[4][24] = 0;
        tmpTestDataMatrix[4][25] = 0;
        tmpTestDataMatrix[4][26] = 0;
        tmpTestDataMatrix[4][27] = 1;

        // Paradise
        tmpTestDataMatrix[5][0] = 0;
        tmpTestDataMatrix[5][1] = 1;
        tmpTestDataMatrix[5][2] = 0;
        tmpTestDataMatrix[5][3] = 0;
        tmpTestDataMatrix[5][4] = 0;
        tmpTestDataMatrix[5][5] = 0;
        tmpTestDataMatrix[5][6] = 0;
        tmpTestDataMatrix[5][7] = 0;
        tmpTestDataMatrix[5][8] = 0;
        tmpTestDataMatrix[5][9] = 0;
        tmpTestDataMatrix[5][10] = 0;
        tmpTestDataMatrix[5][11] = 0;
        tmpTestDataMatrix[5][12] = 0;
        tmpTestDataMatrix[5][13] = 0;
        tmpTestDataMatrix[5][14] = 0;
        tmpTestDataMatrix[5][15] = 0;
        tmpTestDataMatrix[5][16] = 0;
        tmpTestDataMatrix[5][17] = 0;
        tmpTestDataMatrix[5][18] = 0;
        tmpTestDataMatrix[5][19] = 0;
        tmpTestDataMatrix[5][20] = 1;
        tmpTestDataMatrix[5][21] = 0;
        tmpTestDataMatrix[5][22] = 0;
        tmpTestDataMatrix[5][23] = 0;
        tmpTestDataMatrix[5][24] = 0;
        tmpTestDataMatrix[5][25] = 0;
        tmpTestDataMatrix[5][26] = 0;
        tmpTestDataMatrix[5][27] = 0;

        // Curumin
        tmpTestDataMatrix[6][0] = 0;
        tmpTestDataMatrix[6][1] = 0;
        tmpTestDataMatrix[6][2] = 0;
        tmpTestDataMatrix[6][3] = 0;
        tmpTestDataMatrix[6][4] = 0;
        tmpTestDataMatrix[6][5] = 1;
        tmpTestDataMatrix[6][6] = 0;
        tmpTestDataMatrix[6][7] = 0;
        tmpTestDataMatrix[6][8] = 0;
        tmpTestDataMatrix[6][9] = 0;
        tmpTestDataMatrix[6][10] = 0;
        tmpTestDataMatrix[6][11] = 0;
        tmpTestDataMatrix[6][12] = 0;
        tmpTestDataMatrix[6][13] = 0;
        tmpTestDataMatrix[6][14] = 0;
        tmpTestDataMatrix[6][15] = 0;
        tmpTestDataMatrix[6][16] = 0;
        tmpTestDataMatrix[6][17] = 0;
        tmpTestDataMatrix[6][18] = 1;
        tmpTestDataMatrix[6][19] = 0;
        tmpTestDataMatrix[6][20] = 1;
        tmpTestDataMatrix[6][21] = 0;
        tmpTestDataMatrix[6][22] = 0;
        tmpTestDataMatrix[6][23] = 0;
        tmpTestDataMatrix[6][24] = 0;
        tmpTestDataMatrix[6][25] = 1;
        tmpTestDataMatrix[6][26] = 0;
        tmpTestDataMatrix[6][27] = 1;

        // Catechin
        tmpTestDataMatrix[7][0] = 0;
        tmpTestDataMatrix[7][1] = 0;
        tmpTestDataMatrix[7][2] = 0;
        tmpTestDataMatrix[7][3] = 0;
        tmpTestDataMatrix[7][4] = 0;
        tmpTestDataMatrix[7][5] = 0;
        tmpTestDataMatrix[7][6] = 0;
        tmpTestDataMatrix[7][7] = 0;
        tmpTestDataMatrix[7][8] = 0;
        tmpTestDataMatrix[7][9] = 0;
        tmpTestDataMatrix[7][10] = 1;
        tmpTestDataMatrix[7][11] = 0;
        tmpTestDataMatrix[7][12] = 0;
        tmpTestDataMatrix[7][13] = 0;
        tmpTestDataMatrix[7][14] = 0;
        tmpTestDataMatrix[7][15] = 0;
        tmpTestDataMatrix[7][16] = 0;
        tmpTestDataMatrix[7][17] = 1;
        tmpTestDataMatrix[7][18] = 1;
        tmpTestDataMatrix[7][19] = 0;
        tmpTestDataMatrix[7][20] = 0;
        tmpTestDataMatrix[7][21] = 0;
        tmpTestDataMatrix[7][22] = 0;
        tmpTestDataMatrix[7][23] = 0;
        tmpTestDataMatrix[7][24] = 0;
        tmpTestDataMatrix[7][25] = 0;
        tmpTestDataMatrix[7][26] = 0;
        tmpTestDataMatrix[7][27] = 1;

        // Bittersweet
        tmpTestDataMatrix[8][0] = 0;
        tmpTestDataMatrix[8][1] = 0;
        tmpTestDataMatrix[8][2] = 1;
        tmpTestDataMatrix[8][3] = 1;
        tmpTestDataMatrix[8][4] = 0;
        tmpTestDataMatrix[8][5] = 1;
        tmpTestDataMatrix[8][6] = 0;
        tmpTestDataMatrix[8][7] = 0;
        tmpTestDataMatrix[8][8] = 0;
        tmpTestDataMatrix[8][9] = 0;
        tmpTestDataMatrix[8][10] = 0;
        tmpTestDataMatrix[8][11] = 1;
        tmpTestDataMatrix[8][12] = 0;
        tmpTestDataMatrix[8][13] = 0;
        tmpTestDataMatrix[8][14] = 0;
        tmpTestDataMatrix[8][15] = 0;
        tmpTestDataMatrix[8][16] = 0;
        tmpTestDataMatrix[8][17] = 0;
        tmpTestDataMatrix[8][18] = 0;
        tmpTestDataMatrix[8][19] = 0;
        tmpTestDataMatrix[8][20] = 0;
        tmpTestDataMatrix[8][21] = 0;
        tmpTestDataMatrix[8][22] = 1;
        tmpTestDataMatrix[8][23] = 1;
        tmpTestDataMatrix[8][24] = 0;
        tmpTestDataMatrix[8][25] = 0;
        tmpTestDataMatrix[8][26] = 0;
        tmpTestDataMatrix[8][27] = 0;

        // Variamycin
        tmpTestDataMatrix[9][0] = 0;
        tmpTestDataMatrix[9][1] = 0;
        tmpTestDataMatrix[9][2] = 0;
        tmpTestDataMatrix[9][3] = 1;
        tmpTestDataMatrix[9][4] = 0;
        tmpTestDataMatrix[9][5] = 1;
        tmpTestDataMatrix[9][6] = 0;
        tmpTestDataMatrix[9][7] = 0;
        tmpTestDataMatrix[9][8] = 0;
        tmpTestDataMatrix[9][9] = 1;
        tmpTestDataMatrix[9][10] = 0;
        tmpTestDataMatrix[9][11] = 0;
        tmpTestDataMatrix[9][12] = 0;
        tmpTestDataMatrix[9][13] = 0;
        tmpTestDataMatrix[9][14] = 1;
        tmpTestDataMatrix[9][15] = 0;
        tmpTestDataMatrix[9][16] = 1;
        tmpTestDataMatrix[9][17] = 1;
        tmpTestDataMatrix[9][18] = 1;
        tmpTestDataMatrix[9][19] = 0;
        tmpTestDataMatrix[9][20] = 0;
        tmpTestDataMatrix[9][21] = 0;
        tmpTestDataMatrix[9][22] = 0;
        tmpTestDataMatrix[9][23] = 0;
        tmpTestDataMatrix[9][24] = 0;
        tmpTestDataMatrix[9][25] = 0;
        tmpTestDataMatrix[9][26] = 1;
        tmpTestDataMatrix[9][27] = 1;

        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(9); // number of tasks
        List<ART2aClusteringTask> tmpClusteringTask = new LinkedList<>();
        for (float tmpVigilanceParameter = 0.1f; tmpVigilanceParameter < 1.0f; tmpVigilanceParameter += 0.1f) {
            ART2aClusteringTask tmpART2aFloatClusteringTask = new ART2aClusteringTask(tmpVigilanceParameter, tmpTestDataMatrix, 2,true);
            tmpClusteringTask.add(tmpART2aFloatClusteringTask);
        }
        PrintWriter[] tmpPrintWriter = FileUtil.createClusteringResultInFile("Clustering_Result_Folder");
        List<Future<IART2aClusteringResult>> tmpFuturesList;
      //  ART2aAbstractResult tmpClusteringResult;
        ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter = new int[9];
        ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter = new int[9];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter = new int[9][];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[0] = new int[4];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[1] = new int[1];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[2] = new int[1];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[3] = new int[1];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[4] = new int[1];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[5] = new int[1];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[6] = new int[1];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[7] = new int[1];
        ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[8] = new int[1];
        ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter = new int[9];
        ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter = new float[9];
        tmpFuturesList = tmpExecutorService.invokeAll(tmpClusteringTask);
        int tmpIterator = 0;
        for (Future<IART2aClusteringResult> tmpFuture : tmpFuturesList) {
            try {
                ART2aFloatClusteringTaskTest.clusteringResult = (ART2aAbstractResult) tmpFuture.get();
                ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[tmpIterator] = ART2aFloatClusteringTaskTest.clusteringResult.getNumberOfEpochs();
                ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[tmpIterator] = ART2aFloatClusteringTaskTest.clusteringResult.getNumberOfDetectedClusters();
                ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[tmpIterator] = ART2aFloatClusteringTaskTest.clusteringResult.getClusterIndices(tmpIterator);
                ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[tmpIterator] = (float) ART2aFloatClusteringTaskTest.clusteringResult.getAngleBetweenClusters(tmpIterator, tmpIterator + 1);
                ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[tmpIterator] = ART2aFloatClusteringTaskTest.clusteringResult.getClusterRepresentatives(tmpIterator);
                ART2aFloatClusteringTaskTest.clusteringResult.exportClusteringResultsToTextFiles(tmpPrintWriter[0], tmpPrintWriter[1]);
                tmpIterator++;
            } catch (RuntimeException anException) {
                System.out.println(anException);
            }
        }
        tmpPrintWriter[0].flush();
        tmpPrintWriter[0].close();
        tmpPrintWriter[1].flush();
        tmpPrintWriter[1].close();
        tmpExecutorService.shutdown();
      //  Assertions.assertEquals(true, true);
    }
    //</editor-fold>
    //
    //<editor-fold desc="Private method" defaultstate="collapsed">
    /**
     * Sets a user defined numeric tolerance between the excepted number and actual number.
     *
     * @param tmpExpectedNumber excepted number
     * @param tmpActualNumber actual number
     * @param tmpTolerance user defined tolerance
     * @return true, if the tolerance is not exceeded, otherwise false.
     */
    public boolean isEqual(float tmpExpectedNumber, float tmpActualNumber, float tmpTolerance) {
        return Math.abs(tmpExpectedNumber-tmpActualNumber) <= tmpTolerance;
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="tests the number of epochs for all vigilance parameter">
    /**
     * Tests number of epoch for vigilance parameter 0.1f
     */
    @Test
    public void testNumberOfEpochsFor01f() {
        int tmpTestNumberOfEpochsFor01f = 2;
        int tmpNumberOfEpochs01f = ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[0];
        Assertions.assertEquals(tmpTestNumberOfEpochsFor01f, tmpNumberOfEpochs01f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.2f
     */
    @Test
    public void testNumberOfEpochsFor02f() {
        int tmpTestNumberOfEpochs02f = 2;
        int tmpNumberOfEpochs02f =ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[1];
        Assertions.assertEquals(tmpTestNumberOfEpochs02f, tmpNumberOfEpochs02f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.3f
     */
    @Test
    public void testNumberOfEpochsFor03() {
        int tmpTestNumberOfEpochs03f = 2;
        int tmpNumberOfEpochs03f = ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[2];
        Assertions.assertEquals(tmpTestNumberOfEpochs03f, tmpNumberOfEpochs03f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.4f
     */
    @Test
    public void testNumberOfEpochsFor04() {
        int tmpTestNumberOfEpochs04f = 2;
        int tmpNumberOfEpochs04f = ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[3];
        Assertions.assertEquals(tmpTestNumberOfEpochs04f, tmpNumberOfEpochs04f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.5f
     */
    @Test
    public void testNumberOfEpochsFor05() {
        int tmpTestNumberOfEpochs05f = 2;
        int tmpNumberOfEpochs05f = ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[4];
        Assertions.assertEquals(tmpTestNumberOfEpochs05f, tmpNumberOfEpochs05f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.6f
     */
    @Test
    public void testNumberOfEpochsFor06() {
        int tmpTestNumberOfEpochs06f = 2;
        int tmpNumberOfEpochs06f = ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[5];
        Assertions.assertEquals(tmpTestNumberOfEpochs06f, tmpNumberOfEpochs06f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.7f
     */
    @Test
    public void testNumberOfEpochsFor07() {
        int tmpTestNumberOfEpochs07f = 2;
        int tmpNumberOfEpochs07f = ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[6];
        Assertions.assertEquals(tmpTestNumberOfEpochs07f, tmpNumberOfEpochs07f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.8f
     */
    @Test
    public void testNumberOfEpochsFor08() {
        int tmpTestNumberOfEpochs08f = 2;
        int tmpNumberOfEpochs08f = ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[7];
        Assertions.assertEquals(tmpTestNumberOfEpochs08f, tmpNumberOfEpochs08f);
    }
    //
    /**
     * Tests number of epoch for vigilance parameter 0.9f
     */
    @Test
    public void testNumberOfEpochsFor09() {
        int tmpTestNumberOfEpochs09f = 2;
        int tmpNumberOfEpochs09f = ART2aFloatClusteringTaskTest.numberOfEpochsForAllVigilanceParameter[8];
        Assertions.assertEquals(tmpTestNumberOfEpochs09f, tmpNumberOfEpochs09f);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="tests the number of detected clusters for all vigilance parameter">
    /**
     * Tests number of detected clusters for vigilance parameter 0.1f
     */
    @Test
    public void testNumberOfDetectedClustersFor01() {
        int tmpTestNumberOfDetectedClusters01 = 6;
        int tmpNumberOfDetectedClusters01 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[0];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters01, tmpNumberOfDetectedClusters01);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.2f
     */
    @Test
    public void testNumberOfDetectedClustersFor02() {
        int tmpTestNumberOfDetectedClusters02 = 6;
        int tmpNumberOfDetectedClusters02 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[1];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters02, tmpNumberOfDetectedClusters02);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.3f
     */
    @Test
    public void testNumberOfDetectedClustersFor03() {
        int tmpTestNumberOfDetectedClusters03 = 6;
        int tmpNumberOfDetectedClusters03 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[2];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters03, tmpNumberOfDetectedClusters03);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.4f
     */
    @Test
    public void testNumberOfDetectedClustersFor04() {
        int tmpTestNumberOfDetectedClusters04 = 8;
        int tmpNumberOfDetectedClusters04 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[3];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters04, tmpNumberOfDetectedClusters04);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.5f
     */
    @Test
    public void testNumberOfDetectedClustersFor05() {
        int tmpTestNumberOfDetectedClusters05 = 9;
        int tmpNumberOfDetectedClusters05 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[4];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters05, tmpNumberOfDetectedClusters05);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.6f
     */
    @Test
    public void testNumberOfDetectedClustersFor06() {
        int tmpTestNumberOfDetectedClusters06 = 10;
        int tmpNumberOfDetectedClusters06 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[5];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters06, tmpNumberOfDetectedClusters06);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.7f
     */
    @Test
    public void testNumberOfDetectedClustersFor07() {
        int tmpTestNumberOfDetectedClusters07 = 10;
        int tmpNumberOfDetectedClusters07 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[6];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters07, tmpNumberOfDetectedClusters07);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.8f
     */
    @Test
    public void testNumberOfDetectedClustersFor08() {
        int tmpTestNumberOfDetectedClusters08 = 10;
        int tmpNumberOfDetectedClusters08 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[7];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters08, tmpNumberOfDetectedClusters08);
    }
    //
    /**
     * Tests number of detected clusters for vigilance parameter 0.9f
     */
    @Test
    public void testNumberOfDetectedClustersFor09() {
        int tmpTestNumberOfDetectedClusters09 = 10;
        int tmpNumberOfDetectedClusters09 = ART2aFloatClusteringTaskTest.numberOfDetectedClustersForAllVigilanceParameter[8];
        Assertions.assertEquals(tmpTestNumberOfDetectedClusters09, tmpNumberOfDetectedClusters09);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="tests the number of detected clusters for all vigilance parameter">
    /**
     * Tests the cluster indices in cluster 0 for vigilance parameter 0.1f
     */
    @Test
    public void testClusterIndicesInCluster0ForVigilanceParameter01() {
        int[] tmpTestClusterIndicesInCluster0For01 = {4,6,7,9};
        int[] tmpClusterIndicesInClusterFor0For01 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[0];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster0For01, tmpClusterIndicesInClusterFor0For01);
    }
    //
    /**
     *  Tests the cluster indices in cluster 1 for vigilance parameter 0.2f
     */
    @Test
    public void testClusterIndicesInCLuster1ForVigilanceParameter02() {
        int[] tmpTestClusterIndicesInCluster1For02 = {1,2};
        int[] tmpClusterIndicesInClusterFor1For02 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[1];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster1For02, tmpClusterIndicesInClusterFor1For02);
    }
    //
    /**
     *  Tests the cluster indices in cluster 2 for vigilance parameter 0.3f
     */
    @Test
    public void testClusterIndicesInCLuster2ForVigilanceParameter03() {
        int[] tmpTestClusterIndicesInCluster2For03 = {3};
        int[] tmpClusterIndicesInClusterFor2For03 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[2];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster2For03, tmpClusterIndicesInClusterFor2For03);
    }
    //
    /**
     *  Tests the cluster indices in cluster 3 for vigilance parameter 0.4f
     */
    @Test
    public void testClusterIndicesInCLuster3ForVigilanceParameter04() {
        int[] tmpTestClusterIndicesInCluster3For04 = {2};
        int[] tmpClusterIndicesInClusterFor3For04 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[3];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster3For04, tmpClusterIndicesInClusterFor3For04);
    }
    //
    /**
     *  Tests the cluster indices in cluster 4 for vigilance parameter 0.5f
     */
    @Test
    public void testClusterIndicesInCLuster4ForVigilanceParameter05() {
        int[] tmpTestClusterIndicesInCluster4For05 = {5};
        int[] tmpClusterIndicesInCluster4For05 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[4];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster4For05, tmpClusterIndicesInCluster4For05);
    }
    //
    /**
     *  Tests the cluster indices in cluster 5 for vigilance parameter 0.6f
     */
    @Test
    public void testClusterIndicesInCLuster5ForVigilanceParameter06() {
        int[] tmpTestClusterIndicesInCluster5For06 = {5};
        int[] tmpClusterIndicesInCluster5For06 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[5];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster5For06, tmpClusterIndicesInCluster5For06);
    }
    //
    /**
     *  Tests the cluster indices in cluster 6 for vigilance parameter 0.7f
     */
    @Test
    public void testClusterIndicesInCLuster6ForVigilanceParameter07() {
        int[] tmpTestClusterIndicesInCluster6For07 = {6};
        int[] tmpClusterIndicesInClusterFor6For07 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[6];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster6For07, tmpClusterIndicesInClusterFor6For07);
    }
    //
    /**
     *  Tests the cluster indices in cluster 7 for vigilance parameter 0.8f
     */
    @Test
    public void testClusterIndicesInCLuster7ForVigilanceParameter08() {
        int[] tmpTestClusterIndicesInCluster7For08 = {4};
        int[] tmpClusterIndicesInCluster7For08 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[7];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster7For08, tmpClusterIndicesInCluster7For08);
    }
    //
    /**
     *  Tests the cluster indices in cluster 8 for vigilance parameter 0.9f
     */
    @Test
    public void testClusterIndicesInCLuster8ForVigilanceParameter09() {
        int[] tmpTestClusterIndicesInCluster8For09 = {8};
        int[] tmpClusterIndicesInCluster8For09 = ART2aFloatClusteringTaskTest.clusterIndicesForAllVigilanceParameter[8];
        Assertions.assertArrayEquals(tmpTestClusterIndicesInCluster8For09, tmpClusterIndicesInCluster8For09);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="tests cluster representatives in different clusters for all vigilance parameter">
    /**
     * Tests the cluster representatives in cluster 0 for vigilance parameter 0.1f
     */
    @Test
    public void testClusterRepresentativesInCluster0ForVigilanceParameter01() {
        int tmpTestClusterRepresentativesIndexInCluster0For01 = 9;
        int tmpClusterRepresentativesIndexInCluster0For01 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[0];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster0For01, tmpClusterRepresentativesIndexInCluster0For01);
    }
    //
    /**
     * Tests the cluster representatives in cluster 1 for vigilance parameter 0.2f
     */
    @Test
    public void testClusterRepresentativesInCluster1ForVigilanceParameter02() {
        int tmpTestClusterRepresentativesIndexInCluster1For02 = 2;
        int tmpClusterRepresentativesIndexInCluster1For02 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[1];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster1For02, tmpClusterRepresentativesIndexInCluster1For02);
    }
    //
    /**
     * Tests the cluster representatives in cluster 2 for vigilance parameter 0.3f
     */
    @Test
    public void testClusterRepresentativesInCluster2ForVigilanceParameter03() {
        int tmpTestClusterRepresentativesIndexInCluster2For03 = 3;
        int tmpClusterRepresentativesIndexInCluster2For03 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[2];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster2For03, tmpClusterRepresentativesIndexInCluster2For03);
    }
    //
    /**
     * Tests the cluster representatives in cluster 3 for vigilance parameter 0.4f
     */
    @Test
    public void testClusterRepresentativesInCluster3ForVigilanceParameter04() {
        int tmpTestClusterRepresentativesIndexInCluster3For04 = 2;
        int tmpClusterRepresentativesIndexInCluster3For04 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[3];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster3For04, tmpClusterRepresentativesIndexInCluster3For04);
    }
    //
    /**
     * Tests the cluster representatives in cluster 4 for vigilance parameter 0.5f
     */
    @Test
    public void testClusterRepresentativesInCluster4ForVigilanceParameter05() {
        int tmpTestClusterRepresentativesIndexInCluster4For05 = 5;
        int tmpClusterRepresentativesIndexInCluster4For05 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[4];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster4For05, tmpClusterRepresentativesIndexInCluster4For05);
    }
    //
    /**
     * Tests the cluster representatives in cluster 5 for vigilance parameter 0.6f
     */
    @Test
    public void testClusterRepresentativesInCluster5ForVigilanceParameter06() {
        int tmpTestClusterRepresentativesIndexInCluster5For06 = 5;
        int tmpClusterRepresentativesIndexInCluster5For06 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[5];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster5For06, tmpClusterRepresentativesIndexInCluster5For06);
    }
    //
    /**
     * Tests the cluster representatives in cluster 6 for vigilance parameter 0.7f;
     */
    @Test
    public void testClusterRepresentativesInCluster6ForVigilanceParameter07() {
        int tmpTestClusterRepresentativesIndexInCluster6For07 = 6;
        int tmpClusterRepresentativesIndexInCluster6For07 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[6];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster6For07, tmpClusterRepresentativesIndexInCluster6For07);
    }
    //
    /**
     * Tests the cluster representatives in cluster 7 for vigilance parameter 0.8f
     */
    @Test
    public void testClusterRepresentativesInCluster7ForVigilanceParameter08() {
        int tmpTestClusterRepresentativesIndexInCluster7For08 = 4;
        int tmpClusterRepresentativesIndexInCluster7For08 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[7];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster7For08, tmpClusterRepresentativesIndexInCluster7For08);
    }
    //
    /**
     * Tests the cluster representatives in cluster 8 for vigilance parameter 0.9f
     */
    @Test
    public void testClusterRepresentativesInCluster8ForVigilanceParameter09() {
        int tmpTestClusterRepresentativesIndexInCluster8For09 = 8;
        int tmpClusterRepresentativesIndexInCluster8For09 = ART2aFloatClusteringTaskTest.clusterRepresentativesForAllVigilanceParameter[8];
        Assertions.assertEquals(tmpTestClusterRepresentativesIndexInCluster8For09, tmpClusterRepresentativesIndexInCluster8For09);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="tests angle between cluster for all vigilance parameter">
    /**
     * Tests the angle between cluster 0 and 1 for vigilance parameter 0.1f
     */
    @Test
    public void testAngleBetweenCluster0And1For01() {
        float tmpTestAngleBetweenCluster0And1For01 = 64.71957f;
        float tmpAngleBetweenCluster0And1For01 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[0];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster0And1For01,tmpAngleBetweenCluster0And1For01,tmpTolerance));
    }
    //
    /**
     *  Tests the angle between cluster 1 and 2 for vigilance parameter 0.2f
     */
    @Test
    public void testAngleBetweenCluster1And2For02() {
        float tmpTestAngleBetweenCluster1And2For02 = 80.98592f;
        float tmpAngleBetweenCluster1And2For02 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[1];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster1And2For02, tmpAngleBetweenCluster1And2For02,tmpTolerance));
    }
    //
    /**
     *  Tests the angle between cluster 2 and 3 for vigilance parameter 0.3f
     */
    @Test
    public void testAngleBetweenCluster2And3For03() {
        float tmpTestAngleBetweenCluster2And3For03 = 90.000f;
        float tmpAngleBetweenCluster2And3For03 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[2];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster2And3For03, tmpAngleBetweenCluster2And3For03,tmpTolerance));
    }
    //
    /**
     *  Tests the angle between cluster 3 and 4 for vigilance parameter 0.4f
     */
    @Test
    public void testAngleBetweenCluster3And4For04() {
        float tmpTestAngleBetweenCluster3And4For04 = 90.000f;
        float tmpAngleBetweenCluster3And4For04 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[3];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster3And4For04, tmpAngleBetweenCluster3And4For04,tmpTolerance));
    }
    //
    /**
     *  Tests the angle between cluster 4 and 5 for vigilance parameter 0.5f
     */
    @Test
    public void testAngleBetweenCluster4And5For05() {
        float tmpTestAngleBetweenCluster4And5For05 = 71.56505f;
        float tmpAngleBetweenCluster4And5For05 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[4];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster4And5For05, tmpAngleBetweenCluster4And5For05,tmpTolerance));
    }
    //
    /**
     *  Tests the angle between cluster 5 and 6 for vigilance parameter 0.6f
     */
    @Test
    public void testAngleBetweenCluster5And6For06() {
        float tmpTestAngleBetweenCluster5And6For06 = 71.56505f;
        float tmpAngleBetweenCluster5And6For06 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[5];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster5And6For06, tmpAngleBetweenCluster5And6For06,tmpTolerance));
    }
    //
    /**
     *  Tests the angle between cluster 6 and 7 for vigilance parameter 0.7f
     */
    @Test
    public void testAngleBetweenCluster6And7For07() {
        float tmpTestAngleBetweenCluster6And7For07 = 75.03678f;
        float tmpAngleBetweenCluster6And7For07 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[6];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster6And7For07, tmpAngleBetweenCluster6And7For07,tmpTolerance));
    }
    //
    /**
     *  Tests the angle between cluster 7 and 8 for vigilance parameter 0.8f
     */
    @Test
    public void testAngleBetweenCluster7And8For08() {
        float tmpTestAngleBetweenCluster7And8For08 = 90.000f;
        float tmpAngleBetweenCluster7And8For08 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[7];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster7And8For08, tmpAngleBetweenCluster7And8For08,tmpTolerance));
    }
    //
    /**
     *  Tests the angle between cluster 8 and 9 for vigilance parameter 0.9f
     */
    @Test
    public void testAngleBetweenCluster8And9For09() {
        float tmpTestAngleBetweenCluster8And9For09 = 90.000f;
        float tmpAngleBetweenCluster8And9For09 = ART2aFloatClusteringTaskTest.clusterAnglesForAllVigilanceParameter[8];
        float tmpTolerance = 1e-3f;
        Assertions.assertTrue(isEqual(tmpTestAngleBetweenCluster8And9For09, tmpAngleBetweenCluster8And9For09,tmpTolerance));
    }
    //</editor-fold>
    //
}
