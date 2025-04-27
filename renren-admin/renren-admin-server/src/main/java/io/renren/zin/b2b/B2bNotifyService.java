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
import org.springframework.stereotype.Service;

@Service
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

        JB2bEntity jb2bEntity = ConvertUtils.sourceToTarget(tVaMoneyNotify, JB2bEntity.class);

        jb2bEntity.setAgentId(merchant.getAgentId());
        jb2bEntity.setAgentName(merchant.getAgentName());
        jb2bEntity.setMerchantId(merchant.getId());
        jb2bEntity.setMerchantName(merchant.getCusname());
        jb2bEntity.setState(ZinConstant.B2B_MONEY_NEW);  // 初始状态

        // 入库
        jb2bDao.insert(jb2bEntity);

        // 发起生态圈转账
        b2bService.ecoTransfer(merchant, jb2bEntity);
    }

    // 大吉自身的入账通知处理
    public void myB2bNotified(TVaMoneyNotify notifyDto) {
        String bid = notifyDto.getBid();
        JB2bEntity jb2bEntity = jb2bDao.selectOne(Wrappers.<JB2bEntity>lambdaQuery()
                .eq(JB2bEntity::getBid, bid)
        );

        String funMeraplid = CommonUtils.uniqueId();
        jb2bDao.update(Wrappers.<JB2bEntity>lambdaUpdate()
                .eq(JB2bEntity::getId, jb2bEntity.getId())
                .set(JB2bEntity::getState, ZinConstant.B2B_MONEY_MERGE)
                .set(JB2bEntity::getFunMeraplid, funMeraplid)
        );
        jb2bEntity.setFunMeraplid(funMeraplid);

        // 发起同名转账
        b2bService.fundMerge(jb2bEntity);
    }



}
