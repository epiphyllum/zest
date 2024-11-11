package io.renren.zadmin.controller;

import io.renren.zin.cardtxn.ZinCardTxnService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("zbatch")
@Slf4j
public class BatchController {

    @Resource
    private ZinCardTxnService zinCardTxnService;

    // 入账流水同步
    @GetMapping("settledAuthDownload")
    public String settledAuthDownload(@RequestParam("date") String date) {
        zinCardTxnService.syncSettledAuth(date);
        return "OK";
    }
}
