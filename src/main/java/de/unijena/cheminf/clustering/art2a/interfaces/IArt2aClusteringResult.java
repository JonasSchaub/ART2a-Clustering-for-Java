/*
 * ART2a Clustering for Java
 * Copyright (C) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
 *
 * Source code is available at <https://github.com/JonasSchaub/ART2a-Clustering-for-Java>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unijena.cheminf.clustering.art2a.interfaces;

import java.io.Writer;

/**
 * Interface for implementing clustering result classes.
 *
 * @param <T> generic parameter. This parameter is either a Double or a Float.
 *           The type of teh method @code {@link #getAngleBetweenClusters(int, int)}
 *           is calculated either as a float or as a double, depending on the clustering precision option.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public interface IArt2aClusteringResult<T> {
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns the vigilance parameter of the clustering algorithm.
     *
     * @return float vigilance parameter
     */
    T getVigilanceParameter();
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
     * Returns the input indices assigned to the given cluster.
     *
     * @param aClusterNumber given cluster number
     * @return array with the input indices for a given cluster
     * @throws IllegalArgumentException is thrown if the given cluster does not exist.
     */
    int[] getClusterIndices(int aClusterNumber) throws IllegalArgumentException;
    //
    /**
     * Calculates the cluster representative. This means that the input that is most
     * similar to the cluster vector is determined.
     *
     * @param aClusterNumber Cluster number for which the representative is to be calculated.
     * @return int input indices of the representative input in the cluster.
     * @throws IllegalArgumentException is thrown if the given cluster number is invalid.
     */
    int getClusterRepresentatives(int aClusterNumber) throws IllegalArgumentException;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
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
    //
    /**
     * Calculates the angle between two clusters.
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
     T getAngleBetweenClusters(int aFirstCluster, int aSecondCluster) throws IllegalArgumentException;
    // </editor-fold>
}
