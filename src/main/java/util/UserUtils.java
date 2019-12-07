package util;

import com.alibaba.fastjson.JSON;
import model.User;
import java.io.*;

/**
 * @author super lollipop
 * @date 19-12-7
 */
public final class UserUtils {

    /**根据用户登录文件解析出用户对象的函数
     * @param LOGIN_CONFIG_FILE 登录的存储文件*/
    public static User parseUser(File LOGIN_CONFIG_FILE) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(LOGIN_CONFIG_FILE);
        int n = 0;
        StringBuffer stringBuffer = new StringBuffer();
        while (n!=-1){
            n=fileInputStream.read();//读取文件的一个字节(8个二进制位),并将其由二进制转成十进制的整数返回
            char by=(char) n; //转成字符
            stringBuffer.append(by);
        }
        String str = stringBuffer.substring(0,stringBuffer.length()-1);
        return JSON.parseObject(str,User.class);
    }

    /**保存用户对象为Json文件的函数
     * @param user 用户对象
     * @param LOGIN_CONFIG_FILE 登录配置存储文件*/
    public static void saveUser(User user,File LOGIN_CONFIG_FILE) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(LOGIN_CONFIG_FILE);  //获取输出流
        fileOutputStream.write(JSON.toJSONString(user).getBytes());  //写入字节
        fileOutputStream.close(); //关闭
    }
}
