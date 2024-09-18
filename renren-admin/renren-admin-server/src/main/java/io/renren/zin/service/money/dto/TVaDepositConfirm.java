package io.renren.zin.service.money.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TVaDepositConfirm {
    String applyid; // 申请单号applyidString32Y
    BigDecimal amount; // 金额amountNumber18,2Y
    String transferfid; // 转账凭证transferfidString100Y多份材料打包zip上传，文件上传的fid
    String otherfid; // 其他材料otherfidString100Y多份材料打包zip上传，文件上传的fid
}
