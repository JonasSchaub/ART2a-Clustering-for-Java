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
 * Original : G.A. Carpenter et al., Neural Networks 4 (1991) 493-504<br>
 * Secondary : Wienke et al., Chemometrics and Intelligent Laboratory Systems 24
 * (1994), 367-387<br>
 * @author Betuel Sevindik; original C# code: Stefan Neumann, Gesellschaft fuer
 *         naturwissenschaftliche Informatik, stefan.neumann@gnwi.de<br>
 *         porting to Java: Thomas Kuhn and Christian Geiger, University of Applied Sciences
 *         Gelsenkirchen, 2007
 */
public class ART2aFloatClustering {
    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * The matrix contains all fingerprints to be clustered.
     * Each row of the matrix represents a fingerprint.
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
     * Queue for logging clustering process
     */
    private ConcurrentLinkedQueue<String> clusteringProcessLog;
    /**
     * Queue for logging clustering result
     */
    private ConcurrentLinkedQueue<String> clusteringResultLog;
    /**
     * Map maps the number of epochs to the number of detected clusters.
     */
    private HashMap<Integer, Integer> numberOfEpochsToNumberOfClusters;
    /**
     * Map maps the vigilance parameter to the number of epochs and to the number of detected clusters.
     */
    private HashMap<Float, HashMap<Integer,Integer>> vigilanceParameterToNumberOfEpochsAndNumberOfClusters;
    //</editor-fold>
    //
    //<editor-fold desc="private final variables" defaultstate="collapsed">
    /**
     * Learning parameter to modify the cluster after a new input has been added.
     */
    private final float DEFAULT_LEARNING_PARAMETER = 0.01f;
    /**
     * Minimum similarity between the cluster vectors that must be present.
     */
    private final float REQUIRED_SIMILARITY = 0.99f;
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor.
     * Checked the dataMatrix for correctness.
     * Scaling factor and threshold are set.
     *
     * @param aDataMatrix matrix contains all input for clustering.
     * @param aMaximumNumberOfEpochs maximum number of epochs that the system may use for convergence.
     * @throws IllegalArgumentException is thrown if the maximum number of epochs is not assigned correctly.
     * @throws NullPointerException is thrown if aDataMatrix is null.
     *
     */
    public ART2aFloatClustering(float[][] aDataMatrix, int aMaximumNumberOfEpochs) throws IllegalArgumentException, NullPointerException {
        this.clusteringProcessLog = new ConcurrentLinkedQueue<>();
        this.clusteringResultLog = new ConcurrentLinkedQueue<>();
        if(aDataMatrix == null) {
            throw new NullPointerException("aDataMatrix is null.");
        }
        if(aMaximumNumberOfEpochs <= 0) {
            throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
        }
        this.clusteringProcessLog = new ConcurrentLinkedQueue<>();
        this.dataMatrix = aDataMatrix;
        this.checkDataMatrix(this.dataMatrix); // TODO first check dataMatrix (why?)
        this.numberOfFingerprints = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
        this.thresholdForContrastEnhancement = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
    }
    //
    /**
     * Constructor.
     * Checked the dataMatrix for correctness.
     * Scaling factor and threshold are set.
     *
     * @param aFilePathName a text file containing fingerprints.The text file does not contain a header.
     * Each line of the text file contains a fingerprint whose components are separated by a separator.
     * @param aSeparator the separator to separate the components of the fingerprints in the text file.
     * @param aMaximumNumberOfEpochs maximum number of epochs that the system may use for convergence.
     * @throws Exception is thrown if the file cannot be read in.
     */
    public ART2aFloatClustering(String aFilePathName, int aMaximumNumberOfEpochs, char aSeparator) throws IllegalArgumentException, IOException {
        try {
            this.clusteringProcessLog = new ConcurrentLinkedQueue<>();
            this.clusteringResultLog = new ConcurrentLinkedQueue<>();
            if (aMaximumNumberOfEpochs <= 0) {
                throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
            }
            this.importDataMatrixFromFile(aFilePathName, aSeparator);
            this.checkDataMatrix(this.dataMatrix);
            this.numberOfFingerprints = this.dataMatrix.length;
            this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
            this.numberOfComponents = this.dataMatrix[0].length;
            this.scalingFactor = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
            this.thresholdForContrastEnhancement = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
        } catch(IOException anIOException) {
            throw anIOException;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Method checks whether the data matrix with the fingerprints is valid. Each row of the data matrix contains a
     * fingerprint.
     * The data matrix not contain negative inputs. The inputs or the vectors/fingerprints must all have the same length.
     * If the vector/fingerprint components are not between 0 and 1, they are scaled.
     *
     * @param aDataMatrix contains fingerprints.
     * @throws NullPointerException is thrown if aDataMatrix is null.
     * @throws IllegalArgumentException is thrown if the number of inputs/fingerprints are less than or equal to zero or
     * if the inputs are not the same length. It is also thrown if the fingerprints components contain negative values.
     */
    private void checkDataMatrix(float[][] aDataMatrix) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        if(aDataMatrix.length <= 0) {
            throw new IllegalArgumentException("The number of vectors must greater then 0 to cluster inputs");
        }
        int tmpNumberOfVectorComponents = aDataMatrix[0].length;
        float tmpCurrentMatrixComponent;
        float[] tmpRow;
        HashMap<float[],Integer> tmpFingerprintsForScalingToMatrixRow = new HashMap<>(aDataMatrix.length);
        ArrayList<Integer> tmpComponentsForScaling = new ArrayList<>();
        for(int i = 0; i < aDataMatrix.length; i++) {
            tmpRow = aDataMatrix[i];
            if(tmpNumberOfVectorComponents != tmpRow.length) {
                throw new IllegalArgumentException("The vectors must be have the same length!");
            }
            for(int j = 0; j < tmpRow.length; j++) {
                tmpCurrentMatrixComponent = tmpRow[j];
                if(tmpCurrentMatrixComponent > 1) {
                    tmpFingerprintsForScalingToMatrixRow.put(aDataMatrix[i],i);
                    tmpComponentsForScaling.add(i);
                }
                if(tmpCurrentMatrixComponent < 0) {
                    throw new IllegalArgumentException("Only positive values allowed.");
                }
            }
        }
        this.scaleInput(tmpFingerprintsForScalingToMatrixRow); // TODO replace method?
    }
    //
    /**
     * Method for scaling the fingerprints if they are not between 0 and 1.
     * Thus serves for the scaling of count fingerprints.
     *
     * @param aFingerprintToMatrixRowMap is a map that maps the fingerprints with components
     *                                   outside 0-1 to the row position in the matrix.
     */
    private void scaleInput(HashMap<float[],Integer> aFingerprintToMatrixRowMap) {
        for(float[] tmpScalingVector : aFingerprintToMatrixRowMap.keySet()) {
            float tmpFirstComponent = tmpScalingVector[0];
            for(float tmpComponentsOfScalingVector : tmpScalingVector) {
                if (tmpComponentsOfScalingVector > tmpFirstComponent) {
                    tmpFirstComponent = tmpComponentsOfScalingVector;
                }
            }
            for(int i = 0; i < tmpScalingVector.length; i++) {
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
     * The method used to randomize the inputs is the Fisher Yates method.
     * @return int[] in which the random order (indices) of the vectors is stored.
     * @author Thomas Kuhn
     */
    private int[] randomizeVectorIndices() {
        int[] tmpSampleVectorIndicesInRandomOrder = new int[this.numberOfFingerprints];
        for(int i = 0; i < this.numberOfFingerprints; i++) {
            tmpSampleVectorIndicesInRandomOrder[i] = i;
        }
        Random rnd = new Random(this.seed);
        this.seed++;
        int numberOfIterations = (this.numberOfFingerprints / 2) + 1;
        int tmpRandomIndex1;
        int tmpRandomIndex2;
        int tmpBuffer;
        for(int j = 0; j < numberOfIterations; j++) {
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
     * @param anInputVector vector whose length is calculated.
     * @return float vector length.
     * @throws ArithmeticException is thrown if the addition of the vector components results in zero.
     */
    private float getVectorLength(float[] anInputVector) throws ArithmeticException {
        float tmpVectorComponentsSqrtSum = 0;
        float tmpVectorLength;
        for(int i = 0; i< anInputVector.length; i++) {
            tmpVectorComponentsSqrtSum += anInputVector[i] * anInputVector[i];
        }
        if(tmpVectorComponentsSqrtSum == 0) {
            throw new ArithmeticException("Addition of the vector components results in zero!");
        }
        else {
            tmpVectorLength = (float) Math.sqrt(tmpVectorComponentsSqrtSum);
        }
        return tmpVectorLength;
    }
    //
    /**
     * Check after each epoch whether the system converges or not. If the system does not converge,
     * false is returned, otherwise true. To check convergence, the system checks whether the cluster
     * vectors are similar to the vectors of the previous epoch and also whether the cluster members have changed.
     *
     * @param aNumberOfDetectedClasses number of classes detected at the end of the epoch.
     * @param aVectorNew distribution of inputs to the existing clusters in the current epoch.
     * @param aVectorOld distribution of inputs to the existing clusters in the previous epoch.
     * @param aConvergenceEpoch number of current epochs.
     * @return true if system are converged false if not.
     * @throws RuntimeException is thrown if the system cannot converge within the specified maximum epochs.
     */
    private boolean checkConvergence(int aNumberOfDetectedClasses, int[] aVectorNew, int[] aVectorOld, int aConvergenceEpoch) throws RuntimeException {
        if(aConvergenceEpoch < this.maximumNumberOfEpochs) {
            boolean tmpConvergence = true;
            /* first check: It is checked whether the distribution of inputs among
            the existing clusters have changed compared to the previous epoch.
            The system is converged if there is no change between the previous and the current cluster occupation.
             */
            for(int i = 0; i < this.numberOfFingerprints; i++) {
                if (aVectorNew[i] != aVectorOld[i]) {
                    tmpConvergence = false;
                    break;
                }
            }
            /*
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
                    if (tmpScalarProductOfClassVector < this.requiredSimilarity) { // TODO
                        tmpConvergence = false;
                        break;
                    }
                }
            }
             */
            if(!tmpConvergence) {
                /*
                for (int i = 0; i < aNumberOfDetectedClasses; i++) {
                    for (int j = 0; j < this.numberOfComponents; j++) {
                        this.clusterMatrixPreviousEpoch[i][j] = this.clusterMatrix[i][j];
                    }
                }
                 */
                for(int tmpClusterDistribution = 0; tmpClusterDistribution < this.numberOfFingerprints; tmpClusterDistribution++) {
                    aVectorOld[tmpClusterDistribution] = aVectorNew[tmpClusterDistribution];
                }
            }
            return tmpConvergence;
        } else {
            throw new RuntimeException("Convergence failed"); // TODO own Exception, maybe ConvergenceFailedException?
        }
    }
    //
    /**
     * Method to import the fingerprints from a text file. The text file does not contain
     * a header and in each line a fingerprint is represented whose components are separated by a separator.
     *
     * @param aFilePath is a file path that contains fingerprints. It is important that the file is
     *                  correctly formatted so that it can be read in without problems.
     *                  Each line in the file should have a fingerprint, where each component is
     *                  separated by a separator.
     * @param aSeparator the separator to separate the components of the fingerprints in the text file.
     * @throws IOException is thrown if the file cannot be read in.
     * @throws NumberFormatException is thrown if cannot be converted to float.
     *
     */
    private void importDataMatrixFromFile(String aFilePath, char aSeparator) throws IOException, NumberFormatException {
        if(aFilePath == null || aFilePath.isEmpty() || aFilePath.isBlank()) {
            throw new IllegalArgumentException("aFileName is null or empty/blank.");
        }
        BufferedReader tmpFingerprintFileReader;
        tmpFingerprintFileReader = new BufferedReader(new FileReader(aFilePath));
        List<float[]> tmpFingerprintList = new ArrayList<>();
        String tmpFingerprintLine;
        int tmpDataMatrixRow = 0;
            while((tmpFingerprintLine = tmpFingerprintFileReader.readLine()) != null) {
                String[] tmpFingerprint = tmpFingerprintLine.split(String.valueOf(aSeparator));
                float[] tmpFingerprintFloatArray = new float[tmpFingerprint.length];
                try {
                    for(int i = 0; i < tmpFingerprint.length; i++) {
                        tmpFingerprintFloatArray[i] = Float.parseFloat(tmpFingerprint[i]);
                    }
                } catch(NumberFormatException anException) {
                    throw anException;
                }
                tmpDataMatrixRow++;
                tmpFingerprintList.add(tmpFingerprintFloatArray);
            }
        tmpFingerprintFileReader.close();
        this.dataMatrix = new float[tmpDataMatrixRow][tmpFingerprintList.get(0).length];
        for(int tmpCurrentMatrixRow = 0; tmpCurrentMatrixRow < tmpDataMatrixRow; tmpCurrentMatrixRow++) {
            this.dataMatrix[tmpCurrentMatrixRow] = tmpFingerprintList.get(tmpCurrentMatrixRow);
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Start clustering of fingerprints (count and bit) with ART-2a.
     * Implementation of the ART-2a algorithm.
     * The result of the clustering process (process result) is documented in detail in the text file Process_Log.txt
     * The result of the clustering (clustering result) is documented in the text file Result_Log.txt
     * Both files are located in the "Results_Clustering" folder under "src/test/resources".
     *
     * @param aVigilanceParameter Parameter to influence the number of classes. the vigilance
     *                            parameter is between 0 and 1
     * @return True if clustering was successful, false otherwise.
     * @throws Exception if the clustering process failed.
     */
    public boolean startArt2aClustering(float aVigilanceParameter) throws Exception {
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
            //Initialisation of the elements of the cluster matrix with init weight values
            for(int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
                tmpClusterMatrixRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
                tmpClusterMatrixRowOld = this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector];
                for (int tmpCurrentVectorComponentsInClusterMatrix = 0; tmpCurrentVectorComponentsInClusterMatrix < tmpClusterMatrixRow.length; tmpCurrentVectorComponentsInClusterMatrix++) {
                    tmpClusterMatrixRow[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
                    tmpClusterMatrixRowOld[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
                }
            }
            int tmpCurrentNumberOfEpochs = 1;
            this.clusteringResultLog.add("Vigilance parameter: " + aVigilanceParameter);
            // begin clustering process
            while(!tmpConvergence && tmpCurrentNumberOfEpochs <= this.maximumNumberOfEpochs) {
                this.clusteringProcessLog.add("ART-2a clustering result for vigilance parameter:" + aVigilanceParameter);
                this.clusteringProcessLog.add("Number of epochs: " + tmpCurrentNumberOfEpochs);
                this.clusteringProcessLog.add("");
                // randomize input vectors
                int[] tmpSampleVectorIndicesInRandomOrder = this.randomizeVectorIndices();
                for(int tmpCurrentInput = 0; tmpCurrentInput < this.numberOfFingerprints; tmpCurrentInput++) {
                    this.clusteringProcessLog.add("Input: " + tmpCurrentInput);
                    float[] tmpInputVector = new float[this.numberOfComponents];
                    boolean tmpCheckNullVector = true;
                    for(int tmpCurrentInputVectorComponents = 0; tmpCurrentInputVectorComponents < this.numberOfComponents; tmpCurrentInputVectorComponents++) {
                        tmpInputVector[tmpCurrentInputVectorComponents] = this.dataMatrix[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]][tmpCurrentInputVectorComponents]; // TODO also possible in another way?
                    }
                    // check input, if input is a null vector, do not cluster.
                    for(int tmpCheckInputComponentsToNull = 0; tmpCheckInputComponentsToNull < tmpInputVector.length; tmpCheckInputComponentsToNull++) {
                        if(tmpInputVector[tmpCheckInputComponentsToNull] != 0) {
                            tmpCheckNullVector = false;
                        }
                    }
                    if(tmpCheckNullVector) {
                        tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = -1;
                        this.clusteringProcessLog.add("This input is a null vector");
                    } else {
                        /* normalisation of the randomly selected input vector.
                        Subsequently, all components of the input vector are transformed
                        with a non-linear threshold function for contrast enhancement. */
                        tmpVectorLength1 = this.getVectorLength(tmpInputVector);
                        for(int tmpManipulateComponents = 0; tmpManipulateComponents < tmpInputVector.length; tmpManipulateComponents++) {
                            tmpInputVector[tmpManipulateComponents] *= (1 / tmpVectorLength1);
                            if(tmpInputVector[tmpManipulateComponents] <= this.thresholdForContrastEnhancement) {
                                tmpInputVector[tmpManipulateComponents] = 0;
                            }
                        }
                        //the transformed input vector is normalised again.
                        tmpVectorLength2 = this.getVectorLength(tmpInputVector);
                        for(int tmpNormalizeInputComponents = 0; tmpNormalizeInputComponents < tmpInputVector.length; tmpNormalizeInputComponents++) {
                            tmpInputVector[tmpNormalizeInputComponents] *= (1 / tmpVectorLength2);
                        }
                        //First pass, no clusters available, so the first cluster is created.
                        if(tmpNumberOfDetectedClusters == 0) {
                            this.clusterMatrix[0] = tmpInputVector;
                            tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters;
                            tmpNumberOfDetectedClusters++;
                            this.clusteringProcessLog.add("Cluster number: 0");
                            this.clusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                        } else {
                            // Cluster number is greater than or equal to 1, so a rho winner is determined as shown in the following steps.
                            float tmpSumCom = 0;
                            for(float tmpVectorComponentsOfNormalizeVector : tmpInputVector) {
                                tmpSumCom += tmpVectorComponentsOfNormalizeVector;
                            }
                            tmpWinnerClassIndex = tmpNumberOfDetectedClusters;
                            boolean tmpRhoWinner = true;
                            tmpRho = this.scalingFactor * tmpSumCom;
                            for(int tmpCurrentClusterMatrixRow = 0; tmpCurrentClusterMatrixRow < tmpNumberOfDetectedClusters; tmpCurrentClusterMatrixRow++) {
                                float[] tmpRow;
                                float tmpRho2 = 0;
                                tmpRow = this.clusterMatrix[tmpCurrentClusterMatrixRow];
                                for(int tmpElementsInRow = 0; tmpElementsInRow < this.numberOfComponents; tmpElementsInRow++) {
                                    tmpRho2 += tmpInputVector[tmpElementsInRow] * tmpRow[tmpElementsInRow];
                                }
                                if(tmpRho2 > tmpRho) {
                                    tmpRho = tmpRho2;
                                    tmpWinnerClassIndex = tmpCurrentClusterMatrixRow;
                                    tmpRhoWinner = false;
                                }
                            }
                            if(tmpRhoWinner == true || tmpRho < aVigilanceParameter) {
                                tmpNumberOfDetectedClusters++;
                                tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters - 1;
                                this.clusterMatrix[tmpNumberOfDetectedClusters - 1] = tmpInputVector;
                                this.clusteringProcessLog.add("Cluster number: " + (tmpNumberOfDetectedClusters - 1));
                                this.clusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            } else {
                                for(int m = 0; m < this.numberOfComponents; m++) {
                                    if(this.clusterMatrix[tmpWinnerClassIndex][m] <= this.thresholdForContrastEnhancement) {
                                        tmpInputVector[m] = 0;
                                    }
                                }
                                float tmpLength3 = this.getVectorLength(tmpInputVector);
                                float tmpFactor1 = this.DEFAULT_LEARNING_PARAMETER / tmpLength3;
                                float tmpFactor2 = 1 - this.DEFAULT_LEARNING_PARAMETER;
                                for(int tmpAdaptedComponents = 0; tmpAdaptedComponents < this.numberOfComponents; tmpAdaptedComponents++) {
                                    tmpInputVector[tmpAdaptedComponents] = tmpInputVector[tmpAdaptedComponents] * tmpFactor1 + tmpFactor2 * this.clusterMatrix[tmpWinnerClassIndex][tmpAdaptedComponents];
                                }
                                tmpLength4 = this.getVectorLength(tmpInputVector);
                                for(int i = 0; i < tmpInputVector.length; i++) {
                                    tmpInputVector[i] *= (1 / tmpLength4);
                                }
                                this.clusterMatrix[tmpWinnerClassIndex] = tmpInputVector;
                                tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpWinnerClassIndex;
                                this.clusteringProcessLog.add("Cluster number: " + tmpWinnerClassIndex);
                                this.clusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            }
                        }
                    }
                }
                // number of cluster members
                TreeMap<Integer, Integer> tmpClusterToMembersMap = new TreeMap<>();
                int i = 1;
                for(int tmpClusterMembers : tmpClassView) {
                    if (tmpClusterMembers == -1) {
                        continue;
                    }
                    if(tmpClusterToMembersMap.containsKey(tmpClusterMembers) == false) {
                        tmpClusterToMembersMap.put(tmpClusterMembers, i);
                    } else {
                        tmpClusterToMembersMap.put(tmpClusterMembers, tmpClusterToMembersMap.get(tmpClusterMembers) + 1);
                    }
                }
                this.vigilanceParameterToNumberOfEpochsAndNumberOfClusters = new HashMap<>(tmpClusterToMembersMap.size());
                this.numberOfEpochsToNumberOfClusters  = new HashMap<>();
                this.numberOfEpochsToNumberOfClusters.put(tmpCurrentNumberOfEpochs, tmpClusterToMembersMap.size());
                this.vigilanceParameterToNumberOfEpochsAndNumberOfClusters.put(aVigilanceParameter,this.numberOfEpochsToNumberOfClusters);
                tmpCurrentNumberOfEpochs++;
                // check the convergence. If the network is converged, tmpConvergence == true otherwise false
                tmpConvergence = this.checkConvergence(tmpNumberOfDetectedClusters, tmpClassView, tmpClassViewOld, tmpCurrentNumberOfEpochs);
                this.clusteringProcessLog.add("Cluster members: " + tmpClusterToMembersMap);
                this.clusteringProcessLog.add("Convergence status: " + tmpConvergence);
                this.clusteringProcessLog.add("---------------------------------------");
            }
            this.clusteringResultLog.add("Number of epochs: " + (tmpCurrentNumberOfEpochs-1));
            this.clusteringResultLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
            this.clusteringResultLog.add("---------------------------------------");
            return this.clusteringStatus = true;
        } catch(RuntimeException anRuntimeException) {
            throw anRuntimeException;
        } catch(Exception anException) {
            throw new Exception("The clustering process has failed.");
        }
    }
    //
    /**
     * Method returns the clustering process result.
     * Detailed documentation of the clustering result.
     *
     * @return log queue
     */
    public ConcurrentLinkedQueue<String> getClusteringProcessLog() {
        return this.clusteringProcessLog;
    }
    //
    /**
     * Method returns the clustering result.
     * For the respective vigilance parameters, only the number of
     * epochs and the number of detected clusters are returned.
     *
     * @return log queue
     */
    public ConcurrentLinkedQueue<String> getClusteringResultLog() {
        return this.clusteringResultLog;
    }
    //
    /**
     * Method returns the clustering result in the form of a map.
     * The key of the map contains the Vigilance parameter and the value of another
     * HashMap, which in turn represents as key the number of epochs and as value the number of clusters.
     *
     * @return HashMap
     */
    public HashMap getVigilanceParameterToNumberOfEpochsAndNumberOfClusters() {
        return this.vigilanceParameterToNumberOfEpochsAndNumberOfClusters;
    }

    /**
     * Method returns the clustering status.
     * If clustering was completed successfully, the status is set to true otherwise it is set to false.
     *
     * @return true or false
     */
    public boolean getClusteringStatus() {
        return this.clusteringStatus;
    }
    //
    /**
     *
     */
    public void calculateAngleBetweenCluster() {
        throw new UnsupportedOperationException();
    }
    //</editor-fold>
    //
}