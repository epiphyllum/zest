package io.renren.zadmin.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.dao.SysDeptDao;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.entity.*;
import io.renren.zadmin.service.JCardService;
import io.renren.zbalance.Ledger;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * j_card
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@Service
@Slf4j
public class JCardServiceImpl extends CrudServiceImpl<JCardDao, JCardEntity, JCardDTO> implements JCardService {

    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private JVaDao jVaDao;
    @Resource
    private JMcardDao jMcardDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private Ledger ledger;
    @Resource
    private LedgerUtil ledgerUtil;
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private CommonFilter commonFilter;

    @Override
    public PageData<JCardDTO> page(Map<String, Object> params) {
        IPage<JCardEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                applyFilter(params)
        );
        return getPageData(page, JCardDTO.class);
    }

    @Override
    public QueryWrapper<JCardEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JCardEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterAll(wrapper, params);

        String cardno = (String) params.get("cardno");
        if (StringUtils.isNotBlank(cardno)) {
            wrapper.eq("cardno", cardno);
        }

        String producttype = (String) params.get("producttype");
        if (StringUtils.isNotBlank(producttype)) {
            wrapper.eq("producttype", producttype);
        }

        String surname = (String) params.get("surname");
        if (StringUtils.isNotBlank(surname)) {
            wrapper.eq("surname", surname);
        }

        String name = (String) params.get("name");
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq("name", name);
        }

        String normal = (String) params.get("normal");
        if (StringUtils.isNotBlank(normal)) {
            // 卡片管理: 需要卡状态是新的, 发卡成功后，
            wrapper.eq("state", ZinConstant.CARD_APPLY_SUCCESS);
        } else {
            // 发卡管理
            // wrapper.ne("state", ZinConstant.CARD_APPLY_SUCCESS);
        }

        return wrapper;
    }

}