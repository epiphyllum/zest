package io.renren.algo.dp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MinSumPath {

    public int sum(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] dp = new int[rows][cols];
        dp[0][0] = matrix[0][0];

        // first row
        for (int i = 1; i < cols; i++) {
            dp[0][i] = dp[0][i - 1] + matrix[0][i];
        }

        // first column
        for (int i = 1; i < rows; i++) {
            dp[i][0] = dp[i-1][0] + matrix[i][0];
        }

        // 转移
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                dp[i][j] = Math.min(dp[i-1][j], dp[i][j-1]) + matrix[i][j];
            }
        }

        return dp[rows-1][cols-1];
    }

    public static void main(String[] args) {
        MinSumPath solution = new MinSumPath();
        int[][] grid1 = {
                {1, 3, 1},
                {1, 5, 1},
                {4, 2, 1}
        };
        System.out.println("输入: [[1,3,1],[1,5,1],[4,2,1]]");
        System.out.println("输出: " + solution.sum(grid1));  // 输出: 7

        int[][] grid2 = {
                {1, 2, 3},
                {4, 5, 6}
        };
        System.out.println("输入: [[1,2,3],[4,5,6]]");
        System.out.println("输出: " + solution.sum(grid2));  // 输出: 12
    }
}
