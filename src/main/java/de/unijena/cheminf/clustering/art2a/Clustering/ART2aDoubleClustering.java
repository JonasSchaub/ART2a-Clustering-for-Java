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

package de.unijena.cheminf.clustering.art2a.Clustering;

import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClustering;
import de.unijena.cheminf.clustering.art2a.Result.ART2aDoubleClusteringResult;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * The class implements an ART-2A algorithm in double machine precision for fast,
 * stable unsupervised clustering for open categorical problems. The class is primarily intended for the
 * clustering of fingerprints. <br>
 * LITERATURE SOURCE:<br>
 * Primary : G.A. Carpenter,S. Grossberg and D.B. Rosen, Neural Networks 4 (1991) 493-504<br>
 * Secondary : D. Wienke et al., Chemometrics and Intelligent Laboratory Systems 24
 * (1994) 367-387<br>
 *
 * @author Betuel Sevindik
 */
public class ART2aDoubleClustering implements IART2aClustering {
    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * Matrix with all fingerprints to be clustered.
     * Each row of the matrix represents a fingerprint.
     */
    private double[][] dataMatrix;
    /**
     * Matrix contains all cluster vectors.
     */
    private double[][] clusterMatrix;
    /**
     * Matrix contains all cluster vectors of previous epoch. Is needed to check the convergence of
     * the system.
     */
    private double[][] clusterMatrixPreviousEpoch;
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
    private final double thresholdForContrastEnhancement;
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
    private final double scalingFactor;
    /**
     * The required similarity parameter represents the minimum value that must exist between the current
     * cluster vector and the previous cluster vector for the system to be considered convergent.
     * The clustering process continues until there are no more significant changes between
     * the cluster vectors of the current epoch and the previous epoch.
     */
    private final double requiredSimilarity;
    /**
     * Parameter to define the intensity of keeping the old class vector in mind
     * before the system adapts it to the new sample vector.
     */
    private final double learningParameter;
    /**
     * Default value of the learning parameter
     */
    private final double DEFAULT_LEARNING_PARAMETER = 0.01f;
    /**
     * Default value of the required similarity parameter
     */
    private final double REQUIRED_SIMILARITY = 0.99f;
    /**
     * Initial capacity value for maps
     */
    private final int INITIAL_CAPACITY_VALUE = Math.round((4/3) + 1);
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="private static final constants">
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ART2aDoubleClustering.class.getName());
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
    public ART2aDoubleClustering(double[][] aDataMatrix, int aMaximumNumberOfEpochs, float aVigilanceParameter, double aRequiredSimilarity, double aLearningParameter) throws IllegalArgumentException, NullPointerException {
        if(aDataMatrix == null) {
            ART2aDoubleClustering.LOGGER.severe("The data matrix is null.");
            throw new NullPointerException("aDataMatrix is null.");
        }
        if(aMaximumNumberOfEpochs <= 0) {
            ART2aDoubleClustering.LOGGER.severe("Number of epochs must be at least greater than zero.");
            throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
        }
        if(aVigilanceParameter < 0 || aVigilanceParameter > 1) {
            ART2aDoubleClustering.LOGGER.severe("The vigilance parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0 and less than 1.");
        }
        if(aRequiredSimilarity < 0 || aRequiredSimilarity > 1) {
            ART2aDoubleClustering.LOGGER.severe("The required similarity parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The required similarity parameter must be greater than 0 and less than 1.");
        }
        if(aLearningParameter < 0 || aLearningParameter > 1) {
            ART2aDoubleClustering.LOGGER.severe("The learning parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The learning parameter must be greater than 0 and less than 1.");
        }
        this.vigilanceParameter = aVigilanceParameter;
        this.requiredSimilarity = aRequiredSimilarity;
        this.learningParameter = aLearningParameter;
        this.dataMatrix =  this.checkDataMatrix(aDataMatrix);
        this.numberOfFingerprints = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = 1.0 / Math.sqrt(this.numberOfComponents + 1.0);
        this.thresholdForContrastEnhancement = 1.0 / Math.sqrt(this.numberOfComponents + 1.0);
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
    private double[][] checkDataMatrix(double[][] aDataMatrix) throws NullPointerException, IllegalArgumentException {
        if(aDataMatrix == null) {
            ART2aDoubleClustering.LOGGER.severe("aDataMatrix is null.");
            throw new IllegalArgumentException("aDataMatrix is null.");
        }
        if(aDataMatrix.length <= 0) {
            ART2aDoubleClustering.LOGGER.severe("The number of vectors must greater then 0 to cluster inputs.");
            throw new IllegalArgumentException("The number of vectors must greater then 0 to cluster inputs.");
        }
        int tmpNumberOfVectorComponents = aDataMatrix[0].length;
        double tmpCurrentVectorComponent;
        double[] tmpSingleFingerprint;
        HashMap<double[],Integer> tmpFingerprintsForScalingToMatrixRowMap = new HashMap<>(aDataMatrix.length * this.INITIAL_CAPACITY_VALUE, 0.75f);
        for(int i = 0; i < aDataMatrix.length; i++) {
            tmpSingleFingerprint = aDataMatrix[i];
            if(tmpNumberOfVectorComponents != tmpSingleFingerprint.length) {
                ART2aDoubleClustering.LOGGER.severe("The input vectors must be have the same length!");
                throw new IllegalArgumentException("The input vectors must be have the same length!");
            }
            for(int j = 0; j < tmpSingleFingerprint.length; j++) {
                tmpCurrentVectorComponent = tmpSingleFingerprint[j];
                if(tmpCurrentVectorComponent > 1) {
                    System.out.println(tmpCurrentVectorComponent +"---scale");
                    tmpFingerprintsForScalingToMatrixRowMap.put(aDataMatrix[i],i);
                }
                if(tmpCurrentVectorComponent < 0) {
                    ART2aDoubleClustering.LOGGER.severe("Only positive values allowed.");
                    throw new IllegalArgumentException("Only positive values allowed.");
                }
            }
        }
        if(!tmpFingerprintsForScalingToMatrixRowMap.isEmpty()) {
            this.scaleInput(tmpFingerprintsForScalingToMatrixRowMap, aDataMatrix); // TODO replace method?
        }
        return aDataMatrix;
    }
    //
    /**
     * Calculates the length of a vector. The length is needed for the normalisation of the vector.
     *
     * @param anInputVector vector whose length is calculated.
     * @return double vector length.
     * @throws ArithmeticException is thrown if the addition of the vector components results in zero.
     */
    private double getVectorLength (double[] anInputVector) throws ArithmeticException {
        double tmpVectorComponentsSqrtSum = 0;
        double tmpVectorLength;
        for (int i = 0; i < anInputVector.length; i++) {
            tmpVectorComponentsSqrtSum += anInputVector[i] * anInputVector[i];
        }
        if (tmpVectorComponentsSqrtSum == 0) {
            ART2aDoubleClustering.LOGGER.severe("Addition of the vector components result in zero!");
            throw new ArithmeticException("Addition of the vector components results in zero!");
        } else {
            tmpVectorLength = Math.sqrt(tmpVectorComponentsSqrtSum);
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
    private void scaleInput (HashMap <double[],Integer> aFingerprintToMatrixRowMap, double[][] aDataMatrix){
        for (double[] tmpScalingVector : aFingerprintToMatrixRowMap.keySet()) {
            double tmpFirstComponent = tmpScalingVector[0];
            for (double tmpComponentsOfScalingVector : tmpScalingVector) {
                if (tmpComponentsOfScalingVector > tmpFirstComponent) {
                    tmpFirstComponent = tmpComponentsOfScalingVector;
                }
            }
            System.out.println(tmpFirstComponent + "---tmpFirstcomponent");
            for (int i = 0; i < tmpScalingVector.length; i++) {
                double tmpScaledComponent = tmpScalingVector[i] / tmpFirstComponent;
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
     * Initialise the cluster matrix
     *
     */
    @Override
    public void initializeMatrices() {
        this.clusterMatrix = new double[this.numberOfFingerprints][this.numberOfComponents];
        this.clusterMatrixPreviousEpoch = new double[this.numberOfFingerprints][this.numberOfComponents];
    }
    //
    /**
     * The input vectors/fingerprints are randomized so that all input vectors can be clustered by random selection.
     * Here, the Fisher-Yates method is used to randomize the inputs.
     *
     * @return an array with vector indices in a random order
     * @author Thomas Kuhn
     *
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
     * Starts an ART-2A clustering algorithm in single machine precision.
     * The clustering process begins by randomly selecting an input vector/fingerprint from the data matrix.
     * After normalizing the first input vector, it is assigned to the first cluster. For all other subsequent
     * input vectors, they also undergo certain normalization steps. If there is sufficient similarity to an
     * existing cluster, they are assigned to that cluster. Otherwise, a new cluster is formed, and the
     * input is added to it. Null vectors are not clustered.
     *
     * @param aVigilanceParameter parameter that influence the number of clusters
     * @param aAddClusteringReport If the parameter == true, then all information about the clustering is written out
     *                             once in detail and once roughly additionally in text files.
     *                             If the parameter == false, the information is not written out in text files.
     * @return ART2aDoubleClusteringResult result class for clustering
     * @throws RuntimeException is thrown if the system cannot converge within the specified number of epochs.
     */
    @Override
    public ART2aDoubleClusteringResult startClustering(float aVigilanceParameter, boolean aAddClusteringReport) throws RuntimeException {
        this.clusteringStatus = false;
        //<editor-fold desc="Initialization steps for reporting the clustering results if aAddResultLog == true" defaultstate="collapsed">
        ConcurrentLinkedQueue<String> tmpClusteringProcessLog = null;
        ConcurrentLinkedQueue<String>tmpClusteringResultLog = null;
        if(aAddClusteringReport) {
            tmpClusteringProcessLog = new ConcurrentLinkedQueue<>();
            tmpClusteringResultLog = new ConcurrentLinkedQueue<>();
        }
        //</editor-fold>
        //<editor-fold desc="Initialization and declaration of some important variables" defaultstate="collapsed">
        this.initializeMatrices();
        this.seed = 1;
        double[] tmpClusterMatrixRow;
        double[] tmpClusterMatrixRowOld;
        double tmpInitialClusterVectorWeightValue = 1.0 / Math.sqrt(this.numberOfComponents);
        int tmpNumberOfDetectedClusters = 0;
        int[] tmpClusterOccupation = new int[this.numberOfFingerprints];
        double tmpVectorLengthForFirstNormalizationStep;
        double tmpVectorLengthAfterContrastEnhancement;
        double tmpRho;
        double tmpVectorLengthForModificationWinnerCluster;
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
        //<editor-fold desc="Reporting set up." defaultstate="collapsed">
        int tmpCurrentNumberOfEpochs = 0;
        if(aAddClusteringReport) {
            tmpClusteringResultLog.add("Vigilance parameter: " + aVigilanceParameter);
        }
        //</editor-fold>
        //<editor-fold desc="Start clustering process." defaultstate="collapsed">
        while(!tmpConvergence && tmpCurrentNumberOfEpochs <= this.maximumNumberOfEpochs) {
            //<editor-fold desc="Randomization input vectors and start reporting." defaultstate="collapsed">
            if(aAddClusteringReport) {
                tmpClusteringProcessLog.add("ART-2a clustering result for vigilance parameter:" + aVigilanceParameter);
                tmpClusteringProcessLog.add("Number of epochs: " + tmpCurrentNumberOfEpochs);
                tmpClusteringProcessLog.add("");
            }
            int[] tmpSampleVectorIndicesInRandomOrder = this.randomizeVectorIndices();
            //</editor-fold>
            //<editor-fold desc="Check current input vector for null vector." defaultstate="collapsed">
            for(int tmpCurrentInput = 0; tmpCurrentInput < this.numberOfFingerprints; tmpCurrentInput++) {
                double[] tmpInputVector = new double[this.numberOfComponents];
                boolean tmpCheckNullVector = true;
                for(int tmpCurrentInputVectorComponents = 0; tmpCurrentInputVectorComponents < this.numberOfComponents; tmpCurrentInputVectorComponents++) {
                    tmpInputVector[tmpCurrentInputVectorComponents] = this.dataMatrix[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]][tmpCurrentInputVectorComponents];
                    if(tmpInputVector[tmpCurrentInputVectorComponents] !=0) {
                        tmpCheckNullVector = false;
                    }
                }
                if(aAddClusteringReport) {
                    tmpClusteringProcessLog.add("Input: " + tmpCurrentInput + " / Vector " + tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]);
                }
                //<editor-fold desc="If the input vector is a null vector, it will not be clustered." defaultstate="collapsed">
                if(tmpCheckNullVector) {
                    tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = -1;
                    if(aAddClusteringReport) {
                        tmpClusteringProcessLog.add("This input is a null vector");
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
                        tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] =tmpNumberOfDetectedClusters;
                        tmpNumberOfDetectedClusters++;
                        if(aAddClusteringReport) {
                            tmpClusteringProcessLog.add("Cluster number: 0");
                            tmpClusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                        }
                    }
                    //</editor-fold>
                    else {
                        //<editor-fold desc="Cluster number is greater than or equal to 1, so a rho winner is determined as shown in the following steps." defaultstate="collapsed">
                        double tmpSumCom = 0;
                        for(double tmpVectorComponentsOfNormalizeVector : tmpInputVector) {
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
                            double[] tmpRow;
                            double tmpRhoForExistingClusters = 0;
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
                            System.out.println(tmpNumberOfDetectedClusters + "--------- anzahl cluster falls ein neues hinzugefügt wird"); // TODO delete
                            tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters - 1;
                            this.clusterMatrix[tmpNumberOfDetectedClusters - 1] = tmpInputVector;
                            if(aAddClusteringReport) {
                                tmpClusteringProcessLog.add("Cluster number: " + (tmpNumberOfDetectedClusters - 1));
                                tmpClusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
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
                            double tmpVectorLength = this.getVectorLength(tmpInputVector);
                            double tmpFactor1 = this.learningParameter / tmpVectorLength;
                            double tmpFactor2 = 1 - this.learningParameter;
                            for(int tmpAdaptedComponents = 0; tmpAdaptedComponents < this.numberOfComponents; tmpAdaptedComponents++) {
                                tmpInputVector[tmpAdaptedComponents] = tmpInputVector[tmpAdaptedComponents] * tmpFactor1 + tmpFactor2 * this.clusterMatrix[tmpWinnerClassIndex][tmpAdaptedComponents];
                            }
                            tmpVectorLengthForModificationWinnerCluster = this.getVectorLength(tmpInputVector);
                            for(int i = 0; i < tmpInputVector.length; i++) {
                                tmpInputVector[i] *= (1 / tmpVectorLengthForModificationWinnerCluster);
                            }
                            this.clusterMatrix[tmpWinnerClassIndex] = tmpInputVector;
                            tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpWinnerClassIndex;
                            if(aAddClusteringReport) {
                                tmpClusteringProcessLog.add("Cluster number: " + tmpWinnerClassIndex);
                                tmpClusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
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
            //<editor-fold desc="Last process report."
            if(aAddClusteringReport) {
                tmpClusteringProcessLog.add("Convergence status: " + tmpConvergence);
                tmpClusteringProcessLog.add("ClusterIndices" + java.util.Arrays.toString(tmpClusterOccupation)); // TODO delete
                tmpClusteringProcessLog.add("---------------------------------------");
            }
            //</editor-fold>
        }
        //</editor-fold>
        //<editor-fold desc="Last result report."
        if(aAddClusteringReport) {
            tmpClusteringResultLog.add("Number of epochs: " + (tmpCurrentNumberOfEpochs));
            tmpClusteringResultLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
            tmpClusteringResultLog.add("---------------------------------------");
        }
        //</editor-fold>
        System.out.println(java.util.Arrays.toString(tmpClusterOccupation) + "----class view"); // TODO delete
        for(int i = 0; i<10; i++) {
            System.out.println(java.util.Arrays.toString(this.clusterMatrix[i])+ "------ cluster matrix" +i); // TODO delete
        }
        //<editor-fold desc="Return object"
        if(!aAddClusteringReport) {
            System.out.println(tmpNumberOfDetectedClusters + "---wie viele Cluster gibt es?"); // TODO delete
            return new ART2aDoubleClusteringResult(aVigilanceParameter, this.clusterMatrix, tmpClusterOccupation, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters);
        } else {
            return new ART2aDoubleClusteringResult(aVigilanceParameter, this.clusterMatrix, tmpClusterOccupation, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters, tmpClusteringProcessLog, tmpClusteringResultLog);
        }
        //</editor-fold>

        /*
        catch(RuntimeException anRuntimeException) {
            throw anRuntimeException;
        }


        catch(Exception anException) {

          //  throw new Exception("The clustering process has failed.");
            throw anException;
        }

         */
    }
    //
    /**
     * At the end of each epoch, it is checked whether the system has converged or not. If the system has not
     * converged, a new epoch is performed, otherwise the clustering is completed successfully.
     *
     * @param aNumberOfDetectedClasses number of detected clusters per epoch.
     * @param aConvergenceEpoch current epochs number.
     * @return boolean true is returned if the system has converged.
     * False is returned if the system has not converged to the epoch.
     * @throws RuntimeException is thrown if the network does not converge within the
     * specified maximum number of epochs.
     */
    @Override
    public boolean checkConvergence(int aNumberOfDetectedClasses, int aConvergenceEpoch) throws RuntimeException {
        boolean tmpConvergence = true;
        double[] tmpRow;
        if(aConvergenceEpoch <= this.maximumNumberOfEpochs) {
            if (tmpConvergence) {
                // Check convergence by evaluating the similarity of the cluster vectors of this and the previous epoch.
                tmpConvergence = true;
                double tmpScalarProductOfClassVector;
                double[] tmpCurrentRowInClusterMatrix;
                double[] tmpPreviousEpochRow;
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
            if(!tmpConvergence) {
                for(int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
                    tmpRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
                    this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector] = tmpRow;
                }
            }
        } else {
            LOGGER.severe("Convergence failed for: " + this.vigilanceParameter );
            throw new RuntimeException("Convergence failed"); // TODO own Exception, maybe ConvergenceFailedException?
        }
        return tmpConvergence;
    }
}