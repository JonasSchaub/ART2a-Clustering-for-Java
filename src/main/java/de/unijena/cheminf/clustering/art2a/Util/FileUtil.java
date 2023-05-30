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

package de.unijena.cheminf.clustering.art2a.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File utility
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public final class FileUtil {
    //<editor-fold desc="Private static final class variables" defaultstate="collapsed">
    /**
     * Name of file for writing clustering process.
     */
    private static final String CLUSTERING_PROCESS_FILE_NAME = "Clustering_Process";
    /**
     * Name of the file for writing clustering result.
     */
    private static final String CLUSTERING_RESULT_FILE_NAME = "Clustering_Result";
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * The working directory
     */
    private static String workingPath;
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Public static methods">
    /**
     * Set up clustering result file.
     * If necessary, existing result files will also be deleted.
     *
     * @return PrintWriter to write the clustering result into the file.
     * @throws IOException is thrown if an error occurs when creating the file.
     */
    public static PrintWriter createClusteringResultInFile(String aPathName) {
        PrintWriter tmpPrintWriter = null;
        try {
            FileWriter tmpFileWriter = new FileWriter(FileUtil.createClusteringResultFiles(FileUtil.CLUSTERING_RESULT_FILE_NAME, aPathName), false); // Ergebnis der methode ist eij File
            BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter); // used BufferedWriter als Puffer, um die Leistung zu steigern, da die textdatein sehr groß werden können
            tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
            FileUtil.deleteOldestFileIfNecessary(FileUtil.workingPath + File.separator + FileUtil.CLUSTERING_RESULT_FILE_NAME);
        } catch (IOException anException) {
            FileUtil.LOGGER.log(Level.SEVERE, "The file could not be created.");

        }
        return tmpPrintWriter;
    }
    //
    /**
     * Set up clustering process file.
     * If necessary, existing process files will also be deleted.
     *
     * @return PrintWriter to write the clustering process into the file.
     * @throws IOException is thrown if an error occurs when creating the file.
     */
    public static PrintWriter createClusteringProcessInFile(String aPathName) {
        PrintWriter tmpPrintWriter = null;
        try {
            FileWriter tmpFileWriter = new FileWriter(FileUtil.createClusteringResultFiles(FileUtil.CLUSTERING_PROCESS_FILE_NAME, aPathName), false);
            BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
            tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
            FileUtil.deleteOldestFileIfNecessary(FileUtil.workingPath + File.separator + FileUtil.CLUSTERING_PROCESS_FILE_NAME);
        } catch (IOException anException) {
            FileUtil.LOGGER.log(Level.SEVERE, "The file could not be created.");
        }
        return tmpPrintWriter;
    }
    //
    /**
     * The text file contains fingerprints that are read in to prepare them for clustering.
     * Each line of the text file represents one fingerprint. Each component of the fingerprint is
     * separated by a separator. The file has no header line.
     *
     * @param aFilePath path of the text file
     * @param aSeparator separator of the text file to separate the fingerprint components from each other.
     * @return float matrix is returned that contains the fingerprints that were read in.
     * Each row of the matrix represents one fingerprint.
     * @throws IllegalArgumentException is thrown if the given file path is invalid.
     */
    public static float[][] importDataMatrixFromTextFile(String aFilePath, char aSeparator) throws IllegalArgumentException, IOException {
        if (aFilePath == null || aFilePath.isEmpty() || aFilePath.isBlank()) {
            throw new IllegalArgumentException("aFileName is null or empty/blank.");
        }
        BufferedReader tmpFingerprintFileReader = null;
        try {
            tmpFingerprintFileReader = new BufferedReader(new FileReader(aFilePath));
        } catch (IOException anException) {
            FileUtil.LOGGER.log(Level.SEVERE, anException.toString()+ " File is not readable!", anException);
        } finally {
            try {
                tmpFingerprintFileReader.close();
            } catch (IOException anException) {
                FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException + "FileReader can not close.");
            }
        }
        ArrayList<float[]> tmpFingerprintList = new ArrayList<>();
        String tmpFingerprintLine;
        int tmpDataMatrixRow = 0;
        try {
            while ((tmpFingerprintLine = tmpFingerprintFileReader.readLine()) != null) {
                String[] tmpFingerprint = tmpFingerprintLine.split(String.valueOf(aSeparator));
                float[] tmpFingerprintFloatArray = new float[tmpFingerprint.length];
                try {
                    for (int i = 0; i < tmpFingerprint.length; i++) {
                        tmpFingerprintFloatArray[i] = Float.parseFloat(tmpFingerprint[i]);
                    }
                } catch (NumberFormatException anException) {
                    FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException);
                    //throw anException;
                }
                tmpDataMatrixRow++;
                tmpFingerprintList.add(tmpFingerprintFloatArray);
            }
        } catch (IOException anException) {
            FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException + " invalid fingerprint file. At least one line is not readable.");
        }
        finally {
            try {
                tmpFingerprintFileReader.close();
            } catch (IOException anException) {
                FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException + " FileReader can not close.");
            }
        }
        float[][] aDataMatrix = new float[tmpDataMatrixRow][tmpFingerprintList.get(0).length];
        for (int tmpCurrentMatrixRow = 0; tmpCurrentMatrixRow < tmpDataMatrixRow; tmpCurrentMatrixRow++) {
            aDataMatrix[tmpCurrentMatrixRow] = tmpFingerprintList.get(tmpCurrentMatrixRow);
        }
        return aDataMatrix;
    }
    //
    /**
     * Clustering files are deleted when their number is greater than 10.
     *
     * @param aPathName of the clustering files.
     */
    private static void deleteOldestFileIfNecessary(String aPathName) {
        File tmpDirectory = new File(aPathName); // "src/test/resources/de/unijena/cheminf/clustering/art2a/Process_Clustering_Log"
        File[] tmpClusteringFiles = tmpDirectory.listFiles();
        if (tmpClusteringFiles != null && tmpClusteringFiles.length > 10) { // magic number
            Arrays.sort(tmpClusteringFiles, Comparator.comparingLong(File::lastModified));
            if (tmpClusteringFiles[0].delete()) {
                FileUtil.LOGGER.info("Deleted file: " + tmpClusteringFiles[0].getName());
            } else {
                FileUtil.LOGGER.info("Deleted file: " + tmpClusteringFiles[0].getName());
            }
        }
    }
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Private static methods">
    /**
     * Clustering files are generated.
     *
     * @param aFileFolderName for clustering files typ.
     * @return File
     */
    private static File createClusteringResultFiles(String aFileFolderName, String aPathName){
        LocalDateTime tmpDateTime = LocalDateTime.now();
        String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm"));
        FileUtil.workingPath = (new File(aPathName) + File.separator);
        new File(FileUtil.workingPath + aFileFolderName).mkdirs();
        File tmpClusteringReportFile = new File(FileUtil.workingPath + File.separator + aFileFolderName + File.separator
                + aFileFolderName + "_"+tmpProcessingTime+ ".txt");
        return tmpClusteringReportFile;
    }
    //</editor-fold>
}
