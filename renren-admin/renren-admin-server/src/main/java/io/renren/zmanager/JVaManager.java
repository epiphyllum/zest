package io.renren.zmanager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.entity.JVaEntity;
import io.renren.zin.accountmanage.ZinAccountManageService;
import io.renren.zin.accountmanage.dto.TVaListRequest;
import io.renren.zin.accountmanage.dto.TVaListResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JVaManager {

    @Resource
    private ZinAccountManageService zinAccountManageService;

    @Resource
    private JVaDao jVaDao;

    public void refresh() {
        //效验数据
        TVaListResponse tVaListResponse = zinAccountManageService.vaList(new TVaListRequest());
        Long aLong = jVaDao.selectCount(Wrappers.emptyWrapper());
        List<JVaEntity> jVaEntities = new ArrayList<>(tVaListResponse.getAccts().size());
        for (TVaListResponse.VaItem acct : tVaListResponse.getAccts()) {
            JVaEntity jVaEntity = ConvertUtils.sourceToTarget(acct, JVaEntity.class);
            jVaEntity.setTid(acct.getId());
            jVaEntities.add(jVaEntity);
        }
        if (aLong > 0) {
            for (JVaEntity jVaEntity : jVaEntities) {
                jVaDao.update(null, Wrappers.<JVaEntity>lambdaUpdate()
                        .eq(JVaEntity::getAccountno, jVaEntity.getAccountno())
                        .set(JVaEntity::getAmount, jVaEntity.getAmount())
                        .set(JVaEntity::getTid, jVaEntity.getTid())
                        .set(JVaEntity::getUpdateDate, new Date())
                );
            }
        } else {
            for (JVaEntity jVaEntity : jVaEntities) {
                jVaDao.insert(jVaEntity);
            }
        }
    }
}
