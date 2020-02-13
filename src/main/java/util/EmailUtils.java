package util;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * @author super lollipop
 * @date 20-2-13
 */
public class EmailUtils {

    /**发送邮箱验证码
     * @param emailAddress 收件人邮箱地址
     * @param code 验证码
     * @return boolean*/
    public static boolean sendEmail(String emailAddress,int code) throws DocumentException {
        //读取发件邮箱的配置文件
        Map<String,String> emailConfig = XMLUtils.getEmailConfig(new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "email-config.xml"));
        try {
            SimpleEmail simpleEmail = new SimpleEmail();
            simpleEmail.setHostName(emailConfig.get("hostname")); //发件服务器
            simpleEmail.setFrom(emailConfig.get("username"));   //发件账号
            simpleEmail.setAuthentication(emailConfig.get("username"),emailConfig.get("password"));  //发件邮箱登录验证
            simpleEmail.setCharset("utf-8");    //字符编码
            simpleEmail.addTo(emailAddress);    //发送地址
            simpleEmail.setSubject("音乐播放器注册验证");
            simpleEmail.setMsg("尊敬的用户您好,您本次注册的验证码是:" + code + "." + "一分钟内有效");
            simpleEmail.send();
            return true;
        }catch (EmailException e){
            return false;
        }

    }

    /**随机生成六位数字*/
    public static int generateCode(){
        return (int)((Math.random()*9+1)*100000);
    }


}
