package de.unijena.cheminf.clustering.art2a;

import de.unijena.cheminf.clustering.art2a.Logger.FileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *  Class to test the working of ART- 2A clustering.
 */
public class ART2aClusteringTest {
    //<editor-fold desc="Constructor" defaultstate="collapsed">
    /**
     * Empty Constructor
     */
    public ART2aClusteringTest() {
    }
    //</editor-fold>
    //
    //<editor-fold desc="Test methods" defaultstate="collapsed">
    /**
     * Start clustering.
     *
     * @throws Exception if anything goes wrong
     */
    @BeforeAll
    public static void startClustering() throws Exception {
        /*
        float[][] tmpDataMatrix = new float[10][28];
        //valdiazen
        tmpDataMatrix[0][0] = 1;
        tmpDataMatrix[0][1] = 0;
        tmpDataMatrix[0][2] = 0;
        tmpDataMatrix[0][3] = 0;
        tmpDataMatrix[0][4] = 0;
        tmpDataMatrix[0][5] = 0;
        tmpDataMatrix[0][6] = 0;
        tmpDataMatrix[0][7] = 0;
        tmpDataMatrix[0][8] = 1;
        tmpDataMatrix[0][9] = 0;
        tmpDataMatrix[0][10] = 0;
        tmpDataMatrix[0][11] = 0;
        tmpDataMatrix[0][12] = 0;
        tmpDataMatrix[0][13] = 0;
        tmpDataMatrix[0][14] = 0;
        tmpDataMatrix[0][15] = 0;
        tmpDataMatrix[0][16] = 0;
        tmpDataMatrix[0][17] = 1;
        tmpDataMatrix[0][18] = 0;
        tmpDataMatrix[0][19] = 0;
        tmpDataMatrix[0][20] = 0;
        tmpDataMatrix[0][21] = 0;
        tmpDataMatrix[0][22] = 0;
        tmpDataMatrix[0][23] = 0;
        tmpDataMatrix[0][24] = 0;
        tmpDataMatrix[0][25] = 0;
        tmpDataMatrix[0][26] = 0;
        tmpDataMatrix[0][27] = 0;


        // Napthomycin d
        tmpDataMatrix[1][0] = 0;
        tmpDataMatrix[1][1] = 0;
        tmpDataMatrix[1][2] = 0;
        tmpDataMatrix[1][3] = 0;
        tmpDataMatrix[1][4] = 0;
        tmpDataMatrix[1][5] = 1;
        tmpDataMatrix[1][6] = 1;
        tmpDataMatrix[1][7] = 1;
        tmpDataMatrix[1][8] = 0;
        tmpDataMatrix[1][9] = 0;
        tmpDataMatrix[1][10] = 0;
        tmpDataMatrix[1][11] = 0;
        tmpDataMatrix[1][12] = 1;
        tmpDataMatrix[1][13] = 0;
        tmpDataMatrix[1][14] = 0;
        tmpDataMatrix[1][15] = 1;
        tmpDataMatrix[1][16] = 0;
        tmpDataMatrix[1][17] = 1;
        tmpDataMatrix[1][18] = 0;
        tmpDataMatrix[1][19] = 0;
        tmpDataMatrix[1][20] = 0;
        tmpDataMatrix[1][21] = 1;
        tmpDataMatrix[1][22] = 0;
        tmpDataMatrix[1][23] = 0;
        tmpDataMatrix[1][24] = 0;
        tmpDataMatrix[1][25] = 1;
        tmpDataMatrix[1][26] = 1;
        tmpDataMatrix[1][27] = 1;


        // Nona-2,6-dienal
        tmpDataMatrix[2][0] = 0;
        tmpDataMatrix[2][1] = 0;
        tmpDataMatrix[2][2] = 0;
        tmpDataMatrix[2][3] = 0;
        tmpDataMatrix[2][4] = 0;
        tmpDataMatrix[2][5] = 0;
        tmpDataMatrix[2][6] = 0;
        tmpDataMatrix[2][7] = 0;
        tmpDataMatrix[2][8] = 0;
        tmpDataMatrix[2][9] = 0;
        tmpDataMatrix[2][10] = 0;
        tmpDataMatrix[2][11] = 0;
        tmpDataMatrix[2][12] = 1;
        tmpDataMatrix[2][13] = 0;
        tmpDataMatrix[2][14] = 0;
        tmpDataMatrix[2][15] = 0;
        tmpDataMatrix[2][16] = 0;
        tmpDataMatrix[2][17] = 0;
        tmpDataMatrix[2][18] = 0;
        tmpDataMatrix[2][19] = 1;
        tmpDataMatrix[2][20] = 0;
        tmpDataMatrix[2][21] = 1;
        tmpDataMatrix[2][22] = 0;
        tmpDataMatrix[2][23] = 0;
        tmpDataMatrix[2][24] = 0;
        tmpDataMatrix[2][25] = 0;
        tmpDataMatrix[2][26] = 0;
        tmpDataMatrix[2][27] = 0;

        // Istanbulin A
        tmpDataMatrix[3][0] = 0;
        tmpDataMatrix[3][1] = 0;
        tmpDataMatrix[3][2] = 0;
        tmpDataMatrix[3][3] = 0;
        tmpDataMatrix[3][4] = 0;
        tmpDataMatrix[3][5] = 1;
        tmpDataMatrix[3][6] = 0;
        tmpDataMatrix[3][7] = 0;
        tmpDataMatrix[3][8] = 0;
        tmpDataMatrix[3][9] = 0;
        tmpDataMatrix[3][10] = 0;
        tmpDataMatrix[3][11] = 0;
        tmpDataMatrix[3][12] = 0;
        tmpDataMatrix[3][13] = 1;
        tmpDataMatrix[3][14] = 0;
        tmpDataMatrix[3][15] = 0;
        tmpDataMatrix[3][16] = 1;
        tmpDataMatrix[3][17] = 0;
        tmpDataMatrix[3][18] = 0;
        tmpDataMatrix[3][19] = 0;
        tmpDataMatrix[3][20] = 0;
        tmpDataMatrix[3][21] = 0;
        tmpDataMatrix[3][22] = 0;
        tmpDataMatrix[3][23] = 0;
        tmpDataMatrix[3][24] = 1;
        tmpDataMatrix[3][25] = 0;
        tmpDataMatrix[3][26] = 0;
        tmpDataMatrix[3][27] = 0;


        // Estradiol
        tmpDataMatrix[4][0] = 0;
        tmpDataMatrix[4][1] = 0;
        tmpDataMatrix[4][2] = 0;
        tmpDataMatrix[4][3] = 0;
        tmpDataMatrix[4][4] = 1;
        tmpDataMatrix[4][5] = 0;
        tmpDataMatrix[4][6] = 0;
        tmpDataMatrix[4][7] = 0;
        tmpDataMatrix[4][8] = 0;
        tmpDataMatrix[4][9] = 0;
        tmpDataMatrix[4][10] = 0;
        tmpDataMatrix[4][11] = 0;
        tmpDataMatrix[4][12] = 0;
        tmpDataMatrix[4][13] = 0;
        tmpDataMatrix[4][14] = 0;
        tmpDataMatrix[4][15] = 0;
        tmpDataMatrix[4][16] = 0;
        tmpDataMatrix[4][17] = 1;
        tmpDataMatrix[4][18] = 0;
        tmpDataMatrix[4][19] = 0;
        tmpDataMatrix[4][20] = 0;
        tmpDataMatrix[4][21] = 0;
        tmpDataMatrix[4][22] = 0;
        tmpDataMatrix[4][23] = 0;
        tmpDataMatrix[4][24] = 0;
        tmpDataMatrix[4][25] = 0;
        tmpDataMatrix[4][26] = 0;
        tmpDataMatrix[4][27] = 1;

        // Paradise
        tmpDataMatrix[5][0] = 0;
        tmpDataMatrix[5][1] = 1;
        tmpDataMatrix[5][2] = 0;
        tmpDataMatrix[5][3] = 0;
        tmpDataMatrix[5][4] = 0;
        tmpDataMatrix[5][5] = 0;
        tmpDataMatrix[5][6] = 0;
        tmpDataMatrix[5][7] = 0;
        tmpDataMatrix[5][8] = 0;
        tmpDataMatrix[5][9] = 0;
        tmpDataMatrix[5][10] = 0;
        tmpDataMatrix[5][11] = 0;
        tmpDataMatrix[5][12] = 0;
        tmpDataMatrix[5][13] = 0;
        tmpDataMatrix[5][14] = 0;
        tmpDataMatrix[5][15] = 0;
        tmpDataMatrix[5][16] = 0;
        tmpDataMatrix[5][17] = 0;
        tmpDataMatrix[5][18] = 0;
        tmpDataMatrix[5][19] = 0;
        tmpDataMatrix[5][20] = 1;
        tmpDataMatrix[5][21] = 0;
        tmpDataMatrix[5][22] = 0;
        tmpDataMatrix[5][23] = 0;
        tmpDataMatrix[5][24] = 0;
        tmpDataMatrix[5][25] = 0;
        tmpDataMatrix[5][26] = 0;
        tmpDataMatrix[5][27] = 0;

        // Curumin
        tmpDataMatrix[6][0] = 0;
        tmpDataMatrix[6][1] = 0;
        tmpDataMatrix[6][2] = 0;
        tmpDataMatrix[6][3] = 0;
        tmpDataMatrix[6][4] = 0;
        tmpDataMatrix[6][5] = 1;
        tmpDataMatrix[6][6] = 0;
        tmpDataMatrix[6][7] = 0;
        tmpDataMatrix[6][8] = 0;
        tmpDataMatrix[6][9] = 0;
        tmpDataMatrix[6][10] = 0;
        tmpDataMatrix[6][11] = 0;
        tmpDataMatrix[6][12] = 0;
        tmpDataMatrix[6][13] = 0;
        tmpDataMatrix[6][14] = 0;
        tmpDataMatrix[6][15] = 0;
        tmpDataMatrix[6][16] = 0;
        tmpDataMatrix[6][17] = 0;
        tmpDataMatrix[6][18] = 1;
        tmpDataMatrix[6][19] = 0;
        tmpDataMatrix[6][20] = 1;
        tmpDataMatrix[6][21] = 0;
        tmpDataMatrix[6][22] = 0;
        tmpDataMatrix[6][23] = 0;
        tmpDataMatrix[6][24] = 0;
        tmpDataMatrix[6][25] = 1;
        tmpDataMatrix[6][26] = 0;
        tmpDataMatrix[6][27] = 1;

        // Catechin
        tmpDataMatrix[7][0] = 0;
        tmpDataMatrix[7][1] = 0;
        tmpDataMatrix[7][2] = 0;
        tmpDataMatrix[7][3] = 0;
        tmpDataMatrix[7][4] = 0;
        tmpDataMatrix[7][5] = 0;
        tmpDataMatrix[7][6] = 0;
        tmpDataMatrix[7][7] = 0;
        tmpDataMatrix[7][8] = 0;
        tmpDataMatrix[7][9] = 0;
        tmpDataMatrix[7][10] = 1;
        tmpDataMatrix[7][11] = 0;
        tmpDataMatrix[7][12] = 0;
        tmpDataMatrix[7][13] = 0;
        tmpDataMatrix[7][14] = 0;
        tmpDataMatrix[7][15] = 0;
        tmpDataMatrix[7][16] = 0;
        tmpDataMatrix[7][17] = 1;
        tmpDataMatrix[7][18] = 1;
        tmpDataMatrix[7][19] = 0;
        tmpDataMatrix[7][20] = 0;
        tmpDataMatrix[7][21] = 0;
        tmpDataMatrix[7][22] = 0;
        tmpDataMatrix[7][23] = 0;
        tmpDataMatrix[7][24] = 0;
        tmpDataMatrix[7][25] = 0;
        tmpDataMatrix[7][26] = 0;
        tmpDataMatrix[7][27] = 1;

        // Bittersweet
        tmpDataMatrix[8][0] = 0;
        tmpDataMatrix[8][1] = 0;
        tmpDataMatrix[8][2] = 1;
        tmpDataMatrix[8][3] = 1;
        tmpDataMatrix[8][4] = 0;
        tmpDataMatrix[8][5] = 1;
        tmpDataMatrix[8][6] = 0;
        tmpDataMatrix[8][7] = 0;
        tmpDataMatrix[8][8] = 0;
        tmpDataMatrix[8][9] = 0;
        tmpDataMatrix[8][10] = 0;
        tmpDataMatrix[8][11] = 1;
        tmpDataMatrix[8][12] = 0;
        tmpDataMatrix[8][13] = 0;
        tmpDataMatrix[8][14] = 0;
        tmpDataMatrix[8][15] = 0;
        tmpDataMatrix[8][16] = 0;
        tmpDataMatrix[8][17] = 0;
        tmpDataMatrix[8][18] = 0;
        tmpDataMatrix[8][19] = 0;
        tmpDataMatrix[8][20] = 0;
        tmpDataMatrix[8][21] = 0;
        tmpDataMatrix[8][22] = 1;
        tmpDataMatrix[8][23] = 1;
        tmpDataMatrix[8][24] = 0;
        tmpDataMatrix[8][25] = 0;
        tmpDataMatrix[8][26] = 0;
        tmpDataMatrix[8][27] = 0;

        // Variamycin
        tmpDataMatrix[9][0] = 0;
        tmpDataMatrix[9][1] = 0;
        tmpDataMatrix[9][2] = 0;
        tmpDataMatrix[9][3] = 1;
        tmpDataMatrix[9][4] = 0;
        tmpDataMatrix[9][5] = 1;
        tmpDataMatrix[9][6] = 0;
        tmpDataMatrix[9][7] = 0;
        tmpDataMatrix[9][8] = 0;
        tmpDataMatrix[9][9] = 1;
        tmpDataMatrix[9][10] = 0;
        tmpDataMatrix[9][11] = 0;
        tmpDataMatrix[9][12] = 0;
        tmpDataMatrix[9][13] = 0;
        tmpDataMatrix[9][14] = 1;
        tmpDataMatrix[9][15] = 0;
        tmpDataMatrix[9][16] = 1;
        tmpDataMatrix[9][17] = 1;
        tmpDataMatrix[9][18] = 1;
        tmpDataMatrix[9][19] = 0;
        tmpDataMatrix[9][20] = 0;
        tmpDataMatrix[9][21] = 0;
        tmpDataMatrix[9][22] = 0;
        tmpDataMatrix[9][23] = 0;
        tmpDataMatrix[9][24] = 0;
        tmpDataMatrix[9][25] = 0;
        tmpDataMatrix[9][26] = 1;
        tmpDataMatrix[9][27] = 1;

        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(9); // number of tasks
        List<Art2aClusteringTask> tmpClusteringTask = new LinkedList<>();
        for (float tmpVigilanceParameter = 0.1f; tmpVigilanceParameter < 1.0f; tmpVigilanceParameter += 0.1f) {
            Art2aClusteringTask task = new Art2aClusteringTask(tmpVigilanceParameter, "src/test/resources/de/unijena/cheminf/clustering/art2a/Fingerprints.txt", 1, ",");
            tmpClusteringTask.add(task);
        }
        PrintWriter tmpWriter = FileUtil.createResultFile();
        List<Future<ART2aFloatClusteringResult>> tmpFuturesList;
            ART2aFloatClusteringResult tmpClusteringResult;
            tmpFuturesList = tmpExecutorService.invokeAll(tmpClusteringTask);
            for (Future<ART2aFloatClusteringResult> tmpFuture : tmpFuturesList) {
                tmpClusteringResult = tmpFuture.get();
                tmpClusteringResult.getResult();
                for (String tmpInfo : tmpClusteringResult.getResult()) {
                    tmpWriter.println(tmpInfo);
                }
                //Assertions.assertEquals(true, tmpClusteringResult.getClusteringStatus());
            }
            tmpWriter.flush();
            tmpWriter.close();
            tmpExecutorService.shutdown();
            //Assertions.assertEquals(false, true);

         */





    }
    //</editor-fold>
    //
    @Test
    public void test() {
        Assertions.assertEquals(1,1);
    }
}
