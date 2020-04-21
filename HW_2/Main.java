import syntaxtree.*;
// import visitor.*;
import java.io.*;
// import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java [MainClassName] [file1] [file2] ... [fileN]");
            System.exit(1);
        }

        for (String arg : args) {
            System.out.println("---------------------------------------------");
            System.out.println("Parsing file: " + arg);

            FileInputStream fis = null;
            mySymbolTable symbolTable;

            try {

                fis = new FileInputStream(arg);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();
                System.err.println("Program parsed successfully.");

                symbolTable = new mySymbolTable();
                firstPhaseVisitor first = new firstPhaseVisitor(symbolTable);
                root.accept(first, null);
                System.err.println("First phase successful.");

                fis = new FileInputStream(arg);
                parser = new MiniJavaParser(fis);
                root = parser.Goal();
                secondPhaseVisitor second = new secondPhaseVisitor(symbolTable);
                root.accept(second, null);
                System.err.println("Second phase also successful.");
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}
