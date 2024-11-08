package io.renren.zin.umbrella;

import io.renren.zcommon.ZestConfig;
import io.renren.zin.ZinRequester;
import io.renren.zin.umbrella.dto.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static io.renren.zcommon.CommonUtils.uniqueId;

@Service
@Slf4j
public class ZinUmbrellaService {
    @Resource
    private ZinRequester requester;


    /**
     * 8000-入金申请
     * 接口：/gcpapi/card/vadepositapply 方向：合作方->通联
     * 说明：伞形账户入金申请。
     */
    public TMoneyApplyResponse depositApply(TMoneyApply apply) {
        String requestId = uniqueId();
        TMoneyApplyResponse response = requester.request(requestId, "/gcpapi/card/vadepositapply", apply, TMoneyApplyResponse.class);
        return response;
    }

    /**
     * 8001-入金申请确认
     * 接口：/gcpapi/card/vadepositconfirm 方向：合作方->通联
     * 说明：伞形账户入金申请。
     */
    public TMoneyConfirmResponse depositConfirm(TMoneyConfirm confirm) {
        String requestId = uniqueId();
        TMoneyConfirmResponse response = requester.request(requestId, "/gcpapi/card/vadepositconfirm", confirm, TMoneyConfirmResponse.class);


        return response;
    }

    /**
     * 8002-补充材料
     * 接口：/gcpapi/card/submitmaterial 方向：合作方->通联
     * 说明：伞形账户入金申请被退回，重新补充材料。
     */
    public TMaterialSubmitResponse submitMaterial(TMaterialSubmit submit) {
        String requestId = uniqueId();
        TMaterialSubmitResponse response = requester.request(requestId, "/gcpapi/card/submitmaterial", submit, TMaterialSubmitResponse.class);
        return response;
    }

    /**
     * 8003-银行账户新增
     * 接口：/gcpapi/card/addcardinfo 方向：合作方->通联
     * 说明：根据卡号更新持卡人信息。
     * 请求报文
     */
    public TMoneyAccountAddResponse addMoneyAccount(TMoneyAccountAdd add) {
        String requestId = uniqueId();
        TMoneyAccountAddResponse response = requester.request(requestId, "/gcpapi/card/addcardinfo", add, TMoneyAccountAddResponse.class);
        return response;
    }

    /**
     * 8004-银行账户修改
     * 接口：/gcpapi/card/uptcardinfo 方向：合作方->通联
     * 说明：根据银行账户ID更新持卡人信息。
     */
    public TMoneyAccountUpdateResponse updateMoneyAccount(TMoneyAccountUpdate update) {
        String requestId = uniqueId();
        TMoneyAccountUpdateResponse response = requester.request(requestId, "/gcpapi/card/uptcardinfo", update, TMoneyAccountUpdateResponse.class);
        return response;
    }


    /**
     * 8005-银行账户查询
     * 接口：/gcpapi/card/qrycardinfo 方向：合作方->通联
     * 说明：根据卡号查看持卡人信息。
     */
    public TMoneyAccountQueryResponse queryMoneyAccount(TMoneyAccountQuery query) {
        String requestId = uniqueId();
        TMoneyAccountQueryResponse response = requester.request(requestId, "/gcpapi/card/qrycardinfo", query, TMoneyAccountQueryResponse.class);
        return response;
    }



    /**
     * 1001-VA入金信息查询
     */
    public TMoneyAccountDetailResponse accountDetail(TMoneyAccountDetailRequest request) {
        String requestId = uniqueId();
        TMoneyAccountDetailResponse response = requester.request(requestId, "/gcpapi/va/accountdetail", request, TMoneyAccountDetailResponse.class);
        return response;
    }

}
