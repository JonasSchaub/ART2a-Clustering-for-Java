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

import de.unijena.cheminf.clustering.art2a.Abstract.ART2aAbstractResult;
import de.unijena.cheminf.clustering.art2a.Clustering.ART2aDoubleClustering;
import de.unijena.cheminf.clustering.art2a.Clustering.ART2aFloatClustering;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Callable class for clustering fingerprints.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class ART2aClusteringTask implements Callable<ART2aAbstractResult> {
    //<editor-fold desc="private class variables" defaultstate="collapsed>
    /**
     * Float clustering instance
     */
    private ART2aFloatClustering art2aFloatClusteringResult;
    /**
     * Double clustering instance
     */
    private ART2aDoubleClustering art2aDoubleClusteringResult;
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
    //<editor-fold desc="private static class variables" defaultstate="collapsed>
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ART2aClusteringTask.class.getName());
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Float clustering task constructor.
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
    public ART2aClusteringTask(float aVigilanceParameter, float[][] aDataMatrix, int aMaximumEpochsNumber, boolean aClusteringResultInTextFile, float aRequiredSimilarity, float aLearningParameter) {
        this.vigilanceParameter = aVigilanceParameter;
        this.addClusteringResultInTextFile = aClusteringResultInTextFile;
        this.art2aFloatClusteringResult = new ART2aFloatClustering(aDataMatrix, aMaximumEpochsNumber, aVigilanceParameter, aRequiredSimilarity, aLearningParameter);
    }
    //
    /**
     * Float clustering task constructor.
     * For the required similarity and learning parameter default values are used.
     *
     * @param vigilanceParameter parameter to influence the number of clusters.
     * @param aDataMatrix matrix contains all inputs for clustering.
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
        this.art2aDoubleClusteringResult = new ART2aDoubleClustering(aDataMatrix, aMaximumEpochsNumber, aVigilanceParameter, aRequiredSimilarity, aLearningParameter);
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
    public ART2aAbstractResult call()  {
        try {
            if(this.art2aFloatClusteringResult != null) {
                return this.art2aFloatClusteringResult.startClustering(this.vigilanceParameter, this.addClusteringResultInTextFile);
            } else {
                return this.art2aDoubleClusteringResult.startClustering(this.vigilanceParameter, this.addClusteringResultInTextFile);
            }
        } catch (Exception anException) {
            ART2aClusteringTask.LOGGER.log(Level.SEVERE, anException.toString(), anException);
            throw anException;
        }
    }
    //</editor-fold>
}
