[![DOI](https://zenodo.org/badge/580826376.svg)](https://zenodo.org/badge/latestdoi/580826376) 
[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://jonasschaub.github.io/ART2a-Clustering-for-Java/javadoc/latest/index.html)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-blue.svg)](https://GitHub.com/JonasSchaub/ART2a-Clustering-for-Java/graphs/commit-activity)
[![GitHub issues](https://img.shields.io/github/issues/JonasSchaub/ART2a-Clustering-for-Java.svg)](https://GitHub.com/JonasSchaub/ART2a-Clustering-for-Java/issues/)
[![GitHub contributors](https://img.shields.io/github/contributors/JonasSchaub/ART2a-Clustering-for-Java.svg)](https://GitHub.com/JonasSchaub/ART2a-Clustering-for-Java/graphs/contributors/)
[![GitHub release](https://img.shields.io/github/release/JonasSchaub/ART2a-Clustering-for-Java.svg)](https://github.com/JonasSchaub/ART2a-Clustering-for-Java/releases/)
# ART2a-Clustering-for-Java
Implementation of the ART 2-A fingerprint clustering algorithm in Java.

## Description
Implementation of the ART 2-A count and bit fingerprint clustering algorithm 
in Java for fast, stable unsupervised clustering for open categorical problems 
in double or single machine precision. ART stands for "Adaptive Resonance Theory" and 
represents a family of neural models. ART 2-A is a special form of ART that enables 
rapid convergence in clustering. ART is able to adapt to changing environments. 
For clustering, this means that after each assignment of an input to a cluster, 
the model adapts the cluster to the new input.

## Example initialization and usage of ART2a-Clustering-for-Java
See the <a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/wiki">wiki</a> of this repository.

## JavaDoc
The JavaDoc of this library can be found <a href="https://jonasschaub.github.io/ART2a-Clustering-for-Java/javadoc/">here</a>.

## Installation
ART2a-Clustering-for-Java is hosted as a package/artifact on the sonatype maven central repository. See the 
<a href="https://central.sonatype.com/artifact/io.github.jonasschaub/ART2a-Clustering-for-Java/">artifact page</a> for installation
guidelines using build tools like maven or gradle.
<br>
To install ART2a-Clustering-for-Java via its JAR archive, you can get it from the 
<a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/releases">releases</a>. Note that other dependencies 
will need to be installed via JAR archives as well this way.
<br>
In order to open the project locally, e.g. to extend it, download or clone the repository and
open it in a Gradle-supporting IDE (e.g. IntelliJ) as a Gradle project and execute the build.gradle file.
Gradle will then take care of installing all dependencies. A Java Development Kit (JDK) of version 17 or higher must also
be pre-installed.

## Contents of this repository
### Sources
The <a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/tree/main/src">"src"</a> subfolder contains
all source code files including JUnit tests.

### Tests
The test class 
<a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/blob/main/src/test/java/de/unijena/cheminf/clustering/art2a/Art2aDoubleClusteringTest.java">
<i>Art2aDoubleClusteringTest</i></a> tests the functionalities of Art-2a in double machine precision
and the test class 
<a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/blob/main/src/test/java/de/unijena/cheminf/clustering/art2a/Art2aFloatClusteringTest.java">
<i>Art2aFloatClusteringTest</i></a> in single machine precision.
Methods for the clustering results are also tested.

### Test resources
The test 
<a href="https://github.com/JonasSchaub/ART2a-Clustering-for-Java/tree/main/src/test/resources/de/unijena/cheminf/clustering/art2a">"resources"</a> subfolder
contains two text files. The text file named "Bit_Fingerprints.txt" contains 10 bit fingerprints, where each line represents 
one bit fingerprint. And the file named "Count_Fingerprints.txt" contains 6 count fingerprints, where each line represents
one count fingerprint.

## Dependencies for local installation
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
* Javadoc-publisher version 2.4
    * [Javadoc-publisher GitHub repository](https://github.com/MathieuSoysal/Javadoc-publisher.yml)
    * License: Apache-2.0 license

## References and useful links
**ART 2-A: An adaptive resonance algorithm for rapid category learning and recognition**
* [ G.A. Carpenter,S. Grossberg and D.B. Rosen, Neural Networks 4 (1991) 493-504](https://www.sciencedirect.com/science/article/abs/pii/0893608091900457)

**An adaptive resonance theory based artificial neural network (ART-2a) for rapid identification of airborne 
particle shapes from their scanning electron microscopy images**
* [D. Wienke et al., Chemoinformatics and Intelligent Laboratory Systems (1994) 367-387](https://www.sciencedirect.com/science/article/abs/pii/0169743994850542)



