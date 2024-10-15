package io.renren.zcommon;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * AES
 */
public class AESUtil {

    private AESUtil() {}

    // mode
    public static final String ECB_PKCS5 = "AES/ECB/PKCS5Padding"; // DEFAULT
    // KeySize
    public static final Integer KEY_128 = 128; // default,不够长时自动补足
    public static final Integer KEY_192 = 192;
    public static final Integer KEY_256 = 256;

    /**
     * data为String,输出为Hex扩展
     */
    public static String encrypt(String data, String key, boolean isHexKey, String mode, String charset)
            throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] keyB = null;
        if (isHexKey) {
            keyB = ByteUtil.hextobyte(key);
        } else {
            keyB = key.getBytes(charset);
        }

        byte[] r = encrypt(data.getBytes(charset), keyB, mode);
        return Base64.getEncoder().encodeToString(r);
    }

    /**
     * data为Hex格式
     */
    public static String decrypt(String data, String key, boolean isHexKey, String mode, String charset)
            throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] keyB = null;
        if (isHexKey) {
            keyB = ByteUtil.hextobyte(key);
        } else {
            keyB = key.getBytes(charset);
        }

        byte[] dataB = Base64.getDecoder().decode(data);
        return new String(decrypt(dataB, keyB, mode), charset);
    }

    public static byte[] encrypt(byte[] data, byte[] key, String mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(mode);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(key));
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] key, String mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(mode);
        cipher.init(Cipher.DECRYPT_MODE, getKey(key));
        return cipher.doFinal(data);
    }

    private static SecretKeySpec getKey(byte[] key) {
        int keySize = key.length * 8;
        if (keySize != KEY_128 && keySize != KEY_192 && keySize != KEY_256) {
            throw new IllegalArgumentException("密钥长度错误");
        }
        return new SecretKeySpec(key, "AES");
    }
}
