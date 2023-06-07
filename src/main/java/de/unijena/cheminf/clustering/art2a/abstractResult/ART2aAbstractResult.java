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

package de.unijena.cheminf.clustering.art2a.abstractResult;

import de.unijena.cheminf.clustering.art2a.interfaces.IART2aClusteringResult;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class
 *
 * @param <T> generic parameter
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public abstract class ART2aAbstractResult<T> implements IART2aClusteringResult {
    //<editor-fold desc="Private class variables" defaultstate="collapsed">
    /**
     * Represents the cluster assignment of each input vector. For example clusterView[4] = 0 means that
     * input vector 5 cluster 0 has been assigned.
     */
    private final int[] clusterView;
    /**
     * Queue for clustering result (process)
     */
    private ConcurrentLinkedQueue<String> clusteringProcess;
    /**
     * Queue for clustering result
     */
    private  ConcurrentLinkedQueue<String> clusteringResult;
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
    /**
     * Initial capacity value for maps
     */
    private final double INITIAL_CAPACITY_VALUE = 1.5;
    /**
     * Convergence status of the clustering. The convergence status is false, if the given maximum number of epochs
     * is not sufficient until the system converges.
     *
     */
    private final boolean convergenceStatus;

    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ART2aAbstractResult.class.getName());

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
    public ART2aAbstractResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters, int[] aClusterView, boolean aConvergenceStatus) {
        if(aNumberOfEpochs <= 0) {
            ART2aAbstractResult.LOGGER.log(Level.SEVERE,"aNumberOfEpochs is invalid.");
            throw new IllegalArgumentException("aNumberOfEpochs is invalid.");
        }
        if(aVigilanceParameter <= 0 || aVigilanceParameter >= 1) {
            ART2aAbstractResult.LOGGER.log(Level.SEVERE,"The vigilance parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0 and less than 1.");
        }
        if(aNumberOfDetectedClusters < 1) {
            ART2aAbstractResult.LOGGER.log(Level.SEVERE, "aNumberOfDetectedClusters is invalid.");
            throw new IllegalArgumentException("aNumberOfDetectedClusters is invalid.");
        }
        this.vigilanceParameter = aVigilanceParameter;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.clusterView = aClusterView;
        this.convergenceStatus = aConvergenceStatus;
    }
    //
    /**
     * Constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aNumberOfEpochs final epoch number.
     * @param aNumberOfDetectedClusters final number of detected clusters.
     * @param aClusteringProcessQueue clustering result (process) queue.
     * @param aClusteringResultQueue clustering result queue.
     * @param aClusterView array for cluster assignment of each input vector.
     */
    public ART2aAbstractResult(float aVigilanceParameter, int aNumberOfEpochs, int aNumberOfDetectedClusters,
                               ConcurrentLinkedQueue<String> aClusteringProcessQueue,
                               ConcurrentLinkedQueue<String> aClusteringResultQueue, int[] aClusterView, boolean aConvergenceStatus) {
        if(aNumberOfEpochs <= 0) {
            ART2aAbstractResult.LOGGER.log(Level.SEVERE,"aNumberOfEpochs is invalid.");
            throw new IllegalArgumentException("aNumberOfEpochs is invalid.");
        }
        if(aVigilanceParameter <= 0 || aVigilanceParameter >= 1) {
            ART2aAbstractResult.LOGGER.log(Level.SEVERE,"The vigilance parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0 and less than 1.");
        }
        if(aNumberOfDetectedClusters < 1) {
            ART2aAbstractResult.LOGGER.log(Level.SEVERE, "aNumberOfDetectedClusters is invalid.");
            throw new IllegalArgumentException("aNumberOfDetectedClusters is invalid.");
        }
        this.clusterView = aClusterView;
        this.vigilanceParameter = aVigilanceParameter;
        this.numberOfEpochs = aNumberOfEpochs;
        this.numberOfDetectedClusters = aNumberOfDetectedClusters;
        this.clusteringProcess = aClusteringProcessQueue;
        this.clusteringResult = aClusteringResultQueue;
        this.convergenceStatus = aConvergenceStatus;
    }
    //</editor-fold>
    //
    //<editor-fold desc="Overriden public properties" defaultstate="collapsed">
    /**
     * {@inheritDoc}
     */
    @Override
    public float getVigilanceParameter() {
        return this.vigilanceParameter;
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfEpochs() {
        return this.numberOfEpochs;
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfDetectedClusters() {
        return this.numberOfDetectedClusters;
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getConvergenceStatus() {
        return this.convergenceStatus;
    }
    //</editor-fold>
    //
    //<editor-fold desc="Overriden public methods" defaultstate="collapsed">
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getClusterIndices(int aClusterNumber) throws IllegalArgumentException {
        if (aClusterNumber >= this.numberOfDetectedClusters) {
            ART2aAbstractResult.LOGGER.log(Level.SEVERE, "The specified cluster number does not exist and exceeds the maximum number of clusters.");
            throw new IllegalArgumentException("The specified cluster number does not exist and exceeds the maximum number of clusters.");
        } else {
            int[] tmpIndicesInCluster = new int[this.getClusterMembers(this.clusterView).get(aClusterNumber)];
            int tmpInputIndices = 0;
            int tmpIterator = 0;
            for (int tmpClusterMember : this.clusterView) {
                if (tmpClusterMember == aClusterNumber) {
                    tmpIndicesInCluster[tmpIterator] = tmpInputIndices;
                    tmpIterator++;
                }
                tmpInputIndices++;
            }
            return tmpIndicesInCluster;
        }
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public void getClusteringResultsInTextFile(PrintWriter aClusteringResultWriter, PrintWriter aClusteringProcessWriter)  {
        if (this.clusteringProcess == null && this.clusteringResult == null) {
            ART2aAbstractResult.LOGGER.log(Level.SEVERE, "The associated argument to allow writing of the clustering results to text files is set to false.\n" +
                    "Please set the argument to true.");
            throw new NullPointerException("The associated argument to allow writing of the clustering results to text files is set to false.\n" +
                    "Please set the argument to true.");
        }
        for (String tmpClusteringResult : this.clusteringResult) {
            aClusteringResultWriter.write(tmpClusteringResult+"\n");
        } for(String tmpClusteringProcess : this.clusteringProcess) {
            aClusteringProcessWriter.write(tmpClusteringProcess+"\n");
        }
    }
    //</editor-fold>
    //
    //<editor-fold desc="Abstract methods" defaultstate="collapsed">
    /**
     * Abstract method: calculates the cluster representative. This means that the input that is most
     * similar to the cluster vector is determined.
     *
     * @param aClusterNumber Cluster number for which the representative is to be calculated.
     * @return int input indices of the representative input in the cluster.
     * @throws IllegalArgumentException is thrown if the given cluster number is invalid.
     */
    public abstract int getClusterRepresentatives(int aClusterNumber) throws IllegalArgumentException;
    //
    /**
     * Abstract method: calculates the angle between two clusters.
     * The angle between the clusters defines the distance between them.
     * Since all vectors are normalized to unit vectors in the first step of clustering
     * and only positive components are allowed, they all lie in the positive quadrant
     * of the unit sphere, so the maximum distance between two clusters can be 90 degrees.
     *
     * @param aFirstCluster first cluster
     * @param aSecondCluster second cluster
     * @return generic angle double or float.
     * @throws IllegalArgumentException if the given parameters are invalid.
     */
    public abstract T getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException;
    //</editor-fold>
    //
    //<editor-fold desc="Private methods" defaultstate="collapsed">
    /**
     * Method to map the cluster number to the number of cluster members.
     *
     * @param aClusterView represents the cluster assignment of each input vector.
     * @return HashMap<Integer, Integer>
     */
    private HashMap<Integer, Integer> getClusterMembers(int[] aClusterView) {
        HashMap<Integer, Integer> tmpClusterToMembersMap = new HashMap<>((int) (this.getNumberOfDetectedClusters() * this.INITIAL_CAPACITY_VALUE));
        for(int tmpClusterMembers : aClusterView) {
            if (tmpClusterMembers == -1) {
                continue;
            }
            if(!tmpClusterToMembersMap.containsKey(tmpClusterMembers)) {
                tmpClusterToMembersMap.put(tmpClusterMembers, 1);
            } else {
                tmpClusterToMembersMap.put(tmpClusterMembers, tmpClusterToMembersMap.get(tmpClusterMembers) + 1);
            }
        }
        return tmpClusterToMembersMap;
    }
    //</editor-fold>
}