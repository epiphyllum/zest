package io.renren.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户Token
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@AllArgsConstructor
@Schema(description = "用户Token")
public class UserTokenDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "access_token")
    @JsonProperty(value = "access_token")
    private String accessToken;

    @Schema(description = "refresh_token")
    @JsonProperty(value = "refresh_token")
    private String refreshToken;
}
