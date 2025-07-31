package io.renren.algo.dfs;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

// 有n对(), 请打印所有的括号字符串
@Slf4j
public class GenBracket {

    public List<String> gen(int n) {
        List<String> result = new ArrayList<String>();
        String current = "";
        int open = 0;
        int close = 0;
        dfs(current, result, n, open, close);
        return result;
    }

    private void dfs(String current, List<String> result, int n, int open, int close) {
        if (current.length() == 2 * n) {
            result.add(current.toString());
            return;
        }

        if (open < n) {
            dfs(current + "(", result, n, open + 1, close);
        }

        if (close < open) {
            dfs(current + ")", result, n, open, close + 1);
        }


    }

    public static void main(String[] args) {
        List<String> result = new GenBracket().gen(4);
        result.forEach(System.out::println);
    }
}
