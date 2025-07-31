package io.renren.algo;

import java.util.Stack;

public class MinStack {

    private Stack<Integer> stack;
    private Stack<Integer> minStack;

    public void push(int x) {
        stack.push(x);
        if (minStack.peek() > x) {
            minStack.push(x);
        }
    }

    public void pop() {
        int y = stack.pop();
        if(y == minStack.peek()) {
            minStack.pop();
        }
    }

    public int min() {
        return minStack.peek();
    }

    public MinStack() {
        stack = new Stack<>();
        minStack = new Stack<>();
        minStack.push(Integer.MAX_VALUE);
    }

    public static void main(String[] args) {

        MinStack ms = new MinStack();

        ms.push(4); System.out.println(ms.min() + " -> 4");
        ms.push(5); System.out.println(ms.min() + " -> 4");
        ms.push(1); System.out.println(ms.min() + " -> 1");
        ms.push(2); System.out.println(ms.min() + " -> 1");
        ms.push(3); System.out.println(ms.min() + " -> 1");

        ms.pop(); System.out.println(ms.min() + " -> 1");
        ms.pop(); System.out.println(ms.min() + " -> 1");
        ms.pop(); System.out.println(ms.min() + " -> 2");
        ms.pop(); System.out.println(ms.min() + " -> 4");
        ms.pop(); System.out.println(ms.min() + " -> MAX");

    }
}
