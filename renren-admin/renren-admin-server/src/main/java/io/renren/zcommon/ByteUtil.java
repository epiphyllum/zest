package io.renren.zcommon;

import java.io.UnsupportedEncodingException;

public class ByteUtil {
    /**
     * 功能：转换成十六进制字符串
     * 字节数组转16进制字符串
     * b：要转16进制字符串的字节数组
     */
    public static String byte2hex(byte[] b) {
        return byte2hex(b, 0, b.length);
    }

    private ByteUtil() {
        //nothing to do
    }

    public static String byte2hex(byte[] b, int offset, int length) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < length; n++) {
            stmp = (Integer.toHexString(b[n + offset] & 0XFF));
            if (stmp.length() == 1) hs.append("0").append(stmp);
            else hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    /**
     * 转换hex字符串到二进数据
     * str: 要转换为二进制的hex字符串
     * 返回：对应二进制数据
     */
    public static byte[] hextobyte(String str) {
        str = str.toUpperCase();
        int length = (str.length() + 1) / 2;
        byte[] b = new byte[length];
        if (str.length() % 2 == 1) {//字符串长度为奇数,后补零
            str = str + "0";
        }
        for (int i = 0; i < str.length() / 2; i++) {
            int j = i * 2;
            byte x = str.substring(j, j + 1).getBytes()[0];
            byte y = str.substring(j + 1, j + 2).getBytes()[0];
            if (x > 57) {
                b[i] = (byte) (x - 'A' + 10);
            } else {
                b[i] = (byte) (x - '0');
            }
            b[i] = (byte) ((int) b[i] << 4);
            byte tmp;
            if (y > 57) {
                tmp = (byte) (y - 'A' + 10);
            } else {
                tmp = (byte) (y - '0');
            }
            b[i] += tmp;
        }

        return b;
    }


    /**
     * 连接两个byte数组
     */
    public static byte[] connectBytes(byte[] first, byte[] second) {
        byte[] b = new byte[first.length + second.length];
        System.arraycopy(first, 0, b, 0, first.length);
        System.arraycopy(second, 0, b, first.length, second.length);
        return b;
    }

    /**
     * 获取字节数组中的一部分  s为字节数组   start从哪个位置开始，位置从0开始计算    length截取多长的数据
     */
    public static byte[] getSubBytes(byte[] s, int start, int length) {
        byte[] b = null;
        if (start <= s.length && start + length <= s.length && length > 0) {
            b = new byte[length];
            System.arraycopy(s, start, b, 0, length);
        }
        return b;
    }


    /**
     * 获取字节数组中的从start到字节数组的尾部的数据
     * s为字节数组   start从哪个位置开始
     */
    public static byte[] getTailBytes(byte[] s, int start) {
        byte[] b = null;
        int length = s.length;
        if (start <= length && length > 0) {
            b = new byte[length - start];
            System.arraycopy(s, start, b, 0, length - start);
        }
        return b;
    }

    /**
     * 两个字节数组的异或操作 要求两个字节数组的长度相同，若不相同，s要大于f的长度
     */
    public static byte[] getXorBytes(byte[] f, byte[] s) {
        byte[] b = new byte[f.length];
        for (int i = 0; i < f.length; i++) {
            b[i] = (byte) (f[i] ^ s[i]);
        }
        return b;
    }

    public static String readByte2Str(byte[] inbt, int start, int len, String enc) throws UnsupportedEncodingException {
        byte[] by = getSubBytes(inbt, start, len);
        return new String(by, enc);
    }
}
