/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.config;

import com.alibaba.druid.sql.visitor.functions.Char;
import io.renren.commons.tools.config.ModuleConfig;
import io.renren.commons.tools.exception.RenException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 模块配置信息
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class ModuleConfigImpl implements ModuleConfig {
    @Override
    public String getName() {
        return "new";
    }

    public static void main(String[] args) {
        removeDup(new int[]{1,2,3,4,5,5,5,6,6,6,7,7});
    }


    public static int removeDup(int[] input) {
        int len = input.length;
        int i = 0, j = 1;

        boolean repeat = false;
        while(j < len) {
            if(input[i] != input[j]) {
                // 重复变为不重复
                if(repeat) {
                    repeat = false;
                    input[i+1] = input[j];
                    i = i + 1;
                    continue;
                }
                j++;
                i++;
                continue;
            }
            repeat = true;
            j++;
        }
        for (int i1 : input) {
            System.out.print(i1);
        }
        System.out.println("| len = " + i);
        return i + 1;
    }

}