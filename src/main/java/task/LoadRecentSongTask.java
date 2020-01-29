package task;

import controller.RecentPlayContentController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import mediaplayer.MyMediaPlayer;
import model.RecentSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.ImageUtils;
import util.XMLUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-1-29
 */
@Component
@Scope("prototype")
public class LoadRecentSongTask extends Task<ObservableList<RecentSong>> {

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private RecentPlayContentController recentPlayContentController;

    @Override
    protected ObservableList<RecentSong> call() throws Exception {
        List<RecentSong> recentSongs = XMLUtils.getRecentPlaySongs(myMediaPlayer.getRecentPlayStorageFile(),"PlayedSong");
        Collections.reverse(recentSongs);   //倒序集合
        ObservableList<RecentSong> observableList = FXCollections.observableArrayList();
        for (int i = 0; i < recentSongs.size(); i++) {
            StringBuffer stringBuffer = new StringBuffer();
            if (i+1<10){
                stringBuffer.append("0");
            }
            String index = stringBuffer.append(i+1).toString();

            ImageView imageView = ImageUtils.createImageView("/image/FavorTabIcon.png",20,20);
            Label labAddFavor = new Label("",imageView);
            labAddFavor.setOnMouseClicked(event -> {
                System.out.println("clicked ...");
            });
            observableList.add(new RecentSong(index,labAddFavor,recentSongs.get(i).getName(),recentSongs.get(i).getSinger(),recentSongs.get(i).getAlbum(),recentSongs.get(i).getTotalTime(),recentSongs.get(i).getResource()));
        }

        Platform.runLater(()->{
            recentPlayContentController.getLabSongCount().setText(String.valueOf(observableList.size()));
        });

        return observableList;
    }
}
