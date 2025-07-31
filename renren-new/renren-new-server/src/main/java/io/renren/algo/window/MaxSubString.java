package io.renren.algo.window;

import io.renren.commons.tools.exception.RenException;

import java.util.HashSet;
import java.util.Set;

public class MaxSubString {

    // 滑动窗口
    public static String longSubString(String input) {
        if (input == null) {
            throw new RenException("invalid input");
        }

        // 长度为0, 1, 肯定就是自己
        if (input.length() == 1) {
            return input;
        }
        if (input.length() == 0) {
            return input;
        }

        char[] charArray = input.toCharArray();

        // 结果窗口:[0, 0), length = 0;
        int resultI = 0, resultJ = 0;

        // 当前窗口:[0, 0), length = 0
        int i = 0, j = 0;
        Set<Character> set = new HashSet<>();  // 当前窗口包含的字符

        while (j < charArray.length) {
            // 考察当前字符
            Character curChar = charArray[j];

            // 当前运行窗口, 每次都遇到新字符, 窗口扩大
            // new character, enlarge running window
            if (!set.contains(curChar)) {
                set.add(curChar);
                j++;
                continue;
            }

            // 遇到新字符串: 看当前窗口是否大于结果窗口, 更新结果
            // before change window, see if the running window is greater than result window
            int myLen = j - i;
            int resultLen = resultJ - resultI;
            if (myLen > resultLen) {
                resultI = i;
                resultJ = j;
            }

            // change running window: 改变当前运行窗口:
            // 移动i到 charArray[i] == curChar的位置， 然后再往后移动一位
            while (i < j) {
                // 将i移动直到， charArray[i]和curChar相同
                if (charArray[i] != curChar) {
                    set.remove(charArray[i]);
                    i++;
                    continue;
                }

                // 这个地方charArray[i] == curChar
                // 然后再下个位置删除一个:
                set.remove(charArray[i]);
                i++;
                break;
            }
        }
        return input.substring(resultI, resultJ);
    }


    public static void main(String[] args) {
        System.out.println(longSubString("bbbb"));
        System.out.println(longSubString("abcabcbb"));
        System.out.println(longSubString("pwwkew"));
    }

}
