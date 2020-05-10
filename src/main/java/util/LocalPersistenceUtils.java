package util;

import mediaplayer.Config;
import pojo.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author super lollipop
 * @date 5/4/20
 */
public class LocalPersistenceUtils {

    /**保存用户对象状态的函数
     * @param config 播放器文件存储的配置对象
     * @param user 用户对象*/
    public static void saveUserStatus(Config config, User user) throws IOException {
        Path imagePath = config.getCachePath().resolve("image");
        if (!imagePath.toFile().exists()){
            Files.createDirectory(imagePath);
        }
        File imageFile = new File(imagePath.toString() + File.separator + TimeUtils.formatDate(user.getLoginTime(),"yyyyMMddHHmmss") + user.getImageURL().substring(user.getImageURL().lastIndexOf("."))); //用用户的用户名作为图片命名
        HttpClientUtils.download(user.getImageURL(),imageFile);  //下载用户的头像文件，保存供下次打开播放器使用
        user.setLocalImagePath("file:"+imageFile.getPath());
        //保存登录信息到本地文件
        File loginConfigFile = config.getUserStatusFile();
        if (loginConfigFile.exists()){
            loginConfigFile.delete();
        }
        JSONObjectUtils.saveObject(user,loginConfigFile);  //调用存储的函数，将用户对象写入到文件
    }
}
