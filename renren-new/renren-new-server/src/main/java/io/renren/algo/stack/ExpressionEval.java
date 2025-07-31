package io.renren.algo.stack;

import java.util.Stack;

public class ExpressionEval {

    boolean isOperator(String symbol) {
        if (symbol.equals("+") || symbol.equals("-") || symbol.equals("*") || symbol.equals("/")) {
            return true;
        }
        return false;
    }

    int evalRPN(String[] arr) {
        Stack<Integer> stack = new Stack<Integer>();
        for (String s : arr) {
            if (isOperator(s)) {
                int x = evalIt(s, stack.pop(), stack.pop());
                stack.push(x);
            } else {
                stack.push(Integer.parseInt(s));
            }
        }
        return stack.pop();
    }

    private int evalIt(String s, Integer pop, Integer pop1) {
        switch (s) {
            case "+":
                return  pop + pop1;
            case "-":
                return  pop1 - pop;
            case "*":
                return  pop * pop1;
            case "/":
                return  pop1 / pop;
            default:
                throw new IllegalArgumentException("invalid operator: " + s);
        }
    }

    public static void main(String[] args) {

        ExpressionEval solution = new ExpressionEval();

        String[] tokens1 = {"2", "1", "+", "3", "*"};
        int result1 = solution.evalRPN(tokens1);
        System.out.println("表达式的值: " + result1); // 输出 9

        String[] tokens2 = {"4", "13", "5", "/", "+"};
        int result2 = solution.evalRPN(tokens2);
        System.out.println("表达式的值: " + result2); // 输出 6
    }
}
