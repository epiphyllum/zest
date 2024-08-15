/**
 * Copyright 2018 人人开源 https://www.renren.io
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.renren.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 多数据源测试
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@SpringBootTest
public class DynamicDataSourceTest {
    @Resource
    private DynamicDataSourceTestService dynamicDataSourceTestService;

    @Test
    public void test() {
        Long id = 1067246875800000001L;
        dynamicDataSourceTestService.selectById(id);
        dynamicDataSourceTestService.updateUser(id);
        dynamicDataSourceTestService.updateUserBySlave1(id);
        dynamicDataSourceTestService.updateUserBySlave2(id);
    }


}
