/*
 * GNU General Public License v3.0
 *
 * Copyright (c) 2022 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
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

import de.unijena.cheminf.clustering.art2a.Abstract.ART2aAbstractResult;

import de.unijena.cheminf.clustering.art2a.Util.FileUtil;
import org.junit.jupiter.api.Assertions;
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
    //<editor-fold desc="Test methods" defaultstate="collapsed">
    /**
     * Test method
     * TODO add test methods
     *
     */
    @Test
    public void startArt2aClusteringTest() throws Exception {

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
        PrintWriter tmpClusteringProcessWriter = FileUtil.createClusteringProcessInFile("ClusteringFolder");
        PrintWriter tmpClusteringResultWriter = FileUtil.createClusteringResultInFile("ClusteringFolder");
        List<Future<ART2aAbstractResult>> tmpFuturesList;
        ART2aAbstractResult tmpClusteringResult;
        tmpFuturesList = tmpExecutorService.invokeAll(tmpClusteringTask);
        System.out.println("\nCLUSTERING RESULTS:\n");
        for (Future<ART2aAbstractResult> tmpFuture : tmpFuturesList) {
            tmpClusteringResult = tmpFuture.get();
            System.out.println("vigilance parameter: " + tmpClusteringResult.getVigilanceParameter() );
            System.out.println("number of epochs: " + tmpClusteringResult.getNumberOfEpochs());
            System.out.println("cluster indices in cluster 0: " + java.util.Arrays.toString(tmpClusteringResult.getClusterIndices(0)));
            System.out.println("convergence status: " +tmpClusteringResult.getConvergenceStatus());
            System.out.println("Angle: " + tmpClusteringResult.getAngleBetweenClusters(0,1));
            System.out.println("####################################");
            tmpClusteringResult.getClusteringResultsInTextFile(tmpClusteringProcessWriter, tmpClusteringResultWriter);
        }
        tmpClusteringProcessWriter.flush();
        tmpClusteringProcessWriter.close();
        tmpClusteringResultWriter.flush();
        tmpClusteringResultWriter.close();
        tmpExecutorService.shutdown();
        Assertions.assertEquals(true, true);
    }
    //</editor-fold>
    //
}
