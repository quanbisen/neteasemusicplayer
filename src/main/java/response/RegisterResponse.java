package response;

import lombok.Data;

/**
 * @author super lollipop
 * @date 20-2-25
 */
@Data
public class RegisterResponse {
    private String id;
    private String password;
    private String code;
    private int expireSecond;
    private String message;
}
