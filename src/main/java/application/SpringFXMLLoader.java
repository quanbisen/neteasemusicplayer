package application;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.IOException;

@Component
public final class SpringFXMLLoader{

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 获取一个ControllerFactory被SpringBeanFactory管理的FXMLLoader对象
     * */
    public FXMLLoader getLoader(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(resource));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        return fxmlLoader;
    }

}
