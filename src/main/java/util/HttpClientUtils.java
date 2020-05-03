package util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;

/**
 * @author super lollipop
 * @date 20-2-13
 */
public class HttpClientUtils {

    private static CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();

    public static String executePost(String url, HttpEntity httpEntity) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(httpEntity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    public static String executeGet(String url) throws IOException{
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    public static String executeDelete(String url) throws IOException{
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(requestConfig);
        HttpResponse httpResponse = httpClient.execute(httpDelete);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    /**下载网络图片的函数
     * @param url 图片的URL字符串
     * @param file 文件的存储位置*/
    public static boolean download(String url, File file) throws IOException {
        if (file.exists()){
            file.delete();
        }
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        System.out.println("下载文件状态返回：" + httpResponse.getStatusLine());
        InputStream inputStream = httpResponse.getEntity().getContent();
        byte[] bytes = readInputStream(inputStream);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
        return true;
    }

    /**读取输入流的函数
     * @return byte[] */
    private static byte[] readInputStream (InputStream inputStream) throws IOException{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];   //创建一个Buffer数组
        int len = 0;   //每次读取的字符串长度，如果为-1，代表全部读取完毕
        while( (len=inputStream.read(buffer)) != -1 ){   //使用一个输入流从buffer里把数据读取出来
            outStream.write(buffer, 0, len);   //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
        }
        inputStream.close();   //关闭输入流
        return outStream.toByteArray();   //把outStream里的数据写入内存
    }
}
