package io.renren.algo;


import java.util.*;

public class BracketMatch {
    static Map<Character, Character> mapping = new HashMap<Character, Character>(){{
        put('}', '{');
        put(']', '[');
        put(')', '(');
    }};
    static Set<Character> set = new HashSet<Character>(mapping.keySet());
    boolean isRight(Character c) {
        return set.contains(c);
    }

    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if(stack.isEmpty()) {
                stack.push(c);
                continue;
            }
            if (!isRight(c)) {
                stack.push(c);
                continue;
            }
            Character peek = stack.peek();
            if (mapping.get(c).equals(peek)) {
                stack.pop();
            } else {
                stack.push(c);
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        BracketMatch bm = new BracketMatch();
        System.out.println(bm.isValid("{}"));
        System.out.println(bm.isValid("{[]}"));
        System.out.println(bm.isValid("{[]()()((()))}"));
    }
}
