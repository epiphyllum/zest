package io.renren.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@Schema(description = "用户登录")
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "uuid")
    private String uuid;

    @Schema(description = "验证码")
    private String captcha;

    @Schema(description = "otp")
    private Integer otp;
}
