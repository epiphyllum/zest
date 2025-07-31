package io.renren.algo.dfs;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

// 回溯: 给定n几个字符串,个出来: C(n,k)
@Slf4j
public class CombineK {

    public void dfs(int start, List<String> current, List<List<String>> result, int k, List<String> choices){
        if(current.size() == k){
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i <= choices.size() ; i++) {
            current.add(choices.get(i-1));
            dfs(i+1, current, result, k, choices);
            current.remove(current.size() - 1);
        }
    }
    public List<List<String>> combineK(List<String> choices, int k) {
        List<List<String>> result = new ArrayList<>();
        List<String> currentList = new ArrayList<>();
        dfs(1, currentList, result, k, choices);
        return result;
    }

    public static void main(String[] args) {

        List<String> words = new ArrayList<>();
        words.add("a");
        words.add("b");
        words.add("c");
        words.add("d");
        List<List<String>> result = new CombineK().combineK(words, 3);
        for (List<String> strings : result) {
            strings.forEach(System.out::print);
            System.out.println();
        }

    }
}
