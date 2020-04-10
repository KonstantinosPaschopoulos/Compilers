import java.util.HashMap;

public class classValue {

    public boolean extendsBool;
    public String parentClass;
    public HashMap<String, String> classFields; // classFields stores name, type
    public HashMap<String, methodValue> classMethods; //classMethods stores name, method info

    public classValue(boolean ext, String par) {
        extendsBool = ext;
        parentClass = par;
        classFields = new HashMap<String, String>();
        classMethods = new HashMap<String, methodValue>();
    }

    public boolean checkField(String name) {
        return classFields.containsKey(name);
    }

    public boolean checkMethod(String name) {
        return classMethods.containsKey(name);
    }
}