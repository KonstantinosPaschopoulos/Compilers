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

        FileInputStream fis = null;
        mySymbolTable symbolTable;

        try {
            fis = new FileInputStream(args[0]);

            symbolTable = new mySymbolTable();
            MiniJavaParser parser = new MiniJavaParser(fis);

            Goal root = parser.Goal();
            System.err.println("Program parsed successfully.");

            firstPhaseVisitor eval = new firstPhaseVisitor(symbolTable);
            System.out.println(root.accept(eval, null));
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
