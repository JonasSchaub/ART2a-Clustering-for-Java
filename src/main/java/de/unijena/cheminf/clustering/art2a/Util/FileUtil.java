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
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * File utility
 * //TODO possibly delete class
 *
 * @author Betuel Sevindik
 */
public final class FileUtil {
    //<editor-fold desc="Private static class variables" defaultstate="collapsed">
    /**
     * Name of file for writing clustering process.
     */
    private static final String PROCESS_LOG_FILE_NAME = "Process_Log";
    /**
     * Name of the file for writing clustering result.
     */
    private static final String RESULT_FILE_NAME = "Result_Log";
    private static ArrayList<File> fileLists = new ArrayList<>();
    private static final Logger ROOT_LOGGER = LogManager.getLogManager().getLogger("");
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Public static methods">
    /**
     * Set up process log file.
     *
     * @return PrintWriter to write the clustering process into the file.
     * @throws IOException is thrown if an error occurs when creating the file.
     */
    public static PrintWriter createResultLogFile() throws IOException {
        /*
        LOGGER.info("start create Process log");
        FileUtil.deleteOldestFileIfNecessary("src/test/resources/de/unijena/cheminf/clustering/art2a/Process_Clustering_Log");
        LocalDateTime tmpDateTime = LocalDateTime.now();
        String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm"));
        String tmpWorkingPath = (new File("src/test/resources/de/unijena/cheminf/clustering/art2a").getAbsoluteFile().getAbsolutePath()) + File.separator;
        new File("src/test/resources/de/unijena/cheminf/clustering/art2a" + "/Results_Clustering_Log").mkdirs();
        File tmpClusteringResultFile = new File(tmpWorkingPath + "/Results_Clustering_Log/"
                + FileUtil.PROCESS_LOG_FILE_NAME + tmpProcessingTime + ".txt");

         */
        FileUtil.deleteOldestFileIfNecessary("src/test/resources/de/unijena/cheminf/clustering/art2a/Result_Clustering_Log");
        FileWriter tmpFileWriter = new FileWriter(FileUtil.createOutputFile("src/test/resources/de/unijena/cheminf/clustering/art2a", "Result_Clustering_Log"), false);
        BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
        PrintWriter tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
        return tmpPrintWriter;
    }
    //
    /**
     * Set up result log file.
     *
     * @return PrintWriter to write the clustering result into the file.
     * @throws IOException is thrown if an error occurs when creating the file.
     */
    public static PrintWriter createProcessLogFile() throws IOException {

        /*
        FileUtil.deleteOldestFileIfNecessary("src/test/resources/de/unijena/cheminf/clustering/art2a/Result_Clustering_Log");
        LocalDateTime tmpDateTime = LocalDateTime.now();
        String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm"));
        String tmpWorkingPath = (new File("src/test/resources/de/unijena/cheminf/clustering/art2a").getAbsoluteFile().getAbsolutePath()) + File.separator;
        new File("src/test/resources/de/unijena/cheminf/clustering/art2a" + "/Process_Clustering_Log").mkdirs();
        File tmpClusteringResultFile = new File(tmpWorkingPath + "/Process_Clustering_Log/"
                + FileUtil. RESULT_FILE_NAME + tmpProcessingTime+ ".txt");

         */


      //  FileUtil.createOutputFile("src/test/resources/de/unijena/cheminf/clustering/art2a"+ "/Process_Clustering_Log","Process_Clustering_Log");
        FileWriter tmpFileWriter = new FileWriter(FileUtil.createOutputFile("src/test/resources/de/unijena/cheminf/clustering/art2a","Process_Clustering_Log"), false);
        BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
        PrintWriter  tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
        FileUtil.createOutputFile("src/test/resources/de/unijena/cheminf/clustering/art2a"+ "/Process_Clustering_Log","Process_Clustering_Log");
        return tmpPrintWriter;
    }
    public static float[][] importDataMatrixFromFile(String aFilePath, char aSeparator) throws IOException, NumberFormatException {
        if (aFilePath == null || aFilePath.isEmpty() || aFilePath.isBlank()) {
            throw new IllegalArgumentException("aFileName is null or empty/blank.");
        }
        BufferedReader tmpFingerprintFileReader = null;
        try {
           // BufferedReader tmpFingerprintFileReader;
            tmpFingerprintFileReader = new BufferedReader(new FileReader(aFilePath));
        } catch (IOException anException) {
            LOGGER.log(Level.SEVERE, anException.toString()+ " File is not readable!", anException);
        }
        List<float[]> tmpFingerprintList = new ArrayList<>();
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
                    LOGGER.log(Level.SEVERE, anException.toString(), anException);
                    throw anException;
                }
                tmpDataMatrixRow++;
                tmpFingerprintList.add(tmpFingerprintFloatArray);
            }
        } catch (IOException anException) {
            LOGGER.log(Level.SEVERE, anException.toString(), anException + "invalid fingerprint file. At least one line is not readable.");
        }
        finally {
            tmpFingerprintFileReader.close();
        }
       /// tmpFingerprintFileReader.close();
        float[][] aDataMatrix = new float[tmpDataMatrixRow][tmpFingerprintList.get(0).length];
        for (int tmpCurrentMatrixRow = 0; tmpCurrentMatrixRow < tmpDataMatrixRow; tmpCurrentMatrixRow++) {
            aDataMatrix[tmpCurrentMatrixRow] = tmpFingerprintList.get(tmpCurrentMatrixRow);
        }
        return aDataMatrix;
    }
    private static void deleteOldestFileIfNecessary(String aPathName) {
        File dir = new File(aPathName); // "src/test/resources/de/unijena/cheminf/clustering/art2a/Process_Clustering_Log"
        File[] files = dir.listFiles();
        if (files != null && files.length > 3) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            if (files[0].delete()) {
                System.out.println("Deleted file: " + files[0].getName());
                LOGGER.info("Deleted file: " + files[0].getName());
            } else {
                System.out.println("Failed to delete file: " + files[0].getName());
                LOGGER.info("Deleted file: " + files[0].getName());
            }
        }
    }
    public static void createLoggingFile()  {
        try {
            FileUtil.deleteOldestFileIfNecessary("src/test/resources/de/unijena/cheminf/clustering/art2a/Logging_ART2a");
            LocalDateTime tmpDateTime = LocalDateTime.now();
            String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm"));
            String tmpWorkingPath = (new File("src/test/resources/de/unijena/cheminf/clustering/art2a").getAbsoluteFile().getAbsolutePath()) + File.separator;
            new File("src/test/resources/de/unijena/cheminf/clustering/art2a" + "/Logging_ART2a").mkdirs();
        /*
        File tmpClusteringResultFile = new File(tmpWorkingPath + "/Logging_ART2a/"
                + FileUtil. RESULT_FILE_NAME + tmpProcessingTime+ ".txt");

         */
            FileHandler handler = new FileHandler(tmpWorkingPath + "/Logging_ART2a/"
                    + FileUtil.RESULT_FILE_NAME + tmpProcessingTime + ".txt", true);
            handler.setFormatter(new SimpleFormatter());
            ROOT_LOGGER.addHandler(handler);
        }catch (IOException anException) {
            FileUtil.LOGGER.log(Level.SEVERE, anException.toString(), anException);
        }
    }
    private static File createOutputFile(String aFileName, String den){
        LocalDateTime tmpDateTime = LocalDateTime.now();
        String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm"));
        String tmpWorkingPath = (new File(aFileName).getAbsoluteFile().getAbsolutePath()) + File.separator;
        new File(aFileName + "/" +den).mkdirs();
        File tmpClusteringResultFile = new File(tmpWorkingPath + "/"+den+"/"
                + FileUtil. RESULT_FILE_NAME + tmpProcessingTime+ ".txt");
        return tmpClusteringResultFile;
    }

    //</editor-fold>
    //

}
