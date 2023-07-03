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

package de.unijena.cheminf.clustering.art2a;

import de.unijena.cheminf.clustering.art2a.clustering.Art2aDoubleClustering;
import de.unijena.cheminf.clustering.art2a.clustering.Art2aFloatClustering;
import de.unijena.cheminf.clustering.art2a.exceptions.ConvergenceFailedException;
import de.unijena.cheminf.clustering.art2a.interfaces.IArt2aClustering;
import de.unijena.cheminf.clustering.art2a.interfaces.IArt2aClusteringResult;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Callable class for clustering input vectors (fingerprints).
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class Art2aClusteringTask implements Callable<IArt2aClusteringResult> {
    //<editor-fold desc="private class variables" defaultstate="collapsed>
    /**
     * Clustering instance
     */
    private IArt2aClustering art2aClustering;
    /**
     * If isClusteringResultExported = true the cluster results are exported to text files.
     * If isClusteringResultExported = false the clustering results are not exported to text files.
     */
    private boolean isClusteringResultExported;
    /**
     * Seed value to randomize input vectors.
     */
    private int seed;
    /**
     * If the seed is setting by the user isSeedSet == true, otherwise false.
     * Is needed to determine whether the default value or the seed specified by the user should be used.
     */
    private boolean isSeedSet;
    //</editor-fold>
    //
    //<editor-fold desc="private final class constants" defaultstate="collapsed>
    /**
     * Seed value for randomising the input vectors before starting clustering.
     */
    private final int DEFAULT_SEED_VALUE_TO_RANDOMIZE_INPUT_VECTORS = 1;
    //</editor-fold>
    //
    //<editor-fold desc="private static final class constants" defaultstate="collapsed>
    /**
     * Default value of the learning parameter in float
     */
    public static final float DEFAULT_LEARNING_PARAMETER_FLOAT = 0.01f;
    /**
     * Default value of the required similarity parameter in float
     */
    public static final float REQUIRED_SIMILARITY_FLOAT = 0.99f;
    /**
     * Default value of the learning parameter in double
     */
    public static final double DEFAULT_LEARNING_PARAMETER_DOUBLE = 0.01;
    /**
     * Default value of the required similarity parameter in double
     */
    public static final double REQUIRED_SIMILARITY_DOUBLE = 0.99;
    //</editor-fold>
    //
    //<editor-fold desc="private static final class variables" defaultstate="collapsed>
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(Art2aClusteringTask.class.getName());
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Float clustering task constructor.
     * Creates a new Art2aClusteringTask instance with the specified parameters.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering. Each row of the matrix contains one input.
     * In addition, all inputs must have the same length. Each column of the matrix contains one component of the input.
     * @param aMaximumEpochsNumber maximum number of epochs that the system may use for convergence.
     * @param anIsClusteringResultExported if the parameter is set to true, the cluster results
     * are exported to text files.
     * @param aRequiredSimilarity parameter indicating the minimum similarity between the current
     * cluster vectors and the previous cluster vectors. The parameter is crucial
     * for the convergence of the system. If the parameter is set too high, a much
     * more accurate similarity is expected and the convergence may take longer,
     * while a small parameter expects a lower similarity between the cluster
     * vectors and thus the system may converge faster.
     * @param aLearningParameter parameter to define the intensity of keeping the old cluster vector in mind
     * before the system adapts it to the new sample vector.
     * @throws IllegalArgumentException is thrown, if the given arguments are invalid. The checking of the arguments
     * is done in the constructor of Art2aFloatClustering.
     * @throws NullPointerException is thrown, if the given aDataMatrix is null. The checking of the data matrix is
     * done in the constructor of the ArtaFloatClustering.
     *
     */
    public Art2aClusteringTask(float aVigilanceParameter, float[][] aDataMatrix, int aMaximumEpochsNumber,
                               boolean anIsClusteringResultExported, float aRequiredSimilarity, float aLearningParameter)
            throws IllegalArgumentException, NullPointerException {
        this.isClusteringResultExported = anIsClusteringResultExported;
        this.isSeedSet = false;
        this.art2aClustering = new Art2aFloatClustering(aDataMatrix, aMaximumEpochsNumber, aVigilanceParameter,
                aRequiredSimilarity, aLearningParameter);
    }
    //
    /**
     * Float clustering task constructor.
     * Creates a new Art2aClusteringTask instance with the specified parameters.
     * For the required similarity and learning parameter default values are used.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering. Each row of the matrix contains one input.
     *                    In addition, all inputs must have the same length.
     *                    Each column of the matrix contains one component of the input.
     * @param aMaximumEpochsNumber maximum number of epochs that the system may use for convergence.
     * @param anIsClusteringResultExported if the parameter is set to true, the cluster results
     *                                         are exported to text files.
     * @throws IllegalArgumentException is thrown, if the given arguments are invalid. The checking of the arguments
     *                                  is done in the constructor of Art2aFloatClustering.
     * @throws NullPointerException is thrown, if the given aDataMatrix is null. The checking of the data matrix is
     *                              done in the constructor of the ArtaFloatClustering.
     *
     * @see de.unijena.cheminf.clustering.art2a.Art2aClusteringTask#Art2aClusteringTask(float, float[][], int,
     * boolean, float, float)
     */
    public Art2aClusteringTask(float aVigilanceParameter, float[][] aDataMatrix, int aMaximumEpochsNumber,
                               boolean anIsClusteringResultExported)
            throws IllegalArgumentException, NullPointerException {
        this(aVigilanceParameter, aDataMatrix, aMaximumEpochsNumber, anIsClusteringResultExported,
                Art2aClusteringTask.REQUIRED_SIMILARITY_FLOAT, Art2aClusteringTask.DEFAULT_LEARNING_PARAMETER_FLOAT);
    }
    //
    /**
     * Double clustering task constructor.
     * Creates a new Art2aDoubleClustering instance with the specified parameters.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering. Each row of the matrix contains one input.
     * In addition, all inputs must have the same length.
     * Each column of the matrix contains one component of the input.
     * @param aMaximumEpochsNumber maximum number of epochs that the system may use for convergence.
     * @param anIsClusteringResultExported if the parameter is set to true, the cluster results are
     * exported to text files.
     * @param aRequiredSimilarity parameter indicating the minimum similarity between the current
     * cluster vectors and the previous cluster vectors.
     * @param aLearningParameter parameter to define the intensity of keeping the old cluster vector in mind
     * before the system adapts it to the new sample vector.
     * @throws IllegalArgumentException is thrown, if the given arguments are invalid. The checking of the arguments
     * is done in the constructor of Art2aFloatClustering.
     * @throws NullPointerException is thrown, if the given aDataMatrix is null. The checking of the data matrix is
     * done in the constructor of the ArtaFloatClustering.
     */
    public Art2aClusteringTask(double aVigilanceParameter, double[][] aDataMatrix, int aMaximumEpochsNumber,
                               boolean anIsClusteringResultExported, double aRequiredSimilarity, double aLearningParameter)
            throws IllegalArgumentException, NullPointerException {
        this.isClusteringResultExported = anIsClusteringResultExported;
        this.art2aClustering = new Art2aDoubleClustering(aDataMatrix, aMaximumEpochsNumber, aVigilanceParameter,
                aRequiredSimilarity, aLearningParameter);
    }
    //
    /**
     * Double clustering task constructor.
     * Creates a new Art2aDoubleClustering instance with the specified parameters.
     * For the required similarity and learning parameter default values are used.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering. Each row of the matrix contains one input.
     * In addition, all inputs must have the same length. Each column of the matrix contains one component of the input.
     * @param aMaximumEpochsNumber maximum number of epochs that the system may use for convergence.
     * @param anIsClusteringResultExported if the parameter is set to true, the cluster results are
     * exported to text files.
     * @throws IllegalArgumentException is thrown, if the given arguments are invalid. The checking of the arguments
     * is done in the constructor of Art2aFloatClustering.
     * @throws NullPointerException is thrown, if the given aDataMatrix is null. The checking of the data matrix is
     * done in the constructor of the ArtaFloatClustering.
     *
     * @see de.unijena.cheminf.clustering.art2a.Art2aClusteringTask#Art2aClusteringTask(double, double[][], int,
     * boolean, double, double)
     *
     */
    public Art2aClusteringTask(double aVigilanceParameter, double[][] aDataMatrix, int aMaximumEpochsNumber,
                               boolean anIsClusteringResultExported) throws IllegalArgumentException, NullPointerException {
        this(aVigilanceParameter, aDataMatrix, aMaximumEpochsNumber, anIsClusteringResultExported,
                Art2aClusteringTask.REQUIRED_SIMILARITY_DOUBLE, Art2aClusteringTask.DEFAULT_LEARNING_PARAMETER_DOUBLE);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Overriden call() method">
    /**
     * Executes the clustering.
     *
     * @return clustering result.
     */
    @Override
    public IArt2aClusteringResult call() {
        try {
            if(this.isSeedSet) {
                return this.art2aClustering.getClusterResult(this.isClusteringResultExported, this.seed);
            } else {
                return this.art2aClustering.getClusterResult(this.isClusteringResultExported,
                        this.DEFAULT_SEED_VALUE_TO_RANDOMIZE_INPUT_VECTORS);
            }
        } catch (ConvergenceFailedException anException) {
            Art2aClusteringTask.LOGGER.log(Level.SEVERE, anException.toString(), anException);
            return null;
        }
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public method">
    /**
     *
     * User-defined seed value to randomize input vectors.
     * Different seed values can lead to different clustering results.
     *
     * @param aSeed seed value
     * @return user-defined seed value.
     */
    public int setSeed(int aSeed) {
        this.seed = aSeed;
        this.isSeedSet = true;
        return this.seed;
    }
    //</editor-fold>
}
