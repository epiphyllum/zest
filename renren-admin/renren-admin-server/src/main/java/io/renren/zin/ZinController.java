package io.renren.zin;

import io.renren.zin.accountmanage.ZinAccountManageNotifyService;
import io.renren.zin.accountmanage.dto.TMoneyInNotify;
import io.renren.zin.cardapply.ZinCardApplyNotifyService;
import io.renren.zin.cardapply.dto.TCardApplyNotify;
import io.renren.zin.cardstate.ZinCardStateNotifyService;
import io.renren.zin.cardstate.dto.TCardChangeNotify;
import io.renren.zin.cardtxn.ZinCardTxnNotifyService;
import io.renren.zin.cardtxn.dto.TAuthTxnNotify;
import io.renren.zin.exchange.ZinExchangeNotifyService;
import io.renren.zin.exchange.dto.TExchangeStateNotify;
import io.renren.zin.sub.ZinSubNotifyService;
import io.renren.zin.sub.dto.TSubStatusNotify;
import io.renren.zin.umbrella.ZinUmbrellaNotifyService;
import io.renren.zin.umbrella.dto.TMoneyAccountNotify;
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
    private ZinCardStateNotifyService zinCardStateNotifyService;
    @Resource
    private ZinCardTxnNotifyService zinCardTxnNotifyService;
    @Resource
    private ZinExchangeNotifyService zinExchangeNotifyService;
    @Resource
    private ZinSubNotifyService zinSubNotifyService;
    @Resource
    private ZinAccountManageNotifyService zinAccountManageNotifyService;
    @Resource
    private ZinUmbrellaNotifyService zinUmbrellaNotifyService;
    @Resource
    private ZinRequester requester;

    // 银行账户状态通知
    @PostMapping("bnfauditrst")
    public String bnfauditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TMoneyAccountNotify notify = requester.<TMoneyAccountNotify>verify(request, body, auth, date, TMoneyAccountNotify.class);
        zinUmbrellaNotifyService.moneyAccountStatusNotify(notify);
        return "OK";
    }

    // 换汇申请单状态通知:  这个是支付类申请单的通知
    @PostMapping("applynotify")
    public String auditrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TExchangeStateNotify tExchangeStateNotify = requester.<TExchangeStateNotify>verify(request, body, auth, date, TExchangeStateNotify.class);
        zinExchangeNotifyService.handle(tExchangeStateNotify);
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
        zinCardStateNotifyService.handle(tCardChangeNotify);
        return "OK";
    }

    // 授权交易通知
    @PostMapping("cardtrxrst")
    public String cardtrxrst(HttpServletRequest request, @RequestBody String body, @RequestHeader("X-AGCP-Auth") String auth, @RequestHeader("X-AGCP-Date") String date) {
        TAuthTxnNotify tAuthTxnNotify = requester.<TAuthTxnNotify>verify(request, body, auth, date, TAuthTxnNotify.class);
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
        zinAccountManageNotifyService.handle(tMoneyInNotify);
        return "OK";
    }

    // 异常处理:
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleRuntimeException(Throwable ex) {
        ex.printStackTrace();
        return new ResponseEntity<>("Internal Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
