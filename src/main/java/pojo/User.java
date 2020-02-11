package pojo;

import lombok.*;

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
    private String cache;   /*记录缓存的头像文件名*/
}
