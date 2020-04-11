import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class mySymbolTable {

    // First key then value
    // Important to be an ordered data structure to keep track of inheritance
    public LinkedHashMap<String, classValue> classes;

    public mySymbolTable() {
        classes = new LinkedHashMap<String, classValue>();
    }

    public boolean checkClass(String name) {
        return classes.containsKey(name);
    }

    public void polyCheck(String child, String parent) {
        // Iterate through the methods to see if any have been defined again in the child class
        HashMap<String, methodValue> childMap = classes.get(child).classMethods;

        do {
            HashMap<String, methodValue> parentMap = classes.get(parent).classMethods;

            for (String keyC : childMap.keySet()) {
                for (String keyP : parentMap.keySet()) {
                    if (keyC == keyP) {
                        // Check return type
                        if (childMap.get(keyC).returnType != parentMap.get(keyP).returnType) {
                            System.err.println("The return type of the method \'" + keyC + "\' in the subclass \'"
                                    + child + "\' " + "doesn't match the return type in the original method");
                            System.exit(1);
                        }

                        // Check argument types (ordered)
                        LinkedHashMap<String, String> childParams = childMap.get(keyC).methodParams;
                        LinkedHashMap<String, String> parentParams = parentMap.get(keyP).methodParams;
                        if (parentParams.size() != childParams.size()) {
                            System.err.println("The arguments of the method \'" + keyC + "\' in the subclass \'" + child
                                    + "\' " + "don't match the arguments in the original method");
                            System.exit(1);
                        }
                        if ((new ArrayList<>(parentParams.entrySet())
                                .equals(new ArrayList<>(childParams.entrySet()))) == false) {
                            System.err.println("The arguments of the method \'" + keyC + "\' in the subclass \'" + child
                                    + "\' " + "don't match the arguments in the original method");
                            System.exit(1);
                        }

                        // To help later on with the virtual table
                        childMap.get(keyC).markDerived();
                    }
                }
            }

            parent = classes.get(parent).parentClass;

        } while (parent != null && !parent.isEmpty());
    }

}