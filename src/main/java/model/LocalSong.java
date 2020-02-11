package model;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LocalSong {
    private String index;
    @NonNull
    private String name;
    private String singer;
    private String album;
    private String totalTime;
    private String size;
    private String resource;
    private String lyrics;

    /**把歌名、歌手和专辑转换成字符串内容
     * @return String*/
    public String toStringContent() {
        return name + singer + album;
    }
}
