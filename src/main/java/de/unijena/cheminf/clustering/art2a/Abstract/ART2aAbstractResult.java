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

package de.unijena.cheminf.clustering.art2a.Abstract;

import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClusteringResult;

import de.unijena.cheminf.clustering.art2a.Util.FileUtil;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Abstract class
 *
 * @param <T> generic parameter
 */
public abstract class ART2aAbstractResult<T> implements IART2aClusteringResult {
    //<editor-fold desc="Private class variables" defaultstate="collapsed">
    /**
     * Represents the cluster assignment of each input vector.
     */
    private final int[] clusterView;
    /**
     * Queue for clustering result (process)
     */
    private ConcurrentLinkedQueue<String> processLog;
    /**
     * Queue for clustering result
     */
    private  ConcurrentLinkedQueue<String> resultLog;
    //</editor-fold>
    //
    //<editor-fold desc="private final variables" defaultstate="collapsed">
    /**
     * The vigilance parameter is between 0 and 1. The parameter influences the type of clustering.
     * A vigilance parameter close to 0 leads to a coarse clustering (few clusters) and a vigilance
     * parameter close to 1, on the other hand, leads to a fine clustering (many clusters).
     */
    private float vigilanceParameter;
    /**
     * Final number of epochs the system needed to converge.
     */
    private final int numberOfEpochs;
    /**
     * Final number of clusters detected after successful completion of clustering.
     */
    private final int numberOfDetectedClusters;
    //</editor-fold>
    //
    //<editor-fold desc="Constructor" defaultstate="collapsed">
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusterView array for cluster assignment of each input vector.
     */
    public ART2aAbstractResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters, int[] aClusterView) {
        this.vigilanceParameter = aVigilanceParameter;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.clusterView = aClusterView;
    }
    //
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param processLog clustering result (process) queue.
     * @param resultLog clustering result queue.
     * @param aClusterView array for cluster assignment of each input vector.
     */
    public ART2aAbstractResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters,ConcurrentLinkedQueue<String> processLog, ConcurrentLinkedQueue<String>resultLog, int[] aClusterView) {
        this.clusterView = aClusterView;
        this.vigilanceParameter = aVigilanceParameter;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.processLog = processLog;
        this.resultLog = resultLog;
    }
    //</editor-fold>
    //
    //<editor-fold desc="Overriden public properties" defaultstate="collapsed">
    /**
     *
     * @return the vigilance parameter
     */
    @Override
    public float getVigilanceParameter() {
        return this.vigilanceParameter;
    }
    //
    /**
     *
     * @return return number of epochs.
     */
    @Override
    public int getNumberOfEpochs() {
        return this.numberOfEpochs;
    }
    //
    /**
     *
     * @return number of detected clusters.
     */
    @Override
    public int getNumberOfDetectedClusters() {
        return this.numberOfDetectedClusters;
    }
    //</editor-fold>
    //
    //<editor-fold desc="Overriden public methods" defaultstate="collapsed">
    /**
     * Returns cluster indices for a given cluster.
     *
     * @param aClusterNumber cluster number for which all related inputs are to be returned.
     * @return int[] with all associated input vectors assigned to the specified cluster.
     * @throws IllegalArgumentException is thrown if the given cluster number does not exist.
     */
    @Override
    public int[] getClusterIndices(int aClusterNumber) throws IllegalArgumentException {
        if (aClusterNumber > this.numberOfDetectedClusters) {
            throw new IllegalArgumentException("The specified cluster number does not exist and exceeds the maximum number of clusters.");
        } else {
            System.out.println(this.getClusterMembers(this.clusterView).get(aClusterNumber) + "----Array size");
            int[] tmpIndicesInCluster = new int[this.getClusterMembers(this.clusterView).get(aClusterNumber)];
            int i = 0;
            int in = 0;
            for (int tmpClusterMember : this.clusterView) {
                if (tmpClusterMember == aClusterNumber) {
                    tmpIndicesInCluster[in] = i;
                    in++;
                }
                i++;
            }
            System.out.println(java.util.Arrays.toString(tmpIndicesInCluster));
            return tmpIndicesInCluster;
        }
    }
    //
    /**
     * Clustering results are additionally written to text files.
     *
     * @param aPathName to save the text files.
     */
    @Override
    public void getProcessLog(String aPathName) {
        PrintWriter tmpProcessPrintWriter = FileUtil.createProcessLogFile(aPathName);
        for (String tmpProcessLog : this.processLog) {
            System.out.println(tmpProcessLog);
           tmpProcessPrintWriter.println(tmpProcessLog);
        }
        tmpProcessPrintWriter.flush();
        tmpProcessPrintWriter.close();
        PrintWriter tmpResultPrintWriter = FileUtil.createResultLogFile(aPathName);
        for (String tmpProcessLog : this.resultLog) {
            System.out.println(tmpProcessLog);
            tmpResultPrintWriter.println(tmpProcessLog);
        }
        tmpResultPrintWriter.flush();
        tmpResultPrintWriter.close();
    }
    //</editor-fold>
    //
    //<editor-fold desc="Abstract methods" defaultstate="collapsed">
    /**
     * Abstract method: calculates the cluster representative. This means that the input that is most
     * similar to the cluster vector is determined.
     *
     * @param aClusterNumber Cluster number for which the representative is to be calculated.
     * @return int input indices
     */
    public abstract int getClusterRepresentatives(int aClusterNumber);
    //
    /**
     * Abstract method: calculates the angle between two clusters.
     *
     * @param aFirstCluster
     * @param aSecondCluster
     * @return generic angle double or float.
     * @throws IllegalArgumentException if the given parameters are invalid.
     */
    public abstract T getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException;
    //</editor-fold>
    //
    //<editor-fold desc="Private methods" defaultstate="collapsed">
    private HashMap<Integer, Integer> getClusterMembers(int[] aClusterView) {
        HashMap<Integer, Integer> tmpClusterToMembersMap = new HashMap<>(this.getNumberOfDetectedClusters());
        int i = 1;
        for(int tmpClusterMembers : aClusterView) {
            if (tmpClusterMembers == -1) {
                continue;
            }
            if(!tmpClusterToMembersMap.containsKey(tmpClusterMembers)) {
                tmpClusterToMembersMap.put(tmpClusterMembers, i);
            } else {
                tmpClusterToMembersMap.put(tmpClusterMembers, tmpClusterToMembersMap.get(tmpClusterMembers) + 1);
            }
        }
        System.out.println(tmpClusterToMembersMap +"------cluster members");
        return tmpClusterToMembersMap;
    }
    //</editor-fold>
}
