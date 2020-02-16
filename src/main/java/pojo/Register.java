package pojo;

import lombok.*;

import java.util.Date;

/**
 * @author super lollipop
 * @date 20-2-15
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Register {
    @NonNull
    private String id;
    @NonNull
    private String password;
    private Date createTime;
    @NonNull
    private String code;
}
