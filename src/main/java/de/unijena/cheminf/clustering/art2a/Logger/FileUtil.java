package de.unijena.cheminf.clustering.art2a.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtil {
    private static final String RESULTS_FILE_NAME = "Results12";

    String tmpWorkingPath;
    String logFile;

    public FileUtil() throws IOException {

    }

    public boolean createResultFile(File aFile) {
        if (aFile.isFile()) {
            return false;
        } else {
            return true;
        }

    }

    public PrintWriter listToFile() throws IOException {
        boolean test = true;
        // tmpWorkingPath = aDirectoryPath + "LogFile.txt";
        String tmpWorkingPath = (new File("").getAbsoluteFile().getAbsolutePath()) + File.separator;
        new File(tmpWorkingPath + "/Fingerprints").mkdirs();
        File tmpResultsLogFile = new File(tmpWorkingPath + "/Fingerprints/" + FileUtil.RESULTS_FILE_NAME + ".txt");
        if (tmpResultsLogFile.exists()) {
            System.out.println("Hallo");
            tmpResultsLogFile.delete();
        }
        // this.createResultFile(tmpResultsLogFile);


        PrintWriter tmpPrintWriter = null;
        if (this.createResultFile(tmpResultsLogFile)) {
            FileWriter tmpFileWriter = new FileWriter(tmpResultsLogFile);
            BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
            tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
        } else {
            FileWriter tmpFileWriter = new FileWriter(tmpResultsLogFile, true);
            BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter);
            tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
        }
        return tmpPrintWriter;

    }
}
