package util;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.util.Duration;

public final class AnimationUtils {
    public static void addAnimation(Parent parent){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5),parent);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }
}
