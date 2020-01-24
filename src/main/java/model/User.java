package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private String id;
    private String password;
    private String name;
    private String imageURL;

}
