package model;

import javafx.scene.control.Label;
import lombok.*;

/**
 * @author super lollipop
 * @date 20-1-24
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayListSong {
    private String name;
    private String singer;
    private String album;
    private String totalTime;
    private Label labRemoveIcon;
    private String resource;
    private String lyricURL;
    private String imageURL;

    public PlayListSong(String name, String singer, String album, String totalTime, String resource, String lyricURL, String imageURL) {
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.totalTime = totalTime;
        this.resource = resource;
        this.lyricURL = lyricURL;
        this.imageURL = imageURL;
    }

    public PlayListSong(String name, String singer, String album, String totalTime, String resource) {
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.totalTime = totalTime;
        this.resource = resource;
    }
}
