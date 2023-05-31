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

package de.unijena.cheminf.clustering.art2a.Interfaces;

import java.io.PrintWriter;

/**
 * Interface for implementing clustering result classes.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public interface IART2aClusteringResult {
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns the vigilance parameter of the clustering algorithm.
     *
     * @return float vigilance parameter
     */
    float getVigilanceParameter();
    //
    /**
     * Returns the number of detected clusters.
     *
     * @return int detected cluster number
     */
    int getNumberOfDetectedClusters();
    //
    /**
     * Returns the number of epochs.
     *
     * @return int epoch number
     */
    int getNumberOfEpochs();
    //
    /**
     * Returns the convergence status after clustering. The convergence status is false, if the specified
     * number of maximum epochs is not sufficient to achieve a convergence of the system.
     *
     * @return boolean true or false
     */
    boolean getConvergenceStatus();
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns the input indices assigned to the given cluster.
     *
     * @param aClusterNumber given cluster number
     * @return array with the input indices for a given cluster
     * @throws IllegalArgumentException is thrown if the given cluster does not exist.
     */
    int[] getClusterIndices(int aClusterNumber) throws IllegalArgumentException;
    //
    /**
     * The result of the clustering is additionally recorded in 2 text files. One of these files is a
     * very detailed representation of the results (clustering result file), while in the other only the
     * most important results are summarized (clustering process file).
     *
     */
    void getClusteringResultsInTextFile(PrintWriter aClusteringResultWriter, PrintWriter aClusteringProcessWriter);
    // </editor-fold>
}
