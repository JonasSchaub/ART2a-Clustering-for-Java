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

import de.unijena.cheminf.clustering.art2a.Logger.FileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
     */
    @Test
    public void startArt2aClusteringTest() throws IOException, InterruptedException, ExecutionException {
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(9); // number of tasks
        List<ART2aClusteringTask> tmpClusteringTask = new LinkedList<>();
        for(float tmpVigilanceParameter = 0.1f; tmpVigilanceParameter < 1.0f; tmpVigilanceParameter += 0.1f) {
           ART2aClusteringTask tmpART2aFloatClusteringTask = new ART2aClusteringTask(tmpVigilanceParameter, "src/test/resources/de/unijena/cheminf/clustering/art2a/Fingerprints.txt", 10000, ',');
           tmpClusteringTask.add(tmpART2aFloatClusteringTask);
        }
        PrintWriter tmpProcessLogWriter = FileUtil.createProcessLogFile();
        PrintWriter tmpResultLogWriter = FileUtil.createResultLogFile();
        List<Future<ART2aFloatClustering>> tmpFuturesList;
        ART2aFloatClustering tmpClusteringResult;
        tmpFuturesList = tmpExecutorService.invokeAll(tmpClusteringTask);
        for(Future<ART2aFloatClustering> tmpFuture : tmpFuturesList) {
            tmpClusteringResult = tmpFuture.get();
            tmpClusteringResult.getClusteringProcessLog();
            tmpClusteringResult.getClusteringResultLog();
            HashMap<Integer, HashMap<Integer,Integer>> tmpClusteringResultVigilanceParameterToNumberOfEpochsAndNumberOfClusters =
                    tmpClusteringResult.getVigilanceParameterToNumberOfEpochsAndNumberOfClusters();
            // Illustration the clustering result
            System.out.println(tmpClusteringResultVigilanceParameterToNumberOfEpochsAndNumberOfClusters);
            for (String tmpProcessLog : tmpClusteringResult.getClusteringProcessLog()) {
                tmpProcessLogWriter.println(tmpProcessLog);
            }
            for(String tmpResultLog : tmpClusteringResult.getClusteringResultLog()) {
                tmpResultLogWriter.println(tmpResultLog);
            }
            //test
            Assertions.assertEquals(true, tmpClusteringResult.getClusteringStatus());
        }
        tmpProcessLogWriter.flush();
        tmpProcessLogWriter.close();
        tmpResultLogWriter.flush();
        tmpResultLogWriter.close();
        tmpExecutorService.shutdown();
    }
    //</editor-fold>
    //
}
