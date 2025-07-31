package io.renren.algo.dp;

import lombok.extern.slf4j.Slf4j;

// 路径数
@Slf4j
public class NumOfPath {

    private int uniquePathsWithObstacles(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // 如果起点就是障碍， 就没什么好酸的
        if (grid[0][0] == 1) {
            return 0;
        }

        int[][] dp = new int[rows][cols];
        dp[0][0] = 1;

        // 第一行
        for (int i = 1; i < cols; i++) {
            // 如果是石头
            if (grid[0][i] == 1) {
                dp[0][i] = 0;
            } else {
                dp[0][i] = dp[0][i - 1];
            }
        }

        // 第一列
        for (int i = 1; i < rows; i++) {
            if (grid[i][0] == 1) {
                dp[i][0] = 0;
            } else {
                dp[i][0] = dp[i - 1][0];
            }
        }

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                if (grid[i][j] == 1) {
                    dp[i][j] = 0;
                } else {
                    dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
                }
            }
        }

        return dp[rows - 1][cols - 1];
    }

    public static void main(String[] args) {

        NumOfPath solution = new NumOfPath();

        int[][] obstacleGrid1 = {
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
        };
        System.out.println("输入: [[0,0,0],[0,1,0],[0,0,0]]");
        System.out.println("输出: " + solution.uniquePathsWithObstacles(obstacleGrid1));  // 输出: 2

        int[][] obstacleGrid2 = {
                {0, 1},
                {0, 0}
        };
        System.out.println("输入: [[0,1],[0,0]]");
        System.out.println("输出: " + solution.uniquePathsWithObstacles(obstacleGrid2));  // 输出: 1


    }


}
