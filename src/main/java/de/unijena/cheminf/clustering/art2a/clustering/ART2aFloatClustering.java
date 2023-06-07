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

package de.unijena.cheminf.clustering.art2a.clustering;

import de.unijena.cheminf.clustering.art2a.interfaces.IART2aClustering;
import de.unijena.cheminf.clustering.art2a.results.ART2aFloatClusteringResult;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class implements an ART-2A algorithm in single machine precision for fast,
 * stable unsupervised clustering for open categorical problems. The class is primarily intended for the
 * clustering of fingerprints. <br>
 * LITERATURE SOURCE:<br>
 * Primary : G.A. Carpenter,S. Grossberg and D.B. Rosen, Neural Networks 4 (1991) 493-504<br>
 * Secondary : D. Wienke et al., Chemometrics and Intelligent Laboratory Systems 24
 * (1994) 367-387<br>
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class ART2aFloatClustering implements IART2aClustering {
    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * Matrix with all fingerprints to be clustered.
     * Each row of the matrix represents a fingerprint.
     */
    private float[][] dataMatrix;
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
     * Queue for clustering process.
     */
    private ConcurrentLinkedQueue<String> clusteringProcess;
    /**
     * Queue for clustering result.
     */
    private ConcurrentLinkedQueue<String> clusteringResult;
    /**
     * The seed value for permutation of the vector field.
     */
    private int seed;
    /**
     * The vigilance parameter is between 0 and 1. The parameter influences the type of clustering.
     * A vigilance parameter close to 0 leads to a coarse clustering (few clusters) and a vigilance
     * parameter close to 1, on the other hand, leads to a fine clustering (many clusters).
     */
    private float vigilanceParameter;
    //</editor-fold>
    //
    //<editor-fold desc="private final variables" defaultstate="collapsed">
    /**
     * Threshold for contrast enhancement. If a vector/fingerprint component is below the threshold, it is set to zero.
     */
    private final float thresholdForContrastEnhancement;
    /**
     * Number of fingerprints to be clustered.
     */
    private final int numberOfFingerprints;
    /**
     * Dimensionality of the fingerprint.
     */
    private final int numberOfComponents;
    /**
     * The scaling factor controls the sensitivity of the algorithm to new inputs. A low scaling factor makes
     * the algorithm more sensitive to new inputs, while a high scaling factor decreases the sensitivity.
     * Thus, too low a scaling factor will cause a new input to be added to a new cluster, while
     * a high scaling factor will cause the new inputs to be added to existing clusters.<br>
     * Default value:  1 / Math.sqrt(numberOfComponents - 1)
     */
    private final float scalingFactor;
    /**
     * The required similarity parameter represents the minimum value that must exist between the current
     * cluster vector and the previous cluster vector for the system to be considered convergent.
     * The clustering process continues until there are no more significant changes between
     * the cluster vectors of the current epoch and the previous epoch.
     */
    private final float requiredSimilarity;
    /**
     * Parameter to define the intensity of keeping the old class vector in mind
     * before the system adapts it to the new sample vector.
     */
    private final float learningParameter;
    /**
     * Initial capacity value for maps
     */
    private final double INITIAL_CAPACITY_VALUE = 1.5;
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="private static final constants">
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ART2aFloatClustering.class.getName());
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor.
     * The data matrix with the input vectors/fingerprints is checked for correctness. Each row of the matrix
     * corresponds to an input vector/fingerprint. The vectors must not have components smaller than 0.
     * All input vectors must have the same length.
     * If there are components greater than 1, these input vectors are scaled so that all vector components
     * are between 0 and 1.
     * <br>
     * <u>WARNING</u>: If the data matrix consists only of null vectors, no clustering is possible,
     * because they do not contain any information that can be used for similarity evaluation.
     *
     * @param aDataMatrix matrix contains all inputs for clustering.
     * @param aMaximumNumberOfEpochs maximum number of epochs that the system may use for convergence.
     * @param aVigilanceParameter parameter to influence the number of clusters.
     * @param aRequiredSimilarity parameter indicating the minimum similarity between the current
     *                            cluster vectors and the previous cluster vectors.
     * @param aLearningParameter parameter to define the intensity of keeping the old class vector in mind
     *                           before the system adapts it to the new sample vector.
     * @throws IllegalArgumentException is thrown if the given arguments are invalid.
     * @throws NullPointerException is thrown if aDataMatrix is null.
     *
     */
    public ART2aFloatClustering(float[][] aDataMatrix, int aMaximumNumberOfEpochs, float aVigilanceParameter, float aRequiredSimilarity, float aLearningParameter) throws IllegalArgumentException, NullPointerException {
        if(aDataMatrix == null) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE,"The data matrix is null.");
            throw new NullPointerException("aDataMatrix is null.");
        }
        if(aMaximumNumberOfEpochs <= 0) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE,"Number of epochs must be at least greater than zero.");
            throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
        }
        if(aVigilanceParameter <= 0 || aVigilanceParameter >= 1) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE,"The vigilance parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0 and less than 1.");
        }
        if(aRequiredSimilarity < 0 || aRequiredSimilarity > 1) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE, "The required similarity parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The required similarity parameter must be greater than 0 and less than 1.");
        }
        if(aLearningParameter < 0 || aLearningParameter > 1) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE,"The learning parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The learning parameter must be greater than 0 and less than 1.");
        }
        this.vigilanceParameter = aVigilanceParameter;
        this.requiredSimilarity = aRequiredSimilarity;
        this.learningParameter = aLearningParameter;
        this.dataMatrix =  this.checkDataMatrix(aDataMatrix);
        this.numberOfFingerprints = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
        this.thresholdForContrastEnhancement = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="private methods">
    /**
     * The input data matrix with the input vectors/fingerprints is checked for correctness.
     * Accordingly, the input matrix must not contain any vectors that consist of components smaller than zero.
     * All input vectors must have the same length. Components larger than 1 are allowed, but are scaled in the
     * following steps so that all components of an input vector range between zero and 1.
     *
     * @param aDataMatrix the matrix contains all input vectors/fingerprints to be clustered.
     * @throws NullPointerException is thrown if the given data matrix is null.
     * @throws IllegalArgumentException is thrown if the input vectors are invalid
     */
    private float[][] checkDataMatrix(float[][] aDataMatrix) throws NullPointerException, IllegalArgumentException {
        if(aDataMatrix == null) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE,"aDataMatrix is null.");
            throw new IllegalArgumentException("aDataMatrix is null.");
        }
        if(aDataMatrix.length <= 0) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE, "The number of vectors must greater then 0 to cluster inputs.");
            throw new IllegalArgumentException("The number of vectors must greater then 0 to cluster inputs.");
        }
        int tmpNumberOfNullComponentsInDataMatrix = 0;
        int tmpNumberOfElementsInDataMatrix = aDataMatrix.length * aDataMatrix[0].length;
        int tmpNumberOfVectorComponents = aDataMatrix[0].length;
        float tmpCurrentVectorComponent;
        float[] tmpSingleFingerprint;
        HashMap<float[],Integer> tmpFingerprintsForScalingToMatrixRowMap = new HashMap<>((int) (aDataMatrix.length * this.INITIAL_CAPACITY_VALUE), 0.75f);
        for(int i = 0; i < aDataMatrix.length; i++) {
            tmpSingleFingerprint = aDataMatrix[i];
            if(tmpNumberOfVectorComponents != tmpSingleFingerprint.length) {
                ART2aFloatClustering.LOGGER.log(Level.SEVERE,"The input vectors must be have the same length!");
                throw new IllegalArgumentException("The input vectors must be have the same length!");
            }
            for(int j = 0; j < tmpSingleFingerprint.length; j++) {
                tmpCurrentVectorComponent = tmpSingleFingerprint[j];
                if(tmpCurrentVectorComponent > 1) {
                    tmpFingerprintsForScalingToMatrixRowMap.put(aDataMatrix[i],i);
                }
                if(tmpCurrentVectorComponent < 0) {
                    ART2aFloatClustering.LOGGER.log(Level.SEVERE, "Only positive values allowed.");
                    throw new IllegalArgumentException("Only positive values allowed.");
                }
                if(tmpCurrentVectorComponent == 0) {
                    tmpNumberOfNullComponentsInDataMatrix++;
                }
            }
            if(tmpNumberOfNullComponentsInDataMatrix == tmpNumberOfElementsInDataMatrix) {
                ART2aFloatClustering.LOGGER.log(Level.SEVERE, "All vectors are null vectors. Clustering not possible.");
                throw new IllegalArgumentException("All vectors are null vectors. Clustering not possible");
            }
        }
        if(!tmpFingerprintsForScalingToMatrixRowMap.isEmpty()) {
            this.scaleInput(tmpFingerprintsForScalingToMatrixRowMap, aDataMatrix); // TODO replace method? why?
        }
        return aDataMatrix;
    }
    //
    /**
     * Calculates the length of a vector. The length is needed for the normalisation of the vector.
     *
     * @param anInputVector vector whose length is calculated.
     * @return float vector length.
     * @throws ArithmeticException is thrown if the addition of the vector components results in zero.
     */
    private float getVectorLength (float[] anInputVector) throws ArithmeticException {
        float tmpVectorComponentsSqrtSum = 0;
        float tmpVectorLength;
        for (int i = 0; i < anInputVector.length; i++) {
            tmpVectorComponentsSqrtSum += anInputVector[i] * anInputVector[i];
        }
        if (tmpVectorComponentsSqrtSum == 0) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE, "Addition of the vector components result in zero!");
            throw new ArithmeticException("Addition of the vector components results in zero!");
        } else {
            tmpVectorLength = (float) Math.sqrt(tmpVectorComponentsSqrtSum);
        }
        return tmpVectorLength;
    }
    //
    /**
     * Method for scaling the input vectors/fingerprints if they are not between 0 and 1.
     * Thus serves for the scaling of count fingerprints.
     *
     * @param aFingerprintToMatrixRowMap is a map that maps the fingerprints with components
     *                                   outside 0-1 to the row position in the matrix.
     * @param aDataMatrix the matrix contains all input vectors/fingerprints to be clustered.
     */
    private void scaleInput (HashMap <float[],Integer> aFingerprintToMatrixRowMap, float[][] aDataMatrix){
        for (float[] tmpScalingVector : aFingerprintToMatrixRowMap.keySet()) {
            float tmpFirstComponent = tmpScalingVector[0];
            for (float tmpComponentsOfScalingVector : tmpScalingVector) {
                if (tmpComponentsOfScalingVector > tmpFirstComponent) {
                    tmpFirstComponent = tmpComponentsOfScalingVector;
                }
            }
            for (int i = 0; i < tmpScalingVector.length; i++) {
                float tmpScaledComponent = tmpScalingVector[i] / tmpFirstComponent;
                tmpScalingVector[i] = tmpScaledComponent;
                // this.dataMatrix[aFingerprintToMatrixRowMap.get(tmpScalingVector)] = tmpScalingVector;
                aDataMatrix[aFingerprintToMatrixRowMap.get(tmpScalingVector)] = tmpScalingVector;
            }
        }
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="overriden public methods">
    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeMatrices() {
        this.clusterMatrix = new float[this.numberOfFingerprints][this.numberOfComponents];
        this.clusterMatrixPreviousEpoch = new float[this.numberOfFingerprints][this.numberOfComponents];
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] randomizeVectorIndices() {
        int[] tmpSampleVectorIndicesInRandomOrder = new int[this.numberOfFingerprints];
        for(int i = 0; i < this.numberOfFingerprints; i++) {
            tmpSampleVectorIndicesInRandomOrder[i] = i;
        }
        Random tmpRnd = new Random(this.seed);
        this.seed++;
        int tmpNumberOfIterations = (this.numberOfFingerprints / 2) + 1;
        int tmpRandomIndex1;
        int tmpRandomIndex2;
        int tmpBuffer;
        for(int j = 0; j < tmpNumberOfIterations; j++) {
            tmpRandomIndex1 = (int) (this.numberOfFingerprints * tmpRnd.nextDouble());
            tmpRandomIndex2 = (int) (this.numberOfFingerprints * tmpRnd.nextDouble());

            tmpBuffer = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1];
            tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1] = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2];
            tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2] = tmpBuffer;
        }
        return tmpSampleVectorIndicesInRandomOrder;
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public ART2aFloatClusteringResult startClustering(float aVigilanceParameter, boolean aAddClusteringResultFileAdditionally) throws RuntimeException  {
        this.clusteringStatus = false;
        //<editor-fold desc="Initialization steps for writing the clustering results in text files if aAddResultLog == true" defaultstate="collapsed">
        this.clusteringProcess = null;
        this.clusteringResult = null;
        if(aAddClusteringResultFileAdditionally) {
            this.clusteringProcess = new ConcurrentLinkedQueue<>();
            this.clusteringResult = new ConcurrentLinkedQueue<>();
        }
        //</editor-fold>
        //<editor-fold desc="Initialization and declaration of some important variables" defaultstate="collapsed">
        this.initializeMatrices();
        this.seed = 1;
        float[] tmpClusterMatrixRow;
        float[] tmpClusterMatrixRowOld;
        float tmpInitialClusterVectorWeightValue = (float) (1.0 / Math.sqrt(this.numberOfComponents));
        int tmpNumberOfDetectedClusters = 0;
        int[] tmpClusterOccupation = new int[this.numberOfFingerprints];
        float tmpVectorLengthForFirstNormalizationStep;
        float tmpVectorLengthAfterContrastEnhancement;
        float tmpRho;
        float tmpVectorLengthForModificationWinnerCluster;
        int tmpWinnerClassIndex;
        boolean tmpConvergence = false;
        // </editor-fold>
        //<editor-fold desc="Filling the cluster matrix with the calculated initial weight." defaultstate="collapsed">
        for(int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
            tmpClusterMatrixRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
            tmpClusterMatrixRowOld = this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector];
            for (int tmpCurrentVectorComponentsInClusterMatrix = 0; tmpCurrentVectorComponentsInClusterMatrix < tmpClusterMatrixRow.length; tmpCurrentVectorComponentsInClusterMatrix++) {
                tmpClusterMatrixRow[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialClusterVectorWeightValue;
                tmpClusterMatrixRowOld[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialClusterVectorWeightValue;
            }
        }
        //</editor-fold>
        //<editor-fold desc="Clustering results in text file set up." defaultstate="collapsed">
        int tmpCurrentNumberOfEpochs = 0;
        if(aAddClusteringResultFileAdditionally) {
            this.clusteringResult.add("Vigilance parameter: " + aVigilanceParameter);
        }
        //</editor-fold>
        //<editor-fold desc="Start clustering process." defaultstate="collapsed">
        while(!tmpConvergence && tmpCurrentNumberOfEpochs <= this.maximumNumberOfEpochs) {
            //<editor-fold desc="Randomization input vectors and start saving the clustering results to text files if desired." defaultstate="collapsed">
            if(aAddClusteringResultFileAdditionally) {
                this.clusteringProcess.add("ART-2a clustering result for vigilance parameter:" + aVigilanceParameter);
                this.clusteringProcess.add("Number of epochs: " + tmpCurrentNumberOfEpochs);
                this.clusteringProcess.add("");
            }
            int[] tmpSampleVectorIndicesInRandomOrder = this.randomizeVectorIndices();
            //</editor-fold>
            //<editor-fold desc="Check current input vector for null vector." defaultstate="collapsed">
            for(int tmpCurrentInput = 0; tmpCurrentInput < this.numberOfFingerprints; tmpCurrentInput++) {
                float[] tmpInputVector = new float[this.numberOfComponents];
                boolean tmpCheckNullVector = true;
                for(int tmpCurrentInputVectorComponents = 0; tmpCurrentInputVectorComponents < this.numberOfComponents; tmpCurrentInputVectorComponents++) {
                    tmpInputVector[tmpCurrentInputVectorComponents] = this.dataMatrix[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]][tmpCurrentInputVectorComponents];
                    if(tmpInputVector[tmpCurrentInputVectorComponents] !=0) {
                        tmpCheckNullVector = false;
                    }
                }
                if(aAddClusteringResultFileAdditionally) {
                    this.clusteringProcess.add("Input: " + tmpCurrentInput + " / Vector " + tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]);
                }
                //<editor-fold desc="If the input vector is a null vector, it will not be clustered." defaultstate="collapsed">
                if(tmpCheckNullVector) {
                    tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = -1;
                    if(aAddClusteringResultFileAdditionally) {
                        this.clusteringProcess.add("This input is a null vector");
                    }
                }
                //</editor-fold>
                else {
                    //<editor-fold desc=" normalisation of the randomly selected input vector.
                    //                    Subsequently, all components of the input vector are transformed
                    //                    with a non-linear threshold function for contrast enhancement." defaultstate="collapsed">
                    tmpVectorLengthForFirstNormalizationStep = this.getVectorLength(tmpInputVector);
                    for(int tmpManipulateComponents = 0; tmpManipulateComponents < tmpInputVector.length; tmpManipulateComponents++) {
                        tmpInputVector[tmpManipulateComponents] *= (1 / tmpVectorLengthForFirstNormalizationStep);
                        if(tmpInputVector[tmpManipulateComponents] <= this.thresholdForContrastEnhancement) {
                            tmpInputVector[tmpManipulateComponents] = 0;
                        }
                    }
                    //</editor-fold>
                    //<editor-fold desc="the transformed input vector is normalised again." defaultstate="collapsed">
                    tmpVectorLengthAfterContrastEnhancement = this.getVectorLength(tmpInputVector);
                    for(int tmpNormalizeInputComponents = 0; tmpNormalizeInputComponents < tmpInputVector.length; tmpNormalizeInputComponents++) {
                        tmpInputVector[tmpNormalizeInputComponents] *= (1 / tmpVectorLengthAfterContrastEnhancement);
                    }
                    //</editor-fold>
                    //<editor-fold desc="First pass, no clusters available, so the first cluster is created." defaultstate="collapsed">
                    if(tmpNumberOfDetectedClusters == 0) {
                        this.clusterMatrix[0] = tmpInputVector;
                        tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters;
                        tmpNumberOfDetectedClusters++;
                        if(aAddClusteringResultFileAdditionally) {
                            this.clusteringProcess.add("Cluster number: 0");
                            this.clusteringProcess.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                        }
                    }
                    //</editor-fold>
                    else {
                        //<editor-fold desc="Cluster number is greater than or equal to 1, so a rho winner is determined as shown in the following steps." defaultstate="collapsed">
                        float tmpSumCom = 0;
                        for(float tmpVectorComponentsOfNormalizeVector : tmpInputVector) {
                            tmpSumCom += tmpVectorComponentsOfNormalizeVector;
                        }
                        tmpWinnerClassIndex = tmpNumberOfDetectedClusters;
                        boolean tmpRhoWinner = true;
                        //<editor-fold desc="Cluster number is greater than or equal to 1, so a rho winner is determined as shown in the following steps."
                        //</editor-fold>
                        //<editor-fold desc="Calculate first rho value."
                        tmpRho = this.scalingFactor * tmpSumCom;
                        //</editor-fold>
                        //<editor-fold desc="Calculation of the 2nd rho value and comparison of the two rho values to determine the rho winner."
                        for(int tmpCurrentClusterMatrixRow = 0; tmpCurrentClusterMatrixRow < tmpNumberOfDetectedClusters; tmpCurrentClusterMatrixRow++) {
                            float[] tmpRow;
                            float tmpRhoForExistingClusters = 0;
                            tmpRow = this.clusterMatrix[tmpCurrentClusterMatrixRow];
                            for(int tmpElementsInRow = 0; tmpElementsInRow < this.numberOfComponents; tmpElementsInRow++) {
                                tmpRhoForExistingClusters += tmpInputVector[tmpElementsInRow] * tmpRow[tmpElementsInRow];
                            }
                            if(tmpRhoForExistingClusters > tmpRho) {
                                tmpRho = tmpRhoForExistingClusters;
                                tmpWinnerClassIndex = tmpCurrentClusterMatrixRow;
                                tmpRhoWinner = false;
                            }
                        }
                        //</editor-fold>
                        //<editor-fold desc="Deciding whether the input fits into an existing cluster or whether a new cluster must be formed."
                        //</editor-fold>
                        //<editor-fold desc="Input does not fit in existing clusters. A new cluster is formed and the input vector is put into the new cluster vector."
                        if(tmpRhoWinner == true || tmpRho < aVigilanceParameter) {
                            tmpNumberOfDetectedClusters++;
                            tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters - 1;
                            this.clusterMatrix[tmpNumberOfDetectedClusters - 1] = tmpInputVector;
                            if(aAddClusteringResultFileAdditionally) {
                                this.clusteringProcess.add("Cluster number: " + (tmpNumberOfDetectedClusters - 1));
                                this.clusteringProcess.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            }
                        }
                        //</editor-fold>
                        //<editor-fold desc="The input fits into one cluster. The number of clusters is not increased. But the winning cluster vector is modified."
                        else {
                            for(int i = 0; i < this.numberOfComponents; i++) {
                                if(this.clusterMatrix[tmpWinnerClassIndex][i] <= this.thresholdForContrastEnhancement) {
                                    tmpInputVector[i] = 0;
                                }
                            }
                            //<editor-fold desc="Modification of the winner cluster vector."
                            float tmpVectorLength = this.getVectorLength(tmpInputVector);
                            float tmpFactor1 = this.learningParameter / tmpVectorLength;
                            float tmpFactor2 = 1 - this.learningParameter;
                            for(int tmpAdaptedComponents = 0; tmpAdaptedComponents < this.numberOfComponents; tmpAdaptedComponents++) {
                                tmpInputVector[tmpAdaptedComponents] = tmpInputVector[tmpAdaptedComponents] * tmpFactor1 + tmpFactor2 * this.clusterMatrix[tmpWinnerClassIndex][tmpAdaptedComponents];
                            }
                            tmpVectorLengthForModificationWinnerCluster = this.getVectorLength(tmpInputVector);
                            for(int i = 0; i < tmpInputVector.length; i++) {
                                tmpInputVector[i] *= (1 / tmpVectorLengthForModificationWinnerCluster);
                            }
                            this.clusterMatrix[tmpWinnerClassIndex] = tmpInputVector;
                            tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpWinnerClassIndex;
                            if(aAddClusteringResultFileAdditionally) {
                                this.clusteringProcess.add("Cluster number: " + tmpWinnerClassIndex);
                                this.clusteringProcess.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            }
                            //</editor-fold>
                        }
                        //</editor-fold>
                    }
                }
                //</editor-fold>
            }
            //</editor-fold>
            //<editor-fold desc="Check the convergence. If the network is converged, tmpConvergence == true otherwise false."
            tmpConvergence = this.checkConvergence(tmpNumberOfDetectedClusters, tmpCurrentNumberOfEpochs);
            tmpCurrentNumberOfEpochs++;
            //</editor-fold>
            //<editor-fold desc="Last clustering process input."
            if(aAddClusteringResultFileAdditionally) {
                this.clusteringProcess.add("Convergence status: " + tmpConvergence);
                this.clusteringProcess.add("---------------------------------------");
            }
            //</editor-fold>
        }
        //</editor-fold>
        //<editor-fold desc="Last clustering result input."
        if(aAddClusteringResultFileAdditionally) {
            this.clusteringResult.add("Number of epochs: " + (tmpCurrentNumberOfEpochs));
            this.clusteringResult.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
            this.clusteringResult.add("Convergence status: " + tmpConvergence);
            this.clusteringResult.add("---------------------------------------");
        }
        //</editor-fold>
        //<editor-fold desc="Return object"
        if(!aAddClusteringResultFileAdditionally) {
            return new ART2aFloatClusteringResult(aVigilanceParameter, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters, tmpClusterOccupation, tmpConvergence, this.clusterMatrix, this.dataMatrix);
        } else {
            return new ART2aFloatClusteringResult(aVigilanceParameter, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters,this.clusteringProcess, this.clusteringResult, tmpClusterOccupation, tmpConvergence, this.clusterMatrix, this.dataMatrix);
        }
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkConvergence(int aNumberOfDetectedClasses, int aConvergenceEpoch) { // throws RuntimeException
       // boolean tmpConvergence = true;
        boolean tmpConvergence = false;
        float[] tmpRow;
        if(aConvergenceEpoch < this.maximumNumberOfEpochs) {
          //  if (tmpConvergence) {
                // Check convergence by evaluating the similarity of the cluster vectors of this and the previous epoch.
                tmpConvergence = true;
                float tmpScalarProductOfClassVector;
                float[] tmpCurrentRowInClusterMatrix;
                float[] tmpPreviousEpochRow;
                for (int i = 0; i < aNumberOfDetectedClasses; i++) {
                    tmpScalarProductOfClassVector = 0;
                    tmpCurrentRowInClusterMatrix = this.clusterMatrix[i];
                    tmpPreviousEpochRow = this.clusterMatrixPreviousEpoch[i];
                    for (int j = 0; j < this.numberOfComponents; j++) {
                        tmpScalarProductOfClassVector += tmpCurrentRowInClusterMatrix[j] * tmpPreviousEpochRow[j];
                    }
                    if (tmpScalarProductOfClassVector < this.requiredSimilarity) {
                        tmpConvergence = false;
                        break;
                    }
                }
          //  }
            if(!tmpConvergence) {
                for(int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
                    tmpRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
                    this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector] = tmpRow;
                }
            }
        } else {
            this.clusteringProcess.add("Convergence failed for: " + this.vigilanceParameter);
            this.clusteringProcess.add("Please increase the maximum number of epochs to get reliable results.");
            this.clusteringResult.add("Convergence failed for: " + this.vigilanceParameter);
            this.clusteringResult.add("Please increase the maximum number of epochs to get reliable results.");
            ART2aFloatClustering.LOGGER.log(Level.INFO,"Convergence failed for: " + this.vigilanceParameter +"\nPlease take the results of clustering of the vigilance parameter " + this.vigilanceParameter +" with caution.");
           // throw new RuntimeException("Convergence failed");
        }
        return tmpConvergence;
    }
    // </editor-fold>
}
