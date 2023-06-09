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

import de.unijena.cheminf.clustering.art2a.clustering.ART2aDoubleClustering;
import de.unijena.cheminf.clustering.art2a.clustering.ART2aFloatClustering;
import de.unijena.cheminf.clustering.art2a.interfaces.IART2aClustering;
import de.unijena.cheminf.clustering.art2a.interfaces.IART2aClusteringResult;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Callable class for clustering fingerprints.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class ART2aClusteringTask implements Callable<IART2aClusteringResult> {
    //<editor-fold desc="private class variables" defaultstate="collapsed>
    /**
     * Clustering instance
     */
    private IART2aClustering art2aClustering;
    /**
     * Vigilance parameter, which influences the number of clusters to be formed.
     */
    private float vigilanceParameter;
    /**
     * If addClusteringResultInTextFile = true the clustering results are written in text files.
     * If addClusteringResultInTextFile = false the clustering results are not written in text files.
     */
    private boolean addClusteringResultInTextFile;
    //</editor-fold>
    //
    //<editor-fold desc="private final class variables" defaultstate="collapsed>
    /**
     * Default value of the learning parameter in float
     */
    private final float DEFAULT_LEARNING_PARAMETER_FLOAT = 0.01f;
    /**
     * Default value of the required similarity parameter in float
     */
    private final float REQUIRED_SIMILARITY_FLOAT = 0.99f;
    /**
     * Default value of the learning parameter in double
     */
    private final double DEFAULT_LEARNING_PARAMETER_DOUBLE = 0.01;
    /**
     * Default value of the required similarity parameter in double
     */
    private final double REQUIRED_SIMILARITY_DOUBLE = 0.99;
    //</editor-fold>
    //
    //<editor-fold desc="private static final class variables" defaultstate="collapsed>
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ART2aClusteringTask.class.getName());
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Float clustering task constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering. Each row of the matrix contains one input.
     *                    In addition, all inputs must have the same length.
     *                    Each column of the matrix contains one component of the input.
     * @param aMaximumEpochsNumber maximum number of epochs that the system may use for convergence.
     * @param aWriteClusteringResultToTextFile if the parameter is set to true, the clustering results are
     *                                    written to text files, otherwise not.
     * @param aRequiredSimilarity parameter indicating the minimum similarity between the current
     *                            cluster vectors and the previous cluster vectors. The parameter is crucial
     *                            for the convergence of the system. If the parameter is set too high, a much
     *                            more accurate similarity is expected and the convergence may take longer,
     *                            while a small parameter expects a lower similarity between the cluster
     *                            vectors and thus the system may converge faster.
     * @param aLearningParameter parameter to define the intensity of keeping the old class vector in mind
     *                           before the system adapts it to the new sample vector.
     */
    public ART2aClusteringTask(float aVigilanceParameter, float[][] aDataMatrix, int aMaximumEpochsNumber, boolean aWriteClusteringResultToTextFile, float aRequiredSimilarity, float aLearningParameter) {
        this.vigilanceParameter = aVigilanceParameter;
        this.addClusteringResultInTextFile = aWriteClusteringResultToTextFile;
        this.art2aClustering = new ART2aFloatClustering(aDataMatrix, aMaximumEpochsNumber, aVigilanceParameter, aRequiredSimilarity, aLearningParameter);
    }
    //
    /**
     * Float clustering task constructor.
     * For the required similarity and learning parameter default values are used.
     *
     * @param vigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering. Each row of the matrix contains one input.
     *                     In addition, all inputs must have the same length.
     * @param aMaximumEpochsNumber maximum number of epochs that the system may use for convergence.
     * @param aClusteringResultInTextFile if the parameter is set to true, the clustering results are
     *                                     written to text files, otherwise not.
     */
    public ART2aClusteringTask(float vigilanceParameter, float[][] aDataMatrix, int aMaximumEpochsNumber, boolean aClusteringResultInTextFile) {
        this(vigilanceParameter, aDataMatrix, aMaximumEpochsNumber, aClusteringResultInTextFile, 0.99f,0.01f); // TODO ask Jonas and Felix!!!!!!
    }
    //
    /**
     * Double clustering task constructor.
     *
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering.
     * @param aMaximumEpochsNumber maximum number of epochs that the system may use for convergence.
     * @param aClusteringResultInTextFile if the parameter is set to true, the clustering results are
     *                                    written to text files, otherwise not.
     * @param aRequiredSimilarity parameter indicating the minimum similarity between the current
     *                            cluster vectors and the previous cluster vectors.
     * @param aLearningParameter parameter to define the intensity of keeping the old class vector in mind
     *                           before the system adapts it to the new sample vector.
     */
    public ART2aClusteringTask(float aVigilanceParameter, double[][] aDataMatrix, int aMaximumEpochsNumber, boolean aClusteringResultInTextFile, double aRequiredSimilarity, double aLearningParameter) {
        this.vigilanceParameter = aVigilanceParameter;
        this.addClusteringResultInTextFile = aClusteringResultInTextFile;
        this.art2aClustering = new ART2aDoubleClustering(aDataMatrix, aMaximumEpochsNumber, aVigilanceParameter, aRequiredSimilarity, aLearningParameter);
    }
    //
    /**
     * Double clustering task constructor.
     * For the required similarity and learning parameter default values are used.
     *
     * @param vigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering.
     * @param aMaximumEpochsNumber maximum number of epochs that the system may use for convergence.
     * @param aClusteringResultInTextFile if the parameter is set to true, the clustering results are
     *                                     written to text files, otherwise not.
     */
    public ART2aClusteringTask(float vigilanceParameter, double[][] aDataMatrix, int aMaximumEpochsNumber, boolean aClusteringResultInTextFile) {
        this(vigilanceParameter, aDataMatrix, aMaximumEpochsNumber, aClusteringResultInTextFile, 0.99,0.01); // TODO ask Jonas and Felix!!!!!!!!
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
    public IART2aClusteringResult call() {
        try {
            return this.art2aClustering.startClustering(this.vigilanceParameter, this.addClusteringResultInTextFile);
        } catch (RuntimeException anException) {
            ART2aClusteringTask.LOGGER.log(Level.SEVERE, anException.toString(), anException);
            return null;
        }
    }
    //</editor-fold>
}
