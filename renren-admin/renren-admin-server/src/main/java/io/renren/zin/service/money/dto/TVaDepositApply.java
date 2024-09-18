package io.renren.zin.service.money.dto;

import lombok.Data;



@Data
public class TVaDepositApply {

//    String applyid;  // String32Y
//    BigDecimal amount; // Number18,2Y
//    String transferfid; //String100Y多份材料打包zip上传，文件上传的fid
//    String otherfid;  // String100Y多份材料打包zip上传，文件上传的fid

    String meraplid; // 申请单流水meraplidString32Y 客户自己生成，保持唯一
    String currency; // 币种currencyString3Y收款户币种
    String id;       // 银行账户idString30Y付款银行账户id，新增银行账户成功后响应
}
