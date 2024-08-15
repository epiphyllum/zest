/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import io.renren.commons.mybatis.service.impl.BaseServiceImpl;
import io.renren.dao.TableFieldDao;
import io.renren.entity.TableFieldEntity;
import io.renren.service.TableFieldService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 表
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class TableFieldServiceImpl extends BaseServiceImpl<TableFieldDao, TableFieldEntity> implements TableFieldService {

    @Override
    public List<TableFieldEntity> getByTableName(String tableName) {
        return baseDao.getByTableName(tableName);
    }

    @Override
    public void deleteByTableName(String tableName) {
        baseDao.deleteByTableName(tableName);
    }

    @Override
    public void deleteBatchTableIds(Long[] tableIds) {
        baseDao.deleteBatchTableIds(tableIds);
    }

}