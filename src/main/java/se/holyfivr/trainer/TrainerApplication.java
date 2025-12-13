package se.holyfivr.trainer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;


/* =================================================================== */
/* THIS IS A PROJECT MADE BY A FIRST YEAR STUDENT STUDYING TO BECOME   */ 
/* A SOFTWARE DEVELOPER.                            				   */
/* ANYONE IS FREE TO EDIT AND USE THIS CODE AS THEY PLEASE, BUT PLEASE */
/* GIVE CREDIT WHERE CREDIT IS DUE.                                    */ 
/*                                                                     */
/* IF YOU HAVE QUESTIONS ABOUT THE CODE, OR JUST WANT TO GET IN TOUCH  */
/* FEEL FREE TO CONTACT ME. 										   */
/*                                                                     */
/* 		MY GITHUB PROFILE: http://www.github.com/Holyfivr/			   */
/* =================================================================== */


@SpringBootApplication
public class TrainerApplication {

	public static void main(String[] args) {
		// Starts Spring Boot application, retrieves the local server port, and pass it to the JavaFX WebContainer
		ConfigurableApplicationContext context = SpringApplication.run(TrainerApplication.class, args);
		WebContainer.port = Integer.parseInt(context.getEnvironment().getProperty("local.server.port"));
		Application.launch(WebContainer.class, args);
	}

}
