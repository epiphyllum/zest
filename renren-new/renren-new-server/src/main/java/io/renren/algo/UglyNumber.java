package io.renren.algo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

//  能被2,3,5整除的数是
@Slf4j
public class UglyNumber {

    public int nthUglyNumber(int n) {
        List<Integer> uglyNumbers = new ArrayList<Integer>();
        uglyNumbers.add(1);
        int i2 =0, i3=0, i5=0;
        int k = 0;
        while (uglyNumbers.size() < n) {
            int i2Ugly = uglyNumbers.get(i2) * 2;
            int i3Ugly = uglyNumbers.get(i3) * 3;
            int i5Ugly = uglyNumbers.get(i5) * 5;
            int next = Math.min(Math.min(i2Ugly, i3Ugly), i5Ugly);
            if (next == i2Ugly) { i2++; }
            if (next == i3Ugly) { i3++; }
            if (next == i5Ugly) { i5++; }
            uglyNumbers.add(next);
            log.info("{} -> 2[{}] 3[{}] 5[{}] -> {}", k, i2Ugly, i3Ugly, i5Ugly, next );
            k++;
        }
        return uglyNumbers.get(n - 1);
    }

    // 动态规划
    public int dpUglyNumber(int n) {
        int[] ugly = new int[n];  // 前n个丑数
        ugly[0] = 1;
        int i2 = 0, i3 = 0, i5 = 0; // 分别追踪乘以2、3、5后的下一个丑数的位置
        for (int i = 1; i < n; i++) {

            log.info("{} -> {}, {}, {}",ugly[i-1], i2, i3, i5);

            int nextUgly = Math.min(ugly[i2] * 2, Math.min(ugly[i3] * 3, ugly[i5] * 5));
            ugly[i] = nextUgly; // 将下一个丑数加入序列

            // 如果下一个丑数是上一个乘以2的结果，则移动指针i2
            if (nextUgly == ugly[i2] * 2) {
                i2++;
            }
            // 同理，对于乘以3和5的情况
            if (nextUgly == ugly[i3] * 3) {
                i3++;
            }

            // 同理，对于乘以5的情况
            if (nextUgly == ugly[i5] * 5) {
                i5++;
            }
        }
        System.out.println();
        return ugly[n - 1];
    }

    public static void main(String[] args) {
        int n = new UglyNumber().dpUglyNumber(20);
        System.out.println("10, ugly number: " + n);
    }
}
