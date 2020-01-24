package model;


public class LocalSong {
    private String name;
    private String singer;
    private String album;
    private String totalTime;
    private String size;
    private String resource;
    private String lyrics;

    public LocalSong(String name) {
        this.name = name;
    }

    public LocalSong(String name, String singer, String album, String totalTime, String size, String resource, String lyrics) {
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.totalTime = totalTime;
        this.size = size;
        this.resource = resource;
        this.lyrics = lyrics;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    /**把歌名、歌手和专辑转换成字符串内容
     * @return String*/
    public String toStringContent() {
        return name + singer + album;
    }
}
