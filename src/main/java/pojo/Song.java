package pojo;

import lombok.*;

/**
 * @author super lollipop
 * @date 20-1-24
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Song {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String singer;
    @NonNull
    private String album;
    @NonNull
    private String totalTime;
    private String size;
    @NonNull
    private String resourceURL;
    private String lyricURL;
    private String albumURL;
}
