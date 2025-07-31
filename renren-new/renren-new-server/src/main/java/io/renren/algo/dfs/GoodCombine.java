package io.renren.algo.dfs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 给你一个 无重复元素 的整数数组 candidates 和一个目标整数 target ，
// 找出 candidates 中可以使数字和为目标数 target 的 所有 不同组合 ，
// 并以列表形式返回。你可以按 任意顺序 返回这些组合。
// 每个数字只能用一次
public class GoodCombine {

    public void dfs(int start, int[] can, int target, Set<Integer> used,int curSum, List<Integer> curCombine, List<List<Integer>> result) {
        if (curSum == target) {
            result.add(new ArrayList<>(curCombine));
            return;
        }

        if (curSum > target) {
            return;
        }

        for (int i = start; i < can.length; i++) {
            if(used.contains(i)) {
                continue;
            }

            curCombine.add(can[i]);
            used.add(i);
            dfs(i, can, target, used, curSum + can[i], curCombine, result);

            // 回溯:
            curCombine.remove(curCombine.size() - 1);
            used.remove(i);
        }



    }

    public List<List<Integer>> combine(int[] can, int target) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        List<Integer> curCombine = new ArrayList<>();
        Set<Integer> used = new HashSet<>();  // 如果只能用一次 就需要这个
        dfs(0, can, target, used, 0, curCombine, result);
        return result;
    }

    public static void main(String[] args) {
        int[] can = { 1, 2, 3, 4, 5, 6};
        int target = 6;
        List<List<Integer>> result = new GoodCombine().combine(can, target);
        result.forEach( list -> {
            list.forEach(System.out::print);
            System.out.println();
        });
    }
}
