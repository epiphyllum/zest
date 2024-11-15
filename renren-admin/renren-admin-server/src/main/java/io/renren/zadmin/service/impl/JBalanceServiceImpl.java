package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.service.SysDeptService;
import io.renren.service.impl.SysDeptServiceImpl;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dto.JBalanceDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.service.JBalanceService;
import io.renren.zcommon.ZestConstant;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * j_balance
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Service
public class JBalanceServiceImpl extends CrudServiceImpl<JBalanceDao, JBalanceEntity, JBalanceDTO> implements JBalanceService {

    @Resource
    private CommonFilter commonFilter;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private SysDeptService sysDeptService;

    // 商户
    private void merchantFilter(QueryWrapper<JBalanceEntity> wrapper, UserDetail user, Map<String, Object> params) {
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
    private void subFilter(QueryWrapper<JBalanceEntity> wrapper, UserDetail user, Map<String, Object> params) {
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
            if (cardList.size() > 0) {
                wrapper.eq("owner_id", user.getDeptId()).or(w -> {
                    w.in("owner_id", cardList);
                });
            }
        }
    }

    @Override
    public PageData<JBalanceDTO> page(Map<String, Object> params) {
        IPage<JBalanceEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JBalanceDTO.class);
    }

    @Override
    public List<JBalanceDTO> list(Map<String, Object> params) {
        List<JBalanceEntity> jBalanceEntities = baseDao.selectList(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return ConvertUtils.sourceToTarget(jBalanceEntities, JBalanceDTO.class);
    }

    @Override
    public QueryWrapper<JBalanceEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JBalanceEntity> wrapper = new QueryWrapper<>();

        String ownerType = (String) params.get("ownerType");
        wrapper.eq(StringUtils.isNotBlank(ownerType), "owner_type", ownerType);

        String balanceType = (String) params.get("balanceType");
        wrapper.likeRight(StringUtils.isNotBlank(balanceType), "balance_type", balanceType);

        String currency = (String) params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);

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