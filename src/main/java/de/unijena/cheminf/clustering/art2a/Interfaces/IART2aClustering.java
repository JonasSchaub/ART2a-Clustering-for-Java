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

/**
 * Interface for implementing float and double ART-2a clustering.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public interface IART2aClustering {
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Initialise the cluster matrix. // TODO describe the cluster matrix; what is it...?
     */
    void initializeMatrices();
    //
    /**
     * The input vectors/fingerprints are randomized so that all input vectors can be clustered by random selection. // TODO add the argument, why the inputs have to be randomize!
     * Here, the Fisher-Yates method is used to randomize the inputs.
     *
     * @return an array with vector indices in a random order
     * @author Thomas Kuhn
     *
     */
    int[] randomizeVectorIndices();
    //
    /**
     * Starts an ART-2A clustering algorithm in single machine precision.
     * The clustering process begins by randomly selecting an input vector/fingerprint from the data matrix.
     * After normalizing the first input vector, it is assigned to the first cluster. For all other subsequent
     * input vectors, they also undergo certain normalization steps. If there is sufficient similarity to an
     * existing cluster, they are assigned to that cluster. Otherwise, a new cluster is formed, and the
     * input is added to it. Null vectors are not clustered.
     *
     * @param aVigilanceParameter parameter that influence the number of clusters
     * @param aAddClusteringResultFileAdditionally if the parameter == true, then all information
     *                                             about the clustering is written out
     *                                             once in detail and once roughly additionally in text files.
     *                                             If the parameter == false, the information is not written
     *                                             out in text files.
     * @return IART2aClusteringResult // TODO maybe add more information?
     * @throws RuntimeException is thrown if the system cannot converge within the specified number of epochs.
     */
    IART2aClusteringResult startClustering(float aVigilanceParameter, boolean aAddClusteringResultFileAdditionally) throws RuntimeException; // early Exception
    //
    /**
     * At the end of each epoch, it is checked whether the system has converged or not. If the system has not
     * converged, a new epoch is performed, otherwise the clustering is completed successfully.
     *
     * @param aNumberOfDetectedClasses number of detected clusters per epoch.
     * @param aConvergenceEpoch current epochs number.
     * @return boolean true is returned if the system has converged.
     * False is returned if the system has not converged to the epoch.
     * @throws RuntimeException is thrown if the network does not converge within the
     * specified maximum number of epochs.
     */
    boolean checkConvergence(int aNumberOfDetectedClasses, int aConvergenceEpoch);
    // </editor-fold>
}
