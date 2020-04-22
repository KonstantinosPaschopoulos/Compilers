import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

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

    public void polyCheck(String child, String parent) throws Exception {
        // Iterate through all the parent classes or the child subclass to check for methods
        LinkedHashMap<String, methodValue> childMap = classes.get(child).classMethods;

        do {
            LinkedHashMap<String, methodValue> parentMap = classes.get(parent).classMethods;

            // Iterate through the methods to see if any have been defined again in the child class
            for (String keyC : childMap.keySet()) {
                for (String keyP : parentMap.keySet()) {
                    if (keyC == keyP) {
                        // Check return type
                        if (childMap.get(keyC).returnType != parentMap.get(keyP).returnType) {
                            throw new Exception("The return type of the method \'" + keyC + "\' in the subclass \'"
                                    + child + "\' " + "doesn't match the return type in the original method");
                        }

                        // Check argument types (ordered)
                        LinkedHashMap<String, String> childParams = childMap.get(keyC).methodParams;
                        LinkedHashMap<String, String> parentParams = parentMap.get(keyP).methodParams;
                        if (parentParams.size() != childParams.size()) {
                            throw new Exception("The arguments of the method \'" + keyC + "\' in the subclass \'"
                                    + child + "\' " + "don't match the arguments in the original method");
                        }
                        if ((new ArrayList<>(parentParams.values())
                                .equals(new ArrayList<>(childParams.values()))) == false) {
                            throw new Exception("The arguments of the method \'" + keyC + "\' in the subclass \'"
                                    + child + "\' " + "don't match the arguments in the original method");
                        }

                        // To help later on with the virtual table
                        childMap.get(keyC).markDerived();
                    }
                }
            }

            parent = classes.get(parent).parentClass;

        } while (parent != null && !parent.isEmpty());
    }

    public void checkType(String typeId, String name) throws Exception {
        if (Objects.equals("boolean", typeId) || Objects.equals("int", typeId) || Objects.equals("boolean[]", typeId)
                || Objects.equals("int[]", typeId)) {
            // It's one of the basic types
            return;
        } else {
            // Check if the type is one the declared classes
            if (classes.containsKey(typeId)) {
                return;
            } else {
                throw new Exception("Cannot identify the type \'" + typeId + "\' of \'" + name + "\'");
            }
        }
    }

    // Verify that the identifier is properly declared and identifiable
    public String verifyVar(String name, String methName, String className) throws Exception {
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
            throw new Exception("The variable \'" + name + "\' has not been declared");
        }
    }

    public String verifyMethod(String methName, String className) throws Exception {
        // Trying to find the called method in the class
        if (classes.get(className).checkMethod(methName) == true) {
            return classes.get(className).classMethods.get(methName).returnType;
        }

        // Check for inherited methods
        while (classes.get(className).extendsBool == true) {
            className = classes.get(className).parentClass;

            if (classes.get(className).checkMethod(methName) == true) {
                return classes.get(className).classMethods.get(methName).returnType;
            }
        }

        // Could not find the method
        throw new Exception("The method \'" + methName + "\' has not been declared");
    }

    public void checkArguments(String callMeth, String callClass, String args) throws Exception {
        // First of: finding where the method that is called is declared
        if (classes.get(callClass).checkMethod(callMeth) == false) {
            while (classes.get(callClass).extendsBool == true) {
                callClass = classes.get(callClass).parentClass;

                if (classes.get(callClass).checkMethod(callClass) == true) {
                    break;
                }
            }
        }

        // Checking that the arguments match
        if (args == null || args.isEmpty()) {
            if (classes.get(callClass).classMethods.get(callMeth).methodParams.size() != 0) {
                throw new Exception("Wrong arguments when using the method \'" + callMeth + "\'");
            }
        } else {
            // Creating an ArrayList object to hold the argument we want to check
            ArrayList<String> argList = new ArrayList<String>();

            // Adding all the argument we have collected to the array
            StringTokenizer arg = new StringTokenizer(args, ",");
            while (arg.hasMoreTokens()) {
                argList.add(arg.nextToken());
            }

            // Firstly checking that the number of arguments matches
            if (argList.size() != classes.get(callClass).classMethods.get(callMeth).methodParams.size()) {
                throw new Exception("Wrong number of arguments when using the method \'" + callMeth + "\'");
            }

            // Here we check that every individual argument matches
            int index = 0;
            for (String value : classes.get(callClass).classMethods.get(callMeth).methodParams.values()) {
                if (Objects.equals(argList.get(index), value) == false) {
                    if (isParent(argList.get(index), value) == false) {
                        throw new Exception("Wrong arguments when using the method \'" + callMeth + "\'");
                    }
                }

                index++;
            }
        }

    }

    public boolean isParent(String child, String parent) {
        if (classes.containsKey(child) == true) {
            while (classes.get(child).extendsBool == true) {
                child = classes.get(child).parentClass;

                if (Objects.equals(child, parent)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void printOffsets() {
        boolean flag = true;
        int varSum = 0;
        int methSum = 0;
        LinkedHashMap<String, Integer> varOffset = new LinkedHashMap<String, Integer>();
        LinkedHashMap<String, Integer> methOffset = new LinkedHashMap<String, Integer>();

        for (Map.Entry<String, classValue> entry : classes.entrySet()) {
            String key = entry.getKey();
            classValue value = entry.getValue();

            // For each file skip the first class
            if (flag) {
                varOffset.put(key, 0);
                methOffset.put(key, 0);

                flag = false;
                continue;
            }

            System.out.println("-----------Class " + key + "-----------");

            // Make sure the offset table starts at the correct offset
            varSum = 0;
            methSum = 0;
            if (value.extendsBool) {
                varSum = varOffset.get(value.parentClass);
                methSum = methOffset.get(value.parentClass);
            }

            System.out.println("--Variables---");
            for (Map.Entry<String, String> varEntry : value.classFields.entrySet()) {
                String varKey = varEntry.getKey();
                String varValue = varEntry.getValue();

                System.out.println(key + "." + varKey + " : " + varSum);

                if (Objects.equals(varValue, "int")) {
                    varSum += 4;
                } else if (Objects.equals(varValue, "boolean")) {
                    varSum += 1;
                } else {
                    varSum += 8;
                }
            }

            System.out.println("---Methods---");
            for (Map.Entry<String, methodValue> methEntry : value.classMethods.entrySet()) {
                String methKey = methEntry.getKey();
                methodValue methValue = methEntry.getValue();

                if (methValue.isDerived == true) {
                    // Skip this one
                    continue;
                }

                System.out.println(key + "." + methKey + " : " + methSum);
                methSum += 8;
            }

            varOffset.put(key, varSum);
            methOffset.put(key, methSum);

            System.out.println("");
        }
    }

}