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
@NoArgsConstructor
public class User {
    private String id;
    private String password;
    private String name;
    private String token;
    private Date loginTime;
    private String description;
    private String sex;
    private Date birthday;
    private String imageURL;
}
