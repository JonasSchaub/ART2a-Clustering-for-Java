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

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Callable class for clustering fingerprints.
 *
 * @author Betuel Sevindik
 */
public class ART2aClusteringTask implements Callable<ART2aFloatClustering> {
    //<editor-fold desc="private class variables" defaultstate="collapsed>
    /**
     * Clusterer instance
     */
    private ART2aFloatClustering art2aFloatClusteringResult;
    /**
     * Vigilance parameter, which influences the number of clusters to be formed.
     */
    private float vigilanceParameter;
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
    public ART2aClusteringTask(float aVigilance, float[][] aDataMatrix, int aMaximumEpochsNumber)  {
        this.art2aFloatClusteringResult = new ART2aFloatClustering(aDataMatrix, aMaximumEpochsNumber);
        this.vigilanceParameter = aVigilance;
    }
    //
    /**
     * Constructor.
     *
     * @param aVigilanceParameter influences the number of clusters to be formed.
     * @param aFingerprintFilePathName path name of the fingerprint file.
     * @param aMaximumEpochsNumber maximum number of epochs that the system can use.
     * @param aSeparator separator that separates the respective components of the fingerprint.
     * @throws Exception is thrown if the file cannot be read in.
     */
    public ART2aClusteringTask(float aVigilanceParameter, String aFingerprintFilePathName, int aMaximumEpochsNumber, char aSeparator) throws IOException {
        if(aVigilanceParameter < 0 || aVigilanceParameter > 1) {
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0 and less than 1.");
        }
        this.art2aFloatClusteringResult = new ART2aFloatClustering(aFingerprintFilePathName, aMaximumEpochsNumber, aSeparator);
        this.vigilanceParameter = aVigilanceParameter;
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
    public ART2aFloatClustering call() throws Exception {
        this.art2aFloatClusteringResult.startArt2aClustering(this.vigilanceParameter);
        return art2aFloatClusteringResult;
    }
    //</editor-fold>
    //
}
