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

import de.unijena.cheminf.clustering.art2a.Clustering.ART2aFloatClustering;
import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClusteringResult;
import de.unijena.cheminf.clustering.art2a.Result.ART2aFloatClusteringResult;
import de.unijena.cheminf.clustering.art2a.Util.FileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Test class for ART2aClusteringTask
 *
 * @author Betuel Sevindik
 */
public class ART2aFloatClusteringTaskTest {
    //<editor-fold desc="Test methods" defaultstate="collapsed">
    /**
     * Test method
     *
     * @throws IOException is thrown if an error occurs when creating the log files.
     * @throws InterruptedException is thrown if the parallelization is disturbed.
     * @throws ExecutionException is thrown if an error occurs during the task.
     * @throws Exception
     */
    @Test
    public void startArt2aClusteringTest() throws Exception {


        float[][] dataMatrix = new float[10][28];
        //valdiazen
        dataMatrix[0][0] = 1;
        dataMatrix[0][1] = 0;
        dataMatrix[0][2] = 0;
        dataMatrix[0][3] = 0;
        dataMatrix[0][4] = 0;
        dataMatrix[0][5] = 0;
        dataMatrix[0][6] = 0;
        dataMatrix[0][7] = 0;
        dataMatrix[0][8] = 1;
        dataMatrix[0][9] = 0;
        dataMatrix[0][10] = 0;
        dataMatrix[0][11] = 0;
        dataMatrix[0][12] = 0;
        dataMatrix[0][13] = 0;
        dataMatrix[0][14] = 0;
        dataMatrix[0][15] = 0;
        dataMatrix[0][16] = 0;
        dataMatrix[0][17] = 1;
        dataMatrix[0][18] = 0;
        dataMatrix[0][19] = 0;
        dataMatrix[0][20] = 0;
        dataMatrix[0][21] = 0;
        dataMatrix[0][22] = 0;
        dataMatrix[0][23] = 0;
        dataMatrix[0][24] = 0;
        dataMatrix[0][25] = 0;
        dataMatrix[0][26] = 0;
        dataMatrix[0][27] = 0;


        // Napthomycin d
        dataMatrix[1][0] = 0;
        dataMatrix[1][1] = 0;
        dataMatrix[1][2] = 0;
        dataMatrix[1][3] = 0;
        dataMatrix[1][4] = 0;
        dataMatrix[1][5] = 1;
        dataMatrix[1][6] = 1;
        dataMatrix[1][7] = 1;
        dataMatrix[1][8] = 0;
        dataMatrix[1][9] = 0;
        dataMatrix[1][10] = 0;
        dataMatrix[1][11] = 0;
        dataMatrix[1][12] = 1;
        dataMatrix[1][13] = 0;
        dataMatrix[1][14] = 0;
        dataMatrix[1][15] = 1;
        dataMatrix[1][16] = 0;
        dataMatrix[1][17] = 1;
        dataMatrix[1][18] = 0;
        dataMatrix[1][19] = 0;
        dataMatrix[1][20] = 0;
        dataMatrix[1][21] = 1;
        dataMatrix[1][22] = 0;
        dataMatrix[1][23] = 0;
        dataMatrix[1][24] = 0;
        dataMatrix[1][25] = 1;
        dataMatrix[1][26] = 1;
        dataMatrix[1][27] = 1;

        // Nona-2,6-dienal
        dataMatrix[2][0] = 0;
        dataMatrix[2][1] = 0;
        dataMatrix[2][2] = 0;
        dataMatrix[2][3] = 0;
        dataMatrix[2][4] = 0;
        dataMatrix[2][5] = 0;
        dataMatrix[2][6] = 0;
        dataMatrix[2][7] = 0;
        dataMatrix[2][8] = 0;
        dataMatrix[2][9] = 0;
        dataMatrix[2][10] = 0;
        dataMatrix[2][11] = 0;
        dataMatrix[2][12] = 1;
        dataMatrix[2][13] = 0;
        dataMatrix[2][14] = 0;
        dataMatrix[2][15] = 0;
        dataMatrix[2][16] = 0;
        dataMatrix[2][17] = 0;
        dataMatrix[2][18] = 0;
        dataMatrix[2][19] = 1;
        dataMatrix[2][20] = 0;
        dataMatrix[2][21] = 1;
        dataMatrix[2][22] = 0;
        dataMatrix[2][23] = 0;
        dataMatrix[2][24] = 0;
        dataMatrix[2][25] = 0;
        dataMatrix[2][26] = 0;
        dataMatrix[2][27] = 0;


        // Istanbulin A
        dataMatrix[3][0] = 0;
        dataMatrix[3][1] = 0;
        dataMatrix[3][2] = 0;
        dataMatrix[3][3] = 0;
        dataMatrix[3][4] = 0;
        dataMatrix[3][5] = 1;
        dataMatrix[3][6] = 0;
        dataMatrix[3][7] = 0;
        dataMatrix[3][8] = 0;
        dataMatrix[3][9] = 0;
        dataMatrix[3][10] = 0;
        dataMatrix[3][11] = 0;
        dataMatrix[3][12] = 0;
        dataMatrix[3][13] = 1;
        dataMatrix[3][14] = 0;
        dataMatrix[3][15] = 0;
        dataMatrix[3][16] = 1;
        dataMatrix[3][17] = 0;
        dataMatrix[3][18] = 0;
        dataMatrix[3][19] = 0;
        dataMatrix[3][20] = 0;
        dataMatrix[3][21] = 0;
        dataMatrix[3][22] = 0;
        dataMatrix[3][23] = 0;
        dataMatrix[3][24] = 1;
        dataMatrix[3][25] = 0;
        dataMatrix[3][26] = 0;
        dataMatrix[3][27] = 0;


        // Estradiol
        dataMatrix[4][0] = 0;
        dataMatrix[4][1] = 0;
        dataMatrix[4][2] = 0;
        dataMatrix[4][3] = 0;
        dataMatrix[4][4] = 1;
        dataMatrix[4][5] = 0;
        dataMatrix[4][6] = 0;
        dataMatrix[4][7] = 0;
        dataMatrix[4][8] = 0;
        dataMatrix[4][9] = 0;
        dataMatrix[4][10] = 0;
        dataMatrix[4][11] = 0;
        dataMatrix[4][12] = 0;
        dataMatrix[4][13] = 0;
        dataMatrix[4][14] = 0;
        dataMatrix[4][15] = 0;
        dataMatrix[4][16] = 0;
        dataMatrix[4][17] = 1;
        dataMatrix[4][18] = 0;
        dataMatrix[4][19] = 0;
        dataMatrix[4][20] = 0;
        dataMatrix[4][21] = 0;
        dataMatrix[4][22] = 0;
        dataMatrix[4][23] = 0;
        dataMatrix[4][24] = 0;
        dataMatrix[4][25] = 0;
        dataMatrix[4][26] = 0;
        dataMatrix[4][27] = 1;

        // Paradise
        dataMatrix[5][0] = 0;
        dataMatrix[5][1] = 1;
        dataMatrix[5][2] = 0;
        dataMatrix[5][3] = 0;
        dataMatrix[5][4] = 0;
        dataMatrix[5][5] = 0;
        dataMatrix[5][6] = 0;
        dataMatrix[5][7] = 0;
        dataMatrix[5][8] = 0;
        dataMatrix[5][9] = 0;
        dataMatrix[5][10] = 0;
        dataMatrix[5][11] = 0;
        dataMatrix[5][12] = 0;
        dataMatrix[5][13] = 0;
        dataMatrix[5][14] = 0;
        dataMatrix[5][15] = 0;
        dataMatrix[5][16] = 0;
        dataMatrix[5][17] = 0;
        dataMatrix[5][18] = 0;
        dataMatrix[5][19] = 0;
        dataMatrix[5][20] = 1;
        dataMatrix[5][21] = 0;
        dataMatrix[5][22] = 0;
        dataMatrix[5][23] = 0;
        dataMatrix[5][24] = 0;
        dataMatrix[5][25] = 0;
        dataMatrix[5][26] = 0;
        dataMatrix[5][27] = 0;

        // Curumin
        dataMatrix[6][0] = 0;
        dataMatrix[6][1] = 0;
        dataMatrix[6][2] = 0;
        dataMatrix[6][3] = 0;
        dataMatrix[6][4] = 0;
        dataMatrix[6][5] = 1;
        dataMatrix[6][6] = 0;
        dataMatrix[6][7] = 0;
        dataMatrix[6][8] = 0;
        dataMatrix[6][9] = 0;
        dataMatrix[6][10] = 0;
        dataMatrix[6][11] = 0;
        dataMatrix[6][12] = 0;
        dataMatrix[6][13] = 0;
        dataMatrix[6][14] = 0;
        dataMatrix[6][15] = 0;
        dataMatrix[6][16] = 0;
        dataMatrix[6][17] = 0;
        dataMatrix[6][18] = 1;
        dataMatrix[6][19] = 0;
        dataMatrix[6][20] = 1;
        dataMatrix[6][21] = 0;
        dataMatrix[6][22] = 0;
        dataMatrix[6][23] = 0;
        dataMatrix[6][24] = 0;
        dataMatrix[6][25] = 1;
        dataMatrix[6][26] = 0;
        dataMatrix[6][27] = 1;

        // Catechin
        dataMatrix[7][0] = 0;
        dataMatrix[7][1] = 0;
        dataMatrix[7][2] = 0;
        dataMatrix[7][3] = 0;
        dataMatrix[7][4] = 0;
        dataMatrix[7][5] = 0;
        dataMatrix[7][6] = 0;
        dataMatrix[7][7] = 0;
        dataMatrix[7][8] = 0;
        dataMatrix[7][9] = 0;
        dataMatrix[7][10] = 1;
        dataMatrix[7][11] = 0;
        dataMatrix[7][12] = 0;
        dataMatrix[7][13] = 0;
        dataMatrix[7][14] = 0;
        dataMatrix[7][15] = 0;
        dataMatrix[7][16] = 0;
        dataMatrix[7][17] = 1;
        dataMatrix[7][18] = 1;
        dataMatrix[7][19] = 0;
        dataMatrix[7][20] = 0;
        dataMatrix[7][21] = 0;
        dataMatrix[7][22] = 0;
        dataMatrix[7][23] = 0;
        dataMatrix[7][24] = 0;
        dataMatrix[7][25] = 0;
        dataMatrix[7][26] = 0;
        dataMatrix[7][27] = 1;

        // Bittersweet
        dataMatrix[8][0] = 1;
        dataMatrix[8][1] = 0;
        dataMatrix[8][2] = 1;
        dataMatrix[8][3] = 1;
        dataMatrix[8][4] = 0;
        dataMatrix[8][5] = 1;
        dataMatrix[8][6] = 0;
        dataMatrix[8][7] = 0;
        dataMatrix[8][8] = 1;
        dataMatrix[8][9] = 0;
        dataMatrix[8][10] = 0;
        dataMatrix[8][11] = 1;
        dataMatrix[8][12] = 0;
        dataMatrix[8][13] = 0;
        dataMatrix[8][14] = 0;
        dataMatrix[8][15] = 0;
        dataMatrix[8][16] = 0;
        dataMatrix[8][17] = 1;
        dataMatrix[8][18] = 0;
        dataMatrix[8][19] = 0;
        dataMatrix[8][20] = 0;
        dataMatrix[8][21] = 0;
        dataMatrix[8][22] = 1;
        dataMatrix[8][23] = 1;
        dataMatrix[8][24] = 0;
        dataMatrix[8][25] = 0;
        dataMatrix[8][26] = 0;
        dataMatrix[8][27] = 0;

        // Variamycin
        dataMatrix[9][0] = 0;
        dataMatrix[9][1] = 0;
        dataMatrix[9][2] = 0;
        dataMatrix[9][3] = 1;
        dataMatrix[9][4] = 0;
        dataMatrix[9][5] = 1;
        dataMatrix[9][6] = 0;
        dataMatrix[9][7] = 0;
        dataMatrix[9][8] = 0;
        dataMatrix[9][9] = 1;
        dataMatrix[9][10] = 0;
        dataMatrix[9][11] = 0;
        dataMatrix[9][12] = 0;
        dataMatrix[9][13] = 0;
        dataMatrix[9][14] = 1;
        dataMatrix[9][15] = 0;
        dataMatrix[9][16] = 1;
        dataMatrix[9][17] = 1;
        dataMatrix[9][18] = 1;
        dataMatrix[9][19] = 0;
        dataMatrix[9][20] = 0;
        dataMatrix[9][21] = 0;
        dataMatrix[9][22] = 0;
        dataMatrix[9][23] = 0;
        dataMatrix[9][24] = 0;
        dataMatrix[9][25] = 0;
        dataMatrix[9][26] = 1;
        dataMatrix[9][27] = 1;



/*
        float[][] dataMatrix = new float[4][4];

        dataMatrix[0][0] = 1;
        dataMatrix[0][1] = 0;
        dataMatrix[0][2] = 0;
        dataMatrix[0][3] = 1;
        //
        dataMatrix[1][0] = 1;
        dataMatrix[1][1] = 0;
        dataMatrix[1][2] = 0;
        dataMatrix[1][3] = 0;
        //
        dataMatrix[2][0] = 1;
        dataMatrix[2][1] = 1;
        dataMatrix[2][2] = 1;
        dataMatrix[2][3] = 1;
        //
        dataMatrix[3][0] = 0;
        dataMatrix[3][1] = 0;
        dataMatrix[3][2] = 1;
        dataMatrix[3][3] = 1;
        */




/*

       // ART2aFloatClustering d = new ART2aFloatClustering("Fingerprints4096.txt",100, ',');
       // Assertions.assertEquals(1,1);
        FileUtil.createLoggingFile();
        float [] [] deneme =   FileUtil.importDataMatrixFromFile("Count.txt", ',');
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(9); // number of tasks
        List<ART2aClusteringTask> tmpClusteringTask = new LinkedList<>();
        for(float tmpVigilanceParameter = 0.1f; tmpVigilanceParameter < 1.0f; tmpVigilanceParameter += 0.1f) {
           ART2aClusteringTask tmpART2aFloatClusteringTask = new ART2aClusteringTask(tmpVigilanceParameter, dataMatrix, 10, true);
           tmpClusteringTask.add(tmpART2aFloatClusteringTask);
        }
        PrintWriter tmpProcessLogWriter = FileUtil.createProcessLogFile();
        PrintWriter tmpResultLogWriter = FileUtil.createResultLogFile();
        List<Future<IART2aClusteringResult>> tmpFuturesList;
        IART2aClusteringResult tmpClusteringResult;
        tmpFuturesList = tmpExecutorService.invokeAll(tmpClusteringTask);
        for(Future<IART2aClusteringResult> tmpFuture : tmpFuturesList) {
            tmpClusteringResult = tmpFuture.get();
            // tmpClusteringResult.getClusterIndices(6);
            //tmpClusteringResult.getAngleBetweenClusters(0,1);
            tmpClusteringResult.getClusterRepresentatives(0);
            tmpClusteringResult.getProcessLog();
            tmpClusteringResult.getResultLog();
            for (String tmpProcessLog : tmpClusteringResult.getProcessLog()) {
                tmpProcessLogWriter.println(tmpProcessLog);
            }
            for (String tmpResultLog : tmpClusteringResult.getResultLog()) {
                tmpResultLogWriter.println(tmpResultLog);
            }

        }






            //test
           // Assertions.assertEquals(true, tmpClusteringResult.getClusteringStatus());
            Assertions.assertEquals(true, true);

        tmpProcessLogWriter.flush();
        tmpProcessLogWriter.close();
        tmpResultLogWriter.flush();
        tmpResultLogWriter.close();
        tmpExecutorService.shutdown();


 */












       ART2aFloatClustering de = new ART2aFloatClustering(dataMatrix,10, 0.1f,0.99f,0.01f);
       IART2aClusteringResult resu =  de.startClustering(0.1f, false);
       //resu.getAngleBetweenClusters(3,2);
        System.out.println(java.util.Arrays.toString(resu.getClusterIndices(4))+ "---indices");
        resu.getClusterRepresentatives(4);
        float a = 0.34768572f;
        float b = 0.35897768f;
        float c = a+b;
        System.out.println(c);













    }
    //</editor-fold>
    //
}
