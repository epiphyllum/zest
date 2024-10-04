package io.renren.manager;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zin.service.file.ZinFileService;
import io.renren.zin.service.umbrella.ZinUmbrellaService;
import io.renren.zin.service.umbrella.dto.TVaDepositConfirm;
import io.renren.zin.service.umbrella.dto.TVaDepositConfirmResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private ZinUmbrellaService zinUmbrellaService;

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

        //
        this.uploadFiles(entity);

        // 提交通联
        TVaDepositConfirm confirm = new TVaDepositConfirm();
        confirm.setApplyid(entity.getApplyid());
        confirm.setAmount(entity.getApplyAmount());
        confirm.setOtherfid(entity.getOtherfid());
        confirm.setTransferfid(entity.getTransferfid());
        TVaDepositConfirmResponse response = zinUmbrellaService.depositConfirm(confirm);

        // 更新为待匹配
        jMoneyDao.update(null, Wrappers.<JMoneyEntity>lambdaUpdate()
                .eq(JMoneyEntity::getId, entity.getId())
                .set(JMoneyEntity::getStatus, 1)
        );
    }
}
