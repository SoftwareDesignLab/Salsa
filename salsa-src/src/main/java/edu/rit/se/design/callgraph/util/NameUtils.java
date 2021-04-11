package edu.rit.se.design.callgraph.util;

import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

import static com.ibm.wala.types.ClassLoaderReference.Primordial;

/**
 * This class has selectors and type references useful for finding callback methods and creating synthetic methods.
 *
 * @author Joanna C. S. Santos (jds5109@rit.edu)
 */
public class NameUtils {
    // used for finding serialization and deserialization points
    public static TypeReference JavaIoObjectInputStream = TypeReference.findOrCreate(Primordial, "Ljava/io/ObjectInputStream");
    public static TypeReference JavaIoObjectOutputStream = TypeReference.findOrCreate(Primordial, "Ljava/io/ObjectOutputStream");
    public static Selector readObjectSelector = new Selector(Atom.findOrCreateAsciiAtom("readObject"), Descriptor.findOrCreateUTF8("()Ljava/lang/Object;"));
    public static Selector writeObjectSelector = new Selector(Atom.findOrCreateAsciiAtom("writeObject"), Descriptor.findOrCreateUTF8("(Ljava/lang/Object;)V"));
    // used for finding callback methods
    public static Selector writeObjectCallbackSelector = new Selector(Atom.findOrCreateAsciiAtom("writeObject"), Descriptor.findOrCreateUTF8("(Ljava/io/ObjectOutputStream;)V"));
    public static Selector writeReplaceCallbackSelector = new Selector(Atom.findOrCreateAsciiAtom("writeReplace"), Descriptor.findOrCreateUTF8("()Ljava/lang/Object;"));
    // deserialization callback methods
    public static Selector readObjectCallbackSelector = new Selector(Atom.findOrCreateAsciiAtom("readObject"), Descriptor.findOrCreateUTF8("(Ljava/io/ObjectInputStream;)V"));
    public static Selector readObjectNoDataCallbackSelector = new Selector(Atom.findOrCreateAsciiAtom("readObjectNoData"), Descriptor.findOrCreateUTF8("()V"));
    public static Selector readResolveCallbackSelector = new Selector(Atom.findOrCreateAsciiAtom("readResolve"), Descriptor.findOrCreateUTF8("()Ljava/lang/Object;"));
    public static Selector validateObjectCallbackSelector = new Selector(Atom.findOrCreateAsciiAtom("validateObject"), Descriptor.findOrCreateUTF8("()V"));

}
