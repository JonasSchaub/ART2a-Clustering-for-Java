package de.unijena.cheminf.clustering.art2a.Clustering;

import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClustering;
import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClusteringResult;
import de.unijena.cheminf.clustering.art2a.Result.ART2aDoubleClusteringResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ART2aDoubleClustering implements IART2aClustering {
    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * The matrix containing all fingerprints to be clustered.
     * Each row of the matrix represents a single fingerprint.
     */
    private double[][] dataMatrix;
    /**
     * Threshold for contrast enhancement. If a vector/fingerprint component is below the threshold, it is set to zero.
     */
    private double thresholdForContrastEnhancement;
    /**
     * Number of fingerprints to be clustered.
     */
    private int numberOfFingerprints;
    /**
     * Dimensionality of the fingerprint.
     */
    private int numberOfComponents;
    /**
     * The scaling factor used for the modified vectors.
     */
    private double scalingFactor;
    /**
     * Matrix contains all cluster vectors.
     */
    private double[][] clusterMatrix;
    /**
     * Matrix contains all cluster vectors of previous epoch. This is used to check the convergence of the system.
     */
    private double[][] clusterMatrixPreviousEpoch;
    /**
     * Maximum number of epochs the system may need to converge.
     */
    private int maximumNumberOfEpochs;
    /**
     * The seed value for permutation of the vector field.
     */
    private int seed;
    /**
     * Parameter to influence the number of classes.
     */
    private double vigilanceParameter;
    private double requiredSimilarity;
    private double learningParameter;
    /**
     * Default learning parameter to modify the cluster after a new input has been added.
     */
    private final double DEFAULT_LEARNING_PARAMETER = 0.01;
    /**
     * Default required similarity. Minimum similarity between the cluster vectors that must be present.
     */
    private final double REQUIRED_SIMILARITY = 0.99;
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ART2aDoubleClustering.class.getName());
    //</editor-fold>
    //
    //<editor-fold desc="constructor" defaultstate="collapsed">
    /**
     * Constructor.
     *
     * @param aDataMatrix The matrix containing all fingerprints to be clustered.
     *                    Each row of the matrix represents a single fingerprint.
     * @param aMaximumNumberOfEpochs The maximum number of epochs that the system may need to converge.
     *                               Must be greater than zero.
     * @param aVigilanceParameter The vigilance parameter used in the clustering process.
     * @throws IllegalArgumentException is thrown if the maximum number of epochs is not greater than zero,
     * or if the vigilance parameter is not between 0 and 1.
     * @throws NullPointerException is thrown if the data matrix is null.
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
        this.learningParameter = aLearningParameter;
        this.requiredSimilarity = aRequiredSimilarity;
       // this.requiredSimilarity = aRequiredSimilarity;
       // this.vigilanceParameter = aVigilanceParameter;
        // this.dataMatrix = aDataMatrix;
        // this.checkDataMatrix(this.dataMatrix); // TODO first check dataMatrix (why?)
        this.dataMatrix =  this.checkDataMatrix(aDataMatrix);
        this.numberOfFingerprints = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = 1.0 / Math.sqrt(this.numberOfComponents + 1.0);
        this.thresholdForContrastEnhancement = 1.0 / Math.sqrt(this.numberOfComponents + 1.0);
    }
    private double[][] checkDataMatrix(double[][] aDataMatrix) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        if(aDataMatrix.length <= 0) {
            ART2aDoubleClustering.LOGGER.severe("The number of vectors must greater then 0 to cluster inputs.");
            throw new IllegalArgumentException("The number of vectors must greater then 0 to cluster inputs.");
        }
        int tmpNumberOfVectorComponents = aDataMatrix[0].length;
        double tmpCurrentMatrixComponent;
        double[] tmpRow;
        HashMap<double[],Integer> tmpFingerprintsForScalingToMatrixRow = new HashMap<>(aDataMatrix.length);
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
                    ART2aDoubleClustering.LOGGER.severe("Only positive values allowed.");
                    throw new IllegalArgumentException("Only positive values allowed.");
                }
            }
        }
        if(!tmpFingerprintsForScalingToMatrixRow.isEmpty()) {
            this.scaleInput(tmpFingerprintsForScalingToMatrixRow, aDataMatrix); // TODO replace method?
        }
        return aDataMatrix;
    }
    private void scaleInput (HashMap <double[],Integer> aFingerprintToMatrixRowMap, double[][] aDataMatrix){
        for (double[] tmpScalingVector : aFingerprintToMatrixRowMap.keySet()) {
            double tmpFirstComponent = tmpScalingVector[0];
            for (double tmpComponentsOfScalingVector : tmpScalingVector) {
                if (tmpComponentsOfScalingVector > tmpFirstComponent) {
                    tmpFirstComponent = tmpComponentsOfScalingVector;
                }
            }
            for (int i = 0; i < tmpScalingVector.length; i++) {
                double tmpScaledComponent = tmpScalingVector[i] / tmpFirstComponent;
                tmpScalingVector[i] = tmpScaledComponent;
                aDataMatrix[aFingerprintToMatrixRowMap.get(tmpScalingVector)] = tmpScalingVector;
            }
        }
    }
    private double getVectorLength (double[] anInputVector) throws ArithmeticException {
        double tmpVectorComponentsSqrtSum = 0;
        double tmpVectorLength;
        for (int i = 0; i < anInputVector.length; i++) {
            tmpVectorComponentsSqrtSum += anInputVector[i] * anInputVector[i];
        }
        if (tmpVectorComponentsSqrtSum == 0) {
            throw new ArithmeticException("Addition of the vector components results in zero!");
        } else {
            tmpVectorLength = Math.sqrt(tmpVectorComponentsSqrtSum);
        }
        return tmpVectorLength;
    }
    @Override
    public void initializeMatrices() {
        this.clusterMatrix = new double[this.numberOfFingerprints][this.numberOfComponents];
        this.clusterMatrixPreviousEpoch = new double[this.numberOfFingerprints][this.numberOfComponents];
    }

    @Override
    public int[] randomizeVectorIndices() {
        int[] tmpSampleVectorIndicesInRandomOrder = new int[this.numberOfFingerprints];
        for(int i = 0; i < this.numberOfFingerprints; i++) {
            tmpSampleVectorIndicesInRandomOrder[i] = i;
        }
        Random tmpRandom = new Random(this.seed);
        this.seed++;
        int tmpNumberOfIterations = (this.numberOfFingerprints / 2) + 1;
        int tmpRandomIndex1;
        int tmpRandomIndex2;
        int tmpBuffer;
        for(int j = 0; j < tmpNumberOfIterations; j++) {
            tmpRandomIndex1 = (int) (this.numberOfFingerprints * tmpRandom.nextDouble());
            tmpRandomIndex2 = (int) (this.numberOfFingerprints * tmpRandom.nextDouble());

            tmpBuffer = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1];
            tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1] = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2];
            tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2] = tmpBuffer;
        }
        return tmpSampleVectorIndicesInRandomOrder;
    }

    /**
     *
     * @param aVigilanceParameter
     * @param aAddResultLog
     * @return
     * @throws RuntimeException
     */
    @Override
    public IART2aClusteringResult startClustering(float aVigilanceParameter, boolean aAddResultLog) throws RuntimeException {
        //<editor-fold desc="initialization steps for logging the clustering results if aAddResultLog = true" defaultstate="collapsed">
        ConcurrentLinkedQueue<String> tmpClusteringProcessLog = null;
        ConcurrentLinkedQueue<String>tmpClusteringResultLog = null;
        if(aAddResultLog) {
            tmpClusteringProcessLog = new ConcurrentLinkedQueue<>();
            tmpClusteringResultLog = new ConcurrentLinkedQueue<>();
        }
        //</editor-fold>
        //<editor-fold desc="initialization and declaration of some necessary clustering elements" defaultstate="collapsed">
        this.initializeMatrices();
        this.seed = 1;
        double[] tmpClusterMatrixRow;
        double[] tmpClusterMatrixRowOld;
        int tmpNumberOfDetectedClusters = 0;
        int[] tmpClassView = new int[this.numberOfFingerprints];
        double tmpVectorLength1;
        double tmpVectorLength2;
        double tmpRho;
        double tmpVectorLength3;
        int tmpWinnerClassIndex;
        boolean tmpConvergence = false;
        //</editor-fold>
        //<editor-fold desc="initialization cluster matrix elements with init weight values" defaultstate="collapsed">
        double tmpInitialWeightValue = 1.0 / Math.sqrt(this.numberOfComponents);
        for(int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
            tmpClusterMatrixRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
            tmpClusterMatrixRowOld = this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector];
            for (int tmpCurrentVectorComponentsInClusterMatrix = 0; tmpCurrentVectorComponentsInClusterMatrix < tmpClusterMatrixRow.length; tmpCurrentVectorComponentsInClusterMatrix++) {
                tmpClusterMatrixRow[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
                tmpClusterMatrixRowOld[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
            }
        }
        //</editor-fold>
        //<editor-fold desc="begin clustering process" defaultstate="collapsed">
        if(aAddResultLog) {
            tmpClusteringResultLog.add("Vigilance parameter: " + aVigilanceParameter);
        }
        int tmpCurrentNumberOfEpochs = 0;
        while(!tmpConvergence && tmpCurrentNumberOfEpochs <= this.maximumNumberOfEpochs) {
            if(aAddResultLog) {
                tmpClusteringProcessLog.add("ART-2a clustering result for vigilance parameter:" + aVigilanceParameter);
                tmpClusteringProcessLog.add("Number of epochs: " + tmpCurrentNumberOfEpochs);
                tmpClusteringProcessLog.add("");
            }
            //</editor-fold>
            //<editor-fold desc="randomly select any input vector from the data matrix. This step is repeated until the randomly selected vector is not a null vector." defaultstate="collapsed">
            int[] tmpSampleVectorIndicesInRandomOrder = this.randomizeVectorIndices();
            for(int tmpCurrentInput = 0; tmpCurrentInput < this.numberOfFingerprints; tmpCurrentInput++) {
                double[] tmpInputVector = new double[this.numberOfComponents];
                boolean tmpCheckNullVector = true;
                for(int tmpCurrentInputVectorComponents = 0; tmpCurrentInputVectorComponents < this.numberOfComponents; tmpCurrentInputVectorComponents++) {
                    tmpInputVector[tmpCurrentInputVectorComponents] = this.dataMatrix[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]][tmpCurrentInputVectorComponents];
                    if(tmpInputVector[tmpCurrentInputVectorComponents] !=0) {
                        tmpCheckNullVector = false;// TODO also possible in another way?
                    }
                }
                if(aAddResultLog) {
                    tmpClusteringProcessLog.add("Input: " + tmpCurrentInput + " / Vector " + tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]);
                }
                if(tmpCheckNullVector) {
                    tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = -1;
                    if(aAddResultLog) {
                        tmpClusteringProcessLog.add("This input is a null vector");
                    }
                    //</editor-fold>
                    // <editor-fold desc="normalisation of the randomly selected input vector.Subsequently, all components of the input vector are transformed with a non-linear threshold function for contrast enhancement." defaultstate="collapsed">
                } else {
                    tmpVectorLength1 = this.getVectorLength(tmpInputVector);
                    for(int tmpManipulateComponents = 0; tmpManipulateComponents < tmpInputVector.length; tmpManipulateComponents++) {
                        tmpInputVector[tmpManipulateComponents] *= (1 / tmpVectorLength1);
                        if(tmpInputVector[tmpManipulateComponents] <= this.thresholdForContrastEnhancement) {
                            tmpInputVector[tmpManipulateComponents] = 0;
                        }
                    }
                    //</editor-fold>
                    //the transformed input vector is normalised again.
                    tmpVectorLength2 = this.getVectorLength(tmpInputVector);
                    for(int tmpNormalizeInputComponents = 0; tmpNormalizeInputComponents < tmpInputVector.length; tmpNormalizeInputComponents++) {
                        tmpInputVector[tmpNormalizeInputComponents] *= (1 / tmpVectorLength2);
                    }
                    //First pass, no clusters available, so the first cluster is created.
                    if(tmpNumberOfDetectedClusters == 0) {
                        this.clusterMatrix[0] = tmpInputVector;
                        tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] =tmpNumberOfDetectedClusters;
                        tmpNumberOfDetectedClusters++;
                        if(aAddResultLog) {
                            tmpClusteringProcessLog.add("Cluster number: 0");
                            tmpClusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                        }
                    } else {
                        // Cluster number is greater than or equal to 1, so a rho winner is determined as shown in the following steps.
                        double tmpSumCom = 0;
                        for(double tmpVectorComponentsOfNormalizeVector : tmpInputVector) {
                            tmpSumCom += tmpVectorComponentsOfNormalizeVector;
                        }
                        tmpWinnerClassIndex = tmpNumberOfDetectedClusters;
                        boolean tmpRhoWinner = true;
                        tmpRho = this.scalingFactor * tmpSumCom;
                        for(int tmpCurrentClusterMatrixRow = 0; tmpCurrentClusterMatrixRow < tmpNumberOfDetectedClusters; tmpCurrentClusterMatrixRow++) {
                            double[] tmpRow;
                            double tmpRho2 = 0;
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
                            if(aAddResultLog) {
                                tmpClusteringProcessLog.add("Cluster number: " + (tmpNumberOfDetectedClusters - 1));
                                tmpClusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            }
                        } else {
                            for(int m = 0; m < this.numberOfComponents; m++) {
                                if(this.clusterMatrix[tmpWinnerClassIndex][m] <= this.thresholdForContrastEnhancement) {
                                    tmpInputVector[m] = 0;
                                }
                            }
                            double tmpLength3 = this.getVectorLength(tmpInputVector);
                            System.out.println(learningParameter + "----learning parameter");
                            double tmpFactor1 = this.learningParameter / tmpLength3;
                            double tmpFactor2 = 1 - this.learningParameter;
                            for(int tmpAdaptedComponents = 0; tmpAdaptedComponents < this.numberOfComponents; tmpAdaptedComponents++) {
                                tmpInputVector[tmpAdaptedComponents] = tmpInputVector[tmpAdaptedComponents] * tmpFactor1 + tmpFactor2 * this.clusterMatrix[tmpWinnerClassIndex][tmpAdaptedComponents];
                            }
                            tmpVectorLength3 = this.getVectorLength(tmpInputVector);
                            for(int i = 0; i < tmpInputVector.length; i++) {
                                tmpInputVector[i] *= (1 / tmpVectorLength3);
                            }
                            this.clusterMatrix[tmpWinnerClassIndex] = tmpInputVector;
                            tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpWinnerClassIndex;
                            if(aAddResultLog) {
                                tmpClusteringProcessLog.add("Cluster number: " + tmpWinnerClassIndex);
                                tmpClusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            }
                        }
                    }
                }
            }
            // check the convergence. If the network is converged, tmpConvergence == true otherwise false
            tmpConvergence = this.checkConvergence(tmpNumberOfDetectedClusters, tmpCurrentNumberOfEpochs);
            tmpCurrentNumberOfEpochs++;
            if(aAddResultLog) {
                tmpClusteringProcessLog.add("Convergence status: " + tmpConvergence);
                tmpClusteringProcessLog.add("---------------------------------------");
            }
        }
        if(aAddResultLog) {
            tmpClusteringResultLog.add("Number of epochs: " + (tmpCurrentNumberOfEpochs - 1));
            tmpClusteringResultLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
            tmpClusteringResultLog.add("---------------------------------------");
        }
        if(!aAddResultLog) {
            return new ART2aDoubleClusteringResult(aVigilanceParameter,this.clusterMatrix, tmpClassView, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters);
        } else {
            return new ART2aDoubleClusteringResult(aVigilanceParameter,this.clusterMatrix, tmpClassView, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters,tmpClusteringProcessLog, tmpClusteringResultLog);
        }
    }

    @Override
    public boolean checkConvergence(int aNumberOfDetectedClasses, int aConvergenceEpoch) {
        boolean tmpConvergence = true;
        double[] tmpRaw;
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
                    System.out.println(requiredSimilarity + "----required similarity");
                    if (tmpScalarProductOfClassVector < this.requiredSimilarity) { // TODO
                        tmpConvergence = false;
                        break;
                    }
                }
            }
            if(!tmpConvergence) {
                for(int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
                    tmpRaw = this.clusterMatrix[tmpCurrentClusterMatrixVector];
                    this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector] = tmpRaw;
                }
            }
        } else {
            // System.out.println("failed");
            LOGGER.severe("Convergence failed for: " + this.vigilanceParameter );
            throw new RuntimeException("Convergence failed"); // TODO own Exception, maybe ConvergenceFailedException?
        }
        return tmpConvergence;
    }
}
