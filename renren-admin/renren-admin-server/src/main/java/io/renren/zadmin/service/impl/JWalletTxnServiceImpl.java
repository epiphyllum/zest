package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zadmin.dto.JVpaAdjustDTO;
import io.renren.zadmin.dto.JWalletTxnDTO;
import io.renren.zadmin.entity.JVpaAdjustEntity;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zadmin.service.JWalletTxnService;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.WalletTxnItem;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_wallet_txn
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-28
 */
@Service
public class JWalletTxnServiceImpl extends CrudServiceImpl<JWalletTxnDao, JWalletTxnEntity, JWalletTxnDTO> implements JWalletTxnService {

    @Override
    public QueryWrapper<JWalletTxnEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JWalletTxnEntity> wrapper = new QueryWrapper<>();

        String agentId = (String) params.get("agentId");
        wrapper.eq(StringUtils.isNotBlank(agentId), "agent_id", agentId);

        String merchantId = (String) params.get("merchantId");
        wrapper.eq(StringUtils.isNotBlank(merchantId), "merchant_id", merchantId);

        String subId = (String) params.get("subId");
        wrapper.eq(StringUtils.isNotBlank(subId), "sub_id", subId);

        String maincardno = (String) params.get("maincardno");
        wrapper.eq(StringUtils.isNotBlank(maincardno), "maincardno", maincardno);

        String channelId = (String) params.get("channelId");
        wrapper.eq(StringUtils.isNotBlank(channelId), "channel_id", channelId);


        return wrapper;
    }

    @Override
    public PageData<WalletTxnItem> walletPage(Map<String, Object> params) {
        JWalletEntity walletEntity = WalletLoginInterceptor.walletUser();
        // 钱包id过滤
        QueryWrapper<JWalletTxnEntity> wrapper = applyFilter(params);
        wrapper.eq("wallet_id", walletEntity.getId());
        IPage<JWalletTxnEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                wrapper
        );
        return getPageData(page, WalletTxnItem.class);
    }

    @Override
    public PageData<WalletTxnItem> walletPage(Map<String, Object> params, Long walletId) {
        // 钱包id过滤
        QueryWrapper<JWalletTxnEntity> wrapper = applyFilter(params);
        wrapper.eq("wallet_id", walletId);
        IPage<JWalletTxnEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                wrapper
        );
        return getPageData(page, WalletTxnItem.class);
    }

}