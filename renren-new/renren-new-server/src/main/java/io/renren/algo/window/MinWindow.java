package io.renren.algo.window;

import java.util.HashMap;
import java.util.Map;

public class MinWindow {
    public String minWindow(String s, String t) {
        // 存储t中每个字符的出现次数
        Map<Character, Integer> targetMap = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetMap.put(c, targetMap.getOrDefault(c, 0) + 1);
        }

        // 滑动窗口中各字符的出现次数
        Map<Character, Integer> windowMap = new HashMap<>();

        int left = 0;          // 窗口左指针
        int right = 0;         // 窗口右指针
        int valid = 0;         // 记录窗口中满足条件的字符数
        int start = 0;         // 最小覆盖子串的起始索引
        int len = Integer.MAX_VALUE;  // 最小覆盖子串的长度

        while (right < s.length()) {
            // 移动右指针，扩大窗口
            char c = s.charAt(right);
            right++;

            // 如果当前字符是目标字符，更新窗口中的计数
            if (targetMap.containsKey(c)) {
                windowMap.put(c, windowMap.getOrDefault(c, 0) + 1);
                // 当窗口中该字符的数量等于目标数量时，valid加1
                if (windowMap.get(c).equals(targetMap.get(c))) {
                    valid++;
                }
            }

            // 当窗口包含了所有目标字符时，尝试收缩窗口
            while (valid == targetMap.size()) {
                // 更新目标
                if (right - left < len) {
                    start = left;
                    len = right - left;
                }

                // 移动左指针，缩小窗口
                char d = s.charAt(left);
                left++;

                // 如果移除的字符是目标字符，更新窗口计数
                if (targetMap.containsKey(d)) {
                    // 如果移除后数量不满足，valid减1
                    if (windowMap.get(d).equals(targetMap.get(d))) {
                        valid--;
                    }
                    windowMap.put(d, windowMap.get(d) - 1);
                }
            }
        }

        // 返回最小覆盖子串，如果不存在则返回空字符串
        return len == Integer.MAX_VALUE ? "" : s.substring(start, start + len);
    }

    public static void main(String[] args) {
        MinWindow solution = new MinWindow();
        System.out.println(solution.minWindow("ADOBECODEBANC", "ABC"));  // 输出 "BANC"
        System.out.println(solution.minWindow("a", "a"));  // 输出 "a"
        System.out.println(solution.minWindow("a", "aa"));  // 输出 ""
    }
}
