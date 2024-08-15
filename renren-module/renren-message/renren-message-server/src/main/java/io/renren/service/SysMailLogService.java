/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.service;

import io.renren.commons.mybatis.service.BaseService;
import io.renren.commons.tools.page.PageData;
import io.renren.dto.SysMailLogDTO;
import io.renren.entity.SysMailLogEntity;

import java.util.Map;

/**
 * 邮件发送记录
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysMailLogService extends BaseService<SysMailLogEntity> {

    PageData<SysMailLogDTO> page(Map<String, Object> params);

    /**
     * 保存邮件发送记录
     * @param templateId  模板ID
     * @param from        发送者
     * @param to          收件人
     * @param cc          抄送
     * @param subject     主题
     * @param content     邮件正文
     * @param status      状态
     */
    void save(Long templateId, String from, String[] to, String[] cc, String subject, String content, Integer status);
}

