
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20160615 (GIT 4ac7450)
//----------------------------------------------------

import java_cup.runtime.*;
import java_cup.runtime.XMLElement;

/** CUP v0.11b 20160615 (GIT 4ac7450) generated parser.
  */
@SuppressWarnings({"rawtypes"})
public class Parser extends java_cup.runtime.lr_parser {

 public final Class getSymbolContainer() {
    return sym.class;
}

  /** Default constructor. */
  @Deprecated
  public Parser() {super();}

  /** Constructor which sets the default scanner. */
  @Deprecated
  public Parser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public Parser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\051\000\002\002\003\000\002\002\004\000\002\003" +
    "\004\000\002\004\004\000\002\004\003\000\002\007\011" +
    "\000\002\007\010\000\002\010\004\000\002\011\005\000" +
    "\002\011\002\000\002\017\003\000\002\017\003\000\002" +
    "\017\003\000\002\017\003\000\002\017\004\000\002\017" +
    "\004\000\002\017\003\000\002\021\006\000\002\021\005" +
    "\000\002\024\004\000\002\023\005\000\002\023\002\000" +
    "\002\020\005\000\002\022\011\000\002\026\005\000\002" +
    "\005\004\000\002\005\004\000\002\005\002\000\002\006" +
    "\006\000\002\006\005\000\002\012\004\000\002\016\005" +
    "\000\002\016\002\000\002\013\003\000\002\013\003\000" +
    "\002\013\003\000\002\013\003\000\002\013\004\000\002" +
    "\014\005\000\002\015\011\000\002\025\005" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\132\000\004\016\005\001\002\000\004\002\134\001" +
    "\002\000\004\005\131\001\002\000\010\002\uffe6\012\016" +
    "\016\014\001\002\000\004\002\001\001\002\000\004\002" +
    "\ufffd\001\002\000\010\002\uffe6\012\016\016\025\001\002" +
    "\000\004\002\uffff\001\002\000\010\002\uffe6\012\016\016" +
    "\025\001\002\000\004\005\053\001\002\000\010\002\uffe6" +
    "\012\016\016\014\001\002\000\004\005\017\001\002\000" +
    "\012\012\016\015\027\016\025\017\021\001\002\000\022" +
    "\002\uffdf\004\uffdf\006\uffdf\011\uffdf\012\uffdf\013\uffdf\014" +
    "\uffdf\016\uffdf\001\002\000\022\002\uffe0\004\uffe0\006\uffe0" +
    "\011\uffe0\012\uffe0\013\uffe0\014\uffe0\016\uffe0\001\002\000" +
    "\006\004\037\014\050\001\002\000\004\006\044\001\002" +
    "\000\022\002\uffdd\004\uffdd\006\uffdd\011\uffdd\012\uffdd\013" +
    "\uffdd\014\uffdd\016\uffdd\001\002\000\004\005\031\001\002" +
    "\000\022\002\uffde\004\uffde\006\uffde\011\uffde\012\uffde\013" +
    "\uffde\014\uffde\016\uffde\001\002\000\004\017\030\001\002" +
    "\000\022\002\uffdc\004\uffdc\006\uffdc\011\uffdc\012\uffdc\013" +
    "\uffdc\014\uffdc\016\uffdc\001\002\000\014\006\034\012\016" +
    "\015\027\016\025\017\021\001\002\000\004\006\043\001" +
    "\002\000\010\004\037\006\uffe1\011\036\001\002\000\022" +
    "\002\uffe4\004\uffe4\006\uffe4\011\uffe4\012\uffe4\013\uffe4\014" +
    "\uffe4\016\uffe4\001\002\000\004\006\uffe3\001\002\000\012" +
    "\012\016\015\027\016\025\017\021\001\002\000\012\012" +
    "\016\015\027\016\025\017\021\001\002\000\022\002\uffdb" +
    "\004\uffdb\006\uffdb\011\uffdb\012\uffdb\013\uffdb\014\uffdb\016" +
    "\uffdb\001\002\000\010\004\037\006\uffe1\011\036\001\002" +
    "\000\004\006\uffe2\001\002\000\022\002\uffe5\004\uffe5\006" +
    "\uffe5\011\uffe5\012\uffe5\013\uffe5\014\uffe5\016\uffe5\001\002" +
    "\000\012\012\016\015\027\016\025\017\021\001\002\000" +
    "\006\004\037\013\046\001\002\000\012\012\016\015\027" +
    "\016\025\017\021\001\002\000\022\002\uffda\004\037\006" +
    "\uffda\011\uffda\012\uffda\013\uffda\014\uffda\016\uffda\001\002" +
    "\000\012\012\016\015\027\016\025\017\021\001\002\000" +
    "\006\004\037\006\uffd9\001\002\000\004\002\ufffe\001\002" +
    "\000\014\006\055\012\016\015\027\016\056\017\021\001" +
    "\002\000\004\006\123\001\002\000\012\002\uffe4\007\063" +
    "\012\uffe4\016\uffe4\001\002\000\010\005\031\006\ufff8\011" +
    "\057\001\002\000\004\016\061\001\002\000\004\006\ufffa" +
    "\001\002\000\006\006\ufff8\011\057\001\002\000\004\006" +
    "\ufff9\001\002\000\012\012\072\015\073\016\067\017\071" +
    "\001\002\000\006\004\101\010\122\001\002\000\016\004" +
    "\ufff6\006\ufff6\010\ufff6\011\ufff6\013\ufff6\014\ufff6\001\002" +
    "\000\016\004\ufff5\006\ufff5\010\ufff5\011\ufff5\013\ufff5\014" +
    "\ufff5\001\002\000\020\004\ufff1\005\111\006\ufff1\010\ufff1" +
    "\011\ufff1\013\ufff1\014\ufff1\001\002\000\016\004\ufff4\006" +
    "\ufff4\010\ufff4\011\ufff4\013\ufff4\014\ufff4\001\002\000\016" +
    "\004\ufff7\006\ufff7\010\ufff7\011\ufff7\013\ufff7\014\ufff7\001" +
    "\002\000\004\005\076\001\002\000\006\016\074\017\075" +
    "\001\002\000\016\004\ufff2\006\ufff2\010\ufff2\011\ufff2\013" +
    "\ufff2\014\ufff2\001\002\000\016\004\ufff3\006\ufff3\010\ufff3" +
    "\011\ufff3\013\ufff3\014\ufff3\001\002\000\012\012\072\015" +
    "\073\016\067\017\071\001\002\000\004\006\105\001\002" +
    "\000\006\004\101\014\102\001\002\000\012\012\072\015" +
    "\073\016\067\017\071\001\002\000\012\012\072\015\073" +
    "\016\067\017\071\001\002\000\006\004\101\006\uffe9\001" +
    "\002\000\016\004\uffeb\006\uffeb\010\uffeb\011\uffeb\013\uffeb" +
    "\014\uffeb\001\002\000\012\012\072\015\073\016\067\017" +
    "\071\001\002\000\006\004\101\013\107\001\002\000\012" +
    "\012\072\015\073\016\067\017\071\001\002\000\016\004" +
    "\101\006\uffea\010\uffea\011\uffea\013\uffea\014\uffea\001\002" +
    "\000\014\006\114\012\072\015\073\016\067\017\071\001" +
    "\002\000\004\006\121\001\002\000\010\004\101\006\uffec" +
    "\011\115\001\002\000\016\004\uffef\006\uffef\010\uffef\011" +
    "\uffef\013\uffef\014\uffef\001\002\000\012\012\072\015\073" +
    "\016\067\017\071\001\002\000\004\006\uffee\001\002\000" +
    "\010\004\101\006\uffec\011\115\001\002\000\004\006\uffed" +
    "\001\002\000\016\004\ufff0\006\ufff0\010\ufff0\011\ufff0\013" +
    "\ufff0\014\ufff0\001\002\000\010\002\ufffb\012\ufffb\016\ufffb" +
    "\001\002\000\004\007\124\001\002\000\012\012\072\015" +
    "\073\016\067\017\071\001\002\000\006\004\101\010\126" +
    "\001\002\000\010\002\ufffc\012\ufffc\016\ufffc\001\002\000" +
    "\004\002\uffe8\001\002\000\004\002\uffe7\001\002\000\006" +
    "\006\132\016\133\001\002\000\004\007\063\001\002\000" +
    "\006\006\ufff8\011\057\001\002\000\004\002\000\001\002" +
    "" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\132\000\010\002\003\003\006\007\005\001\001\000" +
    "\002\001\001\000\002\001\001\000\014\004\011\005\007" +
    "\006\012\007\014\015\010\001\001\000\002\001\001\000" +
    "\002\001\001\000\010\005\127\006\012\015\010\001\001" +
    "\000\002\001\001\000\010\005\126\006\012\015\010\001" +
    "\001\000\002\001\001\000\014\004\051\005\007\006\012" +
    "\007\014\015\010\001\001\000\002\001\001\000\014\006" +
    "\025\013\021\014\023\015\017\025\022\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\014\006\025\012" +
    "\031\013\032\014\023\015\017\001\001\000\002\001\001" +
    "\000\004\016\034\001\001\000\002\001\001\000\002\001" +
    "\001\000\012\006\025\013\040\014\023\015\017\001\001" +
    "\000\012\006\025\013\037\014\023\015\017\001\001\000" +
    "\002\001\001\000\004\016\041\001\001\000\002\001\001" +
    "\000\002\001\001\000\012\006\025\013\044\014\023\015" +
    "\017\001\001\000\002\001\001\000\012\006\025\013\046" +
    "\014\023\015\017\001\001\000\002\001\001\000\012\006" +
    "\025\013\050\014\023\015\017\001\001\000\002\001\001" +
    "\000\002\001\001\000\016\006\025\010\053\012\031\013" +
    "\032\014\023\015\017\001\001\000\002\001\001\000\002" +
    "\001\001\000\004\011\057\001\001\000\002\001\001\000" +
    "\002\001\001\000\004\011\061\001\001\000\002\001\001" +
    "\000\012\017\063\020\067\021\065\022\064\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\014\017\077\020\067\021\065\022\064\026\076\001" +
    "\001\000\002\001\001\000\002\001\001\000\012\017\103" +
    "\020\067\021\065\022\064\001\001\000\012\017\102\020" +
    "\067\021\065\022\064\001\001\000\002\001\001\000\002" +
    "\001\001\000\012\017\105\020\067\021\065\022\064\001" +
    "\001\000\002\001\001\000\012\017\107\020\067\021\065" +
    "\022\064\001\001\000\002\001\001\000\014\017\112\020" +
    "\067\021\065\022\064\024\111\001\001\000\002\001\001" +
    "\000\004\023\115\001\001\000\002\001\001\000\012\017" +
    "\116\020\067\021\065\022\064\001\001\000\002\001\001" +
    "\000\004\023\117\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\012\017\124" +
    "\020\067\021\065\022\064\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\004" +
    "\010\053\001\001\000\002\001\001\000\004\011\057\001" +
    "\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$Parser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$Parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$Parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 1;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}


  /** Scan to get the next Symbol. */
  public java_cup.runtime.Symbol scan()
    throws java.lang.Exception
    {
 return s.next_token(); 
    }


    // Connect this parser to a scanner!
    Scanner s;
    Parser(Scanner s){ this.s=s; }


/** Cup generated class to encapsulate user supplied action code.*/
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
class CUP$Parser$actions {
  private final Parser parser;

  /** Constructor */
  CUP$Parser$actions(Parser parser) {
    this.parser = parser;
  }

  /** Method 0 with the actual generated action code for actions 0 to 300. */
  public final java_cup.runtime.Symbol CUP$Parser$do_action_part00000000(
    int                        CUP$Parser$act_num,
    java_cup.runtime.lr_parser CUP$Parser$parser,
    java.util.Stack            CUP$Parser$stack,
    int                        CUP$Parser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$Parser$result;

      /* select the action based on the action number */
      switch (CUP$Parser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // syn ::= expr_list 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("syn",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // $START ::= syn EOF 
            {
              Object RESULT =null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		String start_val = (String)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		RESULT = start_val;
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$Parser$parser.done_parsing();
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // expr_list ::= def rest_expr 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr_list",1, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // rest_expr ::= def rest_expr 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("rest_expr",2, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // rest_expr ::= call_list 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("rest_expr",2, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // def ::= IDENTIFIER LPAREN def_args RPAREN LCURLY body RCURLY 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("def",5, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-6)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // def ::= IDENTIFIER LPAREN RPAREN LCURLY body RCURLY 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("def",5, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-5)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // def_args ::= IDENTIFIER rest_args 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("def_args",6, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // rest_args ::= COMMA IDENTIFIER rest_args 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("rest_args",7, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // rest_args ::= 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("rest_args",7, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // body ::= STRING_LITERAL 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body",13, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 11: // body ::= body_if 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body",13, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 12: // body ::= body_func 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body",13, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 13: // body ::= body_concat 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body",13, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 14: // body ::= REVERSE STRING_LITERAL 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body",13, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 15: // body ::= REVERSE IDENTIFIER 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body",13, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 16: // body ::= IDENTIFIER 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body",13, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 17: // body_func ::= IDENTIFIER LPAREN body_args RPAREN 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body_func",15, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 18: // body_func ::= IDENTIFIER LPAREN RPAREN 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body_func",15, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 19: // body_args ::= body re_bod_args 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body_args",18, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 20: // re_bod_args ::= COMMA body re_bod_args 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("re_bod_args",17, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 21: // re_bod_args ::= 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("re_bod_args",17, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 22: // body_concat ::= body PLUS body 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body_concat",14, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 23: // body_if ::= IF LPAREN body_prefix RPAREN body ELSE body 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body_if",16, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-6)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 24: // body_prefix ::= body PREFIX body 
            {
              Object RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("body_prefix",20, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 25: // call_list ::= call call_list 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_list",3, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 26: // call_list ::= if_stmt call_list 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_list",3, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 27: // call_list ::= 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_list",3, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 28: // call ::= IDENTIFIER LPAREN call_args RPAREN 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 29: // call ::= IDENTIFIER LPAREN RPAREN 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 30: // call_args ::= call_arg rest_call 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_args",8, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 31: // rest_call ::= COMMA call_arg rest_call 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("rest_call",12, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 32: // rest_call ::= 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("rest_call",12, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 33: // call_arg ::= STRING_LITERAL 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_arg",9, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 34: // call_arg ::= if_stmt 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_arg",9, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 35: // call_arg ::= call 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_arg",9, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 36: // call_arg ::= concat 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_arg",9, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 37: // call_arg ::= REVERSE STRING_LITERAL 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_arg",9, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 38: // concat ::= call_arg PLUS call_arg 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("concat",10, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 39: // if_stmt ::= IF LPAREN prefix_stmt RPAREN call_arg ELSE call_arg 
            {
              String RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("if_stmt",11, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-6)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 40: // prefix_stmt ::= call_arg PREFIX call_arg 
            {
              Object RESULT =null;

              CUP$Parser$result = parser.getSymbolFactory().newSymbol("prefix_stmt",19, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number "+CUP$Parser$act_num+"found in internal parse table");

        }
    } /* end of method */

  /** Method splitting the generated action code into several parts. */
  public final java_cup.runtime.Symbol CUP$Parser$do_action(
    int                        CUP$Parser$act_num,
    java_cup.runtime.lr_parser CUP$Parser$parser,
    java.util.Stack            CUP$Parser$stack,
    int                        CUP$Parser$top)
    throws java.lang.Exception
    {
              return CUP$Parser$do_action_part00000000(
                               CUP$Parser$act_num,
                               CUP$Parser$parser,
                               CUP$Parser$stack,
                               CUP$Parser$top);
    }
}

}
