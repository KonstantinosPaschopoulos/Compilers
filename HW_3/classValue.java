import java.util.LinkedHashMap;

public class classValue {

    public boolean extendsBool;
    public String parentClass;
    public LinkedHashMap<String, String> classFields; // classFields stores name, type
    public LinkedHashMap<String, methodValue> classMethods; //classMethods stores name, method info

    public classValue(boolean ext, String par) {
        extendsBool = ext;
        parentClass = par;
        classFields = new LinkedHashMap<String, String>();
        classMethods = new LinkedHashMap<String, methodValue>();
    }

    public boolean checkField(String name) {
        return classFields.containsKey(name);
    }

    public boolean checkMethod(String name) {
        return classMethods.containsKey(name);
    }
}