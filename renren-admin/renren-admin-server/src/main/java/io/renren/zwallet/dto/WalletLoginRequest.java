package io.renren.zwallet.dto;

import lombok.Data;

@Data
public class WalletLoginRequest {
    private String email;
    private String password;
    private String otp;
}
