package de.unijena.cheminf.clustering.art2a.Logger;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logger {
   // protected  ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();
   private static final String RESULTS_FILE_NAME = "Results";

    private  ArrayList<ArrayList<String>> logQueue1;
    private ArrayList<String> logQueue;

    public Logger() {
    }


    /**
     *
     */
    public PrintWriter createFile() throws IOException {
        String tmpWorkingPath = (new File("").getAbsoluteFile().getAbsolutePath()) + File.separator;
        LocalDateTime tmpDateTime = LocalDateTime.now();
        String tmpProcessingTime = tmpDateTime.format(DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm"));
        new File(tmpWorkingPath + "/Results").mkdirs();
        File tmpResultsLogFile = new File(tmpWorkingPath + "/Results/" + Logger.RESULTS_FILE_NAME + tmpProcessingTime + ".txt");
        FileWriter tmpResultsLogFileWriter = new FileWriter(tmpResultsLogFile, true);
        PrintWriter tmpResultsPrintWriter = new PrintWriter(tmpResultsLogFileWriter);
        /*
        tmpResultsPrintWriter.println("--------------------------------");
        tmpResultsPrintWriter.println("ART2a Clustering Results:");
        tmpResultsPrintWriter.println("--------------------------------");

         */
        return tmpResultsPrintWriter;
    }

    /**
     *
     * @param aMessage
     */
    public synchronized void appendIntermediateResult(String aMessage) {
        this.logQueue.add(aMessage);
    }

    public void listeErzeugen(ArrayList<String> arrayList1) {
        this.logQueue = arrayList1;
    }
    public synchronized void appendExceptionMessage(String aMessage, Exception anException) {
       // this.logQueue.clear();
        this.logQueue.add(anException + ": " + aMessage);
    }
    public void appendFinalResult(String aMessage) {
        this.logQueue.add(aMessage);
    }

}
