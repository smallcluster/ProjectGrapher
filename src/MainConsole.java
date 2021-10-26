import eval.*;

import java.util.Scanner;

public class MainConsole {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Evaluator exp = new Evaluator();

        while (true){
            System.out.println("Enter expression :");
            String input = scanner.nextLine();
            if(input.equals("exit"))
                break;
            int n = exp.parse(input);
            if(n == -1){
                System.out.println("exp = "+exp);
                while (true){
                    System.out.println("Enter value for x :");
                    String val = scanner.nextLine();
                    if(val.equals("exit"))
                        break;
                    float x = Float.parseFloat(val);
                    System.out.println(exp+" = "+exp.eval(x, 0));
                }
            } else {
                System.out.println("Invalid expression.");
            }
        }
    }
}
