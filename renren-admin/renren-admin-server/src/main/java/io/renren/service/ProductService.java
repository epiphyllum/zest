package io.renren.service;

import io.renren.commons.mybatis.service.CrudService;
import io.renren.dto.ProductDTO;
import io.renren.entity.ProductEntity;

/**
 * 产品管理
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface ProductService extends CrudService<ProductEntity, ProductDTO> {

}