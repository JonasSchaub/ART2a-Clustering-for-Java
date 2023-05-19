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
import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClustering;
import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClusteringResult;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Callable class for clustering fingerprints.
 *
 * @author Betuel Sevindik
 */
public class ART2aClusteringTask implements Callable<ART2aAbstractResult> {
    //<editor-fold desc="private class variables" defaultstate="collapsed>
    /**
     * Clusterer instance
     */
    private ART2aFloatClustering art2aFloatClusteringResult;
    private ART2aDoubleClustering art2aDoubleClusteringResult;
    /**
     * Vigilance parameter, which influences the number of clusters to be formed.
     */
    private float vigilanceParameter;

    private boolean addLog;
    private static final Logger LOGGER = Logger.getLogger(ART2aClusteringTask.class.getName());
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor.
     *
     * @param aVigilance influences the number of clusters to be formed.
     * @param aDataMatrix matrix contains fingerprints.The matrix has as many rows as there are fingerprints,
     *                    and the number
     *                    of columns corresponds to the dimensionality of the fingerprints.
     * @param aMaximumEpochsNumber maximum number of epochs that the system can use.
     */
    public ART2aClusteringTask(float aVigilance, float[][] aDataMatrix, int aMaximumEpochsNumber, boolean log, float aRequiredSimilarity, float aLearningParameter)  {
       // this.art2aFloatClusteringResult = new ART2aFloatClustering(aDataMatrix, aMaximumEpochsNumber);
        this.vigilanceParameter = aVigilance;
        this.addLog = log;
        float tmpRequiredSimilarity = aRequiredSimilarity;
        float tmpLearningParameter = aLearningParameter;
        this.art2aFloatClusteringResult = new ART2aFloatClustering(aDataMatrix, aMaximumEpochsNumber, aVigilance, tmpRequiredSimilarity, tmpLearningParameter);
    }
    public ART2aClusteringTask(float vigilanceParameter, float[][] aDataMatrix, int aMaximumEpochsNumber, boolean log) {
        this(vigilanceParameter, aDataMatrix, aMaximumEpochsNumber, log, 0.99f,0.01f);
    }
    public ART2aClusteringTask(float vigilanceParameter, double[][] aDataMatrix, int aMaximumEpochsNumber, boolean log, double aRequiredSimilarity, double aLearningParameter) {
        this.vigilanceParameter = vigilanceParameter;
        this.addLog = log;
        double tmpRequiredSimilarity = aRequiredSimilarity;
        double tmpLearningParameter = aLearningParameter;
        this.art2aDoubleClusteringResult = new ART2aDoubleClustering(aDataMatrix, aMaximumEpochsNumber, this.vigilanceParameter, tmpRequiredSimilarity, tmpLearningParameter);
    }
    public ART2aClusteringTask(float vigilanceParameter, double[][] aDataMatrix, int aMaximumEpochsNumber, boolean log) {
        this(vigilanceParameter, aDataMatrix, aMaximumEpochsNumber, log, 0.99,0.01);
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Overriden call() method">
    /**
     * Executes the clustering.
     *
     * @return clustering result.
     * @throws Exception is thrown if the clustering process failed.
     */
    @Override
    public ART2aAbstractResult call()  {
        /*
        try {
            if(this.art2aFloatClusteringResult != null) {
                System.out.println("Float");
                return this.art2aFloatClusteringResult.startClustering(this.vigilanceParameter, this.addLog);
            } else {
                System.out.println("Double");
                return this.art2aDoubleClusteringResult.startClustering(this.vigilanceParameter, addLog);
            }
        } catch (RuntimeException anException) {
           // throw anException;
            ART2aClusteringTask.LOGGER.log(Level.SEVERE, anException.toString(), anException);
            throw anException;
        }

         */









       // return art2aFloatClusteringResult;
        throw new UnsupportedOperationException();
    }
    //</editor-fold>
    //
}
