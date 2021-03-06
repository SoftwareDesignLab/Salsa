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
import java.util.Arrays;
import java.util.List;

public class PaperExample {


    public static void main(String[] args) throws Exception {
        // serialization
        Shelter s1 = new Shelter(
                Arrays.asList(new Dog("Max"), new Cat("Joy"))
        );
        FileOutputStream f = new FileOutputStream(new File("shelter.ser"));
        ObjectOutputStream out = new ObjectOutputStream(f);
        out.writeObject(s1);

        // deserialization
        FileInputStream fs = new FileInputStream(new File("shelter.ser"));
        ObjectInputStream in = new ObjectInputStream(fs);
        Shelter s2 = (Shelter) in.readObject();
        new File("shelter.ser").delete();
    }


}


class Pet implements Serializable {
    protected String name;
    public Pet(String name) {
        this.name = name;
    }
}

class Cat extends Pet implements Serializable {
    public Cat(String name) {
        super(name);
    }

    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        System.out.println("Cat.readObject()");
    }

    private void writeObject(ObjectOutputStream s)
            throws IOException {
        s.defaultWriteObject();
        System.out.println("Cat.writeObject()");
    }
}

class Dog extends Pet implements Serializable {

    public Dog(String name) {
        super(name);
    }

    private Object readResolve() throws ObjectStreamException {
        System.out.println("Dog.readResolve()");
        return this;
    }

    private Object writeReplace() throws ObjectStreamException {
        System.out.println("Dog.writeReplace()");
        return this;
    }
}

class Shelter implements Serializable {
    private List<Pet> pets;

    public Shelter(List<Pet> pets) {
        this.pets = pets;
    }
}
