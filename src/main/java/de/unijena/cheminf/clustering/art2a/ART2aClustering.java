package de.unijena.cheminf.clustering.art2a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;

public class ART2aClustering implements Callable<Void> {

    float[][] dataMatrix;
    float thresholdForContrastEnhancement;
    int numberOfVectors;
    int numberOfComponents;
    float scalingFactor;
    float[] [] clusterMatrix;
    float[] [] clusterMatrixPreviousEpoch;
    int maximumNumberOfEpochs;
    int currentNumberOfEpochs;
    final float defaultLearningParameter = 0.01f;
    float sumComponents;
    int[] tmpSampleVectorIndicesInRandomOrder;
    int tmpSeed;



    public ART2aClustering(float[][] aDataMatrix, int aMaximumNumberOfEpochs) {
        this.dataMatrix = aDataMatrix;
        this.checkDataMatrix(this.dataMatrix);
        numberOfVectors = dataMatrix.length;
        maximumNumberOfEpochs = aMaximumNumberOfEpochs;
        numberOfComponents = dataMatrix[0].length;
        scalingFactor = (float) (1 / Math.sqrt(numberOfComponents + 1)); // TODO: @Betuel scalingFactor = thresholdFor...
      //  mClassificationCompleteFlag = false;
        thresholdForContrastEnhancement = (float) (1 / Math.sqrt(numberOfComponents + 1));

    }

    @Override
    public Void call() throws Exception {
        /* clusterMatrix is filled with init value*/
        /*
        this.initialiseMatrices();
        float[] tmpClusterMatrixRow;
        int tmpNumberOfDetectedClusters = 1;
        int[] tmpClassView = new int[numberOfVectors];
        float tmpInitialWeightValue = (float) (1 / Math.sqrt(numberOfComponents));
        float tmpVectorLength1;
        float tmpVectorLength2;
        float rho1;
        float tmpSumCom = 0;
        float rho2 = 0;
        float[] arr;

        ArrayList<Float> list = new ArrayList<>();

         */
        /*
        for (int i = 0; i < clusterMatrix.length; i++) {
            tmpClusterMatrixRow = clusterMatrix[i];
            for (int j = 0; j < tmpClusterMatrixRow.length; j++) {
                tmpClusterMatrixRow[j] = tmpInitialWeightValue;
            }
        }

         */
        /* Start clustering*/
        int tmpNumberOfIterations = 0;
        /*
        float[] tmpInputVector = new float[numberOfComponents];
        float[] tmpClusterMatrixRow;
        float tmpInitialWeightValue = (float) (1 / Math.sqrt(numberOfComponents));
        // long tmpClusteringStartTime = System.currentTimeMillis();

         */
        boolean tmpNetworkConvergence = false;
     //   this.initialiseMatrices();
        try{
        for (float vigilance = 0.1f; vigilance <= 1.0f; vigilance += 0.1f) {
            this.initialiseMatrices();
            tmpSeed = 1;
           // float[] tmpInputVector = new float[numberOfComponents];
            float[] tmpClusterMatrixRow;
            float[] tmpClusterMatrixRowOld;
            float tmpInitialWeightValue = (float) (1 / Math.sqrt(numberOfComponents));
            int tmpNumberOfDetectedClusters = 0;
            int[] tmpClassView = new int[numberOfVectors];
            float tmpVectorLength1;
            float tmpVectorLength2;
            float rho1;
           // float[] arr;
           // float tmpLength3;
            float tmpLenght4;
            int tmpWinnerClassIndex;
            float[] arr2;
            boolean convegence = false;

            ArrayList<Float> list = new ArrayList<>();
            System.out.println(java.util.Arrays.toString(clusterMatrix[0]) + "--initialisierung clsuter matrix");
            System.out.println(java.util.Arrays.toString(tmpSampleVectorIndicesInRandomOrder) + "---sampleRandom");
            for (int i = 0; i < clusterMatrix.length; i++) {
                tmpClusterMatrixRow = clusterMatrix[i];
                tmpClusterMatrixRowOld = clusterMatrixPreviousEpoch[i];
                for (int j = 0; j < tmpClusterMatrixRow.length; j++) {
                    tmpClusterMatrixRow[j] = tmpInitialWeightValue;
                    tmpClusterMatrixRowOld[j] = tmpInitialWeightValue;
                }
            }
            System.out.println("\n");
            System.out.println(vigilance + "---vigilance parameter");
            System.out.println("\n");
            int currentNumberOfEpochs = 0;
            while (!convegence && currentNumberOfEpochs <= maximumNumberOfEpochs) {
                currentNumberOfEpochs++;
                randomizeVectorIndices();
                //float[] tmpInputVector = new float[numberOfComponents];
                for (int h = 0; h < numberOfVectors; h++) {
                    float[] tmpInputVector = new float[numberOfComponents];
                    boolean checkNullVector = true;
                    for(int j = 0; j<numberOfComponents; j++) {
                        tmpInputVector[j] = dataMatrix[tmpSampleVectorIndicesInRandomOrder[h]] [j];
                    }
                   // boolean checkNullVector = true;
                    for (int m = 0; m < tmpInputVector.length; m++) {
                        if (tmpInputVector[m] != 0) {
                            checkNullVector = false;
                        }
                    }
                    System.out.println(java.util.Arrays.toString(tmpInputVector) + "---Inputvektor ausgangsvektor");
                    if (checkNullVector) {
                        tmpClassView[tmpSampleVectorIndicesInRandomOrder[h]] = -1;
                    } else {
                        tmpVectorLength1 = this.getVectorLength(tmpInputVector).get(1);
                        for (int z = 0; z < tmpInputVector.length; z++) {
                            tmpInputVector[z] *= (1 / tmpVectorLength1);
                            if (tmpInputVector[z] <= thresholdForContrastEnhancement) {
                                tmpInputVector[z] = 0;
                            }
                        }
                        list = this.getVectorLength(tmpInputVector);
                        tmpVectorLength2 = list.get(1);
                        for (int k = 0; k < tmpInputVector.length; k++) {
                            tmpInputVector[k] *= (1 / tmpVectorLength2);
                        }
                        System.out.println(java.util.Arrays.toString(tmpInputVector) + "---normalisierte vector");
                        if(tmpNumberOfDetectedClusters == 0) {
                            clusterMatrix[0] = tmpInputVector;
                            tmpClassView[tmpSampleVectorIndicesInRandomOrder[h]] = tmpNumberOfDetectedClusters;
                            tmpNumberOfDetectedClusters++;
                        } else {
                            float tmpSumCom = 0;
                           for(float b : tmpInputVector) {
                               tmpSumCom += b;
                           }
                           tmpWinnerClassIndex = tmpNumberOfDetectedClusters;
                           boolean rho1winner = true;
                            rho1 = scalingFactor*  tmpSumCom;
                            for(int j = 0; j<tmpNumberOfDetectedClusters; j++) {
                                float[] arr;
                                float rho2 = 0;
                                arr = clusterMatrix[j];
                                for(int g = 0; g< numberOfComponents; g++) {
                                    rho2 += tmpInputVector[g] * arr[g];
                                }
                                if(rho2> rho1) {
                                    rho1 = rho2;
                                    tmpWinnerClassIndex = j;
                                    rho1winner = false;
                                }
                            }
                            if(rho1winner== true || rho1< vigilance) {
                                tmpNumberOfDetectedClusters++;
                                tmpClassView[tmpSampleVectorIndicesInRandomOrder[h]] = tmpNumberOfDetectedClusters -1;
                                clusterMatrix[tmpNumberOfDetectedClusters-1] = tmpInputVector;

                            } else {
                                for(int m = 0; m< numberOfComponents; m++) {
                                    if(clusterMatrix[tmpWinnerClassIndex][m] <=thresholdForContrastEnhancement) {
                                        tmpInputVector[m] = 0; // v
                                    }
                                }
                                float tmpLength3 = this.getVectorLength(tmpInputVector).get(1);
                                System.out.println(tmpLength3 +"laenge 3");
                                float tmpFactor1 = defaultLearningParameter / tmpLength3;
                                float tmpFactor2 = 1- defaultLearningParameter;
                                for(int j = 0; j< numberOfComponents; j++) {
                                    tmpInputVector[j] = tmpInputVector[j] * tmpFactor1 + tmpFactor2 * clusterMatrix[tmpWinnerClassIndex][j]; // ergbnis t
                                }
                                tmpLenght4 = this.getVectorLength(tmpInputVector).get(1);
                                for(int z = 0; z<tmpInputVector.length; z++) {
                                    tmpInputVector[z] *= (1/tmpLenght4);
                                }
                                clusterMatrix[tmpWinnerClassIndex] = tmpInputVector;
                                tmpClassView[tmpSampleVectorIndicesInRandomOrder[h]] = tmpWinnerClassIndex;
                            }

                        }
                    }
                }
                System.out.println("the end");
                System.out.println(java.util.Arrays.toString(tmpClassView) + " ----classView end ");
                System.out.println(java.util.Arrays.toString(clusterMatrix[0]) + "----1.Zeile");
                System.out.println(java.util.Arrays.toString(clusterMatrix[1]) + "----2.Zeile");
                System.out.println(java.util.Arrays.toString(clusterMatrix[2]) + "----3.Zeile");
                System.out.println(java.util.Arrays.toString(clusterMatrix[3]) + "----4.Zeile");
                System.out.println(java.util.Arrays.toString(clusterMatrix[4]) + "----5.Zeile");
                System.out.println("anzahl epoche: " + currentNumberOfEpochs);
                System.out.println("\n");
                convegence = this.checkConvergence(tmpNumberOfDetectedClusters);

            }

        }

        } catch(Exception anException){
            throw new Exception("Clusstering failed!");

    }


        return null;
    }

    private void checkDataMatrix(float[][] aDataMatrix) {
        Objects.requireNonNull(aDataMatrix, "aDataMatrix is null.");
        if (aDataMatrix.length <= 0) {
            throw new IllegalArgumentException("The number of vectors must be greater then 0 to cluster inputs");
        }
        int tmpNumberOfVectorComponents = aDataMatrix[0].length;
        float tmpCurrentMatrixComponent;
        float[] row;
        HashMap<float[],Integer> map = new HashMap<>();
        ArrayList<Integer> liste = new ArrayList<>();
        for (int i = 0; i < aDataMatrix.length; i++) {
            row = aDataMatrix[i];
            if (tmpNumberOfVectorComponents != row.length) {
                throw new IllegalArgumentException("the vectors must be have the same length!");
            }
            for (int j = 0; j < row.length; j++) {
                tmpCurrentMatrixComponent = row[j];
                if (tmpCurrentMatrixComponent < 0 || tmpCurrentMatrixComponent > 1) {// TODO: @Betuel check null?
                    map.put(aDataMatrix[i],i);
                    liste.add(i);
                }
            }
        }
        scaleInout(map,liste);
    }
    private  void scaleInout(HashMap<float[],Integer> aMap, ArrayList<Integer> alist) {
        for (float[] a : aMap.keySet()) {
            float v = a[0];
            for (float w : a) {
                if (w > a[0]) {
                    v = w;
                }
            }
            for (int i = 0; i < a.length; i++) {
                float yeni = a[i] / v;
                a[i] = yeni;
                dataMatrix[aMap.get(a)] = a;
            }
            System.out.println(v + "--max");
        }
    }
    private void initialiseMatrices() {
        clusterMatrix = new float[numberOfVectors][numberOfComponents];
        clusterMatrixPreviousEpoch = new float[numberOfVectors][numberOfComponents];
        tmpSampleVectorIndicesInRandomOrder = new int[numberOfVectors];

    }
    private void randomizeVectorIndices() {;
       // int[] tmpSampleVectorIndicesInRandomOrder = new int[numberOfVectors];
        try {
            // fills array with values from 0 - mNumberOfVectors -1
            for (int i = 0; i < numberOfVectors; i++) {
                tmpSampleVectorIndicesInRandomOrder[i] = i; // bsp. [1,2,3]
            }
            Random rnd = new Random(tmpSeed); // warum 1?
            tmpSeed++;
            int numberOfIterations = (numberOfVectors / 2) + 1;
            int randomIndex1;
            int randomIndex2;
            int buffer;

            for (int j = 0; j < numberOfIterations; j++) {
                randomIndex1 = (int) (numberOfVectors * rnd.nextDouble());
                randomIndex2 = (int) (numberOfVectors * rnd.nextDouble());

                buffer = tmpSampleVectorIndicesInRandomOrder[randomIndex1];
                tmpSampleVectorIndicesInRandomOrder[randomIndex1] = tmpSampleVectorIndicesInRandomOrder[randomIndex2];
                tmpSampleVectorIndicesInRandomOrder[randomIndex2] = buffer;
            }
            System.out.println(java.util.Arrays.toString(tmpSampleVectorIndicesInRandomOrder) + "---random order123");
        } catch (Exception exception) {
            throw new RuntimeException("Unable to initialize the array for selection of a random vector!", exception);
        }
        //return tmpSampleVectorIndicesInRandomOrder;
    }
    private ArrayList<Float> getVectorLength(float[] aSampleVector) {
        float tmpVectorComponentsSqrtSum = 0;
        float tmpVectorLength;
        float tmpSumComp = 0;
        ArrayList<Float> list = new ArrayList<>();
        for(int i = 0; i<aSampleVector.length; i++) {
            tmpVectorComponentsSqrtSum += aSampleVector[i] * aSampleVector[i];
            tmpSumComp += aSampleVector[i];
          //  list.add(tmpVectorComponentsSqrtSum);
        }
        list.add(tmpSumComp);
        /* Calling the root function at this point is unnecessary, since sqrt(0) = 0. */
        if(tmpVectorComponentsSqrtSum == 0) {
            //return 0;
            throw new ArithmeticException("Exception");
        }
        else {
            tmpVectorLength = (float) Math.sqrt(tmpVectorComponentsSqrtSum);
            list.add(tmpVectorLength);
          //  return tmpVectorLength;
        }
        return list;
    }
    public boolean checkConvergence(int mNumberOfDetectedClasses) throws Exception {
        try {
            boolean convergenceFlag = true;
            if (convergenceFlag) {
                System.out.println("erste if");
                // Check convergence by evaluating the similarity of the classvectors of this and the previous epoch
                convergenceFlag = true;
                double scalarProductOfClassVector;
                System.out.println(mNumberOfDetectedClasses + "---convergence anzahl klassen");
                float[] row;
                float[] oldrow;
                for (int i = 0; i < mNumberOfDetectedClasses; i++) {
                    System.out.println("deneme");
                    scalarProductOfClassVector = 0;
                    row = clusterMatrix[i];
                    System.out.println(java.util.Arrays.toString(row) + "----------------aktuelle clustermatrix zeile");
                    oldrow = clusterMatrixPreviousEpoch[i];
                    System.out.println(java.util.Arrays.toString(oldrow) + "------------------alte zeile clsutermatrix");

                    for (int j = 0; j < numberOfComponents; j++) {
                        //System.out.println(java.util.Arrays.toString(this.mClassMatrix));
                      //  scalarProductOfClassVector += clusterMatrix[i][j] * clusterMatrixPreviousEpoch[i][j];
                        System.out.println(row[j] + "--row new");
                        System.out.println(oldrow[j] +"---alt row");
                        scalarProductOfClassVector += row[j] * oldrow[j];
                        System.out.println(scalarProductOfClassVector + "---skalarprodukt");
                    }
                    if (scalarProductOfClassVector < 0.99f) { //this.mRequiredSimilarity
                        System.out.println("skalarprodukt kleiner als mindest aenlichkeit");
                        convergenceFlag = false;
                        //i = this.mNumberOfComponents;
                        //	System.out.println(i + "---i");
                        //	continue;
                        break;
                    }
                    System.out.println("hier");
                    System.out.println(convergenceFlag);
                }
                if (!convergenceFlag) {
                    for (int i = 0; i < mNumberOfDetectedClasses; i++) {
                        for (int j = 0; j < numberOfComponents; j++) {
                            clusterMatrixPreviousEpoch[i][j] = clusterMatrix[i][j]; // kopieren der vektoren in mClassMatrix in mClassMatrixOld, um die matrizen in der nächsten epcohe vergleichen zu können.
                        }
                    }
                }
            }
            return convergenceFlag;
        } catch (Exception exception) {
            throw new Exception("The classification failed! Unable to check the convergence of the current classification.", exception);
        }
    }

}
