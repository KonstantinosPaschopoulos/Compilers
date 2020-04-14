import java.util.Objects;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class secondPhaseVisitor extends GJDepthFirst<String, argsObj> {

    mySymbolTable symbolTable;

    public secondPhaseVisitor(mySymbolTable symTable) {
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

        String className = n.f1.accept(this, argu);

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

        // Check types of variables of main
        n.f14.accept(this, argu);

        // TODO statement check
        n.f15.accept(this, new argsObj(className, "main", true, true));

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
        String className = n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        // Checking the types of the declared fields
        n.f3.accept(this, argu);

        // TODO method check
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
        String className = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        // Check the types of the fields
        n.f5.accept(this, argu);

        // TODO method check
        n.f6.accept(this, new argsObj(className, "", true, false));

        n.f7.accept(this, argu);
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

        // Check return type
        String retType = n.f1.accept(this, argu);
        String methName = n.f2.accept(this, argu);
        symbolTable.checkType(retType, methName);

        n.f3.accept(this, argu);

        // Check the types of the parameters
        n.f4.accept(this, argu);

        n.f5.accept(this, argu);
        n.f6.accept(this, argu);

        // Checks the type of the local variables in the method
        n.f7.accept(this, argu);

        // TODO Checks the statements in the function
        n.f8.accept(this, new argsObj(argu.className, methName, true, true));

        n.f9.accept(this, argu);

        // TODO check return matches with declared type
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

        // Make sure the type of the parameters is a legal type
        String paramType = n.f0.accept(this, argu);
        String paramName = n.f1.accept(this, argu);
        symbolTable.checkType(paramType, paramName);

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
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, argsObj argu) {
        String _ret = null;

        // Making sure the type that was used is legal
        String typeVar = n.f0.accept(this, argu);
        String name = n.f1.accept(this, argu);
        symbolTable.checkType(typeVar, name);

        n.f2.accept(this, argu);
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
        return n.f0.toString();
    }

    /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
    public String visit(Statement n, argsObj argu) {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
    public String visit(Block n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, argsObj argu) {
        String _ret = null;

        // Verify that the variable is properly declared and identifiable
        String idName = n.f0.accept(this, argu);
        String leftType = symbolTable.verifyVar(idName, argu.methName, argu.className);

        n.f1.accept(this, argu);

        // TODO: Check if types are matching
        n.f2.accept(this, argu);

        n.f3.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
    public String visit(ArrayAssignmentStatement n, argsObj argu) {
        String _ret = null;

        // Verify that the variable has been properly declared
        String arrayName = n.f0.accept(this, argu);
        String leftType = symbolTable.verifyVar(arrayName, argu.methName, argu.className);

        // leftType can only be type of int[] or boolean[]
        if (!Objects.equals("boolean[]", leftType) && !Objects.equals("int[]", leftType)) {
            System.err.println("Variable \'" + arrayName + "\' is not an array but tries to reference array type");
            System.exit(1);
        }

        n.f1.accept(this, argu);

        // TODO: Make sure the expression inside the [] is an int
        n.f2.accept(this, argu);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        // TODO: Check for matching types
        n.f5.accept(this, argu);

        n.f6.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    public String visit(IfStatement n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        // TODO: Make sure the type is boolean
        n.f2.accept(this, argu);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        // TODO: Make sure the type is boolean
        n.f2.accept(this, argu);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        // TODO: check type of print Type can only be int
        n.f2.accept(this, argu);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    // Expressions from here on out

    /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | Clause()
    */
    public String visit(Expression n, argsObj argu) {
        return n.f0.accept(this, argu).toString();
    }

}