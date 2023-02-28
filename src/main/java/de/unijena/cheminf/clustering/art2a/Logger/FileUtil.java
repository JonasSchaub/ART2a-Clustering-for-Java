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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * File utility
 *
 * @author Betuel Sevindik
 */
public final class FileUtil {
    //<editor-fold desc="Private static class variables" defaultstate="collapsed">
    /**
     * Name of file for writing results
     */
    private static final String RESULT_LOG_FILE_NAME = "Result_Log";
    /**
     * Root logger
     */
    private static final Logger ROOT_LOGGER = LogManager.getLogManager().getLogger("");

    /**
     * Logger of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Public static methods">
    /**
     * Set up result log file
     *
     * @return PrintWriter to write the clustering results into the file
     * @throws IOException is thrown if an error occurs when creating the file.
     */
    public static PrintWriter createResultFile() throws IOException {
        String tmpWorkingPath = (new File("").getAbsoluteFile().getAbsolutePath()) + File.separator;
        LocalDateTime tmpDateTime = LocalDateTime.now();
        String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm"));
        new File(tmpWorkingPath + "/Results").mkdirs();
        File tmpExceptionsLogFile = new File(tmpWorkingPath + "/Results/"
                + FileUtil.RESULT_LOG_FILE_NAME + ".txt");
        FileWriter tmpFileWriter = new FileWriter(tmpExceptionsLogFile, false);
        BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
        PrintWriter  tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
        return tmpPrintWriter;
    }
    //
    /**
     *
     */
    public void manageResultFiles() {
        throw new UnsupportedOperationException();
    }
    //</editor-fold>
    //

}
