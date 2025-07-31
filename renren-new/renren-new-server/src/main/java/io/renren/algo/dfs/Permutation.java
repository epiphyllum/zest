package io.renren.algo.dfs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 全排列
public class Permutation {

    public void dfs(List<String> current, List<List<String>> result, Set<String> used, List<String> choices) {
        if(current.size() == choices.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        // 遍历所有元素
        for (int i = 0; i < choices.size(); i++) {
            if(used.contains(choices.get(i))) {
                continue;
            }
            String s = choices.get(i);
            current.add(s);
            used.add(s);

            dfs(current, result, used, choices);

            // 回溯
            current.remove(current.size() - 1);
            used.remove(s);
        }
    }

    public List<List<String>> permute(List<String> choices) {
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> current = new ArrayList<>();
        Set<String> used = new HashSet<>();
        dfs( current, result, used, choices);
        return result;
    }

    public static void main(String[] args) {
        List<String> words = new ArrayList<>();
        words.add("a");
        words.add("b");
        words.add("c");
        words.add("d");
        List<List<String>> result = new Permutation().permute(words);
        for (List<String> strings : result) {
            strings.forEach(System.out::print);
            System.out.println();
        }
    }
}
