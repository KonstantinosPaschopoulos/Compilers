import syntaxtree.*;
import visitor.GJDepthFirst;

public class llvmVisitor extends GJDepthFirst<String, String> {

    mySymbolTable symbolTable;
    String fileName;

    public llvmVisitor(mySymbolTable symbolTable, String fileName) {
        this.symbolTable = symbolTable;
        this.fileName = fileName;
    }

    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    public String visit(Goal n, String argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

}