/**
 * @Author Hamzat Habibllahi
 */
package com.hicmikrolab.hotelManagementSystem;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * This is the Main class and entry point in to the desktop application via Spring.
 * The application is built on JavaFX 11.0.2, Java 11.0.10 LTS technologies.
 * Spring boot 2.4.4 and Spring 5.3.5 are used, making this application a Spring enhanced JavaFX application.
 *
 * Since JavaFX as its own application context different from spring, This main class focus on bootstrapping the JavaFX application
 * by calling the launch method on <code>javafx.application.Application</code>
 * </p>
 */
@SpringBootApplication
public class PingJavaFXApp {

	/**
	 * <p>
	 * Launches a standalone application. The <code>Application.launch</code> is typically called from the main method().
	 * It must not be called more than once or an exception will be thrown.
	 * The launch method does not return until the application has exited, either via a call to Platform.exit or
	 * all of the application windows have been closed.
	 * The class specified by the appClass argument must be a public subclass of Application with a public no-argument constructor,
	 * in a package that is exported (or open) to at least the javafx.graphics module, or a RuntimeException will be thrown.
	 * </p>
	 */
	public static void main(String[] args) {

		Application.launch(JavaFxApplication.class, args);
	}

}


