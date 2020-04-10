import java.util.HashMap;

public class classValue {

    public HashMap<String, String> classFields; // classFields stores name, type
    public HashMap<String, methodValue> classMethods; //classMethods stores name, method info

    public classValue() {
        System.out.println("yeet");
        classFields = new HashMap<String, String>();
        classMethods = new HashMap<String, methodValue>();
    }

    public boolean checkField(String name) {
        return classFields.containsKey(name);
    }
}