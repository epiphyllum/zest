/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.service;

import io.renren.commons.mybatis.service.CrudService;
import io.renren.commons.tools.page.PageData;
import io.renren.dto.SysNoticeDTO;
import io.renren.entity.SysNoticeEntity;

import java.util.Map;

/**
 * 通知管理
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysNoticeService extends CrudService<SysNoticeEntity, SysNoticeDTO> {

    /**
     * 获取被通知的用户
     */
    PageData<SysNoticeDTO> getNoticeUserPage(Map<String, Object> params);

    /**
     * 获取我的通知列表
     */
    PageData<SysNoticeDTO> getMyNoticePage(Map<String, Object> params);
}