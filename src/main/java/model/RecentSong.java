package model;

import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
