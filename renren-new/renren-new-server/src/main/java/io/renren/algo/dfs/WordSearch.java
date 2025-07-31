package io.renren.algo;

import lombok.extern.slf4j.Slf4j;

// 单词搜索:
@Slf4j
public class WordSearch {

    // 深度优先搜索
    public boolean dfs(int i, int j, int len, char[][] board, String word) {
        // 找到了
        if (len == word.length()) {
            log.info("search ({}, {}), {}, found", i, j, len);
            return true;
        }

        // i, j 是否越界: 减枝
        if (i < 0 || j < 0 || i >= board.length || j >= board[0].length) {
            log.info("search ({}, {}), {}, pruned(out)", i, j, len);
            return false;
        }

        // 不匹配: 减枝
        if (board[i][j] != word.charAt(len)) {
            log.info("search ({}, {}), {}, pruned(no match)", i, j, len);
            return false;
        }
        log.info("search ({}, {}), {}, check:{}", i, j, len, board[i][j]);

        char temp = board[i][j];
        board[i][j] = '*';  // 用过了

        // 四个方向搜索
        boolean found =
                dfs(i + 1, j, len + 1, board, word) ||
                        dfs(i - 1, j, len + 1, board, word) ||
                        dfs(i, j + 1, len + 1, board, word) ||
                        dfs(i, j - 1, len + 1, board, word);

        // 回溯
        board[i][j] = temp;

        return found;
    }

    public boolean exist(char[][] board, String word) {
        int i = 0, j = 0;
        int len = 0;
        return dfs(0, 0, len, board, word);
    }

    public void printBoard(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j]);
                if (j == board[0].length - 1) {
                    System.out.println();
                }
            }
        }

    }
    public static void main(String[] args) {
        WordSearch solution = new WordSearch();

        char[][] board = {
                {'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}
        };

        String word1 = "ABCCED";  // true
        String word2 = "ABFDECCESE";  // true
        String word3 = "ABCB";    // false
        String word4 = "E";       // false
        // 输出结果
        System.out.println("search: " + word1);
        System.out.println(solution.exist(board, word1)); // 输出 true
        System.out.println("----------------------");
        System.out.println("search: " + word2);
        System.out.println(solution.exist(board, word2)); // 输出 true
        System.out.println("----------------------");
        System.out.println("search: " + word3);
        System.out.println(solution.exist(board, word3)); // 输出 false
        System.out.println("----------------------");
        System.out.println("search: " + word4);
        System.out.println(solution.exist(board, word4)); // 输出 false
    }

}
