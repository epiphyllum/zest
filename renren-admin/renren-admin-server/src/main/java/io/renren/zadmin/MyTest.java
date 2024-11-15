package io.renren.zadmin;

import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.entity.JFeeConfigEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MyTest {
    private BigDecimal calcOut(BigDecimal out, JFeeConfigEntity feeConfig) {
        // 正向计算
        BigDecimal deposit = out.multiply(feeConfig.getCostDepositRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal left = out.subtract(deposit);
        BigDecimal charge = left.multiply(feeConfig.getCostChargeRate()).setScale(2, RoundingMode.HALF_UP);
        left = left.subtract(charge);
        return left;
    }

    public BigDecimal calcTxnAmount(BigDecimal amount, JFeeConfigEntity feeConfig) {
        BigDecimal rate1 = BigDecimal.ONE.subtract(feeConfig.getCostChargeRate());
        BigDecimal rate2 = BigDecimal.ONE.subtract(feeConfig.getCostDepositRate());
        BigDecimal middle = amount.divide(rate1, 2, RoundingMode.HALF_UP);
        BigDecimal out = middle.divide(rate2, 2, RoundingMode.HALF_UP);

        BigDecimal left = calcOut(out, feeConfig);
        int compare = left.compareTo(amount);
        if (compare == 0) {
            return out;
        } else if (compare < 0) {
            out = out.add(new BigDecimal("0.01"));
        } else {
            out = out.subtract(new BigDecimal("0.01"));
        }

        left = calcOut(out, feeConfig);
        compare = left.compareTo(amount);
        if (compare == 0) {
            System.out.println(amount + " -> " + out + " adjust");
            return out;
        } else {
            throw new RenException("无法反算发起金额");
        }
    }

    public static void main (String[] args) {
        MyTest passwordTest = new MyTest();
        JFeeConfigEntity feeConfig = new JFeeConfigEntity();
        feeConfig.setCostChargeRate(new BigDecimal("0.01"));
        feeConfig.setCostDepositRate(new BigDecimal("0.01"));
        BigDecimal current = new BigDecimal("1.01");
        for (int i = 0; i < 100000; i++) {
            passwordTest.calcTxnAmount(current, feeConfig);
            current  = current.add(new BigDecimal("0.01"));
        }
    }
}
