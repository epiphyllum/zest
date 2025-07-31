package io.renren.algo.dp;

// 最大正方形
public class MaxSquare {
    public static int maximalSquare(char[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0; // 如果矩阵为空，返回 0
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] dp = new int[rows][cols]; // 初始化 dp 数组
        int maxSide = 0; // 用于记录最大的正方形边长

        // 遍历矩阵中的每一个元素
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1; // 如果是第一行或第一列，只能形成边长为 1 的正方形
                    } else {
                        dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1; // 状态转移方程
                    }
                    maxSide = Math.max(maxSide, dp[i][j]); // 更新最大边长
                }
            }
        }
        return maxSide * maxSide; // 返回最大正方形的面积
    }
}

