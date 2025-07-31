package io.renren.algo.window;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeetCode14 {

    public static void main(String[] args) {

        String[] strs = new String[] {
                "abc",
                "ab",
                "abcd"
        };
        String rtn = longCommonPrefix(strs);
        System.out.println(rtn);
    }

    private static String longCommonPrefix(String[] strs) {
        int strCount = strs.length;
        int minLen = Integer.MAX_VALUE;
        for (String str : strs) {
            if (str.length() < minLen) {
                minLen = str.length();
            }
        }

        String todo = strs[0];

        int right = 0;
        for (int i = 0; i < minLen; i++) {
            boolean equal = true;
            for (int j = 1; j < strCount; j++) {
                log.info("compare {}[{}]  {}[{}]", todo, i, strs[j], i);
                if (strs[j].charAt(i) != todo.charAt(i)) {
                    equal = false;
                    break;
                }
            }
            if(!equal) {
                break;
            }
            log.info("position {} equals {}", i, todo.charAt(i));
            right = i;
        }
        log.info("right = {}", right);
        return todo.substring(0, right+1);
    }

}
