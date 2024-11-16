package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.dao.SysDeptDao;
import io.renren.service.SysDeptService;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JLogDao;
import io.renren.zadmin.dto.JLogDTO;
import io.renren.zadmin.dto.JLogDTO;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JLogEntity;
import io.renren.zadmin.entity.JLogEntity;
import io.renren.zadmin.service.JLogService;
import io.renren.zcommon.ZestConstant;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * j_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Service
@Slf4j
public class JLogServiceImpl extends CrudServiceImpl<JLogDao, JLogEntity, JLogDTO> implements JLogService {

    @Resource
    private CommonFilter commonFilter;

    @Resource
    private JCardDao jCardDao;

    @Override
    public PageData<JLogDTO> page(Map<String, Object> params) {

        QueryWrapper<JLogEntity> wrapper = applyFilter(params);

        UserDetail user = SecurityUser.getUser();
        List<Long> cardList;
        // 商户
        if (ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
            // 找出预付费主卡
            cardList = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getMerchantId, user.getDeptId())
                    .eq(JCardEntity::getMarketproduct, ZinConstant.MP_VPA_MAIN_PREPAID)
                    .select(JCardEntity::getId)
            ).stream().map(JCardEntity::getId).toList();
        } else if (ZestConstant.USER_TYPE_SUB.equals(user.getUserType())) {
            // 找出预付费主卡
            cardList = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getSubId, user.getDeptId())
                    .eq(JCardEntity::getMarketproduct, ZinConstant.MP_VPA_MAIN_PREPAID)
                    .select(JCardEntity::getId)
            ).stream().map(JCardEntity::getId).toList();
        } else {
            cardList = null;
        }
        if (cardList != null && cardList.size() > 0) {
            wrapper.or(w -> {
                w.in("owner_id", cardList);
            });
        }
        IPage<JLogEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                wrapper
        );
        return getPageData(page, JLogDTO.class);
    }


    @Override
    public QueryWrapper<JLogEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JLogEntity> wrapper = new QueryWrapper<>();

        // 过滤
        commonFilter.setLogBalanceFilter(wrapper, params);

        // 归属方类型
        String ownerType = (String) params.get("ownerType");
        wrapper.eq(StringUtils.isNotBlank(ownerType), "owner_type", ownerType);

        // 币种
        String currency = (String) params.get("currency");
        if (StringUtils.isNotBlank(currency)) {
            List<String> list = Arrays.stream(currency.split(",")).toList();
            wrapper.in("currency", list);
        }

        // 余额ID
        String balanceId = (String) params.get("balanceId");
        if (StringUtils.isNotBlank(balanceId)) {
            wrapper.eq("balance_id", Long.parseLong(balanceId));
        }

        // 账户类型
        String balanceType = (String) params.get("balanceType");
        if (StringUtils.isNotBlank(balanceType)) {
            if (balanceType.endsWith("_")) {
                if (StringUtils.isNotBlank(currency)) {
                    // 余额类型以"_"结尾, 且有币种: 就等于
                    wrapper.eq("balance_type", balanceType + currency);
                    log.debug("1. use eq: {}", balanceType + currency);
                } else {
                    // 余额类型以"_"结尾, 没有币种: likeRight;
                    wrapper.likeRight("balance_type", balanceType);
                    log.debug("2. use likeRight: {}", balanceType);
                }
            } else {
                // 不以"_"结尾
                wrapper.eq("balance_type", balanceType);
                log.debug("3. use eq: {}", balanceType);
            }
        }

        // 原始凭证类型
        String originType = (String) params.get("originType");
        wrapper.eq(StringUtils.isNotBlank(originType), "origin_type", originType);

        // 记账类型
        String factType = (String) params.get("factType");
        wrapper.eq(StringUtils.isNotBlank(factType), "fact_type", factType);

        // 原始配置ID
        String factId = (String) params.get("factId");
        wrapper.eq(StringUtils.isNotBlank(factId), "fact_id", factId);

        return wrapper;
    }

}