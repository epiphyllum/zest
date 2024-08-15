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

import io.renren.commons.dynamic.datasource.annotation.DataSource;
import io.renren.dao.SysUserDao;
import io.renren.entity.SysUserEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试多数据源
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Service
public class DynamicDataSourceTestService extends DynamicService {
    @Resource
    private SysUserDao sysUserDao;

    @Transactional
    public void updateUser(Long id) {
        SysUserEntity user = new SysUserEntity();
        user.setId(id);
        user.setMobile("13500000000");
        sysUserDao.updateById(user);
    }

    @DataSource("slave1")
    @Transactional
    public void updateUserBySlave1(Long id) {
        SysUserEntity user = new SysUserEntity();
        user.setId(id);
        user.setMobile("13500000001");
        sysUserDao.updateById(user);
    }

    @DataSource("slave2")
    @Transactional
    public void updateUserBySlave2(Long id) {
        SysUserEntity user = new SysUserEntity();
        user.setId(id);
        user.setMobile("13500000002");
        sysUserDao.updateById(user);

        //测试事物
        int i = 1 / 0;
    }
}
