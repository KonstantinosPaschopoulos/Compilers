import java.util.HashMap;
import java.util.LinkedHashMap;

public class classValue {

    public HashMap<String, String> classFields; // classFields stores name, type
    public HashMap<String, String> classMethods; // classMethods stores name, return type
    public HashMap<String, LinkedHashMap<String, String>> methodParams; //methodParams stores method name, an ordered list of the params
    public HashMap<String, HashMap<String, String>> methodLocals; // methodLocals stores method name, name and type of local var

    public classValue() {
        classFields = new HashMap<String, String>();
        classMethods = new HashMap<String, String>();
        methodParams = new HashMap<String, LinkedHashMap<String, String>>();
        methodLocals = new HashMap<String, HashMap<String, String>>();
    }
}