import java.io.IOException;
import java.util.Arrays;
import java.security.SecureRandom;

public class Main {
    public static void main(String[] args) throws IOException {

        SecureRandom randomKey = new SecureRandom();

        byte[] key = new byte[8];
        randomKey.nextBytes(key);

        System.out.println("Klucz: " + Arrays.toString(key));

        EncryptDecryptFIle.encryptFile("src/input.txt", "src/input_encrypted.txt", key);
        EncryptDecryptFIle.decryptFile("src/input_encrypted.txt", "src/input_decrypted.txt", key);
    }
}