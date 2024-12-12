package io.renren.zwallet.access;

import cn.hutool.core.lang.Pair;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JWalletDao;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zadmin.service.JAuthService;
import io.renren.zadmin.service.JAuthedService;
import io.renren.zadmin.service.JWalletTxnService;
import io.renren.zcommon.ZestConfig;
import io.renren.zwallet.dto.*;
import io.renren.zwallet.manager.JWalletCardManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 钱包接入API
@RestController
@RequestMapping("zwallet/access")
@Slf4j
public class AccessController {

    @Resource
    private JWalletCardManager jWalletCardManager;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JWalletDao jWalletDao;
    @Resource
    private JAuthedService jAuthedService;
    @Resource
    private JAuthService jAuthService;
    @Resource
    private JWalletTxnService jWalletTxnService;

    @Resource
    private ZestConfig zestConfig;

    private <T> Pair<T, JWalletEntity> validateRequest(String sign, Long walletId, String body, Class<T> clazz) throws JsonProcessingException {
        JWalletEntity walletEntity = jWalletDao.selectById(walletId);

        if (zestConfig.isDev()) {
            log.info("测试环境");
            if (!sign.equals("dev")) {
                throw new RenException("签名错误");
            }
        } else {
            String calcSign = DigestUtil.md5Hex(body + walletId + walletEntity.getAccessKey());
            if (!calcSign.equals(sign)) {
                throw new RenException("签名错误");
            }
        }
        if (clazz.equals(void.class)) {
            return Pair.of(null, walletEntity);
        }
        T request = objectMapper.readValue(body, clazz);
        return Pair.of(request, walletEntity);
    }

    /**
     * 匿名卡-开卡
     */
    @PostMapping("openVpa")
    public Result<Long> openVpa(
            @RequestHeader("x-sign") String sign,
            @RequestHeader("x-wallet-id") Long walletId,
            @RequestBody String body) throws JsonProcessingException {
        Pair<WalletCardOpenRequest, JWalletEntity> pair = this.validateRequest(sign, walletId, body, WalletCardOpenRequest.class);
        Long jobId = jWalletCardManager.openVpa(pair.getKey(), pair.getValue());
        Result<Long> result = new Result<>();
        result.setData(jobId);
        return result;
    }

    /**
     * 实名卡-开卡
     */
    @PostMapping("openVcc")
    public Result<Long> openVcc(
            @RequestHeader("x-sign") String sign,
            @RequestHeader("x-wallet-id") Long walletId,
            @RequestBody String body) throws JsonProcessingException {
        Pair<JCardEntity, JWalletEntity> pair = this.validateRequest(sign, walletId, body, JCardEntity.class);
        Long jobId = jWalletCardManager.openVcc(pair.getKey(), pair.getValue());
        Result<Long> result = new Result<>();
        result.setData(jobId);
        return result;
    }

    /**
     * 开卡查询: 匿名卡
     */
    @PostMapping("openVpaQuery")
    public Result openVpaQuery(
            @RequestHeader("x-sign") String sign,
            @RequestHeader("x-wallet-id") Long walletId,
            @RequestBody String body
    ) throws JsonProcessingException {
        Pair<WalletCardOpenQuery, JWalletEntity> pair = this.validateRequest(sign, walletId, body, WalletCardOpenQuery.class);
        jWalletCardManager.openVpaQuery(pair.getKey().getId(), pair.getValue());
        return new Result();
    }

    /**
     * 开卡查询: 实名卡 + 实体卡
     */
    @PostMapping("openVccQuery")
    public Result openVccQuery(
            @RequestHeader("x-sign") String sign,
            @RequestHeader("x-wallet-id") Long walletId,
            @RequestBody String body
    ) throws JsonProcessingException {
        Pair<WalletCardOpenQuery, JWalletEntity> pair = this.validateRequest(sign, walletId, body, WalletCardOpenQuery.class);
        jWalletCardManager.openVccQuery(pair.getKey().getId(), pair.getValue());
        return new Result();
    }

    /**
     * 发起卡充值
     */
    @PostMapping("charge")
    public Result charge(@RequestBody String body,
                         @RequestHeader("x-sign") String sign,
                         @RequestHeader("x-wallet-id") Long walletId) throws JsonProcessingException {
        Pair<WalletCardChargeRequest, JWalletEntity> pair = this.validateRequest(sign, walletId, body, WalletCardChargeRequest.class);
        jWalletCardManager.chargeCard(pair.getKey(), pair.getValue());
        Result<String> result = new Result();
        return result;
    }

    // 钱包交易(钱包充值、钱包提现)列表
    @GetMapping("page/wallet")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    public Result<PageData<WalletTxnItem>> pageWallet(
            @Parameter(hidden = true) @RequestParam Map<String, Object> params,
            @RequestHeader("x-sign") String sign,
            @RequestHeader("x-wallet-id") Long walletId
    ) throws JsonProcessingException {
        this.validateRequest(sign, walletId, "", void.class);
        PageData<WalletTxnItem> walletTxnItemPageData = jWalletTxnService.walletPage(params, walletId);
        Result<PageData<WalletTxnItem>> result = new Result<>();
        result.setData(walletTxnItemPageData);
        return result;
    }

    // 卡授权交易列表
    @GetMapping("page/card")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    public Result<PageData<WalletCardTxnItem>> pageAuth(
            @RequestHeader("x-sign") String sign,
            @RequestHeader("x-wallet-id") Long walletId,
            @Parameter(hidden = true)
            @RequestParam Map<String, Object> params
    ) throws JsonProcessingException {
        this.validateRequest(sign, walletId, "", void.class);
        PageData<WalletCardTxnItem> walletCardTxnItemPageData = jAuthService.walletPage(params, walletId);
        Result<PageData<WalletCardTxnItem>> result = new Result<>();
        result.setData(walletCardTxnItemPageData);
        return result;
    }

    // 卡结算交易列表
    @GetMapping("page/authed")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    public Result<PageData<WalletCardTxnItem>> pageAuthed(
            @RequestHeader("x-sign") String sign,
            @RequestHeader("x-wallet-id") Long walletId,
            @Parameter(hidden = true)
            @RequestParam Map<String, Object> params
    ) throws JsonProcessingException {
        this.validateRequest(sign, walletId, "", void.class);
        PageData<WalletCardTxnItem> walletCardTxnItemPageData = jAuthedService.walletPage(params, walletId);
        Result<PageData<WalletCardTxnItem>> result = new Result<>();
        result.setData(walletCardTxnItemPageData);
        return result;
    }
}
