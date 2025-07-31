package io.renren.algo.dp;

import java.util.Arrays;

public class MaxIncreaseString {

    // 方法：计算最长递增子序列的长度
    public static int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        // 初始化 dp 数组，每个位置的初始值为 1: 自己作为一个序列
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);

        // 遍历数组中的每个元素
        for (int i = 1; i < nums.length; i++) {
            // 遍历当前元素之前的每个元素
            for (int j = 0; j < i; j++) {
                // 如果当前元素大于之前的元素
                if (nums[i] > nums[j]) {
                    // 更新 dp[i]，表示以 nums[i] 结尾的最长递增子序列长度
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }

        // 返回 dp 数组中的最大值，即最长递增子序列的长度
        int maxLength = 0;
        for (int length : dp) {
            maxLength = Math.max(maxLength, length);
        }
        return maxLength;
    }

    public static void main(String[] args) {
        int[] nums1 = {10, 9, 2, 5, 3, 7, 101, 18};
        System.out.println("输入: [10, 9, 2, 5, 3, 7, 101, 18]");
        System.out.println("输出: " + lengthOfLIS(nums1)); // 输出: 4

        int[] nums2 = {0, 1, 0, 3, 2, 3};
        System.out.println("输入: [0, 1, 0, 3, 2, 3]");
        System.out.println("输出: " + lengthOfLIS(nums2)); // 输出: 4

        int[] nums3 = {7, 7, 7, 7, 7, 7, 7};
        System.out.println("输入: [7, 7, 7, 7, 7, 7, 7]");
        System.out.println("输出: " + lengthOfLIS(nums3));
    }
}
