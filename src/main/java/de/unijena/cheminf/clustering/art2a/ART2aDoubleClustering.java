package de.unijena.cheminf.clustering.art2a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ART2aDoubleClustering implements IART2aClustering {
    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * The matrix contains all fingerprints to be clustered.
     * Each row of the matrix represents a fingerprint.
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
     * Scaling factor for the modified vectors.
     */
    private double scalingFactor;
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
    private double vigilanceParameter;
    /**
     * Map maps the number of epochs to the number of detected clusters.
     */
    private HashMap<Integer, Integer> numberOfEpochsToNumberOfClusters;
    /**
     * Map maps the vigilance parameter to the number of epochs and to the number of detected clusters.
     */
    private HashMap<Float, HashMap<Integer,Integer>> vigilanceParameterToNumberOfEpochsAndNumberOfClusters;
    /**
     * Learning parameter to modify the cluster after a new input has been added.
     */
    private final double DEFAULT_LEARNING_PARAMETER = 0.01f;
    /**
     * Minimum similarity between the cluster vectors that must be present.
     */
    private final double REQUIRED_SIMILARITY = 0.99f;
    private static final Logger LOGGER = Logger.getLogger(ART2aDoubleClustering.class.getName());
    //</editor-fold>
    //
    public ART2aDoubleClustering(double[][] aDataMatrix, int aMaximumNumberOfEpochs, float aVigilanceParameter) {
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
        this.vigilanceParameter = aVigilanceParameter;
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
                    System.out.println(tmpCurrentMatrixComponent+"---scale");
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
        System.out.println("hallo scale");
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
    private double getVectorLength (double[] anInputVector) throws ArithmeticException {
        // logger.info("Start calculate vector length: " + this.vigi);
        double tmpVectorComponentsSqrtSum = 0;
        double tmpVectorLength;
        for (int i = 0; i < anInputVector.length; i++) {
            tmpVectorComponentsSqrtSum += anInputVector[i] * anInputVector[i];
        }
        if (tmpVectorComponentsSqrtSum == 0) {
            throw new ArithmeticException("Addition of the vector components results in zero!");
        } else {
            tmpVectorLength = (float) Math.sqrt(tmpVectorComponentsSqrtSum);
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

    @Override
    public ART2aClusteringResult startClustering(float aVigilanceParameter, boolean aAddLog) throws Exception {
        // float aVigilanceParameter = 0.1f;
        this.clusteringStatus = false;
        ConcurrentLinkedQueue<String> tmpClusteringProcessLog = null;
        ConcurrentLinkedQueue<String>tmpClusteringResultLog = null;
        if(aAddLog) {
            tmpClusteringProcessLog = new ConcurrentLinkedQueue<>();
            tmpClusteringResultLog = new ConcurrentLinkedQueue<>();
        }
        this.initializeMatrices();
        this.seed = 1;
        double[] tmpClusterMatrixRow;
        double[] tmpClusterMatrixRowOld;
        double tmpInitialWeightValue = (float) (1.0 / Math.sqrt(this.numberOfComponents));
        int tmpNumberOfDetectedClusters = 0;
        int[] tmpClassView = new int[this.numberOfFingerprints];
        int[] tmpClassViewOld = new int[this.numberOfFingerprints];
        double tmpVectorLength1;
        double tmpVectorLength2;
        double tmpRho;
        double tmpLength4;
        int tmpWinnerClassIndex;
        boolean tmpConvergence = false;
        HashMap<Float, Integer> tmpVigilanceParameterToNumberOfEpochs = new HashMap<>(9); // magic number TODO because 9 vigilance parameters are calculated
        HashMap<Float, Integer> tmpVigilanceParameterToNumberOfCluster = new HashMap<>(9); // magic number TODO because 9 vigilance parameters are calculated
        //TreeMap<Integer, Integer> tmpClusterToMembersMap = null;
        //TreeMap<Integer, Integer> tmpClusterToMembersMap = new TreeMap<>();
        //Initialisation of the elements of the cluster matrix with init weight values
        for(int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
            tmpClusterMatrixRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
            tmpClusterMatrixRowOld = this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector];
            for (int tmpCurrentVectorComponentsInClusterMatrix = 0; tmpCurrentVectorComponentsInClusterMatrix < tmpClusterMatrixRow.length; tmpCurrentVectorComponentsInClusterMatrix++) {
                tmpClusterMatrixRow[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
                tmpClusterMatrixRowOld[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
            }
        }
        int tmpCurrentNumberOfEpochs = 0;
        if(aAddLog) {
            tmpClusteringResultLog.add("Vigilance parameter: " + aVigilanceParameter);
        }
        // begin clustering process
        while(!tmpConvergence && tmpCurrentNumberOfEpochs <= this.maximumNumberOfEpochs) {
            if(aAddLog) {
                tmpClusteringProcessLog.add("ART-2a clustering result for vigilance parameter:" + aVigilanceParameter);
                tmpClusteringProcessLog.add("Number of epochs: " + tmpCurrentNumberOfEpochs);
                tmpClusteringProcessLog.add("");
            }
            // randomize input vectors
            int[] tmpSampleVectorIndicesInRandomOrder = this.randomizeVectorIndices();
            System.out.println(java.util.Arrays.toString(tmpSampleVectorIndicesInRandomOrder )+ "-----random order");
            for(int tmpCurrentInput = 0; tmpCurrentInput < this.numberOfFingerprints; tmpCurrentInput++) {
                //tmpClusteringProcessLog.add("Input: " + tmpCurrentInput);
                System.out.println(tmpCurrentInput + "-------Input");
                double[] tmpInputVector = new double[this.numberOfComponents];
                boolean tmpCheckNullVector = true;
                for(int tmpCurrentInputVectorComponents = 0; tmpCurrentInputVectorComponents < this.numberOfComponents; tmpCurrentInputVectorComponents++) {
                    tmpInputVector[tmpCurrentInputVectorComponents] = this.dataMatrix[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]][tmpCurrentInputVectorComponents];
                    if(tmpInputVector[tmpCurrentInputVectorComponents] !=0) {
                        tmpCheckNullVector = false;// TODO also possible in another way?
                    }
                    // check input, if input is a null vector, do not cluster.
                /*
                for(int tmpCheckInputComponentsToNull = 0; tmpCheckInputComponentsToNull < tmpInputVector.length; tmpCheckInputComponentsToNull++) {
                    if(tmpInputVector[tmpCheckInputComponentsToNull] != 0) {
                        tmpCheckNullVector = false;
                    }
                    */
                }
                if(aAddLog) {
                    tmpClusteringProcessLog.add("Input: " + tmpCurrentInput + " / Vector " + tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]);
                }
                System.out.println(java.util.Arrays.toString(tmpInputVector) + "-----input vector");
                if(tmpCheckNullVector) {
                    tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = -1;
                    if(aAddLog) {
                        tmpClusteringProcessLog.add("This input is a null vector");
                    }
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
                        tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] =tmpNumberOfDetectedClusters;
                        tmpNumberOfDetectedClusters++;
                        if(aAddLog) {
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
                            if(aAddLog) {
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
                            double tmpFactor1 = this.DEFAULT_LEARNING_PARAMETER / tmpLength3;
                            double tmpFactor2 = 1 - this.DEFAULT_LEARNING_PARAMETER;
                            for(int tmpAdaptedComponents = 0; tmpAdaptedComponents < this.numberOfComponents; tmpAdaptedComponents++) {
                                tmpInputVector[tmpAdaptedComponents] = tmpInputVector[tmpAdaptedComponents] * tmpFactor1 + tmpFactor2 * this.clusterMatrix[tmpWinnerClassIndex][tmpAdaptedComponents];
                            }
                            tmpLength4 = this.getVectorLength(tmpInputVector);
                            for(int i = 0; i < tmpInputVector.length; i++) {
                                tmpInputVector[i] *= (1 / tmpLength4);
                            }
                            this.clusterMatrix[tmpWinnerClassIndex] = tmpInputVector;
                            tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpWinnerClassIndex;
                            if(aAddLog) {
                                tmpClusteringProcessLog.add("Cluster number: " + tmpWinnerClassIndex);
                                tmpClusteringProcessLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            }
                        }
                    }
                }

            }
            // number of cluster members
            /*
            TreeMap<Integer, Integer> tmpClusterToMembersMap = new TreeMap<>();
            tmpClusterToMembersMap = new TreeMap<>();
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

             */
            // check the convergence. If the network is converged, tmpConvergence == true otherwise false
            tmpConvergence = this.checkConvergence(tmpNumberOfDetectedClusters, tmpCurrentNumberOfEpochs);
            tmpCurrentNumberOfEpochs++;
            //  tmpClusteringProcessLog.add("Cluster members: " + tmpClusterToMembersMap);
            if(aAddLog) {
                tmpClusteringProcessLog.add("Convergence status: " + tmpConvergence);
                tmpClusteringProcessLog.add("---------------------------------------");
            }
        }
        System.out.println(java.util.Arrays.toString(tmpClassView) + "--------Class view");
        tmpVigilanceParameterToNumberOfEpochs.put(aVigilanceParameter, tmpCurrentNumberOfEpochs-1);
        tmpVigilanceParameterToNumberOfCluster.put(aVigilanceParameter, tmpNumberOfDetectedClusters);
        if(aAddLog) {
            tmpClusteringResultLog.add("Number of epochs: " + (tmpCurrentNumberOfEpochs - 1));
            tmpClusteringResultLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
            tmpClusteringResultLog.add("---------------------------------------");
        }
        // return this.clusteringStatus = true;
        if(!aAddLog) {
            return new ART2aClusteringResult(this.clusterMatrix, tmpClassView, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters);
        } else {
            return  new ART2aClusteringResult(this.clusterMatrix, tmpClassView, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters,tmpClusteringProcessLog, tmpClusteringResultLog);
        }
    }

    @Override
    public boolean checkConvergence(int aNumberOfDetectedClasses, int aConvergenceEpoch) {
        return false;
    }
}
