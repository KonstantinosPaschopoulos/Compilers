import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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

    private void vtable_emit() {
        boolean flag = true;
        // The map contains for each className the function names and LLVM code
        LinkedHashMap<String, LinkedHashMap<String, String>> vtable = new LinkedHashMap<String, LinkedHashMap<String, String>>();

        for (Map.Entry<String, classValue> entry : symbolTable.classes.entrySet()) {
            String className = entry.getKey();
            classValue classValue = entry.getValue();

            // The main class doesn't need much work
            if (flag) {
                emit("@." + className + "_vtable = global [0 x i8*] []\n");

                flag = false;
                continue;
            }

            emit("\n@." + className + "_vtable = global ");

            LinkedHashMap<String, String> functionValues = new LinkedHashMap<String, String>();
            vtable.put(className, functionValues);

            // If the current class is derived copy all the function from the entry of the parent class before adding any more functions
            if (classValue.extendsBool) {
                vtable.get(className).putAll(vtable.get(classValue.parentClass));
            }

            // Loop through the functions of the current class
            for (Map.Entry<String, methodValue> methEntry : classValue.classMethods.entrySet()) {
                String methName = methEntry.getKey();
                methodValue methValue = methEntry.getValue();

                // Create the LLVM code for the vtable
                String llvm_code = "i8* bitcast (";

                // Adding the return value
                if (Objects.equals(methValue.returnType, "int")) {
                    llvm_code += "i32 (";
                } else if (Objects.equals(methValue.returnType, "int[]")) {
                    llvm_code += "i32* (";
                } else if (Objects.equals(methValue.returnType, "boolean")) {
                    llvm_code += "i1 (";
                } else if (Objects.equals(methValue.returnType, "boolean[]")) {
                    llvm_code += "i8* (";
                } else {
                    llvm_code += "i8* (";
                }

                // First adding the "this" pointer
                llvm_code += "i8*";
                // Adding the rest of the parameters
                for (Map.Entry<String, String> paramEntry : methValue.methodParams.entrySet()) {
                    String paramValue = paramEntry.getValue();

                    if (Objects.equals(paramValue, "int")) {
                        llvm_code += ",i32";
                    } else if (Objects.equals(paramValue, "int[]")) {
                        llvm_code += ",i32*";
                    } else if (Objects.equals(paramValue, "boolean")) {
                        llvm_code += ",i1";
                    } else if (Objects.equals(paramValue, "boolean[]")) {
                        llvm_code += ",i8*";
                    } else {
                        llvm_code += ",i8*";
                    }
                }

                llvm_code += ")* @" + className + "." + methName + " to i8*)";

                if (vtable.get(className).containsKey(methName)) {
                    // Replace the LLVM code
                    vtable.get(className).replace(methName, llvm_code);
                } else {
                    // Add the new key and value
                    vtable.get(className).put(methName, llvm_code);
                }
            }

            // Add how many functions are in the class
            emit("[" + vtable.get(className).size() + " x i8*] [\n");

            // Print the details of each function
            boolean emit_flag = true;
            for (Map.Entry<String, String> funcInfo : vtable.get(className).entrySet()) {
                String llvmCode = funcInfo.getValue();

                if (emit_flag) {
                    emit("    " + llvmCode);
                    emit_flag = false;
                    continue;
                }

                emit(",\n    " + llvmCode);
            }

            emit("\n]\n");
        }
    }

    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    public String visit(Goal n, String argu) throws Exception {
        String _ret = null;

        // Add vtables on top of file
        vtable_emit();

        // Add boilerplate
        boilerplate();

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    public String visit(MainClass n, String argu) throws Exception {
        String _ret = null;

        emit("define i32 @main() {\n");

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);

        emit("\nret i32 0\n}\n");

        return _ret;
    }

}