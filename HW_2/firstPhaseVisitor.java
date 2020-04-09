import syntaxtree.*;
import visitor.GJDepthFirst;

public class firstPhaseVisitor extends GJDepthFirst<String, String> {

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
    public String visit(MainClass n, String argu) {
        String _ret = null;
        n.f0.accept(this, argu);

        // Inserting the class name to the symbol table
        String className = n.f1.accept(this, argu);
        if (symbolTable.classes.isEmpty() == false) {
            System.err.println("Error");
            System.exit(1);
        }
        classValue value = new classValue();
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
        // LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        // symbolTable.classes.get(className).methodParams.put("main", params);
        // symbolTable.classes.get(className).methodParams.get("main").put(paramName, "String[]");
        symbolTable.classes.get(className).classMethods.get("main").methodParams.put(paramName, "String[]");

        n.f12.accept(this, argu);
        n.f13.accept(this, argu);

        // Inserting the variables inside the main
        System.out.println("db " + n.f14.accept(this, argu));

        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, String argu) {
        String _ret = null;
        String t = n.f0.accept(this, argu);
        String id = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        System.out.println("reap " + t + " " + id);
        return _ret;
    }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    public String visit(Type n, String argu) {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> BooleanArrayType()
    *       | IntegerArrayType()
    */
    public String visit(ArrayType n, String argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "boolean"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(BooleanArrayType n, String argu) {
        return n.f0.toString() + n.f1.toString() + n.f2.toString();
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(IntegerArrayType n, String argu) {
        return n.f0.toString() + n.f1.toString() + n.f2.toString();
    }

    /**
    * f0 -> "boolean"
    */
    public String visit(BooleanType n, String argu) {
        return n.f0.toString();
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, String argu) {
        return n.f0.toString();
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, String argu) {
        return n.f0.toString(); // Just making sure they are returned as strings
    }

}