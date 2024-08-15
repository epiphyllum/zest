/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.config;

import io.renren.commons.tools.config.ModuleConfig;
import org.springframework.stereotype.Service;

/**
 * 模块配置信息
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class ModuleConfigImpl implements ModuleConfig {
    @Override
    public String getName() {
        return "flow";
    }
}