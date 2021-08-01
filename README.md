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



## Related Publications

```
@inproceedings{santos2020salsa,
    author = {Santos, Joanna C. S. and Jones, Reese A. and Mirakhorli, Mehdi},
    title = {Salsa: Static Analysis of Serialization Features},
    year = {2020},
    isbn = {9781450381864},
    publisher = {Association for Computing Machinery},
    address = {New York, NY, USA},
    url = {https://doi.org/10.1145/3427761.3428343},
    doi = {10.1145/3427761.3428343},
    abstract = {Static analysis has the advantage of reasoning over multiple possible paths. Thus, it has been widely used for verification of program properties. Property verification often requires inter-procedural analysis, in which control and data flow are tracked across methods. At the core of inter-procedural analyses is the call graph, which establishes relationships between caller and callee methods. However, it is challenging to perform static analysis and compute the call graph of programs with dynamic features. Dynamic features are widely used in software systems; not supporting them makes it difficult to reason over properties related to these features. Although state-of-the-art research had explored certain types of dynamic features, such as reflection and RMI-based programs, serialization-related features are still not very well supported, as demonstrated in a recent empirical study. Therefore, in this paper, we introduce Salsa (Static AnaLyzer for SeriAlization features), which aims to enhance existing points-to analysis with respect to serialization-related features. The goal is to enhance the resulting call graph's soundness, while not greatly affecting its precision. In this paper, we report our early effort in developing Salsa and its early evaluation using the Java Call Graph Test Suite (JCG).},
    booktitle = {Proceedings of the 22nd ACM SIGPLAN International Workshop on Formal Techniques for Java-Like Programs},
    pages = {18–25},
    numpages = {8},
    keywords = {Java deserialization, Static analysis, Call graphs, Object marshaling and unmarshalling, Java serialization},
    location = {Virtual, USA},
    series = {FTfJP 2020}
}
```
