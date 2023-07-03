/*
 * ART2a Clustering for Java
 * Copyright (C) 2023 Betuel Sevindik, Felix Baensch, Jonas Schaub, Christoph Steinbeck, and Achim Zielesny
 *
 * Source code is available at <https://github.com/JonasSchaub/ART2a-Clustering-for-Java>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unijena.cheminf.clustering.art2a.exceptions;

/**
 * An exception thrown when convergence fails. This exception occurs when the system is unable to, achieve a
 * convergent state or meet the desired convergence criteria.The ConvergenceFailedException is a special type of
 * Exception and inherits from this class.
 * It can be used to handle convergence failures in order to take appropriate action.
 *
 * @author Betuel Sevindik
 * @version 1.0.0.0
 */
public class ConvergenceFailedException extends Exception {
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor.
     */
    public ConvergenceFailedException() {
        super("Convergence failed!");
    }
    //
    /**
     * Constructor.
     *
     * @param anErrorMessage error message is displayed, when the exception is thrown.
     */
    public ConvergenceFailedException(String anErrorMessage) {
        super(anErrorMessage);
    }
    //</editor-fold>
}
