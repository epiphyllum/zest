package io.renren.algo.dfs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Island {

    public int numIslands(char[][] grid) {
        // 如果网格为空，直接返回0
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int numIslands = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1') {
                    numIslands++;
                    dfs(grid, i, j);  // 将其他填成0
                }
            }
        }


        int i = 0;
        int j = 0;
        dfs(grid, i, j);

        return numIslands;
    }

    private void dfs(char[][] grid, int i, int j) {
        int rows = grid.length;
        int cols = grid[0].length;

        // 如果越界或者当前单元格不是'1'，直接返回
        if (i < 0 || i >= rows || j < 0 || j >= cols) {
            return;
        }

        // 到了岛外面， 也相当于越界了
        if (grid[i][j] != '1') {
            return;
        }

        // 当前格子设置为0
        grid[i][j] = '0';

        dfs(grid, i - 1, j);
        dfs(grid, i + 1, j);
        dfs(grid, i, j - 1);
        dfs(grid, i, j + 1);
    }

    public static void main(String[] args) {
        Island solution = new Island();

        char[][] grid1 = {
                {'1', '1', '1', '1', '0'},
                {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '0', '0', '0'}
        };

        char[][] grid2 = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };

        System.out.println("Grid 1岛屿数量: " + solution.numIslands(grid1)); // 输出: 1
        System.out.println("Grid 2岛屿数量: " + solution.numIslands(grid2)); // 输出: 3
    }
}
