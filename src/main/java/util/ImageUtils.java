package util;

import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author super lollipop
 * @date 19-12-7
 */
public final class ImageUtils {

    /**下载网络图片的函数
     * @param resource 图片的URL字符串
     * @param file 文件的存储位置*/
    public static void download(String resource,File file) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(resource).openConnection();  //创建连接
        connection.setRequestMethod("GET");  //设置请求方式为"GET"
        connection.setConnectTimeout(5 * 1000);  //超时响应时间为5秒
        InputStream inputStream = connection.getInputStream();  //通过输入流获取图片数据
        byte[] bytes = readInputStream(inputStream);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    /**读取输入流的函数
     * @return byte[] */
    private static byte[] readInputStream (InputStream inputStream) throws IOException{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];   //创建一个Buffer字符串
        int len = 0;   //每次读取的字符串长度，如果为-1，代表全部读取完毕
        while( (len=inputStream.read(buffer)) != -1 ){   //使用一个输入流从buffer里把数据读取出来
            outStream.write(buffer, 0, len);   //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
        }
        inputStream.close();   //关闭输入流
        return outStream.toByteArray();   //把outStream里的数据写入内存
    }

}
