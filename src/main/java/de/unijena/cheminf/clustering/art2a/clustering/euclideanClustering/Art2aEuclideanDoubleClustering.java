/*
 * ART2a Clustering for Java
 * Copyright (C) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
 *
 * Source code is available at <https://github.com/JonasSchaub/ART2a-Clustering-for-Java>
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

package de.unijena.cheminf.clustering.art2a.clustering.euclideanClustering;

import de.unijena.cheminf.clustering.art2a.exceptions.ConvergenceFailedException;
import de.unijena.cheminf.clustering.art2a.interfaces.euclideanClusteringInterfaces.IArt2aEuclideanClustering;
import de.unijena.cheminf.clustering.art2a.interfaces.euclideanClusteringInterfaces.IArt2aEuclideanClusteringResult;
import de.unijena.cheminf.clustering.art2a.results.euclideanClusteringResult.Art2aEuclideanDoubleClusteringResult;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * This class implements a modified version of the ART-2a clustering algorithm in double
 * machine precision. The clustering is done by comparing the Euclidean distances of the input vectors
 * and allows a fast, stable and unsupervised clustering for open categorical problems.
 * This class intends the clustering of fingerprints.
 * Literature:
 * D. Wienke,"Neural resonance and adaption. Towards nature's principles in artificial pattern recognition",1993
 *
 * @author Zeynep Dagtekin
 * @version 1.0.0.0
 */
public class Art2aEuclideanDoubleClustering implements IArt2aEuclideanClustering {
    //<editor-fold desc="Private class variables" defaultstate="collapsed">
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
     * Matrix contains all cluster vectors of previous epoch. It is needed to check the convergence of
     * the system.
     */
    private double[][] clusterMatrixPreviousEpoch;
    /**
     * Queue of type String for a clustering process. Queues or thread securities are used
     * but these should not be used by more than one thread.
     */
    private ConcurrentLinkedQueue<String> clusteringProcess;
    /**
     * Queue of type String for a clustering result. Queues or thread securities are used
     * but these should not be used by more than one thread.
     */
    private ConcurrentLinkedQueue<String> clusteringResult;
    /**
     * The seed value for permutation of the vector field.
     */
    private int seed;
    //</editor-fold>
    //
    //<editor-fold desc="Private final variables" defaultstate="collapsed">
    /**
     * The vigilance parameter is above 0. The parameter influences the type of clustering.
     */
    private final double vigilanceParameter;
    /**
     * Maximum number of epochs the system may need to converge.
     */
    private final int maximumNumberOfEpochs;
    /**
     * Threshold for contrast enhancement. If a vector/fingerprint component is below the threshold, it is set to zero.
     */
    private final double thresholdForContrastEnhancement;
    /**
     * Number of fingerprints to be clustered.
     */
    private final int numberOfInputVectors;
    /**
     * Dimensionality of the fingerprint.
     */
    private final int numberOfComponents;
    /**
     * The scaling factor controls the sensitivity of the algorithm to new inputs. A low scaling factor
     * increases the sensitivity to new inputs, while a high scaling factor decreases it.
     * Therefore, a low scaling factor will cause a new input to be added to a new cluster, while
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
     * Parameter to define the intensity of keeping the old cluster vector in mind
     * before the system adapts it to the new sample vector.
     */
    private final double learningParameter;
    //</editor-fold>
    //
     // <editor-fold defaultstate="collapsed" desc="Private static final constants">
    /**
     * Logger of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Art2aEuclideanDoubleClustering.class.getName());
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor.
     * The data matrix with the input vectors/fingerprints is checked for correctness. Each row of the matrix
     * corresponds to an input vector/fingerprint. The vectors must not have components lower than 0. Since the
     * Euclidean distance is analysed, the steps to scale the vectors to a unit length are not needed.
     * <br>
     * <u>WARNING</u>: If the data matrix consists only of null vectors, no clustering is possible,
     * because they do not contain any information that can be used for the evaluation of similarities.
     *
     * @param aDataMatrix            matrix contains all inputs for clustering.
     * @param aMaximumNumberOfEpochs maximum number of epochs that the system may use for convergence.
     * @param aVigilanceParameter    parameter to influence the number of clusters.
     * @param aRequiredSimilarity    parameter indicating the minimum similarity between the current
     *                               cluster vectors and the previous cluster vectors.
     * @param aLearningParameter     parameter to define the intensity of keeping the old cluster vector in mind
     *                               before the system adapts it to the new sample vector.
     * @throws IllegalArgumentException is thrown if the given arguments are invalid.
     * @throws NullPointerException     is thrown if aDataMatrix is null.
     */
    public Art2aEuclideanDoubleClustering(double[][] aDataMatrix, int aMaximumNumberOfEpochs, double aVigilanceParameter,
                                 double aRequiredSimilarity, double aLearningParameter)
            throws IllegalArgumentException, NullPointerException {
        if(aDataMatrix == null) {
            throw new NullPointerException("aDataMatrix is null.");
        }
        if(aMaximumNumberOfEpochs <= 0) {
            throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
        }
        if(aVigilanceParameter < 0.0 ) {
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0.");
        }
        if(aRequiredSimilarity < 0.0 || aRequiredSimilarity > 1.0) {
            throw new IllegalArgumentException("The required similarity parameter must be between 0 and 1.");
        }
        if(aLearningParameter < 0.0 || aLearningParameter > 1.0) {
            throw new IllegalArgumentException("The learning parameter must be greater than 0 and smaller than 1.");
        }
        this.vigilanceParameter = aVigilanceParameter;
        this.requiredSimilarity = aRequiredSimilarity;
        this.learningParameter = aLearningParameter;
        this.dataMatrix =  this.getCheckedAndScaledDataMatrix(aDataMatrix);
        this.numberOfInputVectors = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = 1.0 / Math.sqrt(this.numberOfComponents + 1.0);
        this.thresholdForContrastEnhancement = 1.0 / Math.sqrt(this.numberOfComponents + 1.0);
    }
    //</editor-fold>
    //
      // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * The input data matrix with the input vectors/fingerprints is checked for correctness.
     * Accordingly, the input matrix must not contain any vectors that consist of components smaller than 0.
     * Components larger than 1 are allowed, so that all components of an input vector range between 0 and 1.
     *
     * @param aDataMatrix the matrix contains all input vectors/fingerprints to be clustered.
     * @return valid data matrix
     * @throws NullPointerException is thrown if the given data matrix is null.
     * @throws IllegalArgumentException is thrown if the input vectors are invalid
     */
    private double[][] getCheckedAndScaledDataMatrix(double[][] aDataMatrix) throws NullPointerException,
            IllegalArgumentException {
        // content of aDataMatrix
        if(aDataMatrix == null) {
            throw new IllegalArgumentException("aDataMatrix is null.");
        }
        if(aDataMatrix.length <= 0) {
            throw new IllegalArgumentException("The number of vectors must be greater than 0 to cluster inputs.");
        }
        int tmpNumberOfNullComponentsInDataMatrix = 0;
        int tmpNumberOfElementsInDataMatrix = aDataMatrix.length * aDataMatrix[0].length;
        int tmpNumberOfVectorComponents = aDataMatrix[0].length;
        double[] tmpSingleFingerprint;
        for(int i = 0; i < aDataMatrix.length; i++) {
            tmpSingleFingerprint = aDataMatrix[i];
            if(tmpNumberOfVectorComponents != tmpSingleFingerprint.length) {
                throw new IllegalArgumentException("All input vectors must have the same dimension!");
            }
            if(tmpNumberOfNullComponentsInDataMatrix == tmpNumberOfElementsInDataMatrix) {
                throw new IllegalArgumentException("All vectors are null vectors. Clustering not possible.");
            }
        }
        System.out.println("Datamatrix: " + Arrays.deepToString(aDataMatrix));
        return aDataMatrix;
    }
    //
    /**
     * At the end of each epoch, it is checked whether the system has converged or not. If the system has not
     * converged, a new epoch is performed, otherwise the clustering is completed successfully.
     * The system is considered converged if the cluster vectors of the current epoch and the previous epoch
     * have a minimum similarity. The default value of the similarity parameter is 0.99, but it can also be set
     * by the user when initialising the clustering.
     *
     * @param aNumberOfDetectedClasses number of detected clusters per epoch.
     * @param aConvergenceEpoch current epochs number.
     * @return boolean true is returned if the system has converged.
     * False is returned if the system has not converged to the epoch.
     * @throws ConvergenceFailedException is thrown, when convergence fails.
     */
    private boolean isConverged(int aNumberOfDetectedClasses, int aConvergenceEpoch) //distance
            throws ConvergenceFailedException {
        boolean tmpIsConverged;
        double[] tmpRow;
        if(aConvergenceEpoch < this.maximumNumberOfEpochs) {
            // Check convergence by evaluating the similarity of the cluster vectors of this and the previous epoch.
            tmpIsConverged = true;
            double tmpDistanceOfClassVector;
            double tmpSpatialShift = 0;
            double tmpSumOfRowComponents;
            double tmpSumOfClassVector;
            double[] tmpCurrentRowInClusterMatrix;
            double[] tmpPreviousEpochOtherRow;
            double[] tmpPreviousEpochRow;
            double[] tmpEuclideanDistanceArray = new double[aNumberOfDetectedClasses];
            // Finding the Maximum Distance.
            for (int i = 0; i < aNumberOfDetectedClasses; i++) {
                for (int j = i + 1; j < aNumberOfDetectedClasses; j++) {
                    tmpPreviousEpochRow = this.clusterMatrixPreviousEpoch[i];
                    tmpPreviousEpochOtherRow = this.clusterMatrixPreviousEpoch[j];
                    tmpDistanceOfClassVector = 0.0;
                    for (int tmpComponentsOfEpochRow = 0; tmpComponentsOfEpochRow < this.numberOfComponents; tmpComponentsOfEpochRow++) {
                        tmpSumOfClassVector = tmpPreviousEpochRow[tmpComponentsOfEpochRow] - tmpPreviousEpochOtherRow[tmpComponentsOfEpochRow];
                        tmpDistanceOfClassVector += tmpSumOfClassVector * tmpSumOfClassVector;
                        //tmpEuclideanDistanceArray[tmpComponentsOfEpochRow] = tmpDistanceOfClassVector;
                    }
                    tmpEuclideanDistanceArray[j] = tmpDistanceOfClassVector;
                }
                int tmpGreatestDistanceIndex = 0;
                for (int tmpPossibleMaxDistance = 0; tmpPossibleMaxDistance < tmpEuclideanDistanceArray.length; tmpPossibleMaxDistance++) {
                    if (tmpEuclideanDistanceArray[tmpPossibleMaxDistance] > tmpEuclideanDistanceArray[tmpGreatestDistanceIndex]) {
                        tmpGreatestDistanceIndex = tmpPossibleMaxDistance;//max distance 100% dissimilarity, therefore min distance 0% dissimilarity
                    }
                }
                //for instance if two cars are 60% distant, they are not similar, they have to be minimum 40% far away from each other
                //Maximum distance that is allowed in order to converge
                for (int tmpVectorRow = 0; tmpVectorRow < aNumberOfDetectedClasses; tmpVectorRow++) {
                    tmpCurrentRowInClusterMatrix = this.clusterMatrix[tmpVectorRow];
                    tmpPreviousEpochRow = this.clusterMatrixPreviousEpoch[tmpVectorRow];
                    for (int tmpVectorComponent = 0; tmpVectorComponent < this.numberOfComponents; tmpVectorComponent++) {
                        tmpSumOfRowComponents = tmpPreviousEpochRow[tmpVectorComponent] - tmpCurrentRowInClusterMatrix[tmpVectorComponent];
                        tmpSpatialShift += tmpSumOfRowComponents * tmpSumOfRowComponents;
                    }
                }
                double tmpThresholdForConvergence = tmpGreatestDistanceIndex * this.requiredSimilarity;
                if (tmpSpatialShift > tmpThresholdForConvergence) {
                    tmpIsConverged = false;
                    break;
                }
            }
            if(!tmpIsConverged) {
                for(int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length;
                    tmpCurrentClusterMatrixVector++) {
                    tmpRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
                    this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector] = Arrays.copyOf(tmpRow, tmpRow.length);
                }
            }
        } else {
            throw new ConvergenceFailedException(String.format("Convergence failed for vigilance parameter: %2f"
                    ,this.vigilanceParameter));
        }
        return tmpIsConverged;
    }
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Overriden public methods">
    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeMatrices() {
        this.clusterMatrix = new double[this.numberOfInputVectors][this.numberOfComponents];
        this.clusterMatrixPreviousEpoch = new double[this.numberOfInputVectors][this.numberOfComponents];
        System.out.println("initial cluster: " + Arrays.deepToString(this.clusterMatrix));
    }
    //
    /**
     * {@inheritDoc}
     *
     * @author Thomas Kuhn
     */
    @Override
    public int[] getRandomizeVectorIndices() {
        int[] tmpSampleVectorIndicesInRandomOrder = new int[this.numberOfInputVectors];
        for(int i = 0; i < this.numberOfInputVectors; i++) {
            tmpSampleVectorIndicesInRandomOrder[i] = i;
        }
        Random tmpRnd = new Random(this.seed);
        this.seed++;
        int tmpNumberOfIterations = (this.numberOfInputVectors >> 1) + 1;
        int tmpRandomIndex1;
        int tmpRandomIndex2;
        int tmpBuffer;
        for(int j = 0; j < tmpNumberOfIterations; j++) {
            tmpRandomIndex1 = (int) (this.numberOfInputVectors * tmpRnd.nextDouble());
            tmpRandomIndex2 = (int) (this.numberOfInputVectors * tmpRnd.nextDouble());

            tmpBuffer = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1];
            tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1] =
                    tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2];
            tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2] = tmpBuffer;
        }
        System.out.println("random vector order: " + Arrays.toString(tmpSampleVectorIndicesInRandomOrder));
        return tmpSampleVectorIndicesInRandomOrder;
    }
    //
    /**
     * {@inheritDoc}
     * Starts the clustering in double machine precision.
     */
    @Override
    public IArt2aEuclideanClusteringResult getClusterResult(boolean anIsClusteringResultExported, int aSeedValue) throws ConvergenceFailedException {
        //<editor-fold desc="Initialization steps for writing the clustering results in text files if aAddResultLog == true" defaultstate="collapsed">
        this.clusteringProcess = null;
        this.clusteringResult = null;
        if(anIsClusteringResultExported) {
            this.clusteringProcess = new ConcurrentLinkedQueue<>();
            this.clusteringResult = new ConcurrentLinkedQueue<>();
        }
        //</editor-fold>
        //<editor-fold desc="Initialization and declaration of some important variables" defaultstate="collapsed">
        this.initializeMatrices();
        this.seed = aSeedValue;
        double[] tmpClusterMatrixRow;
        double[] tmpClusterMatrixRowOld;
        double tmpInitialClusterVectorWeightValue = 1.0 / Math.sqrt(this.numberOfComponents);
        int tmpNumberOfDetectedClusters = 0;
        int[] tmpClusterOccupation = new int[this.numberOfInputVectors];
        double tmpRho;
        int tmpWinnerClusterIndex;
        boolean tmpIsSystemConverged = false;
        // </editor-fold>
        //<editor-fold desc="Filling the cluster matrix with the calculated initial weight." defaultstate="collapsed">
        for(int tmpCurrentClusterMatrixVectorIndex = 0; tmpCurrentClusterMatrixVectorIndex < this.clusterMatrix.length;
            tmpCurrentClusterMatrixVectorIndex++) {
            tmpClusterMatrixRow = this.clusterMatrix[tmpCurrentClusterMatrixVectorIndex];
            tmpClusterMatrixRowOld = this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVectorIndex];
            for (int tmpCurrentVectorComponentsInClusterMatrixIndex = 0;
                 tmpCurrentVectorComponentsInClusterMatrixIndex < tmpClusterMatrixRow.length;
                 tmpCurrentVectorComponentsInClusterMatrixIndex++) {
                tmpClusterMatrixRow[tmpCurrentVectorComponentsInClusterMatrixIndex] = tmpInitialClusterVectorWeightValue;
                tmpClusterMatrixRowOld[tmpCurrentVectorComponentsInClusterMatrixIndex] = tmpInitialClusterVectorWeightValue;
            }
            System.out.println("matrix after initial weight value: " + Arrays.toString(tmpClusterMatrixRow));
        }
        //</editor-fold>
        //<editor-fold desc="Clustering results in text files set up." defaultstate="collapsed">
        int tmpCurrentNumberOfEpochs = 0;
        if(anIsClusteringResultExported) {
            this.clusteringResult.add(String.format("Vigilance parameter: %2f",this.vigilanceParameter));
        }
        //</editor-fold>
        //<editor-fold desc="Start clustering process." defaultstate="collapsed">
        while(!tmpIsSystemConverged && tmpCurrentNumberOfEpochs <= this.maximumNumberOfEpochs) {
            //<editor-fold desc="Randomization input vectors and start saving the clustering results to text files if desired." defaultstate="collapsed">
            if(anIsClusteringResultExported) {
                this.clusteringProcess.add(String.format("Art-2a clustering result for vigilance parameter: %2f",this.vigilanceParameter));
                this.clusteringProcess.add(String.format("Number of epochs: %d",tmpCurrentNumberOfEpochs));
                this.clusteringProcess.add("");
            }
            int[] tmpSampleVectorIndicesInRandomOrder = this.getRandomizeVectorIndices();
            //</editor-fold>
            //<editor-fold desc="Check current input vector for null vector." defaultstate="collapsed">
            for(int tmpCurrentInput = 0; tmpCurrentInput < this.numberOfInputVectors; tmpCurrentInput++) {
                double[] tmpInputVector = new double[this.numberOfComponents];
                boolean tmpIsNullVector = true;
                for(int tmpCurrentInputVectorComponentsIndex = 0; tmpCurrentInputVectorComponentsIndex < this.numberOfComponents;
                    tmpCurrentInputVectorComponentsIndex++ ) {
                    tmpInputVector[tmpCurrentInputVectorComponentsIndex] =
                            this.dataMatrix[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]][tmpCurrentInputVectorComponentsIndex];
                    if(tmpInputVector[tmpCurrentInputVectorComponentsIndex] !=0.0) {
                        tmpIsNullVector = false;
                    }
                    System.out.println("input vector: " + Arrays.toString(new double[]{tmpInputVector[tmpCurrentInputVectorComponentsIndex]}));
                }
                if(anIsClusteringResultExported) {
                    this.clusteringProcess.add(String.format("Input: %d / Vector %d", tmpCurrentInput,
                            tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]));
                }
                if(tmpIsNullVector) {
                    tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = -1;
                    if(anIsClusteringResultExported) {
                        this.clusteringProcess.add("This input is a null vector");
                    }
                    System.out.println("This is a null vector");
                }
                //</editor-fold>
                else {
                    //<editor-fold desc="Subsequently, all components of the input vector are transformed
                    //with a non-linear threshold function for contrast enhancement." defaultstate="collapsed">
                    for(int tmpManipulateComponentsIndex = 0; tmpManipulateComponentsIndex < tmpInputVector.length;
                        tmpManipulateComponentsIndex++) {
                        if(tmpInputVector[tmpManipulateComponentsIndex] <= this.thresholdForContrastEnhancement) {
                            tmpInputVector[tmpManipulateComponentsIndex] = 0.0;
                        }
                        System.out.println("Vector after contrast enhancement: " + tmpInputVector[tmpManipulateComponentsIndex]);
                    }
                    //</editor-fold>
                    //<editor-fold desc="First pass, no clusters available, so the first cluster is created." defaultstate="collapsed">
                    if(tmpNumberOfDetectedClusters == 0) {
                        this.clusterMatrix[0] = tmpInputVector;
                        tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] =
                                tmpNumberOfDetectedClusters;
                        tmpNumberOfDetectedClusters++;
                        if(anIsClusteringResultExported) {
                            this.clusteringProcess.add("Cluster number: 0");
                            this.clusteringProcess.add(String.format("Number of detected clusters: %d",tmpNumberOfDetectedClusters));
                        }
                        System.out.println("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                    }
                    //</editor-fold>
                    else {
                        tmpWinnerClusterIndex = tmpNumberOfDetectedClusters;
                        boolean tmpIsMatchingClusterAvailable = true;
                        //<editor-fold desc="Cluster number is greater than or equal to 1, so a rho winner is determined as shown in the following steps." defaultstate="collapsed">
                        //sum of scaling factor and non normalized components!
                        tmpRho = 0.0;
                        double tmpDifferenceOfScalingFactorAndInput;
                        for (int tmpInputVectorComponent = 0; tmpInputVectorComponent < this.numberOfComponents; tmpInputVectorComponent++) {
                            tmpDifferenceOfScalingFactorAndInput = this.scalingFactor - tmpInputVector[tmpInputVectorComponent];
                            tmpRho += tmpDifferenceOfScalingFactorAndInput * tmpDifferenceOfScalingFactorAndInput;
                            System.out.println("inside loop tmpRho: " + tmpRho);
                        }
                        System.out.println("Rho: " + tmpRho);
                        System.out.println("Winner Cluster index: " + tmpWinnerClusterIndex);
                        //tmpWinnerClusterIndex = tmpNumberOfDetectedClusters;
                        //boolean tmpIsMatchingClusterAvailable = true;
                        //<editor-fold desc="Calculate first rho value."
                        //tmpRho += tmpDifferenceOfScalingFactorAndInput * tmpDifferenceOfScalingFactorAndInput;
                        //</editor-fold>
                        //<editor-fold desc="Calculation of the 2nd rho value and comparison of the two rho values to determine the rho winner."
                        for (int tmpCurrentClusterMatrixRowIndex = 0; tmpCurrentClusterMatrixRowIndex < tmpNumberOfDetectedClusters;
                             tmpCurrentClusterMatrixRowIndex++) {
                            double[] tmpRow;
                            double tmpRhoForExistingClustersSquared = 0.0;
                            tmpRow = this.clusterMatrix[tmpCurrentClusterMatrixRowIndex];
                            for (int tmpElementsInRowIndex = 0; tmpElementsInRowIndex < this.numberOfComponents; tmpElementsInRowIndex++) {
                                double tmpRhoForExistingClusters = tmpInputVector[tmpElementsInRowIndex] - tmpRow[tmpElementsInRowIndex];
                                tmpRhoForExistingClustersSquared += tmpRhoForExistingClusters * tmpRhoForExistingClusters;
                                //tmpRhoForExistingClustersSquared += tmpRhoForExistingClusters * tmpRhoForExistingClusters;
                                //tmpDistance += Math.pow(tmpInputVector[tmpElementsInRowIndex] - tmpRow[tmpElementsInRowIndex], 2);
                            }
                            System.out.println("Rho for existing clusters: " + tmpRhoForExistingClustersSquared);
                            if (tmpRhoForExistingClustersSquared > tmpRho) {
                                tmpRho = tmpRhoForExistingClustersSquared;
                                tmpWinnerClusterIndex = tmpCurrentClusterMatrixRowIndex;
                                tmpIsMatchingClusterAvailable = false;
                            }
                            System.out.println("updated tmpRho:" + tmpRho);
                            System.out.println("is matching cluster available: " + tmpIsMatchingClusterAvailable);
                            System.out.println("updates winner cluster index: " + tmpWinnerClusterIndex);
                        }
                        //</editor-fold>
                        //<editor-fold desc="Deciding whether the input fits into an existing cluster or whether a new cluster must be formed."
                        //</editor-fold>
                        //<editor-fold desc="Input does not fit in existing clusters. A new cluster is formed and the input vector is put into the new cluster vector."
                        if(tmpIsMatchingClusterAvailable || tmpRho < this.vigilanceParameter) {
                            tmpNumberOfDetectedClusters++;
                            tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] =
                                    tmpNumberOfDetectedClusters - 1;
                            this.clusterMatrix[tmpNumberOfDetectedClusters - 1] = tmpInputVector;
                            if(anIsClusteringResultExported) {
                                this.clusteringProcess.add(String.format("Cluster number: %d",(tmpNumberOfDetectedClusters - 1)));
                                this.clusteringProcess.add(String.format("Number of detected clusters: %d",tmpNumberOfDetectedClusters));
                            }
                        }
                        //</editor-fold>
                        //<editor-fold desc="The input fits into one cluster. The number of clusters is not increased. But the winning cluster vector is modified."
                        else {
                            for(int i = 0; i < this.numberOfComponents; i++) {
                                if(this.clusterMatrix[tmpWinnerClusterIndex][i] <= this.thresholdForContrastEnhancement) {
                                    tmpInputVector[i] = 0.0;
                                }
                            }
                            //<editor-fold desc="Modification of the winner cluster vector."
                            double tmpFactor = 1.0 - this.learningParameter;
                            for(int tmpAdaptedComponentsIndex = 0; tmpAdaptedComponentsIndex < this.numberOfComponents;
                                tmpAdaptedComponentsIndex++) {
                                tmpInputVector[tmpAdaptedComponentsIndex] =
                                        (tmpInputVector[tmpAdaptedComponentsIndex] * this.learningParameter) + (tmpFactor *
                                                this.clusterMatrix[tmpWinnerClusterIndex][tmpAdaptedComponentsIndex]);
                            }
                            this.clusterMatrix[tmpWinnerClusterIndex] = tmpInputVector;
                            tmpClusterOccupation[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] =
                                    tmpWinnerClusterIndex;
                            if(anIsClusteringResultExported) {
                                clusteringProcess.add(String.format("Cluster number: %d",tmpWinnerClusterIndex));
                                clusteringProcess.add(String.format("Number of detected clusters: %d",tmpNumberOfDetectedClusters));
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
            tmpIsSystemConverged = this.isConverged(tmpNumberOfDetectedClusters, tmpCurrentNumberOfEpochs);
            tmpCurrentNumberOfEpochs++;
            //</editor-fold>
            //<editor-fold desc="Last clustering process input."
            if(anIsClusteringResultExported) {
                clusteringProcess.add(String.format("Convergence status: %b",tmpIsSystemConverged));
                clusteringProcess.add("---------------------------------------");
            }
            //</editor-fold>
        }
        //</editor-fold>
        //<editor-fold desc="Last clustering result input."
        if(anIsClusteringResultExported) {
            this.clusteringResult.add(String.format("Number of epochs: %d",tmpCurrentNumberOfEpochs));
            this.clusteringResult.add(String.format("Number of detected clusters: %d",tmpNumberOfDetectedClusters));
            this.clusteringResult.add(String.format("Convergence status: %b",tmpIsSystemConverged));
            this.clusteringResult.add("---------------------------------------");
        }
        //</editor-fold>
        //<editor-fold desc="Return object"
        if(!anIsClusteringResultExported) {
            return new Art2aEuclideanDoubleClusteringResult(this.vigilanceParameter, tmpCurrentNumberOfEpochs,
                    tmpNumberOfDetectedClusters, tmpClusterOccupation, this.clusterMatrix, this.dataMatrix);
        } else {
            return new Art2aEuclideanDoubleClusteringResult(this.vigilanceParameter, tmpCurrentNumberOfEpochs,
                    tmpNumberOfDetectedClusters, tmpClusterOccupation, this.clusterMatrix, this.dataMatrix,
                    this.clusteringProcess, this.clusteringResult);
        }
        //</editor-fold>
    }
    // </editor-fold>
}
