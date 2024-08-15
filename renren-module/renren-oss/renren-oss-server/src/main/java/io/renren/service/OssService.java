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
import io.renren.entity.OssEntity;

import java.util.Map;

/**
 * 文件上传
 * 
 * @author Mark sunlightcs@gmail.com
 */
public interface OssService extends BaseService<OssEntity> {

	PageData<OssEntity> page(Map<String, Object> params);
}
