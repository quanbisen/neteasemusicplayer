package model;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import util.ImageUtils;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RecentSong {
    private String index;
    private Label labAddFavor;
    @NonNull
    private String name;
    @NonNull
    private String singer;
    @NonNull
    private String album;
    @NonNull
    private String totalTime;
    @NonNull
    private String resource;

}
