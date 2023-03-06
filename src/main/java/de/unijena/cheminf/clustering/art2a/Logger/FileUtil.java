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

package de.unijena.cheminf.clustering.art2a.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Public static methods">
    /**
     * Set up process log file.
     *
     * @return PrintWriter to write the clustering process into the file.
     * @throws IOException is thrown if an error occurs when creating the file.
     */
    public static PrintWriter createProcessLogFile() throws IOException {
        String tmpWorkingPath = (new File("src/test/resources/de/unijena/cheminf/clustering/art2a").getAbsoluteFile().getAbsolutePath()) + File.separator;
        new File("src/test/resources/de/unijena/cheminf/clustering/art2a" + "/Results_Clustering").mkdirs();
        File tmpClusteringResultFile = new File(tmpWorkingPath + "/Results_Clustering/"
                + FileUtil.PROCESS_LOG_FILE_NAME + ".txt");
        FileWriter tmpFileWriter = new FileWriter(tmpClusteringResultFile, false);
        BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
        PrintWriter  tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
        return tmpPrintWriter;
    }
    //
    /**
     * Set up result log file.
     *
     * @return PrintWriter to write the clustering result into the file.
     * @throws IOException is thrown if an error occurs when creating the file.
     */
    public static PrintWriter createResultLogFile() throws IOException {
        String tmpWorkingPath = (new File("src/test/resources/de/unijena/cheminf/clustering/art2a").getAbsoluteFile().getAbsolutePath()) + File.separator;
        new File("src/test/resources/de/unijena/cheminf/clustering/art2a" + "/Results_Clustering").mkdirs();
        File tmpClusteringResultFile = new File(tmpWorkingPath + "/Results_Clustering/"
                + FileUtil. RESULT_FILE_NAME+ ".txt");
        FileWriter tmpFileWriter = new FileWriter(tmpClusteringResultFile, false);
        BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
        PrintWriter  tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
        return tmpPrintWriter;
    }
    //</editor-fold>
    //
}
