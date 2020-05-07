import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class llvmVisitor extends GJDepthFirst<String, String> {

    mySymbolTable symbolTable;
    String fileName;
    String dirName = "LLVM_output";

    public llvmVisitor(mySymbolTable symbolTable, String fileName) throws IOException {
        this.symbolTable = symbolTable;
        String fileName_only = new File(fileName).getName().replaceFirst("[.][^.]+$", "");
        this.fileName = dirName + "/" + fileName_only + ".ll";

        // Create a folder to hold the .ll files
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Make sure that the .ll file is new and empty
        File myF = new File(this.fileName);
        if (myF.exists()) {
            myF.delete();
            myF.createNewFile();
        } else {
            myF.createNewFile();
        }

        emit("yeet" + fileName + "\n");
        emit("yeet" + fileName + "\n");
        emit("yeet" + fileName + "\n");
    }

    private void emit(String buffer) {
        File file = new File(this.fileName);
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(buffer);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    public String visit(Goal n, String argu) throws Exception {
        String _ret = null;

        // Add vtable on top of file

        // Add boilerplate

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

}