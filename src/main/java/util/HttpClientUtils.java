package util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * @author super lollipop
 * @date 20-2-13
 */
public class HttpClientUtils {

    /**下载网络图片的函数
     * @param url 图片的URL字符串
     * @param file 文件的存储位置*/
    public static boolean download(String url, File file) throws IOException {
        if (file.exists()){
            file.delete();
        }
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        System.out.println(httpResponse.getStatusLine());
        InputStream inputStream = httpResponse.getEntity().getContent();
        byte[] bytes = readInputStream(inputStream);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
        return true;
    }

    /**上传文件的函数
     * @param url 服务器发送地址*/
    public static boolean upload(String url){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);  //"http://127.0.0.1:8080/OnlineExam_war_exploded/Student/HandleUploadImage"
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().
                addBinaryBody("file",new File("pom.xml")).
                addTextBody("MAX_FILE_SIZE","5242880");
        httpPost.setEntity(multipartEntityBuilder.build());
        httpPost.setHeader("User-Agent", userAgent);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            httpClient.close();
            HttpEntity httpEntity = response.getEntity();
            BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity
                    .getContent(), "UTF-8"));
            StringBuffer backData = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                backData.append(line);
            }
            System.out.println(backData.toString()   );
        } catch (IOException e) {
            return false;
        }
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
