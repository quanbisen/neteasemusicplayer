package model;

/**
 * @author super lollipop
 * @date 20-1-24
 */
public class OnlineSong {
    private String name;
    private String singer;
    private String album;
    private String totalTime;
    private String resource;
    private String lyrics;
    private String albumURL;

    public OnlineSong(String name, String singer, String album, String totalTime, String resource) {
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.totalTime = totalTime;
        this.resource = resource;
    }

    public OnlineSong(String name, String singer, String album, String totalTime, String resource, String lyrics, String albumURL) {
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.totalTime = totalTime;
        this.resource = resource;
        this.lyrics = lyrics;
        this.albumURL = albumURL;
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

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getAlbumURL() {
        return albumURL;
    }

    public void setAlbumURL(String albumURL) {
        this.albumURL = albumURL;
    }
}
