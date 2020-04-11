import syntaxtree.*;
import visitor.GJDepthFirst;

public class firstPhaseVisitor extends GJDepthFirst<String, argsObj> {

    mySymbolTable symbolTable;

    public firstPhaseVisitor(mySymbolTable symTable) {
        this.symbolTable = symTable;
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
    public String visit(MainClass n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);

        // Inserting the class name to the symbol table
        String className = n.f1.accept(this, argu);
        if (symbolTable.classes.isEmpty() == false) {
            System.err.println("Error");
            System.exit(1);
        }
        classValue value = new classValue(false, "");
        symbolTable.classes.put(className, value);

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        // Inserting the main function to the symbol table
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        methodValue meth = new methodValue("void");
        symbolTable.classes.get(className).classMethods.put("main", meth);

        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);

        // Inserting the main's parameter 
        String paramName = n.f11.accept(this, argu);
        symbolTable.classes.get(className).classMethods.get("main").methodParams.put(paramName, "String[]");

        n.f12.accept(this, argu);
        n.f13.accept(this, argu);

        // Inserting the variables inside the main
        n.f14.accept(this, new argsObj(className, "main", false, true));

        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);
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
    public String visit(ClassDeclaration n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);

        // Adding the class name after checking for duplicates
        String className = n.f1.accept(this, argu);
        if (symbolTable.checkClass(className) == true) {
            System.err.println("There is already a class with the name \'" + className + "\'");
            System.exit(1);
        }
        symbolTable.classes.put(className, new classValue(false, ""));

        n.f2.accept(this, argu);

        // Adding the data members of the class to the symbol table
        n.f3.accept(this, new argsObj(className, "", true, false));

        // Adding the methods of the class to the symbol table
        n.f4.accept(this, new argsObj(className, "", true, false));

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
    public String visit(ClassExtendsDeclaration n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);

        String child = n.f1.accept(this, argu);

        n.f2.accept(this, argu);

        String parent = n.f3.accept(this, argu);

        // Two checks: if the parent class has been declared and if the child class is a duplicate
        if (symbolTable.checkClass(parent) == false) {
            System.err.println("The parent class of \'" + child + "\' has not been declared");
            System.exit(1);
        }
        if (symbolTable.checkClass(child) == true) {
            System.err.println("There is already a class with the name \'" + child + "\'");
            System.exit(1);
        }
        symbolTable.classes.put(child, new classValue(true, parent));

        n.f4.accept(this, argu);

        // Since fields in the base and derived class can have the same name, no more checks are necessary
        n.f5.accept(this, new argsObj(child, "", true, false));

        // Adding the methods of the derived class
        n.f6.accept(this, new argsObj(child, "", true, false));

        // Checking if the redefined methods in the subclass are defined properly
        symbolTable.polyCheck(child, parent);

        n.f7.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, argsObj argu) {
        String _ret = null;
        String t = n.f0.accept(this, argu);
        String id = n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        if (argu.isClass == true) {
            // Check if variable is already a field in the class
            if (symbolTable.classes.get(argu.className).checkField(id) == true) {
                System.err.println(
                        "Variable \'" + id + "\' in class \'" + argu.className + "\' has already been declared");
                System.exit(1);
            }

            // Add field to class
            symbolTable.classes.get(argu.className).classFields.put(id, t);
            System.out.println("added var in class " + id);
        }
        if (argu.isMethod == true) {
            // Checking if variable is already decleared in parameters or local
            if (symbolTable.classes.get(argu.className).classMethods.get(argu.methName).checkVar(id) == true) {
                System.err.println(
                        "Variable \'" + id + "\' in function \'" + argu.methName + "\' has already been declared");
                System.exit(1);
            }

            // Add variable to method
            symbolTable.classes.get(argu.className).classMethods.get(argu.methName).methodLocals.put(id, t);
            System.out.println("added var " + id);
        }

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
    public String visit(MethodDeclaration n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);

        // Adding a new method to the class
        String methType = n.f1.accept(this, argu);
        String methName = n.f2.accept(this, argu);
        if (symbolTable.classes.get(argu.className).checkMethod(methName) == true) {
            System.err.println("There is already a method with the name \'" + methName + "\'" + " in the class \'"
                    + argu.className + "\'");
            System.exit(1);
        }
        methodValue meth = new methodValue(methType);
        symbolTable.classes.get(argu.className).classMethods.put(methName, meth);

        n.f3.accept(this, argu);

        // Adding the parameters
        n.f4.accept(this, new argsObj(argu.className, methName, false, true));

        n.f5.accept(this, argu);
        n.f6.accept(this, argu);

        // Adding the local variables
        n.f7.accept(this, new argsObj(argu.className, methName, false, true));

        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
    public String visit(FormalParameterList n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public String visit(FormalParameter n, argsObj argu) {
        String _ret = null;

        // Here I check and pass the parameters to the symbol table
        String t = n.f0.accept(this, argu);
        String id = n.f1.accept(this, argu);
        if (symbolTable.classes.get(argu.className).classMethods.get(argu.methName).checkParam(id) == true) {
            System.err.println(
                    "Parameter \'" + id + "\' in function \'" + argu.methName + "\' has already been declared");
            System.exit(1);
        }
        symbolTable.classes.get(argu.className).classMethods.get(argu.methName).methodParams.put(id, t);
        System.out.println("added param " + id);

        return _ret;
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
     */
    public String visit(FormalParameterTail n, argsObj argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterTerm n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    public String visit(Type n, argsObj argu) {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> BooleanArrayType()
    *       | IntegerArrayType()
    */
    public String visit(ArrayType n, argsObj argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "boolean"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(BooleanArrayType n, argsObj argu) {
        return n.f0.toString() + n.f1.toString() + n.f2.toString();
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(IntegerArrayType n, argsObj argu) {
        return n.f0.toString() + n.f1.toString() + n.f2.toString();
    }

    /**
    * f0 -> "boolean"
    */
    public String visit(BooleanType n, argsObj argu) {
        return n.f0.toString();
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, argsObj argu) {
        return n.f0.toString();
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, argsObj argu) {
        return n.f0.toString(); // Just making sure they are returned as strings
    }

}