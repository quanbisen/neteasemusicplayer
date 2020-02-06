package model;

import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author super lollipop
 * @date 20-2-6
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LocalSinger {
    private Label labSinger;
    private String songCount;
}
