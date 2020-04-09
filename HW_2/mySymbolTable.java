import java.util.LinkedHashMap;

public class mySymbolTable {

    // First key then value
    // Important to be an ordered data structure to keep track of inheritance
    public LinkedHashMap<String, classValue> classes;

    public mySymbolTable() {
        System.out.println("here");

        classes = new LinkedHashMap<String, classValue>();
    }

}