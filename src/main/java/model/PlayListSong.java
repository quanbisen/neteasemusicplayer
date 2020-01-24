package model;

import javafx.scene.image.ImageView;
import lombok.*;

/**
 * @author super lollipop
 * @date 20-1-24
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayListSong {
    private ImageView ivPlayFlag;
    @NonNull
    private String name;
    @NonNull
    private String singer;
    @NonNull
    private String totalTime;
    @NonNull
    private String resource;
    private String lyrics;

}
