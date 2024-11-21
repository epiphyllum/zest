package io.renren.zapi.vpa;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zapi.ApiNotifyService;
import io.renren.zapi.vpa.dto.*;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zmanager.JVpaManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiVpaNotifyService {
    @Resource
    private ApiNotifyService apiNotifyService;

    /**
     * 通知
     */
    public void vpaJobNotify(JVpaJobEntity entity, JMerchantEntity merchant) {
        VpaJobNotify jobNotify = new VpaJobNotify();
        apiNotifyService.notifyMerchant(jobNotify, merchant, "vpaJobNotify");
    }
}
