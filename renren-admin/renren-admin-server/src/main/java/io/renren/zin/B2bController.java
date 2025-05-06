package io.renren.zin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zcommon.AccessConfig;
import io.renren.zcommon.ZestConfig;
import io.renren.zin.b2b.B2bNotifyService;
import io.renren.zin.b2b.dto.TVaMoneyNotify;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 通联接入的webhook
 */
@RestController
@RequestMapping("b2b/webhook")
@Slf4j
public class B2bController {

    @Resource
    private B2bRequester requester;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private B2bNotifyService b2BNotifyService;

    /**
     * 大吉商户b2b收到钱
     * {
     * "acctno":"80000000370806",
     * "currency":"HKD",
     * "amount":10000,
     * "bid":"2025042204483482",
     * "currency":"HKD",
     * "nid":"8SR3J6FMTI",
     * "time":"2025-04-22T13:57:10Z",
     * "trxcod":"CP213"
     * }
     */
    @PostMapping("{merchantId}/acctbalauditrst")
    public String acctbalauditrst(HttpServletRequest request, @PathVariable("merchantId") Long merchantId, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        AccessConfig b2bConfig = null;
        if (merchantId.equals(0L)) {
            // 大吉的b2b收到钱
            b2bConfig = zestConfig.getB2bConfig();
            TVaMoneyNotify notifyDto = requester.verify(b2bConfig, request, body, auth, date, TVaMoneyNotify.class);
            b2BNotifyService.myB2bNotified(notifyDto);
        } else {
            // 商户收到钱
            JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
            try {
                b2bConfig = objectMapper.readValue(merchant.getB2bva(), AccessConfig.class);
            } catch (JsonProcessingException e) {
                log.error("商户配置错误: {}", merchant.getB2bConfig());
                throw new RenException("商户配置错误");
            }
            // 商户接入配置
            merchant.setB2bConfig(b2bConfig);

            // 验证请求
            TVaMoneyNotify notifyDto = requester.verify(b2bConfig, request, body, auth, date, TVaMoneyNotify.class);
            notifyDto.setMerchantId(merchantId);

            // 处理收到的通知
            b2BNotifyService.merchantB2bNotified(merchant, notifyDto);
        }
        return "OK";
    }

    // 申请单通知
    @PostMapping("{merchantId}/applynotify")
    public String applynotify(HttpServletRequest request, @PathVariable("merchantId") Long merchantId, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        return "OK";
    }

    //
    @PostMapping("{merchantId}/cusauditrst")
    public String cusauditrst(HttpServletRequest request, @PathVariable("merchantId") Long merchantId, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        return "OK";
    }

    // 异常处理:
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleRuntimeException(Throwable ex) {
        ex.printStackTrace();
        return new ResponseEntity<>("Internal Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
