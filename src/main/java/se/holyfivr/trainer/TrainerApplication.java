package se.holyfivr.trainer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;


/* =================================================================== */
/* THIS IS A PROJECT MADE BY A FIRST YEAR STUDENT STUDYING TO BECOME   */ 
/* A SOFTWARE DEVELOPER.                            				   */
/* ANYONE IS FREE TO EDIT AND USE THIS CODE AS THEY PLEASE, BUT PLEASE */
/* GIVE CREDIT WHERE CREDIT IS DUE.                                    */ 
/*                                                                     */
/* IF YOU HAVE QUESTIONS ABOUT THE CODE, FEEL FREE TO CONTACT ME.      */
/* MY GITHUB PROFILE: http://www.github.com/Holyfivr/				   */
/* =================================================================== */


@SpringBootApplication
public class TrainerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainerApplication.class, args);

		Application.launch(WebContainer.class, args);
	}

}
