package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JAuthedDao;
import io.renren.zadmin.dto.JAuthedDTO;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JAuthedEntity;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zadmin.entity.JWalletTxnEntity;
import io.renren.zadmin.service.JAuthedService;
import io.renren.zwallet.config.WalletLoginInterceptor;
import io.renren.zwallet.dto.WalletCardTxnItem;
import io.renren.zwallet.dto.WalletTxnItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_authed
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-10-11
 */
@Service
public class JAuthedServiceImpl extends CrudServiceImpl<JAuthedDao, JAuthedEntity, JAuthedDTO> implements JAuthedService {

    @Override
    public QueryWrapper<JAuthedEntity> getWrapper(Map<String, Object> params){
        QueryWrapper<JAuthedEntity> wrapper = new QueryWrapper<>();

        String cardno = (String)params.get("cardno");
        wrapper.eq(StringUtils.isNotBlank(cardno), "cardno", cardno);

        String authcode = (String)params.get("authcode");
        wrapper.eq(StringUtils.isNotBlank(authcode), "authcode", authcode);

        wrapper.orderByDesc("trxtime");

        return wrapper;
    }


    @Override
    public PageData<WalletCardTxnItem> walletPage(Map<String, Object> params) {
        JWalletEntity walletEntity = WalletLoginInterceptor.walletUser();
        QueryWrapper<JAuthedEntity> wrapper = applyFilter(params);
        wrapper.eq("wallet_id", walletEntity.getId());
        IPage<JAuthedEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                wrapper
        );
        return getPageData(page, WalletCardTxnItem.class);
    }

    @Override
    public PageData<WalletCardTxnItem> walletPage(Map<String, Object> params, Long walletId) {
        QueryWrapper<JAuthedEntity> wrapper = applyFilter(params);
        wrapper.eq("wallet_id", walletId);
        IPage<JAuthedEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                wrapper
        );
        return getPageData(page, WalletCardTxnItem.class);
    }

}