package de.unijena.cheminf.clustering.art2a.Logger;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logger {
    protected  ConcurrentLinkedQueue<String> logQueue =  new ConcurrentLinkedQueue<>();
    private static final String RESULTS_FILE_NAME = "Results";

    /**
     *
     */
    public Logger() {
    }

    /**
     *
     */
    public void start() {
        this.logQueue.clear();
        this.logQueue.add("---------------------------------------");
        this.logQueue.add("ART-2a clustering: ");
        this.logQueue.add("---------------------------------------");
        this.logQueue.add("");

    }

    /**
     *
     * @param aMessage
     */
    public void appendIntermediateResult(String aMessage) {
        this.logQueue.add(aMessage);
        System.out.println(this.logQueue);
    }

    /**
     *
     * @param aList
     * @throws IOException
     */
    public void appendStringListToFile(Iterable<String> aList) throws IOException {
        String tmpWorkingPath = (new File("").getAbsoluteFile().getAbsolutePath()) + File.separator;
        LocalDateTime tmpDateTime = LocalDateTime.now();
        String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm"));
        new File(tmpWorkingPath + "/Results").mkdirs();
        File tmpResultsLogFile = new File(tmpWorkingPath + "/Results/" + Logger.RESULTS_FILE_NAME + tmpProcessingTime + ".txt");
        FileWriter tmpResultsLogFileWriter = new FileWriter(tmpResultsLogFile, true);
        PrintWriter tmpResultsPrintWriter = new PrintWriter(tmpResultsLogFileWriter);
        for(String tmpSingleString : this.logQueue) {
            if(tmpSingleString !=null) {
                tmpResultsPrintWriter.println(tmpSingleString);
            }
            else {
                tmpResultsPrintWriter.println("");
            }
        }
        tmpResultsPrintWriter.flush();
        tmpResultsPrintWriter.close();
    }
    public void finish() throws IOException {
        this.appendStringListToFile(this.logQueue);
    }
}
