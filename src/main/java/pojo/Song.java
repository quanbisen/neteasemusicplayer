package pojo;

import lombok.*;

/**
 * @author super lollipop
 * @date 20-1-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Song {
    private Integer id;
    private String name;
    private String singer;
    private String album;
    private String totalTime;
    private String size;
    private String resourceURL;
    private String lyricURL;
    private String albumURL;
}
