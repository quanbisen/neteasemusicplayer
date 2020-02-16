package controller.authentication;

import application.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 20-2-16
 */
@Controller
public class RegisterSuccessController {

    /**"转到登录界面"按钮*/
    @FXML
    private Button btnJumpToLogin;

    @Resource
    private ApplicationContext applicationContext;

    /**"转到登录界面"按钮的事件*/
    @FXML
    public void onClickedJumpToLogin(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            Scene scene = btnJumpToLogin.getScene();
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/authentication/login.fxml");
            scene.setRoot(fxmlLoader.load());
        }
    }

}
