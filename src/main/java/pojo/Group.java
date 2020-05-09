package pojo;

import lombok.*;
import java.util.Date;

/**
 * @author super lollipop
 * @date 20-2-18
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Group {
    private Integer id;
    private String name;
    private String description;
    private Date createTime;
    private String imageURL;
    private String localImagePath;  //歌单本地存储路径
    private String userID;
    private int favor;
}
