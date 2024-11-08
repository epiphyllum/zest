package io.renren.zadmin.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.service.JCardService;
import io.renren.zcommon.ZinConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

        String maincardno = (String) params.get("maincardno");
        if (StringUtils.isNotBlank(maincardno)) {
            wrapper.eq("maincardno", maincardno);
        }

        String cardno = (String) params.get("cardno");
        if (StringUtils.isNotBlank(cardno)) {
            wrapper.eq("cardno", cardno);
        }

        String producttype = (String) params.get("producttype");
        if (StringUtils.isNotBlank(producttype)) {
            wrapper.eq("producttype", producttype);
        }

        String marketproduct = (String) params.get("marketproduct");
        if (StringUtils.isNotBlank(marketproduct)) {
            String[] split = marketproduct.split(",");
            wrapper.in("marketproduct", List.of(split));
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
            // 发卡管理: 不展示VPA子卡， 因为vpa子卡不是这里发行的
            wrapper.notIn("marketproduct", List.of(ZinConstant.MP_VPA_SHARE, ZinConstant.MP_VPA_PREPAID));
        }
        return wrapper;
    }

}