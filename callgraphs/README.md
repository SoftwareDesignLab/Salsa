# Call graphs


This folder contains the visualization of the call graphs computed by `Salsa`.

- Each PDF provides the call graph computed with Salsa, where we can see it is capable of adding edges to call back methods;
- Nodes in light green are *application nodes*, nodes in light orange are *primordial nodes*, and underlined nodes are *synthetic nodes* added by Salsa to handle the call backs during serialization/deserialization; 
- Each node is preceded by an `id` number to avoid labels to be duplicated (when a same method is invoked multiple times in an application).
- Please notice that each PDF has only a **subset** of nodes and edges from the original call graph because each call graph includes many nodes from API calls.

	For the sake of clarity, each PDF only includes nodes and edges if they fulfill the following criteria:
	
	***For nodes***: 
	
	- The node correspond to a method implemented by the application (application-scope);
	- The node is non-application (i.e., primordial or extension), but it has a call back edge to an application node;
	
	
	***For edges***: 
	
	- All the outgoing edges from application nodes;
	- All the outgoing edges from non-application nodes but wholse target is an application node (or is in a path that will lead to a call back to the application);




## Test Cases

### Ser1

- **Expected Output**: It expects a direct call from `ser.Demo.writeObject` to 
`java.io.ObjectOutputStream.defaultWriteObject`.

- **Actual Output**:![Ser1-JRE1.8.jar.png](https://raw.githubusercontent.com/SoftwareDesignLab/Salsa/master/callgraphs/Ser1-JRE1.8.jar.png)
