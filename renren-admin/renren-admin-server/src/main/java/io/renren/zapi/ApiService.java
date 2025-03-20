package io.renren.zapi;


import ch.qos.logback.classic.Logger;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JPacketDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JPacketEntity;
import io.renren.zapi.account.ApiAccountService;
import io.renren.zapi.allocate.ApiAllocateService;
import io.renren.zapi.cardapply.ApiCardApplyService;
import io.renren.zapi.cardmoney.ApiCardMoneyService;
import io.renren.zapi.cardstate.ApiCardStateService;
import io.renren.zapi.exchange.ApiExchangeService;
import io.renren.zapi.file.ApiFileService;
import io.renren.zapi.sub.ApiSubService;
import io.renren.zapi.vpa.ApiVpaService;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ApiService {

    @Data
    @AllArgsConstructor
    public static class ApiMeta {
        Object instance;
        Method method;
        Class<?> reqClass;
    }

    @Resource
    private ApiLogger apiLogger;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JMerchantDao jMerchantDao;

    // 服务模块
    @Resource
    private ApiSubService apiSubService;
    @Resource
    private ApiAccountService apiAccountService;
    @Resource
    private ApiAllocateService apiAllocateService;
    @Resource
    private ApiExchangeService apiExchangeService;
    @Resource
    private ApiCardMoneyService apiCardMoneyService;
    @Resource
    private ApiCardStateService apiCardStateService;
    @Resource
    private ApiCardApplyService apiCardApplyService;
    @Resource
    private ApiVpaService apiVpaService;

    public static Map<String, ApiMeta> metaMap = new HashMap<>();

    public void initService(Object object) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            // 获取返回类型
            Class<?> returnType = method.getReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (returnType == Result.class && parameterTypes.length == 2 && parameterTypes[1].equals(ApiContext.class)) {
                metaMap.put(method.getName(), new ApiMeta(object, method, parameterTypes[0]));
            }
        }
    }

    /**
     * 初始化服务
     */
    @PostConstruct
    public void init() {
        initService(apiSubService);
        initService(apiAccountService);
        initService(apiCardMoneyService);
        initService(apiExchangeService);
        initService(apiAllocateService);
        initService(apiCardApplyService);
        initService(apiCardMoneyService);
        initService(apiCardStateService);
        initService(apiVpaService);
    }

    /**
     * 商户签名验证器
     *
     * @param merchant
     * @return
     */
    public Sign getMerchantVerifier(JMerchantEntity merchant) {
        Sign verifier = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        RSA rsa = new RSA(null, merchant.getPublicKey());
        verifier.setPublicKey(rsa.getPublicKey());
        return verifier;
    }

    // 验证签名
    public void verify(String body, String reqId, JMerchantEntity merchant, String name, String sign) {
        String bodyDigest = DigestUtil.sha256Hex(body);
        String toSign = bodyDigest + reqId + merchant.getId() + name;
        Sign merchantVerifier = getMerchantVerifier(merchant);
        byte[] bytes = DigestUtil.sha256(toSign);
        if (!merchantVerifier.verify(bytes, sign.getBytes())) {
            log.error("验证签名失败, 代签名串[{}]\nsign=[{}]\n,key=[{}]reqId={}", toSign, sign, merchantVerifier.getPublicKey(), reqId);
            throw new RenException("签名验证失败");
        }
    }

    /**
     * 调用服务
     *
     * @param body
     * @param merchantId
     * @param sign
     * @param reqId
     * @param name
     * @return
     */
    public Result<?> invokeService(String body, Long merchantId, String sign, String reqId, String name) {
        String ip = CommonUtils.getIp();
        ApiMeta apiMeta = metaMap.get(name);
        if (apiMeta == null) {
            throw new RenException("接口名称错误:" + name);
        }

        // 查询商户信息
        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
        if (merchant == null) {
            throw new RenException("非法商户号:" + merchantId);
        }

        // 开发环境不验证签名
        if (merchant.getDebug().equals(1)) {
            log.debug("开发环境, 不校验签名: 不检查IP");
        } else {
            if (merchant.getWhiteIp() == null) {
                log.error("{}尚未配置IP白名单", merchant.getCusname());
                throw new RenException("商户尚未配置IP白名单");
            }
            if (merchant.getWhiteIp().indexOf(ip) == -1) {
                log.error("{}不允许从{}访问", merchant.getCusname(), ip);
                throw new RenException("forbidden ip: " + ip);
            }
            verify(body, reqId, merchant, name, sign);
        }

        // 记录日志准备
        JPacketEntity packetEntity = new JPacketEntity();
        packetEntity.setMerchantId(merchantId);
        packetEntity.setMerchantName(merchant.getCusname());
        packetEntity.setAgentId(merchant.getAgentId());
        packetEntity.setAgentName(merchant.getAgentName());
        packetEntity.setApiName(name);
        packetEntity.setReqId(reqId);
        packetEntity.setRecv(body);
        packetEntity.setIp(ip);
        packetEntity.setSign(sign);

        try {
            Object req = objectMapper.readValue(body, apiMeta.getReqClass());
            Logger logger = CommonUtils.getLogger(merchant.getCusname());
            ApiContext context = new ApiContext(merchant, logger);
            Result<?> result = (Result) apiMeta.getMethod().invoke(apiMeta.getInstance(), req, context);
            apiLogger.logPacketSuccess(packetEntity, result);
            return result;
        } catch (JsonProcessingException e) {
            apiLogger.logPacketException(packetEntity, e);
            e.printStackTrace();
            throw new RenException("数据格式错误");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RenException) {
                RenException ex = (RenException) cause;
                apiLogger.logPacketException(packetEntity, ex);
                throw ex;
            } else {
                e.printStackTrace();
                throw new RenException("未知异常");
            }
        } catch (IllegalAccessException e) {
            apiLogger.logPacketException(packetEntity, e);
            e.printStackTrace();
            throw new RenException("接口异常");
        }
    }
}

