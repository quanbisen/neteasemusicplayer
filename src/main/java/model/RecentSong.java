package model;

import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RecentSong {
    private String index;
    private Label labAddFavor;
    private String name;
    private String singer;
    private String album;
    private String totalTime;
    private String resource;
}
