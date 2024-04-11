import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import Logic.DES;
import Logic.EncryptDecryptFIle;
import Logic.Helper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class DESTest {
    @Test
    public void sizeTestBetweenFileAndDecryptedFile() {
        byte[] key = Helper.generateKey();
        String inputFile = "src/main/resources/test.txt";
        String encryptedFile = "src/main/resources/test_encrypted.txt";
        String decryptedFile = "src/main/resources/test_decrypted.txt";

        try {
            EncryptDecryptFIle.encryptFile(inputFile, encryptedFile, key);
            EncryptDecryptFIle.decryptFile(encryptedFile, decryptedFile, key);

            byte[] originalFileContent = Files.readAllBytes(Paths.get(inputFile));
            byte[] decryptedFileContent = Files.readAllBytes(Paths.get(decryptedFile));

            assertArrayEquals(originalFileContent, decryptedFileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void allBytesAreEqualInPdfInputAndDecrypted(){
        byte[] key = Helper.generateKey();
        String inputFile = "src/main/resources/test.pdf";
        String encryptedFile = "src/main/resources/test_encrypted.pdf";
        String decryptedFile = "src/main/resources/test_decrypted.pdf";

        try {
            EncryptDecryptFIle.encryptFile(inputFile, encryptedFile, key);
            EncryptDecryptFIle.decryptFile(encryptedFile, decryptedFile, key);

            byte[] originalFileContent = Files.readAllBytes(Paths.get(inputFile));
            byte[] decryptedFileContent = Files.readAllBytes(Paths.get(decryptedFile));

            assertArrayEquals(originalFileContent, decryptedFileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}