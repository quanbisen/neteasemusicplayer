package model;

import javafx.scene.image.ImageView;

public class RecentPlaySong {
    private String index;
    private ImageView ivFavor;
    private String name;
    private String singer;
    private String album;
    private String totalTime;

    public RecentPlaySong() {
    }

    public RecentPlaySong(String index, ImageView ivFavor, String name, String singer, String album, String totalTime) {
        this.index = index;
        this.ivFavor = ivFavor;
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.totalTime = totalTime;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public ImageView getIvFavor() {
        return ivFavor;
    }

    public void setIvFavor(ImageView ivFavor) {
        this.ivFavor = ivFavor;
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
}
