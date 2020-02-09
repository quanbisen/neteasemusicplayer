package util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

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

    /**根据歌曲资源的路径获取资源的专辑图片
     * @param resource 资源的路径
     * @return imgAlbum */
    public static ImageView getAlbumImage(String resource) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        ImageView imgAlbum;
        if (!resource.contains("http:")){
            try {
                MP3File mp3File = new MP3File(resource);
                if (mp3File.hasID3v2Tag()){
                    try {
                        AbstractID3v2Frame abstractID3v2Frame = (AbstractID3v2Frame) mp3File.getID3v2Tag().getFrame("APIC");
                        FrameBodyAPIC frameBodyAPIC = (FrameBodyAPIC) abstractID3v2Frame.getBody();
                        byte[] imageData = frameBodyAPIC.getImageData();
                        Image image = new Image(new ByteArrayInputStream(imageData),58,58,false,true);
                        imgAlbum= createImageView(image,58,58);
                    }catch (NullPointerException e){
                        imgAlbum = createImageView("image/DefaultAlbum_58.png",58,58);
                    }

                }
                else {
                    imgAlbum = createImageView("image/DefaultAlbum_58.png",58,58);
                }
            }catch (FileNotFoundException e){
                return createImageView("image/DefaultAlbum_58.png",58,58);
            }
        }else { //在线歌曲资源的专辑图，先设置为默认的黑白专辑图，后面再添加。。。
            imgAlbum = createImageView("image/DefaultAlbum_58.png",58,58);
        }

        return imgAlbum;
    }

    /**获取一个ImageView的对象
     * @param resource 资源的路径
     * @param  fitWidth 图片的宽度
     * @param  fitHeight 图片的高度
     * @return ImageView*/
    public static ImageView createImageView(String resource,double fitWidth,double fitHeight){
        Image image = new Image(resource,fitWidth,fitHeight,false,true);
        return createImageView(image,fitWidth,fitHeight);
    }

    /**获取一个ImageView的对象
     * @param image 图片对象
     * @param  fitWidth 图片的宽度
     * @param  fitHeight 图片的高度
     * @return ImageView*/
    public static ImageView createImageView(Image image,double fitWidth,double fitHeight){
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(fitHeight);
        imageView.setFitWidth(fitWidth);
        return imageView;
    }
}
