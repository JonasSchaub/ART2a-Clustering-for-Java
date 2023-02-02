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

import de.unijena.cheminf.clustering.art2a.Logger.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Implementation of an ART 2a algorithm for fast clustering of fingerprints.
 * It is possible to cluster both bit and count fingerprints. <br>
 * LITERATURE SOURCE:<br>
 * Original : G.A. Carpenter etal., Neural Networks 4 (1991) 493-504<br>
 * Secondary : Wienke etal., Chemometrics and Intelligent Laboratory Systems 24
 * (1994), 367-387<br>
 * @author original C# code: Stefan Neumann, Gesellschaft fuer
 *         naturwissenschaftliche Informatik, stefan.neumann@gnwi.de<br>
 *         porting to Java: Thomas Kuhn and Christian Geiger, University of Applied Sciences
 *         Gelsenkirchen, 2007
 * @author Betuel Sevindik
 */
public class ART2aFloatClusteringResult {
    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * The matrix contains all fingerprints to be clustered.
     */
    private float[][] dataMatrix;
    /**
     * Threshold for contrast enhancement. If a vector/fingerprint component is below the threshold, it is set to zero.
     */
    private float thresholdForContrastEnhancement;
    /**
     * Number of fingerprints to be clustered.
     */
    private int numberOfFingerprints;
    /**
     * Dimensionality of the fingerprint.
     */
    private int numberOfComponents;
    /**
     * scaling factor for the modified vectors.
     */
    private float scalingFactor;
    /**
     * Matrix contains all cluster vectors.
     */
    private float[][] clusterMatrix;
    /**
     * Matrix contains all cluster vectors of previous epoch. Is needed to check the convergence of
     * the system.
     */
    private float[][] clusterMatrixPreviousEpoch;
    /**
     * Maximum number of epochs the system may need to converge.
     */
    private int maximumNumberOfEpochs;
    /**
     * Checks the clustering process. If clustering has failed clusteringStatus = false.
     */
    private boolean clusteringStatus;
    /**
     * The seed value for permutation of the vector field.
     */
    private int seed;
    /**
     * Result logger
     */
    private Logger clusteringLogger;
    /**
     * Queue for logging
     */
    private ConcurrentLinkedQueue<String> intermediateResultList;
    //</editor-fold>
    //
    //<editor-fold desc="private final variables" defaultstate="collapsed">
    /**
     * Learning parameter to modify the cluster after a new input has been added.
     */
    private final float defaultLearningParameter = 0.01f;
    /**
     * Minimum similarity between the cluster vectors that must be present.
     */
    private final float requiredSimilarity = 0.99f;
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor.
     * Scaling factor and threshold are set.
     *
     * @param aDataMatrix matrix contains all input for clustering
     * @param aMaximumNumberOfEpochs maximum number of epochs that the system may use for convergence.
     */
    public ART2aFloatClusteringResult(float[][] aDataMatrix, int aMaximumNumberOfEpochs) {
        if(aDataMatrix == null) {
            throw new IllegalArgumentException("aDataMatrix is null.");
        }
        if(aMaximumNumberOfEpochs <= 0) {
            throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
        }
        this.dataMatrix = aDataMatrix;
        this.checkDataMatrix(this.dataMatrix);
        this.numberOfFingerprints = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0)); // TODO: @Betuel scalingFactor = thresholdFor...
        this.thresholdForContrastEnhancement = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
    }
    //
    /**
     * Constructor.
     * Scaling factor and threshold are set.
     *
     * @param aFile fingerprint file.
     * @param aMaximumNumberOfEpochs maximum number of epochs that the system may use for convergence.
     * @throws Exception is thrown if the file cannot be read in.
     */
    public ART2aFloatClusteringResult(String aFile, int aMaximumNumberOfEpochs, String aSeparator) throws Exception {
        if(aMaximumNumberOfEpochs<= 0) {
            throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
        }
        this.getDataMatrix(aFile, aSeparator);
        this.checkDataMatrix(this.dataMatrix);
        this.numberOfFingerprints = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0)); // TODO: @Betuel scalingFactor = thresholdFor...
        this.thresholdForContrastEnhancement = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Method checks whether the data matrix with the fingerprints is valid.
     * The data matrix not contain negative inputs. The inputs or the vectors/fingerprints must all have the same length.
     * If the vector/fingerprint components are not between 0 and 1, they are scaled.
     *
     * @param aDataMatrix contains fingerprints.
     */
    private void checkDataMatrix(float[][] aDataMatrix) {
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        if (aDataMatrix.length <= 0) {
            throw new IllegalArgumentException("The number of vectors must be greater then 0 to cluster inputs");
        }
        int tmpNumberOfVectorComponents = aDataMatrix[0].length;
        float tmpCurrentMatrixComponent;
        float[] tmpRow;
        HashMap<float[],Integer> tmpMap = new HashMap<>(this.numberOfFingerprints);
        ArrayList<Integer> tmpComponentsForScaling = new ArrayList<>();
        for (int i = 0; i < aDataMatrix.length; i++) {
            tmpRow = aDataMatrix[i];
            if (tmpNumberOfVectorComponents != tmpRow.length) {
                throw new IllegalArgumentException("the vectors must be have the same length!");
            }
            for (int j = 0; j < tmpRow.length; j++) {
                tmpCurrentMatrixComponent = tmpRow[j];
                if (tmpCurrentMatrixComponent > 1) {// TODO: @Betuel check null?
                    tmpMap.put(aDataMatrix[i],i);
                    tmpComponentsForScaling.add(i);
                }
                if(tmpCurrentMatrixComponent < 0) {
                    throw new IllegalArgumentException("Only positive values allowed.");
                }
            }
        }
        this.scaleInput(tmpMap);
    }
    //
    /**
     * Method for scaling the fingerprints if they are not between 0 and 1.
     * Thus serves for the scaling of count fingerprints.
     *
     * @param aFingerprintToMatrixRowMap is a map that maps the fingerprints with components outside 0-1 to the row position in the matrix.
     */
    private void scaleInput(HashMap<float[],Integer> aFingerprintToMatrixRowMap)  {
        for (float[] tmpScalingVector : aFingerprintToMatrixRowMap.keySet()) {
            float tmpFirstComponent = tmpScalingVector[0];
            System.out.println(tmpFirstComponent);
            for (float tmpComponentsOfScalingVector : tmpScalingVector) {
                if (tmpComponentsOfScalingVector > tmpFirstComponent) {
                    tmpFirstComponent = tmpComponentsOfScalingVector;
                }
            }
            for (int i = 0; i < tmpScalingVector.length; i++) {
                float tmpScaledComponent = tmpScalingVector[i] / tmpFirstComponent;
                tmpScalingVector[i] = tmpScaledComponent;
                this.dataMatrix[aFingerprintToMatrixRowMap.get(tmpScalingVector)] = tmpScalingVector;
            }
        }
    }
    //
    /**
     * Initialise the cluster matrix
     *
     */
    private void initialiseMatrices() {
        this.clusterMatrix = new float[this.numberOfFingerprints][this.numberOfComponents];
        this.clusterMatrixPreviousEpoch = new float[this.numberOfFingerprints][this.numberOfComponents];
    }
    //
    /**
     * Fills the array "tmpSampleVectorIndicesInRandomOrder" with vector indices in a
     * random order. <br>
     * Restriction: Every vector index is allowed to occur just one time in the
     * array.
     * @return int[] in which the random order (indices) of the vectors is stored.
     * @author Thomas Kuhn
     */
    private int[] randomizeVectorIndices()  {
        int[] tmpSampleVectorIndicesInRandomOrder = new int[this.numberOfFingerprints];
        for (int i = 0; i < this.numberOfFingerprints; i++) {
            tmpSampleVectorIndicesInRandomOrder[i] = i;
        }
        Random rnd = new Random(this.seed);
        this.seed++;
        int numberOfIterations = (this.numberOfFingerprints / 2) + 1;
        int tmpRandomIndex1;
        int tmpRandomIndex2;
        int tmpBuffer;
        for (int j = 0; j < numberOfIterations; j++) {
            tmpRandomIndex1 = (int) (this.numberOfFingerprints * rnd.nextDouble());
            tmpRandomIndex2 = (int) (this.numberOfFingerprints * rnd.nextDouble());

            tmpBuffer = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1];
            tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1] = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2];
            tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2] = tmpBuffer;
        }
        return tmpSampleVectorIndicesInRandomOrder;
    }
    //
    /**
     * Calculates the length of a vector. The length is needed for the normalisation of the vector.
     *
     * @param anInputVector vector whose length is calculated
     * @return float vector length
     */
    private float getVectorLength(float[] anInputVector) {
        float tmpVectorComponentsSqrtSum = 0;
        float tmpVectorLength;
        float tmpSumComp = 0;
        for(int i = 0; i< anInputVector.length; i++) {
            tmpVectorComponentsSqrtSum += anInputVector[i] * anInputVector[i];
            tmpSumComp += anInputVector[i];
        }
        if(tmpVectorComponentsSqrtSum == 0) {
            throw new ArithmeticException("Exception");
        }
        else {
            tmpVectorLength = (float) Math.sqrt(tmpVectorComponentsSqrtSum);
        }
        return tmpVectorLength;
    }
    //
    /**
     * Check after each epoch whether the system converges or not. If the system does not converge,
     * a false is returned, otherwise a true. To check convergence, the system checks whether the cluster
     * vectors are similar to the vectors of the previous epoch and also whether the cluster members have changed.
     *
     * @param aNumberOfDetectedClasses number of classes detected at the end of the epoch.
     * @param aVectorNew
     * @param aVectorOld
     * @param aConvergenceEpoch number of current epochs
     * @return true if system are converged false if not.
     */
    private boolean checkConvergence(int aNumberOfDetectedClasses, int[] aVectorNew, int[] aVectorOld, int aConvergenceEpoch)   {
        if(aConvergenceEpoch < this.maximumNumberOfEpochs) {
            boolean tmpConvergence = true;
            // first check
            for (int i = 0; i < this.numberOfFingerprints; i++) {
                if (aVectorNew[i] != aVectorOld[i]) {
                    tmpConvergence = false;
                    break;
                }
            }
            if (tmpConvergence) {
                // Check convergence by evaluating the similarity of the cluster vectors of this and the previous epoch.
                tmpConvergence = true;
                double tmpScalarProductOfClassVector;
                float[] tmpCurrentRowInClusterMatrix;
                float[] tmpPreviousEpochRow;
                for (int i = 0; i < aNumberOfDetectedClasses; i++) {
                    tmpScalarProductOfClassVector = 0;
                    tmpCurrentRowInClusterMatrix = this.clusterMatrix[i];
                    tmpPreviousEpochRow = this.clusterMatrixPreviousEpoch[i];
                    for (int j = 0; j < this.numberOfComponents; j++) {
                        tmpScalarProductOfClassVector += tmpCurrentRowInClusterMatrix[j] * tmpPreviousEpochRow[j];
                    }
                    if (tmpScalarProductOfClassVector < this.requiredSimilarity) { // TODO ask
                        tmpConvergence = false;
                        break;
                    }
                }
            }
            if (!tmpConvergence) {
                for (int i = 0; i < aNumberOfDetectedClasses; i++) {
                    for (int j = 0; j < this.numberOfComponents; j++) {
                        this.clusterMatrixPreviousEpoch[i][j] = this.clusterMatrix[i][j];
                    }
                }
                for (int tmpClusterDistribution = 0; tmpClusterDistribution < this.numberOfFingerprints; tmpClusterDistribution++) {
                    aVectorOld[tmpClusterDistribution] = aVectorNew[tmpClusterDistribution];
                }
            }
            return tmpConvergence;
        } else {
            throw new RuntimeException("Convergence failed");
        }
    }
    //
    /**
     * Method that can read in a text file with fingerprints.
     *
     * @param aFileName is a file that contains fingerprints. It is important that the file is
     *                  correctly formatted so that it can be read in without problems.
     *                  Each line in the file should have a fingerprint, where each component is separated by a separator.
     * @throws IOException is thrown if the file cannot be read in.
     */
    private void getDataMatrix(String aFileName, String aSeparator) throws IOException {
        if(aFileName == null || aFileName.isEmpty() || aFileName.isBlank()) {
            throw new IllegalArgumentException("aFileName is null or empty/blank.");
        }
        if(aSeparator == null || aSeparator.isBlank() || aSeparator.isEmpty()) {
            throw new IllegalArgumentException("aSeparator is null or empty/blank");
        }
        BufferedReader tmpFingerprintFileReader;
        try {
            tmpFingerprintFileReader = new BufferedReader(new FileReader(aFileName));
        } catch (IOException anException) {
            throw new IOException("File is not readable!");
        }
       // String tmpSeparatorSemicolon = ",";
        List<float[]> tmpFingerprintList = new ArrayList<>();
        String tmpFingerprintLine;
        int tmpDataMatrixRow = 0;
        try {
            while ((tmpFingerprintLine = tmpFingerprintFileReader.readLine()) != null) {
                String[] tmpFingerprint = tmpFingerprintLine.split(aSeparator);
                float[] tmpFingerprintFloatArray = new float[tmpFingerprint.length];
                for (int i = 0; i < tmpFingerprint.length; i++) {
                    tmpFingerprintFloatArray[i] = Float.parseFloat(tmpFingerprint[i]);
                }
                tmpDataMatrixRow++;
                tmpFingerprintList.add(tmpFingerprintFloatArray);
            }
            tmpFingerprintFileReader.close();
        } catch (IllegalArgumentException anException) {
            throw new IllegalArgumentException("The file is not available in a suitable form.");
        }
        this.dataMatrix = new float[tmpDataMatrixRow][tmpFingerprintList.get(0).length]; // TODO
        for(int tmpCurrentMatrixRow = 0; tmpCurrentMatrixRow < tmpDataMatrixRow; tmpCurrentMatrixRow++) {
            this.dataMatrix[tmpCurrentMatrixRow] = tmpFingerprintList.get(tmpCurrentMatrixRow);
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Start clustering of fingerprints (count and bit) with ART-2a.
     * Implementation of the ART 2a algorithm.
     *
     * @param aVigilanceParameter Parameter to influence the number of classes. 0 less than or equal to vigilance
     *                   Parameter less than or equal to 1.
     * @throws Exception if the clustering process failed.
     */
    public void startArt2aClustering(float aVigilanceParameter) throws Exception {
        this.clusteringStatus = false;
        try{
            this.initialiseMatrices();
            this.seed = 1;
            float[] tmpClusterMatrixRow;
            float[] tmpClusterMatrixRowOld;
            float tmpInitialWeightValue = (float) (1.0 / Math.sqrt(this.numberOfComponents));
            int tmpNumberOfDetectedClusters = 0;
            int[] tmpClassView = new int[this.numberOfFingerprints];
            int[] tmpClassViewOld = new int[this.numberOfFingerprints];
            float tmpVectorLength1;
            float tmpVectorLength2;
            float tmpRho;
            float tmpLength4;
            int tmpWinnerClassIndex;
            boolean tmpConvergence = false;

            ArrayList<Float> tmpList; // TODO remove list
            //Initialisation of the elements of the cluster matrix with init weight values
            for (int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
                tmpClusterMatrixRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
                tmpClusterMatrixRowOld = this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector];
                for (int tmpCurrentVectorComponentsInClusterMatrix = 0; tmpCurrentVectorComponentsInClusterMatrix < tmpClusterMatrixRow.length; tmpCurrentVectorComponentsInClusterMatrix++) {
                    tmpClusterMatrixRow[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
                    tmpClusterMatrixRowOld[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
                }
            }
            int tmpCurrentNumberOfEpochs = 1;
            // Initialisation of Logger
            this.clusteringLogger = new Logger();
            this.intermediateResultList = new ConcurrentLinkedQueue<>();
            // begin clustering process
            while (tmpConvergence == false && tmpCurrentNumberOfEpochs <= this.maximumNumberOfEpochs) { // TODO  maximumNumberOfEpochs
                // start result logging
                this.clusteringLogger.startResultLog(this.intermediateResultList);
                this.clusteringLogger.appendIntermediateResult(" VIGILANCE PARAMETER: " + aVigilanceParameter);
                this.clusteringLogger.appendIntermediateResult("Number of epochs: " + tmpCurrentNumberOfEpochs);
                this.clusteringLogger.appendIntermediateResult("");
                // randomize input vectors
                int[] tmpSampleVectorIndicesInRandomOrder = this.randomizeVectorIndices();
                for (int tmpCurrentInput = 0; tmpCurrentInput < this.numberOfFingerprints; tmpCurrentInput++) {
                    this.clusteringLogger.appendIntermediateResult("Input: " + tmpCurrentInput);
                    float[] tmpInputVector = new float[this.numberOfComponents];
                    boolean tmpCheckNullVector = true;
                    for (int tmpCurrentInputVectorComponents = 0; tmpCurrentInputVectorComponents < this.numberOfComponents; tmpCurrentInputVectorComponents++) {
                        tmpInputVector[tmpCurrentInputVectorComponents] = this.dataMatrix[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]][tmpCurrentInputVectorComponents]; // TODO also possible in another way?
                    }
                    // check input, if input is a null vector, do not cluster.
                    for (int tmpCheckInputComponentsToNull = 0; tmpCheckInputComponentsToNull < tmpInputVector.length; tmpCheckInputComponentsToNull++) {
                        if (tmpInputVector[tmpCheckInputComponentsToNull] != 0) {
                            tmpCheckNullVector = false;
                        }
                    }
                    if (tmpCheckNullVector) {
                        tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = -1;
                        this.clusteringLogger.appendIntermediateResult("This input is a null vector");
                    } else {
                        /* normalisation of the randomly selected input vector.
                        Subsequently, all components of the input vector are transformed
                        with a non-linear threshold function for contrast enhancement. */
                        tmpVectorLength1 = this.getVectorLength(tmpInputVector);
                        for (int tmpManipulateComponents = 0; tmpManipulateComponents < tmpInputVector.length; tmpManipulateComponents++) {
                            tmpInputVector[tmpManipulateComponents] *= (1 / tmpVectorLength1);
                            if (tmpInputVector[tmpManipulateComponents] <= this.thresholdForContrastEnhancement) {
                                tmpInputVector[tmpManipulateComponents] = 0;
                            }
                        }
                        //the transformed input vector is normalised again.
                        // tmpList = this.getVectorLength(tmpInputVector);
                        tmpVectorLength2 = this.getVectorLength(tmpInputVector);
                        for (int tmpNormalizeInputComponents = 0; tmpNormalizeInputComponents < tmpInputVector.length; tmpNormalizeInputComponents++) {
                            tmpInputVector[tmpNormalizeInputComponents] *= (1 / tmpVectorLength2);
                        }
                        //First pass, no clusters available, so the first cluster is created.
                        if (tmpNumberOfDetectedClusters == 0) {
                            this.clusterMatrix[0] = tmpInputVector;
                            tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters;
                            tmpNumberOfDetectedClusters++;
                            this.clusteringLogger.appendIntermediateResult("Cluster number: 0");
                            this.clusteringLogger.appendIntermediateResult("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                        } else {
                            // Cluster number is greater than or equal to 1, so a rho winner is determined as shown in the following steps.
                            float tmpSumCom = 0;
                            for (float tmpVectorComponentsOfNormalizeVector : tmpInputVector) {
                                tmpSumCom += tmpVectorComponentsOfNormalizeVector;
                            }
                            tmpWinnerClassIndex = tmpNumberOfDetectedClusters;
                            boolean tmpRhoWinner = true;
                            tmpRho = this.scalingFactor * tmpSumCom;
                            for (int tmpCurrentClusterMatrixRow = 0; tmpCurrentClusterMatrixRow < tmpNumberOfDetectedClusters; tmpCurrentClusterMatrixRow++) {
                                float[] tmpRow;
                                float tmpRho2 = 0;
                                tmpRow = this.clusterMatrix[tmpCurrentClusterMatrixRow];
                                for (int tmpElementsInRow = 0; tmpElementsInRow < this.numberOfComponents; tmpElementsInRow++) {
                                    tmpRho2 += tmpInputVector[tmpElementsInRow] * tmpRow[tmpElementsInRow];
                                }
                                if (tmpRho2 > tmpRho) {
                                    tmpRho = tmpRho2;
                                    tmpWinnerClassIndex = tmpCurrentClusterMatrixRow;
                                    tmpRhoWinner = false;
                                }
                            }
                            if (tmpRhoWinner == true || tmpRho < aVigilanceParameter) {
                                tmpNumberOfDetectedClusters++;
                                tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters - 1;
                                this.clusterMatrix[tmpNumberOfDetectedClusters - 1] = tmpInputVector;
                                this.clusteringLogger.appendIntermediateResult("Cluster number: " + (tmpNumberOfDetectedClusters - 1));
                                this.clusteringLogger.appendIntermediateResult("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            } else {
                                for (int m = 0; m < this.numberOfComponents; m++) {
                                    if (this.clusterMatrix[tmpWinnerClassIndex][m] <= this.thresholdForContrastEnhancement) {
                                        tmpInputVector[m] = 0;
                                    }
                                }
                                float tmpLength3 = this.getVectorLength(tmpInputVector);
                                float tmpFactor1 = this.defaultLearningParameter / tmpLength3;
                                float tmpFactor2 = 1 - this.defaultLearningParameter;
                                for (int tmpAdaptedComponents = 0; tmpAdaptedComponents < this.numberOfComponents; tmpAdaptedComponents++) {
                                    tmpInputVector[tmpAdaptedComponents] = tmpInputVector[tmpAdaptedComponents] * tmpFactor1 + tmpFactor2 * this.clusterMatrix[tmpWinnerClassIndex][tmpAdaptedComponents]; // result t
                                }
                                tmpLength4 = this.getVectorLength(tmpInputVector);
                                for (int i = 0; i < tmpInputVector.length; i++) {
                                    tmpInputVector[i] *= (1 / tmpLength4);
                                }
                                this.clusterMatrix[tmpWinnerClassIndex] = tmpInputVector;
                                tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpWinnerClassIndex;
                                this.clusteringLogger.appendIntermediateResult("Cluster number: " + tmpWinnerClassIndex);
                                this.clusteringLogger.appendIntermediateResult("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            }
                        }
                    }
                }
                // number of cluster members
                TreeMap<Integer, Integer> tmpClusterToMembersMap = new TreeMap<>();
                int i = 1;
                for (int tmpClusterMembers : tmpClassView) {
                    if (tmpClusterMembers == -1) {
                        continue;
                    }
                    if (tmpClusterToMembersMap.containsKey(tmpClusterMembers) == false) {
                        tmpClusterToMembersMap.put(tmpClusterMembers, i);
                    } else {
                        tmpClusterToMembersMap.put(tmpClusterMembers, tmpClusterToMembersMap.get(tmpClusterMembers) + 1);
                    }
                }
                tmpCurrentNumberOfEpochs++;
                // check the convergence. If the network is converged, tmpConvergence == true otherwise false
                tmpConvergence = this.checkConvergence(tmpNumberOfDetectedClusters, tmpClassView, tmpClassViewOld, tmpCurrentNumberOfEpochs);
                this.clusteringLogger.appendIntermediateResult("Cluster members: " + tmpClusterToMembersMap);
                this.clusteringLogger.appendIntermediateResult("Convergence status: " + tmpConvergence);
                this.clusteringLogger.appendIntermediateResult("---------------------------------------");
            }
            this.clusteringStatus = true;
            System.out.println(this.clusteringStatus);
        } catch (RuntimeException anRuntimeException) {
            this.clusteringStatus = false;
            System.out.println(this.clusteringStatus);
            throw anRuntimeException;
        } catch(Exception anException) {
            this.clusteringStatus = false;
            System.out.println(this.clusteringStatus);
            throw new Exception("The clustering process has failed."); // TODO Exception
        }
    }
    //
    /**
     * Method returns the clustering results.
     *
     * @return log queue
     */
    public ConcurrentLinkedQueue<String> getResult() {
        return this.intermediateResultList;
    }
    public boolean getClusteringStatus() {
        return this.clusteringStatus;
    }
    //</editor-fold>
    //
}