/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.form.service;

import io.renren.commons.mybatis.service.BaseService;
import io.renren.form.dto.CorrectionDTO;
import io.renren.form.entity.CorrectionEntity;

/**
 * 转正申请
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface CorrectionService extends BaseService<CorrectionEntity> {

    CorrectionDTO get(String instanceId);

    void save(CorrectionDTO dto);
}