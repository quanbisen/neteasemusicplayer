package model;

import javafx.scene.control.Label;
import lombok.*;

/**
 * @author super lollipop
 * @date 20-1-24
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayListSong {
    @NonNull
    private String name;
    @NonNull
    private String singer;
    @NonNull
    private String album;
    @NonNull
    private String totalTime;
    private Label labRemoveIcon;
    @NonNull
    private String resource;
    private String lyrics;

}
