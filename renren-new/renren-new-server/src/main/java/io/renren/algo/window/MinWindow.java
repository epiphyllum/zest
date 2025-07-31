package io.renren.algo.window;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

// 最小覆盖子串
@Slf4j
public class LeetCode76 {

    public static boolean canCover(Map<Character, Integer> window, Map<Character, Integer> target) {
        for (Map.Entry<Character, Integer> entry : target.entrySet()) {
            Character key = entry.getKey();
            if (!window.containsKey(key)) {
                return false;
            }
            Integer value = entry.getValue();
            if (window.get(key) < value) {
                return false;
            }
        }
        return true;
    }

    public static String minCoverSubString(String input, String base) {
        if(base == null) {
            return null;
        }
        if (base.equals("")) {
            return "";
        }
        if (input == null || input.equals("")) {
            return null;
        }

        Map<Character, Integer> targetMap = new HashMap<>();

        // 统计每个字符出现次数
        for (char c : base.toCharArray()) {
            targetMap.put(c, targetMap.getOrDefault(c, 0) + 1);
        }

        int resultI = 0, resultJ = Integer.MAX_VALUE, resultLen = Integer.MAX_VALUE;
        int left = 0, right = 0;
        Map<Character, Integer> windowMap = new HashMap<>();

        //
        while(right < input.length()) {
            char c = input.charAt(right);
            right++;
            if (targetMap.containsKey(c)) {
                Integer cold = windowMap.getOrDefault(c, 0);
                windowMap.put(c, cold + 1);
                log.info("expand window [{}, {}) -> {}", left, right, c);
                if (windowMap.get(c) < targetMap.get(c).intValue()) {
                    continue;
                }
            } else {
                log.info("expand window [{}, {}) -> {}", left, right, c);
                continue;
            }

            // 如果执行到这里, 那么必然有一个字符数量， 当前窗口是大于目标字符串的, 可再这里判断: 是否能覆盖目标
            boolean covered = canCover(windowMap, targetMap);
            if(covered) {
                log.info("当前窗口可以覆盖, 开始移动left...");
                // 那么可以移动left, 知道不能覆盖为止
                while(left < right) {
                    char lc = input.charAt(left);
                    if (targetMap.containsKey(lc)) {
                        Integer cnt = windowMap.get(lc);
                        Integer targetCnt = targetMap.get(lc);
                        // 说明再移动left, 就破坏窗口了
                        if (cnt.intValue() == targetCnt.intValue()) {
                            log.info("要破坏覆盖了, 检查是否可以更新result...");
                            if (right - left < resultLen) {
                               log.info("update window [{}, {})", left, right);
                               resultI = left;
                               resultJ = right;
                            } else {
                               log.info("无需更新result [{}, {})", left, right);
                            }
                            windowMap.put(lc, cnt-1);
                            left++;
                            break;
                        } else {
                            windowMap.put(lc, cnt-1);
                            log.info("shrink window [{}, {}), left:{}", left, right, lc);
                            left++;
                        }
                    } else {
                        left++;
                        log.info("shrink window [{}, {}), left:{}", left, right, lc);
                    }
                }
            } else {
                continue;
            }
        }

        if (resultJ == Integer.MAX_VALUE) {
            return null;
        }
        return input.substring(resultI, resultJ);
    }

    public static void main(String[] args) {
        String s = "ADOBECODEBANC";
        String t = "ABC";
        String result = minCoverSubString(s, t);
        System.out.println("最小覆盖子串：" + result);

    }
}
