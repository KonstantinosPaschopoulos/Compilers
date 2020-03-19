// Credits:
// Part of the code was taken from the Course's website:
// http://cgi.di.uoa.gr/~thp06/

import java.io.IOException;
import java.io.InputStream;

public class Calculator {
    private InputStream in;
    private int lookahead;

    public Calculator(InputStream in) throws IOException {
        this.in = in;
        this.lookahead = in.read();
    }

    private void consume(int symbol) throws ParseError, IOException {
        if (this.lookahead != symbol)
            throw new ParseError();
        this.lookahead = in.read();
    }

    private int evalDigit(int digit) {
        return digit - '0';
    }

    private void digit() throws ParseError, IOException {
        if (this.lookahead >= '0' && this.lookahead <= '9') {
            System.out.println(evalDigit(this.lookahead));
            consume(this.lookahead);
            return;
        } else {
            System.out.println("HERE" + evalDigit(this.lookahead));
            throw new ParseError();
        }
    }

    private void multiple() throws ParseError, IOException {
        if (this.lookahead >= '0' && this.lookahead <= '9') {
            num();
            return;
        } else {
            return;
        }
    }

    private void num() throws ParseError, IOException {
        digit();

        multiple();

        return;
    }

    private void factor() throws ParseError, IOException {
        if (this.lookahead == '(') {
            System.out.println("(");
            consume('(');
            exp();
            if (lookahead == ')') {
                System.out.println(")");
                consume(')');
                return;
            } else {
                throw new ParseError();
            }
        } else {
            num();
            return;
        }
    }

    private void term2() throws ParseError, IOException {
        if (this.lookahead == '*') {
            System.out.println("*");
            consume('*');
            factor();
            term2();
            return;
        } else if (this.lookahead == '/') {
            System.out.println("/");
            consume('/');
            factor();
            term2();
            return;
        } else {
            return;
        }
    }

    private void term() throws ParseError, IOException {
        factor();

        term2();

        return;
    }

    private void exp2() throws ParseError, IOException {
        if (this.lookahead == '+') {
            System.out.println("+");
            consume('+');
            term();
            exp2();
            return;
        } else if (this.lookahead == '-') {
            System.out.println("-");
            consume('-');
            term();
            exp2();
            return;
        } else {
            return;
        }
    }

    private void exp() throws ParseError, IOException {
        term();

        exp2();

        return;
    }

    private double syn() throws ParseError, IOException {
        exp();

        return 12;
    }

    public double eval() throws ParseError, IOException {
        double result = syn();
        if ((this.lookahead != '\n') && (this.lookahead != -1)) {
            throw new ParseError();
        }

        return result;
    }
}