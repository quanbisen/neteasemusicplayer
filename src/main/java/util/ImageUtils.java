package util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v1Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;

/**
 * @author super lollipop
 * @date 19-12-7
 */
public final class ImageUtils {

    /**下载网络图片的函数
     * @param resource 图片的URL字符串
     * @param file 文件的存储位置*/
    /*public static void download(String resource,File file) throws IOException {
        if (file.exists()){
            file.delete();
        }
        HttpURLConnection connection = (HttpURLConnection)new URL(resource).openConnection();  //创建连接
        connection.setRequestMethod("GET");  //设置请求方式为"GET"
        connection.setConnectTimeout(5 * 1000);  //超时响应时间为5秒
        InputStream inputStream = connection.getInputStream();  //通过输入流获取图片数据
        byte[] bytes = readInputStream(inputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
        connection.disconnect();
    }
    */


    @Test
    public void testUtils() throws IOException {
/*
        HttpClientUtils.getInstance().download(
                "https://firmware.meizu.com/Firmware/Flyme/m1928/8.0.0.0/cn/20200107162943/90e4cc65/update.zip", "update.zip",
                progress -> System.out.println("download progress = " + progress));
*/

        HttpClientUtils.download("https://firmware.meizu.com/Firmware/Flyme/m1928/8.0.0.0/cn/20200107162943/90e4cc65/update.zip",new File("update.zip"));
    }

    @org.junit.Test
    public void testUpload() throws IOException {
        /*HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/OnlineExam_war_exploded/Student/HandleUploadImage");
        MultipartEntity entity = new MultipartEntity();
        // entity.addPart(file.getName(), new FileBody(file));
        try {
            entity.addPart("attachfile0", new FileBody(new File("neteasemusicplayer.sql")));
            entity.addPart("counter", new StringBody("1"));
            entity.addPart("MAX_FILE_SIZE", new StringBody("5242880"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        httpPost.setEntity(entity);
        httpPost.setHeader("User-Agent", userAgent);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity
                    .getContent(), "UTF-8"));
            StringBuffer backData = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                backData.append(line);
            }
            System.out.println(backData.toString()   );
        } catch (IOException e) {
            return;
        }
        return;*/
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
                        AbstractID3v2Frame abstractID3v2Frame = (AbstractID3v2Frame) mp3File.getID3v2Tag().getFrame("APIC");    //APIC：Attached picture
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

    public static void setAlbumImage(String imageFile) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        MP3File mp3File = new MP3File((System.getProperty("user.home")) + File.separator + "Music" + File.separator + "Amy Diamond - Heartbeats.mp3");
        System.out.println(mp3File.hasID3v2Tag());
        System.out.println((System.getProperty("user.home")) + File.separator + "Pictures" + File.separator + "林俊杰.png");
        File file = new File((System.getProperty("user.home")) + File.separator + "Pictures" + File.separator + "林俊杰.png");
        if (file.exists()){
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            System.out.println(fileBytes.length);
            AbstractID3v2Frame abstractID3v2Frame = (AbstractID3v2Frame) mp3File.getID3v2Tag().getFrame("APIC");    //APIC：Attached picture
            FrameBodyAPIC frameBodyAPIC = (FrameBodyAPIC) abstractID3v2Frame.getBody();
            frameBodyAPIC.setImageData(fileBytes);
        }
        mp3File.getID3v2Tag().setField(FieldKey.TITLE,"Heartbeats");

        System.out.println(mp3File.getID3v2Tag().getFrame("TIT2"));
        mp3File.save();
    }

    @Test
    public void testSetAlbumImage() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        setAlbumImage("");
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
