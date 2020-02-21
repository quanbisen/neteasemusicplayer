package pojo;

import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class User {
    @NonNull
    private String id;
    @NonNull
    private String password;
    @NonNull
    private String name;
    @NonNull
    private String token;
    private String imageURL;
    private Date loginTime;   /*登录时间*/
    private List<Group> groupList;
}
