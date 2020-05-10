package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author super lollipop
 * @date 5/10/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSongDetail {
    private Integer id;
    private String name;
    private String singer;
    private String album;
    private String totalTime;
    private String size;
    private String resourceURL;
    private String lyricURL;
    private String albumURL;
    private Date addTime;
}
