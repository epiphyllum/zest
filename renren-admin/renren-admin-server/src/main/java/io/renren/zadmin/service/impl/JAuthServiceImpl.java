package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dto.JAuthDTO;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JAuthedEntity;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zadmin.service.JAuthService;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.WalletCardTxnItem;
import io.renren.zwallet.dto.WalletTxnItem;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_auth
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Service
public class JAuthServiceImpl extends CrudServiceImpl<JAuthDao, JAuthEntity, JAuthDTO> implements JAuthService {

    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JAuthDTO> page(Map<String, Object> params) {
        IPage<JAuthEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JAuthDTO.class);
    }

    @Override
    public QueryWrapper<JAuthEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JAuthEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterAll(wrapper, params);

        String cardno = (String) params.get("cardno");
        wrapper.eq(StringUtils.isNotBlank(cardno), "cardno", cardno);

        String logkv = (String) params.get("logkv");
        wrapper.eq(StringUtils.isNotBlank(logkv), "logkv", logkv);

        String trxcode = (String) params.get("trxcode");
        wrapper.eq(StringUtils.isNotBlank(trxcode), "trxcode", trxcode);

        String trxdir = (String) params.get("trxdir");
        wrapper.eq(StringUtils.isNotBlank(trxdir), "trxdir", trxdir);

        String authcode = (String) params.get("authcode");
        wrapper.eq(StringUtils.isNotBlank(authcode), "authcode", authcode);

        return wrapper;
    }

    @Override
    public PageData<WalletCardTxnItem> walletPage(Map<String, Object> params) {
        JWalletEntity walletEntity = WalletLoginInterceptor.walletUser();
        QueryWrapper<JAuthEntity> wrapper = applyFilter(params);
        wrapper.eq("wallet_id", walletEntity.getId());
        IPage<JAuthEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                wrapper
        );
        return getPageData(page, WalletCardTxnItem.class);
    }

    @Override
    public PageData<WalletCardTxnItem> walletPage(Map<String, Object> params, Long walletId) {
        QueryWrapper<JAuthEntity> wrapper = applyFilter(params);
        wrapper.eq("wallet_id", walletId);
        IPage<JAuthEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                wrapper
        );
        return getPageData(page, WalletCardTxnItem.class);
    }

}