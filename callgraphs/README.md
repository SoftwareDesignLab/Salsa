# Call graphs


This folder contains the visualization of the call graphs computed by `Salsa`.

- Each PDF provides the call graph computed with Salsa, where we can see it is capable of adding edges to call back methods;
- Nodes in light green are *application nodes*, nodes in light orange are *primordial nodes*, and underlined nodes are *synthetic nodes* added by Salsa to handle the call backs during serialization/deserialization; 
- Each node is preceded by an `id` number to avoid labels to be duplicated (when a same method is invoked multiple times in an application);
- Each method is preceded by its class name only (not the class' fully qualified name);
- Please notice that each PDF has only a **subset** of nodes and edges from the original call graph because each call graph includes many nodes from API calls.

	For the sake of clarity, each PDF only includes nodes and edges if they fulfill the following criteria:
	
	***For nodes***: 
	
	- The node correspond to a method implemented by the application (application-scope);
	- The node is non-application (i.e., primordial or extension), but it has a call back edge to an application node;
	
	
	***For edges***: 
	
	- All the outgoing edges from application nodes;
	- All the outgoing edges from non-application nodes but wholse target is an application node (or is in a path that will lead to a call back to the application);




## Test Cases

These test cases are from the Java Call Graph Test Suite (JCG).
### Ser1

- **Expected Output**: It expects a direct call from `ser.Demo.writeObject` to 
`java.io.ObjectOutputStream.defaultWriteObject` at line 15.

- **Actual Output**:![Ser1-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser1-JRE1.8.jar.png)

- **Result**: ✅ Passed Test Case


### Ser2

- **Expected Output**: It expects a direct call from `ser.Demo.writeObject` to 
`java.io.ObjectOutputStream.defaultWriteObject` at line 15.

- **Actual Output**: ![Ser2-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser2-JRE1.8.jar.png)

- **Result**: ✅ Passed Test Case


### Ser3

- **Expected Output**: It expects a direct call from `ser.Demo.writeObject` to 
`java.io.ObjectOutputStream.defaultWriteObject` at line 15.

- **Actual Output**: ![Ser3-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser3-JRE1.8.jar.png)

- **Result**: ✅ Passed Test Case

### Ser4

- **Expected Output**: It expects a direct call from `ser.Demo.readObject` to 
`java.io.ObjectOutputStream.defaultReadObject` at line 15.

- **Actual Output**:![Ser4-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser4-JRE1.8.jar.png)

- **Result**: ✅ Passed Test Case


### Ser5

- **Expected Output**: It expects a direct call from `ser.Demo.readObject` to 
`java.io.ObjectOutputStream.defaultReadObject` at line 15.

- **Actual Output**:![Ser5-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser5-JRE1.8.jar.png)

- **Result**: ✅ Passed Test Case

### Ser6

- **Expected Output**: It expects a direct call from `ser.Demo.writeReplace` to 
`ser.Demo.replace` at line 17.

- **Actual Output**:![Ser6-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser6-JRE1.8.jar.png)

- **Result**: ❌ Failed Test Case
	- *Reason*:  Salsa currently does not support the `writeReplace` callback during serialization.

### Ser7

- **Expected Output**: It expects a direct call from `ser.Demo.readResolve` to 
`ser.Demo.replace` at line 17.

- **Actual Output**:![Ser7-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser7-JRE1.8.jar.png)

- **Result**: ❌ Failed Test Case
	- *Reason*:  Salsa currently does not support the `replace` callback during deserialization.


### Ser8

- **Expected Output**: It expects a direct call from `ser.Demo.validateObject` to 
`ser.Demo.callback` at line 19.

- **Actual Output**:![Ser8-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser8-JRE1.8.jar.png)

- **Result**: ❌ Failed Test Case
	- *Reason*:  Salsa currently does not support the `validateObject` callback during deserialization.

	
	
### Ser9

- **Expected Output**: It expects that  

- **Actual Output**:![Ser9-JRE1.8.jar.png](https://github.com/SoftwareDesignLab/Salsa/blob/master/callgraphs/Ser9-JRE1.8.jar.png)

- **Result**:  

	
	

	
