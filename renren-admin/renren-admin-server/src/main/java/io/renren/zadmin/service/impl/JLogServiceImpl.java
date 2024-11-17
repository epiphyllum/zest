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
import io.renren.zadmin.entity.JBalanceEntity;
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

import java.util.ArrayList;
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
    @Resource
    private SysDeptService sysDeptService;

    // 商户
    private void merchantFilter(QueryWrapper<JLogEntity> wrapper, UserDetail user, Map<String, Object> params) {
        // 找出预付费主卡
        wrapper.notLikeRight("balance_type", "AIP_");
        List<Long> cardList = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getMerchantId, user.getDeptId())
                .eq(JCardEntity::getMarketproduct, ZinConstant.MP_VPA_MAIN_PREPAID)
                .select(JCardEntity::getId)
        ).stream().map(JCardEntity::getId).toList();
        String ownerType = (String) params.get("ownerType");
        if (StringUtils.isNotBlank(ownerType)) {
            if (ownerType.equals(ZestConstant.USER_TYPE_PREPAID)) {
                wrapper.in("owner_id", cardList);
            } else {
                commonFilter.setLogBalanceFilter(wrapper, params);
            }
        } else {
            List<Long> ownerIdList = sysDeptService.getSubDeptIdList(user.getDeptId());
            ownerIdList.addAll(cardList);
            wrapper.in("owner_id", ownerIdList);
        }
    }
    // 子商户
    private void subFilter(QueryWrapper<JLogEntity> wrapper, UserDetail user, Map<String, Object> params) {
        wrapper.notLikeRight("balance_type", "AIP_");
        // 找出预付费主卡
        List<Long> cardList = jCardDao.selectList(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getSubId, user.getDeptId())
                .eq(JCardEntity::getMarketproduct, ZinConstant.MP_VPA_MAIN_PREPAID)
                .select(JCardEntity::getId)
        ).stream().map(JCardEntity::getId).toList();
        String ownerType = (String) params.get("ownerType");
        if (StringUtils.isNotBlank(ownerType)) {
            if (ownerType.equals(ZestConstant.USER_TYPE_SUB)) {
                wrapper.eq("owner_id", user.getDeptId());
            } else if (ownerType.equals(ZestConstant.USER_TYPE_PREPAID)) {
                if (cardList.size() > 0) {
                    wrapper.in("owner_id", cardList);
                }
            }
        } else {

            // 没有选择归属方类型
            ArrayList<Long> longs = new ArrayList<>();
            if (cardList.size() > 0) {
                longs.addAll(cardList);
            }
            longs.add(user.getDeptId());
            wrapper.in("owner_id", longs);

        }
    }

    @Override
    public PageData<JLogDTO> page(Map<String, Object> params) {
        QueryWrapper<JLogEntity> wrapper = applyFilter(params);
        IPage<JLogEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                wrapper
        );
        return getPageData(page, JLogDTO.class);
    }


    @Override
    public QueryWrapper<JLogEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JLogEntity> wrapper = new QueryWrapper<>();

        // 原始凭证类型
        String originType = (String) params.get("originType");
        if (StringUtils.isNotBlank(originType)) {
            wrapper.eq("origin_type", originType);
        }
        // 记账类型
        String factType = (String) params.get("factType");
        if (StringUtils.isNotBlank(factType)) {
            wrapper.eq("fact_type", factType);
        }
        // 原始配置ID
        String factId = (String) params.get("factId");
        if(StringUtils.isNotBlank(factId)) {
            wrapper.eq("fact_id", factId);
        }

        // 归属方类型
        String ownerType = (String) params.get("ownerType");
        if (StringUtils.isNotBlank(ownerType)) {
            wrapper.eq("owner_type", ownerType);
        }

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

        // 特殊的
        UserDetail user = SecurityUser.getUser();
        if (ZestConstant.USER_TYPE_MERCHANT.equals(user.getUserType())) {
            // 商户
            merchantFilter(wrapper, user, params);
        } else if (ZestConstant.USER_TYPE_SUB.equals(user.getUserType())) {
            // 子商户
            subFilter(wrapper, user, params);
        } else {
            // 代理, 运营
            commonFilter.setLogBalanceFilter(wrapper, params);
        }
        return wrapper;
    }
}