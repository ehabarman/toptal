import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;

public class Test {

    /**
     * Returns precedence of a given operator
     * Higher returned value means higher precedence
     */
    static int getOperatorPrecedence(String op) {
        switch (op.toLowerCase()) {
            case "gt":
            case "ge":
            case "lt":
            case "le":
            case "eq":
            case "ne":
                return 3;
            case "and":
                return 2;
            case "or":
                return 1;
        }
        return -1;
    }

    static int precedence;

    static String infixToPostfix(String exp) {
        String result = new String("");
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i<exp.length(); ++i)
        {
            char c = exp.charAt(i);

            // If the scanned character is an operand, add it to output.
            if (Character.isLetterOrDigit(c))
                result += c;

                // If the scanned character is an '(', push it to the stack.
            else if (c == '(')
                stack.push(c);

                //  If the scanned character is an ')', pop and output from the stack
                // until an '(' is encountered.
            else if (c == ')')
            {
                while (!stack.isEmpty() && stack.peek() != '(')
                    result += stack.pop();

                if (!stack.isEmpty() && stack.peek() != '(')
                    return "Invalid Expression"; // invalid expression
                else
                    stack.pop();
            }
            else // an operator is encountered
            {
//                while (!stack.isEmpty() && getOperatorPrecedence(c) <= getOperatorPrecedence(stack.peek())){
//                    if(stack.peek() == '(')
//                        return "Invalid Expression";
//                    result += stack.pop();
//                }
                stack.push(c);
            }

        }

        // pop all the operators from the stack
        while (!stack.isEmpty()){
            if(stack.peek() == '(')
                return "Invalid Expression";
            result += stack.pop();
        }
        return result;
    }

    // Driver method
    public static void main(String[] args)
    {
//        String exp = "a+b*(c^d-e)^(f+g*h)-i";
//        System.out.println(infixToPostfix(exp));

        try {
            LocalTime time= LocalTime.parse("13:93", DateTimeFormatter.ofPattern("H:m"));
        }
        catch (Exception e) {
            System.out.println("asdasd");
        }

        LocalDate date= LocalDate.parse("1996-6-6", DateTimeFormatter.ofPattern("yyyy-M-d"));

        System.out.println(date);
    }

}