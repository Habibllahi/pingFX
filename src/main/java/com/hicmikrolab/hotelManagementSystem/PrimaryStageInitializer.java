/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URL;

/**
 * <p>
 *     This class is self called. Its a listener to respond to a published StageReadyEvent.
 *     <code>StageReadyEvent</code> is a custom ApplicationEvent for our JavaFX application and subclass of
 *     <code>org.springframework.context.ApplicationEvent</code>
 *
 *     The sole action of this Listener class is create the Primary stage for the application and load the Scene graph described by the
 *     <code>MainView.fxml</code>
 * </p>
 */
@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    @Value("${spring.application.ui.title}")
    private String applicationTitle; //Spring injects the value of spring.application.ui.title from the application.properties file
    @Value("classpath:/MainView.fxml")
    private Resource fxml; //Spring injects the path to MainView.fxml as a resource path

    @Autowired
    ApplicationContext applicationContext; //Spring Injects its application context as a dependency for this StageReadyEvent Application Listener class


    /**
     * This method is called as response to a StageReadyEvent being handled by this Listener.
     * Am using this method to create the Primary Stage of this Application.
     * @param event
     */
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try{
            Stage stage = event.getStage();

            URL url = this.fxml.getURL();
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root,1000,600);
            stage.setTitle(this.applicationTitle);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
