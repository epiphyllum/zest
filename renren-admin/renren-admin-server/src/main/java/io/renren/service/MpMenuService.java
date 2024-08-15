package io.renren.service;


import io.renren.commons.mybatis.service.CrudService;
import io.renren.dto.MpMenuDTO;
import io.renren.entity.MpMenuEntity;

/**
 * 公众号自定义菜单
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface MpMenuService extends CrudService<MpMenuEntity, MpMenuDTO> {

    MpMenuDTO getByAppId(String appId);

    void deleteByAppId(String appId);
}