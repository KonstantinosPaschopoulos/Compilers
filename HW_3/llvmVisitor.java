import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class llvmVisitor extends GJDepthFirst<String, argsObj> {

    mySymbolTable symbolTable;
    String fileName;
    String dirName = "LLVM_output";
    int regCount;
    int labelCount;

    public llvmVisitor(mySymbolTable symbolTable, String fileName) throws IOException {
        this.symbolTable = symbolTable;
        this.regCount = 0;
        this.labelCount = 0;
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

    private String emitType(String type) {
        if (Objects.equals(type, "int")) {
            return "i32";
        } else if (Objects.equals(type, "int[]")) {
            return "i32*";
        } else if (Objects.equals(type, "boolean")) {
            return "i1";
        } else if (Objects.equals(type, "boolean[]")) {
            return "i8*";
        } else {
            // For classes objects
            return "i8*";
        }
    }

    private String getReg() {
        String retReg = "%_" + Integer.toString(regCount);
        regCount++;

        return retReg;
    }

    private String fieldType(String className, String fieldName) {
        if (symbolTable.classes.get(className).checkField(fieldName)) {
            return emitType(symbolTable.classes.get(className).classFields.get(fieldName));
        } else {
            while (symbolTable.classes.get(className).extendsBool == true) {
                className = symbolTable.classes.get(className).parentClass;

                if (symbolTable.classes.get(className).checkField(fieldName) == true) {
                    return emitType(symbolTable.classes.get(className).classFields.get(fieldName));
                }
            }
        }

        // Won't reach this
        return null;
    }

    private String getOffset(String className, String varName) {
        int offset = 0;

        // First I add all the parent classes and the original class to a list with the supermost class on top
        List<String> classList = new ArrayList<String>();
        classList.add(0, className);
        while (symbolTable.classes.get(className).extendsBool) {
            className = symbolTable.classes.get(className).parentClass;

            classList.add(0, className);
        }

        // Then I iterate through all the classes I added to the list
        for (int i = 0; i < classList.size(); i++) {
            // For every class that is in the list I iterate through their fields and calculate the offset
            for (Map.Entry<String, String> classFields : symbolTable.classes.get(classList.get(i)).classFields
                    .entrySet()) {
                String fieldName = classFields.getKey();
                String fieldType = classFields.getValue();

                // When I reach the 'varName' field I return the calculated value
                if (Objects.equals(fieldName, varName)) {
                    offset += 8; // Adding 8 because of the vtable that is in the front
                    return Integer.toString(offset);
                }

                if (Objects.equals(fieldType, "int")) {
                    offset += 4;
                } else if (Objects.equals(fieldType, "boolean")) {
                    offset += 1;
                } else {
                    offset += 8;
                }
            }
        }

        return null;
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
                llvm_code += emitType(methValue.returnType) + " (";

                // First adding the "this" pointer
                llvm_code += "i8*";
                // Adding the rest of the parameters
                for (Map.Entry<String, String> paramEntry : methValue.methodParams.entrySet()) {
                    String paramValue = paramEntry.getValue();

                    llvm_code += "," + emitType(paramValue);
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
    public String visit(Goal n, argsObj argu) throws Exception {
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
    public String visit(MainClass n, argsObj argu) throws Exception {
        String _ret = null;

        emit("define i32 @main() {\n");

        n.f0.accept(this, argu);

        String classId = n.f1.accept(this, argu);

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);

        n.f6.accept(this, argu);
        String methId = "main";

        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);

        // The only two that emit code
        n.f14.accept(this, new argsObj(classId, methId, true, true));
        n.f15.accept(this, new argsObj(classId, methId, true, true));

        n.f16.accept(this, argu);
        n.f17.accept(this, argu);

        emit("\tret i32 0\n}\n\n");

        return _ret;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    public String visit(ClassDeclaration n, argsObj argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);

        String classId = n.f1.accept(this, argu);

        n.f2.accept(this, argu);

        // We don't need to visit the f3 node only the method declerations
        // n.f3.accept(this, argu);
        n.f4.accept(this, new argsObj(classId, "", true, false));

        n.f5.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    public String visit(ClassExtendsDeclaration n, argsObj argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);

        String classId = n.f1.accept(this, argu);

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        // Only visit the method declarations
        // n.f5.accept(this, argu);
        n.f6.accept(this, new argsObj(classId, "", true, false));

        n.f7.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, argsObj argu) throws Exception {
        String _ret = null;

        // Just need to emit an alloca
        String llvm_code = "%";

        String type = n.f0.accept(this, argu);
        String id = n.f1.accept(this, argu);

        // Adding the name of the var with '%' in front
        llvm_code += id + " = alloca ";

        // Adding the type
        llvm_code += emitType(type);
        llvm_code += "\n";

        emit("\t" + llvm_code);

        n.f2.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    public String visit(MethodDeclaration n, argsObj argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);

        String retType = n.f1.accept(this, argu);
        String methId = n.f2.accept(this, argu);

        // Emiting the function declaration
        emit("define " + emitType(retType) + " @" + argu.className + "." + methId);

        // Emit the function parameters
        emit("(i8* %this");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        emit(") {\n");

        // Allocate the parameters
        for (Map.Entry<String, String> paramEntry : symbolTable.classes.get(argu.className).classMethods
                .get(methId).methodParams.entrySet()) {
            String paramName = paramEntry.getKey();
            String paramType = paramEntry.getValue();

            emit("\t%" + paramName + " = alloca " + emitType(paramType) + "\n");
            emit("\tstore " + emitType(paramType) + " %." + paramName + ", " + emitType(paramType) + "* %" + paramName
                    + "\n\n");
        }

        n.f6.accept(this, argu);

        // Allocation of local variables
        n.f7.accept(this, argu);

        // TODO: Statement expressions
        n.f8.accept(this, new argsObj(argu.className, methId, true, true));

        n.f9.accept(this, argu);

        // TODO: Return expression
        n.f10.accept(this, new argsObj(argu.className, methId, true, true));

        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

        emit("}\n\n");

        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    public String visit(FormalParameter n, argsObj argu) throws Exception {
        String _ret = null;

        String paramType = n.f0.accept(this, argu);
        String paramId = n.f1.accept(this, argu);

        emit(", " + emitType(paramType) + " %." + paramId);

        return _ret;
    }

    /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
    public String visit(PrimaryExpression n, argsObj argu) throws Exception {
        String expr = n.f0.accept(this, argu);

        if (n.f0.which == 0) {
            return expr;
        } else if (n.f0.which == 1) {
            return "1";
        } else if (n.f0.which == 2) {
            return "0";
        } else if (n.f0.which == 3) {
            // Checking if the identifier is a local variable or a field of the class
            if (symbolTable.classes.get(argu.className).classMethods.get(argu.methName).checkVar(expr)) {
                // Get the type of the local variable, get a register and emit a load instruction
                String varType = emitType(
                        symbolTable.classes.get(argu.className).classMethods.get(argu.methName).varType(expr));
                String register = getReg();

                emit("\t" + register + " = load " + varType + ", " + varType + "* %" + expr + "\n");

                return register;
            } else {
                // Field

                // Basically three instructions: getelementptr, bitcast, load and then return the register
                String fieldOffset = getOffset(argu.className, expr);
                String fieldType = fieldType(argu.className, expr);

                String regGEP = getReg();
                emit("\t" + regGEP + " = getelementptr i8, i8* %this, i32 " + fieldOffset + "\n");

                String regBC = getReg();
                emit("\t" + regBC + " = bitcast i8* " + regGEP + " to " + fieldType + "*" + "\n");

                String regL = getReg();
                emit("\t" + regL + " = load " + fieldType + ", " + fieldType + "* " + regBC + "\n");

                return regL;
            }
        } else if (n.f0.which == 4) {
            return "%this";
        } else {
            return expr;
        }
    }

    /**
    * f0 -> "!"
    * f1 -> Clause()
    */
    public String visit(NotExpression n, argsObj argu) throws Exception {
        n.f0.accept(this, argu);

        String expr = n.f1.accept(this, argu);

        // Since there is not a logical not instruction, I will use a xor as seen on the examples
        String notReg = getReg();
        emit("\t" + notReg + " = xor i1 1, " + expr + "\n");

        return notReg;
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, argsObj argu) throws Exception {
        return n.f0.toString();
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, argsObj argu) throws Exception {
        String ret = n.f1.accept(this, argu);

        return ret;
    }

    /**
     * f0 -> "boolean"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(BooleanArrayType n, argsObj argu) throws Exception {
        return n.f0.toString() + n.f1.toString() + n.f2.toString();
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(IntegerArrayType n, argsObj argu) throws Exception {
        return n.f0.toString() + n.f1.toString() + n.f2.toString();
    }

    /**
    * f0 -> "boolean"
    */
    public String visit(BooleanType n, argsObj argu) throws Exception {
        return n.f0.toString();
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, argsObj argu) throws Exception {
        return n.f0.toString();
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, argsObj argu) throws Exception {
        return n.f0.toString();
    }

}