package io.renren.algo;

public class HappyNumber {

    public int digitSquareSum(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            n = n / 10;
            sum = sum + digit * digit;
        }
        return sum;
    }

    public boolean isHappy(int n) {
        int slow = n;
        int fast = digitSquareSum(n);
        while(fast != 1) {
            if (fast == slow) {
                System.out.println("has circular number");
                return false; // 有环
            }
            slow = digitSquareSum(slow);
            fast = digitSquareSum(digitSquareSum(fast));
        }
        return fast == 1;
    }

    public static void main(String[] args) {
        HappyNumber happyNumber = new HappyNumber();
        System.out.println(happyNumber.isHappy(1999));
    }
}
