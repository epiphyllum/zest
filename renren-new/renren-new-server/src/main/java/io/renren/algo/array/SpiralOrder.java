package io.renren.algo.array;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

// 顺时针螺旋矩阵
@Slf4j
public class SpiralOrder {
    public List<Integer> spiralOrder(int[][] matrix) {
       int top = 0;
       int left = 0;
       int bottom = matrix.length - 1;
       int right = matrix[0].length - 1;
       List<Integer> result = new ArrayList<>();

       while(top <= bottom && left <= right) {
           // left -> right:  top不动,
           for(int i = left; i <= right; i++) {
               result.add(matrix[top][i]);
           }
           top++;

           // top -> down 右边界: right不动
           for(int i = top; i <= bottom; i++) {
               result.add(matrix[i][right]);
           }
           right--;

           // right -> left 下边界: bottom不动
           if (bottom >= top) {
               for (int i = right; i >= left; i--) {
                   result.add(matrix[bottom][i]);
               }
               bottom--;
           }

           // 左边界: left不动
           if (left <= right) {
               for (int i = bottom; i > top ; i++) {
                   result.add(matrix[i][left]);
               }
               left++;
           }
       }
       return result;
    }

    public static void main(String[] args) {
        SpiralOrder solution = new SpiralOrder();
        int[][] matrix1 = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        System.out.println("顺时针螺旋顺序输出矩阵中的所有元素: " + solution.spiralOrder(matrix1));
        int[][] matrix2 = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12}
        };
        System.out.println("顺时针螺旋顺序输出矩阵中的所有元素: " + solution.spiralOrder(matrix2));
    }
}
