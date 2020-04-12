import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

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
        // Iterate through all the parent classes or the child subclass to check for methods
        HashMap<String, methodValue> childMap = classes.get(child).classMethods;

        do {
            HashMap<String, methodValue> parentMap = classes.get(parent).classMethods;

            // Iterate through the methods to see if any have been defined again in the child class
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

    public void checkType(String typeId, String name) {
        if (Objects.equals("boolean", typeId) || Objects.equals("int", typeId) || Objects.equals("boolean[]", typeId)
                || Objects.equals("int[]", typeId)) {
            // It's one of the basic types
            return;
        } else {
            // Check if the type is one the declared classes
            if (classes.containsKey(typeId)) {
                return;
            } else {
                System.err.println("Cannot identify the type \'" + typeId + "\' of \'" + name + "\'");
                System.exit(1);
            }
        }
    }

    // Verify that the identifier is properly declared and identifiable
    public String verifyVar(String name, String methName, String className) {
        if (classes.get(className).classMethods.get(methName).methodLocals.containsKey(name) == true) {
            // First check the local variables of the method
            return classes.get(className).classMethods.get(methName).methodLocals.get(name);
        } else if (classes.get(className).classMethods.get(methName).methodParams.containsKey(name) == true) {
            // Then check the method parameters
            return classes.get(className).classMethods.get(methName).methodParams.get(name);
        } else if (classes.get(className).classFields.containsKey(name) == true) {
            // Then check the fields of the class
            return classes.get(className).classFields.get(name);
        } else {
            // Finally check for inherited variables
            while (classes.get(className).extendsBool == true) {
                className = classes.get(className).parentClass;

                if (classes.get(className).classFields.containsKey(name) == true) {
                    return classes.get(className).classFields.get(name);
                }
            }

            // Could not find the variable
            System.err.println("The variable \'" + name + "\' has not been declared");
            System.exit(1);
            return "error";
        }
    }

}