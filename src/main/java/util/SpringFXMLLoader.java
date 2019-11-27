package util;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.IOException;

public final class SpringFXMLLoader{

    private static String APPLICATION_CONTEXT_PATH = "/config/application-context.xml";
    private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_PATH);;

    /**
     * 获取一个ControllerFactory被SpringBeanFactory管理的FXMLLoader对象
     * */
    public static FXMLLoader getLoader() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        return fxmlLoader;
    }

}
