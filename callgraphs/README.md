# Call graphs


This folder contains the visualization of the call graphs computed by `Salsa`.
Each PDF provides the call graph computed with and without Salsa, where we can see missing edges and nodes from the call graph.

Please notice that each PDF has only a **subset** of nodes and edges from the original call graph because each call graph includes many nodes from API calls.
For the sake of clarity, each PDF only includes nodes and edges if they fulfill the following criteria:

***For nodes***: 

- The node correspond to a method implemented by the application (application-scope);
- The node is non-application (i.e., primordial or extension), but it has a call back edge to an application node;


***For edges***: 
- All the outgoing edges from application nodes;
- All the outgoing edges from non-application nodes but wholse target is an application node (or is in a path that will lead to a call back to the application);


