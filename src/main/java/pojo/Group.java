package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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
    private String userID;
}
