package io.renren.service;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import io.renren.commons.tools.exception.RenException;
import io.renren.zcommon.ByteUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SignTest {

    public static String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDTkwRkIGY+y0ne/XdX08qm5qgOGejboU+rZrnOxm8sydiTSEMo+eaNvpncevf1zpKAmvqcz/lHWLl2dhd4Ax6xY2A5ss6LAw9/BxX4OR7npjiFmGyKoPFyfMI2ccNhoKoENBUxZGGTaDiGer3JGSPxrBWKPlyLGpeBnbtmjYWq/st7M0oOEahMtw0+i1g2md3uPWvvv7EZa4zBUGl2f2AMsszMnhPOK8W1CYLm+Dh7O/4g/sHARSDVKFMvLfp8RjTywdyh6P6aE48106jg0SmDYf1G7Wixa1LI8NFkfL6QlZEvE6Dujb6I2Yfh9gxjsuR4ofOno1bKiBEwNU0Tdls7AgMBAAECggEARt432lH/7Yiqdcbw8ro+ZylboV2MYgTmasM9+cT9+EKkCLFrzY3nbBMr9m46IFvRNsKyr1P6a6uMc+GaetiQr1paIPmDtYyQ5Du7YCnB8FX9GMK1mLnQJkkjxJjWmRz31fCcKK82/+kpBeKXL4T8RTce/+9jkDSYCxKKcw90dpHFyxy+GrCJST3Viq+zZ9P8Wqu0u41JgfDVDKsbsSinYRq3E3vXYdCD8tEk/omvhGTG83SgadKIK6mQrUE0spt9iSumEKHxYRm3nsPVuH8wYKG4UdyjAMN+LVmwghLPLL68lg1oERLdm6CMs1FeRbnGOC0A4o7gNb4+ICOy9orAAQKBgQD5qpp59Jwv6lpIXk/2NU4zokKeIUlWaKxfsIXiHxTybBLfWonZDETwh0mVKHYz0oMXambt1F/vs9zG65+krNTGx73SM0OzR19rSwta09/FJ0nS4988XITPu07gZ+m+VjTbv1nvqWAn99kMrlex8iloWa1neJaTzyMPoHMHcPM+gQKBgQDY8QiopHvElccjkE+OgxNEP5Iibue9xzQV9SyTsyqzq8uuNoXsC1NcOvAqBB72UGpmzcgb9n8awqyd6QnVlErOBTe8G7TxuZJEGRwH75daSTl16ea/BVZ6RhPJGyLYrSeA8vepc4Lf6db2Hl61OsXM5CRlMRro/gZcQzxzmlQzuwKBgQDZB67lWPf0xxnYUvPbqRbj16dlYrYnwTImtIKNwEsrOTtmoYO3A+1h0ZjrapLmFZcTYdE76SPEcWv4F5ddRUhpy/R8p0ewrtiJomCqggfBkJeiFahXBm4FTmQQuwP8C2BDriF2LpyB4ffyCBP/gezw4xPUIIV009k8mlOkIlGxAQKBgATlU6OyqpRiKO9Vh2YY8SteH+clB5fR0gwYz1u59GG/o8YXtuf/zW7MabUZCLXYVL5jMHvpES9Ca3DY3H70bMe/eGWHXosB/BoLUwWe05SXCHPvxmRTM4No53NnaF7pcXXIhexODgsNlOtS/iIq5GHeWC/sbsYJTY1xewDE39eXAoGBAMrQkSOUrPtq6VYOAcpS1zKL+TfTpID9c+6Gkmc9bX4GlexLGEuSpIzcEZZcAHX6nllvASA/WuNqNHoDC+P6kOV2cXcbI3eVz2y9x9chKXtxz/giH88b8Hqh+xPKhMOiB2/YOiBf0LApV2Yx/gTQsuSu6zZIuhNYhP24TVLLgTwU";
    public static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA05MEZCBmPstJ3v13V9PKpuaoDhno26FPq2a5zsZvLMnYk0hDKPnmjb6Z3Hr39c6SgJr6nM/5R1i5dnYXeAMesWNgObLOiwMPfwcV+Dke56Y4hZhsiqDxcnzCNnHDYaCqBDQVMWRhk2g4hnq9yRkj8awVij5cixqXgZ27Zo2Fqv7LezNKDhGoTLcNPotYNpnd7j1r77+xGWuMwVBpdn9gDLLMzJ4TzivFtQmC5vg4ezv+IP7BwEUg1ShTLy36fEY08sHcoej+mhOPNdOo4NEpg2H9Ru1osWtSyPDRZHy+kJWRLxOg7o2+iNmH4fYMY7LkeKHzp6NWyogRMDVNE3ZbOwIDAQAB";

    public Sign getSigner() {
        RSA rsaSigner = new RSA(privateKey, null);
        Sign signer = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        signer.setPrivateKey(rsaSigner.getPrivateKey());
        return signer;
    }

    public Sign getVerifier() {
        Sign verifier = SecureUtil.sign(SignAlgorithm.SHA512withRSA);
        RSA rsa = new RSA(null, publicKey);
        verifier.setPublicKey(rsa.getPublicKey());
        return verifier;
    }

    // 签名
    public String sign(String body, String reqId, String id, String apiName) {
        String bodyDigest = DigestUtil.sha256Hex(body);
        String toSign = bodyDigest + reqId + id + apiName;
        System.out.println("toSign:" + toSign);
        byte[] bytes = DigestUtil.sha256(toSign);
        String sign = getSigner().signHex(bytes);
        return sign;
    }

    // 验证签名
    public void verify(String body, String reqId, String id,String apiName, String sign) {
        String bodyDigest = DigestUtil.sha256Hex(body);
        String toSign = bodyDigest + reqId + id + apiName;
        System.out.println("toVeri:" + toSign);
        byte[] bytes = DigestUtil.sha256(toSign);
        Sign merchantVerifier = getVerifier();
        if (!merchantVerifier.verify(bytes, ByteUtil.hextobyte(sign))) {
            throw new RenException("签名验证失败");
        }
        System.out.println("签名正确");
    }

    public static void main(String[] args) {
        System.out.println("-------------------------");
        String access_token = "CthjyX9iPrDIf2ds/qabsTSqfXmp5aNiIGT2UiSVtT1JONofp7+uVhz3JTGyjaNn";
        String product_token = "0145bc2c03344e70bfc1c17fb32ecd1d";
        String p16 = product_token.substring(0,16);
        String outKey = aesDecrypt(access_token, p16);
        System.out.println("access_token = " + access_token);
        System.out.println("product_16   = " + p16 + ", length = " + p16.length());
        System.out.println("decrypt      = " + outKey);
    }

    public static String VECTOR = "qe4vnvirwsm0gawz";
    public static String aesDecrypt(String plainStr,String key){
        if (StringUtils.isEmpty(plainStr)){
            return plainStr;
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("utf-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(VECTOR.getBytes("utf-8"));
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            Base64 base64 = new Base64();
            byte[] bytes = base64.decodeBase64(plainStr.getBytes());
            bytes = cipher.doFinal(bytes);
            return new String(bytes, "utf-8");
        }catch (Exception e){
            System.out.println("AES解密异常,解密字符串:" + plainStr);
        }
        return null;
    }
}
