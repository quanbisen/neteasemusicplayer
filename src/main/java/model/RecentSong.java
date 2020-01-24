package model;

import javafx.scene.control.Label;

public class RecentSong {
    private String index;
    private Label labAddFavor;
    private String name;
    private String singer;
    private String album;
    private String totalTime;
    private String resource;

    public RecentSong(String index, Label labAddFavor, String name, String singer, String album, String totalTime, String resource) {
        this.index = index;
        this.labAddFavor = labAddFavor;
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.totalTime = totalTime;
        this.resource = resource;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Label getLabAddFavor() {
        return labAddFavor;
    }

    public void setLabAddFavor(Label labAddFavor) {
        this.labAddFavor = labAddFavor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
