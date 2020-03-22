package util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.PlayListSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.junit.Test;
import java.io.*;
import java.nio.file.Files;

/**
 * @author super lollipop
 * @date 19-12-7
 */
public final class ImageUtils {

    /**根据歌曲资源的路径获取资源的专辑图片
     * @param playListSong 播放歌曲
     * @return imgAlbum */
    public static ImageView getAlbumImageView(PlayListSong playListSong, double width, double height) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        ImageView imgAlbum;
        if (!playListSong.getResource().contains("http:")){
            try {
                MP3File mp3File = new MP3File(playListSong.getResource());
                if (mp3File.hasID3v2Tag()){
                    imgAlbum = createImageView(getAlbumImage(playListSong,width,height),width,height);
                }
                else {
                    imgAlbum = createImageView("image/DefaultAlbumImage_200.png",width,height);
                }
            }catch (FileNotFoundException e){
                return createImageView("image/DefaultAlbumImage_200.png",width,height);
            }
        }else { //在线歌曲资源的专辑图，先设置为默认的黑白专辑图，后面再添加。。。
            imgAlbum = createImageView("image/DefaultAlbumImage_200.png",width,height);
        }
        return imgAlbum;
    }

    /**获取资源路径的音乐文件的专辑图片*/
    public static Image getAlbumImage(PlayListSong playListSong,double width,double height) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        if (!playListSong.getResource().contains("http://")){   //没有包含"http字样",那就是本地歌曲了.
            Image imageData = getAlbumImage(playListSong.getResource(),width, height);
            if (imageData != null) return imageData;
        }else if (playListSong.getImageURL() != null){
            Image image = new Image(playListSong.getImageURL(),width,height,true,true);
            if (!image.isError()){
                return image;
            }
        }
        return new Image("image/DefaultAlbumImage_200.png",width,height,true,true);
    }

    public static Image getAlbumImage(String resource,double width, double height) throws IOException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException {
        MP3File mp3File = new MP3File(resource);
        if (mp3File.hasID3v2Tag()){
            try {
                AbstractID3v2Frame abstractID3v2Frame = (AbstractID3v2Frame) mp3File.getID3v2Tag().getFrame("APIC");    //APIC：Attached picture
                FrameBodyAPIC frameBodyAPIC = (FrameBodyAPIC) abstractID3v2Frame.getBody();
                byte[] imageData = frameBodyAPIC.getImageData();
                return new Image(new ByteArrayInputStream(imageData),width,height,true,true);
            }catch (NullPointerException e){
                return new Image("image/DefaultAlbumImage_200.png",width,height,true,true);
            }
        }
        return null;
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
