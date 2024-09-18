package io.renren.zin;

import io.renren.commons.tools.exception.RenException;
import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.auth.dto.TAuthTxnNotify;
import io.renren.zin.service.card.ZinCardService;
import io.renren.zin.service.card.dto.TCardApplyNotify;
import io.renren.zin.service.cardstatus.dto.TCardChangeNotify;
import io.renren.zin.service.cardstatus.ZinCardStatusService;
import io.renren.zin.service.exchange.ZinExchangeService;
import io.renren.zin.service.exchange.dto.TExchangeStateNotify;
import io.renren.zin.service.money.ZinMoneyService;
import io.renren.zin.service.money.dto.TMoneyAccountNotify;
import io.renren.zin.service.sub.ZinSubService;
import io.renren.zin.service.va.ZinVaService;
import io.renren.zin.service.va.dto.TMoneyInNotify;
import io.renren.zin.service.sub.dto.TSubStatusNotify;
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
@RequestMapping("zin/webhook")
@Slf4j
public class ZinController {
    @Resource
    private ZinCardService zinCardService;
    @Resource
    private ZinCardStatusService zinCardStatusService;
    @Resource
    private ZinExchangeService zinExchangeService;
    @Resource
    private ZinSubService zinSubService;
    @Resource
    private ZinVaService zinVaService;
    @Resource
    private ZinMoneyService zinMoneyService;
    @Resource
    private ZinRequester requester;

    // 换汇申请单状态通知
    @PostMapping("applynotify")
    public String auditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TExchangeStateNotify tExchangeStateNotify = requester.<TExchangeStateNotify>verify(request, body, auth, date, TExchangeStateNotify.class);
        zinExchangeService.exchangeStateNotify(tExchangeStateNotify);
        return "OK";
    }

    // 卡申请状态通知
    @PostMapping("vpaauditrst")
    public String vpaauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TCardApplyNotify tCardApplyNotify = requester.<TCardApplyNotify>verify(request, body, auth, date, TCardApplyNotify.class);
        zinCardService.cardApplyNotify(tCardApplyNotify);
        return "OK";
    }

    // 卡状态变更通知
    @PostMapping("cardchangenotify")
    public String cardchangenotify(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TCardChangeNotify tCardChangeNotify = requester.<TCardChangeNotify>verify(request, body, auth, date, TCardChangeNotify.class);
        zinCardStatusService.cardStatusChangeNotify(tCardChangeNotify);
        return "OK";
    }

    // 授权交易通知
    @PostMapping("cardtrxrst")
    public String cardtrxrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TAuthTxnNotify tAuthTxnNotify = requester.<TAuthTxnNotify>verify(request, body, auth, date, TAuthTxnNotify.class);
        log.info("授权交易通知:{}", body);
        return "OK";
    }

    // 子商户审核通知
    @PostMapping("cusauditrst")
    public String cusauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TSubStatusNotify tSubStatusNotify = requester.verify(request, body, auth, date, TSubStatusNotify.class);
        zinSubService.merchantStatusNotify(tSubStatusNotify);
        return "OK";
    }

    // 入账通知
    @PostMapping("acctbalauditrst")
    public String acctbalauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TMoneyInNotify tMoneyInNotify = requester.verify(request, body, auth, date, TMoneyInNotify.class);
        try {
            zinVaService.moneyInNotify(tMoneyInNotify);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RenException("process failed");
        }
        return "OK";
    }

    //
    @PostMapping("bnfauditrst")
    public String bnfauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TMoneyAccountNotify notify = requester.verify(request, body, auth, date, TMoneyAccountNotify.class);
        try {
            zinMoneyService.moneyAccountStatusHandle(notify);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RenException("process failed");
        }
        return "OK";
    }

    // 异常处理:
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleRuntimeException(Throwable ex) {
        return new ResponseEntity<>("Internal Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
