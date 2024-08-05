/*
 * ART2a Clustering for Java
 * Copyright (C) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
 *
 * Source code is available at <https://github.com/JonasSchaub/ART2a-Clustering-for-Java>
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

package de.unijena.cheminf.clustering.art2a.interfaces.euclideanClusteringInterfaces;

import java.io.Writer;

/**
 * Interface for implementing clustering result classes.
 *
 * @param <T> generic parameter. This parameter is either a Double or a Float.
 *           The type of the method @code {@link #getDistanceBetweenClusters(int, int)}
 *           is calculated either as a float or as a double, depending on the clustering precision option.
 *
 * @author Zeynep Dagtekin, inspired by Betuel Sevindik's interface.
 * @version 1.0.0.0
 */

public interface IArt2aEuclideanClusteringResult<T> {
    /**
     * Returns the vigilance parameter for the clustering algorithm.
     *
     * @return vigilance parameter
     */
    T getVigilanceParameter();
    /**
     * Returns the number of Epochs.
     *
     * @return int epoch number
     */
    int getNumberOfEpochs();
    /**
     * Returns the number of detected  classes.
     *
     * @return int detected cluster number
     */
    int getNumberOfDetectedClusters();
    /**
     * Returns the input indices assigned to the given cluster.
     *
     * @param aClusterNumber the given number of clusters
     * @return array with the input indices for a given cluster.
     * @throws IllegalArgumentException is thrown if the given cluster does not exist
     */
    int[] getClusterIndices(int aClusterNumber) throws IllegalArgumentException;
    /**
     * Calculates the cluster representatives. This means that the input that is the most similar to the cluster vector
     * is determined.
     *
     * @param aClusterNumber Cluster number to calculate the representatives with.
     * @return int input indices of the representative input in the cluster.
     * @throws IllegalArgumentException is thrown if the given cluster number is invalid.
     */
    int getClusterRepresentatives(int aClusterNumber) throws IllegalArgumentException;
    /**
     * The result of the clustering is additionally exported in two text files. One of these files is a
     * very detailed representation of the results (clustering process file), while in the other only the
     * most important results are summarized (clustering result file).
     * <u>IMPORTANT: </u> In order to additionally export the clustering results into text files,
     * the folder must be created first.
     * This requires the method call setUpClusteringResultTextFilePrinter(String aPathName, Class)
     * or user own Writer and text files. This method call is optional, the folder can also be created by the user.
     *
     * @see de.unijena.cheminf.clustering.art2a.util.FileUtil#setUpClusteringResultTextFilePrinters(String, Class)
     *
     * @param aClusteringProcessWriter clustering result (process) writer
     * @param aClusteringResultWriter clustering result writer
     * @throws NullPointerException is thrown, if the Writers are null.
     *
     */
    void exportClusteringResultsToTextFiles(Writer aClusteringResultWriter, Writer aClusteringProcessWriter)
            throws NullPointerException;
    /**
     * Calculates the Euclidean distance between two clusters. The alternative calculation to ART-2a based on angles.
     * The normalization steps are removed so the length of the vectors can be used for the distance.
     *
     * @param aFirstCluster first cluster
     * @param aSecondCluster second cluster
     * @return generic angle double or float.
     * @throws IllegalArgumentException if the given parameters are invalid.
     *
     */
    T getDistanceBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException;
}
