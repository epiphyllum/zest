package io.renren.zadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.commons.tools.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * j_maccount
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@Data
@Schema(description = "j_maccount")
public class JMaccountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;
    private String merchantName;

    private String flag;
    private String currency;
    private String country;
    private String idtype;
    private String idno;
    private String cardno;
    private String cardname;
    private String tel;
    private String email;
    private String accountaddr;
    private String bankname;
    private String bankaddr;
    private String interbankmsg;
    private String swiftcode;
    private String depositcountry;
    private String biccode;
    private String branchcode;

    private Long creator;
    @Schema(description = "创建时间")
    private Date createDate;
    @Schema(description = "更新者")
    private Long updater;
    @Schema(description = "更新时间")
    private Date updateDate;

}