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

import de.unijena.cheminf.clustering.art2a.results.Art2aDoubleClusteringResult;
import de.unijena.cheminf.clustering.art2a.util.FileUtil;
import org.junit.jupiter.api.Test;

/**
 * Test class for double clustering.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class ART2aDoubleClusteringTaskTest {
    //<editor-fold desc="Test methods" defaultstate="collapsed">
    /**
     * Test method
     * TODO add test methods
     */
    @Test
    public void startArt2aClusteringTest() throws Exception {



        double tmpImportBitFingerprints [][] = FileUtil.importDoubleDataMatrixFromTextFile("src/test/resources/de/unijena/cheminf/clustering/art2a/Fingerprints.txt",',');
        /*
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(9); // number of tasks
        List<Art2aClusteringTask> tmpClusteringTask = new LinkedList<>();
        for (float tmpVigilanceParameter = 0.1f; tmpVigilanceParameter < 1.0f; tmpVigilanceParameter += 0.1f) {
            Art2aClusteringTask tmpART2aFloatClusteringTask = new Art2aClusteringTask(tmpVigilanceParameter, tmpImportBitFingerprints, 10, false);
            tmpClusteringTask.add(tmpART2aFloatClusteringTask);
        }
        List<Future<IArt2aClusteringResult>> tmpFuturesList;
        IArt2aClusteringResult tmpClusteringResult;
        tmpFuturesList = tmpExecutorService.invokeAll(tmpClusteringTask);
        System.out.println("\nCLUSTERING RESULTS\n");
        for (Future<IArt2aClusteringResult> tmpFuture : tmpFuturesList) {
            try {
                tmpClusteringResult = tmpFuture.get();
                System.out.println("vigilance parameter: " + tmpClusteringResult.getVigilanceParameter() );
                System.out.println("number of epochs: " + tmpClusteringResult.getNumberOfEpochs());
                System.out.println("cluster indices in cluster 0: " + java.util.Arrays.toString(tmpClusteringResult.getClusterIndices(0)));
                System.out.println("####################################");
            } catch (NullPointerException anException) {
                System.out.println("naaaaaaaaa");
               System.out.println(anException + "----no clustering result, because convergence failed for:");
            }
        }
        tmpExecutorService.shutdown();
        Assertions.assertEquals(true, true);

         */

        Art2aDoubleClustering de = new Art2aDoubleClustering(tmpImportBitFingerprints,100, 0.2f,0.99,0.01);
        Art2aDoubleClusteringResult resu =  de.startClustering( false);
        System.out.println(resu.getNumberOfDetectedClusters());
        System.out.println(java.util.Arrays.toString(resu.getClusterIndices(0)));
        System.out.println(resu.calculateAngleBetweenClusters(1,10));

    }
    //</editor-fold>
    //
}

