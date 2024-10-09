package io.renren.zin;

import io.renren.zin.config.ZinRequester;
import io.renren.zin.service.accountmanage.ZinAccountManageNotifyService;
import io.renren.zin.service.accountmanage.dto.TMoneyInNotify;
import io.renren.zin.service.cardapply.ZinCardApplyNotifyService;
import io.renren.zin.service.cardapply.dto.TCardApplyNotify;
import io.renren.zin.service.cardstatus.ZinCardStatusNotifyService;
import io.renren.zin.service.cardstatus.dto.TCardChangeNotify;
import io.renren.zin.service.cardtxn.ZinCardTxnNotifyService;
import io.renren.zin.service.cardtxn.dto.TAuthTxnNotify;
import io.renren.zin.service.exchange.ZinExchangeNotifyService;
import io.renren.zin.service.exchange.dto.TExchangeStateNotify;
import io.renren.zin.service.sub.ZinSubNotifyService;
import io.renren.zin.service.sub.dto.TSubStatusNotify;
import io.renren.zin.service.umbrella.ZinUmbrellaService;
import io.renren.zin.service.umbrella.dto.TMoneyAccountNotify;
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
    private ZinCardApplyNotifyService zinCardApplyNotifyService;
    @Resource
    private ZinCardStatusNotifyService zinCardStatusNotifyService;
    @Resource
    private ZinCardTxnNotifyService zinCardTxnNotifyService;
    @Resource
    private ZinExchangeNotifyService zinExchangeNotifyService;
    @Resource
    private ZinSubNotifyService zinSubNotifyService;
    @Resource
    private ZinAccountManageNotifyService zinAccountManageNotifyService;
    @Resource
    private ZinUmbrellaService zinUmbrellaService;
    @Resource
    private ZinRequester requester;

    // 银行账户状态通知
    @PostMapping("bnfauditrst")
    public String bnfauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TMoneyAccountNotify notify = requester.<TMoneyAccountNotify>verify(request, body, auth, date, TMoneyAccountNotify.class);
        zinUmbrellaService.moneyAccountStatusNotify(notify);
        return "OK";
    }

    // 换汇申请单状态通知:  这个是支付类申请单的通知
    @PostMapping("applynotify")
    public String auditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TExchangeStateNotify tExchangeStateNotify = requester.<TExchangeStateNotify>verify(request, body, auth, date, TExchangeStateNotify.class);
        zinExchangeNotifyService.exchangeStateNotify(tExchangeStateNotify);
        return "OK";
    }

    // 卡申请状态通知
    @PostMapping("vpaauditrst")
    public String vpaauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TCardApplyNotify tCardApplyNotify = requester.<TCardApplyNotify>verify(request, body, auth, date, TCardApplyNotify.class);
        zinCardApplyNotifyService.cardApplyNotify(tCardApplyNotify);
        return "OK";
    }

    // 卡状态变更通知
    @PostMapping("cardchangenotify")
    public String cardchangenotify(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TCardChangeNotify tCardChangeNotify = requester.<TCardChangeNotify>verify(request, body, auth, date, TCardChangeNotify.class);
        zinCardStatusNotifyService.cardStatusChangeNotify(tCardChangeNotify);
        return "OK";
    }

    // 授权交易通知
    @PostMapping("cardtrxrst")
    public String cardtrxrst(HttpServletRequest request, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TAuthTxnNotify tAuthTxnNotify = requester.<TAuthTxnNotify>verify(request, null, auth, date, TAuthTxnNotify.class);
        zinCardTxnNotifyService.handle(tAuthTxnNotify);
        return "OK";
    }

    // 子商户审核通知
    @PostMapping("cusauditrst")
    public String cusauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TSubStatusNotify tSubStatusNotify = requester.verify(request, body, auth, date, TSubStatusNotify.class);
        zinSubNotifyService.handle(tSubStatusNotify);
        return "OK";
    }

    // 入账通知
    @PostMapping("acctbalauditrst")
    public String acctbalauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TMoneyInNotify tMoneyInNotify = requester.verify(request, body, auth, date, TMoneyInNotify.class);
        zinAccountManageNotifyService.moneyInNotify(tMoneyInNotify);
        return "OK";
    }

    // 异常处理:
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleRuntimeException(Throwable ex) {
        ex.printStackTrace();
        return new ResponseEntity<>("Internal Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
