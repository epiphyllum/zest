package io.renren.zadmin.service;

import io.renren.commons.mybatis.service.CrudService;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dto.JAuthDTO;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zwallet.dto.WalletCardTxnItem;

import java.util.Map;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
public interface JAuthService extends CrudService<JAuthEntity, JAuthDTO> {
    PageData<WalletCardTxnItem> walletPage(Map<String, Object> params);
    PageData<WalletCardTxnItem> walletPage(Map<String, Object> params, Long walletId);
}