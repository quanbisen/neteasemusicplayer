package pojo;

import lombok.*;

import java.util.Date;
import java.util.List;

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
    private Album albumObject;
    private String albumName;
    private List<Singer> singerList;
    private String totalTime;
    private String size;
    private Date publishTime;
    private Integer albumID;
    private Date collectTime;
    private String resourceURL;
    private String lyricURL;
    private String albumURL;
}
