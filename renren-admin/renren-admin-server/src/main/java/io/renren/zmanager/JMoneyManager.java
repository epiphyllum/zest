package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zcommon.CommonUtils;
import io.renren.zin.file.ZinFileService;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.TMoneyApply;
import io.renren.zin.umbrella.dto.TMoneyApplyResponse;
import io.renren.zin.umbrella.dto.TMoneyConfirm;
import io.renren.zin.umbrella.dto.TMoneyConfirmResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class JMoneyManager {
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private JMaccountDao jMaccountDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ZinUmbrellaService zinUmbrellaService;

    public void save(JMoneyEntity entity, String cardId) {
        // 填充商户代理信息
        JMerchantEntity merchant = jMerchantDao.selectById(entity.getMerchantId());
        entity.setMerchantName(merchant.getCusname());
        entity.setAgentName(merchant.getAgentName());
        entity.setAgentId(merchant.getAgentId());
        // 填充来账账户信息
        JMaccountEntity jMaccountEntity = jMaccountDao.selectOne(Wrappers.<JMaccountEntity>lambdaQuery().eq(JMaccountEntity::getCardId, cardId));
        entity.setCardname(jMaccountEntity.getCardname());
        entity.setCardno(jMaccountEntity.getCardno());
        // 调用通联
        TMoneyApply apply = new TMoneyApply();
        apply.setCurrency(entity.getCurrency());
        apply.setId(cardId);
        apply.setMeraplid(CommonUtils.newRequestId());
        TMoneyApplyResponse response = zinUmbrellaService.depositApply(apply);
        entity.setReferencecode(response.getReferencecode());
        entity.setApplyid(response.getApplyid());
        jMoneyDao.insert(entity);
    }

    public void uploadFiles(JMoneyEntity jMoneyEntity) {
        // 拿到所有文件fid
        String transferfid = jMoneyEntity.getTransferfid();
        String otherfid = jMoneyEntity.getOtherfid();

        List<String> fids = List.of(transferfid, otherfid);
        Map<String, CompletableFuture<String>> jobs = new HashMap<>();
        for (String fid : fids) {
            if (StringUtils.isBlank(fid)) {
                continue;
            }
            jobs.put(fid, CompletableFuture.supplyAsync(() -> {
                return zinFileService.upload(fid);
            }));
        }
        jobs.forEach((j, f) -> {
            log.info("wait {}...", j);
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RenException("can not upload file:" + j);
            }
        });
    }

    public void confirm(JMoneyEntity entity) {
        // 更新
        JMoneyEntity updateEntity = new JMoneyEntity();
        updateEntity.setId(entity.getId());
        updateEntity.setTransferfid(entity.getTransferfid());
        updateEntity.setOtherfid(entity.getOtherfid());
        updateEntity.setApplyAmount(entity.getApplyAmount());
        jMoneyDao.updateById(updateEntity);

        // 上传文件到通联
        this.uploadFiles(entity);

        // 提交通联
        TMoneyConfirm confirm = new TMoneyConfirm();
        confirm.setApplyid(entity.getApplyid());
        confirm.setAmount(entity.getApplyAmount());
        confirm.setOtherfid(entity.getOtherfid());
        confirm.setTransferfid(entity.getTransferfid());
        TMoneyConfirmResponse response = zinUmbrellaService.depositConfirm(confirm);

        // 更新为待匹配
        jMoneyDao.update(null, Wrappers.<JMoneyEntity>lambdaUpdate()
                .eq(JMoneyEntity::getId, entity.getId())
                .set(JMoneyEntity::getStatus, 0)
        );
    }
}
