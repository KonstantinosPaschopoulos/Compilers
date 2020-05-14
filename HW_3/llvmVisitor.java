import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    LinkedHashMap<String, LinkedHashMap<String, String>> fieldTable;
    LinkedHashMap<String, LinkedHashMap<String, String>> vtable;
    LinkedHashMap<String, String> regTable;
    int arguIndex;
    ArrayList<String> arguList;

    public llvmVisitor(mySymbolTable symbolTable, String fileName) throws IOException {
        this.symbolTable = symbolTable;
        this.regTable = new LinkedHashMap<String, String>();
        arguList = new ArrayList<String>();
        this.regCount = 0;
        this.labelCount = 0;
        this.arguIndex = -1;
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

    private String getLabel() {
        String retLabel = "label_expr" + Integer.toString(labelCount);
        labelCount++;

        return retLabel;
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
        // Starting the offset with 8 because of the vtable pointer in the front
        int offset = 8;

        for (Map.Entry<String, String> entry : fieldTable.get(className).entrySet()) {
            String fieldName = entry.getKey();
            String fieldType = entry.getValue();

            // Return the offset that has been calculated up until the field
            if (Objects.equals(fieldName, varName)) {
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

        return null;
    }

    private int getClassSize(String className) {
        int offset = 0;

        // Iterate through all the fields in the class calculating the offset
        for (Map.Entry<String, String> entry : fieldTable.get(className).entrySet()) {
            String fieldType = entry.getValue();

            if (Objects.equals(fieldType, "int")) {
                offset += 4;
            } else if (Objects.equals(fieldType, "boolean")) {
                offset += 1;
            } else {
                offset += 8;
            }
        }

        return offset;
    }

    private String getMethOffset(String className, String methName) {
        int offset = 0;

        for (Map.Entry<String, String> entry : vtable.get(className).entrySet()) {
            String meth = entry.getKey();

            if (Objects.equals(methName, meth)) {
                return Integer.toString(offset);
            }

            offset += 8;
        }

        return null;
    }

    private String getNumberMethods(String className) {
        int sum = vtable.get(className).size();

        return Integer.toString(sum);
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
        vtable = new LinkedHashMap<String, LinkedHashMap<String, String>>();

        for (Map.Entry<String, classValue> entry : symbolTable.classes.entrySet()) {
            String className = entry.getKey();
            classValue classValue = entry.getValue();

            LinkedHashMap<String, String> functionValues = new LinkedHashMap<String, String>();
            vtable.put(className, functionValues);

            // The main class doesn't need much work
            if (flag) {
                emit("@." + className + "_vtable = global [0 x i8*] []\n");

                flag = false;
                continue;
            }

            emit("\n@." + className + "_vtable = global ");

            // If the current class is derived copy all the function from the entry of the parent class
            // before adding any more functions
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

    // What this function does is add all the fields of a given class to the map,
    // but also adding the fields of all the superclasses before it
    private void createFieldInfo() {
        // Has className, <fieldName, type>
        fieldTable = new LinkedHashMap<String, LinkedHashMap<String, String>>();
        boolean flag = true;

        for (Map.Entry<String, classValue> entry : symbolTable.classes.entrySet()) {
            String className = entry.getKey();
            classValue classValue = entry.getValue();

            // Skip the main class
            if (flag) {
                flag = false;
                continue;
            }

            // Add the name of the class to the map
            LinkedHashMap<String, String> classFields = new LinkedHashMap<String, String>();
            fieldTable.put(className, classFields);

            // Copy all the elements of the superclass
            if (classValue.extendsBool) {
                fieldTable.get(className).putAll(fieldTable.get(classValue.parentClass));
            }

            // Iterate through the fields of and make changes or add new ones
            for (Map.Entry<String, String> fieldsEntry : symbolTable.classes.get(className).classFields.entrySet()) {
                String fieldName = fieldsEntry.getKey();
                String fieldType = fieldsEntry.getValue();

                if (fieldTable.get(className).containsKey(fieldName)) {
                    // Replace the type
                    fieldTable.get(className).replace(fieldName, fieldType);
                } else {
                    // Add the new field name and type
                    fieldTable.get(className).put(fieldName, fieldType);
                }
            }

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

        // Create a table that stores the field offsets
        createFieldInfo();

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

        // n.f3.accept(this, argu);
        // We don't need to visit the f3 node only the method declerations
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

        String retReg = n.f10.accept(this, new argsObj(argu.className, methId, true, true));
        emit("\t" + "ret " + emitType(retType) + " " + retReg + "\n");

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
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, argsObj argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        String exprReg = n.f2.accept(this, argu);
        emit("\t" + "call void (i32) @print_int(i32 " + exprReg + ")" + "\n");

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    public String visit(AndExpression n, argsObj argu) throws Exception {
        String labelZero = getLabel();
        String labelOne = getLabel();
        String labelTwo = getLabel();
        String labelThree = getLabel();

        // The br instruction
        String firstClause = n.f0.accept(this, argu);
        emit("\t" + "br i1 " + firstClause + ", label %" + labelOne + ", label %" + labelZero + "\n");

        // Short circuit if false
        emit("\t" + labelZero + ":" + "\n");
        emit("\t" + "br label %" + labelTwo + "\n");

        // If true
        emit("\t" + labelOne + ":" + "\n");
        String secondClause = n.f2.accept(this, argu);
        emit("\t" + "br label %" + labelTwo + "\n");

        // The 'useless' block
        emit("\t" + labelTwo + ":" + "\n");
        emit("\t" + "br label %" + labelThree + "\n");

        // Phi instruction
        String phiReg = getReg();
        emit("\t" + labelThree + ":" + "\n");
        emit("\t" + phiReg + " = phi i1 [ 0, %" + labelZero + " ], [ " + secondClause + ", %" + labelTwo + " ]\n");

        regTable.put(phiReg, "boolean");
        return phiReg;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, argsObj argu) throws Exception {
        String leftExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightExpr = n.f2.accept(this, argu);

        String icmpReg = getReg();
        emit("\t" + icmpReg + " = icmp slt i32 " + leftExpr + ", " + rightExpr + "\n");

        regTable.put(icmpReg, "boolean");
        return icmpReg;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, argsObj argu) throws Exception {
        String leftExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightExpr = n.f2.accept(this, argu);

        String plusReg = getReg();
        emit("\t" + plusReg + " = add i32 " + leftExpr + ", " + rightExpr + "\n");

        regTable.put(plusReg, "int");
        return plusReg;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, argsObj argu) throws Exception {
        String leftExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightExpr = n.f2.accept(this, argu);

        String minusReg = getReg();
        emit("\t" + minusReg + " = sub i32 " + leftExpr + ", " + rightExpr + "\n");

        regTable.put(minusReg, "int");
        return minusReg;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, argsObj argu) throws Exception {
        String leftExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightExpr = n.f2.accept(this, argu);

        String multReg = getReg();
        emit("\t" + multReg + " = mul i32 " + leftExpr + ", " + rightExpr + "\n");

        regTable.put(multReg, "int");
        return multReg;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, argsObj argu) throws Exception {
        String arrReg = n.f0.accept(this, argu);

        // TODO: Make it work for boolean arrays

        // Loading the size of the array
        String regArraySize = getReg();
        emit("\t" + regArraySize + " = load i32, i32* " + arrReg + "\n");

        n.f1.accept(this, argu);

        String sizeReg = n.f2.accept(this, argu);

        // Check that the index is legal
        String regSGE = getReg();
        emit("\t" + regSGE + " = icmp sge i32 " + sizeReg + ", 0" + "\n");
        String regSLT = getReg();
        emit("\t" + regSLT + " = icmp slt i32 " + sizeReg + ", " + regArraySize + "\n");

        // Checking that both conditions hold
        String regAND = getReg();
        emit("\t" + regAND + " = and i1 " + regSGE + ", " + regSLT + "\n");
        String labelOK = getLabel();
        String labelERR = getLabel();
        emit("\t" + "br i1 " + regAND + ", label %" + labelOK + ", label %" + labelERR + "\n");

        // Throwing OOB exception
        emit("\t" + labelERR + ":" + "\n");
        emit("\t" + "call void @throw_oob()" + "\n");
        emit("\t" + "br label %" + labelOK + "\n\n");

        // All ok
        emit("\t" + labelOK + ":" + "\n");

        // Adding 1 because the first first position holds the size of the array
        String regADD = getReg();
        emit("\t" + regADD + " = add i32 1, " + sizeReg + "\n");

        // Now get a pointer to the correct position
        String regGEP = getReg();
        emit("\t" + regGEP + " = getelementptr i32, i32* " + arrReg + ", i32 " + regADD + "\n");

        // Load and return the register
        String regL = getReg();
        emit("\t" + regL + " = load i32, i32* " + regGEP + "\n");

        n.f3.accept(this, argu);

        regTable.put(regL, "int[]"); // TODO: boolean fix
        return regL;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, argsObj argu) throws Exception {
        String arrReg = n.f0.accept(this, argu);

        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        // TODO: make it work for booleans

        // Since the length of the array has been stored in the first position
        // It's just a load instruction
        String regL = getReg();
        emit("\t" + regL + " = load i32, i32* " + arrReg + "\n");

        regTable.put(regL, "int");
        return regL;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, argsObj argu) throws Exception {
        String objReg = n.f0.accept(this, argu);

        // Do a bitcast to access the vtable
        String regBC = getReg();
        emit("\t" + regBC + " = bitcast i8* " + objReg + " to i8***" + "\n");

        // Load the vtable
        String regL = getReg();
        emit("\t" + regL + " = load i8**, i8*** " + regBC + "\n");

        // Find the type of the f0 primary expression
        String regType;
        if (Objects.equals(objReg, "%this")) {
            regType = argu.className;
        } else {
            regType = regTable.get(objReg);
        }

        n.f1.accept(this, argu);

        // Get a pointer to the correct offset
        String methName = n.f2.accept(this, argu);
        String methOffset = getMethOffset(regType, methName);
        String regGEP = getReg();

        emit("\t" + regGEP + " = getelementptr i8*, i8** " + regL + ", i32 " + methOffset + "\n");

        // Getting the function pointer
        String regLF = getReg();
        emit("\t" + regLF + " = load i8*, i8** " + regGEP + "\n");

        // Create a function pointer that matches its signature
        String regBCF = getReg();
        emit("\t" + regBCF + " = bitcast i8* " + regLF + " to ");

        String retType = symbolTable.classes.get(regType).classMethods.get(methName).returnType;
        emit(emitType(retType) + " (i8*");

        // Adding the rest of the parameters
        for (Map.Entry<String, String> paramEntry : symbolTable.classes.get(regType).classMethods
                .get(methName).methodParams.entrySet()) {
            String paramValue = paramEntry.getValue();

            emit("," + emitType(paramValue));
        }

        emit(")* \n");

        n.f3.accept(this, argu);

        // Performing the call
        String regCALL = getReg();
        arguIndex++;
        n.f4.accept(this, argu);

        emit("\t" + regCALL + " = call " + emitType(retType) + " " + regBCF + "(i8* " + objReg);

        if ((arguIndex + 1) == arguList.size()) {
            // Emit at once all the arguments that were collected
            emit(arguList.get(arguIndex));

            // Remove the last nested function in the argument
            arguList.remove(arguIndex);
        }
        arguIndex--;

        emit(")\n");

        n.f5.accept(this, argu);

        regTable.put(regCALL, regType);
        return regCALL;
    }

    /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    public String visit(ExpressionList n, argsObj argu) throws Exception {
        String _ret = null;

        String exprReg = n.f0.accept(this, argu);
        String regType = regTable.get(exprReg);
        arguList.add(", " + emitType(regType) + " " + exprReg);

        n.f1.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> ","
    * f1 -> Expression()
    */
    public String visit(ExpressionTerm n, argsObj argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);

        // Adding the new argument to the end of the previous ones
        String exprReg = n.f1.accept(this, argu);
        String regType = regTable.get(exprReg);
        String newExpr = arguList.get(arguIndex) + ", " + emitType(regType) + " " + exprReg;
        arguList.set(arguIndex, newExpr);

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

                regTable.put(register,
                        symbolTable.classes.get(argu.className).classMethods.get(argu.methName).varType(expr));
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

                regTable.put(regL, symbolTable.typeF(argu.className, expr));
                return regL;
            }
        } else if (n.f0.which == 4) {
            return "%this";
        } else {
            return expr;
        }
    }

    /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(IntegerArrayAllocationExpression n, argsObj argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        String sizeReg = n.f3.accept(this, argu);

        // Calculate size bytes to be allocated, plus one for storing the array size
        String regAdd = getReg();
        emit("\t" + regAdd + " = add i32 1, " + sizeReg + "\n");

        // Check that the size is >= 1
        String regCheck = getReg();
        emit("\t" + regCheck + " = icmp sge i32 " + regAdd + ", 1" + "\n");
        String labelOk = getLabel();
        String labelErr = getLabel();
        emit("\t" + "br i1 " + regCheck + ", label %" + labelOk + ", label %" + labelErr + "\n\n");

        // Throw negative size error
        emit("\t" + labelErr + ":" + "\n");
        emit("\t" + "call void @throw_nsz()" + "\n");
        emit("\t" + "br label " + labelOk + "\n\n");

        // Proceed with the allocation
        emit("\t" + labelOk + ":" + "\n");
        String regCalloc = getReg();
        String regBC = getReg();
        emit("\t" + regCalloc + " = call i8* @calloc(i32 " + regAdd + ", i32 4)" + "\n");
        emit("\t" + regBC + " = bitcast i8* " + regCalloc + " to i32*" + "\n");
        emit("\t" + "store i32 " + sizeReg + ", i32* " + regBC + "\n");

        n.f4.accept(this, argu);

        regTable.put(regBC, "int[]");
        return regBC;
    }

    /**
    * f0 -> "new"
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(BooleanArrayAllocationExpression n, argsObj argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        String sizeReg = n.f3.accept(this, argu);

        // Calculate size bytes to be allocated, plus 4 bytes for storing the array size
        String regAdd = getReg();
        emit("\t" + regAdd + " = add i32 4, " + sizeReg + "\n");

        // Check that the size is >= 4
        String regCheck = getReg();
        emit("\t" + regCheck + " = icmp sge i32 " + regAdd + ", 4" + "\n");
        String labelOk = getLabel();
        String labelErr = getLabel();
        emit("\t" + "br i1 " + regCheck + ", label %" + labelOk + ", label %" + labelErr + "\n\n");

        // Throw negative size error
        emit("\t" + labelErr + ":" + "\n");
        emit("\t" + "call void @throw_nsz()" + "\n");
        emit("\t" + "br label " + labelOk + "\n\n");

        // Proceed with the allocation
        emit("\t" + labelOk + ":" + "\n");
        String regCalloc = getReg();
        String regBC = getReg();
        emit("\t" + regCalloc + " = call i8* @calloc(i32 " + regAdd + ", i32 1)" + "\n");
        emit("\t" + regBC + " = bitcast i8* " + regCalloc + " to i32*" + "\n");
        emit("\t" + "store i32 " + sizeReg + ", i32* " + regBC + "\n");

        n.f4.accept(this, argu);

        regTable.put(regCalloc, "boolean[]");
        return regCalloc;
    }

    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, argsObj argu) throws Exception {
        n.f0.accept(this, argu);

        String className = n.f1.accept(this, argu);

        // Allocate memory on heap for the object
        int classSize = getClassSize(className);
        classSize += 8; // Adding 8 bytes to account for the vtable pointer

        String regCalloc = getReg();
        emit("\t" + regCalloc + " = call i8* @calloc(i32 1, i32 " + Integer.toString(classSize) + ")" + "\n");

        // Setting the vtable pointer
        String regBC = getReg();
        emit("\t" + regBC + " = bitcast i8* " + regCalloc + " to i8***" + "\n");

        // Get the address of the vtable
        String regGEP = getReg();
        String numMethods = getNumberMethods(className);
        emit("\t" + regGEP + " = getelementptr [" + numMethods + " x i8*], [" + numMethods + " x i8*]* @." + className
                + "_vtable, i32 0, i32 0" + "\n");

        // Set the vtable to the correct address
        emit("\t" + "store i8** " + regGEP + ", i8*** " + regBC + "\n");

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        regTable.put(regCalloc, className);
        return regCalloc;
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

        regTable.put(notReg, "boolean");
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