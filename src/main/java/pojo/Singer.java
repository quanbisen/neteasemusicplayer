package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * @author super lollipop
 * @date 20-2-8
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Singer {
    private int id;
    private String name;
    private Date birthday;
    private float height;
    private float weight;
    private String constellation;
    private String description;
    private String imageURL;
}

