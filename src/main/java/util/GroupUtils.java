package util;

import javafx.scene.image.Image;
import pojo.Group;
import pojo.GroupSongDetail;

/**
 * @author super lollipop
 * @date 5/10/20
 */
public class GroupUtils {

    /**根据group对象获取图片
     * @param group 歌单对象
     * @param width 宽度
     * @param height 高度*/
    public static Image getGroupImage(Group group, double width, double height){
        Image image;
        if (group.getImageURL() != null){
            image = new Image(group.getImageURL(),width,height,true,true);
            if (image.isError()) {
                image = new Image("image/DefaultAlbumImage_200.png", width, height, true, true);
            }
        }else {
            if (group.getGroupSongDetailList() != null && group.getGroupSongDetailList().size() > 0){
                GroupSongDetail groupSongDetail = group.getGroupSongDetailList().get(0);
                image = new Image(groupSongDetail.getAlbumURL(),width,height,true,true);
            }else {
                image = new Image("image/DefaultAlbumImage_200.png", width, height, true, true);
            }
        }
        return image;
    }
}
