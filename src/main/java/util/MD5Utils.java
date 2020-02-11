package util;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author super lollipop
 * @date 20-2-11
 */
public class MD5Utils {
    /**获取字符串的MD5值
     * @param string
     * @return String*/
    public static String getMD5(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(string.getBytes("utf-8"));
        return DigestUtils.md5DigestAsHex(messageDigest.digest());
    }
}
