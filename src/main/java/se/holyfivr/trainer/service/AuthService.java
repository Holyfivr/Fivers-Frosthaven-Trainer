package se.holyfivr.trainer.service;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;

@Service
public class AuthService {

    // Don't touch
    private static final String OBJ_K = "x9F2m5K8p1L4n7Q3"; 
    private static final String ALGORITHM = "AES";

    // reverse object transformation for use in the frontend
    public byte[] decryptObject(Path path) throws Exception {
        byte[] encData = Files.readAllBytes(path);
        Key k = new SecretKeySpec(OBJ_K.getBytes(), ALGORITHM);
        Cipher ciph = Cipher.getInstance(ALGORITHM);
        ciph.init(Cipher.DECRYPT_MODE, k);
        return ciph.doFinal(encData);
    }

    // forward object transformation for saving objects
    public byte[] encryptObject(byte[] objectData) throws Exception {
        Key k = new SecretKeySpec(OBJ_K.getBytes(), ALGORITHM);
        Cipher ciph = Cipher.getInstance(ALGORITHM);
        ciph.init(Cipher.ENCRYPT_MODE, k);
        return ciph.doFinal(objectData);
    }
}
