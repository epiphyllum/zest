package io.renren.algo.window;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeetCode209 {

    private int minSubArrayLen(int target, int[] nums) {

        int resultI = 0;
        int resultJ = Integer.MAX_VALUE - 1;

        int left = 0;
        int right = 0;
        int windowSum = 0;

        while (right < nums.length) {
            int cur = nums[right];
            windowSum += cur;
            right++;

            // 窗口继续扩大
            if (windowSum < target) {
                log.info("extend window, [{}, {}), sum = {}", left, right, windowSum);
                continue;
            }
            log.info("checki window, [{}, {}), sum = {}", left, right, windowSum);

            //  和过大就移动left
            while (left < right && windowSum > target) {
                windowSum -= nums[left];
                left++;
                log.info("shrink window, [{}, {}), sum = {}", left, right, windowSum);
            }

            // 窗口刚好合适, 更新result
            if (windowSum == target) {
                log.info("发行合适窗口:[{}, {})", left, right);
                int len = right - left;
                int resultLen = resultJ - resultI;
                if (len < resultLen) {
                    resultI = left;
                    resultJ = right;
                    log.info("更新结果: [{}, {}), len = {}", resultI, resultJ, len);
                }
            }
        }
        log.info("resultI = {}, resultJ = {}", resultI, resultJ);
        return resultJ - resultI;
    }

    public static void main(String[] args) {
        LeetCode209 solution = new LeetCode209();
        int[] nums = {2, 3, 1, 2, 4, 3};
        int target = 7;
        int result = solution.minSubArrayLen(target, nums);
        System.out.println("长度最小的连续子数组的长度为：" + result);
    }


}
