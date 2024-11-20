package io.renren.zapi.vpa;

import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.vpa.dto.*;
import org.springframework.stereotype.Service;

@Service
public class ApiVpaService {
    // 发行预付费子卡
    public Result<NewPrepaidJobRes> newPrepaidJob(NewPrepaidJobReq request, ApiContext context) {
        return null;
    }

    // 发行共享子卡
    public Result<NewShareJobRes> newShareJob(NewShareJobReq request, ApiContext context) {
        return null;
    }

    // 发行结果
    public Result<NewShareJobRes> vpaJobQuery(VpaJobQuery request, ApiContext context) {
        return null;
    }
}
