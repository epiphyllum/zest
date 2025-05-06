package io.renren.zin.b2b;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.JB2bDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JB2bEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zcommon.AccessConfig;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.b2b.dto.TVaMoneyNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class B2bNotifyService {

    @Resource
    private ZestConfig zestConfig;
    @Resource
    private JB2bDao jb2bDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private B2bService b2bService;

    // 商户的入账通知
    public void merchantB2bNotified(JMerchantEntity merchant, TVaMoneyNotify tVaMoneyNotify) {
        log.info("商户收到b2b入金通知:{}", tVaMoneyNotify);
        JB2bEntity jb2bEntity = ConvertUtils.sourceToTarget(tVaMoneyNotify, JB2bEntity.class);

        // ID+name设置
        jb2bEntity.setAgentId(merchant.getAgentId());
        jb2bEntity.setAgentName(merchant.getAgentName());
        jb2bEntity.setMerchantId(merchant.getId());
        jb2bEntity.setMerchantName(merchant.getCusname());

        // 初始状态
        jb2bEntity.setState(ZinConstant.B2B_MONEY_NEW);  // 初始状态

        // 入库
        jb2bDao.insert(jb2bEntity);

        // 发起生态圈转账
        b2bService.ecoTransfer(merchant, jb2bEntity);
    }

    // 大吉自身的入账通知处理
    public void myB2bNotified(TVaMoneyNotify notifyDto) {
        log.info("大吉收到b2b入金通知:{}", notifyDto);
        JB2bEntity jb2bEntity = jb2bDao.selectOne(Wrappers.<JB2bEntity>lambdaQuery()
                .eq(JB2bEntity::getEcoApplyid, notifyDto.getApplyid())
        );
        // 发起同名转账
        b2bService.fundMerge(jb2bEntity);
    }
}
