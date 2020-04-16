import java.util.ArrayList;
import java.util.Objects;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class secondPhaseVisitor extends GJDepthFirst<String, argsObj> {

    mySymbolTable symbolTable;
    int arguIndex;
    ArrayList<String> arguList;

    public secondPhaseVisitor(mySymbolTable symTable) {
        this.symbolTable = symTable;
        this.arguIndex = -1;
        arguList = new ArrayList<String>();
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

        // Checking the statements of the main
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

        n.f8.accept(this, new argsObj(argu.className, methName, true, true));

        n.f9.accept(this, argu);

        // Check that return matches with declared type
        String retExpr = n.f10.accept(this, new argsObj(argu.className, methName, true, true));
        if (Objects.equals(retType, retExpr) == false) {
            System.err.println("Wrong type returned from method \'" + methName + "\'");
            System.exit(1);
        }

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

        // Check if types are matching
        String rightType = n.f2.accept(this, argu);
        if (Objects.equals(leftType, rightType) == false) {
            // System.err.println("Invalid type assigned to variable \'" + idName + "\'");
            // System.exit(1);
            // Also check is leftType is a superclass of rightType
            if (symbolTable.isParent(rightType, leftType) == false) {
                System.err.println("Invalid type assigned to variable \'" + idName + "\'");
                System.exit(1);
            }
        }

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

        // Make sure the expression inside the [] is an int
        String indexType = n.f2.accept(this, argu);
        if (Objects.equals("int", indexType) == false) {
            System.err.println("Array index has to be an integer");
            System.exit(1);
        }

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        // Check for matching types
        String rightType = n.f5.accept(this, argu);
        if (Objects.equals(leftType, "boolean[]")) {
            if (!Objects.equals(rightType, "boolean")) {
                System.err.println("Invalid type assigned into array \'" + arrayName + "\'");
                System.exit(1);
            }
        } else {
            if (!Objects.equals(rightType, "int")) {
                System.err.println("Invalid type assigned into array \'" + arrayName + "\'");
                System.exit(1);
            }
        }

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

        // Make sure the type is boolean
        String ifType = n.f2.accept(this, argu);
        if (Objects.equals("boolean", ifType) == false) {
            System.err.println("Expression inside if has to be boolean");
            System.exit(1);
        }

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

        // Make sure the type is boolean
        String whileType = n.f2.accept(this, argu);
        if (Objects.equals("boolean", whileType) == false) {
            System.err.println("Expression inside while has to be boolean");
            System.exit(1);
        }

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

        // Type inside print can only be int
        String printType = n.f2.accept(this, argu);
        if (Objects.equals("int", printType) == false) {
            System.err.println("Expression inside print has to be an integer");
            System.exit(1);
        }

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

    /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    public String visit(AndExpression n, argsObj argu) {
        String leftType = n.f0.accept(this, argu);

        n.f1.accept(this, argu);

        String rightType = n.f2.accept(this, argu);

        // && can only be between two booleans
        if (!Objects.equals("boolean", leftType) || !Objects.equals("boolean", rightType)) {
            System.err.println("&& expression has to be between two booleans");
            System.exit(1);
        }

        return "boolean";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, argsObj argu) {
        String leftType = n.f0.accept(this, argu);

        n.f1.accept(this, argu);

        String rightType = n.f2.accept(this, argu);
        // < can only be between two ints
        if (!Objects.equals("int", leftType) || !Objects.equals("int", rightType)) {
            System.err.println("< expression has to be between two integers");
            System.exit(1);
        }

        return "boolean";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, argsObj argu) {
        String leftType = n.f0.accept(this, argu);

        n.f1.accept(this, argu);

        String rightType = n.f2.accept(this, argu);
        // + can only be between two ints
        if (!Objects.equals("int", leftType) || !Objects.equals("int", rightType)) {
            System.err.println("+ expression has to be between two integers");
            System.exit(1);
        }

        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, argsObj argu) {
        String leftType = n.f0.accept(this, argu);

        n.f1.accept(this, argu);

        String rightType = n.f2.accept(this, argu);
        // - can only be between two ints
        if (!Objects.equals("int", leftType) || !Objects.equals("int", rightType)) {
            System.err.println("- expression has to be between two integers");
            System.exit(1);
        }

        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, argsObj argu) {
        String leftType = n.f0.accept(this, argu);

        n.f1.accept(this, argu);

        String rightType = n.f2.accept(this, argu);
        // * can only be between two ints
        if (!Objects.equals("int", leftType) || !Objects.equals("int", rightType)) {
            System.err.println("* expression has to be between two integers");
            System.exit(1);
        }

        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, argsObj argu) {
        // Make sure varType is of type int[] or boolean[]
        String varType = n.f0.accept(this, argu);
        if (!Objects.equals("boolean[]", varType) && !Objects.equals("int[]", varType)) {
            System.err.println("Invalid reference of an array type");
            System.exit(1);
        }

        n.f1.accept(this, argu);

        // Make sure the expression inside the [] is an integer
        String inType = n.f2.accept(this, argu);
        if (Objects.equals("int", inType) == false) {
            System.err.println("The array iterator \'" + inType + "\' is not an integer");
            System.exit(1);
        }

        n.f3.accept(this, argu);

        // Return the appropriate type for the element that was accessed
        if (Objects.equals("boolean[]", varType)) {
            return "boolean";
        } else {
            return "int";
        }
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, argsObj argu) {
        String exprType = n.f0.accept(this, argu);
        if (!Objects.equals("boolean[]", exprType) && !Objects.equals("int[]", exprType)) {
            System.err.println("Cannot use the .length operator on a variable that is not an array");
            System.exit(1);
        }

        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        // The length is an integer so we return the correct type
        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, argsObj argu) {
        String exprType = n.f0.accept(this, argu);

        // First of make sure it is not one of the basic types
        if (Objects.equals("boolean[]", exprType) || Objects.equals("int[]", exprType)
                || Objects.equals("int", exprType) || Objects.equals("boolean", exprType)) {
            System.err.println("Cannot call a method on a variable that is not a class");
            System.exit(1);
        }

        n.f1.accept(this, argu);

        String calledMethName = n.f2.accept(this, argu);

        String methType = symbolTable.verifyMethod(calledMethName, exprType);

        n.f3.accept(this, argu);

        // Type check the argument list
        arguIndex++;
        n.f4.accept(this, argu);

        if ((arguIndex + 1) == arguList.size()) {
            // There is a new element in the array
            symbolTable.checkArguments(calledMethName, exprType, arguList.get(arguIndex));

            // Remove the last nested function in the argument
            arguList.remove(arguIndex);
        } else {
            // There were no arguments
            symbolTable.checkArguments(calledMethName, exprType, "");
        }
        arguIndex--;

        n.f5.accept(this, argu);

        return methType;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, argsObj argu) {
        String _ret = null;

        // Create a new entry for the array that holds the argument
        String exprType = n.f0.accept(this, argu);
        arguList.add(exprType);

        n.f1.accept(this, argu);

        return _ret;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail n, argsObj argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, argsObj argu) {
        String _ret = null;
        n.f0.accept(this, argu);

        // Add an argument to the latest entry of the array
        String exprType = n.f1.accept(this, argu);
        exprType = arguList.get(arguIndex) + "," + exprType; // Stick the new argument to the end of the previous ones
        arguList.set(arguIndex, exprType);

        return _ret;
    }

    /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
    public String visit(Clause n, argsObj argu) {
        return n.f0.accept(this, argu);
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
    public String visit(PrimaryExpression n, argsObj argu) {
        String primExpr = n.f0.accept(this, argu);

        if (Objects.equals("int", primExpr) || Objects.equals("boolean", primExpr)
                || Objects.equals("boolean[]", primExpr) || Objects.equals("int[]", primExpr)) {
            // It's one of the basic types, return as is
            return primExpr;
        } else if (Objects.equals("this", primExpr)) {
            // Return the from which this was called
            return argu.className;
        } else if (n.f0.which == 3) {
            // In this case it is an identifier which means it is a variable,
            // so we check if it is properly declared and and return its type
            return symbolTable.verifyVar(primExpr, argu.methName, argu.className);
        } else {
            // In the last case we return either a bracketed expression or a type from the AllocationExpression() call
            return primExpr;
        }
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, argsObj argu) {
        n.f0.accept(this, argu);
        return "int";
    }

    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, argsObj argu) {
        n.f0.accept(this, argu);
        return "boolean";
    }

    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, argsObj argu) {
        n.f0.accept(this, argu);
        return "boolean";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, argsObj argu) {
        return n.f0.toString();
    }

    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, argsObj argu) {
        return n.f0.toString();
    }

    /**
    * f0 -> BooleanArrayAllocationExpression()
    *       | IntegerArrayAllocationExpression()
    */
    public String visit(ArrayAllocationExpression n, argsObj argu) {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> "new"
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(BooleanArrayAllocationExpression n, argsObj argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        // Making sure the index expression is an int
        String indexType = n.f3.accept(this, argu);
        if (Objects.equals("int", indexType) == false) {
            System.err.println("Invalid index expression");
            System.exit(1);
        }

        n.f4.accept(this, argu);
        return "boolean[]";
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(IntegerArrayAllocationExpression n, argsObj argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        // Making sure the index expression is an int
        String indexType = n.f3.accept(this, argu);
        if (Objects.equals("int", indexType) == false) {
            System.err.println("Invalid index expression");
            System.exit(1);
        }

        n.f4.accept(this, argu);
        return "int[]";
    }

    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, argsObj argu) {
        n.f0.accept(this, argu);

        // Making sure the class that is being used has been declared
        String className = n.f1.accept(this, argu);
        if (symbolTable.classes.containsKey(className) == false) {
            System.err.println("Cannot allocate \'" + className + "\' because it hasn't been declared before");
            System.exit(1);
        }

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        return className;
    }

    /**
    * f0 -> "!"
    * f1 -> Clause()
    */
    public String visit(NotExpression n, argsObj argu) {
        n.f0.accept(this, argu);

        // The operand to the right of '!' operator has to be boolean
        String clauseType = n.f1.accept(this, argu);
        if (Objects.equals("boolean", clauseType) == false) {
            System.err.println("Wrong operand type for \'!\' operator");
            System.exit(1);
        }

        return "boolean";
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, argsObj argu) {
        n.f0.accept(this, argu);

        // Making sure to return the type of the bracketed expression
        String expr = n.f1.accept(this, argu).toString();

        n.f2.accept(this, argu);
        return expr;
    }

}