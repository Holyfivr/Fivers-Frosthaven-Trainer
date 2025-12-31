package se.holyfivr.trainer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.holyfivr.trainer.service.AuthService;

import java.nio.file.Paths;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // Serves obj to frontend
    @GetMapping("/secure-object/{filename}")
    public ResponseEntity<byte[]> getObject(@PathVariable String filename) {
        try {
            ClassPathResource objFile = new ClassPathResource("static/img/" + filename + ".dat");
            
            if (!objFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Decr the object data
            byte[] objectBytes = authService.decryptObject(Paths.get(objFile.getURI()));

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) 
                    .body(objectBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
