[![DOI](https://zenodo.org/badge/580826376.svg)](https://zenodo.org/badge/latestdoi/580826376)
# ART2a-Clustering-for-Java
Implementation of the ART 2-A fingerprint clustering algorithm in Java.

## Description
Implementation of the ART 2-A count and bit fingerprint clustering algorithm 
in Java for fast, stable unsupervised clustering for open categorical problems 
in double or single machine precision. ART stands for adaptive resonance theory and 
represents a family of neural models. ART 2-A is a special form of ART that enables 
rapid convergence in clustering. ART is able to adapt to changing environments. 
For clustering, this means that after each assignment of an input to a cluster, 
the model adapts the cluster to the new input.

## Contents of this repository
### Sources
The <a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/tree/main/src">"src"</a> subfolder contains
all source code packages including JUnit tests.

### Tests
The test class <i>Art2aDoubleClusteringTaskTest</i> tests the functionalities of Art-2a in double machine precision
and the test class <i>Art2aFloatClusteringTaskTest</i> in single machine precision.
Methods for the clustering results are also tested.

### Test resources
The test <a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/tree/main/src/test/resources/de/unijena/cheminf/clustering/art2a">"resources"</a> subfolder
contains two text files. The text file named "Bit_Fingerprints.txt" contains 10 bit fingerprints, where each line represents 
one bit fingerprint. And the file named "Count_Fingerprints.txt" contains 6 count fingerprints, where each line represents
one count fingerprint.

## Example initialization and usage of ART2a-Clustering-for-Java
see in <a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/wiki">"wiki"</a>

## Installation
This is a Gradle project. In order to use the source code for your own software, download or clone the repository and
open it in a Gradle-supporting IDE (e.g. IntelliJ) as a Gradle project and execute the build.gradle file.
Gradle will then take care of installing all dependencies. A Java Development Kit (JDK) of version 17 or higher must also
be pre-installed.

## Dependencies
**Needs to be pre-installed:**
* Java Development Kit (JDK) version 17
    * [Adoptium OpenJDK](https://adoptium.net) (as one possible source of the JDK)
* Gradle version 7.3
    * [Gradle Build Tool](https://gradle.org)

**Managed by Gradle:**
* JUnit Jupiter version 5.9.1
    * [JUnit ](https://junit.org/junit5/)
    * License: Eclipse Public License - v 2.0
* Spotless version 6.19
    * [Spotless GitHub repository](https://github.com/diffplug/spotless)
    * License: Apache-2.0 license

## References and useful links
**ART 2-A: An adaptive resonance algorithm for rapid category learning and recognition**
* [ G.A. Carpenter,S. Grossberg and D.B. Rosen, Neural Networks 4 (1991) 493-504](https://www.sciencedirect.com/science/article/abs/pii/0893608091900457)

**An adaptive resonance theory based artificial neural network (ART-2a) for rapid identification of airborne 
particle shapes from their scanning electron microscopy images**
* [D. Wienke et al., Chemoinformatics and Intelligent Laboratory Systems (1994) 367-387](https://www.sciencedirect.com/science/article/abs/pii/0169743994850542)



