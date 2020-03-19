// Credits:
// Part of the code was taken from the Course's website:
// http://cgi.di.uoa.gr/~thp06/
// Inspiration was taken from these slides as well:
// http://www.sci.tamucc.edu/~sking/Courses/Compilers/Slides/grammarsandparsing.pdf

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

    private int digit() throws ParseError, IOException {
        if (this.lookahead >= '0' && this.lookahead <= '9') {
            int temp = evalDigit(this.lookahead);
            consume(this.lookahead);

            return temp;
        } else {
            throw new ParseError();
        }
    }

    // Handling numbers with multiple digits
    private int multiple(int res) throws ParseError, IOException {
        if (this.lookahead >= '0' && this.lookahead <= '9') {
            res = (res * 10) + num();

            return res;
        } else {
            return res;
        }
    }

    private int num() throws ParseError, IOException {
        int res = digit();
        res = multiple(res);

        return res;
    }

    private float factor(float res) throws ParseError, IOException {
        if (this.lookahead == '(') {
            // Handling the (exp) case
            consume('(');

            res = exp(res);

            if (lookahead == ')') {
                consume(')');

                return res;
            } else {
                System.out.println("Was expecting ')'");
                throw new ParseError();
            }
        } else {
            res = num();

            return res;
        }
    }

    private float term2(float res) throws ParseError, IOException {
        if (this.lookahead == '*') {
            consume('*');

            res *= factor(res);
            res = term2(res);

            return res;
        } else if (this.lookahead == '/') {
            consume('/');

            res /= factor(res);
            res = term2(res);

            return res;
        } else {
            // ε
            return res;
        }
    }

    private float term(float res) throws ParseError, IOException {
        res = factor(res);
        res = term2(res);

        return res;
    }

    private float exp2(float res) throws ParseError, IOException {
        if (this.lookahead == '+') {
            consume('+');

            res += term(res);
            res = exp2(res);

            return res;
        } else if (this.lookahead == '-') {
            consume('-');

            res -= term(res);
            res = exp2(res);

            return res;
        } else {
            // ε
            return res;
        }
    }

    private float exp(float res) throws ParseError, IOException {
        res = term(res);
        res = exp2(res);

        return res;
    }

    private float syn() throws ParseError, IOException {
        float res = exp(0);

        return res;
    }

    public float eval() throws ParseError, IOException {
        float result = syn();

        if ((this.lookahead != '\n') && (this.lookahead != -1)) {
            throw new ParseError();
        }

        return result;
    }
}