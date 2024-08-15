package io.renren.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * 产品参数管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@Schema(description = "产品参数管理")
public class ProductParamsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "参数名")
    private String paramName;

    @Schema(description = "参数值")
    private String paramValue;
}