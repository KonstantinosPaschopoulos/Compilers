//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package syntaxtree;

/**
 * Grammar production:
 * f0 -> Type()
 * f1 -> Identifier()
 * f2 -> ";"
 */
public class VarDeclaration implements Node {
   public Type f0;
   public Identifier f1;
   public NodeToken f2;

   public VarDeclaration(Type n0, Identifier n1, NodeToken n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public VarDeclaration(Type n0, Identifier n1) {
      f0 = n0;
      f1 = n1;
      f2 = new NodeToken(";");
   }

   public void accept(visitor.Visitor v) throws Exception {
      v.visit(this);
   }
   public <R,A> R accept(visitor.GJVisitor<R,A> v, A argu) throws Exception {
      return v.visit(this,argu);
   }
   public <R> R accept(visitor.GJNoArguVisitor<R> v) throws Exception {
      return v.visit(this);
   }
   public <A> void accept(visitor.GJVoidVisitor<A> v, A argu) throws Exception {
      v.visit(this,argu);
   }
}

