package io.renren.zin.service.internal.dto;

import io.renren.zin.service.TResult;
import lombok.Data;

import java.util.List;

@Data
public class TBalanceResponse extends TResult {
   @Data
   public static class Item {
      private String		acctno	;//	22	账号	通联内部虚拟账号和冻结账号。
      private String		currency	;//	3	币种
      private Number		balance	;//	18,2	余额
      private Number		type	;//	1	类型	0-虚拟户 1-冻结户
   }
   private List<Item> details;
}
