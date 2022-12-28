package de.unijena.cheminf.clustering.art2a;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("hallo");
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
        ART2aClustering clsuter = new ART2aClustering(dataMatrix,1);
        System.out.println(java.util.Arrays.toString(dataMatrix[4]) + "---input 4");
        System.out.println(java.util.Arrays.toString(dataMatrix[3])+ "---input 3");
        System.out.println(java.util.Arrays.toString(dataMatrix[0])+ "---input 0");
        System.out.println("\n");
        clsuter.call();

         */
        ExecutorService executor = Executors.newFixedThreadPool(9);
           ART2aClustering task = new ART2aClustering( dataMatrix, 10);
            Future<Void> future = executor.submit(task);
           executor.shutdown();





    }
}
