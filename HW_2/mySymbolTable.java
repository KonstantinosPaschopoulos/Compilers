import java.util.LinkedHashMap;

public class mySymbolTable {

    // First key then value
    // Important to be an ordered data structure to keep track of inheritance
    public LinkedHashMap<String, classValue> classes;

    public mySymbolTable() {
        classes = new LinkedHashMap<String, classValue>();
    }

}