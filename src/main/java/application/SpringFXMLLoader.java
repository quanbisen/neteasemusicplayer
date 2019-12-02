package application;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class SpringFXMLLoader{

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取一个ControllerFactory被SpringBeanFactory管理的FXMLLoader对象
     * */
    public FXMLLoader getLoader(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(resource));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        return fxmlLoader;
    }

}
