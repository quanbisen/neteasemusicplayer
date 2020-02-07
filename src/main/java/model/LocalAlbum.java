package model;

import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author super lollipop
 * @date 20-2-7
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LocalAlbum {
    private Label labAlbumInformation;
    private String singer;
    private String songCount;
}
