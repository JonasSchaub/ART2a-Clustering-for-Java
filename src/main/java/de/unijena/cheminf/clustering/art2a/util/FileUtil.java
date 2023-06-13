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

package de.unijena.cheminf.clustering.art2a.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File utility.
 * The class provides convenience methods.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public final class FileUtil {
    //<editor-fold desc="Private static final class variables" defaultstate="collapsed">
    /**
     * Name of file for exporting clustering process.
     */
    private static final String CLUSTERING_PROCESS_FILE_NAME = "Clustering_Process";
    /**
     * Name of the file for exporting clustering result.
     */
    private static final String CLUSTERING_RESULT_FILE_NAME = "Clustering_Result";
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Public static methods">
    /**
     * Set up the clustering result text file writers. This method creates a set of Writer objects based on the
     * specified and desired Writer typ for writing the clustering result and clustering process to
     * separate text files. This methode allows the user to create 3 different Writer types, e.g. FileWriter,
     * PrintWriter and BufferedWriter. The user can specify the Writer typ via the aWriterClass parameter.
     * The file names will include a timestamp to make them unique.
     * The method creates the necessary files in the specified path.
     *
     * If necessary, existing result files will also be deleted.
     *
     * @param aPathName path to the export folder where the text files are to be saved.
     * @param aWriterClass Writer ytp to be created. The user can choose one of the supported classes.
     * @param <T> generic typ of the Writer. This will be determined, based on the specified aWriterClass parameter.
     * @return PrintWriter[] an array of a Writer objects,
     * where index 0 corresponds to the clustering result Writer,
     * and index 1 corresponds to the clustering process Writer.
     */
    public static <T extends java.io.Writer> T[] setUpClusteringResultTextFilePrinters(String aPathName, Class<T> aWriterClass) {
        T tmpClusteringResultWriter = null;
        T tmpClusteringProcessWriter = null;
        String tmpWorkingPath;
        try {
            LocalDateTime tmpDateTime = LocalDateTime.now();
            String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm_ss"));
            tmpWorkingPath = (new File(aPathName) + File.separator);
            new File(tmpWorkingPath + CLUSTERING_RESULT_FILE_NAME).mkdirs();
            File tmpClusteringResultFile = new File(tmpWorkingPath + File.separator + CLUSTERING_RESULT_FILE_NAME +
                    File.separator + CLUSTERING_RESULT_FILE_NAME + "_" + tmpProcessingTime + ".txt");
            if (aWriterClass.equals(PrintWriter.class)) {
                tmpClusteringResultWriter = (T) new PrintWriter(tmpClusteringResultFile);
            } else if (aWriterClass.equals(BufferedWriter.class)) {
                tmpClusteringResultWriter = (T) new BufferedWriter(new FileWriter(tmpClusteringResultFile));
            } else if (aWriterClass.equals(FileWriter.class)) {
                tmpClusteringResultWriter = (T) new FileWriter(tmpClusteringResultFile);
            } else if (aWriterClass.equals(StringWriter.class)) {
                tmpClusteringResultWriter = (T) new StringWriter();
            } else if (aWriterClass.equals(CharArrayWriter.class)) {
                tmpClusteringResultWriter = (T) new CharArrayWriter();
            }
            FileUtil.deleteOldestFileIfNecessary(tmpWorkingPath + File.separator + CLUSTERING_RESULT_FILE_NAME); // TODO optimize the method, if possible!!!
            new File(tmpWorkingPath + CLUSTERING_PROCESS_FILE_NAME).mkdirs();
            File tmpClusteringProcessFile = new File(tmpWorkingPath + File.separator + CLUSTERING_PROCESS_FILE_NAME +
                    File.separator + CLUSTERING_PROCESS_FILE_NAME + "_" + tmpProcessingTime + ".txt");
            if (aWriterClass.equals(PrintWriter.class)) {
                tmpClusteringProcessWriter = (T) new PrintWriter(tmpClusteringProcessFile);
            } else if (aWriterClass.equals(BufferedWriter.class)) {
                tmpClusteringProcessWriter = (T) new BufferedWriter(new FileWriter(tmpClusteringProcessFile));
            } else if (aWriterClass.equals(FileWriter.class)) {
                tmpClusteringProcessWriter = (T) new FileWriter(tmpClusteringProcessFile);
            } else if (aWriterClass.equals(StringWriter.class)) {
                tmpClusteringProcessWriter = (T) new StringWriter();
            } else if (aWriterClass.equals(CharArrayWriter.class)) {
                tmpClusteringProcessWriter = (T) new CharArrayWriter();
            }
            FileUtil.deleteOldestFileIfNecessary(tmpWorkingPath + File.separator + CLUSTERING_PROCESS_FILE_NAME); // TODO optimize the method, if possible !!!
        } catch (IOException e) {
            FileUtil.LOGGER.log(Level.SEVERE, "The files could not be created.");
        }
        T[] tmpWriterArray = (T[]) Array.newInstance(aWriterClass, 2);
        tmpWriterArray[0] = tmpClusteringResultWriter;
        tmpWriterArray[1] = tmpClusteringProcessWriter;
        return tmpWriterArray;
    }
    //
    /**
     * The text file contains fingerprints that are read in to prepare them for float clustering.
     * Each line of the text file represents one fingerprint. Each component of the fingerprint is
     * separated by a separator. The file has no header line.
     *
     * @param aFilePath path of the text file
     * @param aSeparator separator of the text file to separate the fingerprint components from each other.
     * @return float matrix is returned that contains the fingerprints that were read in.
     * Each row of the matrix represents one fingerprint.
     * @throws IllegalArgumentException is thrown if the given file path is invalid.
     */
    public static float[][] importFloatDataMatrixFromTextFile(String aFilePath, char aSeparator) throws IllegalArgumentException {
        if (aFilePath == null || aFilePath.isEmpty() || aFilePath.isBlank()) {
            throw new IllegalArgumentException("aFileName is null or empty/blank.");
        }
        BufferedReader tmpFingerprintFileReader = null;
        ArrayList<float[]> tmpFingerprintList = new ArrayList<>();
        String tmpFingerprintLine;
        int tmpDataMatrixRow = 0;
        try {
            tmpFingerprintFileReader = new BufferedReader(new FileReader(aFilePath));
            while ((tmpFingerprintLine = tmpFingerprintFileReader.readLine()) != null) {
                String[] tmpFingerprint = tmpFingerprintLine.split(String.valueOf(aSeparator));
                float[] tmpFingerprintFloatArray = new float[tmpFingerprint.length];
                for (int i = 0; i < tmpFingerprint.length; i++) {
                    try {
                        tmpFingerprintFloatArray[i] = Float.parseFloat(tmpFingerprint[i]);
                    } catch (NumberFormatException anException) {
                        FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException);
                    }
                }
                tmpDataMatrixRow++;
                tmpFingerprintList.add(tmpFingerprintFloatArray);
            }
        } catch (IOException anException) {
            FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException + " invalid fingerprint file. At least one line is not readable.");
        } finally {
            if (tmpFingerprintFileReader != null) {
                try {
                    tmpFingerprintFileReader.close();
                } catch (IOException anException) {
                   FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException + "The reader could not be closed." );
                }
            }
        }
        float[][] aDataMatrix = new float[tmpDataMatrixRow][tmpFingerprintList.get(0).length];
        for (int tmpCurrentMatrixRow = 0; tmpCurrentMatrixRow < tmpDataMatrixRow; tmpCurrentMatrixRow++) {
            aDataMatrix[tmpCurrentMatrixRow] = tmpFingerprintList.get(tmpCurrentMatrixRow);
        }
        return aDataMatrix;
    }
    /**
     * The text file contains fingerprints that are read in to prepare them for double clustering.
     * Each line of the text file represents one fingerprint. Each component of the fingerprint is
     * separated by a separator. The file has no header line.
     *
     * @param aFilePath path of the text file
     * @param aSeparator separator of the text file to separate the fingerprint components from each other.
     * @return double matrix is returned that contains the fingerprints that were read in.
     * Each row of the matrix represents one fingerprint.
     * @throws IllegalArgumentException is thrown if the given file path is invalid.
     */
    public static double[][] importDoubleDataMatrixFromTextFile(String aFilePath, char aSeparator) throws IllegalArgumentException {
        if (aFilePath == null || aFilePath.isEmpty() || aFilePath.isBlank()) {
            throw new IllegalArgumentException("aFileName is null or empty/blank.");
        }
        BufferedReader tmpFingerprintFileReader = null;
        ArrayList<double[]> tmpFingerprintList = new ArrayList<>();
        String tmpFingerprintLine;
        int tmpDataMatrixRow = 0;
        try {
            tmpFingerprintFileReader = new BufferedReader(new FileReader(aFilePath));
            while ((tmpFingerprintLine = tmpFingerprintFileReader.readLine()) != null) {
                String[] tmpFingerprint = tmpFingerprintLine.split(String.valueOf(aSeparator));
                double[] tmpFingerprintDoubleArray = new double[tmpFingerprint.length];
                for (int i = 0; i < tmpFingerprint.length; i++) {
                    try {
                        tmpFingerprintDoubleArray[i] = Double.parseDouble(tmpFingerprint[i]);
                    } catch (NumberFormatException anException) {
                        FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException);
                    }
                }
                tmpDataMatrixRow++;
                tmpFingerprintList.add(tmpFingerprintDoubleArray);
            }
        } catch (IOException anException) {
            FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException + " invalid fingerprint file. At least one line is not readable.");
        } finally {
            if (tmpFingerprintFileReader != null) {
                try {
                    tmpFingerprintFileReader.close();
                } catch (IOException anException) {
                    FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException + "The reader could not be closed." );
                }
            }
        }
        double[][] tmpDataMatrix = new double[tmpDataMatrixRow][tmpFingerprintList.get(0).length];
        for (int tmpCurrentMatrixRow = 0; tmpCurrentMatrixRow < tmpDataMatrixRow; tmpCurrentMatrixRow++) {
            tmpDataMatrix[tmpCurrentMatrixRow] = tmpFingerprintList.get(tmpCurrentMatrixRow);
        }
        return tmpDataMatrix;
    }
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Private static methods">
    /**
     * Clustering files are deleted when their number is greater than 10.
     *
     * @param aPathName of the clustering files.
     */
    private static void deleteOldestFileIfNecessary(String aPathName) {
        File tmpDirectory = new File(aPathName);
        File[] tmpClusteringFiles = tmpDirectory.listFiles();
        if (tmpClusteringFiles != null && tmpClusteringFiles.length > 10) { // magic number
            Arrays.sort(tmpClusteringFiles, Comparator.comparingLong(File::lastModified));
            if (tmpClusteringFiles[0].delete()) {
                FileUtil.LOGGER.log(Level.INFO,"Deleted file: " + tmpClusteringFiles[0].getName());
            } else {
                FileUtil.LOGGER.log(Level.INFO,"Deleted file: " + tmpClusteringFiles[0].getName());
            }
        }
    }
    //</editor-fold>
}
