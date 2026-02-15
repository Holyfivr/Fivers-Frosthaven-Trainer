package se.holyfivr.trainer.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.holyfivr.trainer.service.AuthService;


/** 
 * This class handles decryption of objects.
 * Intentionally leaving this vague to make reverse-engineering slightly more inconvenient.
 */

@Controller
public class AuthController {

    
    private AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/secure-object/items/{filename}")
    public ResponseEntity<byte[]> getItemObject(@PathVariable String filename) {
        try {
            ClassPathResource objFile = new ClassPathResource("static/img/items/" + filename + ".dat");
            
            if (!objFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Decr the object data (read as stream for JAR compatibility)
            byte[] encData = objFile.getInputStream().readAllBytes();
            byte[] objectBytes = authService.decryptObject(encData);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) 
                    .body(objectBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


        @GetMapping("/secure-object/ability-cards/{classname}/{filename}")
    public ResponseEntity<byte[]> getAbilityObject(@PathVariable String classname, @PathVariable String filename) {
        try {

            String formattedClassName = formatClassNames(classname);
            String formattedFileName = formatCardNames(filename);

            ClassPathResource objFile = new ClassPathResource("static/img/abilitycards/" + formattedClassName + "-" + formattedFileName + ".dat");
            
            if (!objFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Decr the object data (read as stream for JAR compatibility)
            byte[] encData = objFile.getInputStream().readAllBytes();
            byte[] objectBytes = authService.decryptObject(encData);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) 
                    .body(objectBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String formatCardNames(String object){
    return object == null ? "" : object.toLowerCase().replaceAll("\\s+", "-");
    }
    private String formatClassNames(String object){
    return object == null ? "" : object.toLowerCase().replaceAll("[\\s-]+", "");
    }

}
