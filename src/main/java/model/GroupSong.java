package model;

import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author super lollipop
 * @date 20-3-9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSong {
    private String index;
    private Label labFavor;
    private String name;
    private Label labLocalFlag;
    private String singer;
    private String album;
    private String totalTime;
    private String resourceURL;
}
