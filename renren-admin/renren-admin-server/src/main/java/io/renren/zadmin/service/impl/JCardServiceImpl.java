package io.renren.zadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zadmin.ZestConstant;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.dto.JMcardDTO;
import io.renren.zadmin.entity.*;
import io.renren.zadmin.service.JCardService;
import io.renren.zbalance.Ledger;
import io.renren.zbalance.LedgerUtil;
import io.renren.zin.config.CardProductConfig;
import io.renren.zin.config.ZestConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        CommonFilter.setFilterAll(wrapper, params);
        return wrapper;
    }

    @Override
    public void save(JCardDTO dto) {
        // 查询开卡费用配置
        CardProductConfig config = zestConfig.getCardProductConfig(dto.getProducttype(), dto.getCurrency(), dto.getCardtype());

        // 查询子商户va余额
        JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(dto.getSubId(), dto.getCurrency());
        if (subVaAccount.getBalance().compareTo(config.getFee()) == -1) {
            throw new RenException("余额不足");
        }

        Long merchantId = dto.getMerchantId();
        SysDeptEntity merchantDept = sysDeptDao.selectById(merchantId);
        Long pid = merchantDept.getPid();
        SysDeptEntity agentDept = sysDeptDao.selectById(pid);
        SysDeptEntity subDept = sysDeptDao.selectById(dto.getSubId());
        dto.setAgentId(agentDept.getId());
        dto.setAgentName(agentDept.getName());
        dto.setMerchantName(merchantDept.getName());
        dto.setSubName(subDept.getName());

        // 费用卡:  什么币种的卡， 就用那个va
        List<JVaEntity> jVaEntities = jVaDao.selectList(Wrappers.emptyWrapper());
        JVaEntity jVaEntity = jVaEntities.stream().filter(e -> e.getCurrency().equals(dto.getCurrency())).findFirst().get();
        dto.setPayerid(jVaEntity.getTid());

        // 查询商户的开通的主卡, 将子卡挂到某个主卡下
        JMcardEntity jMcardEntity = jMcardDao.selectOne(Wrappers.<JMcardEntity>lambdaQuery()
                .eq(JMcardEntity::getMerchantId, merchantId)
                .eq(JMcardEntity::getCurrency, dto.getCurrency())
                .eq(JMcardEntity::getCardtype, dto.getCardtype())
                .eq(JMcardEntity::getProducttype, dto.getProducttype())
                .eq(JMcardEntity::getState, "04")
        );
        if (jMcardEntity == null) {
            throw new RenException("请先给商户开通主卡-" + dto.getCurrency() + "-" + dto.getCardtype() + "-" + dto.getProducttype());
        }
        dto.setMaincardno(jMcardEntity.getCardno());

        // 如果子卡是主体是合作企业， 则通联接口要求必须填cusid
        if (dto.getBelongtype().equals("2")) {
            JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
            dto.setCusid(merchant.getCusid());
        }

        // 入库 + 记账
        JCardEntity entity = ConvertUtils.sourceToTarget(dto, JCardEntity.class);
        entity.setMerchantFee(config.getFee());
        jCardDao.insert(entity);

        ledger.ledgeOpenCard(entity);

        // 再查询余额
        subVaAccount = ledgerUtil.getSubVaAccount(dto.getSubId(), dto.getCurrency());
        if (subVaAccount.getBalance().compareTo(BigDecimal.ZERO) == -1) {
            throw new RenException("余额不足");
        }
    }

}