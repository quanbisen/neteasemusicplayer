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
@NoArgsConstructor
public class Group {
    @NonNull
    private Integer id;
    @NonNull
    private String name;
    private String description;
    private Date createTime;
    private String imageURL;
    @NonNull
    private String userID;
}
