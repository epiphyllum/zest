package io.renren.zadmin.service;

import io.renren.commons.mybatis.service.CrudService;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dto.JAuthedDTO;
import io.renren.zadmin.entity.JAuthedEntity;
import io.renren.zwallet.dto.WalletCardTxnItem;

import java.util.Map;

/**
 * j_authed
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-11
 */
public interface JAuthedService extends CrudService<JAuthedEntity, JAuthedDTO> {
    PageData<WalletCardTxnItem> walletPage(Map<String, Object> params);
}