package de.unijena.cheminf.clustering.art2a.Clustering;

import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClustering;
import de.unijena.cheminf.clustering.art2a.Interfaces.IART2aClusteringResult;
import de.unijena.cheminf.clustering.art2a.Result.ART2aFloatClusteringResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ART2aFloatClustering implements IART2aClustering {
    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * The matrix contains all fingerprints to be clustered.
     * Each row of the matrix represents a fingerprint.
     */
    private float[][] dataMatrix;
    /**
     * Threshold for contrast enhancement. If a vector/fingerprint component is below the threshold, it is set to zero.
     */
    private  float thresholdForContrastEnhancement;
    /**
     * Number of fingerprints to be clustered.
     */
    private  int numberOfFingerprints;
    /**
     * Dimensionality of the fingerprint.
     */
    private  int numberOfComponents;
    /**
     * Scaling factor for the modified vectors.
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
    private float vigilanceParameter;
    private float requiredSimilarity;
    private float learningParameter;
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
    private static final Logger LOGGER = Logger.getLogger(ART2aFloatClustering.class.getName());

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
    public ART2aFloatClustering(float[][] aDataMatrix, int aMaximumNumberOfEpochs, float aVigilanceParameter,float aRequiredSimilarity, float aLearningParameter) throws IllegalArgumentException, NullPointerException {
        if(aDataMatrix == null) {
            ART2aFloatClustering.LOGGER.severe("The data matrix is null.");
            throw new NullPointerException("aDataMatrix is null.");
        }
        if(aMaximumNumberOfEpochs <= 0) {
            ART2aFloatClustering.LOGGER.severe("Number of epochs must be at least greater than zero.");
            throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
        }
        if(aVigilanceParameter < 0 || aVigilanceParameter > 1) {
            ART2aFloatClustering.LOGGER.severe("The vigilance parameter must be greater than 0 and less than 1.");
            throw new IllegalArgumentException("The vigilance parameter must be greater than 0 and less than 1.");
        }
        this.vigilanceParameter = aVigilanceParameter;
        this.requiredSimilarity = aRequiredSimilarity;
        this.learningParameter = aLearningParameter;
        // this.dataMatrix = aDataMatrix;
        // this.checkDataMatrix(this.dataMatrix); // TODO first check dataMatrix (why?)
        this.dataMatrix =  this.checkDataMatrix(aDataMatrix);
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
     * @throws IOException is thrown if the file cannot be read in.
     * @throws IllegalArgumentException
     */
    /*
    public ART2aFloatClustering(String aFilePathName, int aMaximumNumberOfEpochs, char aSeparator, float aVigilanceParameter) throws IllegalArgumentException {
        try {
            if (aMaximumNumberOfEpochs <= 0) {
                ART2aFloatClustering.LOGGER.severe("Number of epochs must be at least greater than zero.");
                throw new IllegalArgumentException("Number of epochs must be at least greater than zero.");
            }
            if(aVigilanceParameter < 0 || aVigilanceParameter > 1) {
                ART2aFloatClustering.LOGGER.severe("The vigilance parameter must be greater than 0 and less than 1.");
                throw new IllegalArgumentException("The vigilance parameter must be greater than 0 and less than 1.");
            }
           // this.importDataMatrixFromFile(aFilePathName, aSeparator);
            this.dataMatrix =  FileUtil.importDataMatrixFromFile(aFilePathName,aSeparator);
            this.checkDataMatrix(this.dataMatrix);
            this.vigilanceParameter = aVigilanceParameter;
            this.numberOfFingerprints = this.dataMatrix.length;
            this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
            this.numberOfComponents = this.dataMatrix[0].length;
            this.scalingFactor = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
            this.thresholdForContrastEnhancement = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
        } catch(IOException anIOException) {
            ART2aFloatClustering.LOGGER.log(Level.SEVERE, anIOException.toString(), anIOException);
        }
    }

     */
    /**
     *
     * @param aDataMatrix
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    private float[][] checkDataMatrix(float[][] aDataMatrix) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        if(aDataMatrix.length <= 0) {
            ART2aFloatClustering.LOGGER.severe("The number of vectors must greater then 0 to cluster inputs.");
            throw new IllegalArgumentException("The number of vectors must greater then 0 to cluster inputs.");
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
                    System.out.println(tmpCurrentMatrixComponent+"---scale");
                    tmpFingerprintsForScalingToMatrixRow.put(aDataMatrix[i],i);
                    tmpComponentsForScaling.add(i);
                }
                if(tmpCurrentMatrixComponent < 0) {
                    ART2aFloatClustering.LOGGER.severe("Only positive values allowed.");
                    throw new IllegalArgumentException("Only positive values allowed.");
                }
            }
        }
        if(tmpFingerprintsForScalingToMatrixRow.isEmpty() == false) {
            this.scaleInput(tmpFingerprintsForScalingToMatrixRow, aDataMatrix); // TODO replace method?
        }
        return aDataMatrix;
    }

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
    /*
    private void importDataMatrixFromFile(String aFilePath, char aSeparator) throws IOException, NumberFormatException {
        if (aFilePath == null || aFilePath.isEmpty() || aFilePath.isBlank()) {
            throw new IllegalArgumentException("aFileName is null or empty/blank.");
        }
        BufferedReader tmpFingerprintFileReader;
        tmpFingerprintFileReader = new BufferedReader(new FileReader(aFilePath));
        List<float[]> tmpFingerprintList = new ArrayList<>();
        String tmpFingerprintLine;
        int tmpDataMatrixRow = 0;
        while ((tmpFingerprintLine = tmpFingerprintFileReader.readLine()) != null) {
            String[] tmpFingerprint = tmpFingerprintLine.split(String.valueOf(aSeparator));
            float[] tmpFingerprintFloatArray = new float[tmpFingerprint.length];
            try {
                for (int i = 0; i < tmpFingerprint.length; i++) {
                    tmpFingerprintFloatArray[i] = Float.parseFloat(tmpFingerprint[i]);
                }
            } catch (NumberFormatException anException) {
                throw anException;
            }
            tmpDataMatrixRow++;
            tmpFingerprintList.add(tmpFingerprintFloatArray);
        }
        tmpFingerprintFileReader.close();
        this.dataMatrix = new float[tmpDataMatrixRow][tmpFingerprintList.get(0).length];
        for (int tmpCurrentMatrixRow = 0; tmpCurrentMatrixRow < tmpDataMatrixRow; tmpCurrentMatrixRow++) {
            this.dataMatrix[tmpCurrentMatrixRow] = tmpFingerprintList.get(tmpCurrentMatrixRow);
        }
        System.out.println(this.dataMatrix[0].length + "Matrix dimension");
        System.out.println(this.dataMatrix.length + "--Matrix Anzahl Fingerprints");
    }

     */
    /**
     * Calculates the length of a vector. The length is needed for the normalisation of the vector.
     *
     * @param anInputVector vector whose length is calculated.
     * @return float vector length.
     * @throws ArithmeticException is thrown if the addition of the vector components results in zero.
     */
    private float getVectorLength (float[] anInputVector) throws ArithmeticException {
        // logger.info("Start calculate vector length: " + this.vigi);
        float tmpVectorComponentsSqrtSum = 0;
        float tmpVectorLength;
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
    /**
     * Method for scaling the fingerprints if they are not between 0 and 1.
     * Thus serves for the scaling of count fingerprints.
     *
     * @param aFingerprintToMatrixRowMap is a map that maps the fingerprints with components
     *                                   outside 0-1 to the row position in the matrix.
     */
    private void scaleInput (HashMap <float[],Integer> aFingerprintToMatrixRowMap, float[][] aDataMatrix){
        System.out.println("hallo scale");
        for (float[] tmpScalingVector : aFingerprintToMatrixRowMap.keySet()) {
            float tmpFirstComponent = tmpScalingVector[0];
            for (float tmpComponentsOfScalingVector : tmpScalingVector) {
                if (tmpComponentsOfScalingVector > tmpFirstComponent) {
                    tmpFirstComponent = tmpComponentsOfScalingVector;
                }
            }
            System.out.println(tmpFirstComponent + "---tmpFirstcomponent");
            for (int i = 0; i < tmpScalingVector.length; i++) {
                float tmpScaledComponent = tmpScalingVector[i] / tmpFirstComponent;
                tmpScalingVector[i] = tmpScaledComponent;
                // this.dataMatrix[aFingerprintToMatrixRowMap.get(tmpScalingVector)] = tmpScalingVector;
                aDataMatrix[aFingerprintToMatrixRowMap.get(tmpScalingVector)] = tmpScalingVector;
            }
        }
    }

    //
    /**
     * Initialise the cluster matrix
     *
     */
    @Override
    public void initializeMatrices() {
        this.clusterMatrix = new float[this.numberOfFingerprints][this.numberOfComponents];
        this.clusterMatrixPreviousEpoch = new float[this.numberOfFingerprints][this.numberOfComponents];
    }
    //
    /**
     * Fills the array "tmpSampleVectorIndicesInRandomOrder" with vector indices in a
     * random order.
     * Restriction: Every vector index is allowed to occur just one time in the
     * array.
     * The method used to randomize the inputs is the Fisher Yates method.
     * @return int[] in which the random order (indices) of the vectors is stored.
     * @author Thomas Kuhn
     */
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
    public IART2aClusteringResult startClustering(float aVigilanceParameter, boolean aAddLog) throws RuntimeException {
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
                float[] tmpInputVector = new float[this.numberOfComponents];
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
                            float tmpLength3 = this.getVectorLength(tmpInputVector);
                            float tmpFactor1 = this.learningParameter / tmpLength3;
                            float tmpFactor2 = 1 - this.learningParameter;
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
        tmpVigilanceParameterToNumberOfEpochs.put(aVigilanceParameter, tmpCurrentNumberOfEpochs-1);
        tmpVigilanceParameterToNumberOfCluster.put(aVigilanceParameter, tmpNumberOfDetectedClusters);
        if(aAddLog) {
            tmpClusteringResultLog.add("Number of epochs: " + (tmpCurrentNumberOfEpochs - 1));
            tmpClusteringResultLog.add("Number of detected clusters: " + tmpNumberOfDetectedClusters);
            tmpClusteringResultLog.add("---------------------------------------");
        }
        // return this.clusteringStatus = true;
        if(!aAddLog) {
            return new ART2aFloatClusteringResult(this.clusterMatrix,this.dataMatrix, tmpClassView, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters);
        } else {
            return new ART2aFloatClusteringResult(this.clusterMatrix,this.dataMatrix, tmpClassView, tmpCurrentNumberOfEpochs, tmpNumberOfDetectedClusters,tmpClusteringProcessLog, tmpClusteringResultLog);
        }

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

    @Override
    public boolean checkConvergence(int aNumberOfDetectedClasses, int aConvergenceEpoch) {
        boolean tmpConvergence = true;
        float[] tmpRaw;
        if(aConvergenceEpoch <= this.maximumNumberOfEpochs) {
            // boolean tmpConvergence = true;
            /* first check: It is checked whether the distribution of inputs among
            the existing clusters have changed compared to the previous epoch.
            The system is converged if there is no change between the previous and the current cluster occupation.
             */
            /*
            for(int i = 0; i < this.numberOfFingerprints; i++) {
                if (aVectorNew[i] != aVectorOld[i]) {
                    tmpConvergence = false;
                    System.out.println("keine convergence");
                    break;
                }
            }
            if(!tmpConvergence) {
                for(int tmpClusterDistribution = 0; tmpClusterDistribution < this.numberOfFingerprints; tmpClusterDistribution++) {
                    aVectorOld[tmpClusterDistribution] = aVectorNew[tmpClusterDistribution];
                }
            }

             */
            if (tmpConvergence) {
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
                /*
                for (int i = 0; i < aNumberOfDetectedClasses; i++) {
                    for (int j = 0; j < this.numberOfComponents; j++) {
                        this.clusterMatrixPreviousEpoch[i][j] = this.clusterMatrix[i][j];
                    }
                }

                 */
            }
            //return tmpConvergence;
        } else {
            // System.out.println("failed");
            LOGGER.severe("Convergence failed for: " + this.vigilanceParameter );
            throw new RuntimeException("Convergence failed"); // TODO own Exception, maybe ConvergenceFailedException?
        }
        return tmpConvergence;
    }
    // </editor-fold>
    //

}
