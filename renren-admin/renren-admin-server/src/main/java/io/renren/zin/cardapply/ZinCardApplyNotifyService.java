package io.renren.zin.cardapply;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JVpaJobDao;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zmanager.JCardManager;
import io.renren.zmanager.JDepositManager;
import io.renren.zmanager.JVpaManager;
import io.renren.zmanager.JWithdrawManager;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JDepositDao;
import io.renren.zadmin.dao.JWithdrawDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JDepositEntity;
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
    private JVpaManager jVpaManager;
    @Resource
    private JVpaJobDao jVpaJobDao;

    // 卡申请单状态通知: 3005
    public void cardApplyNotify(TCardApplyNotify notify) {
        String trxcode = notify.getTrxcode();
        // 开卡
        if (trxcode.equals(ZinConstant.CP450)) {
            handleCP450(notify);
        }
        // 注销
        else if (trxcode.equals(ZinConstant.CP453)) {
            handleCP453(notify);
        }
        // 注销撤回
        else if (trxcode.equals(ZinConstant.CP458)) {
            handleCP458(notify);
        }
        // 保证金缴纳
        else if (trxcode.equals(ZinConstant.CP451)) {
            handleCP451(notify);
        }
        // 保证金提现
        else if (trxcode.equals(ZinConstant.CP452)) {
            handleCP452(notify);
        }
        // VPA卡开卡
        else if (trxcode.equals(ZinConstant.CP460)) {
            handleCP460(notify);
        } else {
            log.error("未知的交易类型: {}", notify);
        }
    }

    // CP450 开卡申请开卡
    public void handleCP450(TCardApplyNotify notify) {
        JCardEntity jCardEntity;
        if (notify.getCardno() == null) {
            log.info("申请单查询卡:{}...", notify.getApplyid());
            jCardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getApplyid, notify.getApplyid())
            );
        } else {
            log.info("卡号查询卡:{}...", notify.getCardno());
            jCardEntity = jCardDao.selectOne(Wrappers.<JCardEntity>lambdaQuery()
                    .eq(JCardEntity::getCardno, notify.getCardno())
            );
        }

        if (jCardEntity == null) {
            log.error("找不到卡申请单: notify:{}", notify);
            return;
        }

        // 状态一样: 不需要处理
        if (jCardEntity.getState().equals(notify.getState())) {
            return;
        }
        boolean notifyFlag = false;
        // 查询通联, 并通知商户
        if (jCardEntity.getApi().equals(1)) {
            notifyFlag = true;
        }
        jCardManager.query(jCardEntity, notifyFlag);
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
        boolean notifyFlag = false;
        if (entity.getApi().equals(1)) {
            notifyFlag = true;
        }
        jDepositManager.query(entity, notifyFlag);
    }

    //CP452	保证金提现
    public void handleCP452(TCardApplyNotify notify) {
        String applyid = notify.getApplyid();
        JWithdrawEntity entity = jWithdrawDao.selectOne(Wrappers.<JWithdrawEntity>lambdaQuery().eq(JWithdrawEntity::getApplyid, applyid));
        // 查询通联并通知商户
        boolean notifyFlag = false;
        if (entity.getApi().equals(1)) {
            notifyFlag = true;
        }
        jWithdrawManager.query(entity, notifyFlag);
    }

    // CP460: 通华金服共享卡子卡开卡通知
    public void handleCP460(TCardApplyNotify notify) {
        JVpaJobEntity entity = jVpaJobDao.selectOne(Wrappers.<JVpaJobEntity>lambdaQuery()
                .eq(JVpaJobEntity::getApplyid, notify.getApplyid())
        );
        boolean notifyFlag = false;
        if (entity.getApi().equals(1)) {
            notifyFlag = true;
        }
        jVpaManager.query(entity, notifyFlag);
    }

}
