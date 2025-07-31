package io.renren.algo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

// 电话号码的字母组合
@Slf4j
public class PhoneCombine {

    // 数字到字母的映射表
    static String[] mapping = new String[]{
            "",    // 0
            "",    // 1
            "abc", // 2
            "def", // 3
            "ghi", // 4
            "jkl", // 5
            "mno", // 6
            "pqrs",// 7
            "tuv", // 8
            "wxyz" // 9
    };

    public List<String> letterCombinations(String digits) {
        if(digits == null || digits.length() == 0) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        StringBuilder curStr = new StringBuilder();
        dfs(digits, 0, curStr, result);
        return result;
    }

    private void dfs(String digits, int index, StringBuilder curStr, List<String> result) {
        if (index == digits.length()) {
            log.info("found: {} {}", index, curStr.toString());
            result.add(curStr.toString());
            return;
        }

        String possibleChars = mapping[digits.charAt(index) - '0'];
        // log.info("check: {} {}", index, possibleChars);

        for (char c : possibleChars.toCharArray()) {
            curStr.append(c);
            dfs(digits, index + 1, curStr, result);
            curStr.deleteCharAt(curStr.length() - 1);
        }
    }


    public static void main(String[] args) {
        PhoneCombine solution = new PhoneCombine();

        // 示例1
        String digits1 = "23";
        System.out.println("输入: " + digits1);
        System.out.println("输出: " + solution.letterCombinations(digits1));

        // 示例2
        String digits2 = "";
        System.out.println("输入: " + digits2);
        System.out.println("输出: " + solution.letterCombinations(digits2));

        // 示例3
        String digits3 = "2";
        System.out.println("输入: " + digits3);
        System.out.println("输出: " + solution.letterCombinations(digits3));
    }
}
