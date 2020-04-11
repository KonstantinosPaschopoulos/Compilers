import java.util.HashMap;
import java.util.LinkedHashMap;

public class methodValue {
    public String returnType; // Stores return type
    public LinkedHashMap<String, String> methodParams; //methodParams stores an ordered list of the params
    public HashMap<String, String> methodLocals; // methodLocals stores name and type of local vars
    public boolean isDerived;

    public methodValue(String ret) {
        returnType = ret;
        methodParams = new LinkedHashMap<String, String>();
        methodLocals = new HashMap<String, String>();
        isDerived = false;
    }

    public boolean checkVar(String name) {
        return methodParams.containsKey(name) || methodLocals.containsKey(name);
    }

    public boolean checkParam(String name) {
        return methodParams.containsKey(name);
    }

    public void markDerived() {
        isDerived = true;
    }

}