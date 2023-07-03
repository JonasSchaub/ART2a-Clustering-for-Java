/*
 * ART2a Clustering for Java
 *
 * Copyright (C) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
 *
 * Source code is available at <https://github.com/JonasSchaub/ART2a-Clustering-for-Java>
 *
 * GNU General Public License v3.0
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.unijena.cheminf.clustering.art2a.interfaces;

import de.unijena.cheminf.clustering.art2a.exceptions.ConvergenceFailedException;

/**
 * Interface for implementing float and double Art-2a clustering.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public interface IArt2aClustering {
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Initialise the cluster matrices.
     */
    void initializeMatrices();
    //
    /**
     * Since the Art-2a algorithm randomly selects any input vector, the input vectors must first be randomized.
     * The input vectors/fingerprints are randomized so that all input vectors can be clustered by random selection.
     *
     * Here, the Fisher-Yates method is used to randomize the inputs.
     *
     * @return an array with vector indices in a random order
     */
    int[] getRandomizeVectorIndices();
    //
    /**
     * Starts an Art-2A clustering algorithm.
     * The clustering process begins by randomly selecting an input vector/fingerprint from the data matrix.
     * After normalizing the first input vector, it is assigned to the first cluster. For all other subsequent
     * input vectors, they also undergo certain normalization steps. If there is sufficient similarity to an
     * existing cluster, they are assigned to that cluster. Otherwise, a new cluster is formed, and the
     * input is added to it. Null vectors are not clustered.
     *
     * @param anIsClusteringResultExported If the parameter == true, all information about the
     * clustering is exported to 2 text files.The first exported text file is a detailed log of the clustering process
     * and the intermediate results and the second file is a rough overview of the final result.
     * @param aSeedValue user-defined seed value to randomize input vectors.
     * @return IArt2aClusteringResult
     * @throws ConvergenceFailedException is thrown, when convergence fails.
     */
    IArt2aClusteringResult getClusterResult(boolean anIsClusteringResultExported, int aSeedValue) throws ConvergenceFailedException;
    // </editor-fold>
}
