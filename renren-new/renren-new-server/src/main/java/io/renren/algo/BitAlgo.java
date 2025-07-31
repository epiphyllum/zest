package io.renren.algo;

public class BitAlgo {

    public void test() {
        int x = -20;
        System.out.println(Integer.toBinaryString(x) + " -> " + x);

        // 左移是不管符号位的
        for (int i = 0; i < 32; i++) {
            x = x << 1;
            System.out.println(Integer.toBinaryString(x) + " -> " + x);
        }

        // 带符号位的右移: 所以会始终是负数,  符号位不动
        x = -20;
        for (int i = 0; i < 32; i++) {
            x = x >> 1;
            System.out.println(Integer.toBinaryString(x) + " -> " + x);
        }

        // 左边补符号位
        x = -20;
        for (int i = 0; i < 32; i++) {
            x = x >>> 1;
            System.out.println(Integer.toBinaryString(x) + " -> " + x);
        }
    }


    public void test2() {

        // 判断末尾
        int x = 0xFF;

        int count = 0;
        while(x != 0) {
           if( (x & 1) == 1) {
               count++;
           }
           x = x >>> 1;   // 无符号右移动，高位总是补0
        }

        System.out.println("count = " + count);
    }

    public int reverseBits(int n) {
        int result = 0; // 初始化结果变量
        for (int i = 0; i < 32; i++) {
            // 提取最低位
            int bit = n & 1;
            // 将提取的位左移到相应的位置
            result = (result << 1) | bit;
            // 右移 n，以处理下一位
            n >>= 1;
        }
        return result;
    }

    public int onlyOne(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    public static void main(String[] args) {
        BitAlgo bitAlgo = new BitAlgo();
        bitAlgo.test2();
        int x = 0b10011111;
        int y = bitAlgo.reverseBits(x);
        System.out.println(Integer.toBinaryString(x));
        System.out.println(Integer.toBinaryString(y));

        ///////////////////////////////
        // onlyOne
        ///////////////////////////////
        int[] nums1 = {2, 2, 1};
        int[] nums2 = {4, 1, 2, 1, 2};
        int[] nums3 = {1};
        int[] nums4 = {1,1, 0, 0};
        System.out.println("示例 1 输出：" + bitAlgo.onlyOne(nums1));
        System.out.println("示例 2 输出：" + bitAlgo.onlyOne(nums2));
        System.out.println("示例 3 输出：" + bitAlgo.onlyOne(nums3));
        System.out.println("示例 4 输出：" + bitAlgo.onlyOne(nums4));
    }
}
