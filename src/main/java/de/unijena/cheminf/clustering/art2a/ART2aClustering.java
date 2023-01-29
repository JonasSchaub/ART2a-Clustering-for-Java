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

public class ART2aClustering {

    float[][] dataMatrix;
    float thresholdForContrastEnhancement;
    int numberOfVectors;
    int numberOfComponents;
    float scalingFactor;
    float[][] clusterMatrix;
    float[][] clusterMatrixPreviousEpoch;
    int maximumNumberOfEpochs;
    final float defaultLearningParameter = 0.01f;
    int seed;
    Logger clusteringLogger;

    private ArrayList<String> intermediateResultList;

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ART2aClustering.class.getName());

    /**
     *
     * @param aDataMatrix
     * @param aMaximumNumberOfEpochs
     * @throws IOException
     */
    public ART2aClustering(float[][] aDataMatrix, int aMaximumNumberOfEpochs)  {
        this.dataMatrix = aDataMatrix;
        this.checkDataMatrix(this.dataMatrix);
        this.numberOfVectors = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0)); // TODO: @Betuel scalingFactor = thresholdFor...
        this.thresholdForContrastEnhancement = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));
    }

    /**
     *
     * @param aFile
     * @param aMaximumNumberOfEpochs
     * @throws IOException
     */
    public ART2aClustering(String aFile, int aMaximumNumberOfEpochs) throws IOException {
        this.getDataMatrix(aFile);
        this.checkDataMatrix(this.dataMatrix);
        this.numberOfVectors = this.dataMatrix.length;
        this.maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        this.numberOfComponents = this.dataMatrix[0].length;
        this.scalingFactor = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0)); // TODO: @Betuel scalingFactor = thresholdFor...
        this.thresholdForContrastEnhancement = (float) (1.0 / Math.sqrt(this.numberOfComponents + 1.0));

    }

    /**
     * @throws Exception
     */
    public void startArt2aClustering(float tmpVigilance) throws Exception {
        try{
            this.initialiseMatrices();
            this.seed = 1;
            float[] tmpClusterMatrixRow;
            float[] tmpClusterMatrixRowOld;
            float tmpInitialWeightValue = (float) (1.0 / Math.sqrt(this.numberOfComponents));
            int tmpNumberOfDetectedClusters = 0;
            int[] tmpClassView = new int[this.numberOfVectors];
            int[] tmpClassViewOld = new int[this.numberOfVectors];
            float tmpVectorLength1;
            float tmpVectorLength2;
            float tmpRho;
            float tmpLength4;
            int tmpWinnerClassIndex;
            boolean tmpConvergence = false;

            ArrayList<Float> tmpList; // TODO remove list
            for (int tmpCurrentClusterMatrixVector = 0; tmpCurrentClusterMatrixVector < this.clusterMatrix.length; tmpCurrentClusterMatrixVector++) {
                tmpClusterMatrixRow = this.clusterMatrix[tmpCurrentClusterMatrixVector];
                tmpClusterMatrixRowOld = this.clusterMatrixPreviousEpoch[tmpCurrentClusterMatrixVector];
                for (int tmpCurrentVectorComponentsInClusterMatrix = 0; tmpCurrentVectorComponentsInClusterMatrix < tmpClusterMatrixRow.length; tmpCurrentVectorComponentsInClusterMatrix++) {
                    tmpClusterMatrixRow[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
                    tmpClusterMatrixRowOld[tmpCurrentVectorComponentsInClusterMatrix] = tmpInitialWeightValue;
                }
            }
            int tmpCurrentNumberOfEpochs = 0;
            this.clusteringLogger = new Logger();
            this.intermediateResultList = new ArrayList<>();
            while (!tmpConvergence && tmpCurrentNumberOfEpochs <= this.maximumNumberOfEpochs) { // TODO ask maximumNumberofEpchs
                tmpCurrentNumberOfEpochs++;
                this.clusteringLogger.listeErzeugen(this.intermediateResultList);
                this.clusteringLogger.appendIntermediateResult(" VIGILANCE PARAMETER: " + tmpVigilance);
                this.clusteringLogger.appendIntermediateResult("Number of epochs: " + tmpCurrentNumberOfEpochs);
                this.clusteringLogger.appendIntermediateResult("");
                int[] tmpSampleVectorIndicesInRandomOrder = this.randomizeVectorIndices();
                for (int tmpCurrentInput = 0; tmpCurrentInput < this.numberOfVectors; tmpCurrentInput++) {
                    this.clusteringLogger.appendIntermediateResult("Input: " + tmpCurrentInput);
                    float[] tmpInputVector = new float[this.numberOfComponents];
                    boolean tmpCheckNullVector = true;
                    for(int tmpCurrentInputVectorComponents = 0; tmpCurrentInputVectorComponents <this.numberOfComponents; tmpCurrentInputVectorComponents++) {
                        tmpInputVector[tmpCurrentInputVectorComponents] = this.dataMatrix[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] [tmpCurrentInputVectorComponents]; // TODO also possible in another way?
                    }
                    for (int tmpCheckInputComponentsToNull = 0; tmpCheckInputComponentsToNull < tmpInputVector.length; tmpCheckInputComponentsToNull++) {
                        if (tmpInputVector[tmpCheckInputComponentsToNull] != 0) {
                            tmpCheckNullVector = false;
                        }
                    }
                    if (tmpCheckNullVector) {
                        tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = -1;
                        this.clusteringLogger.appendIntermediateResult("This input is a null vector");
                        //TODO
                    } else {
                        tmpVectorLength1 = this.getVectorLength(tmpInputVector).get(1);
                        for (int tmpManipulateComponents = 0; tmpManipulateComponents < tmpInputVector.length; tmpManipulateComponents++) {
                            tmpInputVector[tmpManipulateComponents] *= (1 / tmpVectorLength1);
                            if (tmpInputVector[tmpManipulateComponents] <= this.thresholdForContrastEnhancement) {
                                tmpInputVector[tmpManipulateComponents] = 0;
                            }
                        }
                        tmpList = this.getVectorLength(tmpInputVector);
                        tmpVectorLength2 = tmpList.get(1);
                        for (int tmpNormalizeInputComponents = 0; tmpNormalizeInputComponents < tmpInputVector.length; tmpNormalizeInputComponents++) {
                            tmpInputVector[tmpNormalizeInputComponents] *= (1 / tmpVectorLength2);
                        }
                        if (tmpNumberOfDetectedClusters == 0) {
                            this.clusterMatrix[0] = tmpInputVector;
                            tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters;
                            tmpNumberOfDetectedClusters++;
                            this.clusteringLogger.appendIntermediateResult("Cluster number: 0");
                            this.clusteringLogger.appendIntermediateResult("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                        } else {
                            float tmpSumCom = 0;
                            for(float tmpVectorComponentsOfNormalizeVector : tmpInputVector) {
                                tmpSumCom += tmpVectorComponentsOfNormalizeVector;
                            }
                            tmpWinnerClassIndex = tmpNumberOfDetectedClusters;
                            boolean tmpRhoWinner = true;
                            tmpRho = this.scalingFactor*  tmpSumCom;
                            for(int tmpCurrentClusterMatrixRow = 0; tmpCurrentClusterMatrixRow <tmpNumberOfDetectedClusters; tmpCurrentClusterMatrixRow++) {
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
                            if(tmpRhoWinner == true || tmpRho < tmpVigilance) {
                                tmpNumberOfDetectedClusters++;
                                tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpNumberOfDetectedClusters -1;
                                this.clusterMatrix[tmpNumberOfDetectedClusters-1] = tmpInputVector;
                                this.clusteringLogger.appendIntermediateResult("Cluster number: " + (tmpNumberOfDetectedClusters-1));
                                this.clusteringLogger.appendIntermediateResult("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            } else {
                                for(int m = 0; m< this.numberOfComponents; m++) {
                                    if(this.clusterMatrix[tmpWinnerClassIndex][m] <=this.thresholdForContrastEnhancement) {
                                        tmpInputVector[m] = 0;// v
                                    }
                                }
                                float tmpLength3 = this.getVectorLength(tmpInputVector).get(1);
                                float tmpFactor1 = this.defaultLearningParameter / tmpLength3;
                                float tmpFactor2 = 1- this.defaultLearningParameter;
                                for(int tmpAdaptedComponents = 0; tmpAdaptedComponents < this.numberOfComponents; tmpAdaptedComponents++) {
                                    tmpInputVector[tmpAdaptedComponents] = tmpInputVector[tmpAdaptedComponents] * tmpFactor1 + tmpFactor2 * this.clusterMatrix[tmpWinnerClassIndex][tmpAdaptedComponents]; // result t
                                }
                                tmpLength4 = this.getVectorLength(tmpInputVector).get(1);
                                for(int i = 0; i <tmpInputVector.length; i++) {
                                    tmpInputVector[i] *= (1/ tmpLength4);
                                }
                                this.clusterMatrix[tmpWinnerClassIndex] = tmpInputVector;
                                tmpClassView[tmpSampleVectorIndicesInRandomOrder[tmpCurrentInput]] = tmpWinnerClassIndex;
                                this.clusteringLogger.appendIntermediateResult("Cluster number: " + tmpWinnerClassIndex);
                                this.clusteringLogger.appendIntermediateResult("Number of detected clusters: " + tmpNumberOfDetectedClusters);
                            }
                        }
                    }
                }
                TreeMap<Integer, Integer> tmpClusterToMembersMap = new TreeMap<>();
                int i = 1;
                for(int tmpClusterMembers : tmpClassView) {
                    if (tmpClusterMembers == -1) {
                        continue;
                    }
                    if (tmpClusterToMembersMap.containsKey(tmpClusterMembers) == false) {
                        tmpClusterToMembersMap.put(tmpClusterMembers, i);
                    } else {
                        tmpClusterToMembersMap.put(tmpClusterMembers, tmpClusterToMembersMap.get(tmpClusterMembers) + 1);
                    }
                }
                this.clusteringLogger.appendIntermediateResult("Cluster members: " + tmpClusterToMembersMap);
               // this.clusteringLogger.getEndOfList();  // Klassenvariable ArrayList<ArrayList<String>> machen und eine getter methode schreiben, diese in der Testklasse aufrufen!
                tmpConvergence = this.checkConvergence(tmpNumberOfDetectedClusters, tmpClassView, tmpClassViewOld);
                this.clusteringLogger.appendIntermediateResult("---------------------------------------");
            }
        } catch(Exception anException){
            this.clusteringLogger.appendExceptionMessage("The Clustering could not be carried out successfully.", anException);



        }
    }

    /**
     *
     * @param aDataMatrix
     */
    private void checkDataMatrix(float[][] aDataMatrix) {
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        if (aDataMatrix.length <= 0) {
            throw new IllegalArgumentException("The number of vectors must be greater then 0 to cluster inputs");
        }
        int tmpNumberOfVectorComponents = aDataMatrix[0].length;
        float tmpCurrentMatrixComponent;
        float[] tmpRow;
        HashMap<float[],Integer> tmpMap = new HashMap<>();
        ArrayList<Integer> tmpComponentsForScaling = new ArrayList<>();
        for (int i = 0; i < aDataMatrix.length; i++) {
            tmpRow = aDataMatrix[i];
            if (tmpNumberOfVectorComponents != tmpRow.length) {
                throw new IllegalArgumentException("the vectors must be have the same length!");
            }
            for (int j = 0; j < tmpRow.length; j++) {
                tmpCurrentMatrixComponent = tmpRow[j];
                if (tmpCurrentMatrixComponent < 0 || tmpCurrentMatrixComponent > 1) {// TODO: @Betuel check null?
                    tmpMap.put(aDataMatrix[i],i);
                    tmpComponentsForScaling.add(i);
                }
            }
        }
        try {
            this.scaleInput(tmpMap, tmpComponentsForScaling);
        } catch (Exception anException) {

        }
    }
    private void scaleInput(HashMap<float[],Integer> aMap, ArrayList<Integer> aList) throws Exception {
        for (float[] tmpScalingVector : aMap.keySet()) {
            float tmpFirstComponent = tmpScalingVector[0];
            for (float tmpComponentsOfScalingVector : tmpScalingVector) {
                if (tmpComponentsOfScalingVector > tmpScalingVector[0]) {
                    tmpFirstComponent = tmpComponentsOfScalingVector;
                }
            }
            for (int i = 0; i < tmpScalingVector.length; i++) {
                float tmpScaledComponent = tmpScalingVector[i] / tmpFirstComponent;
                tmpScalingVector[i] = tmpScaledComponent;
                this.dataMatrix[aMap.get(tmpScalingVector)] = tmpScalingVector;
            }
            System.out.println(tmpFirstComponent + "--max");
        }
    }

    /**
     *
     */
    private void initialiseMatrices() {
        this.clusterMatrix = new float[this.numberOfVectors][this.numberOfComponents];
        this.clusterMatrixPreviousEpoch = new float[this.numberOfVectors][this.numberOfComponents];

    }

    /**
     *
     * @return
     * @author
     */
    private int[] randomizeVectorIndices() {;
        int[] tmpSampleVectorIndicesInRandomOrder = new int[this.numberOfVectors];
        try {
            // fills array with values from 0 - mNumberOfVectors -1
            for (int i = 0; i < this.numberOfVectors; i++) {
                tmpSampleVectorIndicesInRandomOrder[i] = i; // bsp. [1,2,3]
            }
            Random rnd = new Random(this.seed); // warum 1?
            seed++;
            int numberOfIterations = (this.numberOfVectors / 2) + 1;
            int tmpRandomIndex1;
            int tmpRandomIndex2;
            int tmpBuffer;

            for (int j = 0; j < numberOfIterations; j++) {
                tmpRandomIndex1 = (int) (numberOfVectors * rnd.nextDouble());
                tmpRandomIndex2 = (int) (numberOfVectors * rnd.nextDouble());

                tmpBuffer = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1];
                tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex1] = tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2];
                tmpSampleVectorIndicesInRandomOrder[tmpRandomIndex2] = tmpBuffer;
            }
            // System.out.println(java.util.Arrays.toString(tmpSampleVectorIndicesInRandomOrder) + "---random order123");
        } catch (Exception exception) {
           // log.appendIntermediateResult("Exception: THE RANDOMISATION OF THE INPUT VECTORS HAS FAILED!");
            throw new RuntimeException("Unable to initialize the array for selection of a random vector!", exception);
        }
        return tmpSampleVectorIndicesInRandomOrder;
    }

    /**
     *
     * @param aSampleVector
     * @return
     */
    private ArrayList<Float> getVectorLength(float[] aSampleVector) {
        float tmpVectorComponentsSqrtSum = 0;
        float tmpVectorLength;
        float tmpSumComp = 0;
        ArrayList<Float> tmpList = new ArrayList<>();
        for(int i = 0; i<aSampleVector.length; i++) {
            tmpVectorComponentsSqrtSum += aSampleVector[i] * aSampleVector[i];
            tmpSumComp += aSampleVector[i];
            //  list.add(tmpVectorComponentsSqrtSum);
        }
        tmpList.add(tmpSumComp);
        /* Calling the root function at this point is unnecessary, since sqrt(0) = 0. */
        if(tmpVectorComponentsSqrtSum == 0) {
            //return 0;
            throw new ArithmeticException("Exception");
        }
        else {
            tmpVectorLength = (float) Math.sqrt(tmpVectorComponentsSqrtSum);
            tmpList.add(tmpVectorLength);
            //  return tmpVectorLength;
        }
        return tmpList;
    }

    /**
     *
     * @param aNumberOfDetectedClasses
     * @param aVectorNew
     * @param aVectorOld
     * @return
     * @throws Exception
     */
    public boolean checkConvergence(int aNumberOfDetectedClasses, int[] aVectorNew, int[] aVectorOld) throws Exception {
        try {
            boolean tmpConvergence = true;
            // System.out.println(java.util.Arrays.toString(vectornew) + "--vector new");
            // System.out.println(java.util.Arrays.toString(vectorold) + "----vector old");
            for(int i = 0; i <this.numberOfVectors; i++) {
                if(aVectorNew[i] != aVectorOld[i]) {
                    tmpConvergence = false;
                    break;
                }
            }
            // boolean convergenceFlag = true;
            if (tmpConvergence) {
                // Check convergence by evaluating the similarity of the classvectors of this and the previous epoch
                tmpConvergence = true;
                double tmpScalarProductOfClassVector;
                System.out.println(aNumberOfDetectedClasses + "---convergence anzahl klassen");
                float[] tmpCurrentRowInClusterMatrix;
                float[] tmpPreviousEpochRow;
                for (int i = 0; i < aNumberOfDetectedClasses; i++) {
                    tmpScalarProductOfClassVector = 0;
                    tmpCurrentRowInClusterMatrix = this.clusterMatrix[i];
                    // System.out.println(java.util.Arrays.toString(row) + "----------------aktuelle clustermatrix zeile");
                    tmpPreviousEpochRow = this.clusterMatrixPreviousEpoch[i];
                    // System.out.println(java.util.Arrays.toString(oldrow) + "------------------alte zeile clsutermatrix");

                    for (int j = 0; j < this.numberOfComponents; j++) {
                        //System.out.println(java.util.Arrays.toString(this.mClassMatrix));
                        //  scalarProductOfClassVector += clusterMatrix[i][j] * clusterMatrixPreviousEpoch[i][j];
                        tmpScalarProductOfClassVector += tmpCurrentRowInClusterMatrix[j] * tmpPreviousEpochRow[j];
                    }
                    // System.out.println(scalarProductOfClassVector + "---skalarprodukt");
                    if (tmpScalarProductOfClassVector < 0.99f) {
                        //  System.out.println("skalarprodukt kleiner als mindest aenlichkeit");
                        tmpConvergence = false;
                        //i = this.mNumberOfComponents;
                        //	System.out.println(i + "---i");
                        //	continue;
                        break;
                    }
                    System.out.println(tmpConvergence);
                }
            }
            System.out.println("convergence false");
            if (!tmpConvergence) {
                for (int i = 0; i < aNumberOfDetectedClasses; i++) {
                    for (int j = 0; j < this.numberOfComponents; j++) {
                        this.clusterMatrixPreviousEpoch[i][j] = this.clusterMatrix[i][j]; // TODO also possible in another way?
                    }
                }
                for(int tmpClusterDistribution = 0; tmpClusterDistribution <this.numberOfVectors; tmpClusterDistribution++) {
                    aVectorOld[tmpClusterDistribution] = aVectorNew[tmpClusterDistribution];
                }
            }
            // System.out.println(java.util.Arrays.toString(vectornew) + "--vector new");
            // System.out.println(java.util.Arrays.toString(vectorold) + "----vector old");
            return tmpConvergence;
        } catch (Exception exception) {
            throw new Exception("The classification failed! Unable to check the convergence of the current classification.", exception);
        }
    }

    /**
     *
     * @param aFileName
     * @throws IOException
     */
    public void getDataMatrix(String aFileName) throws IOException {
        BufferedReader tmpMoleculeFragmentsReader = new BufferedReader(new FileReader(aFileName));
        String tmpSeparatorSemicolon = ",";
        List<float[]> list = new ArrayList<>();
        String tmpMoleculeLine;
        int ip = 0;
        while ((tmpMoleculeLine = tmpMoleculeFragmentsReader.readLine()) != null) {
            String[] tmpMoleculeFragmentsAndFrequencies = tmpMoleculeLine.split(tmpSeparatorSemicolon);
            float[] floatArray = new float[tmpMoleculeFragmentsAndFrequencies.length];
            for(int i = 0; i<tmpMoleculeFragmentsAndFrequencies.length; i++) {
                floatArray[i] = Float.parseFloat(tmpMoleculeFragmentsAndFrequencies[i]);
            }
            ip++;
            list.add(floatArray);
        }
        this.dataMatrix = new float[100][99];
        for(int z = 0; z<ip; z++) {
            this.dataMatrix[z] = list.get(z);
        }
    }
    public ArrayList<String> getResult() {
        return intermediateResultList;
    }




}