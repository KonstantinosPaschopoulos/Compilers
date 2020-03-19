import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Calculator calculator = new Calculator(System.in);
            System.out.println("=");
            System.out.println(calculator.eval());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseError err) {
            System.err.println(err.getMessage());
        }
    }
}