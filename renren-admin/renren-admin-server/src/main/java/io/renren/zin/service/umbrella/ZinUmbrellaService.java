package io.renren.zin.service.umbrella;

import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.umbrella.dto.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static io.renren.zin.config.CommonUtils.newRequestId;

@Service
public class ZinUmbrellaService {

    @Resource
    ZinRequester requester;

    /**
     * 8000-入金申请
     * 接口：/gcpapi/card/vadepositapply 方向：合作方->通联
     * 说明：伞形账户入金申请。
     */
    public TVaDepositApplyResponse depositApply(TVaDepositApply apply) {
        String requestId = newRequestId();
        TVaDepositApplyResponse response = requester.request(requestId, "/gcpapi/card/vadepositapply", apply, TVaDepositApplyResponse.class);
        return response;
    }

    /**
     * 8001-入金申请确认
     * 接口：/gcpapi/card/vadepositconfirm 方向：合作方->通联
     * 说明：伞形账户入金申请。
     */
    public TVaDepositConfirmResponse depositConfirm(TVaDepositConfirm confirm) {
        String requestId = newRequestId();
        TVaDepositConfirmResponse response = requester.request(requestId, "/gcpapi/card/vadepositconfirm", confirm, TVaDepositConfirmResponse.class);
        return response;
    }

    /**
     * 8002-补充材料
     * 接口：/gcpapi/card/submitmaterial 方向：合作方->通联
     * 说明：伞形账户入金申请被退回，重新补充材料。
     */
    public TSubmitMaterialResponse submitMaterial(TSubmitMaterial submit) {
        String requestId = newRequestId();
        TSubmitMaterialResponse response = requester.request(requestId, "/gcpapi/card/submitmaterial", submit, TSubmitMaterialResponse.class);
        return response;
    }

    /**
     * 8003-银行账户新增
     * 接口：/gcpapi/card/addcardinfo 方向：合作方->通联
     * 说明：根据卡号更新持卡人信息。
     * 请求报文
     */
    public TMoneyAccountAddResponse addMoneyAccount(TMoneyAccountAdd add) {
        String requestId = newRequestId();
        TMoneyAccountAddResponse response = requester.request(requestId, "/gcpapi/card/addcardinfo", add, TMoneyAccountAddResponse.class);
        return response;
    }

    /**
     * 8004-银行账户修改
     * 接口：/gcpapi/card/uptcardinfo 方向：合作方->通联
     * 说明：根据银行账户ID更新持卡人信息。
     */
    public TMoneyAccountUpdateResponse updateMoneyAccount(TMoneyAccountUpdate update) {
        String requestId = newRequestId();
        TMoneyAccountUpdateResponse response = requester.request(requestId, "/gcpapi/card/uptcardinfo", update, TMoneyAccountUpdateResponse.class);
        return response;
    }


    /**
     * 8005-银行账户查询
     * 接口：/gcpapi/card/qrycardinfo 方向：合作方->通联
     * 说明：根据卡号查看持卡人信息。
     */
    public TMoneyAccountQueryResponse queryMoneyAccount(TMoneyAccountQuery query) {
        String requestId = newRequestId();
        TMoneyAccountQueryResponse response = requester.request(requestId, "/gcpapi/card/qrycardinfo", query, TMoneyAccountQueryResponse.class);
        return response;
    }



    /**
     * 1001-VA入金信息查询
     */
    public TVaAccountDetailResponse accountDetail(TVaAccountDetailRequest request) {
        String requestId = newRequestId();
        TVaAccountDetailResponse response = requester.request(requestId, "/gcpapi/va/accountdetail", request, TVaAccountDetailResponse.class);
        return response;
    }

}
