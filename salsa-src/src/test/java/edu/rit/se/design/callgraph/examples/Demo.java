/*
 * Copyright (c) 2020 - Present. Rochester Institute of Technology
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.rit.se.design.callgraph.examples;

import java.io.*;

public class Demo extends Superclass implements Serializable, ObjectInputValidation {

    static final long serialVersionUID = 42L;

    public void validateObject() throws InvalidObjectException {
        System.out.println("Demo.validateObject()");
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.registerValidation(this, 0);
        in.defaultReadObject();
        System.out.println("Demo.readObject()");
    }
    private void readObjectNoData() throws ObjectStreamException{
        System.out.println("Demo.readObjectNoData()");
    }

    private Object readResolve() throws ObjectStreamException {
        System.out.println("Demo.readResolve()");
        return this;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        System.out.println("Demo.writeObject()");
    }
    private Object writeReplace() throws ObjectStreamException {
        System.out.println("Demo.writeReplace()");
        return this;
    }

    public static void main(String[] args) throws Exception {

        System.out.println("SERIALIZATION");
        Demo serialize = new Demo();
        FileOutputStream fos = new FileOutputStream("test.ser");
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(serialize);
        out.close();

        System.out.println("DESERIALIZATION");

        FileInputStream fis = new FileInputStream("test.ser");
        ObjectInputStream in = new ObjectInputStream(fis);
        Demo obj = (Demo) in.readObject();
        in.close();


    }
}

class Superclass {
    public void callback() {
        System.out.println("Superclass.callback()");
    }

    public Superclass() {
        callback();
    }
}
