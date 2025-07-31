package io.renren.algo.window;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 给定一个字符串 s 和一个字符串数组 words。 words 中所有字符串 长度相同。
public class LeetCode30 {

    private static List<Integer> findSubstring(String s, String[] words) {
        List<Integer> result = new ArrayList<Integer>();

        // 特殊情况
        if (s == null || s.length() == 0 || words == null || words.length == 0) {
            return result;
        }

        int stepLen =  words[0].length();      // 步长
        int wordCount = words.length;          // 单词数
        int totalLength = stepLen * wordCount; // 总长度

        Map<String, Integer> wordMap = new HashMap<>();
        for (String word : words) {
            wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
        }

        return result;
    }

    public static void main(String[] args) {
        String s = "barfoothefoobarman";
        String[] words = {"foo", "bar"};
        List<Integer> result = findSubstring(s, words);

        System.out.print("起始索引：");
        for (int idx : result) {
            System.out.print(idx + " ");
        }
        System.out.println();
    }


}
