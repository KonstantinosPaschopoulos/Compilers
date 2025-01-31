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
            res = num(res);

            return res;
        } else {
            return res;
        }
    }

    private int num(int number) throws ParseError, IOException {
        int res = digit();

        // 'Building' all the digits of the current number
        number = (number * 10) + res;
        res = multiple(number);

        return res;
    }

    private float factor() throws ParseError, IOException {
        if (this.lookahead == '(') {
            // Handling the (exp) case
            consume('(');

            float res = exp();

            if (lookahead == ')') {
                consume(')');

                return res;
            } else {
                System.out.println("Was expecting ')'");
                throw new ParseError();
            }
        } else {
            // Starting with 0 in order not to affect the 'building of the digits'
            float res = num(0);

            return res;
        }
    }

    private float term2(float res) throws ParseError, IOException {
        if (this.lookahead == '*') {
            consume('*');

            res *= factor();
            res = term2(res);

            return res;
        } else if (this.lookahead == '/') {
            consume('/');

            res /= factor();
            res = term2(res);

            return res;
        } else {
            // ε
            return res;
        }
    }

    private float term() throws ParseError, IOException {
        float res = factor();
        res = term2(res);

        return res;
    }

    private float exp2(float res) throws ParseError, IOException {
        if (this.lookahead == '+') {
            consume('+');

            res += term();
            res = exp2(res);

            return res;
        } else if (this.lookahead == '-') {
            consume('-');

            res -= term();
            res = exp2(res);

            return res;
        } else {
            // ε
            return res;
        }
    }

    private float exp() throws ParseError, IOException {
        float res = term();
        res = exp2(res);

        return res;
    }

    private float syn() throws ParseError, IOException {
        float res = exp();

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