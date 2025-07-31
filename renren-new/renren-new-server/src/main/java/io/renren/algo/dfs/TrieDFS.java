package io.renren.algo.dfs;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class TrieNode {
    TrieNode[] children;
    String word;  // 叶子节点保存完整单子

    public TrieNode() {
        children = new TrieNode[26];
        word = null;  // 代表非叶子
    }

    public boolean isLeaf(String word) {
        return word != null;
    }
}

class Trie {
    TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.word = word;  // 叶子节点放整个单词
    }
}

@Slf4j
public class TrieDFS {

    // 给你一个board,
    private List<String> findWords(char[][] board, String[] words) {
        // 构件trie
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        // 结果集
        Set<String> result = new HashSet<>();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                dfs(board, i, j, trie.root, result); // 从每个位置进行 DFS 搜索
            }
        }
        return new ArrayList<>(result);
    }

    private void dfs(char[][] board, int i, int j, TrieNode node, Set<String> result) {

        // 越界: 剪枝
        if (i < 0 || j < 0 || i >= board.length || j >= board[0].length || board[i][j] == '#') {
            return;
        }

        // 被使用过
        if (board[i][j] == '#') {
            return;
        }

        // trie树里没有匹配上
        if (node.children[board[i][j] - 'a'] == null) {
            return;
        }

        // trie匹配上了, node下移
        char c = board[i][j];
        node = node.children[c - 'a'];
        if (node.word != null) {
            result.add(node.word);
            node.word = null;  // 改掉防止重复添加
        }

        // 标记当前i,j被用
        board[i][j] = '#';
        dfs(board, i - 1, j, node, result);
        dfs(board, i + 1, j, node, result);
        dfs(board, i, j - 1, node, result);
        dfs(board, i, j + 1, node, result);

        // 回溯
        board[i][j] = c;
    }

    public static void main(String[] args) {
        TrieDFS solution = new TrieDFS();

        char[][] board = {
                {'o', 'a', 'a', 'n'},
                {'e', 't', 'a', 'e'},
                {'i', 'h', 'k', 'r'},
                {'i', 'f', 'l', 'v'}
        };
        String[] words = {"oath", "pea", "eat", "rain"};

        List<String> result = solution.findWords(board, words);
        System.out.println("找到的单词: " + result);
    }


}
