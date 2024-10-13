package io.renren.zin.sub;

import io.renren.zin.ZinRequester;
import io.renren.zin.sub.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static io.renren.zcommon.CommonUtils.newRequestId;

@Service
@Slf4j
public class ZinSubService {
    @Resource
    private ZinRequester requester;

    // 创建通联子商户: 5000
    public TSubCreateResponse create(TSubCreateRequest tSubCreateRequest) {
        TSubCreateResponse response = requester.request(newRequestId(), "/gcpapi/card/mermanage/create", tSubCreateRequest, TSubCreateResponse.class);
        return response;
    }

    // 查询通联子商户创建情况: 5001
    public TSubQueryResponse query(TSubQuery query) {
        TSubQueryResponse response = requester.request(newRequestId(), "/gcpapi/card/mermanage/detail", query, TSubQueryResponse.class);
        return response;
    }


}
