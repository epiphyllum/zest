package io.renren.zadmin.service;

import io.renren.commons.mybatis.service.CrudService;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dto.JWalletTxnDTO;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zwallet.dto.WalletTxnItem;

import java.util.Map;

/**
 * j_wallet_txn
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
public interface JWalletTxnService extends CrudService<JWalletTxnEntity, JWalletTxnDTO> {
    PageData<WalletTxnItem> walletPage(Map<String, Object> params);
}