package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public final class SpringUtil {

    /**
     * 获取一个ControllerFactory被SpringBeanFactory管理的FXMLLoader对象
     * */
    public static FXMLLoader getFXMLLoaderWithSpring() throws IOException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory((Callback<Class<?>,Object>)applicationContext.getParentBeanFactory());
        return fxmlLoader;
    }

}
