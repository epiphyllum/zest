package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.annotation.DataFilter;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.service.impl.SysDeptServiceImpl;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dto.JBalanceDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.service.JBalanceService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * j_balance
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@Service
public class JBalanceServiceImpl extends CrudServiceImpl<JBalanceDao, JBalanceEntity, JBalanceDTO> implements JBalanceService {

    @Resource
    private SysDeptServiceImpl sysDeptService;

    @Override
    public PageData<JBalanceDTO> page(Map<String, Object> params) {
        IPage<JBalanceEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JBalanceDTO.class);
    }

    @Override
    public QueryWrapper<JBalanceEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JBalanceEntity> wrapper = new QueryWrapper<>();

        String ownerId = (String) params.get("ownerId");
        if( ownerId == null) {
            UserDetail user = SecurityUser.getUser();
            ownerId = user.getDeptId().toString();
        }

        if (StringUtils.isNotBlank(ownerId)) {
            List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(Long.parseLong(ownerId));
            System.out.println("子部门id列表:");
            for (Long aLong : subDeptIdList) {
                System.out.println(aLong);
            }
            wrapper.in("owner_id", subDeptIdList);
        }


        String balanceType = (String) params.get("balanceType");
        wrapper.likeRight(StringUtils.isNotBlank(balanceType), "balance_type", balanceType);

        String currency = (String) params.get("currency");
        wrapper.eq(StringUtils.isNotBlank(currency), "currency", currency);

        return wrapper;
    }

}