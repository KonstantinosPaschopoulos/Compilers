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

    private void boilerplate() {
        emit("\ndeclare i8* @calloc(i32, i32)\n");
        emit("declare i32 @printf(i8*, ...)\n");
        emit("declare void @exit(i32)\n\n");
        emit("@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n");
        emit("@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n");
        emit("@_cNSZ = constant [15 x i8] c\"Negative size\\0a\\00\"\n\n");
        emit("define void @print_int(i32 %i) {\n");
        emit("    %_str = bitcast [4 x i8]* @_cint to i8*\n");
        emit("    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n");
        emit("    ret void\n");
        emit("}\n\n");
        emit("define void @throw_oob() {\n");
        emit("    %_str = bitcast [15 x i8]* @_cOOB to i8*\n");
        emit("    call i32 (i8*, ...) @printf(i8* %_str)\n");
        emit("    call void @exit(i32 1)\n");
        emit("    ret void\n");
        emit("}\n\n");
        emit("define void @throw_nsz() {\n");
        emit("    %_str = bitcast [15 x i8]* @_cNSZ to i8*\n");
        emit("    call i32 (i8*, ...) @printf(i8* %_str)\n");
        emit("    call void @exit(i32 1)\n");
        emit("    ret void\n");
        emit("}\n\n");
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
        boilerplate();

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

}