package io.renren.zin.cardapply;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zmanager.JCardManager;
import io.renren.zmanager.JDepositManager;
import io.renren.zmanager.JMcardManager;
import io.renren.zmanager.JWithdrawManager;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JDepositDao;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dao.JWithdrawDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zcommon.ZinConstant;
import io.renren.zin.cardapply.dto.TCardApplyNotify;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用以在主卡/子卡申请、缴纳保证金、提取保证金、卡注销申请单状态变化时通知合作方。
 */
@Service
@Slf4j
public class ZinCardApplyNotifyService {

    // dao
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JMcardDao jMcardDao;
    @Resource
    private JDepositDao jDepositDao;
    @Resource
    private JWithdrawDao jWithdrawDao;
    // manager
    @Resource
    private JDepositManager jDepositManager;
    @Resource
    private JWithdrawManager jWithdrawManager;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private JMcardManager jMcardManager;

    // 卡申请单状态通知: 3005
    public void cardApplyNotify(TCardApplyNotify notify) {
        String trxcode = notify.getTrxcode();
        if (trxcode.equals(ZinConstant.CP450)) {
            handleCP450(notify);
        } else if (trxcode.equals(ZinConstant.CP453)) {
            handleCP453(notify);
        } else if (trxcode.equals(ZinConstant.CP458)) {
            handleCP458(notify);
        } else if (trxcode.equals(ZinConstant.CP451)) {
            handleCP451(notify);
        } else if (trxcode.equals(ZinConstant.CP452)) {
            handleCP452(notify);
        } else if (trxcode.equals(ZinConstant.CP462)) {
            handleCP452(notify);
        } else {
            log.error("unknown notify: {}", notify);
        }
    }

    // 收到主卡申请通知
    public void handleCP450MainCard(TCardApplyNotify notify) {
        JMcardEntity jMcardEntity = jMcardDao.selectOne(Wrappers.<JMcardEntity>lambdaQuery()
                .eq(JMcardEntity::getApplyid, notify.getApplyid())
        );
        // 状态一样
        if (jMcardEntity.getState().equals(notify.getState())) {
            return;
        }
        jMcardManager.query(jMcardEntity);
    }

    // 收到子卡申请通知
    public void handleCP450SubCard(TCardApplyNotify notify) {
        JCardEntity jCardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                .eq(JCardEntity::getApplyid, notify.getApplyid())
        );
        // 状态一样: 不需要处理
        if (jCardEntity.getState().equals(notify.getState())) {
            return;
        }
        // 查询通联, 并通知商户
        jCardManager.query(jCardEntity, true);
    }

    // CP450 开卡申请开卡
    public void handleCP450(TCardApplyNotify notify) {
        String cardbusinesstype = notify.getCardbusinesstype();
        // 主卡
        if (cardbusinesstype.equals("1")) {
            this.handleCP450MainCard(notify);
        }
        // 子卡
        if (cardbusinesstype.equals("0")) {
            this.handleCP450SubCard(notify);
        }
    }

    // CP453: 注销
    public void handleCP453(TCardApplyNotify notify) {
        this.handleCP450(notify);
    }

    // CP458: 注销撤回
    public void handleCP458(TCardApplyNotify notify) {
        this.handleCP450(notify);
    }

    //CP451 保证金缴纳
    public void handleCP451(TCardApplyNotify notify) {
        // 查询下申请单
        String applyid = notify.getApplyid();
        JDepositEntity entity = jDepositDao.selectOne(Wrappers.<JDepositEntity>lambdaQuery().eq(JDepositEntity::getApplyid, applyid));

        // 查询通联并通知商户
        jDepositManager.query(entity, true);
    }

    //CP452	保证金提现
    public void handleCP452(TCardApplyNotify notify) {
        String applyid = notify.getApplyid();
        JWithdrawEntity entity = jWithdrawDao.selectOne(Wrappers.<JWithdrawEntity>lambdaQuery().eq(JWithdrawEntity::getApplyid, applyid));

        // 查询通联并通知商户
        jWithdrawManager.query(entity, true);
    }

    // CP462: 释放担保金
    public void handleCP462(TCardApplyNotify notify) {
    }

}
