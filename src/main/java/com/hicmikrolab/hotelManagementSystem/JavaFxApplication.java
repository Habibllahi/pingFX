/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * com.hicmikrolab.hotelManagementSystem.JavaFxApplication is the entry point for
 * Hotel Management System as a JavaFX application. This class is bootstrap by
 * com.hicmikrolab.hotelManagementSystem.PingJavaFXApp.
 */
public class JavaFxApplication extends Application {

    //by this I want the JavaFX application to encapsulate the Spring Application Context
    //Hence have access to Spring defined beans
    private ConfigurableApplicationContext context;

    /**
     * <p>The init method is called on the launcher thread, not on the JavaFX Application Thread.
     * This means that an application must not construct a Scene or a Stage in the init method.
     * An application may construct other JavaFX objects in the init method.</p>
     *
     * Author uses this Init method to
     * <ol>
     *     <li>register some beans using <code>org.springframework.context.ConfigurableApplicationContext.registerBean()
     *     </code>
     *     These beans registration is done during implementation of <code>@Functional Interface
     *     </code> <code>org.springframework.context.ApplicationContextInitializer</code> to create an initializer object.
     *     The registered beans are:
     *          <ul>
     *              <li><code>javafx.application.Application</code> Bean,</li>
     *               <li><code>javafx.application.Application.Parameter</code> Bean</li>
     *              <li><code>javafx.application.HostServices</code> Bean</li>
     *         </ul>
     *      </li>
     *
     *     <li>The registered beans are created by implementation of Functional Interface Supplier
     *         as the second parameter of the registerBean(...) method
     *     <li>
     *
     *     <li>
     *         Further Author uses the init() to instantiate  org.springframework.boot.builder.SpringApplicationBuilder
     *         object and pass it to reference variable "context" of type
     *         <code>org.springframework.context.ConfigurableApplicationContext</code>
     *    </li>
     *  </ol>
     *
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        ApplicationContextInitializer<GenericApplicationContext> initializer = (applicationContext)->{
            applicationContext.registerBean(Application.class,() -> JavaFxApplication.this); //registers the JavaFx Application as a Spring bean itself
            applicationContext.registerBean(Application.Parameters.class,() -> getParameters());//registers the JavaFX Application environment parameters as Spring bean
            applicationContext.registerBean(HostServices.class,() ->getHostServices());//register the JavaFX host services as spring bean
        };

        this.context = new SpringApplicationBuilder()
                .sources(PingJavaFXApp.class)// Add a source of the Application context
                .initializers(initializer) //Initialized all registered beans
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    /**
     * <p>
     *  JavaFX creates an application thread (namely JavaFX application thread) for running the application start method, processing input events,
     *  and running animation timelines.
     *  Creation of JavaFX Scene and Stage objects as well as modification of scene graph operations to live objects
     *  (those objects already attached to a scene) must be done on the JavaFX application thread.
     * </p>
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Will use the JavaFX'S Spring Application context to publish an Event
        //org.springframework.context.ApplicationListener.onApplicationEvent(..) method of A matching Listening candidate bean
        // in the Spring Application context will be executed.
        //Since the Spring Application context is now available to JavaFX Application, The Application listener will executes its
        //org.springframework.context.ApplicationListener.onApplicationEvent(..) on the same Thread in which the JavaFX start() method is executed
        this.context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() throws Exception { //(3)
        this.context.close();
        Platform.exit();
    }
}

class StageReadyEvent extends ApplicationEvent{

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public StageReadyEvent(Object source) {
        super(source);
    }

    public Stage getStage(){
        return Stage.class.cast(getSource());
    }


}