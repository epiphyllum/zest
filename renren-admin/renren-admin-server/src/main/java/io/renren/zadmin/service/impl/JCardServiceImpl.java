package io.renren.zadmin.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
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
    public List<JCardDTO> list(Map<String, Object> params) {
        QueryWrapper<JCardEntity> wrapper = getWrapper(params);
        List<JCardEntity> jCardEntities = baseDao.selectList(wrapper);
        return ConvertUtils.sourceToTarget(jCardEntities, JCardDTO.class);
    }

    @Override
    public QueryWrapper<JCardEntity> getWrapper(Map<String, Object> params) {
        QueryWrapper<JCardEntity> wrapper = new QueryWrapper<>();
        commonFilter.setFilterAll(wrapper, params);

        // id
        String id = (String) params.get("id");
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", Long.parseLong(id));
        }

        // 主卡
        String maincardno = (String) params.get("maincardno");
        if (StringUtils.isNotBlank(maincardno)) {
            wrapper.eq("maincardno", maincardno);
        }

        // 卡号
        String cardno = (String) params.get("cardno");
        if (StringUtils.isNotBlank(cardno)) {
            wrapper.eq("cardno", cardno);
        }

        // 卡id
        String cardid = (String) params.get("cardid");
        if (StringUtils.isNotBlank(cardid)) {
            wrapper.eq("cardid", cardid);
        }

        // 产品
        String marketproduct = (String) params.get("marketproduct");
        if (StringUtils.isNotBlank(marketproduct)) {
            String[] split = marketproduct.split(",");
            wrapper.in("marketproduct", List.of(split));
        }

        // 姓
        String surname = (String) params.get("surname");
        if (StringUtils.isNotBlank(surname)) {
            wrapper.eq("surname", surname);
        }

        // 名
        String name = (String) params.get("name");
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq("name", name);
        }

        // 已发行的卡 || 发卡中
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