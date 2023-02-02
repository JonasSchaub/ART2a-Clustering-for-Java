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

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Logger.
 * Stores clustering results.
 *
 * @author Betuel Sevindik
 */
public class Logger {
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     *
     */
    private ConcurrentLinkedQueue<String> logQueue;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Empty constructor">
    /**
     * Empty constructor.
     */
    public Logger() {
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Add clustering info/result.
     *
     * @param aIntermediateResult added information.
     */
    public synchronized void appendIntermediateResult(String aIntermediateResult) {
        this.logQueue.add(aIntermediateResult);
    }
    //
    /**
     * Initialization log queue.
     *
     * @param aLogList add all information and clustering results.
     */
    public void startResultLog(ConcurrentLinkedQueue<String> aLogList) {
        this.logQueue = aLogList;
    }
    //
    /**
     *
     * @param aMessage
     */
    public synchronized void appendFinalResult(String aMessage) {
        this.logQueue.add(aMessage);
    }
    // </editor-fold>
    //
}
