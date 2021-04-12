# Salsa - Static AnaLysis for SeriAlization

This repository shows the callgraphs generated using `SALSA` (**S**tatic **A**na**L**ysis for **S**eri**A**lization).


## Overview
Salsa improves existing off-the-shelf pointer analysis to support the construction of callgraph that handles programs with serialization/deserialization.
To evaluate it, we performed the following:

1. We used the nine test cases from the [Java Call Graph Test Suite](https://bitbucket.org/delors/jcg/src/master/jcg_testcases/src/main/resources/Serialization.md) that uses serialization/deserialization features (i.e. `Ser1`-`Ser9`).
2. We compiled these test cases using JRE 1.8 and generated JAR files.
3. We run Salsa against each test case and computed its callgraphs.


## Repository Organization


* `jars/`: The compiled JARs for each test case;
* `callgraphs/`: PDFs with the callgraphs; 
* `salsa-src/`: source code for Salsa;



